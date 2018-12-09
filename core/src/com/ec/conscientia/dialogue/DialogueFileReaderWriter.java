package com.ec.conscientia.dialogue;

import com.badlogic.gdx.Gdx;
import com.ec.conscientia.entities.Address;
import com.ec.conscientia.entities.Response;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;

public class DialogueFileReaderWriter {

	private String dialogueFileContents;
	private String currentDialogueFile;
	private MainGameScreen mgScr;
	private FileIOManager fileRW;

	/*
	 * For test suite
	 */
	public DialogueFileReaderWriter() {
		this.fileRW = new FileIOManager();
	}

	/*
	 * For actual game
	 */
	public DialogueFileReaderWriter(MainGameScreen mgScr) {
		this.mgScr = mgScr;
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);
	}

	public void setDialogueFile(String currentAddress) {
		String tempLoc1 = "";
		if (addressExists(currentAddress)) {
			dialogueFileContents = Gdx.files.internal("Game Files/Dialogue/DialogueMaster.mao").readString();

			// if the address is null, it just resets it to the default address
			// for the area
			try {
				tempLoc1 = currentAddress.substring(0, currentAddress.indexOf('!'));

				// trims file to relevant major location
				dialogueFileContents = dialogueFileContents.substring(dialogueFileContents.indexOf(tempLoc1),
						dialogueFileContents.lastIndexOf(tempLoc1));

				// determines sublocation (e.g., MAGE'S ABODE, etc.)
				String tempLoc2 = currentAddress.substring(currentAddress.indexOf('!') + 1);
				tempLoc1 = tempLoc2.substring(0, tempLoc2.indexOf('!'));

				// trims file to relevant sublocation
				dialogueFileContents = dialogueFileContents.substring(
						dialogueFileContents.indexOf("/" + tempLoc1 + "]"),
						dialogueFileContents.lastIndexOf("[" + tempLoc1 + "/"));

				currentDialogueFile = dialogueFileContents.substring(dialogueFileContents.indexOf('|') + 1,
						dialogueFileContents.lastIndexOf('|'));

				// opens target file
				dialogueFileContents = Gdx.files.internal("Game Files/Dialogue/" + currentDialogueFile + ".mao")
						.readString();
			} catch (Exception e) {
				String resetAddress = "";
				for (String npc : mgScr.mgVar.NPCbyNum.keySet())
					if (mgScr.mgVar.NPCbyNum.get(npc) == mgScr.getCurrentNPC())
						resetAddress = fileRW.writer.resetFaultyAddress(npc, mgScr.getCurrentLocation());
				mgScr.getDialogue().setCurrentAddress(resetAddress);
				setDialogueFile(resetAddress);
			}
		} else {
			for (String npc : mgScr.mgVar.NPCbyNum.keySet())
				if (mgScr.mgVar.NPCbyNum.get(npc) == mgScr.getCurrentNPC())
					tempLoc1 = fileRW.writer.resetFaultyAddress(npc, mgScr.getCurrentLocation());
			mgScr.getDialogue().setCurrentAddress(tempLoc1);
			setDialogueFile(tempLoc1);
		}
	}

	private boolean addressExists(String currentAddress) {
		dialogueFileContents = Gdx.files.internal("Game Files/Dialogue/DialogueMaster.mao").readString();

		String tempLoc1 = "";
		try {

			tempLoc1 = currentAddress.substring(0, currentAddress.indexOf('!'));

			// trims file to relevant major location
			dialogueFileContents = dialogueFileContents.substring(dialogueFileContents.indexOf(tempLoc1),
					dialogueFileContents.lastIndexOf(tempLoc1));

			// determines sublocation (e.g., MAGE'S ABODE, etc.)
			String tempLoc2 = currentAddress.substring(currentAddress.indexOf('!') + 1);
			tempLoc1 = tempLoc2.substring(0, tempLoc2.indexOf('!'));

			// trims file to relevant sublocation
			dialogueFileContents = dialogueFileContents.substring(dialogueFileContents.indexOf("/" + tempLoc1 + "]"),
					dialogueFileContents.lastIndexOf("[" + tempLoc1 + "/"));

			currentDialogueFile = dialogueFileContents.substring(dialogueFileContents.indexOf('|') + 1,
					dialogueFileContents.lastIndexOf('|'));

			// opens target file
			dialogueFileContents = Gdx.files.internal("Game Files/Dialogue/" + currentDialogueFile + ".mao")
					.readString();

			return (dialogueFileContents.contains("[/" + currentAddress)
					&& dialogueFileContents.contains(currentAddress + "/]"));
		} catch (Exception e) {
			return false;
		}
	}

	public String getNpcDialogue(String currentAddress) {
		// trims to relevant dialogue address
		String tempDialogue = null;

		if (dialogueFileContents.contains("[/" + currentAddress)
				&& dialogueFileContents.contains(currentAddress + "/]")) {
			tempDialogue = dialogueFileContents.substring(dialogueFileContents.indexOf("[/" + currentAddress),
					dialogueFileContents.lastIndexOf(currentAddress + "/]"));

			// trims to only NPC dialogue
			tempDialogue = tempDialogue.substring(tempDialogue.indexOf('*') + 1, tempDialogue.lastIndexOf('*'));
		}

		return tempDialogue;
	}

	public Response[] getResponses(String currentAddress) {
		// sets them to null initially, so that we can check to see if it exists
		// when ordering the responses
		Response[] responses = new Response[] { null, null, null, null, null, null };

		// trims to relevant dialogue address
		String tempResponses = dialogueFileContents.substring(dialogueFileContents.indexOf("[/" + currentAddress),
				dialogueFileContents.lastIndexOf(currentAddress + "/]"));

		// adds Player responses to responses array
		// =2 is to account for letter and #
		for (int index = 0; index < responses.length; index++) {
			if (tempResponses.contains("A#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("A#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("A#"))));
				responses[index].setPersonalityType(Response.DIPLOMAT);
			} else if (tempResponses.contains("B#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("B#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("B#"))));
				responses[index].setPersonalityType(Response.TRUTHSEEKER);
			} else if (tempResponses.contains("C#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("C#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("C#"))));
				responses[index].setPersonalityType(Response.NEUTRAL);
			} else if (tempResponses.contains("D#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("D#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("D#"))));
				responses[index].setPersonalityType(Response.SURVIVALIST);
			} else if (tempResponses.contains("E#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("E#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("E#"))));
				responses[index].setPersonalityType(Response.TYRANT);
			} else if (tempResponses.contains("F#")) {
				responses[index] = new Response();

				responses[index].setResponse(tempResponses.substring(tempResponses.indexOf("F#") + 2,
						tempResponses.indexOf(')', tempResponses.indexOf("F#"))));
				responses[index].setPersonalityType(Response.LOON);
			} else
				responses[index] = null;

			tempResponses = tempResponses.substring(tempResponses.indexOf("}") + 1);
		}

		return responses;
	}

	// returns addresses && personality points
	public Address[] getPotentialAddresses(Response[] responsesOrdered, String currentAddress) {
		Address[] addresses = new Address[6];

		// trims to relevant dialogue address
		String tempAddresses = dialogueFileContents.substring(dialogueFileContents.indexOf("[/" + currentAddress),
				dialogueFileContents.lastIndexOf(currentAddress + "/]"));
		tempAddresses = tempAddresses.substring(tempAddresses.indexOf("("));

		// +1 is to account for the curly bracket
		if (responsesOrdered[0] != null) {
			addresses[0] = new Address();

			addresses[0].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[0].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[0].getResponse()))));

			addresses[0].setRank(Address.FIRST);
		} else
			addresses[0] = null;
		if (responsesOrdered[1] != null) {
			addresses[1] = new Address();

			addresses[1].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[1].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[1].getResponse()))));

			addresses[1].setRank(Address.SECOND);
		} else
			addresses[1] = null;
		if (responsesOrdered[2] != null) {
			addresses[2] = new Address();

			addresses[2].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[2].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[2].getResponse()))));

			addresses[2].setRank(Address.THIRD);
		} else
			addresses[2] = null;
		if (responsesOrdered[3] != null) {
			addresses[3] = new Address();

			addresses[3].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[3].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[3].getResponse()))));

			addresses[3].setRank(Address.FOURTH);
		} else
			addresses[3] = null;
		if (responsesOrdered[4] != null) {
			addresses[4] = new Address();

			addresses[4].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[4].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[4].getResponse()))));

			addresses[4].setRank(Address.FIFTH);
		} else
			addresses[4] = null;
		if (responsesOrdered[5] != null) {
			addresses[5] = new Address();

			addresses[5].setAddress(tempAddresses.substring(
					tempAddresses.indexOf("{", tempAddresses.indexOf("#" + responsesOrdered[5].getResponse())) + 1,
					tempAddresses.indexOf("}", tempAddresses.indexOf("#" + responsesOrdered[5].getResponse()))));

			addresses[5].setRank(Address.SIXTH);
		} else
			addresses[5] = null;

		return addresses;
	}

	public String[] checkForEvent(String currentAddress) {
		String[] tempList = new String[2];

		// trims to relevant dialogue address
		String tempEvent = dialogueFileContents.substring(dialogueFileContents.indexOf("[/" + currentAddress),
				dialogueFileContents.lastIndexOf(currentAddress + "/]"));

		tempEvent = tempEvent.substring(tempEvent.indexOf('|') + 1, tempEvent.lastIndexOf('|'));

		if (tempEvent.contains(":")) {
			// parses out the index of the event that will be changed to true
			tempList[0] = tempEvent.substring(0, tempEvent.indexOf(':'));
			// parses out the new address you will be taken to
			tempList[1] = tempEvent.substring(tempEvent.indexOf(':') + 1);

			return tempList;
		} else
			return null;
	}

	public boolean validAddress(String targetNewAddress) {
		try {
			// can find file
			setDialogueFile(targetNewAddress);

			// can find address itself
			if (getNpcDialogue(targetNewAddress) == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			// these are still valid addresses, theoretically
			// For game endings, multicheckers, and fights
			if (targetNewAddress.contains("!END!") || targetNewAddress.contains("NO ADDRESS")
					|| targetNewAddress.contains("FIGHT") || targetNewAddress.equals("a")
					|| targetNewAddress.equals("b") || targetNewAddress.equals("c") || targetNewAddress.equals("d")
					|| targetNewAddress.equals("e") || targetNewAddress.equals("f"))
				return true;
			else
				return false;
		}
	}
}
