package com.ec.conscientia.filerw;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ec.conscientia.entities.Acquirable;
import com.ec.conscientia.entities.SavedGame;
import com.ec.conscientia.variables.CommonVar;

public class Writer {
	private FileIOManager fileMan;
	private int SBStartInd, SBEndInd;

	public Writer(FileIOManager fileMan) {
		this.fileMan = fileMan;
	}

	/*
	 * GAME SAVING
	 */
	public void gameSave() {
		// loads save file
		fileMan.reader.loadFile(CommonVar.SAVE_FILE, true);

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

		writeToFile(CommonVar.SAVE_FILE);

		savePersistents();
	}

	public void updateCurrentLocation() {
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("currentLocation:") + 16);
		String secondHalf = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf(',', fileMan.currentSavedGameFile.indexOf("currentLocation:"))
						+ 1);

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getCurrentLocation() + ',' + secondHalf;
	}

	public void updateCurrentNPC() {
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{CURRENT NPC}") + 13);
		String secondHalf = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.lastIndexOf("{CURRENT NPC}"));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getCurrentNPC() + secondHalf;
	}

	public void updateAcquirables() {
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}") + 12);
		String secondHalf = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));

		// has eidos glyph & extraction glyph automatically if in book of Eidos
		String itemList = "";
		for (Integer i : fileMan.mgScr.getPlayer().getItemsAcquired())
			// here to avoid duplicate entries
			// TODO, find out why there are duplicate entries
			if (!itemList.contains("|" + i + ","))
				itemList += "|" + i + ",";

		fileMan.currentSavedGameFile = firstHalf + itemList + secondHalf;
	}

	public void updateAwareness() {
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{AWARENESS}") + 11);
		String secondHalf = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.lastIndexOf("{AWARENESS}"));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getAwareness() + secondHalf;
	}

	public void updatePlayerStats() {
		// sets Diplomat's stats
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("A:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		String secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("A:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getDiplomat() + secondHalf;

		// sets Truthseeker's stats
		firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("B:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("B:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getTruthseeker() + secondHalf;

		// sets Neutral's stats
		firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("C:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("C:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));
		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getNeutral() + secondHalf;

		// sets Survivalist's stats
		firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("D:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("D:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getSurvivalist() + secondHalf;

		// sets Tyrant's stats
		firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("E:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("E:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getTyrant() + secondHalf;

		// sets Loon's stats
		firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("F:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}")) + 2);
		secondHalf = fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(',',
				fileMan.currentSavedGameFile.indexOf("F:", fileMan.currentSavedGameFile.indexOf("{PERSONALITY}"))));

		fileMan.currentSavedGameFile = firstHalf + fileMan.mgScr.getPlayer().getLoon() + secondHalf;
	}

	public void updateTriggeredEvents() {
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18);
		String secondHalf = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));

		for (Integer event : fileMan.conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
			firstHalf += "|" + event + ":" + fileMan.conscientia.getConscVar().triggeredEvents.get(event) + ",";

		fileMan.currentSavedGameFile = firstHalf + secondHalf;
	}

	public void savePersistents() {
		String itemList = "", eventsList = "";

		for (Integer i : CommonVar.persistentAcquirables)
			if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(i))
				itemList += "|" + i + ",";

		for (Integer i : CommonVar.persistentEvents)
			if (fileMan.conscientia.getConscVar().triggeredEvents.get(i))
				eventsList += i + ",";

		fileMan.reader.loadFile(CommonVar.UNI_FILE, false);

		fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/ACQ_UNI]") + 10)
				+ itemList + fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[ACQ_UNI/]"));

		fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/EVE]") + 6) + eventsList
				+ fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[EVE/]"));

		writeToFile(CommonVar.UNI_FILE);
	}

	
	public void saveNPCstats(String NPCname, String currentAddress, String currentLocation) {
		FileHandle file = Gdx.files.local("SG/NPCs.mao");

		// adds 0 in front of current game num if less than 10
		String lessThanTen = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";

		// find relevant NPC & relevant location
		int npcInd = file.readString()
				.indexOf("%" + lessThanTen + fileMan.conscientia.getConscVar().currentSavedGameNum);
		npcInd = file.readString().indexOf(":",
				file.readString().indexOf(currentLocation+":", file.readString().indexOf("[/" + NPCname, npcInd))) + 1; //must be currentLocation+":" or else leads to nasty overwrite errors post some combat encounters

		// writes to file
		file.writeString(file.readString().substring(0, npcInd) + currentAddress
				+ file.readString().substring(file.readString().indexOf(",", npcInd)), false);
	}

	public void mindscapeSave() {
		// loads save file
		fileMan.reader.loadFile(CommonVar.SAVE_FILE, false);
		// UPDATES PRE-MINDSCAPE LOCATION & NPC
		String firstHalf = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{MINDSCAPE}") + 11);
		String secondHalf = fileMan.currentSavedGameFile
				.substring(fileMan.currentSavedGameFile.lastIndexOf("{MINDSCAPE}"));

		fileMan.currentSavedGameFile = firstHalf + "|" + fileMan.mgScr.mgVar.lastAddBeforeMindEntry + ":"
				+ fileMan.mgScr.getCurrentNPC() + "," + secondHalf;

		writeToFile(CommonVar.SAVE_FILE);
	}

	public void rewriteMostRecentEvents(int eventNum) {
		int[] indexes = new int[2];

		indexes[0] = (fileMan.currentSavedGameFile.indexOf("|" + eventNum + ":") == -1) ? -1
				: fileMan.currentSavedGameFile.indexOf("|" + eventNum + ":") + 7;
		indexes[1] = fileMan.currentSavedGameFile.indexOf(",", indexes[0]);

		String firstHalf = fileMan.currentSavedGameFile.substring(0, indexes[0]);
		String secondHalf = fileMan.currentSavedGameFile.substring(indexes[1]);

		fileMan.currentSavedGameFile = firstHalf + "true" + secondHalf;

		// write to save file
		writeToFile(CommonVar.SAVE_FILE);
	}

	public void actBook(int book) {
		int startInd = 0, endInd = 0;

		if (Gdx.files.local("SG/UniSave.mao").exists()) {
			fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
			switch (book) {
			case CommonVar.BIR:
				startInd = fileMan.uniSaveFile.indexOf("|B:") + 3;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "VIRACOCHA"
							+ fileMan.uniSaveFile.substring(endInd);
					startInd = fileMan.uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "2000,"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			case CommonVar.EID:
				startInd = fileMan.uniSaveFile.indexOf("|E:") + 3;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "SINGULARITY"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			case CommonVar.RIK:
				startInd = fileMan.uniSaveFile.indexOf("|R:") + 3;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "ARKSBANE"
							+ fileMan.uniSaveFile.substring(endInd);
					startInd = fileMan.uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "2001,"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			case CommonVar.THE:
				startInd = fileMan.uniSaveFile.indexOf("|Th:") + 4;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "DEATHSLAYER"
							+ fileMan.uniSaveFile.substring(endInd);
					startInd = fileMan.uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "2002,"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			case CommonVar.TOR:
				startInd = fileMan.uniSaveFile.indexOf("|T:") + 3;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "NON-PROPHET"
							+ fileMan.uniSaveFile.substring(endInd);
					startInd = fileMan.uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "2003,"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			case CommonVar.WUL:
				startInd = fileMan.uniSaveFile.indexOf("|W:") + 3;
				endInd = fileMan.uniSaveFile.indexOf(",", startInd);
				if (fileMan.uniSaveFile.substring(startInd, endInd).equals("?")) {
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "BEAST OF THIUDA"
							+ fileMan.uniSaveFile.substring(endInd);
					startInd = fileMan.uniSaveFile.indexOf("[/EVE]") + 6;
					endInd = startInd;
					fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, startInd) + "2004,"
							+ fileMan.uniSaveFile.substring(endInd);

					writeToFile(CommonVar.UNI_FILE);
				}
				break;
			}
		} else {
			// writes universal save file
			fileMan.uniSaveFile = Gdx.files.internal("Game Files/UniSave.mao").readString();
			Gdx.files.local("SG/UniSave.mao").writeString(fileMan.uniSaveFile, false);
			actBook(book);
		}
	}

	public void setBook(int bookNum) {
		fileMan.reader.loadFile(CommonVar.SAVE_FILE, false);
		fileMan.reader.loadFile(CommonVar.NPC_FILE, false);
		fileMan.reader.loadFile(CommonVar.UNI_FILE, false);

		// resets all non-persistent triggered events upon changing books for a
		// 'fresh' start
		// +3 is so that it doesn't run out of String before it runs out of
		// commas
		String tempEventList = fileMan.uniSaveFile
				.substring(fileMan.uniSaveFile.indexOf("[/EVE]") + 6, fileMan.uniSaveFile.indexOf("[EVE/]") + 3).trim();
		ArrayList<Integer> eventsUniSaveFile = new ArrayList<Integer>();
		while (tempEventList.contains(",")) {
			eventsUniSaveFile.add(Integer.parseInt(tempEventList.substring(0, tempEventList.indexOf(","))));
			// trims list to next event
			tempEventList = tempEventList.substring(tempEventList.indexOf(",") + 1);
		}

		// check the two against each other and the big list of persistents
		for (int event : CommonVar.persistentEvents) {
			if (fileMan.conscientia.getConscVar().triggeredEvents.get(event) && !eventsUniSaveFile.contains(event))
				eventsUniSaveFile.add(event);
			else if (!fileMan.conscientia.getConscVar().triggeredEvents.get(event) && eventsUniSaveFile.contains(event))
				fileMan.conscientia.getConscVar().triggeredEvents.put(event, true);
		}

		// resets
		for (Integer event : fileMan.conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
			if (!eventsUniSaveFile.contains(event))
				fileMan.conscientia.getConscVar().triggeredEvents.put(event, false);

		// changes current book ID
		int startInd = fileMan.currentSavedGameFile.indexOf("{BOOK ID}");
		int endInd = fileMan.currentSavedGameFile.lastIndexOf("{BOOK ID}");
		fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, startInd) + "{BOOK ID}" + bookNum
				+ fileMan.currentSavedGameFile.substring(endInd);

		// removes volatile Glyphs: Awareness, discipline, farcasting, wulfias
		if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(Acquirable.AWARENESS_GLYPH))
			fileMan.mgScr.getPlayer().getItemsAcquired()
					.remove(fileMan.mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.AWARENESS_GLYPH));
		else if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(Acquirable.DISCIPLINE_GLYPH))
			fileMan.mgScr.getPlayer().getItemsAcquired()
					.remove(fileMan.mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.DISCIPLINE_GLYPH));
		else if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(Acquirable.FARCASTING_GLYPH)) {
			fileMan.mgScr.getPlayer().getItemsAcquired()
					.remove(fileMan.mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.FARCASTING_GLYPH));
			// can only get Wulfias if have farcasting
			if (fileMan.mgScr.getPlayer().getItemsAcquired().contains(Acquirable.WULFIAS_GLYPH))
				fileMan.mgScr.getPlayer().getItemsAcquired()
						.remove(fileMan.mgScr.getPlayer().getItemsAcquired().indexOf(Acquirable.WULFIAS_GLYPH));
		}

		// add relevant starting events/acq for a given book
		switch (bookNum) {
		case CommonVar.BIR:
			break;
		case CommonVar.EID:
			// must be here or else when changing books, Glyph Menu will not
			// load
			fileMan.mgScr.mgVar.hasGlyphs = true;
			// events
			String tempEventListBoE = fileMan.uniSaveFile
					.substring(fileMan.uniSaveFile.indexOf("[/EVE_E]") + 8, fileMan.uniSaveFile.indexOf("[EVE_E/]") + 3)
					.trim();
			ArrayList<Integer> eventsUniSaveFileBoE = new ArrayList<Integer>();
			while (tempEventListBoE.contains(",")) {
				eventsUniSaveFileBoE
						.add(Integer.parseInt(tempEventListBoE.substring(0, tempEventListBoE.indexOf(","))));
				// trims list to next event
				tempEventListBoE = tempEventListBoE.substring(tempEventListBoE.indexOf(",") + 1);
			}

			for (Integer event : fileMan.conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
				if (eventsUniSaveFileBoE.contains(event))
					fileMan.conscientia.getConscVar().triggeredEvents.put(event, true);

			// acq
			String tempAcqListBoE = fileMan.uniSaveFile
					.substring(fileMan.uniSaveFile.indexOf("[/ACQ_E]") + 8, fileMan.uniSaveFile.indexOf("[ACQ_E/]") + 3)
					.trim();
			ArrayList<Integer> acqUniSaveFileBoE = new ArrayList<Integer>();
			while (tempAcqListBoE.contains(",")) {
				acqUniSaveFileBoE.add(Integer.parseInt(
						tempAcqListBoE.substring(tempAcqListBoE.indexOf("|") + 1, tempAcqListBoE.indexOf(","))));
				// trims list to next event
				tempAcqListBoE = tempAcqListBoE.substring(tempAcqListBoE.indexOf(",") + 1);
			}

			for (int acq : acqUniSaveFileBoE)
				if (!fileMan.mgScr.getPlayer().getItemsAcquired().contains(acq))
					fileMan.mgScr.getPlayer().getItemsAcquired().add(acq);
			break;
		case CommonVar.RIK:
			break;
		case CommonVar.THE:
			break;
		case CommonVar.TOR:
			// enables map feature
			fileMan.conscientia.getConscVar().triggeredEvents.put(0, true);
			fileMan.mgScr.mgVar.hasMaps = true;
			break;
		case CommonVar.WUL:
			break;
		}

		try {
			// resets all NPC addresses
			// adds 0 in front of current game num if less than 10
			String gameSaveNum = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10)
					? "0" + fileMan.conscientia.getConscVar().currentSavedGameNum
					: "" + fileMan.conscientia.getConscVar().currentSavedGameNum;

			fileMan.NPCFile = "%" + gameSaveNum + "/~" + Gdx.files.internal("Game Files/NPCs.mao").readString() + "~/"
					+ gameSaveNum;

			// TODO should I be updating acquirables here too?
			// updates triggered events
			startInd = fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}");
			endInd = fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
			String tempEventsList = "";
			for (Integer event : fileMan.conscientia.getConscVar().triggeredEvents.triggeredEvents.keySet())
				tempEventsList += "|" + event + ":" + fileMan.conscientia.getConscVar().triggeredEvents.get(event)
						+ ",";
			fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, startInd) + "{TRIGGERED EVENTS}"
					+ tempEventsList + fileMan.currentSavedGameFile.substring(endInd);

			// updates uni save file
			String tempUniEventsList = "";
			for (Integer event : eventsUniSaveFile)
				tempUniEventsList += event + ",";
			fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/EVE]") + 6)
					+ tempUniEventsList + fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[EVE/]"));

			writeToFile(CommonVar.SAVE_FILE);
			writeToFile(CommonVar.NPC_FILE);
			writeToFile(CommonVar.UNI_FILE);
		} finally {
			// just means the player is starting up a new game or beginning a
			// new game as a result of story termination in a previous book
			writeToFile(CommonVar.SAVE_FILE);
			writeToFile(CommonVar.NPC_FILE);
			writeToFile(CommonVar.UNI_FILE);
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
		String tempEventList = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));
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

		int startInd = fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18;
		int endInd = fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
		fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, startInd) + triggeredEventList
				+ fileMan.currentSavedGameFile.substring(endInd);
		// WRITE THE FILES
		writeToFile(CommonVar.SAVE_FILE);
	}
	/*
	 * GAME SAVING
	 */

	void updatePersistents() {
		// ACQUIRABLES
		// current save file
		String tempItemList = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}"),
				fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		ArrayList<Integer> acqItemsCurrentSaveFile = new ArrayList<Integer>();
		while (tempItemList.contains("|")) {
			acqItemsCurrentSaveFile.add(
					Integer.parseInt(tempItemList.substring(tempItemList.indexOf("|") + 1, tempItemList.indexOf(","))));
			// trims list to next glyph
			tempItemList = tempItemList.substring(tempItemList.indexOf(",") + 1);
		}
		// universal save list
		fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
		tempItemList = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/ACQ_UNI]"),
				fileMan.uniSaveFile.indexOf("[ACQ_UNI/]"));
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

		fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}") + 12) + tempItemList
				+ fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));

		tempItemList = "";
		for (int i : acqItemsUniSaveFile)
			tempItemList += "|" + i + ",";
		fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/ACQ_UNI]") + 10)
				+ tempItemList + fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[ACQ_UNI/]"));

		// EVENTS
		// current saved game
		String tempEventList = fileMan.currentSavedGameFile.substring(
				fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"),
				fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}"));
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
		tempEventList = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/EVE]") + 6,
				fileMan.uniSaveFile.indexOf("[EVE/]") + 3);
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

		int startInd = fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}") + 18;
		int endInd = fileMan.currentSavedGameFile.lastIndexOf("{TRIGGERED EVENTS}");
		fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, startInd) + triggeredEventList
				+ fileMan.currentSavedGameFile.substring(endInd);

		tempEventList = "";
		for (int i : eventsUniSaveFile)
			tempEventList += i + ",";
		fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/EVE]") + 6)
				+ tempEventList + fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[EVE/]"));

		// WRITE THE FILES
		writeToFile(CommonVar.SAVE_FILE);
		writeToFile(CommonVar.UNI_FILE);
	}

	// Updates persistent events to universal file
	private void addEvents(int bookID, boolean virgin) {
		fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
		try {
			fileMan.conscientia.setUseAltFont(fileMan.uniSaveFile
					.substring(fileMan.uniSaveFile.indexOf("[/FONT]") + 7, fileMan.uniSaveFile.indexOf("[FONT/]"))
					.equals("1"));
		} catch (Exception e) {
			fileMan.conscientia.setUseAltFont(false);
			fileMan.uniSaveFile += "[/FONT]0[FONT/]";
			writeToFile(CommonVar.UNI_FILE);
		}
		if (virgin && bookID == CommonVar.EID) {
			// need try catch because otherwise new games fail
			try {
				String events = fileMan.uniSaveFile
						.substring(fileMan.uniSaveFile.indexOf("[/EVE_E]") + 8, fileMan.uniSaveFile.indexOf("[EVE_E/]"))
						.trim();
				while (events.length() > 1) {
					// trims initial ,
					if (events.substring(0, 1).equals(","))
						events = events.substring(1);
					String specificEvent = events.substring(0, events.indexOf(","));
					int start = fileMan.currentSavedGameFile.indexOf("|" + specificEvent + ":") + specificEvent.length()
							+ 2;
					fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, start) + "true"
							+ fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(",", start));
					// next event
					events = events.substring(events.indexOf(","));
				}
			} catch (Exception e) {
				fileMan.uniSaveFile += "[/EVE_E]2050,[EVE_E/]";
				writeToFile(CommonVar.UNI_FILE);
			}
		} else if (virgin && bookID == CommonVar.TOR) {
			// sets map feature as true
			fileMan.mgScr.mgVar.hasMaps = true;
		}

		String persistentEvents = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/EVE]") + 6,
				fileMan.uniSaveFile.indexOf("[EVE/]"));

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
				start = fileMan.currentSavedGameFile.indexOf(event + ":") + event.length() + 1;
				fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0, start) + "true"
						+ fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf(",", start));
			}
	}

	// Updates persistent acquirables to universal file
	private void addAcq(int bookID, boolean virgin) {
		if (virgin && bookID == CommonVar.EID) {
			fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
			String acqs = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/ACQ_E]") + 8,
					fileMan.uniSaveFile.indexOf("[ACQ_E/]"));
			fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
					fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}")) + "{ACQUIRABLE}" + acqs
					+ fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
		} else if (!virgin) {
			fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
			String acqs = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/ACQ_UNI]") + 10,
					fileMan.uniSaveFile.indexOf("[ACQ_UNI/]"));

			if (bookID == CommonVar.EID && acqs.length() == 0) {
				acqs = fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[/ACQ_E]") + 8,
						fileMan.uniSaveFile.indexOf("[ACQ_E/]"));
				fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
						fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}")) + "{ACQUIRABLE}" + acqs
						+ fileMan.currentSavedGameFile
								.substring(fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
			} else
				fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
						fileMan.currentSavedGameFile.indexOf("{ACQUIRABLE}")) + "{ACQUIRABLE}" + acqs
						+ fileMan.currentSavedGameFile
								.substring(fileMan.currentSavedGameFile.lastIndexOf("{ACQUIRABLE}"));
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
				fileMan.conscientia.getConscVar().currentSavedGameNum = newNum;
				// checks to see if it's greater than 10, otherwise adds a 0 in
				// front of single digit
				String nextNumStr = (newNum < 10) ? "0" + newNum : "" + newNum;
				fileMan.currentSavedGameFile = "%" + nextNumStr + "/~"
						+ Gdx.files.internal("Game Files/DefaultSavedGame.mao").readString() + "~/" + nextNumStr + "%";
				// set bookID
				fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
						fileMan.currentSavedGameFile.lastIndexOf("{PERSONALITY}") + 13) + "{BOOK ID}" + bookID
						+ "{BOOK ID}" + fileMan.currentSavedGameFile
								.substring(fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"));
				// sets start address for selected book
				fileMan.reader.setStartAdd(bookID);
				// sets persistent acquirables
				addAcq(bookID, false);
				// sets persistent events
				addEvents(bookID, true);
				// appends new saved game to saved file
				Gdx.files.local("SG/genericSG.mao").writeString(fileMan.currentSavedGameFile, true);
				// NPC FILE
				fileMan.NPCFile = "%" + nextNumStr + "/~" + Gdx.files.internal("Game Files/NPCs.mao").readString()
						+ "~/" + nextNumStr + "%";
				Gdx.files.local("SG/NPCs.mao").writeString(fileMan.NPCFile, true);
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
		fileMan.conscientia.getConscVar().currentSavedGameNum = 0;
		fileMan.currentSavedGameFile = "%00/~" + Gdx.files.internal("Game Files/DefaultSavedGame.mao").readString()
				+ "~/00%";
		// set bookID
		fileMan.currentSavedGameFile = fileMan.currentSavedGameFile.substring(0,
				fileMan.currentSavedGameFile.lastIndexOf("{PERSONALITY}") + 13) + "{BOOK ID}" + bookID + "{BOOK ID}"
				+ fileMan.currentSavedGameFile.substring(fileMan.currentSavedGameFile.indexOf("{TRIGGERED EVENTS}"));
		// sets start address for selected book
		fileMan.reader.setStartAdd(bookID);
		// if Eidos, then add in acquirables specific to her
		addAcq(bookID, true);
		// sets persistent events
		addEvents(bookID, true);
		// creates new saved game to saved file
		Gdx.files.local("SG/genericSG.mao").writeString(fileMan.currentSavedGameFile, false);

		// NPC FILE
		fileMan.NPCFile = "%00/~" + Gdx.files.internal("Game Files/NPCs.mao").readString() + "~/00%";
		Gdx.files.local("SG/NPCs.mao").writeString(fileMan.NPCFile, false);

		// MAPS FILE
		FileHandle file;
		file = Gdx.files.internal("Game Files/Maps.mao");
		String mapString = file.readString();
		file = Gdx.files.local("SG/Maps.mao");
		// writes to file
		file.writeString(mapString, false);
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
		fileMan.reader.loadFile(CommonVar.UNI_FILE, false);
		fileMan.uniSaveFile = fileMan.uniSaveFile.substring(0, fileMan.uniSaveFile.indexOf("[/FONT]") + 7)
				+ ((useAltFont) ? "1" : "0") + fileMan.uniSaveFile.substring(fileMan.uniSaveFile.indexOf("[FONT/]"));
		writeToFile(CommonVar.UNI_FILE);
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

	/*
	 * MAPS
	 */
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
		// TODO crashes, throwing IndexOutOfBoundsException
		// start < 0 || end > mapString.length || end < start
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
	/*
	 * MAPS
	 */

	/*
	 * WRITE TO FILES
	 */
	public void writeToFile(int fileType) {
		FileHandle file;
		String lessThanTen;

		switch (fileType) {
		case CommonVar.SAVE_FILE:
			file = Gdx.files.local("SG/genericSG.mao");

			// adds 0 in front of current game num if less than 10
			lessThanTen = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10) ? "0" : "";
			// indexes for current saved game portion
			SBStartInd = file.readString()
					.indexOf("%" + lessThanTen + fileMan.conscientia.getConscVar().currentSavedGameNum);
			SBEndInd = file.readString()
					.indexOf(lessThanTen + fileMan.conscientia.getConscVar().currentSavedGameNum + "%")
					+ (lessThanTen.length() + 1);

			// writes to file
			file.writeString(file.readString().substring(0, SBStartInd) + fileMan.currentSavedGameFile
					+ file.readString().substring(SBEndInd), false);
			break;
		case CommonVar.NPC_FILE:
			file = Gdx.files.local("SG/NPCs.mao");

			// adds 0 in front of current game num if less than 10
			lessThanTen = (fileMan.conscientia.getConscVar().currentSavedGameNum < 10)
					? ("0" + fileMan.conscientia.getConscVar().currentSavedGameNum)
					: ("" + fileMan.conscientia.getConscVar().currentSavedGameNum);

			// indexes for current saved game portion
			SBStartInd = file.readString().indexOf("%" + lessThanTen);
			SBEndInd = file.readString().indexOf(lessThanTen + "%");

			// Needs to be here to avoid the exponential increase in size of NPC
			// file
			fileMan.NPCFile = file.readString().substring(SBStartInd, SBEndInd);

			// writes to file
			file.writeString(file.readString().substring(0, SBStartInd) + fileMan.NPCFile
					+ file.readString().substring(SBEndInd), false);
			break;
		case CommonVar.UNI_FILE:
			file = Gdx.files.local("SG/UniSave.mao");

			// writes to file
			file.writeString(fileMan.uniSaveFile, false);
			break;
		}
	}
}