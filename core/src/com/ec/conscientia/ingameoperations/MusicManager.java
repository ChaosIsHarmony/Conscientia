package com.ec.conscientia.ingameoperations;

import com.ec.conscientia.Conscientia;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.screens.MainGameScreen;

public class MusicManager {
	MainGameScreen mgScr;
	SoundManager sm;
	Conscientia conscientia;

	// for location soundtrack changes
	public boolean changeLocations = false;

	public MusicManager(Conscientia conscientia, MainGameScreen gameInfo) {
		this.conscientia = conscientia;
		this.mgScr = gameInfo;
		sm = gameInfo.getSoundManager();
	}

	public void checkChangeMusic() {
		// here so that the program won't be constantly calling
		// nullDialogueError and making it impossible to hit the OK button to
		// clear it
		if (mgScr.mgVar.gameState != mgScr.ERROR)
			try {
				// checks for music change cues
				if (mgScr.getSoundManager().isBGMon() && !mgScr.mgVar.isLooney
						&& !(mgScr.getGameState() == mgScr.COMBAT || mgScr.getGameState() == mgScr.COMBAT_FADE_IN)) {
					// sets player state as not being in the mindscape
					// this is used to prevent from double entering the
					// mindscape when already in the mindscape
					mgScr.mgVar.inMindscape = false;
					switch (mgScr.getCurrentLocAddress(1)) {
					case "ENCLAVE!":
						changeEnclaveMusic();
						break;
					case "JER!":
						changeJerMusic();
						break;
					case "KABU!":
						changeKabuMusic();
						break;
					case "KAVU!":
						changeKavuMusic();
						break;
					case "MIND!":
						changeMindMusic();
						break;
					case "THIUDA!":
						changeThiudaMusic();
						break;
					case "URUGH!":
						changeUrughMusic();
						break;
					}
				} else if (mgScr.mgVar.isLooney)
					if (sm.getCurrentBGMID() != SoundManager.BGM_LOONEY)
						bgmSwitch(SoundManager.BGM_LOONEY);
			} catch (Exception e) {
				// turns music off
				sm.toggleBGM(false);
				sm.toggleSFX(false);
				mgScr.loadingUtils.nullError("CHECK_CHANGE_MUSIC: " + mgScr.getDialogue().getCurrentAddress());
			}
	}

	private void changeEnclaveMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "ENCLAVE!ARCHIVES!":
			switch (mgScr.getCurrentLocAddress(3)) {
			case "ENCLAVE!ARCHIVES!SCRIPTORIUM!":
				// sees the scriptorium for what it truly is
				if (conscientia.getConscVar().triggeredEvents.get(2036)){
					if (sm.getCurrentBGMID() != -1)
					sm.stopBGM();
				} 
				// scriptorium effect is weaker
				else if (conscientia.getConscVar().triggeredEvents.get(2035)){
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM);
				} else {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM);
				}
				break;
			case "ENCLAVE!ARCHIVES!SEAT OF THE TRUE ARCHON!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON);
				break;
			case "ENCLAVE!ARCHIVES!CRYPT OF THE ARCHONS!":
				if (sm.getCurrentBGMID() != -1)
					sm.stopBGM();
				break;
			case "ENCLAVE!ARCHIVES!SYLVAN REFUGE!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER);
				break;
			default:
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_GENERAL)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_GENERAL);
				break;
			}
			break;
		case "ENCLAVE!HALLS OF THE ADEPTI!":
			switch (mgScr.getCurrentLocAddress(3)) {
			case "ENCLAVE!HALLS OF THE ADEPTI!ORMENOS' CELL!":
				// opened sylvan refuge
				if (conscientia.getConscVar().triggeredEvents.get(2034)) {
					if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
						bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
				} else if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS)
					bgmSwitch(SoundManager.BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS);
				break;
			default:
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL)
					bgmSwitch(SoundManager.BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL);
				break;
			}
			break;
		case "ENCLAVE!THE NAVE!":
			switch (mgScr.getCurrentLocAddress(3)) {
			case "ENCLAVE!THE NAVE!ARCHONS' APSE!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER)
					bgmSwitch(SoundManager.BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER);
				break;
			default:
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_THE_NAVE_RECKONING)
					bgmSwitch(SoundManager.BGM_ENCLAVE_THE_NAVE_RECKONING);
				break;
			}
			break;
		case "ENCLAVE!THE PATH OF DISCIPLINE!":
			if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_PATH_OF_DISCIPLINE)
				bgmSwitch(SoundManager.BGM_ENCLAVE_PATH_OF_DISCIPLINE);
			break;
		case "ENCLAVE!THE THRESHOLD!":
			// out as torma
			if (conscientia.getConscVar().triggeredEvents.get(2014)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_EXODUS)
					bgmSwitch(SoundManager.BGM_ENCLAVE_EXODUS);
			} // out as ormenos
			else if (conscientia.getConscVar().triggeredEvents.get(2015)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_THRESHOLD_GENERAL)
					bgmSwitch(SoundManager.BGM_ENCLAVE_THRESHOLD_GENERAL);
			}
			break;
		case "ENCLAVE!UNDERHALLS!":
			if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_UNDERHALLS)
				bgmSwitch(SoundManager.BGM_ENCLAVE_UNDERHALLS);
			break;
		case "ENCLAVE!THE VAULT!":
			// out as torma
			if (conscientia.getConscVar().triggeredEvents.get(2014)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_EXODUS)
					bgmSwitch(SoundManager.BGM_ENCLAVE_EXODUS);
			} // out as ormenos
			else if (conscientia.getConscVar().triggeredEvents.get(2015)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_THE_VAULT)
					bgmSwitch(SoundManager.BGM_ENCLAVE_THE_VAULT);
			}
			break;
		}
	}

	private void changeKabuMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "KABU!CANYON!":
			if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_CANYON)
				bgmSwitch(SoundManager.BGM_KABU_CANYON);
			break;
		case "KABU!DAWN FORTRESS!":
			if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_DAWN_FORTRESS)
				bgmSwitch(SoundManager.BGM_KABU_DAWN_FORTRESS);
			break;
		case "KABU!DAZIR!":
			if (conscientia.getConscVar().triggeredEvents.get(20001)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_DAZIR)
				bgmSwitch(SoundManager.BGM_KABU_DAZIR);
			break;
		case "KABU!SANCTUARY!":
			if (mgScr.getDialogue().getCurrentAddress().contains("THRONE ROOM")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_CLOSE_TO_THE_ONE)
					bgmSwitch(SoundManager.BGM_CLOSE_TO_THE_ONE);
			} else if (mgScr.getDialogue().getCurrentAddress().contains("FAMLICUS")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_SANCTUARY) {
				bgmSwitch(SoundManager.BGM_KABU_SANCTUARY);
			}
			break;
		case "KABU!TAMBUL!":
			if (conscientia.getConscVar().triggeredEvents.get(20001)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else if (mgScr.getDialogue().getCurrentAddress().contains("AKKEBER")
					&& sm.getCurrentBGMID() != SoundManager.BGM_KABU_AKKEBERS_PLAY)
				bgmSwitch(SoundManager.BGM_KABU_AKKEBERS_PLAY);
			else if (!mgScr.getDialogue().getCurrentAddress().contains("AKKEBER")
					&& sm.getCurrentBGMID() != SoundManager.BGM_KABU_TAMBUL)
				bgmSwitch(SoundManager.BGM_KABU_TAMBUL);
			break;
		case "KABU!UR'RUK!":
			if (mgScr.getDialogue().getCurrentAddress().contains("THRONE ROOM")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_CLOSE_TO_THE_ONE)
					bgmSwitch(SoundManager.BGM_CLOSE_TO_THE_ONE);
			} else if (mgScr.getDialogue().getCurrentAddress().contains("DUNGEON OF THE VOID")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ARK_RIKHARR)
					bgmSwitch(SoundManager.BGM_ARK_RIKHARR);
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_URRUK)
				bgmSwitch(SoundManager.BGM_KABU_URRUK);
			break;
		case "KABU!WASTELAND!":
			if (conscientia.getConscVar().triggeredEvents.get(16064)
					&& (mgScr.getDialogue().getCurrentAddress().contains("STILLED SAND CRATER")
							&& (mgScr.getDialogue().getCurrentAddress().contains("1.501")
									|| mgScr.getDialogue().getCurrentAddress().contains("1.5010")
									|| mgScr.getDialogue().getCurrentAddress().contains("1.5011")
									|| mgScr.getDialogue().getCurrentAddress().contains("1.5012")))) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_ARK_RIKHARR)
					bgmSwitch(SoundManager.BGM_ARK_RIKHARR);
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_WASTELAND)
				bgmSwitch(SoundManager.BGM_KABU_WASTELAND);
			break;
		case "KABU!WELLSPRING!":
			if (mgScr.getDialogue().getCurrentAddress().contains("THRONE ROOM")
					|| mgScr.getDialogue().getCurrentAddress().contains("SILVER SHRINE")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_CLOSE_TO_THE_ONE)
					bgmSwitch(SoundManager.BGM_CLOSE_TO_THE_ONE);
			} else if (mgScr.getDialogue().getCurrentAddress().contains("HALL OF ETERNAL ATONEMENT")) {
				if (sm.getCurrentBGMID() != -1)
					sm.stopBGM();
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_WELLSPRING)
				bgmSwitch(SoundManager.BGM_KABU_WELLSPRING);
			break;
		case "KABU!WILDERNESS!":
			if (conscientia.getConscVar().triggeredEvents.get(20001)) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_PLOT_THICKENS)
					bgmSwitch(SoundManager.BGM_PLOT_THICKENS);
			} else if (mgScr.getDialogue().getCurrentAddress().contains("FAMLICUS")) {
				if (sm.getCurrentBGMID() != SoundManager.BGM_FAMLICUS)
					bgmSwitch(SoundManager.BGM_FAMLICUS);
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_WILDERNESS)
				bgmSwitch(SoundManager.BGM_KABU_WILDERNESS);
			break;
		}
	}

	private void changeKavuMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "KAVU!DAZIL!":
			if (sm.getCurrentBGMID() != -1)
				sm.stopBGM();
			break;
		}
	}

	private void changeJerMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "JER!VALONHEIM!":
			if (sm.getCurrentBGMID() != -1)
				sm.stopBGM();
			break;
		}
	}

	private void changeMindMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "MIND!MINDSCAPE!":
			mgScr.mgVar.inMindscape = true;
			if (mgScr.getCurrentLocAddress(3).contains("!THE VOID!")) {
				if (sm.getCurrentBGMID() != -1)
					sm.stopBGM();
			} else if (sm.getCurrentBGMID() != SoundManager.BGM_CLOSE_TO_THE_ONE)
				bgmSwitch(SoundManager.BGM_CLOSE_TO_THE_ONE);
			break;
		case "MIND!NETHER EDGE!":
			if (sm.getCurrentBGMID() != SoundManager.BGM_NEXUS)
				bgmSwitch(SoundManager.BGM_NEXUS);
			break;
		case "MIND!PALACE OF MEMORY!":
			switch (mgScr.getCurrentLocAddress(3)) {
			case "MIND!PALACE OF MEMORY!ZHILIAN STEPPE!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_ENCLAVE_EXODUS)
					bgmSwitch(SoundManager.BGM_ENCLAVE_EXODUS);
				break;
			case "MIND!PALACE OF MEMORY!ISLE OF THE WYRM!":
			case "MIND!PALACE OF MEMORY!TEMPLE OF THE VOICE!":
			case "MIND!PALACE OF MEMORY!HYDRARGYRUM KEEP!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_SANCTUARY)
					bgmSwitch(SoundManager.BGM_KABU_SANCTUARY);
				break;
			case "MIND!PALACE OF MEMORY!WASTELAND!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_WASTELAND)
					bgmSwitch(SoundManager.BGM_KABU_WASTELAND);
				break;
			case "MIND!PALACE OF MEMORY!RUINS OF URUGH!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_KABU_URRUK)
					bgmSwitch(SoundManager.BGM_KABU_URRUK);
				break;
			case "MIND!PALACE OF MEMORY!THE EMPYREAN!":
				if (sm.getCurrentBGMID() != SoundManager.BGM_CLOSE_TO_THE_ONE)
					bgmSwitch(SoundManager.BGM_CLOSE_TO_THE_ONE);
				break;
			}
			break;
		case "MIND!THE BOOK OF BIRACUL!":
		case "MIND!THE BOOK OF EIDOS!":
		case "MIND!THE BOOK OF RIKHARR!":
		case "MIND!THE BOOK OF THETIAN!":
		case "MIND!THE BOOK OF TORMA!":
		case "MIND!THE BOOK OF WULFIAS!":
			if (sm.getCurrentBGMID() != -1)
				sm.stopBGM();
			break;
		}
	}

	private void changeThiudaMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "THIUDA!SLUMS!":
			if (sm.getCurrentBGMID() != -1)
				sm.stopBGM();
			break;
		}
	}

	private void changeUrughMusic() {
		switch (mgScr.getCurrentLocAddress(2)) {
		case "URUGH!THE EMPYREAN!":
			if (sm.getCurrentBGMID() != -1)
				sm.stopBGM();
			break;
		}
	}

	// next song to play after switching areas
	private int nextTrack;

	private void bgmSwitch(int songNum) {
		nextTrack = songNum;
		// checks to see if sound is muted
		if (sm.getCurrentBGMID() != -1) {
			changeLocations = true;
			sm.setFadeOut(true);
		} else
			switchBGM();
	}

	public void switchBGM() {
		sm.setFadeIn(true);
		sm.loadBGM(nextTrack);
		if (sm.getCurrentBGMID() != -1)
			sm.playBGM();
	}
}