package com.ec.conscientia.entities;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;
import com.ec.conscientia.variables.ConscientiaVar;

public class NPC {

	// final ints for stats array
	public static final int ALIVE_BOOL = 0, LEVEL = 1, IMAGE_INDEX = 2;
	public static final int STATS_ARRAY_LENGTH = 3;
	public static String[] idNumIndToStringID;

	public HashMap<String, String> locToDialogueAdd;;

	protected String[] stats;
	protected int[] combatAbilities;
	protected String name;
	protected int idNum;
	protected Drawable img;

	private FileIOManager fileRW;
	private ConscientiaVar conscVar;

	public NPC(int idNum, MainGameScreen mgScr) {
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);
		this.conscVar = mgScr.getConscientia().getConscVar();

		this.idNum = idNum;
		this.name = fileRW.reader.getNPCsbyNum(idNum);
		this.stats = fileRW.reader.getNPCsStats(this.name);
		this.combatAbilities = fileRW.reader.getNPCsCombatStats(this.name);
		this.locToDialogueAdd = fileRW.reader.getNPCsDialogueAdds(this.name);
		// if not DESCRIPTION
		if (idNum != 9999) {
			String bookFolder = "";
			switch (conscVar.bookID) {
			case CommonVar.BIR:
				bookFolder = "Biracul/";
				break;
			case CommonVar.EID:
				bookFolder = "Eidos/";
				break;
			case CommonVar.RIK:
				bookFolder = "Rikharr/";
				break;
			case CommonVar.THE:
				bookFolder = "Thetian/";
				break;
			case CommonVar.TOR:
				bookFolder = "Torma/";
				break;
			case CommonVar.WUL:
				bookFolder = "Wulfias/";
				break;
			}

			// loads NPC avatar, if none found, just loads Biracul to prevent
			// crash
			try {
				this.img = new TextureRegionDrawable(new TextureRegion(
						new Texture(Gdx.files.internal("Imgs/" + bookFolder + this.stats[IMAGE_INDEX]))));
			} catch (Exception e) {
				try {
					this.img = new TextureRegionDrawable(
							new TextureRegion(new Texture(Gdx.files.internal("Imgs/Misc/" + this.stats[IMAGE_INDEX]))));
				} catch (Exception f) {
					this.img = new TextureRegionDrawable(
							new TextureRegion(new Texture(Gdx.files.internal("Imgs/Misc/Biracul.jpg"))));
				}
			}
		}
	}

	public String getDialogueAddress(String loc) {
		return this.locToDialogueAdd.get(loc);
	}

	public void setDialogueAddress(String newAddress, String loc) {
		// loc maps to specific location
		this.locToDialogueAdd.put(loc, newAddress);
	}

	public HashMap<String, String> getAllAddresses() {
		return locToDialogueAdd;
	}

	public String getIdNumIndToStringID() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public int getIDnum() {
		return this.idNum;
	}

	public String[] getStats() {
		return this.stats;
	}

	public int getLevel() {
		return Integer.parseInt(this.stats[LEVEL]);
	}

	public int[] getCombatAbilities() {
		return combatAbilities;
	}

	public boolean getRequiresComboToBeat() {
		return Boolean.parseBoolean(this.stats[0]);
	}

	public Drawable getImg() {
		return this.img;
	}
}