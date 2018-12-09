package com.ec.conscientia.ingameoperations;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ec.conscientia.screens.MainGameScreen;

public class ScreenEffects {

	private MainGameScreen mgScr;

	public ScreenEffects(MainGameScreen mgScr) {
		this.mgScr = mgScr;
	}

	public void resetWhiteIn() {
		mgScr.mgVar.globalR = 1;
		mgScr.mgVar.globalG = 1;
		mgScr.mgVar.globalB = 1;
		mgScr.mgVar.globalA = 1;
		mgScr.mgVar.gameState = mgScr.SCREEN_EFFECT_SPAWN;
		mgScr.mgVar.fadeSpeed = .5f;
		mgScr.mgVar.globalColorValues = 0;
	}

	// used if person presses pause button while fade occurs to prevent fade
	// coloration pervasion
	public void resetNormalWorldColors() {
		mgScr.mgVar.globalR = 0;
		mgScr.mgVar.globalG = 0;
		mgScr.mgVar.globalB = 0;
		mgScr.mgVar.globalA = 1;
		mgScr.mgVar.globalColorValues = 1;

		mgScr.mgVar.table.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
				mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
		mgScr.mgVar.pauseButtonWindow.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
				mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
	}

	// SPAWNING SCREEN EFFECT
	public boolean whiteIn(float delta) {
		// here so that it doesn't show up after respawning in the Awakening
		// Chamber after death
		if (!mgScr.mgVar.checkedLocChange)
			changedArea();
		if (mgScr.mgVar.globalColorValues < 1) {
			mgScr.mgVar.fadeSpeed = ((mgScr.mgVar.fadeSpeed -= .00000000000001f) < .01f) ? .01f : mgScr.mgVar.fadeSpeed;
			mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues += mgScr.mgVar.fadeSpeed * delta) > 1) ? 1
					: mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalR = 1 - mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalG = 1 - mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalB = 1 - mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalA = 1;
			if (mgScr.getCurrentLocation().contains("MIND!")) {
				mgScr.mgVar.bg.setColor(.1f, .1f, 1, mgScr.mgVar.globalColorValues);
				mgScr.mgVar.mindscapeFadeIn = true;
			} else
				mgScr.mgVar.bg.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
						mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
			mgScr.mgVar.table.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
					mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
			mgScr.mgVar.pauseButtonWindow.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
					mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
			return false;
		} else {
			mgScr.enableResponseAreas();
			mgScr.enablePauseButton();
			mgScr.mgVar.checkedLocChange = false;
			return true;
		}
	}

	// ROOM CHANGE SCREEN EFFECT
	public void fadeChangeRoom(float delta) {
		if (mgScr.getCurrentLocation().contains("MIND!"))
			mgScr.mgVar.mindscapeFadeIn = true;
		else
			mgScr.mgVar.mindscapeFadeIn = false;

		if (!mgScr.mgVar.fadeComplete && mgScr.mgVar.globalColorValues > 0) {
			mgScr.mgVar.fadeSpeed = ((mgScr.mgVar.fadeSpeed += .1f) > 1) ? 1 : mgScr.mgVar.fadeSpeed;
			mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues -= mgScr.mgVar.fadeSpeed * delta) < 0) ? 0
					: mgScr.mgVar.globalColorValues;
			mgScr.mgVar.table.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
			mgScr.mgVar.pauseButtonWindow.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
			mgScr.mgVar.stats.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
			if (mgScr.mgVar.mindscapeFadeIn && !(mgScr.mgVar.bg.getColor().r < 1))
				mgScr.mgVar.bg.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
			else if (mgScr.mgVar.bg.getColor().r < 1)
				mgScr.mgVar.bg.setColor(.1f, .1f, 1, 0 + mgScr.mgVar.globalColorValues);
			else
				mgScr.mgVar.bg.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
		} else if (!mgScr.mgVar.fadeBackInComplete && !mgScr.mgVar.showLocationNameComplete) {
			if (!mgScr.mgVar.checkedLocChange)
				changedArea();
			if (mgScr.mgVar.locChanged) {
				if (mgScr.mgVar.soundManager.getBGMVolume() > .01f)
					mgScr.mgVar.soundManager.setFadeOut(true);
				else
					mgScr.mgVar.soundManager.setBGMTransitionVolume(0);

				locNameFade();
			} else
				mgScr.mgVar.showLocationNameComplete = true;
		} else if (!mgScr.mgVar.fadeBackInComplete && !mgScr.mgVar.fadeComplete) {
			mgScr.mgVar.checkedLocChange = false;
			if (mgScr.mgVar.locChanged) {
				mgScr.mgVar.soundManager.setBGMTransitionVolume(0);
				// saves stats and current dialogue address for NPC
				mgScr.mgVar.fileRW.writer.gameSave();
			}
			mgScr.mgVar.fadeComplete = true;
			mgScr.mgVar.fadeSpeed = 0;
		} else if (!mgScr.mgVar.fadeBackInComplete && mgScr.mgVar.fadeComplete) {
			if (mgScr.mgVar.globalColorValues < 1) {
				if (mgScr.mgVar.locChanged)
					mgScr.mgVar.soundManager.stopBGM();

				mgScr.mgVar.fadeSpeed = ((mgScr.mgVar.fadeSpeed += .1f) > 1) ? 1 : mgScr.mgVar.fadeSpeed;
				mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues += mgScr.mgVar.fadeSpeed * delta) > 1)
						? 1 : mgScr.mgVar.globalColorValues;
				// resets bg color from red to black if fading from combat mode
				if (mgScr.mgVar.gameState == mgScr.COMBAT) {
					mgScr.mgVar.globalR = 1 - mgScr.mgVar.globalColorValues;
					mgScr.mgVar.globalG = 0;
					mgScr.mgVar.globalB = 0;
					mgScr.mgVar.globalA = 1;
				}
				mgScr.mgVar.table.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
				mgScr.mgVar.pauseButtonWindow.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
				mgScr.mgVar.stats.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
				if (mgScr.mgVar.mindscapeFadeIn)
					mgScr.mgVar.bg.setColor(.1f, .1f, 1, 0 + mgScr.mgVar.globalColorValues);
				else
					mgScr.mgVar.bg.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
				// updates dialogue so that it displays updated screen when
				// screen is fading in
				if (mgScr.mgVar.globalColorValues > .01f && mgScr.mgVar.dialogue.getSwitchedToNPC() != -1) {
					if (mgScr.mgVar.mindscapeExit) {
						mgScr.mgVar.dialogue.setCurrentAddress(mgScr.mgVar.lastAddBeforeMindEntry);
						mgScr.mgVar.dialogue.setSwitchedToNPC(mgScr.mgVar.lastNPCBeforeMindEntry);
						// Trim last location
						int endInd = mgScr.mgVar.lastAddBeforeMindEntry.indexOf("!",
								mgScr.mgVar.lastAddBeforeMindEntry.indexOf("!") + 1) + 1;
						endInd = mgScr.mgVar.lastAddBeforeMindEntry.indexOf("!", endInd) + 1;
						String lastLoc = mgScr.mgVar.lastAddBeforeMindEntry.substring(0, endInd);
						mgScr.mgVar.dialogue.setLastLoc(lastLoc);
						mgScr.mgVar.mindscapeExit = false;
					}
					mgScr.mgVar.dialogue.endDialogue();
					mgScr.updateLocationArea();
					mgScr.updateDialogueArea();
					mgScr.updateResponseArea();
				}
			} else {
				mgScr.mgVar.locChanged = false;
				mgScr.mgVar.fadeBackInComplete = true;
			}
		}
	}

	private void locNameFade() {
		if (mgScr.loadingUtils.loadLocationName()) {
			mgScr.mgVar.bg.setColor(0, 0, 0, 0);
			if (System.currentTimeMillis() - mgScr.mgVar.locNameTimer < 1500)
				mgScr.mgVar.locName.setColor(1, 1, 1,
						((System.currentTimeMillis() - mgScr.mgVar.locNameTimer) / 1500.0f));
			else if (System.currentTimeMillis() - mgScr.mgVar.locNameTimer > 3000) {
				float value = ((System.currentTimeMillis() - mgScr.mgVar.locNameTimer - 3000) / 2000.0f);
				mgScr.mgVar.locName.setColor(1, 1, 1, 1 - value);
			}
			mgScr.mgVar.showLocationNameComplete = (System.currentTimeMillis() - mgScr.mgVar.locNameTimer > 5000) ? true
					: false;
			if (mgScr.mgVar.showLocationNameComplete) {
				mgScr.mgVar.locName.getTexture().dispose();
				mgScr.mgVar.locName = null;
			}
		}
	}

	private void changedArea() {
		mgScr.mgVar.checkedLocChange = true;
		if (mgScr.mgVar.dialogue.getCurrentAddress() != null) {
			String newArea = mgScr.mgVar.dialogue.getCurrentAddress().substring(
					mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1, mgScr.mgVar.dialogue.getCurrentAddress()
							.indexOf("!", mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1));
			if (!mgScr.mgVar.oldArea.equals(newArea)) {
				mgScr.mgVar.oldArea = newArea;
				mgScr.mgVar.locChanged = true;
			} else
				mgScr.mgVar.locChanged = false;
		} else
			mgScr.loadingUtils.nullError("CHANGED_AREA: " + mgScr.mgVar.dialogue.getCurrentAddress());
	}

	// CHANGE BACK FROM COMBAT SCREEN EFFECT
	// first half (fade out)
	// then switches to switch room fade to fade-in the main stage
	public void fadeChangeFromCombat(float delta) {
		if (!mgScr.mgVar.fadeComplete && mgScr.mgVar.globalColorValues > 0) {
			mgScr.mgVar.fadeSpeed = ((mgScr.mgVar.fadeSpeed += .1f) > 1) ? 1 : mgScr.mgVar.fadeSpeed;
			mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues -= mgScr.mgVar.fadeSpeed * delta) < 0) ? 0
					: mgScr.mgVar.globalColorValues;
			for (Actor a : mgScr.mgVar.stageCombat.getActors())
				a.setColor(1, 1, 1, 0 + mgScr.mgVar.globalColorValues);
		} else if (!mgScr.mgVar.fadeBackInComplete && !mgScr.mgVar.fadeComplete) {
			mgScr.mgVar.fadeComplete = true;
			mgScr.mgVar.fadeSpeed = 0;
		}
	}

	public boolean redIn(float delta) {
		if (mgScr.mgVar.globalColorValues < 1) {
			mgScr.mgVar.fadeSpeed = .999999999999999999999999999999f;
			mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues += mgScr.mgVar.fadeSpeed * delta) > 1) ? 1
					: mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalR = 1;
			mgScr.mgVar.globalG = 1 - mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalB = 1 - mgScr.mgVar.globalColorValues;
			mgScr.mgVar.globalA = 1;
			mgScr.mgVar.bg.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
					mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
			for (Actor a : mgScr.mgVar.stageCombat.getActors())
				a.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
						mgScr.mgVar.globalColorValues);
			return false;
		} else
			return true;

	}

	// MIND EDITING SCREEN EFFECT
	public boolean mindEditingFlickerEffect() {
		// for if loading a mind-editing address as initial address from
		// savefile
		mgScr.mgVar.globalColorValues = 1;
		if (System.currentTimeMillis() - mgScr.mgVar.flickerStartTime < 2000) {
			if (mgScr.mgVar.rand.nextBoolean())
				mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues += mgScr.mgVar.rand.nextFloat()) > 1)
						? 1 : mgScr.mgVar.globalColorValues;
			else
				mgScr.mgVar.globalColorValues = ((mgScr.mgVar.globalColorValues -= mgScr.mgVar.rand.nextFloat()) < 0)
						? 0 : mgScr.mgVar.globalColorValues;
			mgScr.mgVar.table.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
					mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
		} else {
			mgScr.mgVar.globalColorValues = 1;
			mgScr.mgVar.table.setColor(mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues,
					mgScr.mgVar.globalColorValues, mgScr.mgVar.globalColorValues);
			return true;
		}
		return false;
	}
}
