package com.ec.conscientia.ingameoperations;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Location;
import com.ec.conscientia.entities.PauseMenu;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class GenerateDisplayWin {
	MainGameScreen mgScr;

	public GenerateDisplayWin(MainGameScreen mgScr) { this.mgScr = mgScr; }

	public Table createTable() {
		// location area
		mgScr.mgVar.locationArea = new TextButton("", mgScr.mgVar.skin, "locArea");
		mgScr.mgVar.locationArea.align(Align.center);
		mgScr.mgVar.locationArea.setTouchable(Touchable.disabled);

		// dialogue area
		setDialogueLabels();
		mgScr.mgVar.dialogueLabel.setWrap(true);
		mgScr.mgVar.dialogueLabel.setAlignment(Align.topLeft, Align.topLeft);
		mgScr.mgVar.npcDialogueLabel.setWrap(true);
		mgScr.mgVar.npcDialogueLabel.setAlignment(Align.topLeft, Align.topLeft);

		mgScr.mgVar.dialogueArea = new Window("", mgScr.mgVar.skin, "no_bg");
		mgScr.mgVar.dialogueArea.setMovable(false);

		mgScr.mgVar.canScrollButton = new TextButton(" \\/ ", mgScr.mgVar.skin, "no_bg");
		mgScr.mgVar.canScrollButton.setTouchable(Touchable.disabled);
		mgScr.mgVar.canScrollButton.addListener(new ClickListener() {
			// advances scroll by 50%
			public void clicked(InputEvent event, float x, float y) {
				mgScr.mgVar.canScrollButton.setText(" \\\\// ");

				mgScr.mgVar.dialogueAreaScrollPane.setScrollY(mgScr.mgVar.dialogueAreaScrollPane.getScrollY()
						+ (mgScr.mgVar.dialogueAreaScrollPane.getHeight() * .86f));
			}

			// doubles up on the scroll area when focused
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				mgScr.mgVar.canScrollButton.setText(" \\\\// ");
			}

			// resets to normal if not focused
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				mgScr.mgVar.canScrollButton.setText(" \\/ ");
			}
		});

		// non NPC
		mgScr.mgVar.NonNPCdialogueArea = new Window("", mgScr.mgVar.skin);
		mgScr.mgVar.NonNPCdialogueArea.setMovable(false);
		mgScr.mgVar.NonNPCdialogueArea.setSize(mgScr.mgVar.dialogueArea.getWidth(),
				mgScr.mgVar.dialogueArea.getHeight());

		// NPC dialogue area
		mgScr.mgVar.NPCdialogueArea = new Window("", mgScr.mgVar.skin, "no_bg");
		mgScr.mgVar.NPCdialogueArea.setMovable(false);
		mgScr.mgVar.NPCdialogueArea.setSize(mgScr.mgVar.dialogueArea.getWidth(), mgScr.mgVar.dialogueArea.getHeight());

		// scrollpanes
		mgScr.mgVar.dialogueAreaScrollPane = new ScrollPane(mgScr.mgVar.dialogueLabel, mgScr.mgVar.skin, "no_bg");
		mgScr.mgVar.dialogueAreaScrollPane.setScrollPercentY(0f);
		mgScr.mgVar.NPCdialogueAreaScrollPane = new ScrollPane(mgScr.mgVar.npcDialogueLabel, mgScr.mgVar.skin);
		mgScr.mgVar.NPCdialogueAreaScrollPane.setScrollPercentY(0f);

		mgScr.mgVar.NonNPCdialogueArea.add(mgScr.mgVar.dialogueAreaScrollPane).align(Align.topLeft).fill().expand()
				.row();
		mgScr.mgVar.NonNPCdialogueArea.add(mgScr.mgVar.canScrollButton).align(Align.center).expandX();

		createDialogueArea();

		// responses area
		setResponseAreaLabels();

		mgScr.mgVar.responseOneArea.align(Align.center);
		mgScr.mgVar.responseOneArea.setTouchable(Touchable.disabled);
		mgScr.mgVar.responseOneArea.addListener(new ClickListener() {
			// selects first response
			public void clicked(InputEvent event, float x, float y) {
				mgScr.selectResponse(0);
			}

			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				mgScr.mgVar.responseOneArea.getLabel().setColor(1, 0, 0, 1);
			}

			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				mgScr.mgVar.responseOneArea.getLabel().setColor(1, 1, 1, 1);
			}
		});

		mgScr.mgVar.responseTwoArea.align(Align.center);
		mgScr.mgVar.responseTwoArea.setTouchable(Touchable.disabled);
		mgScr.mgVar.responseTwoArea.addListener(new ClickListener() {
			// selects second response
			public void clicked(InputEvent event, float x, float y) {
				mgScr.selectResponse(1);
			}

			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				mgScr.mgVar.responseTwoArea.getLabel().setColor(1, 0, 0, 1);
			}

			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				mgScr.mgVar.responseTwoArea.getLabel().setColor(1, 1, 1, 1);
			}
		});

		mgScr.mgVar.responseThreeArea.align(Align.center);
		mgScr.mgVar.responseThreeArea.setTouchable(Touchable.disabled);
		mgScr.mgVar.responseThreeArea.addListener(new ClickListener() {
			// selects third response
			public void clicked(InputEvent event, float x, float y) {
				mgScr.selectResponse(2);
			}

			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				mgScr.mgVar.responseThreeArea.getLabel().setColor(1, 0, 0, 1);
			}

			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				mgScr.mgVar.responseThreeArea.getLabel().setColor(1, 1, 1, 1);
			}
		});

		// create table
		mgScr.mgVar.table = new Table();
		mgScr.mgVar.table.setBounds(0, 0, mgScr.mgVar.stage.getWidth(), mgScr.mgVar.stage.getHeight());
		mgScr.mgVar.table.setFillParent(true);
		mgScr.mgVar.table.align(Align.center | Align.center);
		mgScr.mgVar.table.add(mgScr.mgVar.locationArea).minSize(CommonVar.GAME_WIDTH * .1f,
				CommonVar.GAME_HEIGHT * .1f);
		mgScr.mgVar.table.getCell(mgScr.mgVar.locationArea).expandX().pad(5);
		mgScr.mgVar.table.row();
		mgScr.mgVar.table.add(mgScr.mgVar.dialogueArea).size(CommonVar.GAME_WIDTH * .9f, CommonVar.GAME_HEIGHT * .6f);
		mgScr.mgVar.table.getCell(mgScr.mgVar.dialogueArea).pad(5);
		mgScr.mgVar.table.row();
		mgScr.mgVar.table.add(mgScr.mgVar.responseOneArea).minSize(CommonVar.GAME_WIDTH * .7f,
				CommonVar.GAME_HEIGHT * .05f);
		mgScr.mgVar.table.getCell(mgScr.mgVar.responseOneArea).expandX().pad(5);
		mgScr.mgVar.table.row();
		mgScr.mgVar.table.add(mgScr.mgVar.responseTwoArea).minSize(CommonVar.GAME_WIDTH * .7f,
				CommonVar.GAME_HEIGHT * .05f);
		mgScr.mgVar.table.getCell(mgScr.mgVar.responseTwoArea).expandX().pad(5);
		mgScr.mgVar.table.row();
		mgScr.mgVar.table.add(mgScr.mgVar.responseThreeArea).minSize(CommonVar.GAME_WIDTH * .7f,
				CommonVar.GAME_HEIGHT * .05f);
		mgScr.mgVar.table.getCell(mgScr.mgVar.responseThreeArea).expandX().pad(5);
		mgScr.mgVar.table.row();

		return mgScr.mgVar.table;
	}

	public void setResponseAreaLabels() {
		if (mgScr.mgVar.responseOneArea != null && mgScr.mgVar.conscientia.isUseAltFont()) {
			TextButtonStyle resAreaStyle = new TextButtonStyle();
			resAreaStyle.font = mgScr.mgVar.skin.getFont("open_sans_font_24");
			resAreaStyle.fontColor = mgScr.mgVar.responseOneArea.getStyle().fontColor;
			resAreaStyle.up = mgScr.mgVar.responseOneArea.getStyle().up;
			resAreaStyle.over = mgScr.mgVar.responseOneArea.getStyle().over;
			resAreaStyle.down = new NinePatchDrawable(
					mgScr.loadingUtils.processNinePatchFile("Skin/ResponseAreaDown.9.png"));
			mgScr.mgVar.responseOneArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseTwoArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseThreeArea.setStyle(resAreaStyle);
		} else if (mgScr.mgVar.responseOneArea != null) {
			TextButtonStyle resAreaStyle = new TextButtonStyle();
			resAreaStyle.font = mgScr.mgVar.skin.getFont("Amerika_Sans_24");
			resAreaStyle.fontColor = mgScr.mgVar.responseOneArea.getStyle().fontColor;
			resAreaStyle.up = mgScr.mgVar.responseOneArea.getStyle().up;
			resAreaStyle.over = mgScr.mgVar.responseOneArea.getStyle().over;
			resAreaStyle.down = new NinePatchDrawable(
					mgScr.loadingUtils.processNinePatchFile("Skin/ResponseAreaDown.9.png"));
			mgScr.mgVar.responseOneArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseTwoArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseThreeArea.setStyle(resAreaStyle);
		} else if (mgScr.mgVar.conscientia.isUseAltFont()) {
			mgScr.mgVar.responseOneArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			mgScr.mgVar.responseTwoArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			mgScr.mgVar.responseThreeArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			TextButtonStyle resAreaStyle = new TextButtonStyle();
			resAreaStyle.font = mgScr.mgVar.skin.getFont("open_sans_font_24");
			resAreaStyle.fontColor = mgScr.mgVar.responseOneArea.getStyle().fontColor;
			resAreaStyle.up = mgScr.mgVar.responseOneArea.getStyle().up;
			resAreaStyle.over = mgScr.mgVar.responseOneArea.getStyle().over;
			resAreaStyle.down = new NinePatchDrawable(
					mgScr.loadingUtils.processNinePatchFile("Skin/ResponseAreaDown.9.png"));
			mgScr.mgVar.responseOneArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseTwoArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseThreeArea.setStyle(resAreaStyle);
		} else {
			mgScr.mgVar.responseOneArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			mgScr.mgVar.responseTwoArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			mgScr.mgVar.responseThreeArea = new TextButton("", mgScr.mgVar.skin, "resArea");
			TextButtonStyle resAreaStyle = new TextButtonStyle();
			resAreaStyle.font = mgScr.mgVar.responseOneArea.getStyle().font;
			resAreaStyle.fontColor = mgScr.mgVar.responseOneArea.getStyle().fontColor;
			resAreaStyle.up = mgScr.mgVar.responseOneArea.getStyle().up;
			resAreaStyle.over = mgScr.mgVar.responseOneArea.getStyle().over;
			resAreaStyle.down = new NinePatchDrawable(
					mgScr.loadingUtils.processNinePatchFile("Skin/ResponseAreaDown.9.png"));
			mgScr.mgVar.responseOneArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseTwoArea.setStyle(resAreaStyle);
			mgScr.mgVar.responseThreeArea.setStyle(resAreaStyle);
		}
	}

	public void setDialogueLabels() {
		if (mgScr.mgVar.dialogueLabel != null && mgScr.mgVar.conscientia.isUseAltFont()) {
			LabelStyle ls = mgScr.loadingUtils.getLabelStyle(28);
			mgScr.mgVar.dialogueLabel.setStyle(ls);
			mgScr.mgVar.npcDialogueLabel.setStyle(ls);
		} else if (mgScr.mgVar.dialogueLabel != null) {
			LabelStyle dls = new LabelStyle(mgScr.mgVar.skin.getFont("Amerika_Sans_28"), Color.WHITE);
			mgScr.mgVar.dialogueLabel.setStyle(dls);
			mgScr.mgVar.npcDialogueLabel.setStyle(dls);
		} else if (mgScr.mgVar.conscientia.isUseAltFont()) {
			LabelStyle ls = mgScr.loadingUtils.getLabelStyle(28);
			mgScr.mgVar.dialogueLabel = new Label("", mgScr.mgVar.skin);
			mgScr.mgVar.dialogueLabel.setStyle(ls);
			mgScr.mgVar.npcDialogueLabel = new Label("", mgScr.mgVar.skin);
			mgScr.mgVar.npcDialogueLabel.setStyle(ls);
		} else {
			mgScr.mgVar.dialogueLabel = new Label("", mgScr.mgVar.skin);
			mgScr.mgVar.npcDialogueLabel = new Label("", mgScr.mgVar.skin);
		}
	}

	public void createDialogueArea() {
		if (mgScr.mgVar.currentNPC != mgScr.mgVar.newNPC) {
			mgScr.mgVar.newNPC = mgScr.mgVar.currentNPC;
			mgScr.mgVar.dialogueArea.clear();
			mgScr.mgVar.dialogueArea.setSize(CommonVar.GAME_WIDTH * .9f, CommonVar.GAME_HEIGHT * .6f);
			if (mgScr.mgVar.currentNPC == 9999) {
				mgScr.mgVar.dialogueArea.add(mgScr.mgVar.NonNPCdialogueArea).align(Align.topLeft).fill().expand();
			} else {
				mgScr.mgVar.NPCdialogueArea = new Window("", mgScr.mgVar.skin, "no_bg");
				mgScr.mgVar.NPCdialogueArea.setMovable(false);
				mgScr.mgVar.NPCdialogueArea.setSize(mgScr.mgVar.dialogueArea.getWidth(),
						mgScr.mgVar.dialogueArea.getHeight());

				Window NPCavatarArea = new Window("", mgScr.mgVar.skin, "no_bg");
				NPCavatarArea.setSize(mgScr.mgVar.NPCdialogueArea.getWidth() / 4,
						mgScr.mgVar.NPCdialogueArea.getHeight());

				// NPC name
				TextButton npcArea;
				if (mgScr.getConscientia().isUseAltFont()) {
					npcArea = new TextButton(mgScr.mgVar.dialogue.getCurrentNPC().getName(), mgScr.mgVar.skin);
					npcArea.setStyle(mgScr.loadingUtils.getTextButtonStyle("npcArea"));
				}
				else
					npcArea = new TextButton(mgScr.mgVar.dialogue.getCurrentNPC().getName(), mgScr.mgVar.skin,
						"npcArea");
				npcArea.align(Align.center);
				npcArea.setTouchable(Touchable.disabled);

				// NPC avatar
				Window avatarWindow = new Window("", mgScr.mgVar.skin, "no_bg");
				ImageButtonStyle imgNPCavatar = new ImageButtonStyle();
				imgNPCavatar.up = mgScr.mgVar.dialogue.getCurrentNPC().getImg();
				ImageButton NPCavatar = new ImageButton(imgNPCavatar);
				avatarWindow.add(NPCavatar).grow();

				NPCavatarArea.add(npcArea).size(NPCavatarArea.getWidth(), NPCavatarArea.getHeight() / 6).row();
				// adds a combat window if you can fight the NPC
				if (mgScr.loadingUtils.getGenerateFightButton()) {
					NPCavatarArea.add(avatarWindow).size(NPCavatarArea.getWidth(), (4 * NPCavatarArea.getHeight()) / 6)
							.grow().row();
					NPCavatarArea.add(createFightButton(NPCavatarArea.getWidth(), NPCavatarArea.getHeight(), true))
							.size(NPCavatarArea.getWidth(), NPCavatarArea.getHeight() / 6).grow();
				} else {
					NPCavatarArea.add(avatarWindow).size(NPCavatarArea.getWidth(), (4 * NPCavatarArea.getHeight()) / 6)
							.grow().row();
					NPCavatarArea.add(createFightButton(NPCavatarArea.getWidth(), NPCavatarArea.getHeight(), false))
							.size(NPCavatarArea.getWidth(), NPCavatarArea.getHeight() / 6).grow();
				}

				mgScr.mgVar.NPCdialogueArea.add(NPCavatarArea).align(Align.left)
						.size(mgScr.mgVar.NPCdialogueArea.getWidth() / 4, mgScr.mgVar.NPCdialogueArea.getHeight())
						.grow();
				mgScr.mgVar.NPCdialogueArea.add(mgScr.mgVar.NPCdialogueAreaScrollPane).align(Align.left)
						.size((2.99f * mgScr.mgVar.NPCdialogueArea.getWidth()) / 4,
								.715f * mgScr.mgVar.NPCdialogueArea.getHeight())
						.grow();

				mgScr.mgVar.dialogueArea.add(mgScr.mgVar.NPCdialogueArea).align(Align.topLeft).fill().expand();
			}
		}
	}

	public Window createLogoPauseButton(boolean immediateActivation) {
		mgScr.mgVar.pauseButtonWindow = new Window("", mgScr.mgVar.skin, "no_bg");

		ImageButtonStyle imgPauseBS = new ImageButtonStyle();
		imgPauseBS.up = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("General/Pause Button.png"))));
		imgPauseBS.imageOver = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("General/Pause Button Hover.png"))));
		mgScr.mgVar.pauseHoverImg = new ImageButton(imgPauseBS);
		mgScr.mgVar.pauseHoverImg.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.scrFX.resetNormalWorldColors();
				// plays click sound
				mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
				mgScr.mgVar.gameState = mgScr.PAUSED;

				mgScr.mgVar.pauseMenu = new PauseMenu(mgScr.mgVar.skin, mgScr.mgVar.mgScr);
				mgScr.mgVar.stagePause.addActor(mgScr.mgVar.pauseMenu.getPauseWindow());
				mgScr.mgVar.stagePause.addActor(mgScr.mgVar.pauseMenu.getUnpauseButton());
				Gdx.input.setInputProcessor(mgScr.mgVar.stagePause);
			}
		});
		// Disabled until fade in complete
		mgScr.mgVar.pauseHoverImg.setTouchable((immediateActivation) ? Touchable.enabled : Touchable.disabled);

		mgScr.mgVar.pauseButtonWindow.add(mgScr.mgVar.pauseHoverImg).size(mgScr.mgVar.stage.getWidth() / 9,
				mgScr.mgVar.stage.getHeight() / 6);
		return mgScr.mgVar.pauseButtonWindow;
	}

	public Actor createStats() {
		mgScr.mgVar.statsWindow = new Window("", mgScr.mgVar.skin, "no_bg");
		// for debugging
		mgScr.mgVar.stats = new Label("", mgScr.mgVar.skin);

		mgScr.mgVar.statsWindow.setSize(mgScr.mgVar.stage.getWidth() / 10, mgScr.mgVar.stage.getHeight() / 6);
		mgScr.mgVar.statsWindow.setPosition(mgScr.mgVar.stage.getWidth() - mgScr.mgVar.statsWindow.getWidth(), 15);
		mgScr.mgVar.statsWindow.add(mgScr.mgVar.stats).size(mgScr.mgVar.statsWindow.getWidth(),
				mgScr.mgVar.statsWindow.getHeight());
		// set true for debugging
		mgScr.mgVar.statsWindow.setVisible(true);

		return mgScr.mgVar.statsWindow;
	}

	public TextButton createFightButton(float w, float h, boolean activate) {
		final TextButton fightButton;
		if (mgScr.getConscientia().isUseAltFont()) {
			fightButton = new TextButton("", mgScr.mgVar.skin);
			fightButton.setStyle(mgScr.loadingUtils.getTextButtonStyle("npcArea"));
		}
		else
			fightButton = new TextButton("", mgScr.mgVar.skin,"npcArea");

		if (activate) {
			fightButton.getLabel().setText("FIGHT!");
			fightButton.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					mgScr.initCombatMode();
				}
			});
		} else
			fightButton.setTouchable(Touchable.disabled);


		return fightButton;
	}

	public void generateMap(final boolean topLevel, final String areaName, Location loc, float scrollPercent) {
		mgScr.setStageMap(new Stage(mgScr.viewport, mgScr.batch));

		// Table layout for whole screen
		final Window wholeScreenWin = new Window("", mgScr.mgVar.skin, "no_bg");
		wholeScreenWin.setMovable(false);
		wholeScreenWin.setSize(mgScr.getStageMap().getWidth() * .9f, mgScr.getStageMap().getHeight() * .9f);
		wholeScreenWin.setPosition((mgScr.getStageMap().getWidth() / 2) - (wholeScreenWin.getWidth() / 2),
				(mgScr.getStageMap().getHeight() / 2) - (wholeScreenWin.getHeight() / 2));

		// Area name window
		int endInd = (topLevel) ? mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!")
				: mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!",
						mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1);
		String areaNameStr = (topLevel) ? mgScr.mgVar.dialogue.getCurrentAddress().substring(0, endInd) : areaName;
		final TextField areaNameLabel = new TextField(areaNameStr, mgScr.mgVar.skin, "title_small");
		areaNameLabel.setDisabled(true);

		// Map window
		final Window mapWin = new Window("", mgScr.mgVar.skin);
		mapWin.setMovable(false);
		ArrayList<Location> tempList = mgScr.mgVar.fileRW.reader.getMapLocation(mgScr.mgVar.conscientia.getConscVar().bookID,
				areaNameStr);
		final TextButton[] areaButtonList = new TextButton[tempList.size()];
		int[] coordX = new int[tempList.size()];
		int[] coordY = new int[tempList.size()];
		int ind = 0, lowestIDnum = 100; // lastIDnum is to see which one to
										// autoselect

		// checks for lowest ID number for autoselect
		for (Location l : tempList)
			if (l.getID() < lowestIDnum)
				lowestIDnum = l.getID();

		// populates button list and sets coords
		for (final Location l : tempList) {
			coordX[ind] = l.getCoords()[0];
			coordY[ind] = l.getCoords()[1];
			final TextButton tb = new TextButton(l.getID() + "", mgScr.mgVar.skin, "no_bg");
			TextButtonStyle ts = new TextButtonStyle();
			ts.font = tb.getStyle().font;
			ts.downFontColor = tb.getStyle().downFontColor;
			ts.overFontColor = tb.getStyle().overFontColor;
			// highlights area you're in
			if (topLevel && mgScr.mgVar.dialogue.getCurrentAddress()
					.substring(mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1,
							mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!",
									mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1))
					.equals(l.getAreaName())) {
				ts.up = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileURHere.png"));
				ts.fontColor = tb.getStyle().overFontColor;
			} else if (mgScr.mgVar.dialogue.getCurrentAddress()
					.substring(
							mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!",
									mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1) + 1,
							mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!",
									mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!",
											mgScr.mgVar.dialogue.getCurrentAddress().indexOf("!") + 1) + 1))
					.equals(l.getAreaName())) {
				ts.up = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileURHere.png"));
				ts.fontColor = tb.getStyle().overFontColor;
			} else {
				if (mgScr.mgVar.isSelected && mgScr.mgVar.selectedAreaName.equals(l.getAreaName())) {
					ts.up = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileSelected.png"));
					mgScr.mgVar.isSelected = false;
					mgScr.mgVar.selectedAreaName = "";
				} else if (!mgScr.mgVar.isSelected && l.getID() == lowestIDnum)
					ts.up = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileSelected.png"));
				else
					ts.up = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileUp.png"));
				ts.fontColor = tb.getStyle().fontColor;
			}

			ts.over = new NinePatchDrawable(mgScr.loadingUtils.processNinePatchFile("Skin/mapTileOver.png"));
			tb.setStyle(ts);
			if (topLevel)
				tb.addListener(new ClickListener() {
					public void clicked(InputEvent event, float x, float y) {
						// plays click sound
						mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
						mgScr.getStageMap().clear();
						generateMap(false, l.getAreaName(), null, 0);
					}
				});
			else
				tb.setTouchable(Touchable.disabled);
			areaButtonList[ind++] = tb;
		}

		// add textbutton numbers to map
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				boolean addBlank = true;
				for (int k = 0; k < coordY.length; k++) {
					if (coordY[k] == i && coordX[k] == j) {
						mapWin.add(areaButtonList[k]).size((mapWin.getWidth() / 2), (mapWin.getHeight() / 2)).fill()
								.expand().grow();
						addBlank = false;
					}
				}
				if (i == 8 && j == 0)
					mapWin.add(new TextButton("/\\", mgScr.mgVar.skin, "no_bg"))
							.size(mapWin.getWidth() / 2, mapWin.getHeight() / 2).fill().expand().grow();
				else if (i == 9 && j == 0)
					mapWin.add(new TextButton("U", mgScr.mgVar.skin, "open_sans_no_bg"))
							.size(mapWin.getWidth() / 2, mapWin.getHeight() / 2).fill().expand().grow();
				else if (addBlank)
					mapWin.add(new TextButton(" ", mgScr.mgVar.skin, "no_bg"))
							.size(mapWin.getWidth() / 2, mapWin.getHeight() / 2).fill().expand().grow();
			}
			mapWin.row();
		}

		// Room name list window
		Window listAreaWin = new Window("", mgScr.mgVar.skin, "no_bg");
		listAreaWin.setMovable(false);
		final List<Location> list = mgScr.loadingUtils.getMapList(areaName, tempList);
		if (loc != null) {
			for (Location location : list.getItems())
				if (loc.getAreaName().equals(location.getAreaName()))
					list.setSelected(location);
		}

		final ScrollPane listPane = new ScrollPane(list, mgScr.mgVar.skin);
		listPane.layout();
		listPane.setScrollY(scrollPercent);
		listPane.addListener(new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				// Highlights area on map on single click
				mgScr.mgVar.isSelected = true;
				mgScr.mgVar.selectedAreaName = list.getSelected().getAreaName();
				generateMap(topLevel, areaName, list.getSelected(), listPane.getScrollY());
			}
		});

		// resume button
		final TextButton resume;
		if (mgScr.mgVar.conscientia.isUseAltFont()) {
			resume = new TextButton("Resume", mgScr.mgVar.skin);
			resume.setStyle(mgScr.loadingUtils.getTextButtonStyle("default"));
		}
		else
			resume = new TextButton("Resume", mgScr.mgVar.skin);
		resume.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
				mgScr.getStageMap().clear();
				mgScr.setGameState(mgScr.IN_PLAY);
				Gdx.input.setInputProcessor(mgScr.getStage());
			}
		});

		final TextButton back;
		if (mgScr.mgVar.conscientia.isUseAltFont()) {
			back = new TextButton("Prev", mgScr.mgVar.skin);
			back.setStyle(mgScr.loadingUtils.getTextButtonStyle("default"));
		}
		else
			back = new TextButton("Prev", mgScr.mgVar.skin);
		back.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
				generateMap(true, "", null, 0);
			}
		});

		Window buttonArea = new Window("", mgScr.mgVar.skin, "no_bg");
		buttonArea.setMovable(false);
		buttonArea.add(resume).size((wholeScreenWin.getWidth() / 3) / 2, wholeScreenWin.getHeight() * .1f);
		buttonArea.add(back).size((wholeScreenWin.getWidth() / 3) / 2, wholeScreenWin.getHeight() * .1f);

		listAreaWin.add(areaNameLabel).grow().row();
		listAreaWin.add(listPane).grow().row();
		listAreaWin.add(buttonArea).fill();

		wholeScreenWin.add(listAreaWin).align(Align.left).grow().size(wholeScreenWin.getWidth() / 3,
				wholeScreenWin.getHeight() * .9f);
		wholeScreenWin.add(mapWin).align(Align.right).grow().size((2 * wholeScreenWin.getWidth()) / 3,
				wholeScreenWin.getHeight() * .9f);

		// populate stage
		mgScr.getStageMap().addActor(wholeScreenWin);
		// sets the stage as the input processor
		Gdx.input.setInputProcessor(mgScr.getStageMap());
	}
}
