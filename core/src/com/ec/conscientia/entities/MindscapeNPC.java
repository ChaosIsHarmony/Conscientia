package com.ec.conscientia.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;

public class MindscapeNPC extends Acquirable {
	private int ID, lastNPC;
	private String lastLocation, title, explanationText, listTitle, imgPathway;

	private MainGameScreen mgScr;
	private FileIOManager fileRW;

	public MindscapeNPC(int ID, MainGameScreen mgScr) {
		this.mgScr = mgScr;
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);

		this.ID = ID;
		this.lastLocation = mgScr.getCurrentLocation();
		this.lastNPC = mgScr.getCurrentNPC();
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
		return fileRW.reader.getAcquirableListString(this.ID);
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
		return new Image(new Texture(fileRW.reader.getAcquirableImage(this.ID)));
	}

	public int getNPCid() {
		switch (this.ID) {
		// Enclave specters
		case ORMENOS:
			return mgScr.mgVar.NPCbyNum.get("ORMENOS");
		// Spawn of Vanargand
		case FENRIR:
			return mgScr.mgVar.NPCbyNum.get("FENRIR");
		case HEL:
			return mgScr.mgVar.NPCbyNum.get("HEL");
		case LUIN:
			return mgScr.mgVar.NPCbyNum.get("LUIN");
		case JORMUNGUND:
			return mgScr.mgVar.NPCbyNum.get("JORMUNGUND");
		// Jinetes
		case NARGUND:
			return mgScr.mgVar.NPCbyNum.get("NARGUND");
		case FAMLICUS:
			return mgScr.mgVar.NPCbyNum.get("FAMLICUS");
		case CONQUISTIS_GUERNICUS:
			return mgScr.mgVar.NPCbyNum.get("CONQUISTUS_GUERNICUS");
		case BABEL:
			return mgScr.mgVar.NPCbyNum.get("BABEL");
		case MORTIS:
			return mgScr.mgVar.NPCbyNum.get("MORTIS");
		// Mini-bosses
		case GULGANNA:
			return mgScr.mgVar.NPCbyNum.get("GULGANNA");
		case ARK:
			return mgScr.mgVar.NPCbyNum.get("ARK");
		}
		return -1;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(String lastLocation) {
		this.lastLocation = lastLocation;
	}

	public int getLastNPC() {
		return lastNPC;
	}

	public void setLastNPC(int lastNPC) {
		this.lastNPC = lastNPC;
	}
}
