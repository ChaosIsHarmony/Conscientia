package com.ec.conscientia;

import com.badlogic.gdx.Game;
import com.ec.conscientia.FileReaderWriter;
import com.ec.conscientia.screens.EndCreditsScreen;
import com.ec.conscientia.screens.LoadScreen;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.screens.MainMenuScreen;
import com.ec.conscientia.variables.CommonVar;
import com.ec.conscientia.variables.ConscientiaVar;

public class Conscientia extends Game {

	private SoundManager soundManager;
	private FileReaderWriter fileRW;
	private boolean useAltFont = false;
	private ConscientiaVar conscientiaVar;

	@Override
	public void create() {
		soundManager = new SoundManager();
		fileRW = new FileReaderWriter(this);
		conscientiaVar = new ConscientiaVar();
		
		// Testing suite
		// Tests test = new Tests(this);
		// test.runTests();

		// actBook(CommonVar.BIR);
		actBook(CommonVar.EID);
		// actBook(CommonVar.RIK);
		// actBook(CommonVar.THE);
		actBook(CommonVar.TOR);
		// actBook(CommonVar.WUL);

		// change to MainMenu
		changeScreen(CommonVar.MAIN_MENU, true, 0);
	}

	public void changeScreen(int screen, boolean tORf, int bookID) {
		switch (screen) {
		case CommonVar.MAIN_MENU:
			setScreen(new MainMenuScreen(tORf, soundManager, this));
			break;
		case CommonVar.END_CREDITS:
			setScreen(new EndCreditsScreen(soundManager, this));
			break;
		case CommonVar.MAIN_GAME:
			if (tORf) {
				setScreen(new MainGameScreen("new game", bookID, soundManager, this));
			} else
				setScreen(new MainGameScreen("load game", bookID, soundManager, this));
			break;
		case CommonVar.LOAD_SCREEN:
			setScreen(new LoadScreen(soundManager, this));
			break;
		}
	}

	private void actBook(int book) {
		fileRW.actBook(book);
	}

	@Override
	public void render() {
		super.render();
	}

	public boolean isUseAltFont() {
		return useAltFont;
	}

	public void setUseWhinersFont(boolean tORf) {
		useAltFont = tORf;
	}

	public ConscientiaVar getConscVar() {
		return conscientiaVar;
	}
}