package com.ec.conscientia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.FileReaderWriter;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.variables.CommonVar;

public class EndCreditsScreen implements Screen {
	private SpriteBatch batch;
	private SoundManager soundManager;
	private Conscientia conscientia;
	private FileReaderWriter fileRW;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	private ScrollPane scrollPane;

	private static final int LOGO_SYMBOL_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_SYMBOL_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);
	private static final int LOGO_WORDS_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_WORDS_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);
	private static final int CIH_LOGO_WIDTH = (int) (CommonVar.GAME_WIDTH * 0.75);
	private static final int CIH_LOGO_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);

	private static final float creditRollSpeed = .45f;

	private Sprite logoSymbol, logoWords, cihLogo;

	private float creditsY;

	private boolean tappedOnce = false, tappedTwice = false;

	public EndCreditsScreen(SoundManager soundMan, Conscientia conscientia) {
		// hide cursor
		// Gdx.input.setCursorCatched(true);

		this.batch = new SpriteBatch();
		this.fileRW = new FileReaderWriter(conscientia);
		this.soundManager = soundMan;
		this.conscientia = conscientia;
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(CommonVar.GAME_WIDTH, CommonVar.GAME_HEIGHT, camera);
		this.stage = new Stage(viewport, batch);
		this.skin = new Skin(Gdx.files.internal("Skin/ConscientiaSkin.json"));
		this.camera.position.set(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2, 0);

		// Sprites instead of textures so that we can fade in individually
		logoSymbol = new Sprite(new Texture("General\\AO_center.png"));
		logoSymbol.setAlpha(0);
		logoSymbol.setPosition(0, 0);
		logoSymbol.setSize(LOGO_SYMBOL_WIDTH, LOGO_SYMBOL_HEIGHT);
		logoWords = new Sprite(new Texture("General\\Conscientia.png"));
		logoWords.setAlpha(0);
		logoWords.setPosition((CommonVar.GAME_WIDTH / 2) - (LOGO_WORDS_WIDTH / 2), 0);
		logoWords.setSize(LOGO_WORDS_WIDTH, LOGO_WORDS_HEIGHT);
		cihLogo = new Sprite(new Texture("General\\LOGO_Alternate.png"));
		cihLogo.setAlpha(0);
		cihLogo.setPosition((CommonVar.GAME_WIDTH / 2) - (CIH_LOGO_WIDTH / 2), 0);
		cihLogo.setSize(CIH_LOGO_WIDTH, CIH_LOGO_HEIGHT);

		// create table
		Table table = new Table();
		table.setFillParent(true);
		table.setPosition(0, 0);

		// credits position
		creditsY = 0f;

		// Loads credit file contents to label for scrolling
		scrollPane = new ScrollPane(new Label(fileRW.loadCredits(), skin), skin, "no_bg_no_bar");
		scrollPane.setScrollPercentY(creditsY);

		table.add().width((2 * CommonVar.GAME_WIDTH) / 9);
		table.add(scrollPane).pad(25).size((5 * CommonVar.GAME_WIDTH) / 9, CommonVar.GAME_HEIGHT * .9f).center();
		table.add().width((2 * CommonVar.GAME_WIDTH) / 9);

		stage.addActor(table);
		stage.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!tappedOnce)
					tappedOnce = true;
				else if (!tappedTwice)
					tappedTwice = true;
				else if (tappedTwice) {
					fadeInComplete = true;
					fadeOutComplete = true;
				}
			}
		});

		// sets the stage as the input processor
		Gdx.input.setInputProcessor(stage);

		// music
		soundManager.setFadeIn(true);
		soundManager.loadBGM(SoundManager.BGM_CREDITS);
		soundManager.playNonLoopingBGM();

		// will display a quote of Biracul and update all saved games
		MainMenuScreen.savesUpdated = false;
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(globalColorValues, globalColorValues, globalColorValues, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// sets center
		batch.setProjectionMatrix(camera.combined);

		// draws background
		batch.begin();

		logoSymbol.draw(batch);
		logoWords.draw(batch);
		cihLogo.draw(batch);

		batch.end();

		stage.act();
		stage.draw();
	}

	public void update(float delta) {
		// deals with music fade
		if (soundManager.getFadeIn())
			soundManager.fadeInBGM();
		else if (soundManager.getFadeOut())
			soundManager.fadeOutBGM();

		// rolls credits
		scrollPane.setScrollY((creditsY += creditRollSpeed));

		// fades in
		if (!fadeInComplete)
			fadeIn(delta);
		// fades out and switches to MainScreen
		else if (!fadeOutComplete) {
			fadeOut(delta);
		} else {
			// stop current bgm
			soundManager.stopBGM();
			soundManager.setFadeOut(false);
			soundManager.setFadeIn(true);
			conscientia.changeScreen(CommonVar.MAIN_MENU, true, 0);
		}
	}

	private float fadeSpeed = .005f;
	private float globalColorValues = 0;
	private float logoSymAlpha = 0, logoWordsAlpha = 0, cihLogoAlpha = 0;
	private boolean fadeInComplete = false, fadeOutComplete = false, musicFade = false;

	// fades bg imgs in
	private void fadeIn(float delta) {
		if (logoSymAlpha < 1) {
			logoSymAlpha = ((logoSymAlpha += fadeSpeed * delta) > 1) ? 1 : logoSymAlpha;
			logoSymbol.setAlpha(logoSymAlpha);
		} else if (logoWordsAlpha < 1) {
			logoWordsAlpha = ((logoWordsAlpha += (fadeSpeed * 10) * delta) > 1) ? 1 : logoWordsAlpha;
			logoWords.setAlpha(logoWordsAlpha);
		}

		fadeInComplete = (logoWordsAlpha == 1) ? true : false;
	}

	private void fadeOut(float delta) {
		if (logoWordsAlpha > 0) {
			logoSymAlpha = ((logoSymAlpha -= fadeSpeed * 8 * delta) < 0) ? 0 : logoSymAlpha;
			logoWordsAlpha = ((logoWordsAlpha -= fadeSpeed * 4 * delta) < 0) ? 0 : logoWordsAlpha;

			logoSymbol.setAlpha(logoSymAlpha);
			logoWords.setAlpha(logoWordsAlpha);
		} else if (cihLogoAlpha < 1) {
			cihLogoAlpha = ((cihLogoAlpha += fadeSpeed * 8 * delta) > 1) ? 1 : cihLogoAlpha;
			cihLogo.setAlpha(cihLogoAlpha);
		} else if (!musicFade) {
			musicFade = true;
			soundManager.setFadeOut(true);
		} else if (globalColorValues < 1) {
			soundManager.longFadeOutBGM();
			fadeSpeed *= 1.05f;
			cihLogo.setAlpha((1 - globalColorValues > 0) ? 1 - globalColorValues : 0);
			globalColorValues = ((globalColorValues += fadeSpeed * delta) > 1) ? 1 : globalColorValues;
		}
		fadeOutComplete = (globalColorValues == 1) ? true : false;
	}

	/////////////////////////////////////////////////////////////
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		viewport.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		logoSymbol.getTexture().dispose();
		logoWords.getTexture().dispose();
		cihLogo.getTexture().dispose();
		stage.dispose();
		skin.dispose();
	}

}
