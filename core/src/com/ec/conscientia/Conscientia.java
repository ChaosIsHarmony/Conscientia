package com.ec.conscientia;

import com.badlogic.gdx.Game;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.EndCreditsScreen;
import com.ec.conscientia.screens.LoadScreen;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.screens.MainMenuScreen;
import com.ec.conscientia.variables.CommonVar;
import com.ec.conscientia.variables.ConscientiaVar;

public class Conscientia extends Game {

    private SoundManager soundManager;
    private FileIOManager fileRW;
    private boolean useAltFont = false;
    private ConscientiaVar conscientiaVar;

    @Override
    public void create() {
        soundManager = new SoundManager();
        fileRW = new FileIOManager(this);
        conscientiaVar = new ConscientiaVar();

        // Testing suite
//		Tests test = new Tests(this);
//		test.runTests();
//        setScreen(new MainGameScreen(true, CommonVar.TOR, soundManager, this, true));

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
                setScreen(new MainGameScreen(tORf, bookID, soundManager, this, false));
                break;
            case CommonVar.LOAD_SCREEN:
                setScreen(new LoadScreen(soundManager, this));
                break;
        }
    }

    private void actBook(int book) {
        fileRW.writer.actBook(book);
    }

    @Override
    public void render() {
        super.render();
    }

    public boolean isUseAltFont() {
        return useAltFont;
    }

    public void setUseAltFont(boolean tORf) {
        useAltFont = tORf;
    }

    public ConscientiaVar getConscVar() {
        return conscientiaVar;
    }
}