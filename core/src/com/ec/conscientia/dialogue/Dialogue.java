package com.ec.conscientia.dialogue;

import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Address;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.entities.Response;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class Dialogue {

	private NPC currentNpc;
	private int switchedToNPC;
	private DialogueFileReaderWriter dialogueFileRW;
	private FileIOManager fileRW;
	private Address[] addresses;

	private Response[] responses, responsesOrdered;

	// holds the current NPC dialogue to be displayed & the current address
	private String currentNpcDialogue, currentAddress, oldAddress;

	// tracks which of the responses the pointer is on
	private int pointerIndex;

	// index is for personality type, value is for rank
	private int[] currentAffinity;

	private MainGameScreen mgScr;

	public Dialogue(MainGameScreen mgScr) {
		this.mgScr = mgScr;
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);
		this.dialogueFileRW = new DialogueFileReaderWriter(mgScr);
	}

	// booleans for room switching and address forcing
	private boolean roomSwitch = false, dialogueForceEnacted = false;

	// UPDATE METHODS: BEGIN //////////
	public void update() {
		// FOR DEBUGGING
		// System.out.println("currentAddress: " + currentAddress);

		if (currentAddress == null || currentAddress.contains("COMBAT FAILURE")) {
			mgScr.loadingUtils.nullError("DIALOGUE_UPDATE: " + currentAddress);
		} else {
			// For nasty fenrir error where it randomly goes to the Hydragyrum
			// Keep from meditation
			if (currentAddress.contains("HYDRAGYRUM KEEP"))
				if (mgScr.getConscientia().getConscVar().bookID != CommonVar.TOR)
					currentAddress = "MIND!MINDSCAPE!GRAYLANDS!0.X000!DESCRIPTION!";

			if (validAddress(currentAddress)) {
				// check once for unique events
				mgScr.mgVar.uniqueEvent.checkForUniqueEvents();

				// check to see if major area has changed, if yes end
				// dialogue and change area in Main class
				if (!getCurrentLocAddress(3).equals(getCurrentMainLocAddress(3))) {
					// updates locations visited
					fileRW.addMapLocation(mgScr.getConscientia().getConscVar().bookID,
							getBroaderAreaName(getCurrentLocAddress(3)), getAreaName(getCurrentLocAddress(3)));
					// resets fade speed
					mgScr.setFadeSpeed(0);

					// checks to see where to change to
					if (mgScr.getCues().get("DEATH").contains(currentAddress)
							|| mgScr.getCues().get("DRAUG END").contains(currentAddress)) {
						mgScr.getSoundManager().playSFX(SoundManager.SFX_SPAWNING_SOUND);
						mgScr.scrFX.resetWhiteIn();
						if (!overrideSwitchedToNPC)
							switchedToNPC = mgScr.mgVar.NPCbyNum.get("DESCRIPTION");
						roomSwitch = true;
						endDialogue();
					} else if (mgScr.getCues().get("MINDSCAPE").contains(currentAddress)) {
						mgScr.setGameState(mgScr.SCREEN_EFFECT_CHANGE_ROOM);
						switchedToNPC = lastNPC;
						roomSwitch = true;
						switchFromMindscape = true;
						mgScr.setIsMindscape(false);
					} else {
						mgScr.setGameState(mgScr.SCREEN_EFFECT_CHANGE_ROOM);
						// when changing rooms, it does an auto NPC switch
						// to description in next room
						if (!overrideSwitchedToNPC)
							switchedToNPC = mgScr.mgVar.NPCbyNum.get("DESCRIPTION");
						roomSwitch = true;
					}
				}
				// check for triggered event if the current address has a
				// ".X" in it
				else if (currentAddress.contains(".X"))
					checkForTriggeredEvent();
				// get's new NPC dialogue and new responses
				else if (!oldAddress.equals(currentAddress)) {
					// saves new address for a NPC other than DESCRIPTION
					if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
							&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
						fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());
					// update the NPC dialogue address for current location
					updateDialogue();
				}
			}
		}
	}

	private String getAreaName(String areaName) {
		String realAreaName = areaName.substring(areaName.indexOf("!", areaName.indexOf("!") + 1) + 1);
		return realAreaName.substring(0, realAreaName.length() - 1);
	}

	private String getBroaderAreaName(String areaName) {
		String realAreaName = areaName.substring(areaName.indexOf("!") + 1);
		return realAreaName.substring(0, realAreaName.indexOf("!"));
	}

	// ensures program doesn't crash from faulty addresses
	public boolean validAddress(String targetNewAddress) {
		// otherwise the game-ending and mindscape-ending addresses get
		// rejected
		if (targetNewAddress.contains("END GAME!") || targetNewAddress.contains("END!"))
			return true;
		else
			return (dialogueFileRW.validAddress(targetNewAddress)) ? true : false;
	}

	public void checkForTriggeredEvent() {
		// check for event
		String[] event = dialogueFileRW.checkForEvent(currentAddress);

		if (event != null) {
			if (event[0].contains("^")) {
				try {
					// if null, just assumes event is false to prevent crashing
					mgScr.getConscientia().getConscVar().triggeredEvents.get(Integer.parseInt(event[0].substring(1)));

					// ^ precedes boolean to be checked
					if (mgScr.getConscientia().getConscVar().triggeredEvents
							.get(Integer.parseInt(event[0].substring(1)))) {
						currentAddress = event[1];
						// save current address as default address for given
						// area
						if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
								&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
							fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());
						update();
					} else {
						// save current address as default address for given
						// area
						if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
								&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
							fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());
						updateDialogue();
					}
				} catch (Exception e) {
					// save current address as default address for given area
					if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
							&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
						fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());
					updateDialogue();
				}
			} // used when switch between Description and NPCs in a room
			else if (event[0].contains("#npcSwitch")) {
				// set NPC to switch to as event[1]
				switchedToNPC = Integer.parseInt(event[1]);
				endDialogue();
			} else if (event[0].contains("@")) {
				// save current address as default address for given area
				fileRW.saveNPCstats(currentNpc.getName(), event[1], mgScr.getCurrentLocation());
				dialogueForceEnacted = true;
				updateDialogue();
			} else if (!event[0].equals("")) {
				mgScr.mgVar.checkItemAcquired = true;
				// sets event as true, or leaves it unchanged if it was
				// previously true
				mgScr.getConscientia().getConscVar().triggeredEvents.put(Integer.parseInt(event[0]), true);
				// sets old & current addresses to new addresses if event has
				// occurred else leaves current address unaffected
				oldAddress = currentAddress;
				currentAddress = event[1];

				// save current address as default address for given area
				if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
						&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
					fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());

				update();
			}
		} else {
			for (String address : mgScr.getCues().get("MULTICHECKER"))
				if (address.equals(currentAddress))
					mgScr.multichecker(currentAddress);
			updateDialogue();
		}
	}

	private void updateDialogue() {
		// retrieves new dialogue and responses if address has changed
		currentNpcDialogue = dialogueFileRW.getNpcDialogue(currentAddress);

		if (currentNpcDialogue == null) {
			mgScr.loadingUtils.nullError("UPDATE_DIALOGUE: " + currentAddress);
		} else {
			// used for checking affinity
			if (currentNpcDialogue.equals("~ac")) {
				// gets responses, determines affinity, orders responses
				responses = dialogueFileRW.getResponses(currentAddress);
				determineAffinity();
				orderResponses();

				// selects the first response automatically as it is already
				// ordered
				// by affinity updates currentAddress to selected address &
				// resets
				// pointerIndex

				// starts from index 4 to account for personality points and
				// comma
				currentAddress = addresses[pointerIndex = 0].getAddress().substring(4);
				update();
			} else {
				responses = dialogueFileRW.getResponses(currentAddress);
				// determines affinity and then places the appropriate responses
				// in
				// the appropriate slots
				determineAffinity();
				orderResponses();

				// changes the oldAddress to match the currentAddress
				oldAddress = currentAddress;
			}
		}
	}

	// determines which personalities you have greatest affinity to
	// index is for personality type, value is for rank
	private void determineAffinity() {
		currentAffinity = new int[6];

		// checks stats in relation to each other and puts in currentAffinity in
		// order of rank
		for (int i = 0; i < mgScr.getPlayer().getAllPersonalityStats().length; i++) {
			// sets personality to relative rank (0 being highest)
			currentAffinity[i] = findRank(i);
			for (Response resp : responses)
				if (resp != null && resp.getPersonalityTypeInt() == i)
					resp.setRank(findRank(i));
		}
	}

	// takes personality num as input and gives its affinity ranking as output
	private int findRank(int i) {
		int[] personalityStats = mgScr.getPlayer().getAllPersonalityStats();
		// starts as lowest rank
		int rank = 5;
		// if there are more affinity points for i than the diplomat, the rank
		// increases
		for (int j = 0; j < personalityStats.length; j++)
			if (i != j && personalityStats[i] >= personalityStats[j])
				if (personalityStats[i] != personalityStats[j])
					rank--;
				else if (i > j)
					rank--;

		return rank;
	}

	private void orderResponses() {
		// clears the previous list
		for (int i = 0; i < responsesOrdered.length; i++)
			responsesOrdered[i] = null;

		// index for rank in responsesOrdered array
		int ind = 0;
		// place first ranked response in ordered responses list
		for (int nextRank = 0; nextRank < currentAffinity.length; nextRank++)
			for (int i = 0; i < currentAffinity.length; i++)
				if (responses[i] != null)
					if (responses[i].getRankInt() == nextRank)
						responsesOrdered[ind++] = responses[i];

		// puts their corresponding addresses into the array in the same
		// order
		addresses = dialogueFileRW.getPotentialAddresses(responsesOrdered, currentAddress);
	}

	public DialogueFileReaderWriter getFileRW() {
		return dialogueFileRW;
	}

	public void setFileRW(DialogueFileReaderWriter fileRW) {
		this.dialogueFileRW = fileRW;
	}

	// updates the array of personality stats for the player
	public void updateStats() {
		String personality = addresses[pointerIndex].getAddress().substring(0, 1);

		if (personality.equals("A")) {
			mgScr.getPlayer().setDiplomat(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		} else if (personality.equals("B")) {
			mgScr.getPlayer().setTruthseeker(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		} else if (personality.equals("C")) {
			mgScr.getPlayer().setNeutral(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		} else if (personality.equals("D")) {
			mgScr.getPlayer().setSurvivalist(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		} else if (personality.equals("E")) {
			mgScr.getPlayer().setTyrant(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		} else if (personality.equals("F")) {
			mgScr.getPlayer().setLoon(Integer.parseInt(addresses[pointerIndex].getAddress().substring(2,
					addresses[pointerIndex].getAddress().indexOf(','))));
		}
	}

	// UPDATE METHODS: END //////////
	public void initiateDialogue(NPC npc, String address) {
		// initiates fileReaderWriter and sets relevant stats for the area
		dialogueFileRW.setDialogueFile(address);
		currentNpc = npc;
		responses = new Response[6];
		responsesOrdered = new Response[6];
		currentAddress = (address.contains("!")) ? address : getCurrentAddress();
		oldAddress = "";
		currentNpcDialogue = "\n";

		// this update is necessary, otherwise won't do the appropriate event
		// checking when going to a .X address after npcSwitching
		// DO NOT DELETE!
		update();
	}

	public void endDialogue() {
		if (!dialogueForceEnacted && !overrideSwitchedToNPC) {
			// saves address prior to npcSwitcher as last address/default
			// address, unless previously forced
			if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
				fileRW.saveNPCstats(currentNpc.getName(), oldAddress, mgScr.getCurrentLocation());
		} else if (overrideSwitchedToNPC) {
			// used when switching to the mindscape to save previous NPCs last
			// dialogue
			NPC prevNPC = new NPC(lastNPC, mgScr);
			if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
				fileRW.saveNPCstats(prevNPC.getName(), oldAddress, lastLoc);
		}

		// change location
		// and/or switch NPC
		if (switchFromMindscape)
			mgScr.setCurrentLocation(lastLoc);
		else
			mgScr.setCurrentLocation(getCurrentLocAddress(3));
		currentNpc = new NPC(switchedToNPC, mgScr);

		// sets appropriate address for newNpc given their last default address
		if (currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("DESCRIPTION")
				&& currentNpc.getIDnum() != mgScr.mgVar.NPCbyNum.get("FARCASTER"))
			currentNpc.setDialogueAddress(currentNpc.getDialogueAddress(mgScr.getCurrentLocation()),
					mgScr.getCurrentLocation());
		// checks to see if switching rooms, if so, uses the currentAddress (the
		// one JUST selected to guide the description to the proper address
		else if (roomSwitch && !switchFromMindscape) {
			fileRW.saveNPCstats(currentNpc.getName(), currentAddress, mgScr.getCurrentLocation());
			currentNpc.setDialogueAddress(currentAddress, mgScr.getCurrentLocation());
		} else if (switchFromMindscape) {
			currentNpc.setDialogueAddress(currentNpc.getDialogueAddress(lastLoc), lastLoc);
			switchFromMindscape = false;
		}

		// reset to non-existent NPC
		switchedToNPC = -1;
		// sets roomSwitch to false no matter what
		roomSwitch = false;
		// force set to false no matter what
		dialogueForceEnacted = false;
		// used for talking to characters in the mindscape
		overrideSwitchedToNPC = false;

		// sets current NPC for restarting purposes
		mgScr.setCurrentNPC(currentNpc.getIDnum());

		// targeted saves
		fileRW.loadFile(FileIOManager.SAVE_FILE, false);
		fileRW.updateCurrentNPC();
		fileRW.updateCurrentLocation();
		fileRW.updateTriggeredEvents();
		fileRW.writeToFile(FileIOManager.SAVE_FILE);

		// Ends game
		if (currentAddress.contains("END GAME!")) {
			mgScr.getSoundManager().setFadeOut(true);
			mgScr.setGameState(mgScr.END_GAME);
		} else
			// reinitiate dialogue with new address
			initiateDialogue(currentNpc, currentNpc.getDialogueAddress(mgScr.getCurrentLocation()));
	}

	// // getters and setters
	public NPC getCurrentNPC() {
		return this.currentNpc;
	}

	public Response[] getResponsesOrdered() {
		return responsesOrdered;
	}

	public void setResponsesOrdered(Response[] responsesOrdered) {
		this.responsesOrdered = responsesOrdered;
	}

	public Address[] getAddresses() {
		return addresses;
	}

	public void setAddresses(Address[] addresses) {
		this.addresses = addresses;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public int getPointerIndex() {
		return pointerIndex;
	}

	public void setPointerIndex(int pointerIndex) {
		this.pointerIndex = pointerIndex;
	}

	public String getCurrentNpcDialogue() {
		return currentNpcDialogue;
	}

	public void setCurrentNpcDialogue(String currentNpcDialogue) {
		this.currentNpcDialogue = currentNpcDialogue;
	}

	private String getCurrentLocAddress(int excalmationPoint) {
		int ind = 0;
		for (int i = 0; i < excalmationPoint; i++) {
			ind = currentAddress.indexOf("!", ind) + 1;
		}

		String newLoc = currentAddress.substring(0, ind);

		return newLoc;
	}

	private String getCurrentMainLocAddress(int excalmationPoint) {
		int ind = 0;
		for (int i = 0; i < excalmationPoint; i++) {
			ind = mgScr.getCurrentLocation().indexOf("!", ind) + 1;
		}

		String newLoc = mgScr.getCurrentLocation().substring(0, ind);

		return newLoc;
	}

	public int getSwitchedToNPC() {
		return switchedToNPC;
	}

	public void setSwitchedToNPC(int NPC) {
		switchedToNPC = NPC;
	}

	// used when talking to people in the mindscape, otherwise it always resets
	// the currentNPC to description
	private boolean overrideSwitchedToNPC = false, switchFromMindscape = false;
	private String lastLoc;
	private int lastNPC;

	public void setOverrideSwitchedToNPC(String lastLocation, int prevNPC) {
		overrideSwitchedToNPC = true;
		lastLoc = lastLocation;
		lastNPC = prevNPC;
	}

	public boolean isDialogueForceEnacted() {
		return dialogueForceEnacted;
	}

	public void setDialogueForceEnacted(boolean dialogueForceEnacted) {
		this.dialogueForceEnacted = dialogueForceEnacted;
	}

	public void setLastLoc(String lastLocation) {
		this.lastLoc = lastLocation;
	}
}