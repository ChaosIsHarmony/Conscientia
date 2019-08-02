package com.ec.conscientia.ingameoperations;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Location;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class LoadingUtils {
	MainGameScreen mgScr;

	public LoadingUtils(MainGameScreen mgScr) {
		this.mgScr = mgScr;
	}

	public boolean loadLocationName() {
		if (mgScr.mgVar.locName == null) {
			switch (mgScr.mgVar.dialogue.getCurrentAddress().substring(
					mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1, mgScr.mgVar.dialogue.getCurrentAddress()
							.indexOf("!", mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1))) {
			// Special
			case "END GAME":
				mgScr.mgVar.checkedLocChange = false;
				mgScr.mgVar.locChanged = false;
				return false;
			case "THE BOOK OF BIRACUL":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Biracul.png"));
				break;
			case "THE BOOK OF EIDOS":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Eidos.png"));
				break;
			case "THE BOOK OF RIKHARR":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Rikharr.png"));
				break;
			case "THE BOOK OF THETIAN":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Thetian.png"));
				break;
			case "THE BOOK OF TORMA":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Torma.png"));
				break;
			case "THE BOOK OF WULFIAS":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Books/Book of Wulfias.png"));
				break;

			// THE ENCLAVE
			case "ARCHIVES":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/Archives.png"));
				break;
			case "HALLS OF THE ADEPTI":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/Halls of the Adepti.png"));
				break;
			case "THE THRESHOLD":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/The Threshold.png"));
				break;
			case "THE NAVE":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/The Nave.png"));
				break;
			case "PALACE OF MEMORY":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/The Palace of Memory.png"));
				break;
			case "THE PATH OF DISCIPLINE":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/The Path of Discipline.png"));
				break;
			case "THE VAULT":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/The Vault.png"));
				break;
			case "UNDERHALLS":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Torma/Underhalls of the Enclave.png"));
				break;

			// KABU
			case "CANYON":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Crescent Canyon.png"));
				break;
			case "DAWN FORTRESS":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Dawn Fortress.png"));
				break;
			case "DAZIR":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Dazir.png"));
				break;
			case "MINDSCAPE":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Mindscape.png"));
				break;
			case "NETHER EDGE":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Nether Edge.png"));
				break;
			case "SANCTUARY":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Sanctuary.png"));
				break;
			case "TAMBUL":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Tambul.png"));
				break;
			case "UR'RUK":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Ur'Ruk.png"));
				break;
			case "WASTELAND":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Wasteland.png"));
				break;
			case "WELLSPRING":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/The Wellspring.png"));
				break;
			case "WILDERNESS":
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Kaban Wilderness.png"));
				break;

			default:
				mgScr.mgVar.locName = new Sprite(new Texture("Loc Names Eidos/Parts Unknown.png"));
			}
			mgScr.mgVar.locName.setSize(mgScr.mgVar.stage.getWidth(), mgScr.mgVar.stage.getHeight() / 2.0f);
			mgScr.mgVar.locName.setAlpha(0);
			mgScr.mgVar.locNameTimer = System.currentTimeMillis();
		} else
			return true;
		return false;
	}

	public List<Location> getMapList(String areaName, ArrayList<Location> tempList) {
		final List<Location> list;

		if (mgScr.mgVar.conscientia.isUseAltFont())
			list = new List<Location>(mgScr.mgVar.skin, "whiners_no_bg");
		else
			list = new List<Location>(mgScr.mgVar.skin, "no_bg");

		Location[] locationList = new Location[tempList.size()];

		int ind = 0;
		for (Location g : tempList)
			locationList[ind++] = g;

		list.setItems(locationList);

		return list;
	}

	// hacky, used only for the sake of providing feedback on the responseAreas
	// by highlighting in red
	// should be put in the atlas when refactoring
	public NinePatch processNinePatchFile(String fname) {
		final Texture t = new Texture(Gdx.files.internal(fname));
		final int width = t.getWidth() - 2;
		final int height = t.getHeight() - 2;
		// last four parameters must be thus to match current responseArea
		// dimensions
		return new NinePatch(new TextureRegion(t, 1, 1, width, height), 25, 25, 10, 10);
	}

	public boolean getGenerateFightButton() {
		if (mgScr.getCurrentLocAddress(2).contains("MIND!"))
			return false;
		else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FAMLICUS")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("EIDOS") 
                || mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("NARGUND"))
			return false;
		// during trial
		else if ((mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("HEYAR")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("LOGIRA")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("KHLUTT"))
				&& mgScr.getCurrentLocAddress(3).contains("COURTYARD!"))
			return false;
		else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("LOGIRA")
				&& mgScr.getCurrentLocAddress(3).contains("ANTECHAMBER!"))
			return false;
		// intangible characters
		else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FENRIR")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("LUIN")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("RECKONER")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FARCASTER")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FATHOM")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("COGITO")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("ARBITER")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("EXECUTOR")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("CHOIR OF ARCHONS"))
			return false;
		// in a relaxed state, Ormenos will not attack
		else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("WORD SMITH")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("WORD WEAVER"))
			return false;
		// unable to attack them during the debate
		else if ((mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FORM FORGER")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("MEMORY KEEPER"))
				&& mgScr.getCurrentLocAddress(3).contains("MATHETIA!"))
			return false;
		// should be too terrified to fight
		else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("SYLVARCH")
				|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("BIRACUL"))
			return false;
		// would fuck up throne room interaction
		else if (mgScr.getCurrentLocAddress(3).contains("THRONE ROOM!")
				&& mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("QUANGJO"))
			return false;
		// flashback
		else if (mgScr.getCurrentLocAddress(2).contains("UR'RUK")
				&& (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("BIRACUL")
						|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("VANARGAND")
						|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("ARK")))
			return false;

		else
			return true;
	}

	public void nullError(String currentAddress) {
		mgScr.mgVar.stageError = new Stage(mgScr.viewport, mgScr.batch);
		mgScr.mgVar.gameState = mgScr.ERROR;
		// Shows a pop-up with shit address + instructions to post in subreddit
		String message = "The Thought Wheel has encountered an error." + "\nFurther progress is unfathomable."
				+ "\n\nGo to: www.reddit.com/r/Conscientia/" + "\nSubmit code: ";

		// pare down to area
		if (currentAddress != null && currentAddress.contains("LOADING ERROR")) {
			message += currentAddress;
		} else if (currentAddress != null) {
			try {
				String heading = currentAddress.substring(0, currentAddress.indexOf(":") + 2);
				String area = currentAddress.substring(currentAddress.indexOf("!") + 1);
				area = area.substring(area.indexOf("!") + 1);
				String address = area.substring(area.indexOf("!") + 1);
				String npc = address.substring(address.indexOf("!") + 1, address.lastIndexOf("!"));

				area = area.substring(0, area.indexOf("!"));
				address = address.substring(0, address.indexOf("!"));

				message += heading + area + " - " + address + " -- " + npc;
			} catch (Exception e) {
				message += currentAddress + " - "
						+ ((mgScr.mgVar.currentLocation != null) ? mgScr.mgVar.currentLocation : "nullLoc");
			}
		} else if (mgScr.mgVar.currentLocation != null) {
			message += mgScr.mgVar.currentLocation;
		} else
			message += "LOC FAILURE";

		// Mind editing flicker + pop-up
		final Window popupWin = new Window("", mgScr.mgVar.skin, "no_bg");
		final Window popupLabelWin = new Window("", mgScr.mgVar.skin);
		final Label popupLabel = new Label("", mgScr.mgVar.skin);
		popupLabel.setText(message);
		popupLabel.setWrap(true);
		final TextButton acceptButton = new TextButton("End", mgScr.mgVar.skin);
		acceptButton.setTouchable(Touchable.enabled);
		acceptButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.mgVar.soundManager.stopBGM();
				mgScr.mgVar.soundManager.setFadeIn(true);
				mgScr.mgVar.soundManager.setBGMVolume(.3f);
				mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
				mgScr.mgVar.conscientia.changeScreen(CommonVar.MAIN_MENU, true, 0);
			}
		});

		popupLabelWin.add(popupLabel).grow();

		final Window spacerWindow = new Window("", mgScr.mgVar.skin, "no_bg");
		spacerWindow.setMovable(false);

		popupWin.setSize(mgScr.mgVar.stageError.getWidth() / 1.5f, mgScr.mgVar.stageError.getHeight() / 1.7f);
		popupWin.setPosition((mgScr.mgVar.stageError.getWidth() / 2) - (popupWin.getWidth() / 2),
				(mgScr.mgVar.stageError.getHeight() / 2) - (popupWin.getHeight() / 2));
		popupWin.add(spacerWindow).grow().size(popupWin.getWidth(), popupWin.getHeight() * .01f).row();
		popupWin.add(popupLabelWin).grow().size(popupWin.getWidth(), popupWin.getHeight() * .7f).row();
		popupWin.add(spacerWindow).grow().size(popupWin.getWidth(), popupWin.getHeight() * .09f).row();
		popupWin.add(acceptButton).grow().size(popupWin.getWidth() * .25f, popupWin.getHeight() * .2f);

		mgScr.mgVar.stageError.addActor(popupWin);
		// sets the stage as the input processor
		Gdx.input.setInputProcessor(mgScr.mgVar.stageError);
	}
}
