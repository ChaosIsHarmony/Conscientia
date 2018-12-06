package com.ec.conscientia.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Book;
import com.ec.conscientia.entities.SavedGame;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.variables.CommonVar;

/*
 * FATAL BUG UPON LOADING OR RETURNING TO MAIN
 */

public class LoadScreen implements Screen {
	private Conscientia conscientia;
	private FileIOManager fileRW;
	private SoundManager soundManager;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Stage stage;
	private Table table;
	private Skin skin;
	private List<SavedGame> savedGameList;
	private List<Book> bookList;
	private ArrayList<SavedGame> allSavedGamesList;
	private TextButton loadButton, backButton, deleteSaveButton;
	private int savedGameNum, bookID;

	private static final int LOGO_SYMBOL_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_SYMBOL_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);
	private static final int LOGO_WORDS_WIDTH = (int) (CommonVar.GAME_WIDTH / 1.01);
	private static final int LOGO_WORDS_HEIGHT = (int) (CommonVar.GAME_HEIGHT / 1.01);

	private Sprite logoSymbol, logoWords, bg;

	public LoadScreen(final SoundManager soundManager, final Conscientia conscientia) {
		fileRW = new FileIOManager(conscientia);

		setNormalCursor();

		this.conscientia = conscientia;
		this.soundManager = soundManager;
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(CommonVar.GAME_WIDTH, CommonVar.GAME_HEIGHT, camera);
		this.stage = new Stage(viewport, conscientia.getConscVar().sb);
		this.skin = new Skin(Gdx.files.internal("Skin/ConscientiaSkin.json"));
		this.camera.position.set(viewport.getScreenWidth() / 2, viewport.getScreenHeight() / 2, 0);

		// Sprites instead of textures so that we can fade in individually
		logoSymbol = new Sprite(new Texture("General/AO_center.png"));
		logoSymbol.setAlpha(1);
		logoSymbol.setPosition(0, 0);
		logoSymbol.setSize(LOGO_SYMBOL_WIDTH, LOGO_SYMBOL_HEIGHT);
		logoWords = new Sprite(new Texture("General/Conscientia.png"));
		logoWords.setAlpha(1);
		logoWords.setPosition((CommonVar.GAME_WIDTH / 2) - (LOGO_WORDS_WIDTH / 2), 0);
		logoWords.setSize(LOGO_WORDS_WIDTH, LOGO_WORDS_HEIGHT);

		// BG
		bg = new Sprite(new Texture("Backgrounds/leather_black_01.png"));
		bg.setSize(stage.getWidth(), stage.getHeight());

		table = new Table(skin);
		table.setFillParent(true);

		bookList = new List<Book>(skin);
		bookList.setItems(fileRW.loadBookList());

		allSavedGamesList = new ArrayList<SavedGame>();
		for (SavedGame sg : fileRW.loadScreenList())
			allSavedGamesList.add(sg);

		savedGameList = new List<SavedGame>(skin);
		savedGameList.setItems(setSavedList());

		bookList.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				savedGameList.setItems(setSavedList());
			}
		});

		final Window listWindow = new Window("", skin, "no_bg");
		listWindow.setMovable(false);
		final Window bookListWindow = new Window("", skin);
		bookListWindow.setMovable(false);
		final Window saveFileWindow = new Window("", skin);
		saveFileWindow.setMovable(false);

		final ScrollPane bookListScrollPane = new ScrollPane(bookList, skin, "no_bg");
		final ScrollPane saveFileScrollPane = new ScrollPane(savedGameList, skin, "no_bg");

		bookListWindow.add(bookListScrollPane).grow();
		saveFileWindow.add(saveFileScrollPane).grow().row();

		listWindow.setSize(CommonVar.GAME_WIDTH * .8f, CommonVar.GAME_HEIGHT * .5f);
		listWindow.add(bookListWindow).size((1.1f * listWindow.getWidth()) / 3, listWindow.getHeight());
		listWindow.add(saveFileWindow).size((2.1f * listWindow.getWidth()) / 3, listWindow.getHeight());

		loadButton = new TextButton("LOAD", skin, "big");
		loadButton.pad(15);
		loadButton.addListener(new ClickListener() {
			// selects first response
			public void clicked(InputEvent event, float x, float y) {
				if (savedGameList.getSelected() != null && !savedGameList.getSelected().getIsCompletedGameFile()) {
					setHourGlassCursor();

					// plays click sound
					soundManager.playSFX(SoundManager.SFX_SPAWNING_SOUND);

					SavedGame sg = (SavedGame) savedGameList.getSelected();
					savedGameNum = sg.getSavedGameNum();
					bookID = sg.getBookID();
					whiteOut = true;

					// fades music out
					soundManager.setFadeIn(false);
					soundManager.setFadeOut(true);
				} else {
					// plays negative click sound
					soundManager.playSFX(SoundManager.SFX_CLICK_NEGATIVE);
				}

			}
		});
		backButton = new TextButton("BACK", skin, "big");
		backButton.pad(18);
		backButton.addListener(new ClickListener() {
			// selects first response
			public void clicked(InputEvent event, float x, float y) {
				setHourGlassCursor();

				// plays click sound
				soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);

				backToMain = true;
				MainMenuScreen.savesUpdated = true;
			}
		});
		deleteSaveButton = new TextButton("DELETE", skin, "big");
		deleteSaveButton.pad(18);
		deleteSaveButton.addListener(new ClickListener() {
			// selects first response
			public void clicked(InputEvent event, float x, float y) {
				if (savedGameList.getSelected() != null) {
					// plays click sound
					soundManager.playSFX(SoundManager.SFX_CLICK_POSITIVE);
					// delete file
					fileRW.deleteSelectedSaveFile(savedGameList.getSelected());
					saveFileWindow.clear();

					populateSavedGameList();
					ScrollPane saveFileScrollPane = new ScrollPane(savedGameList, skin, "no_bg");
					saveFileWindow.add(saveFileScrollPane).grow().row();
					saveFileWindow.invalidate();
				} else
					// plays click sound
					soundManager.playSFX(SoundManager.SFX_CLICK_NEGATIVE);
			}
		});

		Table buttonTable = new Table();
		buttonTable.add(loadButton).size(CommonVar.GAME_WIDTH * .2f, CommonVar.GAME_HEIGHT * .15f).padRight(30);
		buttonTable.add(backButton).size(CommonVar.GAME_WIDTH * .2f, CommonVar.GAME_HEIGHT * .15f).padRight(30);
		buttonTable.add(deleteSaveButton).size(CommonVar.GAME_WIDTH * .2f, CommonVar.GAME_HEIGHT * .15f);

		TextField title = new TextField("SELECT MEMORY", skin, "loadArea");
		title.setDisabled(true);
		title.setAlignment(Align.center);

		table.add(title).size(CommonVar.GAME_WIDTH * .5f, CommonVar.GAME_HEIGHT * .15f).colspan(3).pad(10).row();
		table.add(listWindow).center().padBottom(10).padTop(10).grow().row();
		table.add(buttonTable).size(CommonVar.GAME_WIDTH * .5f, CommonVar.GAME_HEIGHT * .15f).colspan(2).center();

		stage.addActor(table);

		// sets stage to accept input
		Gdx.input.setInputProcessor(stage);
	}

	public SavedGame[] setSavedList() {
		ArrayList<SavedGame> sgList = new ArrayList<SavedGame>();
		for (SavedGame sg : allSavedGamesList)
			if (sg != null && sg.getBookID() == bookList.getSelected().getID())
				sgList.add(sg);

		int ind = 0;
		SavedGame[] sgActualList = new SavedGame[sgList.size()];
		for (SavedGame sg : sgList)
			sgActualList[ind++] = sg;

		return sgActualList;
	}

	public void populateSavedGameList() {
		allSavedGamesList = new ArrayList<SavedGame>();
		for (SavedGame sg : fileRW.loadScreenList())
			allSavedGamesList.add(sg);
		savedGameList.setItems(setSavedList());
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
		conscientia.getConscVar().sb.setProjectionMatrix(camera.combined);

		// draws background
		conscientia.getConscVar().sb.begin();

		bg.draw(conscientia.getConscVar().sb);
		logoSymbol.draw(conscientia.getConscVar().sb);
		logoWords.draw(conscientia.getConscVar().sb);

		conscientia.getConscVar().sb.end();

		stage.act();
		// draw buttons
		stage.draw();
	}

	private float fadeSpeed = .9f;
	private float globalColorValues = 0;
	private boolean whiteOutComplete = false, whiteOut = false;

	private void whiteOut(float delta) {
		if (globalColorValues < 1) {
			globalColorValues = ((globalColorValues += fadeSpeed * delta) > 1) ? 1 : globalColorValues;
			logoSymbol.setAlpha(1 - globalColorValues);
			logoWords.setAlpha(1 - globalColorValues);
			bg.setAlpha(1 - globalColorValues);
			table.setColor(1, 1, 1, 1 - globalColorValues);
		} else
			whiteOutComplete = true;
	}

	private boolean backToMain = false;

	public void update(float delta) {
		// deals with music fade
		if (soundManager.getFadeIn())
			soundManager.fadeInBGM();
		else if (soundManager.getFadeOut())
			soundManager.fadeOutBGM();

		stage.act();
		camera.update();

		// back to main
		if (backToMain)
			conscientia.changeScreen(CommonVar.MAIN_MENU, false, 0);
		// load selected
		if (whiteOut) {
			whiteOut(delta);
			if (whiteOutComplete && !soundManager.getFadeOut()) {
				this.dispose();
				conscientia.getConscVar().currentSavedGameNum = savedGameNum;
				// false = load game
				conscientia.changeScreen(CommonVar.MAIN_GAME, false, bookID);
			}
		}
	}

	private void setNormalCursor() {
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
		bg.getTexture().dispose();
		logoSymbol.getTexture().dispose();
		logoWords.getTexture().dispose();
		stage.dispose();
		skin.dispose();
	}

}
