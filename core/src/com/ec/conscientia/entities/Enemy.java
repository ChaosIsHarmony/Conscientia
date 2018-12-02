package com.ec.conscientia.entities;

import com.ec.conscientia.screens.MainGameScreen;

public class Enemy {

	private String name;
	private int ID;
	private NPC npcInfo;
	private int[] combatWeaknesses;
	private boolean requiresMultipleItems;
	// final ints for NPC IDs of NPCs you can fight and kill;
	public final static int VIRACOCHA = 2, KHAA = 3, FAMLICUS = 6, MORTIS = 7, CONQUISTUSGUERNICUS = 9, NARGUND = 10,
			BABEL = 11, ADARIN = 12, PAKAHRON = 13, KHLUTT = 14, SENTRY = 49;
	// BOOK OF EIDOS
	// Dazir NPCs
	public final static int DORUK = 50, MERT = 51, JERHAAK = 52, CETIN = 53, SERPIL = 54, AYDIN = 55, COZKUIN = 56,
			PEMBE = 57, RASHAK = 58, ARKARA = 59, GALIP = 60, HAZAN = 61, DIYGU = 62, BEHIYE = 63, EGIMEN = 64,
			SIDIKA = 65, KORAY = 66, KADIR = 67, RADA = 68, QUANGJO = 69, ASANSOR = 70;
	// Tambul NPCs
	public final static int BAHADUR = 101, NIYOOSH = 102, ARGHAVAN = 104, DARY = 105, KALEKI = 106, REBA = 107,
			NOURI = 108, FARRIN = 109, AKKEBER = 110, ABLAH = 111, KAMBIN = 112, SORUSH = 113, DYSMAS = 114, BRUS = 115,
			BOZ = 116, KAGIN = 117, ISMAT = 118, ENTEGHAD = 119;
	// Tacriba NPCs
	public final static int LOGIRA = 201, YARMAK = 202, FIDAN = 203, MINAH = 204, FWAYYA = 205, RUHI = 206, HEYAR = 207,
			ISSAM = 208, CIRE = 209, DIYA = 211, IMAT = 212, XERK = 213, KARRA = 214, QUST = 215, MALIK = 216,
			ADARIK = 217, BISHRA = 218;
	// Canyon NPCs
	public final static int ASSALA = 402, HUBBIYH = 403, NADUB = 404;
	// Wellspring
	public final static int CLOCKWORK_CROWS = 502, MATHELIAN = 503, VALVORTHR = 504, RIKHARR = 18;
	// Ur'Ruk NPCs
	public final static int HAZANNA = 600, KALEKO = 601, EGIMESH = 602, ARKASH = 603, BIRARKUL = 604, RADAN = 605,
			MAGDA = 607, WHABYN = 608, GAPA = 609, KAHIN = 610, NAKARA = 611, SAMESH = 612;
	// Mini bosses
	public final static int GULGANNA = 1000, ARK = 1001;
	// BOOK OF TORMA
	public final static int BEAST_LORD = 1200, GATE_KEEPER = 1201, JINN_SLAYER = 1202, MIND_SEER = 1203,
			COOK_DING = 1208, FORM_FORGER = 1209, MEMORY_KEEPER = 1210, MIDNIGHT = 1211, OATH_ABETTOR = 1214,
			CHAOS_TAMER = 1216, LAW_BRINGER = 1217, MESSAGE_BEARER = 1218;

	public Enemy(int ID, MainGameScreen mgScr) {
		this.ID = ID;
		// load stats from NPC file to determine level and skill set
		this.npcInfo = new NPC(ID, mgScr);
		this.setName(this.npcInfo.getName());
		this.setCombatWeaknesses(this.npcInfo.getCombatAbilities());
		this.requiresMultipleItems = this.npcInfo.getRequiresComboToBeat();
	}

	// getters and setters
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return this.ID;
	}

	public int[] getCombatWeaknesses() {
		return combatWeaknesses;
	}

	public void setCombatWeaknesses(int[] combatWeaknessesIntArray) {
		this.combatWeaknesses = combatWeaknessesIntArray;
	}

	public NPC getNPCinfo() {
		return this.npcInfo;
	}

	public boolean requiresMultipleItems() {
		return this.requiresMultipleItems;
	}
}
