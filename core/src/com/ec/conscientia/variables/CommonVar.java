package com.ec.conscientia.variables;

import com.ec.conscientia.entities.Acquirable;

public class CommonVar {
	// Screen Dims
	public final static int GAME_WIDTH = 1280, GAME_HEIGHT = 720;

	// Screen IDs
	public final static int MAIN_MENU = 0, END_CREDITS = 1, MAIN_GAME = 2, LOAD_SCREEN = 3;

	// Book IDs
	public static final int BIR = 0, EID = 1, RIK = 2, THE = 3, TOR = 4, WUL = 5;

	// File IO
	public final static int SAVE_FILE = 0, NPC_FILE = 1, UNI_FILE = 3, NUM_BIRACULIAN_VERSES = 20;

	// Persistent across playthroughs
	public final static int[] persistentAcquirables = new int[] {
			// GLYPHS
			Acquirable.EXTRACTION_GLYPH, Acquirable.LUIN_SWORD_GLYPH, Acquirable.LUIN_ARMOR_GLYPH,
			Acquirable.EIDOS_GLYPH, Acquirable.OCCULT_GLYPH, Acquirable.CORRUPTION_GLYPH, Acquirable.RESURRECTION_GLYPH,
			Acquirable.PURIFICATION_GLYPH, Acquirable.NEARCASTER_GLYPH,
			// TOMES
			Acquirable.KABU_COMBAT_TOME, Acquirable.KABU_FARCASTER_TOME, Acquirable.KABU_GLYPHS_TOME,
			Acquirable.INTROSPECTION_TOME, Acquirable.CLOCKWORK_CROWS_TOME, Acquirable.KHAA_EXCAVATION_TOME,
			Acquirable.BOOK_OF_QUANGJO_VOL_II_TOME, Acquirable.SONGS_OF_KABU_TOME, Acquirable.KABU_RITUAL_TOME,
			Acquirable.TORMA_III_TOME, Acquirable.TORMA_VII_TOME, Acquirable.CONFESSION_OF_THETIAN_TOME,
			Acquirable.EFFECTS_OF_REDGRAIN_TOME, Acquirable.TONGUES_GLYPH_TOME, Acquirable.VALVORTHR_MANUAL_TOME,
			Acquirable.KABU_DAZIR_ARCHIVES_TOME, Acquirable.KABU_WELLSPRING_QUANGJO_TOME, Acquirable.THE_RED_TOWER,
			Acquirable.TORMA_VI_TOME, Acquirable.TORMA_X_TOME, Acquirable.SANCTUARY_MUNNIN_TOME,
			Acquirable.KABU_SALT_FOREST_TOME, Acquirable.KABU_RIKHARR_TOME, Acquirable.TORMA_II_TOME,
			Acquirable.KABAN_MYTHS_TOME, Acquirable.DEATHLY_CULTS_TOME, Acquirable.PRECEPTS_OF_THOUGHT_TOME,
			Acquirable.WHISPERED_PROPHECY_TOME, Acquirable.BOOK_OF_QUANGJO_VOL_XVII_TOME,
			Acquirable.BOOK_OF_QUANGJO_VOL_XX_TOME, Acquirable.ART_AND_ARTIFICE, Acquirable.THE_INFINITE_PURSUIT,
			Acquirable.ORIGINS_OF_THE_SCHISM, Acquirable.PRIMORDIAL_TECHNOCRAFT, Acquirable.SPECTRAL_PROJECTION,
			Acquirable.WAYFARER_EXTRACTIONS, Acquirable.PRIME_ARCHON_LIST, Acquirable.BEFORE_THE_FALL_I, Acquirable.BEFORE_THE_FALL_II, Acquirable.BEFORE_THE_FALL_III,
			// MINDSCAPE NPCs
			Acquirable.FENRIR, Acquirable.HEL, Acquirable.JORMUNGUND, Acquirable.LUIN, Acquirable.ORMENOS,
			Acquirable.GULGANNA, Acquirable.NARGUND, Acquirable.FAMLICUS, Acquirable.CONQUISTIS_GUERNICUS,
			Acquirable.BABEL, Acquirable.MORTIS };

	// A list of all persistent events, used when writing save files
	public final static int[] persistentEvents = new int[] { 0, 1000, 2000, 2001, 2002, 2003, 2004, 2012, 2025, 2026,
			2045, 2046, 2047, 2048, 2049, 2050, 2051, 2052, 2053, 2067, 2069, 2074, 2079, 2083, 2088, 2089, 2090, 2091,
			2092, 2093, 2094, 2095, 2096, 2102, 2103, 3007, 10000, 10108, 10110, 10111, 10112, 10115, 11100, 11211, 11418,
			11430, 11433, 11435, 11437, 11439, 11440, 11441, 11502, 11503, 12001, 12003, 12300, 12301, 12302, 12303,
			12304, 12305, 12306, 12307, 12308, 12401, 12407, 12414, 12416, 12417, 12420, 13002, 13109, 13110, 13113,
			13119, 13124, 13800, 13802, 13803, 13806, 14002, 11441, 14502, 15000, 15001, 15003, 15004, 15007, 15008,
			15106, 16003, 16007, 16008, 16010, 16012, 16013, 16019, 16024, 16029, 16050, 16053, 17007, 17300, 17902,
			18109, 19000, 19001, 19002, 19004, 20000, 21000, 21003, 21004, 21005, 21006, 30000, 30001 };

	public static int EIDOS_DEATH_RESPAWN = 0;
}
