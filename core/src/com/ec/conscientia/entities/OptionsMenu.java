package com.ec.conscientia.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ec.conscientia.SoundManager;
import com.ec.conscientia.filerw.FileIOManager;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.screens.MainMenuScreen;

public class OptionsMenu {

	private Window optionsWindow;
	private FileIOManager fileRW;

	public OptionsMenu(Skin skin, final MainGameScreen mgScr, final PauseMenu pauseMenu) {
		this.fileRW = new FileIOManager(mgScr.getConscientia(), mgScr);

		optionsWindow = new Window("", skin);
		optionsWindow.setMovable(false);

		// sound checkbox
		final CheckBox soundCB;
		if (mgScr.getConscientia().isUseAltFont())
			soundCB = new CheckBox(" SFX On/Off", skin, "whiners_default");
		else
			soundCB = new CheckBox(" SFX On/Off", skin);
		soundCB.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.getSoundManager().toggleSFX(soundCB.isChecked());
				// plays confirmation sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
			}
		});
		soundCB.setChecked(mgScr.getSoundManager().isSFXon());

		final Slider sfxSlider = new Slider(1, 100, 1, false, skin);
		sfxSlider.setAnimateDuration(.1f);
		sfxSlider.setValue(mgScr.getSoundManager().getSFXVolume() * 100);
		sfxSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (mgScr.getSoundManager().isSFXon())
					mgScr.getSoundManager().setSFXVolume(sfxSlider.getPercent());
			}
		});

		// bgm checkbox
		final CheckBox bgmCB;
		if (mgScr.getConscientia().isUseAltFont())
			bgmCB = new CheckBox(" BGM On/Off", skin, "whiners_default");
		else
			bgmCB = new CheckBox(" BGM On/Off", skin);
		bgmCB.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.getSoundManager().toggleBGM(bgmCB.isChecked());
				if (!mgScr.getSoundManager().isBGMon())
					// plays confirmation noise
					mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
			}
		});
		bgmCB.setChecked(mgScr.getSoundManager().isBGMon());

		final Slider bgmSlider = new Slider(1, 100, 1, false, skin);
		bgmSlider.setAnimateDuration(.1f);
		bgmSlider.setValue(mgScr.getSoundManager().getBGMVolume() * 100);
		bgmSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (mgScr.getSoundManager().isBGMon())
					mgScr.getSoundManager().setBGMVolume(bgmSlider.getPercent());
			}
		});

		// whiner's font
		final CheckBox altFontCB;
		if (mgScr.getConscientia().isUseAltFont())
			altFontCB = new CheckBox(" Change Font", skin, "whiners_default");
		else
			altFontCB = new CheckBox(" Change Font", skin);
		altFontCB.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				mgScr.getConscientia().setUseAltFont(altFontCB.isChecked());
				fileRW.writer.setUseAltFont(altFontCB.isChecked());
				mgScr.genDispWin.setDialogueLabels();
				mgScr.genDispWin.setResponseAreaLabels();
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				pauseMenu.setDisplayWindow(PauseMenu.RESET_OPTIONS_WINDOW);
			}
		});
		altFontCB.setChecked(mgScr.getConscientia().isUseAltFont());

		TextButton returnToMainMenu;
		if (mgScr.getConscientia().isUseAltFont())
			returnToMainMenu = new TextButton("End Session", skin, "whiners_default");
		else
			returnToMainMenu = new TextButton("End Session", skin);
		returnToMainMenu.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				// will display a quote of Biracul and update all saved games
				MainMenuScreen.savesUpdated = false;
				// stop current bgm
				mgScr.getSoundManager().stopBGM();
				mgScr.getSoundManager().setBGMVolume(.3f);
				// plays click sound
				mgScr.getSoundManager().playSFX(SoundManager.SFX_CLICK_POSITIVE);
				fileRW.writer.gameSave();
				mgScr.getConscientia()
						.setScreen(new MainMenuScreen(true, mgScr.getSoundManager(), mgScr.getConscientia()));
			}
		});

		// set size and position
		optionsWindow.setSize(mgScr.getStagePause().getWidth() * .4f, mgScr.getStagePause().getHeight() * .7f);
		optionsWindow.setPosition((mgScr.getStagePause().getWidth() / 2) - (optionsWindow.getWidth() / 2),
				(mgScr.getStagePause().getHeight() / 2) - (optionsWindow.getHeight() / 2));

		// create layout
		optionsWindow.add(returnToMainMenu).align(Align.center).pad(15)
				.size(optionsWindow.getWidth() * .75f, optionsWindow.getHeight() / 7).row();
		optionsWindow.add(soundCB).align(Align.center).pad(5)
				.size(optionsWindow.getWidth() * .75f, optionsWindow.getHeight() / 10).row();
		optionsWindow.add(sfxSlider).align(Align.center)
				.size(optionsWindow.getWidth() * .75f, optionsWindow.getHeight() / 10).row();
		optionsWindow.add(bgmCB).align(Align.center).pad(5)
				.size(optionsWindow.getWidth() * .75f, optionsWindow.getHeight() / 10).row();
		optionsWindow.add(bgmSlider).align(Align.center)
				.size(optionsWindow.getWidth() * .75f, optionsWindow.getHeight() / 10).row();
		optionsWindow.add(altFontCB).align(Align.center).size(optionsWindow.getWidth() * .75f,
				optionsWindow.getHeight() / 10);
	}

	public Window getOptionsWindow() {
		return optionsWindow;
	}
}