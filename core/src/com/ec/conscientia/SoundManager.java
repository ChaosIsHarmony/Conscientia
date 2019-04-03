package com.ec.conscientia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.ec.conscientia.ingameoperations.MusicManager;

public class SoundManager {

	// SFX list
	public static final int SFX_SPAWNING_SOUND = 0, SFX_CLICK_NEGATIVE = 1, SFX_CLICK_POSITIVE = 2,
			SFX_ITEM_ACQUIRED = 3;
	// BGM list
	public static final int BGM_MAIN_MENU = 0, BGM_KABU_SANCTUARY = 1, BGM_KABU_TAMBUL = 2, BGM_KABU_DAZIR = 3,
			BGM_KABU_WILDERNESS = 4, BGM_KABU_CANYON = 5, BGM_FAMLICUS = 6, BGM_NEXUS = 7, BGM_KABU_DAWN_FORTRESS = 8,
			BGM_KABU_WASTELAND = 9, BGM_KABU_WELLSPRING = 10, BGM_KABU_URRUK = 11,
			// Jer

			// Enclave
			BGM_ENCLAVE_THE_VAULT = 100, BGM_ENCLAVE_ARCHIVES_GENERAL = 101, BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM = 102,
			BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER = 103, BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON = 104,
			BGM_ENCLAVE_THE_NAVE_RECKONING = 105, BGM_ENCLAVE_THE_NAVE_APSE = 106, BGM_ENCLAVE_PATH_OF_DISCIPLINE = 107,
			BGM_ENCLAVE_THRESHOLD_GENERAL = 108, BGM_ENCLAVE_EXODUS = 109, BGM_ENCLAVE_UNDERHALLS = 110,
			BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL = 111, BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS = 112, BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM = 113,
			// Kavu
			BGM_KAVU_DAZIL = 150,

			// Thiuda
			BGM_THIUDA_SLAVE_PEN = 200,

			// Urugh
			BGM_URUGH_EMPYREAN = 250,
			// Special
			BGM_LOONEY = 350, BGM_CLOSE_TO_THE_ONE = 351, BGM_KABU_TRIAL = 352, BGM_KABU_AKKEBERS_PLAY = 353,
			BGM_ARK_RIKHARR = 354,
			// Misc
			BGM_GENERIC_BATTLE_MUSIC = 900, BGM_PLOT_THICKENS = 901, BGM_LOON_BATTLE_MUSIC = 902, BGM_CREDITS = 999;
	private static Sound[] sfx = new Sound[4];

	private static Music currentBGM;
	private MusicManager musicMan;
	private static int currentBGMID;
	private static boolean SFXon, BGMon;
	private static float sfxVolume, bgmVol, maxBgmVol, fadeOutConstant;

	public SoundManager() {
		fadeIn = true;
		sfxVolume = .2f;
		bgmVol = maxBgmVol = .2f;
		targetVol = bgmVol;
		SFXon = false;
		BGMon = false;
//		 SFXon = true;
//		 BGMon = true;
		currentBGMID = -1;
		loadSFX();
	}

	public void setMusicManager(MusicManager man) {
		musicMan = man;
	}

	// loads all short sfx to memory (necessary for mobile)
	public void loadSFX() {
		sfx[SFX_SPAWNING_SOUND] = Gdx.audio.newSound(Gdx.files.internal("SFX/click.ogg"));
		sfx[SFX_CLICK_NEGATIVE] = Gdx.audio.newSound(Gdx.files.internal("SFX/click_2.ogg"));
		sfx[SFX_CLICK_POSITIVE] = Gdx.audio.newSound(Gdx.files.internal("SFX/click_3.ogg"));
		sfx[SFX_ITEM_ACQUIRED] = Gdx.audio.newSound(Gdx.files.internal("SFX/Upper01.ogg"));
	}

	// load as currentBGM
	public void loadBGM(int BGM) {
		try {
			if (BGMon) {
				// makes sure that the same song isn't played twice
				if (currentBGMID != BGM)
					// different BGM by final int ID
					switch (BGM) {
					case BGM_MAIN_MENU:
					if (currentBGMID != BGM_MAIN_MENU)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/Awareness_Real.ogg")), BGM_MAIN_MENU, maxBgmVol);
					break;
					case BGM_CREDITS:
					if (currentBGMID != BGM_CREDITS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/Beast of Vedt.ogg")), BGM_CREDITS, maxBgmVol);
					break;

					// SPECIAL THEMES
					case BGM_FAMLICUS:
					if (currentBGMID != BGM_FAMLICUS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - The Red Father.ogg")), BGM_FAMLICUS, maxBgmVol);
					break;
					case BGM_CLOSE_TO_THE_ONE:
					if (currentBGMID != BGM_CLOSE_TO_THE_ONE)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - Awareness.ogg")), BGM_CLOSE_TO_THE_ONE, maxBgmVol);
					break;
					case BGM_ARK_RIKHARR:
					if (currentBGMID != BGM_ARK_RIKHARR)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - Fate's Sacrifices.ogg")), BGM_ARK_RIKHARR, maxBgmVol);
					break;

					// SPECIAL EVENTS
					case BGM_KABU_AKKEBERS_PLAY:
					if (currentBGMID != BGM_KABU_AKKEBERS_PLAY)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Akkeber's Play.ogg")), BGM_KABU_AKKEBERS_PLAY, maxBgmVol);
					break;
					case BGM_GENERIC_BATTLE_MUSIC:
					if (currentBGMID != BGM_GENERIC_BATTLE_MUSIC)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - Invasion.ogg")), BGM_GENERIC_BATTLE_MUSIC, maxBgmVol);
					break;
					case BGM_LOON_BATTLE_MUSIC:
					if (currentBGMID != BGM_GENERIC_BATTLE_MUSIC)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - Crushed By the Weight of Feathers.ogg")), BGM_GENERIC_BATTLE_MUSIC, maxBgmVol);
					break;
					case BGM_LOONEY:
					if (currentBGMID != BGM_LOONEY)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - The End.ogg")), BGM_LOONEY, maxBgmVol);
					break;
					case BGM_PLOT_THICKENS:
					if (currentBGMID != BGM_PLOT_THICKENS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - The Plot Thickens.ogg")), BGM_PLOT_THICKENS, maxBgmVol);
					break;
					case BGM_KABU_TRIAL:
					if (currentBGMID != BGM_KABU_TRIAL)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/2019 - The Red Father.ogg")), BGM_KABU_TRIAL, maxBgmVol);
					break;

					// ENCLAVE
					case BGM_ENCLAVE_ARCHIVES_GENERAL:
					if (currentBGMID != BGM_ENCLAVE_ARCHIVES_GENERAL)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/From Tome to Tome.ogg")), BGM_ENCLAVE_ARCHIVES_GENERAL, maxBgmVol);
					break;
					case BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM:
					if (currentBGMID != BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/The Scriptorium.ogg")), BGM_ENCLAVE_ARCHIVES_SCRIPTORIUM, maxBgmVol);
					break;
					case BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM:
					if (currentBGMID != BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - Broken Scriptorium.ogg")), BGM_ENCLAVE_ARCHIVES_BROKEN_SCRIPTORIUM, maxBgmVol);
					break;
					case BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON:
					if (currentBGMID != BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - True Archon.ogg")), BGM_ENCLAVE_ARCHIVES_SEAT_OF_THE_TRUE_ARCHON, maxBgmVol);
					break;
					case BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER:
					if (currentBGMID != BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - The Sylvan Encounter.ogg")), BGM_ENCLAVE_ARCHIVES_SYLVAN_ENCOUNTER, maxBgmVol);
					break;
					case BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL:
					if (currentBGMID != BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - Memories of the Adepti.ogg")), BGM_ENCLAVE_HALL_OF_THE_ADEPTI_GENERAL, maxBgmVol);
					break;
					case BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS:
					if (currentBGMID != BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - Solemne.ogg")), BGM_ENCLAVE_HALL_OF_THE_ADEPTI_ORMENOS, maxBgmVol);
					break;
					case BGM_ENCLAVE_THE_NAVE_RECKONING:
					if (currentBGMID != BGM_ENCLAVE_THE_NAVE_RECKONING)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - The Nave.ogg")), BGM_ENCLAVE_THE_NAVE_RECKONING, maxBgmVol);
					break;
					case BGM_ENCLAVE_PATH_OF_DISCIPLINE:
					if (currentBGMID != BGM_ENCLAVE_PATH_OF_DISCIPLINE)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - Specters of the Enclave.ogg")), BGM_ENCLAVE_PATH_OF_DISCIPLINE, maxBgmVol);
					break;
					case BGM_ENCLAVE_THRESHOLD_GENERAL:
					if (currentBGMID != BGM_ENCLAVE_THRESHOLD_GENERAL)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/Solitude.ogg")), BGM_ENCLAVE_THRESHOLD_GENERAL, maxBgmVol);
					break;
					case BGM_ENCLAVE_EXODUS:
					if (currentBGMID != BGM_ENCLAVE_EXODUS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/Passing the Torch.ogg")), BGM_ENCLAVE_EXODUS, maxBgmVol);
					break;
					case BGM_ENCLAVE_UNDERHALLS:
					if (currentBGMID != BGM_ENCLAVE_UNDERHALLS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/2019 - The Halls of the Enclave.ogg")), BGM_ENCLAVE_UNDERHALLS, maxBgmVol);
					break;
					case BGM_ENCLAVE_THE_VAULT:
					if (currentBGMID != BGM_ENCLAVE_THE_VAULT)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Enclave/The Vault.ogg")), BGM_ENCLAVE_THE_VAULT, maxBgmVol);
					break;

					// JER

					// KABU
					case BGM_KABU_CANYON:
					if (currentBGMID != BGM_KABU_CANYON)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Decisions.ogg")), BGM_KABU_CANYON, maxBgmVol);
					break;
					case BGM_KABU_DAWN_FORTRESS:
					if (currentBGMID != BGM_KABU_DAWN_FORTRESS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - In Ark's Name.ogg")), BGM_KABU_DAWN_FORTRESS, maxBgmVol);
					break;
					case BGM_KABU_DAZIR:
					if (currentBGMID != BGM_KABU_DAZIR)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Living in These Times.ogg")), BGM_KABU_DAZIR, maxBgmVol);
					break;
					case BGM_KABU_SANCTUARY:
					if (currentBGMID != BGM_KABU_SANCTUARY)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Forgotten Past.ogg")), BGM_KABU_SANCTUARY, maxBgmVol);
					break;
					case BGM_KABU_TAMBUL:
					if (currentBGMID != BGM_KABU_TAMBUL)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Aestate.ogg")), BGM_KABU_TAMBUL, maxBgmVol);
					break;
					case BGM_KABU_URRUK:
					if (currentBGMID != BGM_KABU_URRUK)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Lost City.ogg")), BGM_KABU_URRUK, maxBgmVol);
					break;
					case BGM_KABU_WASTELAND:
					if (currentBGMID != BGM_KABU_WASTELAND)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Ruinous Laments.ogg")), BGM_KABU_WASTELAND, maxBgmVol);
					break;
					case BGM_KABU_WELLSPRING:
					if (currentBGMID != BGM_KABU_WELLSPRING)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Within These Crumbling Halls.ogg")), BGM_KABU_WELLSPRING, maxBgmVol);
					break;
					case BGM_KABU_WILDERNESS:
					if (currentBGMID != BGM_KABU_WILDERNESS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Kabu/2019 - Road to Dazir.ogg")), BGM_KABU_WILDERNESS, maxBgmVol);
					break;

					// MIND
					case BGM_NEXUS:
					if (currentBGMID != BGM_NEXUS)
					loadSong(Gdx.audio.newMusic(Gdx.files.internal("BGM/Misc/Yarron.ogg")), BGM_NEXUS, maxBgmVol);
					break;

					// THIUDA
					case BGM_THIUDA_SLAVE_PEN:
					break;

					// URUGH

					}
			}
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	private void loadSong(Music song, int id, float vol) {
		currentBGM = song;
		currentBGMID = id;
		targetVol = vol;
	}

	// plays currently loaded SFX
	public void playSFX(int sfxID) {
		try {
			if (SFXon)
				sfx[sfxID].play(sfxVolume);
		} catch (Exception e) {
			// TODO think about how to handle this because it will sometimes
			// throw an illegalStateException
		}
	}

	public void stopSFX(int sfxID) {
		try {
			if (SFXon)
				sfx[sfxID].stop();
		} catch (Exception e) {

		}
	}

	public void playBGM() {
		try {
			if (currentBGM != null)
				if (BGMon) {
					currentBGM.setVolume(bgmVol);
					currentBGM.setLooping(true);
					currentBGM.play();
				}
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public void playNonLoopingBGM() {
		try {
			if (currentBGM != null)
				if (BGMon) {
					currentBGM.setVolume(bgmVol);
					currentBGM.setLooping(false);
					currentBGM.play();
				}
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public void stopBGM() {
		try {
			currentBGM.stop();
			currentBGM.dispose();
			currentBGMID = -1;
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	private static float targetVol;
	private static boolean fadeOut = false, fadeIn = false;

	public void setFadeIn(boolean tORf) {
		fadeIn = tORf;
	}

	public boolean getFadeIn() {
		return fadeIn;
	}

	public boolean getFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(boolean tORf) {
		fadeOut = tORf;
		if (fadeOut)
			fadeOutConstant = ((currentBGMID == BGM_MAIN_MENU) ? 50f : 200f);
	}

	public void fadeOutBGM() {
		try {
			if (bgmVol < 0)
				bgmVol = .002f;
			if (BGMon && currentBGM != null) {
				if (bgmVol > .005f) {
					bgmVol -= targetVol / fadeOutConstant;
					currentBGM.setVolume(bgmVol);
				} else {
					stopBGM();
					fadeOut = false;
					if (musicMan.changeLocations)
						musicMan.switchBGM();
				}
			} else {
				fadeOut = false;
				currentBGMID = -1;
			}
		} catch (Exception e) {
			fadeOut = false;
			currentBGMID = -1;
			currentBGM = null;
		}

	}

	// for endGame()
	public void longFadeOutBGM() {
		try {
			if (bgmVol < 0)
				bgmVol = .002f;
			if (BGMon && currentBGM != null) {
				if (bgmVol > .005f) {
					bgmVol -= targetVol / (fadeOutConstant * 1.5f);
					currentBGM.setVolume(bgmVol);
				} else {
					stopBGM();
					fadeOut = false;
				}
			} else {
				fadeOut = false;
				currentBGMID = -1;
			}
		} catch (Exception e) {
			fadeOut = false;
			currentBGM = null;
			currentBGMID = -1;
		}

	}

	public void fadeInBGM() {
		try {
			if (bgmVol < 0)
				bgmVol = .002f;
			if (BGMon && currentBGM != null) {
				if (bgmVol < targetVol) {
					bgmVol += targetVol / 250f;
					currentBGM.setVolume(bgmVol);
				} else
					fadeIn = false;
			} else {
				fadeIn = false;
				currentBGMID = -1;
			}
		} catch (Exception e) {
			fadeIn = false;
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public void toggleSFX(boolean status) {
		SFXon = status;
		try {
			// if turning off
			if (!status)
				for (Sound s : sfx)
					s.dispose();
			else
				loadSFX();
		} catch (Exception e) {
			SFXon = false;
		}
	}

	public void toggleBGM(boolean status) {
		BGMon = status;
		try {
			// if turning off
			if (!status && currentBGM != null) {
				currentBGM.stop();
				currentBGMID = -1;
				bgmVol = 0.01f;
				fadeIn = false;
				fadeOut = false;
				currentBGM.dispose();
				currentBGM = null;
			}
			// if turning on and there is a currentBGM loaded
			else if (status && currentBGM != null) {
				fadeIn = true;
				bgmVol = 0.01f;
				targetVol = maxBgmVol;
				currentBGM.play();
			}
			// turn on, check for what should be playing and then play
			else {
				fadeIn = true;
				bgmVol = 0.01f;
				targetVol = maxBgmVol;
				musicMan.checkChangeMusic();
			}
		} catch (Exception e) {
			BGMon = false;
			fadeIn = false;
			fadeOut = false;
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public boolean isSFXon() {
		return SFXon;
	}

	public boolean isBGMon() {
		return BGMon;
	}

	public void setSFXVolume(float newVol) {
		sfxVolume = newVol;
	}

	public float getSFXVolume() {
		return sfxVolume;
	}

	public void setBGMVolume(float newVol) {
		maxBgmVol = newVol;
		bgmVol = newVol;
		try {
			currentBGM.setVolume(newVol);
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public void setBGMTransitionVolume(float newVol) {
		bgmVol = newVol;
		try {
			currentBGM.setVolume(newVol);
		} catch (Exception e) {
			currentBGM = null;
			currentBGMID = -1;
		}
	}

	public float getBGMVolume() {
		return bgmVol;
	}

	/*
	 * returns int ID num for song unless null returns -1 if null
	 */
	public int getCurrentBGMID() {
		return currentBGMID;
	}
}
