package com.ec.conscientia.entities;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;

public class MindscapeMenu {
	private Window mindscapeNPCWindow;
	private MainGameScreen mgScr;
	private FileIOManager fileRW;

	public MindscapeMenu(Skin skin, final MainGameScreen mgScr, final PauseMenu pauseMenu) {
		this.mgScr = mgScr;
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);

		mindscapeNPCWindow = new Window("", skin, "no_bg");
		mindscapeNPCWindow.setMovable(false);
		mindscapeNPCWindow.setSize(mgScr.getStagePause().getWidth() * .9f, mgScr.getStagePause().getHeight() * .9f);
		mindscapeNPCWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (mindscapeNPCWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (mindscapeNPCWindow.getHeight() / 2));

		// area for Mindscape NPC img and version info
		final Window visualAreaWindow = new Window("", skin);
		visualAreaWindow.setMovable(false);
		visualAreaWindow.setSize((2 * mindscapeNPCWindow.getWidth()) / 3, mindscapeNPCWindow.getHeight() / 4);
		final Window spacerWindow = new Window("", skin, "no_bg");
		spacerWindow.setMovable(false);

		// glyph image
		final ImageButtonStyle imgMindscapeNPCBS = new ImageButtonStyle();
		final ImageButton mindscapeNPCImgAreaButton = new ImageButton(imgMindscapeNPCBS);
		final Label mindscapeNPCVersionLabel;
		if (mgScr.getConscientia().isUseAltFont())
			mindscapeNPCVersionLabel = new Label("", skin, "whiners_default");
		else
			mindscapeNPCVersionLabel = new Label("", skin);

		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(mindscapeNPCImgAreaButton)
				.size((6 * visualAreaWindow.getWidth()) / 21, visualAreaWindow.getHeight()).align(Align.left).pad(15);
		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(mindscapeNPCVersionLabel)
				.size((13 * visualAreaWindow.getWidth()) / 21, visualAreaWindow.getHeight()).align(Align.topLeft);

		// text explanation area
		final Label textExplanationArea;
		if (mgScr.getConscientia().isUseAltFont())
			textExplanationArea = new Label("", skin, "whiners_default");
		else
			textExplanationArea = new Label("", skin);
		textExplanationArea.setWrap(true);
		textExplanationArea.setAlignment(Align.topLeft);
		final Window textExpWindow = new Window("", skin);
		textExpWindow.setMovable(false);
		final ScrollPane textExpAreaScrollPane = new ScrollPane(textExplanationArea, skin, "no_bg");
		textExpWindow.add(textExpAreaScrollPane).grow().row();
		// list area
		Window listAreaWindow = new Window("", skin, "no_bg");
		listAreaWindow.setMovable(false);
		TextField mindscapeNPCs = new TextField("TRUEFLESH", skin, "title");
		mindscapeNPCs.setDisabled(true);

		final List<MindscapeNPC> list;
		if (mgScr.getConscientia().isUseAltFont())
			list = new List<MindscapeNPC>(skin, "whiners_no_bg");
		else
			list = new List<MindscapeNPC>(skin, "no_bg");
		ArrayList<MindscapeNPC> tempList = new ArrayList<MindscapeNPC>();
		for (Integer i : mgScr.getPlayer().getItemsAcquired())
			if (i > MindscapeNPC.MAX_LOG && i <= MindscapeNPC.MAX_MINDSCAPE_NPC)
				tempList.add(new MindscapeNPC(i, mgScr));

		MindscapeNPC[] mindscapeNPCsList = fileRW.loadMindscapeNPCs(tempList);
		mindscapeNPCsList = sortList(tempList);
		// there was a crash because of a null pointer exception once, but I
		// cannot replicate it right now so it remains unfixed
		list.setItems(mindscapeNPCsList);

		ScrollPane listPane = new ScrollPane(list, skin);
		listPane.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// clear any image that was already there
				MindscapeNPC selectedNPC = list.getSelected();
				mindscapeNPCImgAreaButton.clear();
				imgMindscapeNPCBS.up = selectedNPC.getImg().getDrawable();
				mindscapeNPCVersionLabel.setText(selectedNPC.getTitle());
				textExplanationArea.setText("\t" + selectedNPC.getExplanationText());
				// resets scrollbar up to the top
				textExpAreaScrollPane.setScrollPercentY(0);
			}
		});

		listAreaWindow.add(mindscapeNPCs).expandX().fillX().row();
		listAreaWindow.add(listPane).grow();
		listAreaWindow.align(Align.left);

		// back button
		final TextButton back;
		if (mgScr.getConscientia().isUseAltFont())
			back = new TextButton("Back", skin, "whiners_default");
		else
			back = new TextButton("Back", skin);
		back.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				pauseMenu.setDisplayWindow(PauseMenu.PAUSE_WINDOW);
			}
		});
		// resume button
		final TextButton talk;
		if (mgScr.getConscientia().isUseAltFont())
			talk = new TextButton("Meditate", skin, "whiners_default");
		else
			talk = new TextButton("Meditate", skin);
		talk.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!mgScr.isInMindscape()) {
					// sets selected NPC as NPC to talk to and passes the
					// lastLocation && lastNPC variable
					MindscapeNPC selectedMindscapeNPC = list.getSelected();
					NPC targetMindscapeNPC = new NPC(selectedMindscapeNPC.getNPCid(), mgScr);
					NPC switchedToNPC = new NPC(9999, mgScr);// description
					mgScr.mgVar.lastAddBeforeMindEntry = mgScr.getDialogue().getCurrentAddress();
					mgScr.mgVar.lastNPCBeforeMindEntry = mgScr.getCurrentNPC();

					// saves currentLocation and currentNPC
					fileRW.mindscapeSave();

					// sets new loc and NPC
					// if player was already in the mindscape, they get
					// inception ending
					if (mgScr.mgVar.lastAddBeforeMindEntry.contains("MINDSCAPE")
							|| mgScr.mgVar.lastAddBeforeMindEntry.contains("NETHER EDGE")) {
						// set to description
						mgScr.setCurrentNPC(switchedToNPC.getIDnum());
						mgScr.getDialogue().setCurrentAddress(mgScr.getCues().get("INCEPTION ENDING").get(0));
					} else {
						mgScr.setCurrentNPC(switchedToNPC.getIDnum());
						mgScr.getDialogue().setCurrentAddress(
								switchedToNPC.getDialogueAddress(getMindscapeLoc(targetMindscapeNPC)));
					}

					// plays click sound
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
					mgScr.getStagePause().clear();
					mgScr.mgVar.mindscapeFadeIn = true;
					mgScr.mgVar.mindscapeFadeOut = false;
					mgScr.setGameState(mgScr.IN_PLAY);
					Gdx.input.setInputProcessor(mgScr.getStage());

					// switch screens and THEN update dialogue or else it
					// doesn't display correctly
					mgScr.getDialogue().update();
				}
			}
		});

		Window buttonArea = new Window("", skin, "no_bg");
		buttonArea.setMovable(false);
		buttonArea.add(back).size((mindscapeNPCWindow.getWidth() / 3) / 2, mindscapeNPCWindow.getHeight() * .1f);
		buttonArea.add(talk).size((mindscapeNPCWindow.getWidth() / 3) / 2, mindscapeNPCWindow.getHeight() * .1f);

		// add all elements to the big window
		mindscapeNPCWindow.add(listAreaWindow).align(Align.topLeft).grow().size(mindscapeNPCWindow.getWidth() / 3,
				mindscapeNPCWindow.getHeight() * .9f);
		mindscapeNPCWindow.add(visualAreaWindow).align(Align.topRight).grow()
				.size((2 * mindscapeNPCWindow.getWidth()) / 3, mindscapeNPCWindow.getHeight() * .4f).row();
		mindscapeNPCWindow.add(buttonArea).align(Align.bottomLeft).grow().size(mindscapeNPCWindow.getWidth() / 3,
				mindscapeNPCWindow.getHeight() * .1f);
		mindscapeNPCWindow.add(textExpWindow).align(Align.bottomRight).grow()
				.size((2 * mindscapeNPCWindow.getWidth()) / 3, mindscapeNPCWindow.getHeight() * .6f);

		// selects first item on list as item to display
		// must be here or else first item image may be too large and push title
		// off screen
		MindscapeNPC defaultmindscapeNPC = list.getSelected();
		mindscapeNPCImgAreaButton.clear();
		imgMindscapeNPCBS.up = defaultmindscapeNPC.getImg().getDrawable();
		mindscapeNPCVersionLabel.setText(defaultmindscapeNPC.getTitle());
		textExplanationArea.setText("\t" + defaultmindscapeNPC.getExplanationText());
		// resets scrollbar up to the top
		textExpAreaScrollPane.setScrollPercentY(0);
	}

	public Window getMindscapeNPCWindow() {
		return mindscapeNPCWindow;
	}

	private String getMindscapeLoc(NPC targetMindscapeNPC) {
		if (mgScr.getConscientia().getConscVar().triggeredEvents.get(19004)) {
			for (String loc : targetMindscapeNPC.getAllAddresses().keySet())
				if (loc.contains("MIND!"))
					return loc;
		}
		// virgin entrance
		else
			return "MIND!MINDSCAPE!GRAYLANDS!";

		return "NO MINDSCAPE ADDRESS";
	}

	private MindscapeNPC[] sortList(ArrayList<MindscapeNPC> tempList) {
		MindscapeNPC[] sortedList = new MindscapeNPC[tempList.size()];

		for (MindscapeNPC g : tempList) {
			int ind = 0;
			for (MindscapeNPC l : tempList)
				ind = (compare(g.getTitle(), l.getTitle()) > 0) ? ind + 1 : ind;
			sortedList[ind] = g;
		}

		return sortedList;
	}

	private int compare(String str1, String str2) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		return (res != 0) ? res : str1.compareTo(str2);
	}
}