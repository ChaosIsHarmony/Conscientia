package com.ec.conscientia.entities;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.screens.MainGameScreen;

public class PauseMenu {

	private static Window pauseWindow;
	private static Window unpauseButtonWindow;
	private static ImageButton pauseHoverImg;
	private static ImageButton mapHoverImg;
	private static GlyphMenu glyphMenu;
	private static LogMenu logMenu;
	private static OptionsMenu optionsMenu;
	private static MindscapeMenu mindscapeMenu;
	private float height, width, heightMenu, widthMenu;

	public static final int PAUSE_WINDOW = 0, GLYPH_WINDOW = 1, LOG_WINDOW = 2, OPTIONS_WINDOW = 3,
			MINDSCAPE_WINDOW = 4, RESET_OPTIONS_WINDOW = 5, MAP_WINDOW = 6;
	public static int displayWindow;

	public MainGameScreen mgScr;

	public PauseMenu(final Skin skin, final MainGameScreen mgScr) {
		this.mgScr = mgScr;
		final PauseMenu pauseMenu = this;

		height = mgScr.getStagePause().getHeight() * .5f;
		width = mgScr.getStagePause().getWidth() * .75f;
		int numOfMenus = (mgScr.mgVar.hasIntrospection) ? 4 : 3;
		heightMenu = height * .75f;
		widthMenu = (width / (float) numOfMenus);

		pauseWindow = new Window("", skin);
		pauseWindow.setMovable(false);

		// set size and position on screen
		pauseWindow.setSize(width, height);
		pauseWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (pauseWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (pauseWindow.getHeight() / 2));

		// instantiate windows
		Window glyphWindow = new Window("", skin, "no_bg");
		glyphWindow.setMovable(false);
		Window logWindow = new Window("", skin, "no_bg");
		logWindow.setMovable(false);
		Window mindscapeWindow = new Window("", skin, "no_bg");
		mindscapeWindow.setMovable(false);
		Window optionsWindow = new Window("", skin, "no_bg");
		optionsWindow.setMovable(false);

		// add glyph menu
		ImageButtonStyle imgGlyphBS = new ImageButtonStyle();
		imgGlyphBS.up = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Glyphs Menu.png"))));
		imgGlyphBS.imageOver = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Menu BG.png"))));
		ImageButton glyphHoverImg = new ImageButton(imgGlyphBS);
		glyphHoverImg.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (mgScr.mgVar.hasGlyphs) {
					// plays click sound
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
					glyphMenu = new GlyphMenu(skin, mgScr, pauseMenu);
					setDisplayWindow(GLYPH_WINDOW);
				} else {
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_NEGATIVE);
				}
			}
		});
		glyphWindow.add(glyphHoverImg).width(widthMenu).height(heightMenu).space(20);

		// add log menu
		ImageButtonStyle imgLogBS = new ImageButtonStyle();
		imgLogBS.up = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Tomes Menu.png"))));
		imgLogBS.imageOver = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Menu BG.png"))));
		ImageButton logHoverImg = new ImageButton(imgLogBS);
		logHoverImg.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (mgScr.mgVar.hasLogs) {
					// plays click sound
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
					logMenu = new LogMenu(skin, mgScr, pauseMenu);
					setDisplayWindow(LOG_WINDOW);
				} else {
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_NEGATIVE);
				}
			}
		});
		logWindow.add(logHoverImg).width(widthMenu).height(heightMenu).space(20);

		// add specter menu, if specters have been acquired
		if (mgScr.mgVar.hasIntrospection) {
			ImageButtonStyle imgMindscapeBS = new ImageButtonStyle();
			imgMindscapeBS.up = new TextureRegionDrawable(
					new TextureRegion(new Texture(Gdx.files.internal("Menus/Mindscape Menu.png"))));
			imgMindscapeBS.imageOver = new TextureRegionDrawable(
					new TextureRegion(new Texture(Gdx.files.internal("Menus/Menu BG.png"))));
			ImageButton mindscapeHoverImg = new ImageButton(imgMindscapeBS);
			mindscapeHoverImg.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					// plays click sound
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
					mindscapeMenu = new MindscapeMenu(skin, mgScr, pauseMenu);
					setDisplayWindow(MINDSCAPE_WINDOW);
				}
			});
			mindscapeWindow.add(mindscapeHoverImg).width(widthMenu).height(heightMenu).space(20);
		}

		// add options menu
		ImageButtonStyle imgOptionsBS = new ImageButtonStyle();
		imgOptionsBS.up = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Options Menu.png"))));
		imgOptionsBS.imageOver = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("Menus/Menu BG.png"))));
		ImageButton optionsHoverImg = new ImageButton(imgOptionsBS);
		optionsHoverImg.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				optionsMenu = new OptionsMenu(skin, mgScr, pauseMenu);
				setDisplayWindow(OPTIONS_WINDOW);
			}
		});
		optionsWindow.add(optionsHoverImg).width(widthMenu).height(heightMenu).space(20);

		// add all things to window
		pauseWindow.add(glyphWindow).expand().fill();
		pauseWindow.add(logWindow).expand().fill();
		if (mgScr.mgVar.hasIntrospection)
			pauseWindow.add(mindscapeWindow).expand().fill();
		pauseWindow.add(optionsWindow).expand().fill();

		// unpause button
		unpauseButtonWindow = new Window("", skin, "no_bg");

		ImageButtonStyle imgPauseBS = new ImageButtonStyle();
		imgPauseBS.up = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("General/Pause Button.png"))));
		imgPauseBS.imageOver = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("General/Pause Button Hover.png"))));
		pauseHoverImg = new ImageButton(imgPauseBS);
		pauseHoverImg.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				mgScr.getStagePause().clear();
				mgScr.setGameState(mgScr.IN_PLAY);
				Gdx.input.setInputProcessor(mgScr.getStage());
			}
		});

		// maps button
		if (mgScr.mgVar.hasMaps) {
			ImageButtonStyle imgmapBS = new ImageButtonStyle();
			imgmapBS.up = new TextureRegionDrawable(
					new TextureRegion(new Texture(Gdx.files.internal("General/MapIcon.png"))));
			imgmapBS.imageOver = new TextureRegionDrawable(
					new TextureRegion(new Texture(Gdx.files.internal("General/MapIconDown.png"))));
			mapHoverImg = new ImageButton(imgmapBS);
			mapHoverImg.addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					try {
						// plays click sound
						mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
						mgScr.genDispWin.generateMap(true, "", null, 0);
						mgScr.setGameState(mgScr.MAP);
						setDisplayWindow(MAP_WINDOW);
						// stageMap.addActor();
						Gdx.input.setInputProcessor(mgScr.getStageMap());
					} catch (Exception e) {
						// plays click sound
						mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_NEGATIVE);
					}
				}
			});
		}

		if (mgScr.mgVar.hasMaps) {
			unpauseButtonWindow.setSize(mgScr.getStage().getWidth(), mgScr.getStage().getHeight() / 6);
			unpauseButtonWindow.setPosition(0, 15);
			unpauseButtonWindow.add(pauseHoverImg)
					.size(mgScr.getStage().getWidth() / 9, mgScr.getStage().getHeight() / 6).padRight(494);
			unpauseButtonWindow.add(mapHoverImg).size(mgScr.getStage().getWidth() / 9, mgScr.getStage().getHeight() / 6)
					.padLeft(494);
		} else
			unpauseButtonWindow.add(pauseHoverImg).size(mgScr.getStage().getWidth() / 9,
					mgScr.getStage().getHeight() / 6);

	}

	public Window getPauseWindow() {
		return pauseWindow;
	}

	public void setDisplayWindow(int windowToDisplay) {
		mgScr.getStagePause().clear();
		if (windowToDisplay == RESET_OPTIONS_WINDOW) {
			optionsMenu = null;
			optionsMenu = new OptionsMenu(new Skin(Gdx.files.internal("Skin/ConscientiaSkin.json")), mgScr, this);
			displayWindow = OPTIONS_WINDOW;
		} else
			displayWindow = windowToDisplay;
		mgScr.getStagePause().addActor(getDisplayWindow());
		if (displayWindow == PAUSE_WINDOW || displayWindow == OPTIONS_WINDOW)
			mgScr.getStagePause().addActor(getUnpauseButton());
	}

	public Window getDisplayWindow() {
		switch (displayWindow) {
		case PAUSE_WINDOW:
			return pauseWindow;
		case GLYPH_WINDOW:
			return glyphMenu.getGlyphWindow();
		case LOG_WINDOW:
			return logMenu.getLogWindow();
		case OPTIONS_WINDOW:
			return optionsMenu.getOptionsWindow();
		case MINDSCAPE_WINDOW:
			return mindscapeMenu.getMindscapeNPCWindow();
		case RESET_OPTIONS_WINDOW:
		case MAP_WINDOW:
		default:
			return null;
		}

	}

	public Window getUnpauseButton() {
		return unpauseButtonWindow;
	}
}
