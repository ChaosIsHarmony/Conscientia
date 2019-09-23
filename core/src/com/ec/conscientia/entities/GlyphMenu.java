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

public class GlyphMenu {

	private Window glyphWindow;
	private FileIOManager fileRW;

	public GlyphMenu(Skin skin, final MainGameScreen mgScr, final PauseMenu pauseMenu) {
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);

		glyphWindow = new Window("", skin, "no_bg");
		glyphWindow.setMovable(false);
		glyphWindow.setSize(mgScr.getStagePause().getWidth() * .9f, mgScr.getStagePause().getHeight() * .9f);
		glyphWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (glyphWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (glyphWindow.getHeight() / 2));

		// area for Glyph img and version info
		final Window visualAreaWindow = new Window("", skin);
		visualAreaWindow.setSize((2 * glyphWindow.getWidth()) / 3, glyphWindow.getHeight() / 4);
		visualAreaWindow.setMovable(false);
		final Window spacerWindow = new Window("", skin, "no_bg");
		spacerWindow.setMovable(false);

		// glyph image
		final ImageButtonStyle imgGlyphBS = new ImageButtonStyle();
		final ImageButton glyphImgAreaButton = new ImageButton(imgGlyphBS);
		final Label glyphVersionLabel;
		if (mgScr.getConscientia().isUseAltFont()) {
			glyphVersionLabel = new Label("", skin);
			glyphVersionLabel.setStyle(mgScr.loadingUtils.getLabelStyle(24));
		}
		else
			glyphVersionLabel = new Label("", skin);

		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(glyphImgAreaButton)
				.size((6 * visualAreaWindow.getWidth()) / 21, visualAreaWindow.getHeight()).align(Align.left).pad(15);
		visualAreaWindow.add(spacerWindow).size(visualAreaWindow.getWidth() / 21, visualAreaWindow.getHeight());
		visualAreaWindow.add(glyphVersionLabel)
				.size((13 * visualAreaWindow.getWidth()) / 21, visualAreaWindow.getHeight()).align(Align.topLeft);

		// text explanation area
		final Label textExplanationArea;
		if (mgScr.getConscientia().isUseAltFont()) {
			textExplanationArea = new Label("", skin);
			textExplanationArea.setStyle(mgScr.loadingUtils.getLabelStyle(24));
		}
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
		TextField glyphs = new TextField("GLYPHS", skin, "title");
		glyphs.setDisabled(true);

		final List<Glyph> list;
		if (mgScr.getConscientia().isUseAltFont()) {
			list = new List<Glyph>(skin);
			list.setStyle(mgScr.loadingUtils.getListStyle("no_bg"));
		}
		else
			list = new List<Glyph>(skin, "no_bg");
		ArrayList<Glyph> tempList = new ArrayList<Glyph>();
		for (Integer i : mgScr.getPlayer().getItemsAcquired())
			if (i <= Glyph.MAX_GLYPH) {
				boolean add = true;
				for (Glyph g : tempList)
					if (g.getID() == i)
						add = false;
				if (add)
					tempList.add(new Glyph(i, mgScr));
			}

		Glyph[] glyphList = fileRW.reader.loadGlyphs(tempList);
		glyphList = sortList(tempList);
		list.setItems(glyphList);

		ScrollPane listPane = new ScrollPane(list, skin);
		listPane.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// clear any image that was already there
				Glyph selectedGlyph = list.getSelected();
				glyphImgAreaButton.clear();
				imgGlyphBS.up = selectedGlyph.getImg().getDrawable();
				glyphVersionLabel.setText(selectedGlyph.getTitle());
				textExplanationArea.setText("\t" + selectedGlyph.getExplanationText());
				// resets scrollbar up to the top
				textExpAreaScrollPane.setScrollPercentY(0);
			}
		});

		listAreaWindow.add(glyphs).expandX().fillX().row();
		listAreaWindow.add(listPane).grow();
		listAreaWindow.align(Align.left);

		// resume button
		final TextButton resume;
		if (mgScr.getConscientia().isUseAltFont()) {
			resume = new TextButton("Resume", skin);
			resume.setStyle(mgScr.loadingUtils.getTextButtonStyle("default"));
		}
		else
			resume = new TextButton("Resume", skin);
		resume.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				glyphWindow.clear();
				mgScr.setGameState(mgScr.IN_PLAY);
				Gdx.input.setInputProcessor(mgScr.getStage());
			}
		});

		final TextButton back;
		if (mgScr.getConscientia().isUseAltFont()) {
			back = new TextButton("Back", skin);
			back.setStyle(mgScr.loadingUtils.getTextButtonStyle("default"));
		}
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
		buttonArea.add(resume).size((glyphWindow.getWidth() / 3) / 2, glyphWindow.getHeight() * .1f);
		buttonArea.add(back).size((glyphWindow.getWidth() / 3) / 2, glyphWindow.getHeight() * .1f);

		glyphWindow.add(listAreaWindow).align(Align.topLeft).grow().size(glyphWindow.getWidth() / 3,
				glyphWindow.getHeight() * .9f);
		glyphWindow.add(visualAreaWindow).align(Align.topRight).grow()
				.size((2 * glyphWindow.getWidth()) / 3, glyphWindow.getHeight() * .4f).row();
		glyphWindow.add(buttonArea).align(Align.bottomLeft).grow().size(glyphWindow.getWidth() / 3,
				glyphWindow.getHeight() * .1f);
		glyphWindow.add(textExpWindow).align(Align.bottomRight).grow().size((2 * glyphWindow.getWidth()) / 3,
				glyphWindow.getHeight() * .6f);

		// selects first item on list as item to display
		Glyph defaultGlyph = list.getSelected();
		glyphImgAreaButton.clear();
		imgGlyphBS.up = defaultGlyph.getImg().getDrawable();
		glyphVersionLabel.setText(defaultGlyph.getTitle());
		textExplanationArea.setText("\t" + defaultGlyph.getExplanationText());
		textExpAreaScrollPane.setScrollPercentY(0);
	}

	// sort alphabetically
	private Glyph[] sortList(ArrayList<Glyph> tempList) {
		Glyph[] sortedList = new Glyph[tempList.size()];

		for (Glyph g : tempList) {
			int ind = 0;
			for (Glyph l : tempList)
				ind = (compare(g.toString(), l.toString()) > 0) ? ind + 1 : ind;
			sortedList[ind] = g;
		}

		return sortedList;
	}

	private int compare(String str1, String str2) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		return (res != 0) ? res : str1.compareTo(str2);
	}

	public Window getGlyphWindow() {
		return glyphWindow;
	}
}