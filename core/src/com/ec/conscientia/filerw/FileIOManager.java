package com.ec.conscientia.filerw;

import com.ec.conscientia.Conscientia;
import com.ec.conscientia.screens.MainGameScreen;

public class FileIOManager {
	public Reader reader;
	public Writer writer;

	public final String SAVE_FILE_STR = "SG/genericSG.mao";

	// used in the reader and writer
	String currentSavedGameFile, NPCFile, uniSaveFile;
	Conscientia conscientia;
	MainGameScreen mgScr;
	
	/*
	 * For test suite
	 */
	public FileIOManager() {
		this.reader = new Reader(this);
		this.writer = new Writer(this);
	}

	/*
	 * For actual game
	 */
	public FileIOManager(Conscientia conscientia) {
		this.reader = new Reader(this);
		this.writer = new Writer(this);
		this.conscientia = conscientia;
	}

	public FileIOManager(Conscientia conscientia, MainGameScreen mainGameScr) {
		this.reader = new Reader(this);
		this.writer = new Writer(this);
		this.conscientia = conscientia;
		this.mgScr = mainGameScr;
	}
}