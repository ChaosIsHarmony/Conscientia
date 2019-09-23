package com.ec.conscientia.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.ec.conscientia.Conscientia;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.dialogue.Dialogue;
import com.ec.conscientia.entities.Acquirable;
import com.ec.conscientia.entities.CombatMenu;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.entities.PauseMenu;
import com.ec.conscientia.entities.Player;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.ingameoperations.CheckUniqueEvent;
import com.ec.conscientia.ingameoperations.MusicManager;
import com.ec.conscientia.screens.MainGameScreen;

public class MainGameVar {
	public MainGameVar() {
	}

	public Stage stage, stagePause, stageCombat, stageError, stageMap;
	public Skin skin;
	public Conscientia conscientia;
	public Sprite bg, locName;
	public FileIOManager fileRW;
	public MusicManager musicManager;
	public SoundManager soundManager;

	public Table table;
	public Label dialogueLabel;
	public Label npcDialogueLabel;
	public Window dialogueArea, NPCdialogueArea, NonNPCdialogueArea;
	public ScrollPane dialogueAreaScrollPane, NPCdialogueAreaScrollPane;
	public TextButton locationArea;
	public TextButton responseOneArea, responseTwoArea, responseThreeArea, canScrollButton;
	public PauseMenu pauseMenu;
	public Window pauseButtonWindow;
	public CombatMenu combatMenu;
	public Window statsWindow;
	public ImageButton pauseHoverImg;

	// an instance of itself to send to PauseMenu
	public MainGameScreen mgScr;
	public CheckUniqueEvent uniqueEvent;

	// FROM OLD READER
	public Player player;
	public Dialogue dialogue;
	public int currentNPC;
	// string to keep currentLocation for NPC purposes
	public String currentLocation;

	// NPCs and whatnot in the area
	public ArrayList<NPC> currentAreaNPCs;
	public HashMap<String, Integer> NPCbyNum;
	// Special Event Cues
	public HashMap<String, ArrayList<String>> cues;

	public int gameState;

	// for menus
	public boolean hasLogs, hasIntrospection, hasGlyphs, hasMaps;
	// for music
	public boolean isLooney;
	// for scrolling functionality
	public boolean canScroll, initLoading;
	// for mindscape
	public String lastAddBeforeMindEntry;
	public int lastNPCBeforeMindEntry;
	// for awareness/uber-viracocha
	public int awareness;

	// for debugging
	public Label stats;

	// used so that the Dialogue display area is only reset when NPC changes
	public int newNPC = -1;

	public float globalR, globalG, globalB, globalA;

	public boolean playerVictorious, fadeInFromCombat;

	public int itemToDisplay;
	public boolean itemToDisplaySetUpComplete, itemFadeIn, itemFadeOut;
	public Acquirable item;
	public float itemColorValue;
	public Window itemWindow;
	public long holdDisplay;

	// SCREEN EFFECTS
	public float fadeSpeed;
	public float globalColorValues;
	public boolean fadeComplete, fadeBackInComplete, showLocationNameComplete;

	public String oldArea = null;
	public boolean checkedLocChange = false, locChanged = false;

	public long locNameTimer;

	public Random rand = new Random();

	public long flickerStartTime;

	// boolean to keep the gameLoop static when nothing has been clicked
	public boolean clicked;

	// click timer to avoid double clicks
	public boolean isClickable;
	public long lastClick;

	// used to pare the unique event checks down to only a few per cycle
	public boolean checkItemAcquired, inMindscape, mindscapeFadeIn, mindscapeFadeOut, mindscapeExit;

	// used to dynamically change the up drawable on buttons selected from
	// list
	public boolean isSelected = false;
	public String selectedAreaName = "";
}
