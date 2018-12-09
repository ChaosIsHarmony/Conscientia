package com.ec.conscientia.ingameoperations;

import java.util.Random;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.entities.Glyph;
import com.ec.conscientia.entities.Log;
import com.ec.conscientia.entities.MindscapeNPC;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class CheckUniqueEvent {
	private MainGameScreen mgScr;

	// Redundant in MainGameScreen because won't work in switch statement
	// otherwise
	public final int ITEM_GLYPH = 0, ITEM_TOME = 1;

	public CheckUniqueEvent(MainGameScreen mgScr) {
		this.mgScr = mgScr;
	}

	public void checkForUniqueEvents() {
		try {
			// looks to see if a number of events are true and thus if another
			// related event should be true isLooney
			if (!mgScr.mgVar.isLooney && mgScr.mgVar.player.getLoon() > 30)
				mgScr.mgVar.isLooney = true;

			// check for end game conditions
			// SANCTUARY
			// Acquired Glyph of Discipline, Destroyed Ormenos crystal and Luin
			// Crystal
			if (!mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12304)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12008)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12011))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(20000, true);
			// Acquired Glyph of Awareness, Destroyed Library crystal and Luin
			// Crystal
			else if (!mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12304)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12408)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12011))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(20000, true);
			// Rejected Fenrir and blew up Luin
			else if (!mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12304)
					&& !(mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12000)
							|| mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12400)
							|| mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12403))
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12900)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12011))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(20000, true);
			// Acquired GoA, Reckoner did not open door and shut down
			else if (!mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12304)
					&& !mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12409)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12410)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12011))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(20000, true);

			// sets knowledge of Vanargand's involvement in your birth as true
			if (mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(19000)
					|| mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12001))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(19002, true);

			// JENOWIN PLAIN
			// Rejected Fenrir's help, destroyed portcullis crystal, not welcome
			// in Tambul
			if (!(mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12000)
					|| mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12400)
					|| mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12403))
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12405)
					&& mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(13900))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(20003, true);

			// makes in Ur'Ruk event false unless person is in Ur'Ruk
			// this is relevant for Ormenos dialogue
			if (mgScr.getCurrentLocAddress(3).contains("UR'RUK"))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(17003, true);
			else
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(17003, false);

			// Sees if Eidos drank elixir
			// Sets no elixir event as false if true
			if (mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(12412))
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(12413, false);

			// resets address + NPC in real world upon leaving the MINDSCAPE
			if (mgScr.mgVar.cues.get("MINDSCAPE").contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				mgScr.mgVar.mindscapeFadeIn = false;
				mgScr.mgVar.mindscapeFadeOut = true;
				mgScr.mgVar.mindscapeExit = true;
			} else if (mgScr.mgVar.cues.get("MINDSCAPE_NEXUS_ENTRANCE")
					.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				mgScr.mgVar.mindscapeFadeIn = true;
				mgScr.mgVar.mindscapeFadeOut = false;
			} else if (mgScr.mgVar.cues.get("MINDSCAPE_NEXUS_EXIT")
					.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				mgScr.mgVar.mindscapeFadeIn = false;
				mgScr.mgVar.mindscapeFadeOut = true;
			}
			// randomly selects a vision for the obsidian ruin
			else if (mgScr.mgVar.cues.get("OBSIDIAN VISION").get(0)
					.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				Random rand = new Random();
				// num_of_visions = 6, increase if visions increase
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put((rand.nextInt(6) + 16101), true);
				mgScr.mgVar.dialogue.setCurrentAddress(mgScr.mgVar.cues.get("OBSIDIAN VISION").get(1));
			}
			// Khlutt steals Luin from you, this removes all traces of Luin from
			// your inventory
			else if (mgScr.mgVar.cues.get("KHLUTT STEALS LUIN").contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				if (mgScr.mgVar.player.getItemsAcquired().contains(MindscapeNPC.LUIN))
					mgScr.mgVar.player.getItemsAcquired()
							.remove(mgScr.mgVar.player.getItemsAcquired().indexOf(MindscapeNPC.LUIN));
				if (mgScr.mgVar.player.getItemsAcquired().contains(Glyph.LUIN_SWORD_GLYPH))
					mgScr.mgVar.player.getItemsAcquired()
							.remove(mgScr.mgVar.player.getItemsAcquired().indexOf(Glyph.LUIN_SWORD_GLYPH));
				if (mgScr.mgVar.player.getItemsAcquired().contains(Glyph.LUIN_ARMOR_GLYPH))
					mgScr.mgVar.player.getItemsAcquired()
							.remove(mgScr.mgVar.player.getItemsAcquired().indexOf(Glyph.LUIN_ARMOR_GLYPH));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(MindscapeNPC.LUIN))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.remove(
							mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.indexOf(MindscapeNPC.LUIN));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(Glyph.LUIN_SWORD_GLYPH))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
							.remove(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
									.indexOf(Glyph.LUIN_SWORD_GLYPH));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(Glyph.LUIN_ARMOR_GLYPH))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
							.remove(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
									.indexOf(Glyph.LUIN_ARMOR_GLYPH));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(12300))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
							.remove(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.indexOf(12300));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(12301))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
							.remove(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.indexOf(12301));
				if (mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(12302))
					mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents
							.remove(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.indexOf(12302));
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(12300, false);
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(12301, false);
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(12302, false);
			} else if (mgScr.mgVar.cues.get("ABSORB POTCULLIS CRYSTAL")
					.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
				// deactivates portcullis crystal
				mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(12404, false);
			}
			// checks for mind-editing/reading
			else if (mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FAMLICUS")
					|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("KHAA")
					|| mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("PAKAHRON"))
				if (checkForMindEditing()) {
					// sets famlicus as gone post editing for all addresses
					// except
					// where he sends you to Tambul
					if (!mgScr.mgVar.dialogue.getCurrentAddress()
							.equals("KABU!WILDERNESS!JENOWIN PLAIN!0.X701!FAMLICUS!")
							&& mgScr.mgVar.currentNPC == mgScr.mgVar.NPCbyNum.get("FAMLICUS"))
						mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(17900, true);
					mgScr.mgVar.gameState = mgScr.SCREEN_EFFECT_MIND_EDITING;
					mgScr.mgVar.flickerStartTime = System.currentTimeMillis();
				}

			if (mgScr.mgVar.checkItemAcquired) {
				// GLYPHS
				if (isItemAcquired(10106, Glyph.WULFIAS_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.WULFIAS_GLYPH);
				else if (isItemAcquired(11100, Glyph.PURIFICATION_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.PURIFICATION_GLYPH);
				else if (isItemAcquired(12000, Glyph.DISCIPLINE_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.DISCIPLINE_GLYPH);
				else if (isItemAcquired(12301, Glyph.LUIN_SWORD_GLYPH, true)) {
					addItem(mgScr.ITEM_GLYPH, Glyph.LUIN_SWORD_GLYPH);
					if (!mgScr.mgVar.player.getItemsAcquired().contains(MindscapeNPC.LUIN))
						mgScr.mgVar.player.getItemsAcquired().add(MindscapeNPC.LUIN);
				} else if (isItemAcquired(12302, Glyph.LUIN_ARMOR_GLYPH, true)) {
					addItem(mgScr.ITEM_GLYPH, Glyph.LUIN_ARMOR_GLYPH);
					if (!mgScr.mgVar.player.getItemsAcquired().contains(MindscapeNPC.LUIN))
						mgScr.mgVar.player.getItemsAcquired().add(MindscapeNPC.LUIN);
				} else if (isItemAcquired(12304, Glyph.OCCULT_GLYPH, true)) {
					addItem(mgScr.ITEM_GLYPH, Glyph.OCCULT_GLYPH);
					if (!mgScr.mgVar.player.getItemsAcquired().contains(MindscapeNPC.ORMENOS))
						mgScr.mgVar.player.getItemsAcquired().add(MindscapeNPC.ORMENOS);
				} else if (isItemAcquired(12400, Glyph.AWARENESS_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.AWARENESS_GLYPH);
				else if (isItemAcquired(12403, Glyph.FARCASTING_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.FARCASTING_GLYPH);
				else if (isItemAcquired(17300, Glyph.RESURRECTION_GLYPH, false)) {
					addItem(mgScr.ITEM_GLYPH, Glyph.RESURRECTION_GLYPH);
					mgScr.mgVar.player.getItemsAcquired().add(MindscapeNPC.FAMLICUS);
					// sets Famlicus acquired to true
					mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(17902, true);
				} else if (isItemAcquired(13113, Glyph.CORRUPTION_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.CORRUPTION_GLYPH);
				else if (isItemAcquired(11503, MindscapeNPC.GULGANNA, true))
					mgScr.mgVar.player.getItemsAcquired().add(MindscapeNPC.GULGANNA);
				else if (isItemAcquired(2083, Glyph.NEARCASTER_GLYPH, false))
					addItem(mgScr.ITEM_GLYPH, Glyph.NEARCASTER_GLYPH);
				// TOMES
				else if (isItemAcquired(14002, Log.INTROSPECTION_TOME, true)) {
					mgScr.mgVar.hasIntrospection = true;
					addItem(mgScr.ITEM_TOME, Log.INTROSPECTION_TOME);
				} else if (isItemAcquired(12305, Log.KABU_GLYPHS_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_GLYPHS_TOME);
				else if (isItemAcquired(13802, Log.KABU_FARCASTER_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_FARCASTER_TOME);
				else if (isItemAcquired(15106, Log.KABU_COMBAT_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_COMBAT_TOME);
				else if (isItemAcquired(11433, Log.CLOCKWORK_CROWS_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.CLOCKWORK_CROWS_TOME);
				else if (isItemAcquired(11435, Log.KHAA_EXCAVATION_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KHAA_EXCAVATION_TOME);
				else if (isItemAcquired(13800, Log.TORMA_III_TOME, true)) {
					activateMaps();
					addItem(mgScr.ITEM_TOME, Log.TORMA_III_TOME);
				} else if (isItemAcquired(12306, Log.CONFESSION_OF_THETIAN_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.CONFESSION_OF_THETIAN_TOME);
				else if (isItemAcquired(13803, Log.EFFECTS_OF_REDGRAIN_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.EFFECTS_OF_REDGRAIN_TOME);
				else if (isItemAcquired(12303, Log.TONGUES_GLYPH_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.TONGUES_GLYPH_TOME);
				else if (isItemAcquired(16024, Log.SONGS_OF_KABU_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.SONGS_OF_KABU_TOME);
				else if (isItemAcquired(14502, Log.VALVORTHR_MANUAL_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.VALVORTHR_MANUAL_TOME);
				else if (isItemAcquired(10110, Log.KABU_DAZIR_ARCHIVES_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_DAZIR_ARCHIVES_TOME);
				else if (isItemAcquired(10111, Log.TORMA_VII_TOME, true)) {
					activateMaps();
					addItem(mgScr.ITEM_TOME, Log.TORMA_VII_TOME);
				} else if (isItemAcquired(11430, Log.KABU_WELLSPRING_QUANGJO_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_WELLSPRING_QUANGJO_TOME);
				else if (isItemAcquired(13806, Log.THE_RED_TOWER, true))
					addItem(mgScr.ITEM_TOME, Log.THE_RED_TOWER);
				else if (isItemAcquired(10112, Log.TORMA_VI_TOME, true)) {
					activateMaps();
					addItem(mgScr.ITEM_TOME, Log.TORMA_VI_TOME);
				} else if (isItemAcquired(16050, Log.TORMA_X_TOME, true)) {
					activateMaps();
					addItem(mgScr.ITEM_TOME, Log.TORMA_X_TOME);
				} else if (isItemAcquired(12308, Log.SANCTUARY_MUNNIN_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.SANCTUARY_MUNNIN_TOME);
				else if (isItemAcquired(16029, Log.KABU_SALT_FOREST_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_SALT_FOREST_TOME);
				else if (isItemAcquired(11441, Log.BOOK_OF_QUANGJO_VOL_II_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.BOOK_OF_QUANGJO_VOL_II_TOME);
				else if (isItemAcquired(12420, Log.TORMA_II_TOME, true)) {
					activateMaps();
					addItem(mgScr.ITEM_TOME, Log.TORMA_II_TOME);
				} else if (isItemAcquired(2045, Log.ANALYSIS_OF_RIKHARR, true))
					addItem(mgScr.ITEM_TOME, Log.ANALYSIS_OF_RIKHARR);
				else if (isItemAcquired(2046, Log.KABAN_MYTHS_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABAN_MYTHS_TOME);
				else if (isItemAcquired(2047, Log.PRECEPTS_OF_THOUGHT_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.PRECEPTS_OF_THOUGHT_TOME);
				else if (isItemAcquired(2048, Log.DEATHLY_CULTS_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.DEATHLY_CULTS_TOME);
				else if (isItemAcquired(2049, Log.WAYFARER_EXTRACTIONS, true))
					addItem(mgScr.ITEM_TOME, Log.WAYFARER_EXTRACTIONS);
				else if (isItemAcquired(2088, Log.WHISPERED_PROPHECY_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.WHISPERED_PROPHECY_TOME);
				else if (isItemAcquired(2089, Log.BOOK_OF_QUANGJO_VOL_XVII_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.BOOK_OF_QUANGJO_VOL_XVII_TOME);
				else if (isItemAcquired(2090, Log.BOOK_OF_QUANGJO_VOL_XX_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.BOOK_OF_QUANGJO_VOL_XX_TOME);
				else if (isItemAcquired(2091, Log.ART_AND_ARTIFICE, true))
					addItem(mgScr.ITEM_TOME, Log.ART_AND_ARTIFICE);
				else if (isItemAcquired(2092, Log.THE_INFINITE_PURSUIT, true))
					addItem(mgScr.ITEM_TOME, Log.THE_INFINITE_PURSUIT);
				else if (isItemAcquired(2093, Log.ORIGINS_OF_THE_SCHISM, true))
					addItem(mgScr.ITEM_TOME, Log.ORIGINS_OF_THE_SCHISM);
				else if (isItemAcquired(2094, Log.PRIMORDIAL_TECHNOCRAFT, true))
					addItem(mgScr.ITEM_TOME, Log.PRIMORDIAL_TECHNOCRAFT);
				else if (isItemAcquired(2095, Log.SPECTRAL_PROJECTION, true))
					addItem(mgScr.ITEM_TOME, Log.SPECTRAL_PROJECTION);
				else if (isItemAcquired(2096, Log.PRIME_ARCHON_LIST, true))
					addItem(mgScr.ITEM_TOME, Log.PRIME_ARCHON_LIST);
				else if (isItemAcquired(2098, Log.KABU_RIKHARR_TOME, true))
					addItem(mgScr.ITEM_TOME, Log.KABU_RIKHARR_TOME);

				// prevents loading crash
				if (mgScr.mgVar.pauseHoverImg != null)
					mgScr.mgVar.pauseHoverImg.setTouchable(Touchable.enabled);
				mgScr.mgVar.checkItemAcquired = false;
			}

			// Changes the book number in the file to organize save files
			for (String address : mgScr.mgVar.cues.get("BOOK CHANGE")) {
				if (address.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
					mgScr.scrFX.resetWhiteIn();
					if (address.contains("KABU") || address.contains("EIDOS")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.EID);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.EID;
					} else if (address.contains("KAVU") || address.contains("RIKHARR")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.RIK);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.RIK;
					} else if (address.contains("THIUDA") || address.contains("WULFIAS")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.WUL);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.WUL;
					} else if (address.contains("EMPYREAN") || address.contains("BIRACUL")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.BIR);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.BIR;
					} else if (address.contains("JER") || address.contains("THETIAN")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.THE);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.THE;
					} else if (address.contains("ENCLAVE") || address.contains("TORMA")) {
						mgScr.mgVar.fileRW.writer.setBook(CommonVar.TOR);
						mgScr.mgVar.conscientia.getConscVar().bookID = CommonVar.TOR;
					}
				}
			}

			// Check for words that lead to a fight
			for (String address : mgScr.mgVar.cues.get("FIGHTING WORDS")) {
				if (address.contains(mgScr.mgVar.dialogue.getCurrentAddress())) {
					if (address.contains("KHAA!"))
						// this can be called when description is NPC, so we
						// need to change it to Khaa for you to fight
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("KHAA");
					else if (address.contains("PAKAHRON!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("PAKAHRON");
					else if (address.contains("KHLUTT!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("KHLUTT");
					else if (address.contains("GULGANNA!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("GULGANNA");
					else if (address.contains("DYSMAS!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("DYSMAS");
					else if (address.contains("ARK!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("ARK");
					else if (address.contains("CLOCKWORK CROWS!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("CLOCKWORK CROWS");
					else if (address.contains("MATHELIAN!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("MATHELIAN");
					else if (address.contains("VALVORTHR!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("VALVORTHR");
					else if (address.contains("ORMENOS!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("JINNWRAITH ORMENOS");
					else if (address.contains("HUBBIYH!"))
						mgScr.mgVar.currentNPC = mgScr.mgVar.NPCbyNum.get("HUBBIYH");

					mgScr.initCombatMode();
				}
			}
		} catch (Exception e) {
			mgScr.loadingUtils.nullError("CHECK_FOR_UNIQUE: " + mgScr.mgVar.dialogue.getCurrentAddress());
		}
	}

	private boolean isItemAcquired(int triggeredEvent, int acqNum, boolean persistEvent) {
		if (persistEvent) {
			return (mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(triggeredEvent)
					&& !mgScr.mgVar.player.getItemsAcquired().contains(acqNum))
					&& !(mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(acqNum)
							|| mgScr.mgVar.conscientia.getConscVar().persistentItemsAndEvents.contains(triggeredEvent));
		} else
			return mgScr.mgVar.conscientia.getConscVar().triggeredEvents.get(triggeredEvent)
					&& !mgScr.mgVar.player.getItemsAcquired().contains(acqNum);
	}

	private void addItem(int itemType, int itemNum) {
		mgScr.mgVar.soundManager.playSFX(SoundManager.SFX_ITEM_ACQUIRED);
		mgScr.mgVar.gameState = mgScr.DISPLAY_ITEM;
		// Disabled until display complete
		mgScr.mgVar.pauseHoverImg.setTouchable(Touchable.disabled);

		// adds item to list
		mgScr.mgVar.player.getItemsAcquired().add(itemNum);
		mgScr.mgVar.itemToDisplay = itemType;
		mgScr.mgVar.itemToDisplaySetUpComplete = false;

		switch (itemType) {
		case ITEM_GLYPH:
			mgScr.mgVar.item = new Glyph(itemNum, mgScr);
			mgScr.mgVar.hasGlyphs = true;
			break;
		case ITEM_TOME:
			mgScr.mgVar.item = new Log(itemNum, mgScr);
			// turns the logs option on in the pause menu
			mgScr.mgVar.hasLogs = true;
			break;
		}

		mgScr.mgVar.fileRW.writer.gameSave();
	}

	private void activateMaps() {
		// hasMaps is true
		mgScr.mgVar.conscientia.getConscVar().triggeredEvents.put(0, true);
		mgScr.mgVar.hasMaps = true;
	}

	private boolean checkForMindEditing() {
		// checks all addresses in mind editing category to see if they match
		// the current address
		for (String category : mgScr.mgVar.cues.keySet())
			if (category.equals("MIND EDIT"))
				for (String address : mgScr.mgVar.cues.get(category)) {
					if (mgScr.mgVar.dialogue.getCurrentAddress().equals(address))
						return true;
				}

		return false;
	}
}
