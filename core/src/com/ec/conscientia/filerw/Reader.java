package com.ec.conscientia.filerw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ec.conscientia.entities.Book;
import com.ec.conscientia.entities.Glyph;
import com.ec.conscientia.entities.Location;
import com.ec.conscientia.entities.Log;
import com.ec.conscientia.entities.MindscapeNPC;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.entities.SavedGame;
import com.ec.conscientia.variables.CommonVar;

public class Reader {
	private FileIOManager fileMan;
	private int SBStartInd, SBEndInd;

	public Reader(FileIOManager fileMan) {
		this.fileMan = fileMan;
	}

	public String loadSplashQuote() {
		// loads bookOfBiracul file
		FileHandle file = Gdx.files.internal("Game Files/BookOfBiracul.mao");
		String bookStr = file.readString();

		Random rand = new Random();
		int quoteInd = rand.nextInt(CommonVar.NUM_BIRACULIAN_VERSES);

		String quoteStr = bookStr.substring(bookStr.indexOf("[/" + quoteInd), bookStr.indexOf(quoteInd + "/]"));
		quoteStr = quoteStr.substring(quoteStr.indexOf("\""), quoteStr.length() - 2);
		return quoteStr;
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
		fileMan.mgScr.loadingUtils.nullError("NPC_BY_NUM: " + NPCnum);
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

	public String loadCredits() {
		// loads credits file
		FileHandle file = Gdx.files.internal("Game Files/Credits.mao");
		String creditsStr = file.readString();

		return creditsStr;
	}

	void setStartAdd(int bookID) {
		// set starting address for given book
		loadFile(CommonVar.UNI_FILE, false);

		String firstPart = fileMan.currentSavedGameFile.substring(0, fileMan.currentSavedGameFile.indexOf(":") + 1);
		int startAddInd = fileMan.uniSaveFile.indexOf("|" + bookID + ":") + 3;
		String address = fileMan.uniSaveFile.substring(startAddInd, fileMan.uniSaveFile.indexOf(",", startAddInd));

		String lastPart = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(","));
		fileMan.currentSavedGameFile = firstPart + address + lastPart;
	}

	// READ METHODS
	public HashMap<Integer, Boolean> loadTriggeredEvents() {
		// loads save file
		loadFile(CommonVar.SAVE_FILE, false);

		HashMap<Integer, Boolean> tEvents = new HashMap<Integer, Boolean>();
		String tempStr = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));

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
		for (int event : fileMan.mgScr.getPersistentEvents())
			if (tEvents.get(event)) {
				fileMan.mgScr.setAwareness(fileMan.mgScr.getAwareness() + 1);
				fileMan.conscientia.getConscVar().persistentItemsAndEvents.add(event);
			}

		return tEvents;
	}

	public HashMap<String, String> getDialogueAddressesMap(String ID) {
		HashMap<String, String> tempMap = new HashMap<String, String>();

		String tempStr = fileMan.NPCFile.substring(fileMan.NPCFile.indexOf("[/" + ID + "]"),
				fileMan.NPCFile.indexOf("[" + ID + "/]"));
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
	public String[] getNPCsStats(String id, boolean testing) {
		String[] tempArray = new String[NPC.STATS_ARRAY_LENGTH];

		// loads NPC save file
		if (!testing)
			loadFile(CommonVar.NPC_FILE, false);
		// for testing
		else {
			FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
			fileMan.NPCFile = file.readString();
		}

		try {
			// trims to relevant NPC as determined by id
			String tempStats = fileMan.NPCFile.substring(fileMan.NPCFile.indexOf("[/" + id + "]"),
					fileMan.NPCFile.indexOf("[" + id + "/]"));
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
			fileMan.mgScr.loadingUtils.nullError("NPC_STATS: " + id);
		}

		return tempArray;
	}

	public void addNPC(String id) {
		FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
		String addNPC = file.readString();
		// must have, or else the final ']' gets overwritten
		String NPCentry = addNPC.substring(addNPC.indexOf("[/" + id), addNPC.indexOf("/]", addNPC.indexOf(id))) + "/] ";

		fileMan.NPCFile = fileMan.NPCFile.substring(0, fileMan.NPCFile.indexOf("[/DESCRIPTION]") - 1) + NPCentry
				+ fileMan.NPCFile.substring(fileMan.NPCFile.indexOf("[/DESCRIPTION]"));
	}

	public int[] getNPCsCombatStats(String id, boolean testing) {
		ArrayList<Integer> tempArray = new ArrayList<Integer>();

		// loads NPC save file
		if (!testing)
			loadFile(CommonVar.NPC_FILE, false);
		// for testing
		else {
			FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
			fileMan.NPCFile = file.readString();
		}

		// trims to relevant NPC as determined by id
		// TODO crashes, throwing NullPointerException
		// id? NPCFile?
		String tempStats = fileMan.NPCFile.substring(fileMan.NPCFile.indexOf("[/" + id + "]"),
				fileMan.NPCFile.indexOf("[" + id + "/]"));
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

	public HashMap<String, String> getNPCsDialogueAdds(String id, boolean testing) {
		HashMap<String, String> tempMap = new HashMap<String, String>();

		// loads NPC save file
		if (!testing)
			loadFile(CommonVar.NPC_FILE, false);
		// for testing
		else {
			FileHandle file = Gdx.files.internal("Game Files/NPCs.mao");
			fileMan.NPCFile = file.readString();
		}

		// trims to relevant NPC as determined by id
		String tempAdds = fileMan.NPCFile.substring(fileMan.NPCFile.indexOf("[/" + id + "]"),
				fileMan.NPCFile.indexOf("[" + id + "/]"));
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
		loadFile(CommonVar.SAVE_FILE, false);

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
		if (fileMan.mgScr.getCurrentLocation() == null)
			fileMan.mgScr.setCurrentLocation(fileMan.currentSavedGameFile.substring(
					fileMan.currentSavedGameFile.indexOf("currentLocation:") + 16, fileMan.currentSavedGameFile
							.indexOf(',', fileMan.currentSavedGameFile.indexOf("currentLocation:"))));
	}

	private void loadPersonalityAffinity() {
		// loads personality stats
		fileMan.mgScr.getPlayer()
				.setDiplomat(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("A:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("A:")))));
		fileMan.mgScr.getPlayer()
				.setTruthseeker(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("B:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("B:")))));
		fileMan.mgScr.getPlayer()
				.setNeutral(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("C:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("C:")))));
		fileMan.mgScr.getPlayer()
				.setSurvivalist(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("D:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("D:")))));
		fileMan.mgScr.getPlayer()
				.setTyrant(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("E:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("E:")))));
		fileMan.mgScr.getPlayer()
				.setLoon(Integer.parseInt(fileMan.currentSavedGameFile.substring(
						fileMan.currentSavedGameFile.indexOf("F:") + 2,
						fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("F:")))));
	}

	private void loadItemsAcquired() {
		// loads glyphs, abilities and logs
		loadFile(CommonVar.SAVE_FILE, false);
		String tempItemList = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}"),
				fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		while (tempItemList.contains("|")) {
			fileMan.mgScr.getPlayer().getItemsAcquired().add(
					Integer.parseInt(tempItemList.substring(tempItemList.indexOf("|") + 1, tempItemList.indexOf(","))));
			// trims list to next glyph
			tempItemList = tempItemList.substring(tempItemList.indexOf(",") + 1);
		}

		for (Integer i : CommonVar.persistentAcquirables)
			if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(i))
				fileMan.conscientia.getConscVar().persistentItemsAndEvents.add(i);
	}

	private void loadAwareness() {
		// loads glyphs, abilities and logs
		loadFile(CommonVar.SAVE_FILE, false);
		String awareness = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.indexOf("{AWARENESS}") + 11,
						fileMan.currentSavedGameFile.lastIndexOf("{AWARENESS}"))
				.trim();
		fileMan.mgScr.setAwareness(Integer.parseInt(awareness));
	}

	public boolean checkBookListSpecific(int bookID) {
		loadFile(CommonVar.UNI_FILE, false);

		int startInd = 0, endInd = 0;

		switch (bookID) {
		case CommonVar.BIR:
			startInd = fileMan.uniSaveFile.indexOf("|B:") + 3;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("VIRACOCHA"))
				return true;
			else
				return false;
		case CommonVar.EID:
			startInd = fileMan.uniSaveFile.indexOf("|E:") + 3;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("SINGULARITY"))
				return true;
			else
				return false;
		case CommonVar.RIK:
			startInd = fileMan.uniSaveFile.indexOf("|R:") + 3;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("ARKSBANE"))
				return true;
			else
				return false;
		case CommonVar.THE:
			startInd = fileMan.uniSaveFile.indexOf("|Th:") + 4;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("DEATHSLAYER"))
				return true;
			else
				return false;
		case CommonVar.TOR:
			startInd = fileMan.uniSaveFile.indexOf("|T:") + 3;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("NON-PROPHET"))
				return true;
			else
				return false;
		case CommonVar.WUL:
			startInd = fileMan.uniSaveFile.indexOf("|W:") + 3;
			endInd = fileMan.uniSaveFile.indexOf(",", startInd);
			if (fileMan.uniSaveFile.substring(startInd, endInd).equals("BEAST OF THIUDA"))
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

	private String getLocation(String tempFile, int end) {
		String location = "";
		// parse out location
		location = tempFile.substring(tempFile.indexOf(":", end) + 1, tempFile.indexOf(",", end));
		location = location.substring(location.indexOf("!") + 1);
		location = location.substring(0, location.indexOf("!")) + " - "
				+ location.substring(location.indexOf("!") + 1, location.length() - 1);

		return location;
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
					fileMan.conscientia.getConscVar().triggeredEvents
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
						fileMan.conscientia.getConscVar().triggeredEvents
								.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

					if (event.contains("*")) {
						if (symbol.equals("^") && fileMan.conscientia.getConscVar().triggeredEvents
								.get(Integer.parseInt(event.substring(1)))) {
							allTrue = false;
							break; // one false in all ands is an auto stop
						} else if (!fileMan.conscientia.getConscVar().triggeredEvents
								.get(Integer.parseInt(event.substring(1))))
							allTrue = true;
					} else {
						if (symbol.equals("^")
								&& !fileMan.conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(event))) {
							allTrue = false;
							break; // one false in all ands is an auto stop
						} else if (fileMan.conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(event)))
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
				if (!fileMan.conscientia.getConscVar().triggeredEvents
						.get(Integer.parseInt(eventToCheck.substring(1)))) {

					// if event asks to be reset, e.g., entering from caverns to
					// Dawn Fortress Archives
					if (eventToCheck.contains("#"))
						fileMan.conscientia.getConscVar().triggeredEvents
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
				if (fileMan.conscientia.getConscVar().triggeredEvents
						.get(Integer.parseInt(eventToCheck.substring(0, eventToCheck.indexOf("#"))))) {
					fileMan.conscientia.getConscVar().triggeredEvents
							.put(Integer.parseInt(eventToCheck.substring(eventToCheck.indexOf("#") + 1)), false);

					return returnCode(address, addressSection);
				} else {
					if (address.contains("!"))
						addressSection = addressSection.substring(addressSection.indexOf(",") + 1);
					else
						addressSection = addressSection.substring(addressSection.lastIndexOf("{" + address + "}"));
				}
			} else if (fileMan.conscientia.getConscVar().triggeredEvents.get(Integer.parseInt(eventToCheck))) {
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

	public int getCurrentNPC() {
		// loads save file
		loadFile(CommonVar.SAVE_FILE, false);

		int startInd = fileMan.currentSavedGameFile.indexOf("{CURRENT NPC}") + 13;
		int endInd = fileMan.currentSavedGameFile.indexOf("{CURRENT NPC}", startInd);

		int currentNpc = Integer.parseInt(fileMan.currentSavedGameFile.substring(startInd, endInd).trim());

		return currentNpc;
	}

	public void loadSavedGameFiles() {
		// loads save file
		loadFile(CommonVar.SAVE_FILE, true);

		// loads NPC save file
		loadFile(CommonVar.NPC_FILE, true);

		// sets font preference
		loadFile(CommonVar.UNI_FILE, false);
		try {
			fileMan.conscientia.setUseAltFont(fileMan.uniSaveFile
					.substring(fileMan.uniSaveFile.indexOf("[/FONT]") + 7, fileMan.uniSaveFile.indexOf("[FONT/]"))
					.equals("1"));
		} catch (Exception e) {
			fileMan.conscientia.setUseAltFont(false);
			fileMan.uniSaveFile += "[/FONT]0[FONT/]";
			fileMan.writer.writeToFile(CommonVar.UNI_FILE);
		}
	}

	public String loadCurrentLocationFromSavedGameFiles() {
		// loads save file
		loadFile(CommonVar.SAVE_FILE, false);

		String currentLocation = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("currentLocation:") + 16,
				fileMan.currentSavedGameFile.indexOf(","));

		return currentLocation;
	}

	public String[] loadMindscapeStuff() {
		// loads save file
		loadFile(CommonVar.SAVE_FILE, false);

		String relevantArea = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{MINDSCAPE}") + 11,
				fileMan.currentSavedGameFile.lastIndexOf("{MINDSCAPE}"));

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

	/*
	 * MAPS
	 */
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

	/*
	 * SUB MENUS
	 */
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

	public void loadFile(int fileType, boolean updatePersistents) {
		FileHandle file;
		String lessThanTen;
		try {
			switch (fileType) {
			case CommonVar.SAVE_FILE:
				// OPENS TARGET SAVE FILE
				file = Gdx.files.local("SG/genericSG.mao");

				// adds 0 in front of current game num if less than 10
				lessThanTen = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";
				// assigns indexes for current saved game portion
				SBStartInd = file.readString()
						.indexOf("%" + lessThanTen + fileMan.conscientia.getConscVar().currentSavedGameNum);
				SBEndInd = file.readString()
						.indexOf(lessThanTen + fileMan.conscientia.getConscVar().currentSavedGameNum + "%")
						+ (lessThanTen.length() + 1);

				fileMan.currentSavedGameFile = file.readString().substring(SBStartInd, SBEndInd);

				if (updatePersistents) {
					// checks to see if more triggered events have been added in
					// the defaultSave file upon loading a saved game
					fileMan.writer.writeNewEvents();
					fileMan.writer.updatePersistents();
				}
				break;
			case CommonVar.NPC_FILE:
				file = Gdx.files.local("SG/NPCs.mao");

				// adds 0 in front of current game num if less than 10
				lessThanTen = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10)
						? ("0" + fileMan.conscientia.getConscVar().currentSavedGameNum)
						: ("" + fileMan.conscientia.getConscVar().currentSavedGameNum);

				// indexes for current saved game portion
				SBStartInd = file.readString().indexOf("%" + lessThanTen);
				SBEndInd = file.readString().indexOf(lessThanTen + "%") + (lessThanTen.length() + 1);

				fileMan.NPCFile = file.readString().substring(SBStartInd, SBEndInd);

				// add anomaly if not present
				String[] addedPostHoc = { "THE ANOMALY", "ARKARA" };
				for (String name : addedPostHoc) {
					if (!fileMan.NPCFile.contains("[/" + name + "]")) {
						addNPC(name);
						// Needs to write to file here, or else it fucks up the
						// save file
						SBStartInd = file.readString().indexOf("%" + lessThanTen);
						SBEndInd = file.readString().indexOf(lessThanTen + "%") + (lessThanTen.length() + 1);
						// writes to file
						file.writeString(file.readString().substring(0, SBStartInd) + fileMan.NPCFile
								+ file.readString().substring(SBEndInd), false);
					}
				}
				break;
			case CommonVar.UNI_FILE:
				file = Gdx.files.local("SG/UniSave.mao");

				fileMan.uniSaveFile = file.readString();
				break;
			}
		} catch (Exception e) {
			fileMan.mgScr.loadingUtils.nullError("LOADING_FILE: "
					+ fileMan.conscientia.getConscVar().currentSavedGameNum + " | FILE TYPE: " + fileType);
		}
	}

}
