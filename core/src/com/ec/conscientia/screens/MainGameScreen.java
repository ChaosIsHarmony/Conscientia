package com.ec.conscientia.screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ec.conscientia.entities.Acquirable;
import com.ec.conscientia.entities.CombatMenu;
import com.ec.conscientia.entities.Log;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.dialogue.Dialogue;
import com.ec.conscientia.entities.Player;
import com.ec.conscientia.ingameoperations.CheckUniqueEvent;
import com.ec.conscientia.ingameoperations.GenerateDisplayWin;
import com.ec.conscientia.ingameoperations.LoadingUtils;
import com.ec.conscientia.ingameoperations.MusicManager;
import com.ec.conscientia.ingameoperations.ScreenEffects;
import com.ec.conscientia.ingameoperations.TriggeredEvents;
import com.ec.conscientia.variables.MainGameVar;
import com.ec.conscientia.variables.CommonVar;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.FileReaderWriter;
import com.ec.conscientia.SoundManager;

public class MainGameScreen implements Screen {
	public MainGameVar mgVar;
	public LoadingUtils loadingUtils;
	public ScreenEffects scrFX;
	public GenerateDisplayWin genDispWin;

	public Viewport viewport;
	public OrthographicCamera camera;
	public SpriteBatch batch;

	// final ints to set && retrieve text from given areas/fields
	public final int LOCATION_AREA = 0, DIALOGUE_AREA = 1, RESPONSE_1_AREA = 2, RESPONSE_2_AREA = 3,
			RESPONSE_3_AREA = 4, NPC_AREA = 5;

	// display states
	public final int IN_PLAY = 0, PAUSED = 1, SCREEN_EFFECT_CHANGE_ROOM = 2, SCREEN_EFFECT_CHANGE_AREA = 3,
			SCREEN_EFFECT_MIND_EDITING = 4, SCREEN_EFFECT_SPAWN = 5, DISPLAY_ITEM = 6, COMBAT = 7, COMBAT_FADE_IN = 8,
			END_GAME = 9, ERROR = 10, MAP = 11;

	public final int ITEM_GLYPH = 0, ITEM_TOME = 1;

	// this one is used exclusively with test suite
	public MainGameScreen(Conscientia consc) {
		mgVar = new MainGameVar();
		mgVar.conscientia = consc;
	}

	public MainGameScreen(String newOrLoadGame, int bookNum, SoundManager sound, Conscientia consc) {
		loadingUtils = new LoadingUtils(this);
		scrFX = new ScreenEffects(this);
		genDispWin = new GenerateDisplayWin(this);

		mgVar = new MainGameVar();

		mgVar.mgScr = this;
		mgVar.conscientia = consc;
		mgVar.uniqueEvent = new CheckUniqueEvent(this);
		mgVar.fileRW = new FileReaderWriter(consc, this);
		mgVar.soundManager = sound;
		mgVar.musicManager = new MusicManager(consc, this); // must go here or
															// else soundManger
															// == null
		mgVar.soundManager.setMusicManager(mgVar.musicManager);

		Gdx.input.setOnscreenKeyboardVisible(false);
		mgVar.conscientia.getConscVar().bookID = bookNum;

		// resets all variables for new game
		// if is a loaded game, then code further down will autocorrect these
		// variables
		resetStaticVariables();

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(CommonVar.GAME_WIDTH, CommonVar.GAME_HEIGHT, camera);
		mgVar.stage = new Stage(viewport, batch);
		mgVar.stagePause = new Stage(viewport, batch);
		mgVar.stageCombat = new Stage(viewport, batch);
		mgVar.skin = new Skin(Gdx.files.internal("Skin/ConscientiaSkin.json"));
		camera.position.set(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2, 0);

		// new game
		if (newOrLoadGame.equals("new game")) {
			// writes new game files
			mgVar.fileRW.writeNewGameFiles(mgVar.conscientia.getConscVar().bookID);
			// sets current location
			setCurrentLocation(mgVar.fileRW.loadCurrentLocationFromSavedGameFiles());
		} else if (newOrLoadGame.equals("load game")) {
			// load
			mgVar.fileRW.loadSavedGameFiles();
			// sets current location
			setCurrentLocation(mgVar.fileRW.loadCurrentLocationFromSavedGameFiles());
			// for mindscape potentiality
			setMindscapeStuff();
		}

		// dialogue
		mgVar.dialogue = new Dialogue(this);

		// Player
		mgVar.player = new Player();
		mgVar.fileRW.setPlayerStats();

		// to see if Mindscape or logs should be displayed
		for (Integer i : mgVar.player.getItemsAcquired()) {
			if (i <= Acquirable.MAX_LOG) {
				if (i == Acquirable.INTROSPECTION_TOME)
					mgVar.hasIntrospection = true;
			}
			if (i > Acquirable.MAX_GLYPH && i <= Acquirable.MAX_LOG)
				mgVar.hasLogs = true;
			if (i < Acquirable.MAX_GLYPH)
				mgVar.hasGlyphs = true;
			if (i == Log.TORMA_II_TOME || i == Log.TORMA_III_TOME || i == Log.TORMA_VI_TOME || i == Log.TORMA_VII_TOME
					|| i == Log.TORMA_X_TOME || mgVar.conscientia.getConscVar().bookID == CommonVar.TOR)
				mgVar.hasMaps = true;
		}

		// load triggered events
		mgVar.conscientia.getConscVar().triggeredEvents = new TriggeredEvents(mgVar.conscientia, this);
		// sets Fam as gone if player already has resurrection
		if (mgVar.player.getItemsAcquired().contains(Acquirable.FAMLICUS)
				|| mgVar.player.getItemsAcquired().contains(Acquirable.RESURRECTION_GLYPH))
			mgVar.conscientia.getConscVar().triggeredEvents.put(17900, true);

		// to see if player can enter red tower
		if (getAwareness() / (CommonVar.persistentEvents.length * 1.0f) > 1.01f)
			mgVar.conscientia.getConscVar().triggeredEvents.put(30000, true);

		// enables map feature if played Book of Torma but have no Torma tomes
		if (mgVar.conscientia.getConscVar().triggeredEvents.get(0))
			mgVar.hasMaps = true;
		// sets currentNPC
		mgVar.currentNPC = mgVar.fileRW.getCurrentNPC();

		// loads special event cues
		mgVar.cues = mgVar.fileRW.getEventCues();

		// loads all NPCs key = name, value = idNum
		mgVar.NPCbyNum = mgVar.fileRW.getNPCsbyNumHashMap();

		// load NPCs
		mgVar.currentAreaNPCs = new ArrayList<NPC>();
		for (int id : mgVar.fileRW.getNPCs(mgVar.currentLocation)) {
			if (id != -1)
				mgVar.currentAreaNPCs.add(new NPC(id, this));
		}

		// chooses last relevant NPC
		for (NPC npc : mgVar.currentAreaNPCs) {
			// start dialogue
			if (npc.getIDnum() == mgVar.currentNPC) {
				mgVar.dialogue.initiateDialogue(npc, npc.getDialogueAddress(mgVar.currentLocation));
				break;
			}
		}

		// used to see if we need to display area name
		mgVar.oldArea = getCurrentLocation().substring(getCurrentLocation().indexOf("!") + 1,
				getCurrentLocation().indexOf("!", getCurrentLocation().indexOf("!") + 1));

		mgVar.musicManager.checkChangeMusic();

		// BG
		mgVar.bg = new Sprite(new Texture("Backgrounds/leather_black_01.png"));
		mgVar.bg.setSize(mgVar.stage.getWidth(), mgVar.stage.getHeight());
		mgVar.bg.setAlpha(0);

		createStage();
	}

	// loads mindscape variables in case player closed out of game while in
	// mindscape
	private void setMindscapeStuff() {
		String[] mindscapeStuff = mgVar.fileRW.loadMindscapeStuff();
		mgVar.lastAddBeforeMindEntry = mindscapeStuff[0];
		mgVar.lastNPCBeforeMindEntry = Integer.parseInt(mindscapeStuff[1]);
	}

	public void resetStaticVariables() {
		mgVar.hasLogs = false;
		mgVar.hasGlyphs = false;
		mgVar.hasIntrospection = false;
		mgVar.hasMaps = false;
		mgVar.isLooney = false;
		mgVar.playerVictorious = false;
		mgVar.inMindscape = false;
		mgVar.canScroll = mgVar.initLoading = true;

		// whites in always upon starting up the game
		scrFX.resetWhiteIn();

		// reinitialized so that the gameLoop will get called if exit from one
		// game and loading another
		mgVar.isClickable = true;
		mgVar.clicked = true;
		mgVar.lastClick = System.currentTimeMillis();

		// fades
		mgVar.fadeComplete = false;
		mgVar.fadeBackInComplete = false;
		mgVar.showLocationNameComplete = false;
		mgVar.fadeInFromCombat = false;

		// unique event checker bools
		mgVar.checkItemAcquired = false;

		// for dialogueArea changes
		mgVar.newNPC = -1;
	}

	private void createStage() {
		mgVar.stage.addActor(genDispWin.createTable());
		mgVar.stage.addActor(genDispWin.createLogoPauseButton(false));
		mgVar.stage.addActor(genDispWin.createStats());
		// sets the stage as the input processor
		Gdx.input.setInputProcessor(mgVar.stage);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		// to see if there is more text to scroll
		mgVar.canScrollButton.setVisible((mgVar.dialogueAreaScrollPane.getScrollPercentY() < 1f) ? true : false);
		mgVar.canScrollButton
				.setTouchable((mgVar.canScrollButton.isVisible()) ? Touchable.enabled : Touchable.disabled);

		// for debugging
		mgVar.stats.setText(mgVar.player.getAllPersonalityStats()[0] + "-" + mgVar.player.getAllPersonalityStats()[1]
				+ "\n" + mgVar.player.getAllPersonalityStats()[2] + "-" + mgVar.player.getAllPersonalityStats()[3]
				+ "\n" + mgVar.player.getAllPersonalityStats()[4] + "-" + mgVar.player.getAllPersonalityStats()[5]);

		gameLoop(delta);

		Gdx.gl.glClearColor(mgVar.globalR, mgVar.globalG, mgVar.globalB, mgVar.globalA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// sets center
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		mgVar.bg.draw(batch);
		if (mgVar.locName != null)
			mgVar.locName.draw(batch);
		batch.end();

		switch (mgVar.gameState) {
		case ERROR:
			Gdx.input.setInputProcessor(mgVar.stageError);
			mgVar.stageError.act(delta);
			mgVar.stageError.draw();
			break;
		case IN_PLAY:
			// Otherwise controls stop working when fading in from death at the
			// Enclave
			if (Gdx.input.getInputProcessor() != mgVar.stage)
				Gdx.input.setInputProcessor(mgVar.stage);
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case END_GAME:
			endGame(delta);
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case PAUSED:
			mgVar.stagePause.act(delta);
			mgVar.stagePause.draw();
			break;
		case SCREEN_EFFECT_CHANGE_ROOM:
			if (!mgVar.fadeComplete) {
				// needed so that when starting non-Eidos book, the dialogue
				// shit won't display
				mgVar.table.setVisible(false);
				mgVar.pauseButtonWindow.setVisible(false);
				setHourGlassCursor();
				disableClickableAreas();
				scrFX.fadeChangeRoom(delta);
			} else if (!mgVar.fadeBackInComplete) {
				scrFX.fadeChangeRoom(delta);
				// reset to visible so that they will show up
				mgVar.table.setVisible(true);
				mgVar.pauseButtonWindow.setVisible(true);
			} else {
				scrFX.resetNormalWorldColors();
				setNormalCursor();
				mgVar.fadeComplete = false;
				mgVar.fadeBackInComplete = false;
				mgVar.showLocationNameComplete = false;
				enablePauseButton();
				enableResponseAreas();
				mgVar.gameState = IN_PLAY;
				gameLoop(delta);
			}
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case SCREEN_EFFECT_MIND_EDITING:
			// flickers screen briefly when mind-editing occurs
			if (scrFX.mindEditingFlickerEffect())
				mgVar.gameState = IN_PLAY;
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case SCREEN_EFFECT_SPAWN:
			// this part used when transitioning back to dialogue after death in
			// combat
			if (mgVar.stageCombat != null) {
				mgVar.stageCombat.clear();
				mgVar.stageCombat.dispose();
				Gdx.input.setInputProcessor(mgVar.stage);
			}
			if (scrFX.whiteIn(delta))
				mgVar.gameState = IN_PLAY;
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case DISPLAY_ITEM:
			if (!displayItem()) {
				// Disabled until fade in complete
				mgVar.pauseHoverImg.setTouchable(Touchable.enabled);
				mgVar.gameState = IN_PLAY;
			}
			mgVar.stage.act(delta);
			mgVar.stage.draw();
			break;
		case COMBAT_FADE_IN:
			if (scrFX.redIn(delta))
				mgVar.gameState = COMBAT;
			else
				mgVar.stageCombat.draw();
			break;
		case COMBAT:
			if (mgVar.combatMenu.update()) {
				// necessary for death condition so that the areas are
				// updated before the whiteIn() occurs
				updateLocationArea();
				updateDialogueArea();
				updateResponseArea();
				mgVar.fadeInFromCombat = true;
				mgVar.fadeComplete = false;
				mgVar.fadeBackInComplete = false;
			} else if (mgVar.fadeInFromCombat) {
				if (mgVar.playerVictorious) {
					if (!mgVar.fadeComplete) {
						setHourGlassCursor();
						disableClickableAreas();
						scrFX.fadeChangeFromCombat(delta);
						mgVar.stageCombat.act(delta);
						mgVar.stageCombat.draw();
					} else if (!mgVar.fadeBackInComplete) {
						mgVar.stageCombat.clear();
						mgVar.stageCombat.dispose();
						Gdx.input.setInputProcessor(mgVar.stage);
						scrFX.fadeChangeRoom(delta);
						mgVar.stage.act();
						mgVar.stage.draw();
						mgVar.uniqueEvent.checkForUniqueEvents();
					} else {
						setNormalCursor();
						mgVar.fadeComplete = false;
						mgVar.fadeBackInComplete = false;
						enablePauseButton();
						enableResponseAreas();
						mgVar.gameState = IN_PLAY;
					}
				} else
					mgVar.gameState = SCREEN_EFFECT_SPAWN;
			} else {
				mgVar.stageCombat.act(delta);
				mgVar.stageCombat.draw();
			}
			break;
		case MAP:
			getStageMap().act(delta);
			getStageMap().draw();
			break;
		}

		// Change music if necessary
		mgVar.musicManager.checkChangeMusic();
	}

	private boolean displayItem() {
		switch (mgVar.itemToDisplay) {
		case ITEM_GLYPH:
			return displayItemWindow(mgVar.item);
		case ITEM_TOME:
			return displayItemWindow(mgVar.item);
		}
		return true;
	}

	private boolean displayItemWindow(Acquirable item) {
		if (!mgVar.itemToDisplaySetUpComplete) {
			disableClickableAreas();
			mgVar.itemWindow = new Window(item.acqToString(), mgVar.skin, "smallTitle");
			mgVar.itemWindow.getTitleLabel().setVisible(false);
			Window spacerWindow = new Window("", mgVar.skin, "no_bg");
			Label itemName = new Label(item.acqToString(), mgVar.skin, "bigFont");

			mgVar.itemWindow.setSize(mgVar.stage.getWidth() / 2, mgVar.stage.getHeight() / 2);
			mgVar.itemWindow.setPosition((mgVar.stage.getWidth() / 2) - (mgVar.itemWindow.getWidth() / 2),
					(mgVar.stage.getHeight() / 2) - (mgVar.itemWindow.getHeight() / 2));
			mgVar.itemColorValue = 0;

			mgVar.itemWindow.add(itemName).align(Align.top).row();
			mgVar.itemWindow.add(spacerWindow).align(Align.top).padBottom(20).row();
			mgVar.itemWindow.add(item.acqGetImg())
					.size(mgVar.itemWindow.getWidth() * .5f, mgVar.itemWindow.getHeight() * .5f).align(Align.top);
			mgVar.itemWindow.setColor(mgVar.itemColorValue, mgVar.itemColorValue, mgVar.itemColorValue,
					mgVar.itemColorValue);
			mgVar.stage.addActor(mgVar.itemWindow);
			mgVar.itemToDisplaySetUpComplete = true;
			mgVar.itemFadeIn = true;
		} else {
			if (mgVar.itemFadeIn) {
				mgVar.itemColorValue = ((mgVar.itemColorValue += .01f) > 1) ? 1 : mgVar.itemColorValue;
				if (mgVar.itemColorValue == 1) {
					mgVar.itemFadeIn = false;
					mgVar.itemFadeOut = true;
					mgVar.holdDisplay = System.currentTimeMillis();
				}
				mgVar.itemWindow.setColor(mgVar.itemColorValue, mgVar.itemColorValue, mgVar.itemColorValue,
						mgVar.itemColorValue);
			} else if (mgVar.itemFadeOut) {
				if (!(System.currentTimeMillis() - mgVar.holdDisplay < 1500)) {
					mgVar.itemColorValue = ((mgVar.itemColorValue -= .02f) < 0) ? 0 : mgVar.itemColorValue;
					if (mgVar.itemColorValue == 0)
						mgVar.itemFadeOut = false;
					mgVar.itemWindow.setColor(mgVar.itemColorValue, mgVar.itemColorValue, mgVar.itemColorValue,
							mgVar.itemColorValue);
				}
			} else {
				mgVar.itemWindow.remove();
				enablePauseButton();
				enableResponseAreas();
				return false;
			}
		}
		return true;
	}

	@Override
	public void resize(int width, int height) {
		mgVar.stage.getViewport().update(width, height, true);
		mgVar.stagePause.getViewport().update(width, height, true);
		mgVar.stageCombat.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
		mgVar.stage.dispose();
		mgVar.skin.dispose();
		mgVar.bg.getTexture().dispose();

		mgVar.responseOneArea = null;
		mgVar.responseTwoArea = null;
		mgVar.responseThreeArea = null;
	}

	/////// GETTERS & SETTERS ///////////
	private void setText(int textToSwitch, String string) {
		switch (textToSwitch) {
		case LOCATION_AREA:
			mgVar.locationArea.setText(string);
			break;
		case DIALOGUE_AREA:
			genDispWin.createDialogueArea();
			// resets scrollbar to the top
			mgVar.NPCdialogueAreaScrollPane.setScrollPercentY(0);
			mgVar.dialogueAreaScrollPane.setScrollPercentY(0);
			mgVar.npcDialogueLabel.setText("\t\t" + string);
			mgVar.dialogueLabel.setText("\t\t" + string);
			break;
		case RESPONSE_1_AREA:
			mgVar.responseOneArea.setText(string);
			break;
		case RESPONSE_2_AREA:
			mgVar.responseTwoArea.setText(string);
			break;
		case RESPONSE_3_AREA:
			mgVar.responseThreeArea.setText(string);
			break;
		}
	}

	public String getCurrentLocation() {
		return mgVar.currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		mgVar.currentLocation = currentLocation;
	}

	public Player getPlayer() {
		return mgVar.player;
	}

	private String getPlayerLocation() {
		String loc = "";
		loc = getCurrentLocation();
		// Trims to relevant room
		loc = loc.substring(loc.indexOf('!') + 1);
		// cuts off last exclamation mark
		loc = loc.substring(loc.indexOf('!') + 1, loc.length() - 1);
		return loc;
	}

	/////// GAME LOOP ////////

	private void gameLoop(float delta) {
		// deals with music fade
		if (mgVar.soundManager.getFadeIn() && mgVar.soundManager.getCurrentBGMID() != -1) {
			mgVar.soundManager.fadeInBGM();
		} else if (mgVar.soundManager.getFadeOut()) {
			if (mgVar.gameState != END_GAME)
				mgVar.soundManager.fadeOutBGM();
			else
				mgVar.soundManager.longFadeOutBGM();
		}

		if (isClickable() && mgVar.gameState != ERROR) {
			// update
			if (mgVar.clicked) {
				setHourGlassCursor();
				// update everything
				mgVar.dialogue.update();
				// prevents location area from updating if changing rooms
				if (mgVar.gameState != SCREEN_EFFECT_CHANGE_ROOM) {
					updateLocationArea();
					updateDialogueArea();
					updateResponseArea();
				}

				mgVar.clicked = false;
				mgVar.isClickable = false;
				// shows it's ready to be clicked
				setNormalCursor();
			}
		}
	}

	// removes listeners so no activity happens during pause
	public void disableClickableAreas() {
		// Pause Menu; or else when you switch back, the screen will freeze and
		// never complete
		mgVar.pauseHoverImg.setTouchable(Touchable.disabled);
		// Response areas
		mgVar.responseOneArea.setTouchable(Touchable.disabled);
		mgVar.responseTwoArea.setTouchable(Touchable.disabled);
		mgVar.responseThreeArea.setTouchable(Touchable.disabled);
	}

	public void enablePauseButton() {
		mgVar.pauseHoverImg.setTouchable(Touchable.enabled);
	}

	// puts listeners on responses
	public void enableResponseAreas() {
		// Response areas
		mgVar.responseOneArea.setTouchable(Touchable.enabled);
		mgVar.responseTwoArea.setTouchable(Touchable.enabled);
		mgVar.responseThreeArea.setTouchable(Touchable.enabled);
		// can scroll
		mgVar.canScrollButton.setTouchable(Touchable.enabled);
	}

	public void selectResponse(int index) {
		mgVar.dialogue.setPointerIndex(index);

		mgVar.clicked = true;
		// starts from the index of the first comma + 1 to account
		// for personality points
		String targetNewAddress = (mgVar.dialogue.getAddresses()[mgVar.dialogue.getPointerIndex()] != null)
				? mgVar.dialogue.getAddresses()[mgVar.dialogue.getPointerIndex()].getAddress().substring(
						mgVar.dialogue.getAddresses()[mgVar.dialogue.getPointerIndex()].getAddress().indexOf(',') + 1)
				: "(-_-)";

		// updates currentAddress to selected address & resets
		// pointerIndex
		if (mgVar.dialogue.validAddress(targetNewAddress)
				&& mgVar.dialogue.getAddresses()[mgVar.dialogue.getPointerIndex()] != null) {
			// plays click sound
			mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
			// updates personality stats
			mgVar.dialogue.updateStats();
			mgVar.dialogue.setCurrentAddress(targetNewAddress);

			// reset the red highlight
			mgVar.responseOneArea.getLabel().setColor(1, 1, 1, 1);
			mgVar.responseTwoArea.getLabel().setColor(1, 1, 1, 1);
			mgVar.responseThreeArea.getLabel().setColor(1, 1, 1, 1);

			// update
			// added delta for fade in
			gameLoop(1.0f);
		} else {
			// plays click sound
			mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_NEGATIVE);
		}
	}

	private void setHourGlassCursor() {
		Pixmap pm = new Pixmap(Gdx.files.internal("General/hourGlassCursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();
	}

	private void setNormalCursor() {
		Pixmap pm = new Pixmap(Gdx.files.internal("General/normalCursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();
	}

	// multi-event checker: used to see if a group of triggered events are true
	// and sets corresponding dialogue address
	public void multichecker(String address) {
		// checks first part of address to determine which file it's in
		if (address.substring(0, address.indexOf("!")).equals("KABU"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.EID));
		else if (address.substring(0, address.indexOf("!")).equals("KAVU"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.RIK));
		else if (address.substring(0, address.indexOf("!")).equals("ENCLAVE"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.TOR));
		else if (address.substring(0, address.indexOf("!")).equals("JER"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.THE));
		else if (address.substring(0, address.indexOf("!")).equals("THIUDA"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.WUL));
		else if (address.substring(0, address.indexOf("!")).equals("URUGH"))
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, CommonVar.BIR));
		else // Mindscape and Nether Edge
			mgVar.dialogue.setCurrentAddress(mgVar.fileRW.mutlichecker(address, -1));

		mgVar.dialogue.update();
	}

	public void endGame(float delta) {
		mgVar.fadeSpeed = ((mgVar.fadeSpeed += .1f) > 1) ? 1 : mgVar.fadeSpeed;
		mgVar.bg.setAlpha((mgVar.bg.getColor().a - (mgVar.fadeSpeed * .25f * delta) < 0) ? 0
				: mgVar.bg.getColor().a - (mgVar.fadeSpeed * .25f * delta));
		if (!mgVar.soundManager.getFadeOut()) {
			// stop current bgm
			mgVar.soundManager.stopBGM();
			mgVar.soundManager.setFadeIn(true);
			mgVar.conscientia.changeScreen(CommonVar.END_CREDITS, false, 0);
		}
	}

	public String getCurrentLocAddress(int excalmationPoint) {
		int ind = 0;
		for (int i = 0; i < excalmationPoint; i++) {
			ind = mgVar.currentLocation.indexOf("!", ind) + 1;
		}

		String newLoc = mgVar.currentLocation.substring(0, ind);

		return newLoc;
	}

	public boolean isClickable() {
		if (System.currentTimeMillis() - mgVar.lastClick > 1000) {
			mgVar.lastClick = System.currentTimeMillis();
			return mgVar.isClickable = true;
		} else
			return mgVar.isClickable;
	}

	// updates location text
	public void updateLocationArea() {
		setText(LOCATION_AREA, getPlayerLocation());
	}

	public void updateDialogueArea() {
		setText(DIALOGUE_AREA, mgVar.dialogue.getCurrentNpcDialogue());
	}

	public void updateResponseArea() {
		// if there is a response, updates, otherwise sets invisible
		if (mgVar.dialogue.getResponsesOrdered()[0] != null)
			setText(RESPONSE_1_AREA, mgVar.dialogue.getResponsesOrdered()[0].getResponse());
		else
			setText(RESPONSE_1_AREA, "");
		if (mgVar.dialogue.getResponsesOrdered()[1] != null) {
			setText(RESPONSE_2_AREA, mgVar.dialogue.getResponsesOrdered()[1].getResponse());
			mgVar.responseTwoArea.setVisible(true);
		} else {
			setText(RESPONSE_2_AREA, "");
			mgVar.responseTwoArea.setVisible(false);
		}
		if (mgVar.dialogue.getResponsesOrdered()[2] != null) {
			setText(RESPONSE_3_AREA, mgVar.dialogue.getResponsesOrdered()[2].getResponse());
			mgVar.responseThreeArea.setVisible(true);
		} else {
			setText(RESPONSE_3_AREA, "");
			mgVar.responseThreeArea.setVisible(false);
		}
	}

	public void initCombatMode() {
		mgVar.dialogue.checkForTriggeredEvent(); // why is this here???
		mgVar.globalColorValues = 0;
		// plays combat start sound
		mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
		// switch music to combat music
		mgVar.soundManager.stopBGM();
		mgVar.soundManager
				.loadBGM((mgVar.isLooney) ? SoundManager.BGM_LOON_BATTLE_MUSIC : SoundManager.BGM_GENERIC_BATTLE_MUSIC);
		mgVar.soundManager.playBGM();
		// load combat menu
		mgVar.combatMenu = new CombatMenu(mgVar.skin, mgVar.currentNPC, this);
		mgVar.gameState = COMBAT_FADE_IN;
		// set stage
		mgVar.stageCombat.addActor(mgVar.combatMenu.getCombatWindow());
		Gdx.input.setInputProcessor(mgVar.stageCombat);
		// so that it will fade in after combat is over (either with
		// room switch fade if victorious or respawn if not)
		mgVar.fadeInFromCombat = false;
	}

	////// GETTERS & SETTERS
	public int getCurrentNPC() {
		return mgVar.currentNPC;
	}

	public void setCurrentNPC(int newNPC) {
		mgVar.currentNPC = newNPC;
	}

	public void setGameState(int newState) {
		mgVar.gameState = newState;
	}

	public void setFadeSpeed(float newSpeed) {
		mgVar.fadeSpeed = newSpeed;
	}

	public int getGameState() {
		return mgVar.gameState;
	}

	public HashMap<String, ArrayList<String>> getCues() {
		return mgVar.cues;
	}

	public Stage getStage() {
		return mgVar.stage;
	}

	public Stage getStagePause() {
		return mgVar.stagePause;
	}

	public Conscientia getConscientia() {
		return mgVar.conscientia;
	}

	public Dialogue getDialogue() {
		return mgVar.dialogue;
	}

	public Stage getStageCombat() {
		return mgVar.stageCombat;
	}

	public boolean isInMindscape() {
		return mgVar.inMindscape;
	}

	public void setIsMindscape(boolean newState) {
		mgVar.inMindscape = newState;
	}

	public int getAwareness() {
		return mgVar.awareness;
	}

	public void setAwareness(int value) {
		mgVar.awareness = value;
	}

	public int[] getPersistentEvents() {
		return CommonVar.persistentEvents;
	}

	public Stage getStageMap() {
		return mgVar.stageMap;
	}

	public void setStageMap(Stage stageMap) {
		mgVar.stageMap = stageMap;
	}

	public SoundManager getSoundManager() {
		return mgVar.soundManager;
	}
}
