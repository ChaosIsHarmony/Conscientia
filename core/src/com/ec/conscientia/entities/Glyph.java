package com.ec.conscientia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;

public class Glyph extends Acquirable {
	private int ID;
	private String title, explanationText, listTitle, imgPathway;
	private FileIOManager fileRW;

	public Glyph(int ID, MainGameScreen mgScr) {
		this.ID = ID;
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);
	}

	public int getID() {
		return ID;
	}

	public String getExplanationText() {
		return this.explanationText;
	}

	public void setExplanationText(String explanationText) {
		this.explanationText = explanationText;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// necessary for ordering list alphabetically
	public String toString() {
		return this.listTitle;
	}

	public void setToString(String listTitle) {
		this.listTitle = listTitle;
	}

	public String acqToString() {
		return fileRW.getAcquirableListString(this.ID);
	}

	public String getImgPathway() {
		return this.imgPathway;
	}

	public void setImgPathway(String imgPathway) {
		this.imgPathway = imgPathway;
	}

	public Image getImg() {
		return new Image(new Texture(getImgPathway()));
	}

	public Image acqGetImg() {
		return new Image(new Texture(fileRW.getAcquirableImage(this.ID)));
	}
}
