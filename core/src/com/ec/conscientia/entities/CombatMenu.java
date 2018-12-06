package com.ec.conscientia.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class CombatMenu {

	private Window combatWindow;
	private ScrollPane battleLogScrollPane;
	private Label battleLog, canScrollLabel;
	private Enemy enemy;
	public final static int BATTLE_LOG_LENGTH = 48;
	private String battleOutcome;
	private TextButton proceed;
	private MainGameScreen mgScr;
	private FileIOManager fileRW;

	public CombatMenu(final Skin skin, int npc, MainGameScreen mgScr) {
		this.mgScr = mgScr;
		this.fileRW = new FileIOManager(mgScr.getConscientia());

		battleOutcome = "";

		// SETUP ENEMY
		enemy = new Enemy(npc, mgScr);

		// SETUP WINDOW
		setupWindow(skin);

		// endsCombat and changes game state
		endCombat = false;
		// Resets initial and turn-based variables
		playerVictorious = false;
		canUpdate = true;
		canUpdateEndCombat = false;
		clearText = false;
	}

	private void setupWindow(Skin skin) {
		combatWindow = new Window("", skin, "no_bg");
		combatWindow.setMovable(false);
		combatWindow.setSize(mgScr.getStagePause().getWidth() * .9f, mgScr.getStagePause().getHeight() * .9f);
		combatWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (combatWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (combatWindow.getHeight() / 2));

		// combat battle log
		Window combatBattleLogWindow = new Window("", skin, "no_bg");
		combatBattleLogWindow.setMovable(false);
		TextField battleLogTitle = new TextField("BATTLE LOG", skin);
		battleLogTitle.setDisabled(true);
		battleLogTitle.setAlignment(Align.center);
		Window textAreaWindow = new Window("", skin);
		textAreaWindow.setMovable(false);
		battleLog = new Label(battleOutcome, skin);
		battleLog.setWrap(true);
		battleLog.setTouchable(Touchable.disabled);
		battleLogScrollPane = new ScrollPane(battleLog, skin, "no_bg");
		canScrollLabel = new Label(" \\/ ", skin);
		canScrollLabel.addListener(new ClickListener() {
			// advances scroll by 50%
			public void clicked(InputEvent event, float x, float y) {
				canScrollLabel.setText(" \\\\// ");
				battleLogScrollPane
						.setScrollY(battleLogScrollPane.getScrollY() + (battleLogScrollPane.getHeight() * .86f));
			}

			// doubles up on the scroll area when focused
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (pointer == -1)
					canScrollLabel.setText(" \\\\// ");
			}

			// resets to normal if not focused
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (pointer == -1)
					canScrollLabel.setText(" \\/ ");
			}
		});
		canScrollLabel.setVisible(false);
		textAreaWindow.add(battleLogScrollPane).grow().row();
		textAreaWindow.add(canScrollLabel).align(Align.center).expandX().setActorHeight(5);

		// Act button
		proceed = new TextButton("PROCEED", skin);
		proceed.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				// shows the next phase of the battle (if multiphased)
				canUpdate = true;
				clearText = true;
			}
		});
		proceed.setTouchable(Touchable.disabled);

		combatBattleLogWindow.add(battleLogTitle).height(combatWindow.getHeight() / 10).align(Align.center).expandX()
				.fillX().row();
		combatBattleLogWindow.add(textAreaWindow).grow().row();
		combatBattleLogWindow.add(proceed).height(combatWindow.getHeight() / 10).align(Align.center).expandX().fillX()
				.row();

		combatWindow.add(combatBattleLogWindow).size((2 * combatWindow.getWidth()) / 3, combatWindow.getHeight())
				.align(Align.center);
	}

	private boolean endCombat, playerVictorious, canUpdate, canUpdateEndCombat, clearText;
	private String combatEndAddress;

	public boolean update() {
		// to see if there is more text to scroll
		canScrollLabel.setVisible((battleLogScrollPane.getScrollPercentY() < 1f) ? true : false);
		// clears Text
		if (clearText)
			battleLog.setText("");

		// Deals with the consequences of victory or defeat by setting all
		// relevant variables as true or false
		if (endCombat) {
			if (canUpdateEndCombat && canUpdate) {
				// if won
				mgScr.mgVar.globalColorValues = playerVictorious ? 1 : 0;
				// kills enemy or kills Eidos
				switch (enemy.getID()) {
				// BOOK OF EIDOS
				// MINI BOSSES
				case Enemy.GULGANNA:
					// Quangjo is dead
					mgScr.getConscientia().getConscVar().triggeredEvents.put(11429, true);
					// Fought Gulganna
					mgScr.getConscientia().getConscVar().triggeredEvents.put(11436, true);
					// W Luin
					if (playerVictorious) {
						// W/ Quangjo
						if (mgScr.getConscientia().getConscVar().triggeredEvents.get(11408))
							combatEndAddress = "KABU!WELLSPRING!SANCTUM OF GULGANNA!15.000!DESCRIPTION!";
						// W/o Quangjo
						else
							combatEndAddress = "KABU!WELLSPRING!SANCTUM OF GULGANNA!1.000!DESCRIPTION!";
					}
					// W/o Luin
					else {
						// W/ Quangjo
						if (mgScr.getConscientia().getConscVar().triggeredEvents.get(11408))
							combatEndAddress = "KABU!WELLSPRING!SANCTUM OF GULGANNA!15.2000!DESCRIPTION!";
						// W/o Quangjo
						else
							combatEndAddress = "KABU!WELLSPRING!SANCTUM OF GULGANNA!2.000!DESCRIPTION!";
					}
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();
				case Enemy.ARK:
					if (playerVictorious) {
						// if acquired Jormungund
						if (mgScr.getConscientia().getConscVar().triggeredEvents.get(1000)) {
							// Absorbed Hel
							mgScr.getPlayer().getItemsAcquired().add(Acquirable.HEL);
							// Acquired Hel
							mgScr.getConscientia().getConscVar().triggeredEvents.put(16003, true);
						}
						// Defeated Ark
						mgScr.getConscientia().getConscVar().triggeredEvents.put(16012, true);
						combatEndAddress = "KABU!UR'RUK!DUNGEON OF THE VOID!100.X000!DESCRIPTION!";
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else
						combatEndAddress = mgScr.getCues().get("DRAUG END").get(0);
					return dialogueReset();

				// BEASTMAN
				case Enemy.ADARIN:
					// Fought with Adarin
					mgScr.getConscientia().getConscVar().triggeredEvents.put(14005, true);
					// Killed Adarin
					mgScr.getConscientia().getConscVar().triggeredEvents.put(14501, true);
					combatEndAddress = mgScr.getCues().get("ADARIN DEATH").get(0);
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();

				// DAZIR NPCs
				case Enemy.KHAA:
					// If already not welcome in Dazir
					if (mgScr.getConscientia().getConscVar().triggeredEvents.get(10900))
						mgScr.getConscientia().getConscVar().triggeredEvents.put(10998, true);
					else {
						// not welcome in Dazir
						mgScr.getConscientia().getConscVar().triggeredEvents.put(10900, true);
						// not welcome in Tambul
						mgScr.getConscientia().getConscVar().triggeredEvents.put(13900, true);
					}
					// Khaa dead
					mgScr.getConscientia().getConscVar().triggeredEvents.put(10999, playerVictorious ? true : false);
					// if Khaa dies, refugees go to Tambul and abandon Dazir
					if (mgScr.getConscientia().getConscVar().triggeredEvents.get(10999))
						mgScr.getConscientia().getConscVar().triggeredEvents.put(13900, true);
					// killed by him, takes you back to Sanctuary
					// kill him, refugees go to Tambul
					if (playerVictorious) {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!DAZIR!ARBORETUM!":
							combatEndAddress = "KABU!DAZIR!ARBORETUM!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ARTISANS' MANSION!":
							combatEndAddress = "KABU!DAZIR!ARTISANS' MANSION!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ARTISAN'S PLAZA!":
							combatEndAddress = "KABU!DAZIR!ARTISAN'S PLAZA!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ATRIUM!":
							combatEndAddress = "KABU!DAZIR!ATRIUM!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!GATES OF DAZIR!":
							combatEndAddress = "KABU!DAZIR!GATES OF DAZIR!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!MAGE'S ABODE!":
							combatEndAddress = "KABU!DAZIR!MAGE'S ABODE!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!SLUMS!":
							combatEndAddress = "KABU!DAZIR!SLUMS!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!DAZIR!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!WORKSHOP!":
							combatEndAddress = "KABU!DAZIR!WORKSHOP!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!WORKSHOP EXTERIOR!":
							combatEndAddress = "KABU!DAZIR!WORKSHOP EXTERIOR!999.X000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
						// Khaa is dead
						mgScr.getConscientia().getConscVar().triggeredEvents.put(10999, true);
					} else {
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					}
					return dialogueReset();

				case Enemy.QUANGJO:
					// Fought Quangjo
					mgScr.getConscientia().getConscVar().triggeredEvents.put(11432, true);
					// Quangjo dead
					mgScr.getConscientia().getConscVar().triggeredEvents.put(11429, playerVictorious ? true : false);
					// set address by area
					if (playerVictorious) {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!WELLSPRING!DEEP LAKE!":
							combatEndAddress = "KABU!WELLSPRING!DEEP LAKE!45.000!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!DROWNED ARCHIVE!":
							combatEndAddress = "KABU!WELLSPRING!DROWNED ARCHIVE!30.000!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!FLOOD GATE!":
							combatEndAddress = "KABU!WELLSPRING!FLOOD GATE!20.000!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!RUINED ARCHIVE!":
							combatEndAddress = "KABU!WELLSPRING!RUINED ARCHIVE!30.X000!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!SANCTUM OF GULGANNA!":
							combatEndAddress = "KABU!WELLSPRING!SANCTUM OF GULGANNA!20.000!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!THRONE ROOM!":
							combatEndAddress = "KABU!WELLSPRING!THRONE ROOM!40.X000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else {
						if (mgScr.getCurrentLocation().contains("THRONE ROOM"))
							combatEndAddress = "KABU!WELLSPRING!THRONE ROOM!50.000!DESCRIPTION!";
						else if (mgScr.getCurrentLocation().contains("DEEP LAKE"))
							combatEndAddress = "KABU!WELLSPRING!THRONE ROOM!40.000!DESCRIPTION!";
						else
							combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					}
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();

				case Enemy.MERT:
				case Enemy.JERHAAK:
				case Enemy.CETIN:
				case Enemy.SERPIL:
				case Enemy.AYDIN:
				case Enemy.COZKUIN:
				case Enemy.PEMBE:
				case Enemy.RASHAK:
				case Enemy.GALIP:
				case Enemy.HAZAN:
				case Enemy.DIYGU:
				case Enemy.BEHIYE:
				case Enemy.EGIMEN:
				case Enemy.SIDIKA:
				case Enemy.KORAY:
				case Enemy.KADIR:
				case Enemy.RADA:
				case Enemy.ARKARA:
				case Enemy.ASANSOR:
					// Not welcome in Dazir
					mgScr.getConscientia().getConscVar().triggeredEvents.put(10900, true);
					// Loads Khaa fight dialogue for area you're in
					if (playerVictorious) {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!DAZIR!ARBORETUM!":
							combatEndAddress = "KABU!DAZIR!ARBORETUM!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ARTISANS' MANSION!":
							combatEndAddress = "KABU!DAZIR!ARTISANS' MANSION!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ARTISAN'S PLAZA!":
							combatEndAddress = "KABU!DAZIR!ARTISAN'S PLAZA!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!ATRIUM!":
							combatEndAddress = "KABU!DAZIR!ATRIUM!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!MAGE'S ABODE!":
							combatEndAddress = "KABU!DAZIR!MAGE'S ABODE!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!SLUMS!":
							combatEndAddress = "KABU!DAZIR!SLUMS!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!DAZIR!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAZIR!WORKSHOP!":
							combatEndAddress = "KABU!DAZIR!WORKSHOP!0099.000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					}
					// or if, for some bizarre reason, the NPC killed you
					else
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();

				case Enemy.DORUK:
					// killed Doruk
					mgScr.getConscientia().getConscVar().triggeredEvents.put(10501, true);
					combatEndAddress = playerVictorious ? "KABU!CANYON!MOUNTAIN PASS!1.000!DESCRIPTION!"
							: mgScr.getCues().get("DEATH").get(0);
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();

				// TAMBUL NPCs
				case Enemy.PAKAHRON:
					// not welcome in Dazir
					mgScr.getConscientia().getConscVar().triggeredEvents.put(10900, true);
					// not welcome in Tambul
					mgScr.getConscientia().getConscVar().triggeredEvents.put(13900, true);
					// Famlicus disappears if you fought Pakharon without
					// talking about the poison
					mgScr.getConscientia().getConscVar().triggeredEvents.put(17900,
							(mgScr.getConscientia().getConscVar().triggeredEvents.get(13902) ? false : true));
					// Talked to Fam w/o fighting Pak
					mgScr.getConscientia().getConscVar().triggeredEvents.put(17005, false);
					if (playerVictorious) {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!TAMBUL!ARCHIVES!":
							combatEndAddress = "KABU!TAMBUL!ARCHIVES!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!ATRIUM!":
							combatEndAddress = "KABU!TAMBUL!ATRIUM!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!HYDROPONIC FARM!":
							combatEndAddress = "KABU!TAMBUL!HYDROPONIC FARM!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!MOON KEEP!":
							combatEndAddress = "KABU!TAMBUL!MOON KEEP!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!MOON TOWER!":
							// Decided to stay in Moon Tower
							if (mgScr.getConscientia().getConscVar().triggeredEvents.get(13804))
								combatEndAddress = "KABU!TAMBUL!MOON TOWER!999.001!DESCRIPTION!";
							else
								combatEndAddress = "KABU!TAMBUL!MOON TOWER!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!RAMPARTS!":
							combatEndAddress = "KABU!TAMBUL!RAMPARTS!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!RESIDENCE DISTRICT!":
							combatEndAddress = "KABU!TAMBUL!RESIDENCE DISTRICT!999.X000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!TAMBUL!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
						// Pak is dead
						mgScr.getConscientia().getConscVar().triggeredEvents.put(13999, true);
					} else {
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
						// killed by pak
						mgScr.getConscientia().getConscVar().triggeredEvents.put(13901, true);
					}
					return dialogueReset();
				case Enemy.KALEKI:
				case Enemy.BAHADUR:
				case Enemy.NIYOOSH:
				case Enemy.ARGHAVAN:
				case Enemy.DARY:
				case Enemy.REBA:
				case Enemy.NOURI:
				case Enemy.FARRIN:
				case Enemy.AKKEBER:
				case Enemy.ABLAH:
				case Enemy.KAMBIN:
				case Enemy.SORUSH:
				case Enemy.BRUS:
				case Enemy.KAGIN:
				case Enemy.ISMAT:
				case Enemy.BOZ:
				case Enemy.ENTEGHAD:
					// not welcome in Tambul
					mgScr.getConscientia().getConscVar().triggeredEvents.put(13900, true);
					// what happens after murder/attempted murder
					if (playerVictorious) {
						// Pakharon finds you and kills you
						// create dialogues for different areas
						switch (mgScr.getCurrentLocation()) {
						case "KABU!TAMBUL!ARCHIVES!":
							combatEndAddress = "KABU!TAMBUL!ARCHIVES!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!ATRIUM!":
							combatEndAddress = "KABU!TAMBUL!ATRIUM!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!BRIDGE!":
							combatEndAddress = "KABU!TAMBUL!BRIDGE!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!HYDROPONIC FARM!":
							combatEndAddress = "KABU!TAMBUL!HYDROPONIC FARM!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!MOON KEEP!":
							combatEndAddress = "KABU!TAMBUL!MOON KEEP!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!MOON TOWER!":
							combatEndAddress = "KABU!TAMBUL!MOON TOWER!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!RAMPARTS!":
							combatEndAddress = "KABU!TAMBUL!RAMPARTS!0099.000!DESCRIPTION!";
						case "KABU!TAMBUL!RESIDENCE DISTRICT!":
							combatEndAddress = "KABU!TAMBUL!RESIDENCE DISTRICT!0099.000!DESCRIPTION!";
							break;
						case "KABU!TAMBUL!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!TAMBUL!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else
						// or if, for some bizarre reason, the NPC killed you
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();

				case Enemy.DYSMAS:
					// if Dysmas, then sets his killed event as true
					mgScr.getConscientia().getConscVar().triggeredEvents.put(13112,
							(enemy.getID() == Enemy.DYSMAS && playerVictorious) ? true : false);
					// need to save here or it will take you to the last
					// address before combat
					if (playerVictorious) {
						if (mgScr.getCurrentLocation().contains("DAZIR"))
							combatEndAddress = "KABU!DAZIR!GATES OF DAZIR!0099.000!DESCRIPTION!";
						else if (mgScr.getCurrentLocation().contains("EMERALD FONT"))
							combatEndAddress = "KABU!UR'RUK!EMERALD FONT!20.000!DESCRIPTION!";
						else if (mgScr.getCurrentLocation().contains("PORTAL OF WISDOM")) {
							// resets this event so that he won't appear again
							mgScr.getConscientia().getConscVar().triggeredEvents.put(13115, false);
							combatEndAddress = "KABU!UR'RUK!PORTAL OF WISDOM!20.000!DESCRIPTION!";
						} else if (mgScr.getCurrentLocation().contains("TOWER")) {
							// let Dysmas go, if he's not dead
							mgScr.getConscientia().getConscVar().triggeredEvents.put(13117,
									mgScr.getConscientia().getConscVar().triggeredEvents.get(13112) ? false : true);
							combatEndAddress = "KABU!TAMBUL!MOON TOWER!0099.000!DESCRIPTION!";
						} else {
							// let Dysmas go, if he's not dead
							mgScr.getConscientia().getConscVar().triggeredEvents.put(13117,
									mgScr.getConscientia().getConscVar().triggeredEvents.get(13112) ? false : true);
							combatEndAddress = "KABU!TAMBUL!MOON KEEP!0099.000!DESCRIPTION!";
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else
						// or if, for some bizarre reason, the NPC killed you
						combatEndAddress = (mgScr.getCurrentLocation().contains("UR'RUK"))
								? mgScr.getCues().get("DRAUG END").get(0) : mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();

				// TACRIBA NPCs
				case Enemy.KHLUTT:
					// not welcome in Dazir
					mgScr.getConscientia().getConscVar().triggeredEvents.put(10900, true);
					// not welcome in Tambul
					mgScr.getConscientia().getConscVar().triggeredEvents.put(13900, true);
					// not welcome in Tacriba
					mgScr.getConscientia().getConscVar().triggeredEvents.put(15900, true);
					// killed by him, takes you back to Sanctuary
					// kill him, ends game
					if (playerVictorious) {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!DAWN FORTRESS!AMPHITHEATRE!":
							combatEndAddress = "KABU!DAWN FORTRESS!AMPHITHEATRE!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!ARCHIVES!":
							combatEndAddress = (mgScr.getConscientia().getConscVar().triggeredEvents.get(15901)
									? "KABU!DAWN FORTRESS!ARCHIVES!999.X000!DESCRIPTION!"
									: "KABU!DAWN FORTRESS!ARCHIVES!15.000!DESCRIPTION!");
							break;
						case "KABU!DAWN FORTRESS!BARRACKS!":
							combatEndAddress = "KABU!DAWN FORTRESS!BARRACKS!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!COURTYARD!":

							combatEndAddress = "KABU!DAWN FORTRESS!COURTYARD!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!GATES OF DAWN!":
							combatEndAddress = "KABU!DAWN FORTRESS!GATES OF DAWN!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!MAGE'S ABODE!":
							combatEndAddress = "KABU!DAWN FORTRESS!MAGE'S ABODE!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!PROVING GROUNDS!":
							combatEndAddress = "KABU!DAWN FORTRESS!PROVING GROUNDS!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!SUN KEEP!":
							combatEndAddress = "KABU!DAWN FORTRESS!SUN KEEP!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!WALLS!":
							combatEndAddress = "KABU!DAWN FORTRESS!WALLS!999.X000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
						// Khlutt is dead
						mgScr.getConscientia().getConscVar().triggeredEvents.put(15999, true);
					} else {
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					}
					return dialogueReset();
				case Enemy.LOGIRA:
					// Logira is dead
					mgScr.getConscientia().getConscVar().triggeredEvents.put(15901, (playerVictorious) ? true : false);
				case Enemy.YARMAK:
				case Enemy.FIDAN:
				case Enemy.MINAH:
				case Enemy.FWAYYA:
					if (mgScr.getCurrentLocation().equals("KABU!WASTELAND!SALT FOREST!")) {
						combatEndAddress = (playerVictorious) ? "KABU!WASTELAND!SALT FOREST!99.X00!DESCRIPTION!"
								: "MIND!NETHER EDGE!ATELIER!0.X000!DESCRIPTION!";
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
						return dialogueReset();
					}
				case Enemy.RUHI:
				case Enemy.ISSAM:
				case Enemy.CIRE:
				case Enemy.QUST:
				case Enemy.MALIK:
					// not welcome in Tacriba
					mgScr.getConscientia().getConscVar().triggeredEvents.put(15900, true);
					// what happens after murder/attempted murder
					if (playerVictorious) {
						// Khlutt finds you and kills you
						// create dialogues for different areas
						switch (mgScr.getCurrentLocation()) {
						case "KABU!DAWN FORTRESS!AMPHITHEATRE!":
							combatEndAddress = "KABU!DAWN FORTRESS!AMPHITHEATRE!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!ARCHIVES!":
							combatEndAddress = "KABU!DAWN FORTRESS!ARCHIVES!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!BARRACKS!":
							combatEndAddress = "KABU!DAWN FORTRESS!BARRACKS!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!COURTYARD!":
							combatEndAddress = "KABU!DAWN FORTRESS!COURTYARD!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!PROVING GROUNDS!":
							combatEndAddress = "KABU!DAWN FORTRESS!PROVING GROUNDS!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!SUN KEEP!":
							combatEndAddress = "KABU!DAWN FORTRESS!SUN KEEP!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!":
							combatEndAddress = "KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!";
							break;
						case "KABU!DAWN FORTRESS!WALLS!":
							combatEndAddress = "KABU!DAWN FORTRESS!WALLS!0099.000!DESCRIPTION!";
							break;
						}
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else
						// or if, for some bizarre reason, the NPC killed you
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();
				case Enemy.DIYA:
				case Enemy.IMAT:
				case Enemy.XERK:
					// not welcome in Tacriba
					mgScr.getConscientia().getConscVar().triggeredEvents.put(15900, true);
					combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();
				case Enemy.KARRA:
					if (playerVictorious)
						// Killed Karra
						mgScr.getConscientia().getConscVar().triggeredEvents.put(14500, true);

					combatEndAddress = playerVictorious ? "KABU!WASTELAND!CRESCENT CANYON!0.X500!DESCRIPTION!"
							: mgScr.getCues().get("DRAUG END").get(0);
					;

					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();
				case Enemy.ADARIK:
				case Enemy.BISHRA:
					combatEndAddress = mgScr.getCues().get("DRAUG END").get(0);
					return dialogueReset();
				case Enemy.HEYAR:
					// killed Heyar
					mgScr.getConscientia().getConscVar().triggeredEvents.put(16023,
							((playerVictorious) ? true : false));
					// sets post kill address
					if (mgScr.getCurrentLocation().contains("KABU!WASTELAND!ARK'S BEACON!"))
						combatEndAddress = playerVictorious ? "KABU!WASTELAND!ARK'S BEACON!90.X000!DESCRIPTION!"
								: "MIND!NETHER EDGE!ATELIER!0.X000!DESCRIPTION!";
					else if (mgScr.getCurrentLocation().contains("KABU!WASTELAND!CRESCENT CANYON!"))
						combatEndAddress = playerVictorious ? "KABU!WASTELAND!CRESCENT CANYON!90.X000!DESCRIPTION!"
								: "MIND!NETHER EDGE!ATELIER!0.X000!DESCRIPTION!";
					else if (mgScr.getCurrentLocation().contains("KABU!WASTELAND!GATES OF DAWN!"))
						combatEndAddress = playerVictorious ? "KABU!WASTELAND!GATES OF DAWN!90.X000!DESCRIPTION!"
								: "MIND!NETHER EDGE!ATELIER!0.X000!DESCRIPTION!";
					else
						combatEndAddress = playerVictorious ? "KABU!WASTELAND!THE VEDT!90.X000!DESCRIPTION!"
								: "MIND!NETHER EDGE!ATELIER!0.X000!DESCRIPTION!";
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();
				case Enemy.ASSALA:
				case Enemy.HUBBIYH:
				case Enemy.NADUB:
					if (playerVictorious) {
						// killed Guards
						mgScr.getConscientia().getConscVar().triggeredEvents.put(14412, true);
						combatEndAddress = "KABU!CANYON!VALLEY OF BONES!90.001!DESCRIPTION!";
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					} else {
						// fought guards and lost
						mgScr.getConscientia().getConscVar().triggeredEvents.put(14413, true);
						combatEndAddress = mgScr.getCues().get("DEATH").get(0);
					}
					return dialogueReset();

				// UR'RUK
				case Enemy.HAZANNA:
				case Enemy.KALEKO:
				case Enemy.EGIMESH:
				case Enemy.ARKASH:
				case Enemy.BIRARKUL:
				case Enemy.RADAN:
				case Enemy.MAGDA:
				case Enemy.WHABYN:
				case Enemy.GAPA:
				case Enemy.KAHIN:
				case Enemy.NAKARA:
				case Enemy.SAMESH:
					// Refused entry into Ur'Ruk
					mgScr.getConscientia().getConscVar().triggeredEvents.put(16017, true);
					switch (mgScr.getCurrentLocation()) {
					case "KABU!UR'RUK!EMERALD FONT!":
						combatEndAddress = "KABU!UR'RUK!EMERALD FONT!20.000!DESCRIPTION!";
						break;
					case "KABU!UR'RUK!GREENHOUSE!":
						combatEndAddress = "KABU!UR'RUK!GREENHOUSE!20.000!DESCRIPTION!";
						break;
					case "KABU!UR'RUK!HALL OF VANARGAND!":
						combatEndAddress = "KABU!UR'RUK!HALL OF VANARGAND!20.000!DESCRIPTION!";
						break;
					case "KABU!UR'RUK!LIVING QUARTERS!":
						combatEndAddress = "KABU!UR'RUK!LIVING QUARTERS!20.000!DESCRIPTION!";
						break;
					case "KABU!UR'RUK!STONE CIRCLE!":
						combatEndAddress = "KABU!UR'RUK!STONE CIRCLE!20.000!DESCRIPTION!";
						break;
					}
					// need to save here or it will take you to the last
					// address before combat
					fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					return dialogueReset();

				// MISC NPCs
				case Enemy.SENTRY:
					mgScr.getConscientia().getConscVar().triggeredEvents.put(12006, (playerVictorious ? true : false));
					// player was killed by sentry leads to new sentry dialogue
					mgScr.getConscientia().getConscVar().triggeredEvents.put(12999, (playerVictorious ? false : true));
					combatEndAddress = playerVictorious ? mgScr.getCues().get("SENTRY DEATH").get(0)
							: mgScr.getCues().get("DEATH").get(0);
					return dialogueReset();
				case Enemy.CLOCKWORK_CROWS:
					if (playerVictorious) {
						mgScr.getConscientia().getConscVar().triggeredEvents.put(11434, true);
						switch (mgScr.getCurrentLocation()) {
						case "KABU!WELLSPRING!GATE OF THE HEATHEN!":
							combatEndAddress = "KABU!WELLSPRING!GATE OF THE HEATHEN!9990.X11434!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!LIVING QUARTERS!":
							combatEndAddress = "KABU!WELLSPRING!LIVING QUARTERS!9990.X11434!DESCRIPTION!";
							break;
						}
					} else {
						switch (mgScr.getCurrentLocation()) {
						case "KABU!WELLSPRING!GATE OF THE HEATHEN!":
							combatEndAddress = "KABU!WELLSPRING!GATE OF THE HEATHEN!502.200!DESCRIPTION!";
							break;
						case "KABU!WELLSPRING!LIVING QUARTERS!":
							combatEndAddress = "KABU!WELLSPRING!LIVING QUARTERS!502.200!DESCRIPTION!";
							break;
						}
						// put here so that there is no white in, respawn flash
						playerVictorious = true;
						// need to save here or it will take you to the last
						// address before combat
						fileRW.saveNPCstats("DESCRIPTION", combatEndAddress, mgScr.getCurrentLocation());
					}
					return dialogueReset();
				case Enemy.MATHELIAN:
					// put here so that there is white in, respawn flash
					playerVictorious = false;
					combatEndAddress = mgScr.getCues().get("DEATH").get(CommonVar.EIDOS_DEATH_RESPAWN);
					fileRW.saveNPCstats("MATHELIAN", "KABU!WELLSPRING!REFECTORY OF THE VALVORTHR!0.X000!MATHELIAN!",
							mgScr.getCurrentLocation());
					return dialogueReset();
				case Enemy.RIKHARR:
				case Enemy.VALVORTHR:
					// put here so that there is white in, respawn flash
					playerVictorious = false;
					// Rikharr's book
					combatEndAddress = mgScr.getCues().get("BOOK CHANGE").get(CommonVar.RIK);
					return dialogueReset();

				// BOOK OF TORMA
				case Enemy.BABEL:
					if (playerVictorious)
						combatEndAddress = "ENCLAVE!THE NAVE!ARCHONS' APSE!0.005!DESCRIPTION!";
					else
						combatEndAddress = "MIND!MINDSCAPE!THE VOID!6.500!DESCRIPTION!";
					return dialogueReset();
				case Enemy.BEAST_LORD:
				case Enemy.GATE_KEEPER:
				case Enemy.COOK_DING:
				case Enemy.FORM_FORGER:
				case Enemy.MEMORY_KEEPER:
				case Enemy.MIDNIGHT:
				case Enemy.MESSAGE_BEARER:
					combatEndAddress = "MIND!MINDSCAPE!THE VOID!6.500!DESCRIPTION!";
					return dialogueReset();
				case Enemy.MIND_SEER:
					combatEndAddress = "MIND!THE BOOK OF THETIAN!THE BEGINNING!0.X000!DESCRIPTION!";
					return dialogueReset();
				case Enemy.JINN_SLAYER:
				case Enemy.OATH_ABETTOR:
				case Enemy.CHAOS_TAMER:
				case Enemy.LAW_BRINGER:
					combatEndAddress = "ENCLAVE!THE VAULT!DISSENT'S REMORSE!0.X000!DESCRIPTION!";
					return dialogueReset();
				}
				return dialogueReset();
			} else {
				canUpdate = false;
				if (!clearText) {
					if (!canUpdateEndCombat)
						updateBattleLogLabel();
					canUpdateEndCombat = true;
				} else
					battleLog.setText("");
			}
		} else {
			if (!endCombat) {
				// check to see if player has item to satisfy win condition
				satisfiesWinCondition();
				endCombat = true;
				proceed.setTouchable(Touchable.enabled);
			}
		}
		return false;
	}

	private boolean dialogueReset() {
		mgScr.setCurrentNPC(mgScr.mgVar.NPCbyNum.get("DESCRIPTION"));
		mgScr.getDialogue().setSwitchedToNPC(mgScr.mgVar.NPCbyNum.get("DESCRIPTION"));
		mgScr.getDialogue().setCurrentAddress(
				(combatEndAddress == null) ? ("COMBAT FAILURE:" + enemy.getName()) : combatEndAddress);
		mgScr.mgVar.playerVictorious = playerVictorious;
		mgScr.getDialogue().update();
		canUpdateEndCombat = false;
		canUpdate = false;
		endCombat = false;
		return true;
	}

	private void satisfiesWinCondition() {
		int ability = 0;

		for (int i : enemy.getCombatWeaknesses()) {
			playerVictorious = mgScr.getPlayer().getItemsAcquired().contains(i);
			if (playerVictorious) {
				ability = i;
				break;
			}
		}
		// describes battle depending on win or loss
		battleOutcome = fileRW.getCombatDescription(enemy.getID(), playerVictorious, ability) + battleOutcome;
		updateBattleLogLabel();
	}

	private void updateBattleLogLabel() {
		battleLog.setText(battleOutcome);
	}

	public Window getCombatWindow() {
		return combatWindow;
	}
}
