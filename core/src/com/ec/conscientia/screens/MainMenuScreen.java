package com.ec.conscientia.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Book;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.variables.CommonVar;

public class MainMenuScreen implements Screen {
	public Stage stage;
	private Viewport viewport;
	private OrthographicCamera camera;

	private static final int LOGO_SYMBOL_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_SYMBOL_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);
	private static final int LOGO_WORDS_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_WORDS_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);

	private Conscientia conscientia;
	private FileIOManager fileRW;
	private SoundManager soundManager;

	private Sprite logoSymbol, logoWords, bg;
	private TextButton newButton, loadButton, exitButton;
	private Label bookOfBiraculQuote;
	private Skin skin;
	private List<Book> bookList;

	private int bookID = 1; // default is Eidos

	private boolean newGame = false, startFadeOutProcess = false, saveCompleted = false, bookSelected = false;
	public static boolean savesUpdated = false;

	public MainMenuScreen(boolean fade, SoundManager soundMan, Conscientia consc) {
		conscientia = consc;
		fileRW = new FileIOManager(consc);
		soundManager = soundMan;

		// if fade is true the fades in
		fadeInComplete = !fade;

		setNormalCursor();

		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(CommonVar.GAME_WIDTH, CommonVar.GAME_HEIGHT, camera);
		this.stage = new Stage(viewport, conscientia.getConscVar().sb);
		this.skin = new Skin(Gdx.files.internal("Skin/ConscientiaSkin.json"));
		this.camera.position.set(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2, 0);

		// BG
		bg = new Sprite(new Texture("Backgrounds/leather_black_01.png"));
		bg.setSize(stage.getWidth(), stage.getHeight());
		bg.setAlpha((fade) ? 0 : 1);

		// Sprites instead of textures so that we can fade in individually
		logoSymbol = new Sprite(new Texture("General/AO_center.png"));
		logoSymbol.setAlpha((fade) ? 0 : 1);
		logoSymbol.setPosition(0, 0);
		logoSymbol.setSize(LOGO_SYMBOL_WIDTH, LOGO_SYMBOL_HEIGHT);
		logoWords = new Sprite(new Texture("General/Conscientia.png"));
		logoWords.setAlpha((fade) ? 0 : 1);
		logoWords.setPosition((CommonVar.GAME_WIDTH / 2) - (LOGO_WORDS_WIDTH / 2), 0);
		logoWords.setSize(LOGO_WORDS_WIDTH, LOGO_WORDS_HEIGHT);

		// buttons
		newButton = new TextButton("BEGIN", skin, "big");
		newButton.pad(10);
		newButton.setColor(1, 1, 1, (fade) ? 0 : 1);

		loadButton = new TextButton("MEMORY", skin, "big");
		loadButton.pad(10);
		loadButton.setColor(1, 1, 1, (fade) ? 0 : 1);

		exitButton = new TextButton("END", skin, "big");
		exitButton.pad(10);
		exitButton.setColor(1, 1, 1, (fade) ? 0 : 1);

		bookSelectWindow = new Window("", skin, "no_bg");
		bookSelectWindow.setSize(CommonVar.GAME_WIDTH * .35f, CommonVar.GAME_HEIGHT * .75f);
		bookSelectWindow.getTitleLabel().setAlignment(Align.center);
		bookSelectWindow.setPosition((CommonVar.GAME_WIDTH / 2) - (bookSelectWindow.getWidth() / 2),
				(CommonVar.GAME_HEIGHT / 2) - (bookSelectWindow.getHeight() / 2));

		final TextButton title = new TextButton("SELECT BOOK", skin);
		title.setTouchable(Touchable.disabled);

		bookList = new List<Book>(skin);
		bookList.setItems(fileRW.reader.loadBookList());

		final ScrollPane listPane = new ScrollPane(bookList, skin);

		final TextButton button = new TextButton("Select", skin);
		button.addListener(new ClickListener() {
			// selects first response
			public void clicked(InputEvent event, float x, float y) {
				// plays game starting sound
				soundManager.playSFX(SoundManager.SFX_SPAWNING_SOUND);

				// selects the book to start with
				Book book = (Book) bookList.getSelected();
				bookID = book.getID();
				bookSelected = true;

				// fades music out
				soundManager.setFadeIn(false);
				soundManager.setFadeOut(true);
			}
		});

		bookSelectWindow.add(title).size(bookSelectWindow.getWidth(), bookSelectWindow.getHeight() / 8).row();
		bookSelectWindow.add(listPane).size(bookSelectWindow.getWidth(), (6 * bookSelectWindow.getHeight()) / 8).row();
		bookSelectWindow.add(button).size(bookSelectWindow.getWidth(), bookSelectWindow.getHeight() / 8);
		bookSelectWindow.setVisible(false);

		// makes the buttons work even without fading in
		// like when coming back from load screen
		if (!fade)
			enableButtons();

		// create table
		Table table = new Table();
		table.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		table.align(Align.center | Align.center);
		table.add(newButton).pad(25);
		table.getCell(newButton).expandX();
		table.row();
		table.add(loadButton).pad(25);
		table.getCell(loadButton).expandX();
		table.row();
		table.add(exitButton).pad(25);
		table.getCell(exitButton).expandX();

		stage.addActor(table);
		stage.addActor(bookSelectWindow);

		// sets the stage as the input processor
		Gdx.input.setInputProcessor(stage);

		// Grabs a random quote from the book of biracul
		bookOfBiraculQuote = new Label("", skin);
		bookOfBiraculQuote.setText(fileRW.reader.loadSplashQuote());
		bookOfBiraculQuote.setPosition(stage.getWidth() / 3.5f, stage.getHeight() / 2);
		bookOfBiraculQuote.setColor(1, 1, 1, 0);

		// DEBUG CODE
		// to make sure there are no formatting mistakes in the dialogue files
		// Tests tests = new Tests();
		// tests.runTests();
	}

	@Override
	public void show() {
	}

	long timer = System.currentTimeMillis();

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(globalColorValues, globalColorValues, globalColorValues, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// sets center
		conscientia.getConscVar().sb.setProjectionMatrix(camera.combined);

		// draws background
		conscientia.getConscVar().sb.begin();
		bg.draw(conscientia.getConscVar().sb);
		conscientia.getConscVar().sb.end();

		if (!savesUpdated) {
			conscientia.getConscVar().sb.begin();
			bookOfBiraculQuote.draw(conscientia.getConscVar().sb, 1);
			conscientia.getConscVar().sb.end();

			if (!startFadeOutProcess)
				fadeQuote(true, delta);
			else
				fadeQuote(false, delta);

		} else {
			update(delta);

			// draws background
			conscientia.getConscVar().sb.begin();
			logoSymbol.draw(conscientia.getConscVar().sb);
			logoWords.draw(conscientia.getConscVar().sb);
			conscientia.getConscVar().sb.end();

			stage.act();
			// draw buttons
			stage.draw();
		}
	}

	private boolean fadeInComplete = false;
	private float logoSymAlpha = 0, logoWordsAlpha = 0, buttonsAlpha = 0;
	private float fadeSpeed = .5f;

	private void fadeIn(float delta) {
		if (!fadeInComplete)
			if (logoSymAlpha < 1) {
				logoSymAlpha = ((logoSymAlpha += fadeSpeed * delta) > 1) ? 1 : logoSymAlpha;
				logoSymbol.setAlpha(logoSymAlpha);
				bg.setAlpha(logoSymAlpha);
			} else if (logoWordsAlpha < 1) {
				logoWordsAlpha = ((logoWordsAlpha += fadeSpeed * delta) > 1) ? 1 : logoWordsAlpha;
				logoWords.setAlpha(logoWordsAlpha);
			} else if (buttonsAlpha < 1) {
				buttonsAlpha = ((buttonsAlpha += (fadeSpeed) * delta) > 1) ? 1 : buttonsAlpha;
				newButton.setColor(1, 1, 1, buttonsAlpha);
				loadButton.setColor(1, 1, 1, buttonsAlpha);
				exitButton.setColor(1, 1, 1, buttonsAlpha);
			} else {
				fadeInComplete = true;
				enableButtons();
			}

	}

	float quoteAlpha = 0;

	private void fadeQuote(boolean fadeIn, float delta) {
		if (fadeIn) {
			if (quoteAlpha < 1)
				quoteAlpha = ((quoteAlpha += fadeSpeed * delta) > 1) ? 1 : quoteAlpha;
			else
				startFadeOutProcess = true;
			bookOfBiraculQuote.setColor(1, 1, 1, quoteAlpha);
			if (startFadeOutProcess)
				saveCompleted = true;
		} else {
			if (System.currentTimeMillis() - 6000 > timer && saveCompleted) {
				if (quoteAlpha > 0)
					quoteAlpha = ((quoteAlpha -= fadeSpeed * delta) < 0) ? 0 : quoteAlpha;
				else {
					savesUpdated = true;
				}
				bookOfBiraculQuote.setColor(1, 1, 1, quoteAlpha);
			} else if (System.currentTimeMillis() - 5500 > timer && saveCompleted) {
				if (soundManager.getCurrentBGMID() != SoundManager.BGM_MAIN_MENU) {
					soundManager.setFadeIn(true);
					soundManager.loadBGM(SoundManager.BGM_MAIN_MENU);
					soundManager.playBGM();
				}
			}
		}
	}

	private void enableButtons() {
		newButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				setHourGlassCursor();

				// plays click sound
				soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);

				// whites out and then switches to main game screen with new
				// game
				whiteOut = true;
				newGame = true;
			}
		});
		loadButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				setHourGlassCursor();

				// switches to load screen
				loadGame = true;
			}
		});
		exitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);

				// exits
				Gdx.app.exit();
			}
		});
	}

	private float globalColorValues = 0;
	private boolean whiteOutComplete = false, whiteOut = false;

	private void whiteOut(float delta) {
		if (globalColorValues < 1) {
			globalColorValues = ((globalColorValues += fadeSpeed * delta) > 1) ? 1 : globalColorValues;
			logoSymbol.setAlpha(1 - globalColorValues);
			logoWords.setAlpha(1 - globalColorValues);
			bg.setAlpha(1 - globalColorValues);
			newButton.setColor(1, 1, 1, 1 - globalColorValues);
			loadButton.setColor(1, 1, 1, 1 - globalColorValues);
			exitButton.setColor(1, 1, 1, 1 - globalColorValues);
			bookSelectWindow.setColor(1, 1, 1, 1 - (globalColorValues * 2.5f));
		} else
			whiteOutComplete = true;
	}

	private boolean loadGame = false;
	private Window bookSelectWindow;

	public void update(float delta) {
		// deals with music fade
		if (soundManager.getFadeOut())
			soundManager.fadeOutBGM();

		stage.act();
		// fade in
		if (!fadeInComplete)
			fadeIn(delta);
		camera.update();

		// switches to load screen
		if (loadGame) {
			// checks to see if there are any saved games
			if (fileRW.reader.loadScreenList()[0] != null) {
				// plays click sound
				soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
				conscientia.changeScreen(CommonVar.LOAD_SCREEN, false, 0);
			} else {
				// plays negative sound
				soundManager.playSFX(SoundManager.SFX_CLICK_NEGATIVE);
				loadGame = false;
				setNormalCursor();
			}
		}

		// new selected
		if (whiteOut) {
			if (bookSelected) {
				whiteOut(delta);
				if (whiteOutComplete && !soundManager.getFadeOut()) {
					this.dispose();
					if (newGame) {
						conscientia.changeScreen(CommonVar.MAIN_GAME, true, bookID);
					}
				}
			} else {
				setNormalCursor();
				bookSelectWindow.setVisible(true);
				newButton.setVisible(false);
				loadButton.setVisible(false);
				exitButton.setVisible(false);
			}
		}
	}

	private void setNormalCursor() {
		Gdx.input.setCursorCatched(false);
		Pixmap pm = new Pixmap(Gdx.files.internal("General/normalCursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();
	}

	private void setHourGlassCursor() {
		Pixmap pm = new Pixmap(Gdx.files.internal("General/hourGlassCursor.png"));
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();
	}

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
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		bg.getTexture().dispose();
		logoSymbol.getTexture().dispose();
		logoWords.getTexture().dispose();
	}

}
