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

public class LogMenu {
	private Window logWindow;
	private FileIOManager fileRW;

	public LogMenu(Skin skin, final MainGameScreen mgScr, final PauseMenu pauseMenu) {
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);

		logWindow = new Window("", skin, "no_bg");
		logWindow.setMovable(false);
		logWindow.setSize(mgScr.getStagePause().getWidth() * .9f, mgScr.getStagePause().getHeight() * .9f);
		logWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (logWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (logWindow.getHeight() / 2));

		// area for Log img and version info
		final Window visualAreaWindow = new Window("", skin);
		visualAreaWindow.setMovable(false);
		visualAreaWindow.setSize((2 * logWindow.getWidth()) / 3, logWindow.getHeight() / 4);
		final Window spacerWindow = new Window("", skin, "no_bg");
		spacerWindow.setMovable(false);

		// tome image
		final ImageButtonStyle imgLogBS = new ImageButtonStyle();
		final ImageButton logImgAreaButton = new ImageButton(imgLogBS);
		final Label logVersionLabel;
		if (mgScr.getConscientia().isUseAltFont())
			logVersionLabel = new Label("", skin, "whiners_default");
		else
			logVersionLabel = new Label("", skin);

		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(logImgAreaButton)
				.size((6 * visualAreaWindow.getWidth()) / 21, visualAreaWindow.getHeight()).align(Align.left).pad(15);
		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(logVersionLabel)
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
		TextField logs = new TextField("LOGS", skin, "title");
		logs.setDisabled(true);

		final List<Log> list;
		if (mgScr.getConscientia().isUseAltFont())
			list = new List<Log>(skin, "whiners_no_bg");
		else
			list = new List<Log>(skin, "no_bg");
		ArrayList<Log> tempList = new ArrayList<Log>();
		for (Integer i : mgScr.getPlayer().getItemsAcquired())
			if (i > Log.MAX_GLYPH && i <= Log.MAX_LOG)
				tempList.add(new Log(i, mgScr));

		Log[] logsList = fileRW.reader.loadLogs(tempList);
		logsList = sortList(tempList);
		list.setItems(logsList);

		ScrollPane listPane = new ScrollPane(list, skin);
		listPane.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Log selectedLog = list.getSelected();
				logImgAreaButton.clear();
				imgLogBS.up = selectedLog.getImg().getDrawable();
				logVersionLabel.setText(selectedLog.getTitle());
				textExplanationArea.setText("\t" + selectedLog.getExplanationText());
				// resets scrollbar up to the top
				textExpAreaScrollPane.setScrollPercentY(0);
			}
		});

		listAreaWindow.add(logs).expandX().fillX().row();
		listAreaWindow.add(listPane).grow();
		listAreaWindow.align(Align.left);

		// resume button
		final TextButton resume;
		if (mgScr.getConscientia().isUseAltFont())
			resume = new TextButton("Resume", skin, "whiners_default");
		else
			resume = new TextButton("Resume", skin);
		resume.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				mgScr.getStagePause().clear();
				mgScr.setGameState(mgScr.IN_PLAY);
				Gdx.input.setInputProcessor(mgScr.getStage());
			}
		});

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

		Window buttonArea = new Window("", skin, "no_bg");
		buttonArea.setMovable(false);
		buttonArea.add(resume).size((logWindow.getWidth() / 3) / 2, logWindow.getHeight() * .1f);
		buttonArea.add(back).size((logWindow.getWidth() / 3) / 2, logWindow.getHeight() * .1f);

		// add all elements to the big window
		logWindow.add(listAreaWindow).align(Align.topLeft).grow().size(logWindow.getWidth() / 3,
				logWindow.getHeight() * .9f);
		logWindow.add(visualAreaWindow).align(Align.topRight).grow()
				.size((2 * logWindow.getWidth()) / 3, logWindow.getHeight() * .4f).row();
		logWindow.add(buttonArea).align(Align.bottomLeft).grow().size(logWindow.getWidth() / 3,
				logWindow.getHeight() * .1f);
		logWindow.add(textExpWindow).align(Align.bottomRight).grow().size((2 * logWindow.getWidth()) / 3,
				logWindow.getHeight() * .6f);

		// selects first item on list as item to display
		// must be here or else first item image may be too large and push title
		// off screen
		Log defaultLog = list.getSelected();
		logImgAreaButton.clear();
		imgLogBS.up = defaultLog.getImg().getDrawable();
		logVersionLabel.setText(defaultLog.getTitle());
		textExplanationArea.setText("\t" + defaultLog.getExplanationText());
		// resets scrollbar up to the top
		textExpAreaScrollPane.setScrollPercentY(0);
	}

	// sort alphabetically
	private Log[] sortList(ArrayList<Log> tempList) {
		Log[] sortedList = new Log[tempList.size()];

		for (Log g : tempList) {
			int ind = 0;
			for (Log l : tempList)
				ind = (compare(g.toString(), l.toString()) > 0) ? ind + 1 : ind;
			sortedList[ind] = g;
		}

		return sortedList;
	}

	private int compare(String str1, String str2) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		return (res != 0) ? res : str1.compareTo(str2);
	}

	public Window getLogWindow() {
		return logWindow;
	}

}
