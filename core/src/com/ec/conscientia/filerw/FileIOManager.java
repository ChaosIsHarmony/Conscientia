package com.ec.conscientia.filerw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.entities.Acquirable;
import com.ec.conscientia.entities.Book;
import com.ec.conscientia.entities.Glyph;
import com.ec.conscientia.entities.Location;
import com.ec.conscientia.entities.Log;
import com.ec.conscientia.entities.MindscapeNPC;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.entities.SavedGame;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class FileIOManager {
	private String currentSavedGameFile, NPCFile, uniSaveFile;
	public final static int SAVE_FILE = 0, NPC_FILE = 1, UNI_FILE = 3, NUM_BIRACULIAN_VERSES = 20;

	public final static String SAVE_FILE_STR = "SG/genericSG.mao";
	private int SBStartInd, SBEndInd;

	private Conscientia conscientia;
	private MainGameScreen mgScr;

	/*
	 * For test suite
	 */
	public FileIOManager() {
	}

	/*
	 * For actual game
	 */
	public FileIOManager(Conscientia conscientia) {
		this.conscientia = conscientia;
	}

	public FileIOManager(Conscientia conscientia, MainGameScreen mainGameScr) {
		this.conscientia = conscientia;
		this.mgScr = mainGameScr;
	}

	public void loadFile(int fileType, boolean updatePersistents) {
		FileHandle file;
		String lessThanTen;
		try {
			switch (fileType) {
			case SAVE_FILE:
				// OPENS TARGET SAVE FILE
				file = Gdx.files.local("SG/genericSG.mao");

				// adds 0 in front of current game num if less than 10
				lessThanTen = (conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";
				// assigns indexes for current saved game portion
				SBStartInd = file.readString()
						.indexOf("%" + lessThanTen + conscientia.getConscVar().currentSavedGameNum);
				SBEndInd = file.readString().indexOf(lessThanTen + conscientia.getConscVar().currentSavedGameNum + "%")
						+ (lessThanTen.length() + 1);

				currentSavedGameFile = file.readString().substring(SBStartInd, SBEndInd);

				if (updatePersistents) {
					// checks to see if more triggered events have been added in
					// the defaultSave file upon loading a saved game
					writeNewEvents();
					updatePersistents();
				}
				break;
			case NPC_FILE:
				file = Gdx.files.local("SG/NPCs.mao");

				// adds 0 in front of current game num if less than 10
				lessThanTen = (conscientia.getConscVar().currentSavedGameNum < 10)
						? ("0" + conscientia.getConscVar().currentSavedGameNum)
						: ("" + conscientia.getConscVar().currentSavedGameNum);

				// indexes for current saved game portion
				SBStartInd = file.readString().indexOf("%" + lessThanTen);
				SBEndInd = file.readString().indexOf(lessThanTen + "%") + (lessThanTen.length() + 1);

				NPCFile = file.readString().substring(SBStartInd, SBEndInd);

				// add anomaly if not present
				String[] addedPostHoc = { "THE ANOMALY", "ARKARA" };
				for (String name : addedPostHoc) {
					if (!NPCFile.contains("[/" + name + "]")) {
						addNPC(name);
						// Needs to write to file here, or else it fucks up the
						// save file
						SBStartInd = file.readString().indexOf("%" + lessThanTen);
						SBEndInd = file.readString().indexOf(lessThanTen + "%") + (lessThanTen.length() + 1);
						// writes to file
						file.writeString(file.readString().substring(0, SBStartInd) + NPCFile
								+ file.readString().substring(SBEndInd), false);
					}
				}
				break;
			case UNI_FILE:
				file = Gdx.files.local("SG/UniSave.mao");

				uniSaveFile = file.readString();
				break;
			}
		} catch (Exception e) {
			mgScr.loadingUtils.nullError(
					"LOADING_FILE: " + conscientia.getConscVar().currentSavedGameNum + " | FILE TYPE: " + fileType);
		}
	}

	private void updatePersistents() {
		// ACQUIRABLES
		// current save file
		String tempItemList = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{ACQUIRABLE}"),
				currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		ArrayList<Integer> acqItemsCurrentSaveFile = new ArrayList<Integer>();
		while (tempItemList.contains("|")) {
			acqItemsCurrentSaveFile.add(
					Integer.parseInt(tempItemList.substring(tempItemList.indexOf("|") + 1, tempItemList.indexOf(","))));
			// trims list to next glyph
			tempItemList = tempItemList.substring(tempItemList.indexOf(",") + 1);
		}
		// universal save list
		loadFile(UNI_FILE, false);
		tempItemList = uniSaveFile.substring(uniSaveFile.indexOf("[/ACQ_UNI]"), uniSaveFile.indexOf("[ACQ_UNI/]"));
		ArrayList<Integer> acqItemsUniSaveFile = new ArrayList<Integer>();
		while (tempItemList.contains("|")) {
			acqItemsUniSaveFile.add(
					Integer.parseInt(tempItemList.substring(tempItemList.indexOf("|") + 1, tempItemList.indexOf(","))));
			// trims list to next glyph
			tempItemList = tempItemList.substring(tempItemList.indexOf(",") + 1);
		}
		// check for matching
		for (int i : CommonVar.persistentAcquirables) {
			if (acqItemsCurrentSaveFile.contains(i) && !acqItemsUniSaveFile.contains(i))
				acqItemsUniSaveFile.add(i);
			else if (!acqItemsCurrentSaveFile.contains(i) && acqItemsUniSaveFile.contains(i))
				acqItemsCurrentSaveFile.add(i);
		}

		// rewrite files
		tempItemList = "";
		for (int i : acqItemsCurrentSaveFile)
			tempItemList += "|" + i + ",";

		currentSavedGameFile = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{ACQUIRABLE}") + 12)
				+ tempItemList + currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));

		tempItemList = "";
		for (int i : acqItemsUniSaveFile)
			tempItemList += "|" + i + ",";
		uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/ACQ_UNI]") + 10) + tempItemList
				+ uniSaveFile.substring(uniSaveFile.indexOf("[ACQ_UNI/]"));

		// EVENTS
		// current saved game
		String tempEventList = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));
		HashMap<Integer, Boolean> eventsCurrentSaveFile = new HashMap<Integer, Boolean>();
		while (tempEventList.contains("|")) {
			eventsCurrentSaveFile.put(
					Integer.parseInt(
							tempEventList.substring(tempEventList.indexOf("|") + 1, tempEventList.indexOf(":"))),
					Boolean.parseBoolean(
							tempEventList.substring(tempEventList.indexOf(":") + 1, tempEventList.indexOf(","))));
			// trims list to next event
			tempEventList = tempEventList.substring(tempEventList.indexOf(",") + 1);
		}
		// universal save file
		tempEventList = uniSaveFile.substring(uniSaveFile.indexOf("[/EVE]") + 6, uniSaveFile.indexOf("[EVE/]") + 3);
		ArrayList<Integer> eventsUniSaveFile = new ArrayList<Integer>();
		while (tempEventList.contains(",")) {
			eventsUniSaveFile.add(Integer.parseInt(tempEventList.substring(0, tempEventList.indexOf(","))));
			// trims list to next event
			tempEventList = tempEventList.substring(tempEventList.indexOf(",") + 1);
		}

		// check the two against each other and the big list of persistents
		for (int event : CommonVar.persistentEvents) {
			if (eventsCurrentSaveFile.get(event) != null)
				if (eventsCurrentSaveFile.get(event) && !eventsUniSaveFile.contains(event))
					eventsUniSaveFile.add(event);
				else if (!eventsCurrentSaveFile.get(event) && eventsUniSaveFile.contains(event))
					eventsCurrentSaveFile.put(event, true);
		}

		// rewrite the files
		String triggeredEventList = "";
		for (int event : eventsCurrentSaveFile.keySet())
			triggeredEventList += "|" + event + ":" + eventsCurrentSaveFile.get(event) + ",";

		int startInd = currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18;
		int endInd = currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
		currentSavedGameFile = currentSavedGameFile.substring(0, startInd) + triggeredEventList
				+ currentSavedGameFile.substring(endInd);

		tempEventList = "";
		for (int i : eventsUniSaveFile)
			tempEventList += i + ",";
		uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/EVE]") + 6) + tempEventList
				+ uniSaveFile.substring(uniSaveFile.indexOf("[EVE/]"));

		// WRITE THE FILES
		writeToFile(SAVE_FILE);
		writeToFile(UNI_FILE);
	}

	public void writeToFile(int fileType) {
		FileHandle file;
		String lessThanTen;

		switch (fileType) {
		case SAVE_FILE:
			file = Gdx.files.local("SG/genericSG.mao");

			// adds 0 in front of current game num if less than 10
			lessThanTen = (conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";
			// indexes for current saved game portion
			SBStartInd = file.readString().indexOf("%" + lessThanTen + conscientia.getConscVar().currentSavedGameNum);
			SBEndInd = file.readString().indexOf(lessThanTen + conscientia.getConscVar().currentSavedGameNum + "%")
					+ (lessThanTen.length() + 1);

			// writes to file
			file.writeString(file.readString().substring(0, SBStartInd) + currentSavedGameFile
					+ file.readString().substring(SBEndInd), false);
			break;
		case NPC_FILE:
			file = Gdx.files.local("SG/NPCs.mao");

			// adds 0 in front of current game num if less than 10
			lessThanTen = (conscientia.getConscVar().currentSavedGameNum < 10)
					? ("0" + conscientia.getConscVar().currentSavedGameNum)
					: ("" + conscientia.getConscVar().currentSavedGameNum);

			// indexes for current saved game portion
			SBStartInd = file.readString().indexOf("%" + lessThanTen);
			SBEndInd = file.readString().indexOf(lessThanTen + "%");

			// Needs to be here to avoid the exponential increase in size of NPC
			// file
			NPCFile = file.readString().substring(SBStartInd, SBEndInd);

			// writes to file
			file.writeString(
					file.readString().substring(0, SBStartInd) + NPCFile + file.readString().substring(SBEndInd),
					false);
			break;
		case UNI_FILE:
			file = Gdx.files.local("SG/UniSave.mao");

			// writes to file
			file.writeString(uniSaveFile, false);
			break;
		}
	}

	public void writeNewGameFiles(int bookID) {
		FileHandle file;

		// Sets last saved num +1
		// GDX won't let me modify internal files, so I have to do it this way
		// or else the virgin save file would be overwritten
		if (Gdx.files.local("SG/genericSG.mao").exists()) {
			file = Gdx.files.local("SG/genericSG.mao");
			if (file.readString().contains("%")) {
				// SAVE FILE
				// Find last used save file number
				String fileContent = Gdx.files.local("SG/genericSG.mao").readString();
				int numIndStart = fileContent.lastIndexOf("~/") + 2;
				int numIndEnd = fileContent.lastIndexOf("%");
				// increments to make next saved file
				int newNum = Integer.parseInt(fileContent.substring(numIndStart, numIndEnd)) + 1;
				// sets the saved game num when we need to access it later
				conscientia.getConscVar().currentSavedGameNum = newNum;
				// checks to see if it's greater than 10, otherwise adds a 0 in
				// front of single digit
				String nextNumStr = (newNum < 10) ? "0" + newNum : "" + newNum;
				currentSavedGameFile = "%" + nextNumStr + "/~"
						+ Gdx.files.internal("Game Files/DefaultSavedGame.mao").readString() + "~/" + nextNumStr + "%";
				// set bookID
				currentSavedGameFile = currentSavedGameFile.substring(0,
						currentSavedGameFile.lastIndexOf("{PERSONALITY}") + 13) + "{BOOK ID}" + bookID + "{BOOK ID}"
						+ currentSavedGameFile.substring(currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"));
				// sets start address for selected book
				setStartAdd(bookID);
				// sets persistent acquirables
				addAcq(bookID, false);
				// sets persistent events
				addEvents(bookID, true);
				// appends new saved game to saved file
				Gdx.files.local("SG/genericSG.mao").writeString(currentSavedGameFile, true);
				// NPC FILE
				NPCFile = "%" + nextNumStr + "/~" + Gdx.files.internal("Game Files/NPCs.mao").readString() + "~/"
						+ nextNumStr + "%";
				Gdx.files.local("SG/NPCs.mao").writeString(NPCFile, true);
			} else {
				generateNewSave(bookID);
			}
		} else {
			generateNewSave(bookID);
		}
	}

	private void generateNewSave(int bookID) {
		// if brand new, no saved files
		// SAVE FILE
		// sets game number to 0
		conscientia.getConscVar().currentSavedGameNum = 0;
		currentSavedGameFile = "%00/~" + Gdx.files.internal("Game Files/DefaultSavedGame.mao").readString() + "~/00%";
		// set bookID
		currentSavedGameFile = currentSavedGameFile.substring(0, currentSavedGameFile.lastIndexOf("{PERSONALITY}") + 13)
				+ "{BOOK ID}" + bookID + "{BOOK ID}"
				+ currentSavedGameFile.substring(currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"));
		// sets start address for selected book
		setStartAdd(bookID);
		// if Eidos, then add in acquirables specific to her
		addAcq(bookID, true);
		// sets persistent events
		addEvents(bookID, true);
		// creates new saved game to saved file
		Gdx.files.local("SG/genericSG.mao").writeString(currentSavedGameFile, false);

		// NPC FILE
		NPCFile = "%00/~" + Gdx.files.internal("Game Files/NPCs.mao").readString() + "~/00%";
		Gdx.files.local("SG/NPCs.mao").writeString(NPCFile, false);

		// MAPS FILE
		FileHandle file;
		file = Gdx.files.internal("Game Files/Maps.mao");
		String mapString = file.readString();
		file = Gdx.files.local("SG/Maps.mao");
		// writes to file
		file.writeString(mapString, false);
	}

	private void addEvents(int bookID, boolean virgin) {
		loadFile(UNI_FILE, false);
		try {
			conscientia.setUseWhinersFont(uniSaveFile
					.substring(uniSaveFile.indexOf("[/FONT]") + 7, uniSaveFile.indexOf("[FONT/]")).equals("1"));
		} catch (Exception e) {
			conscientia.setUseWhinersFont(false);
			uniSaveFile += "[/FONT]0[FONT/]";
			writeToFile(UNI_FILE);
		}
		if (virgin && bookID == CommonVar.EID) {
			// need try catch because otherwise new games fail
			try {
				String events = uniSaveFile
						.substring(uniSaveFile.indexOf("[/EVE_E]") + 8, uniSaveFile.indexOf("[EVE_E/]")).trim();
				while (events.length() > 1) {
					// trims initial ,
					if (events.substring(0, 1).equals(","))
						events = events.substring(1);
					String specificEvent = events.substring(0, events.indexOf(","));
					int start = currentSavedGameFile.indexOf("|" + specificEvent + ":") + specificEvent.length() + 2;
					currentSavedGameFile = currentSavedGameFile.substring(0, start) + "true"
							+ currentSavedGameFile.substring(currentSavedGameFile.indexOf(",", start));
					// next event
					events = events.substring(events.indexOf(","));
				}
			} catch (Exception e) {
				uniSaveFile += "[/EVE_E]2050,[EVE_E/]";
				writeToFile(UNI_FILE);
			}
		} else if (virgin && bookID == CommonVar.TOR) {
			// sets map feature as true
			mgScr.mgVar.hasMaps = true;
		}

		String persistentEvents = uniSaveFile.substring(uniSaveFile.indexOf("[/EVE]") + 6,
				uniSaveFile.indexOf("[EVE/]"));

		ArrayList<String> eventsList = new ArrayList<String>();
		while (true) {
			if (!persistentEvents.contains(","))
				break;
			else {
				eventsList.add(persistentEvents.substring(0, persistentEvents.indexOf(",")).trim());
				persistentEvents = persistentEvents.substring(persistentEvents.indexOf(",") + 1);
			}
		}

		int start = 0;
		if (eventsList.size() > 0)
			for (String event : eventsList) {
				start = currentSavedGameFile.indexOf(event + ":") + event.length() + 1;
				currentSavedGameFile = currentSavedGameFile.substring(0, start) + "true"
						+ currentSavedGameFile.substring(currentSavedGameFile.indexOf(",", start));
			}
	}

	private void addAcq(int bookID, boolean virgin) {
		if (virgin && bookID == CommonVar.EID) {
			loadFile(UNI_FILE, false);
			String acqs = uniSaveFile.substring(uniSaveFile.indexOf("[/ACQ_E]") + 8, uniSaveFile.indexOf("[ACQ_E/]"));
			currentSavedGameFile = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{ACQUIRABLE}"))
					+ "{ACQUIRABLE}" + acqs
					+ currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		} else if (!virgin) {
			loadFile(UNI_FILE, false);
			String acqs = uniSaveFile.substring(uniSaveFile.indexOf("[/ACQ_UNI]") + 10,
					uniSaveFile.indexOf("[ACQ_UNI/]"));

			if (bookID == CommonVar.EID && acqs.length() == 0) {
				acqs = uniSaveFile.substring(uniSaveFile.indexOf("[/ACQ_E]") + 8, uniSaveFile.indexOf("[ACQ_E/]"));
				currentSavedGameFile = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{ACQUIRABLE}"))
						+ "{ACQUIRABLE}" + acqs
						+ currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
			} else
				currentSavedGameFile = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{ACQUIRABLE}"))
						+ "{ACQUIRABLE}" + acqs
						+ currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		}

	}

	private void setStartAdd(int bookID) {
		// set starting address for given book
		loadFile(UNI_FILE, false);

		String firstPart = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf(":") + 1);
		int startAddInd = uniSaveFile.indexOf("|" + bookID + ":") + 3;
		String address = uniSaveFile.substring(startAddInd, uniSaveFile.indexOf(",", startAddInd));

		String lastPart = currentSavedGameFile.substring(currentSavedGameFile.indexOf(","));
		currentSavedGameFile = firstPart + address + lastPart;
	}

	// READ METHODS
	public HashMap<Integer, Boolean> loadTriggeredEvents() {
		// loads save file
		loadFile(SAVE_FILE, false);

		HashMap<Integer, Boolean> tEvents = new HashMap<Integer, Boolean>();
		String tempStr = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));

		int start;
		int end;
		while (tempStr.contains("|")) {
			// puts triggered event into array
			start = tempStr.indexOf('|') + 1;
			end = tempStr.indexOf(',');
			tEvents.put(Integer.parseInt(tempStr.substring(start, tempStr.indexOf(":"))),
					Boolean.parseBoolean(tempStr.substring(tempStr.indexOf(":") + 1, end)));
			// trims string to next relevant area
			tempStr = tempStr.substring(end + 1);
		}

		// Updates Awareness metric
		for (int event : mgScr.getPersistentEvents())
			if (tEvents.get(event)) {
				mgScr.setAwareness(mgScr.getAwareness() + 1);
				conscientia.getConscVar().persistentItemsAndEvents.add(event);
			}

		return tEvents;
	}

	public HashMap<String, String> getDialogueAddressesMap(String ID) {
		HashMap<String, String> tempMap = new HashMap<String, String>();

		String tempStr = NPCFile.substring(NPCFile.indexOf("[/" + ID + "]"), NPCFile.indexOf("[" + ID + "/]"));
		tempStr = tempStr.substring(tempStr.indexOf("(") + 1, tempStr.indexOf(")"));

		String tempLoc, tempAdd;

		while (tempStr.length() > 1) {
			// parses the add & loc
			tempLoc = tempStr.substring(0, tempStr.indexOf(":"));
			tempAdd = tempStr.substring(tempStr.indexOf(":") + 1, tempStr.indexOf(","));
			// adds pair to hashMap
			tempMap.put(tempLoc, tempAdd);
			// trims string
			tempStr = tempStr.substring(tempStr.indexOf(",") + 1);
		}

		return tempMap;
	}

	public int[] getNPCs(String currentLocation) {
		int[] npcList = new int[10];

		// sets all items in array to -1 so that this can indicate unused
		// indexes
		for (int i = 0; i < npcList.length; i++)
			npcList[i] = -1;

		FileHandle file = Gdx.files.internal("Game Files/NPCListByLocation.mao");
		// looks at actual list, parses the ints and adds them to the array
		String tempNPC = file.readString();
		// trims list to current location

		for (int i = 0; i < 3; i++) {
			tempNPC = tempNPC.substring(
					tempNPC.indexOf("[/" + currentLocation.substring(0, currentLocation.indexOf('!')) + "]"),
					tempNPC.indexOf("[" + currentLocation.substring(0, currentLocation.indexOf('!')) + "/]"));
			// trims the location to area area
			currentLocation = currentLocation.substring(currentLocation.indexOf('!') + 1);
		}
		// trims to array of NPCs in file
		tempNPC = tempNPC.substring(tempNPC.indexOf('|') + 1, tempNPC.lastIndexOf('|'));
		// adds them to the NPC array that will be returned
		int index = 0;
		while (tempNPC.contains(",")) {
			npcList[index++] = Integer.parseInt(tempNPC.substring(0, tempNPC.indexOf(',')));
			tempNPC = tempNPC.substring(tempNPC.indexOf(',') + 1);
		}

		return npcList;
	}

	// loads NPC stats to a string array and then returns the array
	public String[] getNPCsStats(String id) {
		String[] tempArray = new String[NPC.STATS_ARRAY_LENGTH];

		// loads NPC save file
		loadFile(NPC_FILE, false);

		try {
			// trims to relevant NPC as determined by id
			String tempStats = NPCFile.substring(NPCFile.indexOf("[/" + id + "]"), NPCFile.indexOf("[" + id + "/]"));
			// trims only to stats array in file
			tempStats = tempStats.substring(tempStats.indexOf('{') + 1, tempStats.indexOf('}'));

			int index = 0;
			while (tempStats.length() > 0) {
				// puts next item into array
				tempArray[index++] = tempStats.substring(0, tempStats.indexOf(','));
				// trims tempStats
				tempStats = tempStats.substring(tempStats.indexOf(',') + 1);
			}
		} catch (Exception e) {
			mgScr.loadingUtils.nullError("NPC_STATS: " + id);
		}

		return tempArray;
	}

	public void addNPC(String id) {
		FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
		String addNPC = file.readString();
		// must have, or else the final ']' gets overwritten
		String NPCentry = addNPC.substring(addNPC.indexOf("[/" + id), addNPC.indexOf("/]", addNPC.indexOf(id))) + "/] ";

		NPCFile = NPCFile.substring(0, NPCFile.indexOf("[/DESCRIPTION]") - 1) + NPCentry
				+ NPCFile.substring(NPCFile.indexOf("[/DESCRIPTION]"));
	}

	public int[] getNPCsCombatStats(String id) {
		ArrayList<Integer> tempArray = new ArrayList<Integer>();

		// loads NPC save file
		loadFile(NPC_FILE, false);

		// trims to relevant NPC as determined by id
		String tempStats = NPCFile.substring(NPCFile.indexOf("[/" + id + "]"), NPCFile.indexOf("[" + id + "/]"));
		// trims only to stats array in file
		tempStats = tempStats.substring(tempStats.indexOf('*') + 1, tempStats.lastIndexOf('*'));

		while (tempStats.length() > 0) {
			// puts next item into array
			tempArray.add(Integer.parseInt(tempStats.substring(0, tempStats.indexOf(','))));
			// trims tempStats
			tempStats = tempStats.substring(tempStats.indexOf(',') + 1);
		}

		int[] combatStats = new int[tempArray.size()];
		int ind = 0;
		for (Integer i : tempArray)
			combatStats[ind++] = i;

		return combatStats;
	}

	public HashMap<String, String> getNPCsDialogueAdds(String id) {
		HashMap<String, String> tempMap = new HashMap<String, String>();

		// loads NPC save file
		loadFile(NPC_FILE, false);

		// trims to relevant NPC as determined by id
		String tempAdds = NPCFile.substring(NPCFile.indexOf("[/" + id + "]"), NPCFile.indexOf("[" + id + "/]"));
		// trims only to stats array in file
		tempAdds = tempAdds.substring(tempAdds.indexOf('(') + 1, tempAdds.indexOf(')'));

		while (tempAdds.length() > 0) {
			// puts next item into array
			tempMap.put(tempAdds.substring(0, tempAdds.indexOf(':')).trim(),
					tempAdds.substring(tempAdds.indexOf(':') + 1, tempAdds.indexOf(',')).trim());
			// trims tempStats
			tempAdds = tempAdds.substring(tempAdds.indexOf(',') + 1);
		}

		return tempMap;
	}

	public void setPlayerStats() {
		// loads save file
		loadFile(SAVE_FILE, false);

		// sets all the relevant game variables according to the save file
		// loads current location to determine correct dialogue file
		loadCurrentLocation();

		// load personality stats
		loadPersonalityAffinity();

		// load items acquired
		loadItemsAcquired();

		// set awareness stat
		loadAwareness();
	}

	private void loadCurrentLocation() {
		if (mgScr.getCurrentLocation() == null)
			mgScr.setCurrentLocation(
					currentSavedGameFile.substring(currentSavedGameFile.indexOf("currentLocation:") + 16,
							currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("currentLocation:"))));
	}

	private void loadPersonalityAffinity() {
		// loads personality stats
		mgScr.getPlayer()
				.setDiplomat(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("A:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("A:")))));
		mgScr.getPlayer()
				.setTruthseeker(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("B:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("B:")))));
		mgScr.getPlayer()
				.setNeutral(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("C:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("C:")))));
		mgScr.getPlayer()
				.setSurvivalist(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("D:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("D:")))));
		mgScr.getPlayer()
				.setTyrant(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("E:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("E:")))));
		mgScr.getPlayer()
				.setLoon(Integer.parseInt(currentSavedGameFile.substring(currentSavedGameFile.indexOf("F:") + 2,
						currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("F:")))));
	}

	private void loadItemsAcquired() {
		// loads glyphs, abilities and logs
		loadFile(SAVE_FILE, false);
		String tempItemList = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{ACQUIRABLE}"),
				currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		while (tempItemList.contains("|")) {
			mgScr.getPlayer().getItemsAcquired().add(
					Integer.parseInt(tempItemList.substring(tempItemList.indexOf("|") + 1, tempItemList.indexOf(","))));
			// trims list to next glyph
			tempItemList = tempItemList.substring(tempItemList.indexOf(",") + 1);
		}

		for (Integer i : CommonVar.persistentAcquirables)
			if (mgScr.getPlayer().getItemsAcquired().contains(i))
				conscientia.getConscVar().persistentItemsAndEvents.add(i);
	}

	private void loadAwareness() {
		// loads glyphs, abilities and logs
		loadFile(SAVE_FILE, false);
		String awareness = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{AWARENESS}") + 11,
				currentSavedGameFile.lastIndexOf("{AWARENESS}")).trim();
		mgScr.setAwareness(Integer.parseInt(awareness));
	}

	public void gameSave() {
		// loads save file
		loadFile(SAVE_FILE, true);

		// UPDATES CURRENT LOCATION
		updateCurrentLocation();

		// UPDATES CURRENT NPC
		updateCurrentNPC();

		// UPDATES ACQUIRABLES
		updateAcquirables();

		// AWARENESS
		updateAwareness();

		// UPDATES PLAYER STATS
		updatePlayerStats();

		// UPDATES TRIGGERED EVENTS
		updateTriggeredEvents();

		writeToFile(SAVE_FILE);

		savePersistents();
	}

	public void updateCurrentLocation() {
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("currentLocation:") + 16);
		String secondHalf = currentSavedGameFile
				.substring(currentSavedGameFile.indexOf(',', currentSavedGameFile.indexOf("currentLocation:")) + 1);

		currentSavedGameFile = firstHalf + mgScr.getCurrentLocation() + ',' + secondHalf;
	}

	public void updateCurrentNPC() {
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{CURRENT NPC}") + 13);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{CURRENT NPC}"));

		currentSavedGameFile = firstHalf + mgScr.getCurrentNPC() + secondHalf;
	}

	public void updateAcquirables() {
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{ACQUIRABLE}") + 12);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));

		// has eidos glyph & extraction glyph automatically if in book of Eidos
		String itemList = "";
		for (Integer i : mgScr.getPlayer().getItemsAcquired())
			// here to avoid duplicate entries
			// TODO, find out why there are duplicate entries
			if (!itemList.contains("|" + i + ","))
				itemList += "|" + i + ",";

		currentSavedGameFile = firstHalf + itemList + secondHalf;
	}

	public void updateAwareness() {
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{AWARENESS}") + 11);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{AWARENESS}"));

		currentSavedGameFile = firstHalf + mgScr.getAwareness() + secondHalf;
	}

	public void updatePlayerStats() {
		// sets Diplomat's stats
		String firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("A:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("A:", currentSavedGameFile.indexOf("{PERSONALITY}"))));

		currentSavedGameFile = firstHalf + mgScr.getPlayer().getDiplomat() + secondHalf;

		// sets Truthseeker's stats
		firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("B:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("B:", currentSavedGameFile.indexOf("{PERSONALITY}"))));

		currentSavedGameFile = firstHalf + mgScr.getPlayer().getTruthseeker() + secondHalf;

		// sets Neutral's stats
		firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("C:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("C:", currentSavedGameFile.indexOf("{PERSONALITY}"))));
		currentSavedGameFile = firstHalf + mgScr.getPlayer().getNeutral() + secondHalf;

		// sets Survivalist's stats
		firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("D:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("D:", currentSavedGameFile.indexOf("{PERSONALITY}"))));

		currentSavedGameFile = firstHalf + mgScr.getPlayer().getSurvivalist() + secondHalf;

		// sets Tyrant's stats
		firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("E:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("E:", currentSavedGameFile.indexOf("{PERSONALITY}"))));

		currentSavedGameFile = firstHalf + mgScr.getPlayer().getTyrant() + secondHalf;

		// sets Loon's stats
		firstHalf = currentSavedGameFile.substring(0,
				currentSavedGameFile.indexOf("F:", currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = currentSavedGameFile.substring(currentSavedGameFile.indexOf(',',
				currentSavedGameFile.indexOf("F:", currentSavedGameFile.indexOf("{PERSONALITY}"))));

		currentSavedGameFile = firstHalf + mgScr.getPlayer().getLoon() + secondHalf;
	}

	public void updateTriggeredEvents() {
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));

		for (Integer event : conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
			firstHalf += "|" + event + ":" + conscientia.getConscVar().triggeredEvents.get(event) + ",";

		currentSavedGameFile = firstHalf + secondHalf;
	}

	public void savePersistents() {
		String itemList = "", eventsList = "";

		for (Integer i : CommonVar.persistentAcquirables)
			if (mgScr.getPlayer().getItemsAcquired().contains(i))
				itemList += "|" + i + ",";

		for (Integer i : CommonVar.persistentEvents)
			if (conscientia.getConscVar().triggeredEvents.get(i))
				eventsList += i + ",";

		loadFile(UNI_FILE, false);

		uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/ACQ_UNI]") + 10) + itemList
				+ uniSaveFile.substring(uniSaveFile.indexOf("[ACQ_UNI/]"));

		uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/EVE]") + 6) + eventsList
				+ uniSaveFile.substring(uniSaveFile.indexOf("[EVE/]"));

		writeToFile(UNI_FILE);
	}

	public String getNPCsbyNum(int NPCnum) {
		FileHandle file = Gdx.files.internal("Game Files/NPCsbyNum.mao");
		String npcsByNumFile = file.readString();
		String name;
		int num;

		while (npcsByNumFile.contains("|")) {
			name = npcsByNumFile.substring(npcsByNumFile.indexOf("|") + 1, npcsByNumFile.indexOf(":"));
			num = Integer.parseInt(npcsByNumFile.substring(npcsByNumFile.indexOf(":") + 1, npcsByNumFile.indexOf(",")));

			if (NPCnum == num)
				return name;
			else
				npcsByNumFile = npcsByNumFile.substring(npcsByNumFile.indexOf(",") + 1);
		}
		mgScr.loadingUtils.nullError("NPC_BY_NUM: " + NPCnum);
		return "";
	}

	public HashMap<String, Integer> getNPCsbyNumHashMap() {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

		FileHandle file = Gdx.files.internal("Game Files/NPCsbyNum.mao");
		String npcsByNumFile = file.readString();
		String name;
		int num;

		while (npcsByNumFile.contains("|")) {
			name = npcsByNumFile.substring(npcsByNumFile.indexOf("|") + 1, npcsByNumFile.indexOf(":"));
			num = Integer.parseInt(npcsByNumFile.substring(npcsByNumFile.indexOf(":") + 1, npcsByNumFile.indexOf(",")));

			tempMap.put(name, num);

			npcsByNumFile = npcsByNumFile.substring(npcsByNumFile.indexOf(",") + 1);
		}

		return tempMap;
	}

	public void saveNPCstats(String NPCname, String currentAddress, String currentLocation) {
		FileHandle file = Gdx.files.local("SG/NPCs.mao");

		// adds 0 in front of current game num if less than 10
		String lessThanTen = (conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";

		// find relevant NPC & relevant location
		int npcInd = file.readString().indexOf("%" + lessThanTen + conscientia.getConscVar().currentSavedGameNum);
		npcInd = file.readString().indexOf(":",
				file.readString().indexOf(currentLocation, file.readString().indexOf("[/" + NPCname, npcInd))) + 1;

		// writes to file
		file.writeString(file.readString().substring(0, npcInd) + currentAddress
				+ file.readString().substring(file.readString().indexOf(",", npcInd)), false);
	}

	private String getLocation(String tempFile, int end) {
		String location = "";
		// parse out location
		location = tempFile.substring(tempFile.indexOf(":", end) + 1, tempFile.indexOf(",", end));
		location = location.substring(location.indexOf("!") + 1);
		location = location.substring(0, location.indexOf("!")) + " - "
				+ location.substring(location.indexOf("!") + 1, location.length() - 1);

		return location;
	}

	public int getCurrentNPC() {
		// loads save file
		loadFile(SAVE_FILE, false);

		int startInd = currentSavedGameFile.indexOf("{CURRENT NPC}") + 13;
		int endInd = currentSavedGameFile.indexOf("{CURRENT NPC}", startInd);

		int currentNpc = Integer.parseInt(currentSavedGameFile.substring(startInd, endInd).trim());

		return currentNpc;
	}

	public void loadSavedGameFiles() {
		// loads save file
		loadFile(SAVE_FILE, true);

		// loads NPC save file
		loadFile(NPC_FILE, true);

		// sets font preference
		loadFile(UNI_FILE, false);
		try {
			conscientia.setUseWhinersFont(uniSaveFile
					.substring(uniSaveFile.indexOf("[/FONT]") + 7, uniSaveFile.indexOf("[FONT/]")).equals("1"));
		} catch (Exception e) {
			conscientia.setUseWhinersFont(false);
			uniSaveFile += "[/FONT]0[FONT/]";
			writeToFile(UNI_FILE);
		}
	}

	public String loadCurrentLocationFromSavedGameFiles() {
		// loads save file
		loadFile(SAVE_FILE, false);

		String currentLocation = currentSavedGameFile.substring(currentSavedGameFile.indexOf("currentLocation:") + 16,
				currentSavedGameFile.indexOf(","));

		return currentLocation;
	}

	public String[] loadMindscapeStuff() {
		// loads save file
		loadFile(SAVE_FILE, false);

		String relevantArea = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{MINDSCAPE}") + 11,
				currentSavedGameFile.lastIndexOf("{MINDSCAPE}"));

		String lastAdd = relevantArea.substring(1, relevantArea.indexOf(":"));
		String lastNPC = relevantArea.substring(relevantArea.indexOf(":") + 1, relevantArea.indexOf(","));

		return new String[] { lastAdd, lastNPC };
	}

	public HashMap<String, ArrayList<String>> getEventCues() {
		FileHandle file = Gdx.files.internal("Game Files/Cues.mao");

		String fileContents = file.readString();

		HashMap<String, ArrayList<String>> cues = new HashMap<String, ArrayList<String>>();

		// Load a hashmap of String CATEGORY to String[] of ADDRESSES
		while (fileContents.contains("[/")) {
			String categoryContents = fileContents.substring(fileContents.indexOf("[/"), fileContents.indexOf("/]"));
			ArrayList<String> categoryAddresses = new ArrayList<String>();
			while (categoryContents.contains("|")) {
				categoryAddresses.add(
						categoryContents.substring(categoryContents.indexOf("|") + 1, categoryContents.indexOf(",")));
				categoryContents = categoryContents.substring(categoryContents.indexOf(",") + 1);
			}
			// adds String[] and Category to HashMap with Category as the key
			// and the array as the value
			cues.put(fileContents.substring(fileContents.indexOf("[/") + 2, fileContents.indexOf("]")),
					categoryAddresses);
			// trims file contents to next category
			fileContents = fileContents.substring(fileContents.indexOf("/]") + 2);
		}

		return cues;
	}

	public void mindscapeSave() {
		// loads save file
		loadFile(SAVE_FILE, false);
		// UPDATES PRE-MINDSCAPE LOCATION & NPC
		String firstHalf = currentSavedGameFile.substring(0, currentSavedGameFile.indexOf("{MINDSCAPE}") + 11);
		String secondHalf = currentSavedGameFile.substring(currentSavedGameFile.lastIndexOf("{MINDSCAPE}"));

		currentSavedGameFile = firstHalf + "|" + mgScr.mgVar.lastAddBeforeMindEntry + ":" + mgScr.getCurrentNPC() + ","
				+ secondHalf;

		writeToFile(SAVE_FILE);
	}

	public String getCombatDescription(int id, boolean playerVictorious, int ability) {
		FileHandle file = Gdx.files.internal("Game Files/CombatDescription.mao");

		String fileContents = file.readString();

		// trim to relevant item
		fileContents = fileContents.substring(fileContents.indexOf("[/" + id + "]"),
				fileContents.indexOf("[" + id + "/]"));
		// trim to relevant dialogue
		if (playerVictorious)
			return fileContents.substring(fileContents.indexOf(":", fileContents.indexOf("*" + ability)) + 1,
					fileContents.indexOf(ability + "*"));
		else
			return fileContents.substring(fileContents.indexOf("@") + 1, fileContents.lastIndexOf("@"));
	}

	public String getAcquirableTitle(int id) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");

		String fileContents = file.readString();

		// trim to relevant item
		fileContents = fileContents.substring(fileContents.indexOf("[/" + id + "]"),
				fileContents.indexOf("[" + id + "/]"));
		return "\n" + fileContents.substring(fileContents.indexOf("*") + 1, fileContents.lastIndexOf("*"));
	}

	public String getAcquirableListString(int id) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");

		String fileContents = file.readString();

		// trim to relevant item
		fileContents = fileContents.substring(fileContents.indexOf("[/" + id + "]"),
				fileContents.indexOf("[" + id + "/]"));
		return fileContents.substring(fileContents.indexOf("@") + 1, fileContents.lastIndexOf("@"));
	}

	public String getAcquirableImage(int id) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");

		String fileContents = file.readString();

		// trim to relevant item
		fileContents = fileContents.substring(fileContents.indexOf("[/" + id + "]"),
				fileContents.indexOf("[" + id + "/]"));
		return fileContents.substring(fileContents.indexOf("$") + 1, fileContents.lastIndexOf("$"));
	}

	public String getAcquirableExplanationText(int id) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");

		String fileContents = file.readString();

		// trim to relevant item
		fileContents = fileContents.substring(fileContents.indexOf("[/" + id + "]"),
				fileContents.indexOf("[" + id + "/]"));
		return fileContents.substring(fileContents.indexOf("#") + 1, fileContents.lastIndexOf("#"));
	}

	public Log[] loadLogs(ArrayList<Log> logs) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");
		String fileContents = file.readString();

		Log[] logList = new Log[logs.size()];
		int counter = 0;

		for (Log log : logs) {
			// title
			String logEntry = fileContents.substring(fileContents.indexOf("[/" + log.getID() + "]"),
					fileContents.indexOf("[" + log.getID() + "/]"));
			log.setTitle("\n" + logEntry.substring(logEntry.indexOf("*") + 1, logEntry.lastIndexOf("*")));
			// To String (list title)
			log.setToString(logEntry.substring(logEntry.indexOf("@") + 1, logEntry.lastIndexOf("@")));
			// img pathway
			log.setImgPathway(logEntry.substring(logEntry.indexOf("$") + 1, logEntry.lastIndexOf("$")));
			// explanation text
			log.setExplanationText(logEntry.substring(logEntry.indexOf("#") + 1, logEntry.lastIndexOf("#")));
			// adds log to list
			logList[counter++] = log;
		}
		return logList;
	}

	public Glyph[] loadGlyphs(ArrayList<Glyph> glyphs) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");
		String fileContents = file.readString();

		Glyph[] glyphList = new Glyph[glyphs.size()];
		int counter = 0;

		for (Glyph glyph : glyphs) {
			// title
			String glyphEntry = fileContents.substring(fileContents.indexOf("[/" + glyph.getID() + "]"),
					fileContents.indexOf("[" + glyph.getID() + "/]"));
			glyph.setTitle("\n" + glyphEntry.substring(glyphEntry.indexOf("*") + 1, glyphEntry.lastIndexOf("*")));
			// To String (list title)
			glyph.setToString(glyphEntry.substring(glyphEntry.indexOf("@") + 1, glyphEntry.lastIndexOf("@")));
			// img pathway
			glyph.setImgPathway(glyphEntry.substring(glyphEntry.indexOf("$") + 1, glyphEntry.lastIndexOf("$")));
			// explanation text
			glyph.setExplanationText(glyphEntry.substring(glyphEntry.indexOf("#") + 1, glyphEntry.lastIndexOf("#")));
			// adds glyph to list
			glyphList[counter++] = glyph;
		}
		return glyphList;
	}

	public MindscapeNPC[] loadMindscapeNPCs(ArrayList<MindscapeNPC> mindscapeNPCs) {
		FileHandle file = Gdx.files.internal("Game Files/AcquirableFile.mao");
		String fileContents = file.readString();

		MindscapeNPC[] mindscapeNPCList = new MindscapeNPC[mindscapeNPCs.size()];
		int counter = 0;

		for (MindscapeNPC mindscapeNPC : mindscapeNPCs) {
			// title
			String mindscapeNPCEntry = fileContents.substring(fileContents.indexOf("[/" + mindscapeNPC.getID() + "]"),
					fileContents.indexOf("[" + mindscapeNPC.getID() + "/]"));
			mindscapeNPC.setTitle("\n" + mindscapeNPCEntry.substring(mindscapeNPCEntry.indexOf("*") + 1,
					mindscapeNPCEntry.lastIndexOf("*")));
			// To String (list title)
			mindscapeNPC.setToString(mindscapeNPCEntry.substring(mindscapeNPCEntry.indexOf("@") + 1,
					mindscapeNPCEntry.lastIndexOf("@")));
			// img pathway
			mindscapeNPC.setImgPathway(mindscapeNPCEntry.substring(mindscapeNPCEntry.indexOf("$") + 1,
					mindscapeNPCEntry.lastIndexOf("$")));
			// explanation text
			mindscapeNPC.setExplanationText(mindscapeNPCEntry.substring(mindscapeNPCEntry.indexOf("#") + 1,
					mindscapeNPCEntry.lastIndexOf("#")));
			// adds mindscapeNPC to list
			mindscapeNPCList[counter++] = mindscapeNPC;
		}
		return mindscapeNPCList;
	}

	public void rewriteMostRecentEvents(int eventNum) {
		int[] indexes = new int[2];

		indexes[0] = (currentSavedGameFile.indexOf("|" + eventNum + ":") == -1) ? -1
				: currentSavedGameFile.indexOf("|" + eventNum + ":") + 7;
		indexes[1] = currentSavedGameFile.indexOf(",", indexes[0]);

		String firstHalf = currentSavedGameFile.substring(0, indexes[0]);
		String secondHalf = currentSavedGameFile.substring(indexes[1]);

		currentSavedGameFile = firstHalf + "true" + secondHalf;

		// write to save file
		writeToFile(SAVE_FILE);
	}

	public String loadCredits() {
		// loads credits file
		FileHandle file = Gdx.files.internal("Game Files/Credits.mao");
		String creditsStr = file.readString();

		return creditsStr;
	}

	public String loadSplashQuote() {
		// loads bookOfBiracul file
		FileHandle file = Gdx.files.internal("Game Files/BookOfBiracul.mao");
		String bookStr = file.readString();

		Random rand = new Random();
		int quoteInd = rand.nextInt(NUM_BIRACULIAN_VERSES);

		String quoteStr = bookStr.substring(bookStr.indexOf("[/" + quoteInd), bookStr.indexOf(quoteInd + "/]"));
		quoteStr = quoteStr.substring(quoteStr.indexOf("\""), quoteStr.length() - 2);
		return quoteStr;
	}

	public void actBook(int book) {
		int startInd = 0, endInd = 0;

		if (Gdx.files.local("SG/UniSave.mao").exists()) {
			loadFile(UNI_FILE, false);
			switch (book) {
			case CommonVar.BIR:
				startInd = uniSaveFile.indexOf("|B:") + 3;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "VIRACOCHA" + uniSaveFile.substring(endInd);
					startInd = uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					uniSaveFile = uniSaveFile.substring(0, startInd) + "2000," + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			case CommonVar.EID:
				startInd = uniSaveFile.indexOf("|E:") + 3;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "SINGULARITY" + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			case CommonVar.RIK:
				startInd = uniSaveFile.indexOf("|R:") + 3;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "ARKSBANE" + uniSaveFile.substring(endInd);
					startInd = uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					uniSaveFile = uniSaveFile.substring(0, startInd) + "2001," + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			case CommonVar.THE:
				startInd = uniSaveFile.indexOf("|Th:") + 4;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "DEATHSLAYER" + uniSaveFile.substring(endInd);
					startInd = uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					uniSaveFile = uniSaveFile.substring(0, startInd) + "2002," + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			case CommonVar.TOR:
				startInd = uniSaveFile.indexOf("|T:") + 3;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "NON-PROPHET" + uniSaveFile.substring(endInd);
					startInd = uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					uniSaveFile = uniSaveFile.substring(0, startInd) + "2003," + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			case CommonVar.WUL:
				startInd = uniSaveFile.indexOf("|W:") + 3;
				endInd = uniSaveFile.indexOf(",", startInd);
				if (uniSaveFile.substring(startInd, endInd).equals("?")) {
					uniSaveFile = uniSaveFile.substring(0, startInd) + "BEAST OF THIUDA"
							+ uniSaveFile.substring(endInd);
					startInd = uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					uniSaveFile = uniSaveFile.substring(0, startInd) + "2004," + uniSaveFile.substring(endInd);

					writeToFile(UNI_FILE);
				}
				break;
			}
		} else {
			// writes universal save file
			uniSaveFile = Gdx.files.internal("Game Files/UniSave.mao").readString();
			Gdx.files.local("SG/UniSave.mao").writeString(uniSaveFile, false);
			actBook(book);
		}
	}

	public boolean checkBookListSpecific(int bookID) {
		loadFile(UNI_FILE, false);

		int startInd = 0, endInd = 0;

		switch (bookID) {
		case CommonVar.BIR:
			startInd = uniSaveFile.indexOf("|B:") + 3;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("VIRACOCHA"))
				return true;
			else
				return false;
		case CommonVar.EID:
			startInd = uniSaveFile.indexOf("|E:") + 3;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("SINGULARITY"))
				return true;
			else
				return false;
		case CommonVar.RIK:
			startInd = uniSaveFile.indexOf("|R:") + 3;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("ARKSBANE"))
				return true;
			else
				return false;
		case CommonVar.THE:
			startInd = uniSaveFile.indexOf("|Th:") + 4;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("DEATHSLAYER"))
				return true;
			else
				return false;
		case CommonVar.TOR:
			startInd = uniSaveFile.indexOf("|T:") + 3;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("NON-PROPHET"))
				return true;
			else
				return false;
		case CommonVar.WUL:
			startInd = uniSaveFile.indexOf("|W:") + 3;
			endInd = uniSaveFile.indexOf(",", startInd);
			if (uniSaveFile.substring(startInd, endInd).equals("BEAST OF THIUDA"))
				return true;
			else
				return false;
		}
		return false;
	}

	public Book[] loadBookList() {
		Book[] booksOwned = new Book[6];

		int numOfBooksOwned = 0;

		// checks to see if you own the book
		for (int i = 0; i < 6; i++) {
			if (checkBookListSpecific(i)) {
				booksOwned[i] = new Book(i);
				numOfBooksOwned++;
			}
		}

		// makes actual list of books owned
		Book[] booksTrulyOwned = new Book[numOfBooksOwned];
		int ind = 0;
		for (Book b : booksOwned)
			if (b != null)
				booksTrulyOwned[ind++] = b;

		return booksTrulyOwned;
	}

	public SavedGame[] loadScreenList() {
		SavedGame[] savedGameList = new SavedGame[500];
		FileHandle file;

		if (Gdx.files.local("SG/genericSG.mao").exists()) {
			file = Gdx.files.local("SG/genericSG.mao");
			if (!file.readString().contains("%"))
				return savedGameList;
		} else
			return savedGameList;

		String tempSaveFileStr = file.readString();

		// see how many saved games there are and save them to array
		int start = 0, end = 0, index = 0;
		while (true) {
			// game file number
			start = tempSaveFileStr.indexOf("%") + 1;
			end = tempSaveFileStr.indexOf("/~", start);
			// if there is a NumberFormatException thrown by the IntParser, then
			// the loop breaks because even with trimming the file it still
			// wouldn't break otherwise
			try {
				if (index - 1 != Integer.parseInt(tempSaveFileStr.substring(start, end)))
					savedGameList[index++] = new SavedGame(Integer.parseInt(tempSaveFileStr.substring(start, end)),
							getLocation(tempSaveFileStr, end), getBookID(tempSaveFileStr, end));
			} catch (Exception e) {
				break;
			}
			// trims file
			tempSaveFileStr = tempSaveFileStr.substring(tempSaveFileStr.indexOf("%", end) + 1);
			// sees if there are any other instances left, else breaks
			if (!tempSaveFileStr.contains("/~"))
				break;
		}
		// makes new array the size of the actual number of saved games and
		// populates
		SavedGame[] savedGameListReal = new SavedGame[index];
		for (int i = 0; i < index; i++)
			savedGameListReal[i] = savedGameList[i];

		return savedGameListReal;
	}

	private int getBookID(String tempSaveFileStr, int end) {
		int start = tempSaveFileStr.indexOf("{BOOK ID}", end) + 9;
		int finish = tempSaveFileStr.indexOf("{BOOK ID}", start);
		return Integer.parseInt(tempSaveFileStr.substring(start, finish).trim());
	}

	public String mutlichecker(String address, int fileID) {
		// Set filepath based on fileID
		String path = "";
		switch (fileID) {
		case -1:
			path = "Game Files/Multichecker/Mind.mao";
			break;
		case CommonVar.BIR:
			path = "Game Files/Multichecker/Urugh.mao";
			break;
		case CommonVar.EID:
			path = "Game Files/Multichecker/Kabu.mao";
			break;
		case CommonVar.RIK:
			path = "Game Files/Multichecker/Kavu.mao";
			break;
		case CommonVar.THE:
			path = "Game Files/Multichecker/Jer.mao";
			break;
		case CommonVar.TOR:
			path = "Game Files/Multichecker/Enclave.mao";
			break;
		case CommonVar.WUL:
			path = "Game Files/Multichecker/Thiuda.mao";
			break;
		}

		// open relevant file
		String checkerFile = Gdx.files.internal(path).readString();

		// trim to specific address
		checkerFile = checkerFile.substring(checkerFile.indexOf("{" + address + "}"),
				checkerFile.lastIndexOf("{" + address + "}"));

		// check events and decide on new address
		return getNewAddress(checkerFile);
	}

	// a copy to use when there are no |-1 within a subgroup like {a}{a}
	private String subsectionAdd, subsectionAddSection;

	private String getNewAddress(String addressSection) {
		String eventToCheck = "";
		String address = "";

		while (addressSection.contains("|")) {
			eventToCheck = addressSection.substring(addressSection.indexOf("|") + 1, addressSection.indexOf(":"))
					.trim();
			address = addressSection.substring(addressSection.indexOf(":") + 1, addressSection.indexOf(",")).trim();

			if (address.length() == 1) {
				subsectionAdd = address;
				subsectionAddSection = addressSection;
			}

			// checks for default case
			if (eventToCheck.contains("-1")) {
				// if event asks to be reset, e.g., entering from caverns to
				// Dawn Fortress Archives
				if (eventToCheck.contains("#"))
					conscientia.getConscVar().triggeredEvents
							.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

				if (address.contains("!"))
					return address;
				else
					addressSection = addressSection.substring(addressSection.indexOf("{" + address + "}"),
							addressSection.lastIndexOf("{" + address + "}"));
				// ^ = &&, $ = ||, * = !
			} else if (eventToCheck.contains("^") || eventToCheck.contains("$")) {
				String symbol = (eventToCheck.contains("^")) ? "^" : "$";
				ArrayList<String> events = new ArrayList<String>();
				int startInd = 0;
				// parse list of events to check
				while (true) {
					if (eventToCheck.length() < 2)
						break;
					else {
						startInd = eventToCheck.indexOf(symbol) + 1;
						events.add(eventToCheck.substring(startInd, eventToCheck.indexOf(symbol, startInd)));
						eventToCheck = eventToCheck.substring(eventToCheck.indexOf(symbol, startInd));
					}
				}
				// check events
				boolean allTrue = (symbol.equals("^")) ? true : false;
				for (String event : events) {
					// if event asks to be reset, e.g., entering from caverns to
					// Dawn Fortress Archives
					if (eventToCheck.contains("#"))
						conscientia.getConscVar().triggeredEvents
								.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

					if (event.contains("*")) {
						if (symbol.equals("^") && conscientia.getConscVar().triggeredEvents
								.get(Integer.parseInt(event.substring(1)))) {
							allTrue = false;
							break; // one false in all ands is an auto stop
						} else if (!conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(event.substring(1))))
							allTrue = true;
					} else {
						if (symbol.equals("^")
								&& !conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(event))) {
							allTrue = false;
							break; // one false in all ands is an auto stop
						} else if (conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(event)))
							allTrue = true;
					}
				}

				if (allTrue)
					return returnCode(address, addressSection);
				else {
					if (address.contains("!"))
						addressSection = addressSection.substring(addressSection.indexOf(",") + 1);
					else
						addressSection = addressSection.substring(addressSection.lastIndexOf("{" + address + "}"));
				}
			} else if (eventToCheck.contains("*")) {
				if (!conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(eventToCheck.substring(1)))) {

					// if event asks to be reset, e.g., entering from caverns to
					// Dawn Fortress Archives
					if (eventToCheck.contains("#"))
						conscientia.getConscVar().triggeredEvents
								.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

					// if there are nested if, thens, recursively deals with it
					return returnCode(address, addressSection);
				} else {
					if (address.contains("!"))
						addressSection = addressSection.substring(addressSection.indexOf(",") + 1);
					else
						addressSection = addressSection.substring(addressSection.lastIndexOf("{" + address + "}"));
				}
			} // if event asks to be reset, e.g., entering from caverns to Dawn
				// Fortress Archives
			else if (eventToCheck.contains("#")) {
				if (conscientia.getConscVar().triggeredEvents
						.get(Integer.parseInt(eventToCheck.substring(0, eventToCheck.indexOf("#"))))) {
					conscientia.getConscVar().triggeredEvents
							.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

					return returnCode(address, addressSection);
				} else {
					if (address.contains("!"))
						addressSection = addressSection.substring(addressSection.indexOf(",") + 1);
					else
						addressSection = addressSection.substring(addressSection.lastIndexOf("{" + address + "}"));
				}
			} else if (conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(eventToCheck))) {
				return returnCode(address, addressSection);
			} else {
				// if no nested if, thens cuts to the next event, else skips
				// nest
				if (address.contains("!"))
					addressSection = addressSection.substring(addressSection.indexOf(",") + 1);
				else
					addressSection = addressSection.substring(addressSection.lastIndexOf("{" + address + "}"));
			}
		}
		// used to return null, but would crash the game
		// this happened because it would fail when entering subsections like
		// {a} when there is no |-1 condition is present,
		// e.g. ARK'S BEACON!0.X000!DESCRIPTION!
		return getNewAddress(
				subsectionAddSection.substring(subsectionAddSection.lastIndexOf("{" + subsectionAdd + "}")));
	}

	private String returnCode(String address, String checkerFile) {
		// if there are nested if, thens, recursively deals with it
		if (!address.contains("!"))
			return getNewAddress(checkerFile.substring(checkerFile.indexOf("{" + address + "}"),
					checkerFile.lastIndexOf("{" + address + "}")));
		else
			return address;
	}

	public void setBook(int bookNum) {
		loadFile(SAVE_FILE, false);
		loadFile(NPC_FILE, false);
		loadFile(UNI_FILE, false);

		// resets all non-persistent triggered events upon changing books for a
		// 'fresh' start
		// +3 is so that it doesn't run out of String before it runs out of
		// commas
		String tempEventList = uniSaveFile
				.substring(uniSaveFile.indexOf("[/EVE]") + 6, uniSaveFile.indexOf("[EVE/]") + 3).trim();
		ArrayList<Integer> eventsUniSaveFile = new ArrayList<Integer>();
		while (tempEventList.contains(",")) {
			eventsUniSaveFile.add(Integer.parseInt(tempEventList.substring(0, tempEventList.indexOf(","))));
			// trims list to next event
			tempEventList = tempEventList.substring(tempEventList.indexOf(",") + 1);
		}

		// check the two against each other and the big list of persistents
		for (int event : CommonVar.persistentEvents) {
			if (conscientia.getConscVar().triggeredEvents.get(event) && !eventsUniSaveFile.contains(event))
				eventsUniSaveFile.add(event);
			else if (!conscientia.getConscVar().triggeredEvents.get(event) && eventsUniSaveFile.contains(event))
				conscientia.getConscVar().triggeredEvents.put(event, true);
		}

		// resets
		for (Integer event : conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
			if (!eventsUniSaveFile.contains(event))
				conscientia.getConscVar().triggeredEvents.put(event, false);

		// changes current book ID
		int startInd = currentSavedGameFile.indexOf("{BOOK ID}");
		int endInd = currentSavedGameFile.lastIndexOf("{BOOK ID}");
		currentSavedGameFile = currentSavedGameFile.substring(0, startInd) + "{BOOK ID}" + bookNum
				+ currentSavedGameFile.substring(endInd);

		// removes volatile Glyphs: Awareness, discipline, farcasting, wulfias
		if (mgScr.getPlayer().getItemsAcquired().contains(Acquirable.AWARENESS_GLYPH))
			mgScr.getPlayer().getItemsAcquired()
					.remove(mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.AWARENESS_GLYPH));
		else if (mgScr.getPlayer().getItemsAcquired().contains(Acquirable.DISCIPLINE_GLYPH))
			mgScr.getPlayer().getItemsAcquired()
					.remove(mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.DISCIPLINE_GLYPH));
		else if (mgScr.getPlayer().getItemsAcquired().contains(Acquirable.FARCASTING_GLYPH)) {
			mgScr.getPlayer().getItemsAcquired()
					.remove(mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.FARCASTING_GLYPH));
			// can only get Wulfias if have farcasting
			if (mgScr.getPlayer().getItemsAcquired().contains(Acquirable.WULFIAS_GLYPH))
				mgScr.getPlayer().getItemsAcquired()
						.remove(mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.WULFIAS_GLYPH));
		}

		// add relevant starting events/acq for a given book
		switch (bookNum) {
		case CommonVar.BIR:
			break;
		case CommonVar.EID:
			// must be here or else when changing books, Glyph Menu will not
			// load
			mgScr.mgVar.hasGlyphs = true;
			// events
			String tempEventListBoE = uniSaveFile
					.substring(uniSaveFile.indexOf("[/EVE_E]") + 8, uniSaveFile.indexOf("[EVE_E/]") + 3).trim();
			ArrayList<Integer> eventsUniSaveFileBoE = new ArrayList<Integer>();
			while (tempEventListBoE.contains(",")) {
				eventsUniSaveFileBoE
						.add(Integer.parseInt(tempEventListBoE.substring(0, tempEventListBoE.indexOf(","))));
				// trims list to next event
				tempEventListBoE = tempEventListBoE.substring(tempEventListBoE.indexOf(",") + 1);
			}

			for (Integer event : conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
				if (eventsUniSaveFileBoE.contains(event))
					conscientia.getConscVar().triggeredEvents.put(event, true);

			// acq
			String tempAcqListBoE = uniSaveFile
					.substring(uniSaveFile.indexOf("[/ACQ_E]") + 8, uniSaveFile.indexOf("[ACQ_E/]") + 3).trim();
			ArrayList<Integer> acqUniSaveFileBoE = new ArrayList<Integer>();
			while (tempAcqListBoE.contains(",")) {
				acqUniSaveFileBoE.add(Integer.parseInt(
						tempAcqListBoE.substring(tempAcqListBoE.indexOf("|") + 1, tempAcqListBoE.indexOf(","))));
				// trims list to next event
				tempAcqListBoE = tempAcqListBoE.substring(tempAcqListBoE.indexOf(",") + 1);
			}

			for (int acq : acqUniSaveFileBoE)
				if (!mgScr.getPlayer().getItemsAcquired().contains(acq))
					mgScr.getPlayer().getItemsAcquired().add(acq);
			break;
		case CommonVar.RIK:
			break;
		case CommonVar.THE:
			break;
		case CommonVar.TOR:
			// enables map feature
			conscientia.getConscVar().triggeredEvents.put(0, true);
			mgScr.mgVar.hasMaps = true;
			break;
		case CommonVar.WUL:
			break;
		}

		try {
			// resets all NPC addresses
			// adds 0 in front of current game num if less than 10
			String gameSaveNum = (conscientia.getConscVar().currentSavedGameNum < 10)
					? "0" + conscientia.getConscVar().currentSavedGameNum
					: "" + conscientia.getConscVar().currentSavedGameNum;

			NPCFile = "%" + gameSaveNum + "/~" + Gdx.files.internal("Game Files/NPCs.mao").readString() + "~/"
					+ gameSaveNum;

			// TODO should I be updating acquirables here too?
			// updates triggered events
			startInd = currentSavedGameFile.indexOf("{TRIGGERED EVENTS}");
			endInd = currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
			String tempEventsList = "";
			for (Integer event : conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
				tempEventsList += "|" + event + ":" + conscientia.getConscVar().triggeredEvents.get(event) + ",";
			currentSavedGameFile = currentSavedGameFile.substring(0, startInd) + "{TRIGGERED EVENTS}" + tempEventsList
					+ currentSavedGameFile.substring(endInd);

			// updates uni save file
			String tempUniEventsList = "";
			for (Integer event : eventsUniSaveFile)
				tempUniEventsList += event + ",";
			uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/EVE]") + 6) + tempUniEventsList
					+ uniSaveFile.substring(uniSaveFile.indexOf("[EVE/]"));

			writeToFile(SAVE_FILE);
			writeToFile(NPC_FILE);
			writeToFile(UNI_FILE);
		} finally {
			// just means the player is starting up a new game or beginning a
			// new game as a result of story termination in a previous book
			writeToFile(SAVE_FILE);
			writeToFile(NPC_FILE);
			writeToFile(UNI_FILE);
		}
	}

	// used to update old save files with new triggered events
	// useful when writing new books
	public void writeNewEvents() {
		// populates list of most recent list of triggered events and their
		// values
		ArrayList<Integer> updatedEventsList = new ArrayList<Integer>();
		String mostRecentEventsList = Gdx.files.internal("Game Files/DefaultSavedGame.mao").readString();
		mostRecentEventsList = mostRecentEventsList.substring(mostRecentEventsList.indexOf("{TRIGGERED EVENTS}"),
				mostRecentEventsList.lastIndexOf("{TRIGGERED EVENTS}"));
		while (mostRecentEventsList.contains(":")) {
			updatedEventsList.add(Integer.parseInt(mostRecentEventsList.substring(mostRecentEventsList.indexOf("|") + 1,
					mostRecentEventsList.indexOf(":"))));
			mostRecentEventsList = mostRecentEventsList.substring(mostRecentEventsList.indexOf(",") + 1);
		}

		// current saved game
		String tempEventList = currentSavedGameFile.substring(currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));
		HashMap<Integer, Boolean> eventsCurrentSaveFile = new HashMap<Integer, Boolean>();
		while (tempEventList.contains("|")) {
			eventsCurrentSaveFile.put(
					Integer.parseInt(
							tempEventList.substring(tempEventList.indexOf("|") + 1, tempEventList.indexOf(":"))),
					Boolean.parseBoolean(
							tempEventList.substring(tempEventList.indexOf(":") + 1, tempEventList.indexOf(","))));
			// trims list to next event
			tempEventList = tempEventList.substring(tempEventList.indexOf(",") + 1);
		}
		// copies to current save game files list of events
		for (Integer i : updatedEventsList)
			if (!eventsCurrentSaveFile.keySet().contains(i))
				eventsCurrentSaveFile.put(i, false);
		// rewrite the files
		String triggeredEventList = "";
		for (int event : eventsCurrentSaveFile.keySet())
			triggeredEventList += "|" + event + ":" + eventsCurrentSaveFile.get(event) + ",";

		int startInd = currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18;
		int endInd = currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
		currentSavedGameFile = currentSavedGameFile.substring(0, startInd) + triggeredEventList
				+ currentSavedGameFile.substring(endInd);
		// WRITE THE FILES
		writeToFile(SAVE_FILE);
	}

	// resets an address that doesn't exist to a default address
	public String resetFaultyAddress(String npc, String currentLocation) {
		FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
		String address = file.readString();

		// trim to relevant NPC
		address = address.substring(address.indexOf("[/" + npc), address.indexOf(npc + "/]"));
		// trim to relevant loc and address
		return address.substring(address.indexOf(":", address.indexOf(currentLocation)) + 1,
				address.indexOf(",", address.indexOf(currentLocation)));
	}

	// deletes a save file from the load menu
	public void deleteSelectedSaveFile(SavedGame selected) {
		// delete save
		FileHandle file = Gdx.files.local("SG/genericSG.mao");

		// adds 0 in front of current game num if less than 10
		String saveNum = (selected.getSavedGameNum() < 10) ? ("0" + selected.getSavedGameNum())
				: ("" + selected.getSavedGameNum());
		// indexes for current saved game portion
		SBStartInd = file.readString().indexOf("%" + saveNum);
		SBEndInd = file.readString().indexOf(saveNum + "%") + (saveNum.length() + 1);

		// if last file, index will be out of bounds
		try {
			file.writeString(file.readString().substring(0, SBStartInd) + file.readString().substring(SBEndInd), false);
		} catch (Exception e) {
			file.writeString(file.readString().substring(0, SBStartInd), false);
		}

		// delete npc file
		file = Gdx.files.local("SG/NPCs.mao");

		// indexes for current saved game portion
		SBStartInd = file.readString().indexOf("%" + saveNum);
		SBEndInd = file.readString().indexOf(saveNum + "%") + (saveNum.length() + 1);

		// if last file, index will be out of bounds
		try {
			file.writeString(file.readString().substring(0, SBStartInd) + file.readString().substring(SBEndInd), false);
		} catch (Exception e) {
			// STILL THROWS AN ERROR!!!
			file.writeString(file.readString().substring(0, SBStartInd), false);
		}
	}

	public void setUseAltFont(boolean useAltFont) {
		loadFile(UNI_FILE, false);
		uniSaveFile = uniSaveFile.substring(0, uniSaveFile.indexOf("[/FONT]") + 7) + ((useAltFont) ? "1" : "0")
				+ uniSaveFile.substring(uniSaveFile.indexOf("[FONT/]"));
		writeToFile(UNI_FILE);
	}

	public ArrayList<Location> getMapLocation(int bookID, String areaName) {
		ArrayList<Location> list = new ArrayList<Location>();
		FileHandle file;
		String mapString;
		try {
			file = Gdx.files.local("SG/Maps.mao");
			mapString = file.readString();
		} catch (Exception e) {
			file = Gdx.files.internal("Game Files/Maps.mao");
			mapString = file.readString();
		}

		// trim to relevant map list
		mapString = mapString.substring(mapString.indexOf("[/" + bookID + ']'), mapString.indexOf("[" + bookID + "/]"));
		mapString = mapString.substring(mapString.indexOf("{" + areaName + "}"),
				mapString.lastIndexOf("{" + areaName + "}"));
		// populate list
		ArrayList<String> locations = new ArrayList<String>();
		while (true) {
			try {
				locations.add(mapString.substring(mapString.indexOf("(") + 1, mapString.indexOf(")")));
				mapString = mapString.substring(mapString.indexOf(")") + 1);
			} catch (Exception e) {
				break;
			}
		}
		for (String loc : locations) {
			String locName = loc.substring(0, loc.indexOf(","));
			loc = loc.substring(loc.indexOf(",") + 1);
			int ID = Integer.parseInt(loc.substring(0, loc.indexOf(",")));
			loc = loc.substring(loc.indexOf(",") + 1);
			boolean isDisplayed = Boolean.parseBoolean(loc.substring(0, loc.indexOf(",")));
			loc = loc.substring(loc.indexOf(",") + 1);
			int sizeX = Integer.parseInt(loc.substring(0, loc.indexOf(",")));
			loc = loc.substring(loc.indexOf(",") + 1);
			int sizeY = Integer.parseInt(loc.substring(0, loc.indexOf(",")));
			loc = loc.substring(loc.indexOf(",") + 1);
			int coordX = Integer.parseInt(loc.substring(0, loc.indexOf(",")));
			loc = loc.substring(2);
			int coordY = Integer.parseInt(loc);
			if (isDisplayed)
				list.add(new Location(locName, ID, isDisplayed, new int[] { sizeX, sizeY },
						new int[] { coordX, coordY }));
		}

		return list;
	}

	public void addMapLocation(int bookID, String broaderAreaName, String areaName) {
		FileHandle file;
		String mapString;
		try {
			file = Gdx.files.local("SG/Maps.mao");
			mapString = file.readString();
		} catch (Exception e) {
			// creates new file
			file = Gdx.files.internal("Game Files/Maps.mao");
			mapString = file.readString();
			file = Gdx.files.local("SG/Maps.mao");
			file.writeString(mapString, false);
		}

		// trim to relevant book
		int start = mapString.indexOf("[/" + bookID + ']');
		// see if broader area is included
		start = mapString.indexOf("(" + broaderAreaName, start);
		start = mapString.indexOf(",", mapString.indexOf(",", start) + 1) + 1;
		int end = mapString.indexOf(",", start);
		if (!Boolean.parseBoolean(mapString.substring(start, end)))
			mapString = mapString.substring(0, start) + "true" + mapString.substring(end);
		// isolate specific area
		start = mapString.indexOf("{" + broaderAreaName + "}", start);
		start = mapString.indexOf("(" + areaName, start);
		start = mapString.indexOf(",", mapString.indexOf(",", start) + 1) + 1;
		end = mapString.indexOf(",", start);
		if (!Boolean.parseBoolean(mapString.substring(start, end)))
			mapString = mapString.substring(0, start) + "true" + mapString.substring(end);

		// writes updated maplist to file
		file.writeString(mapString, false);
	}
}