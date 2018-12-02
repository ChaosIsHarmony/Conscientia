package com.ec.conscientia;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ec.conscientia.dialogue.DialogueFileReaderWriter;
import com.ec.conscientia.entities.NPC;
import com.ec.conscientia.screens.MainGameScreen;
import com.ec.conscientia.variables.CommonVar;

public class Tests {
	private FileReaderWriter fileRW;
	private Conscientia consc;

	public Tests(Conscientia consc) {
		fileRW = new FileReaderWriter(consc);
		this.consc = consc;
	}

	public void runTests() {
		checkDialogueFile();
		checkNPCFile();
		countWords();
		loadAllNPCS();
	}

	// ERROR CHECKING SUITE
	private String[] exceptionList;

	public void checkDialogueFile() {
		ArrayList<String> filesToCheck = new ArrayList<String>();
		exceptionList = generateAddressExceptionList();
		String[] fileNamesBoE = new String[] { "EIDOS_CANYON.mao", "EIDOS_DAWN_FORTRESS.mao", "EIDOS_DAZIR.mao",
				"EIDOS_SANCTUARY.mao", "EIDOS_TAMBUL.mao", "EIDOS_UR'RUK.mao", "EIDOS_WASTELAND.mao",
				"EIDOS_WELLSPRING.mao", "EIDOS_WILDERNESS.mao", "MIND_MINDSCAPE.mao", "MIND_NETHER_EDGE.mao",
				"MIND_PALACE_OF_MEMORY.mao", "MIND_THE_BOOKS.mao" };
		String[] fileNamesBoT = new String[] { "TORMA_ARCHIVES.mao", "TORMA_HALLS_OF_THE_ADEPTI.mao",
				"TORMA_THE_THRESHOLD.mao", "TORMA_THE_NAVE.mao", "TORMA_THE_PATH_OF_DISCIPLINE.mao",
				"TORMA_THE_VAULT.mao", "TORMA_UNDERHALLS.mao", "MIND_MINDSCAPE.mao", "MIND_PALACE_OF_MEMORY.mao",
				"MIND_THE_BOOKS.mao" };
		String[] fileNamesBoTh = new String[] { "THETIAN_A_CITY_UNDER_THE_STAIRS.mao", "THETIAN_A_FAMILY_SECRET.mao",
				"THETIAN_A_JOURNEY_ACROSS_THE_SEA.mao", "THETIAN_AMONG_FRIENDS.mao", "THETIAN_A_RUDE_AWAKENING.mao",
				"THETIAN_A_TIDE_OF_BLOOD.mao", "THETIAN_AN_EVENING_IN_THE_LIBRARY.mao", "THETIAN_DRAGONFLY.mao",
				"THETIAN_EMBRACE_OF_THE_WYRM.mao", "THETIAN_FIRE_OF_THE_MIND.mao", "THETIAN_HOMECOMING.mao",
				"THETIAN_LOOSE_ENDS.mao", "THETIAN_PROLOGUE.mao", "THETIAN_THE_HART_AND_THE_HAWTHORN.mao",
				"THETIAN_THE_HOUSE_OF_BRYNMOR.mao", "THETIAN_THE_LOST_CHILD.mao", "THETIAN_THE_INVITATION.mao",
				"THETIAN_THE_SHAPE.mao", "THETIAN_THE_TRUE_MASTER.mao", "THETIAN_THROUGH_THE_SILVER_GATES.mao",
				"THETIAN_THE_HOUSE_IN_THE_MIST.mao" };
		String[] fileNamesBoR = new String[] {};
		String[] fileNamesBoW = new String[] {};
		String[] fileNamesBoB = new String[] {};
		String[] checkerFileNames = new String[] { "Enclave.mao", "Jer.mao", "Kabu.mao", "Kavu.mao", "Mind.mao",
				"Thiuda.mao", "Urugh.mao" };
		FileHandle file;

		// populates list of files to check
		for (String str : fileNamesBoE)
			filesToCheck.add(str);
		for (String str : fileNamesBoT)
			filesToCheck.add(str);
		// for (String str : fileNamesBoTh)
		// filesToCheck.add(str);
		// for (String str : fileNamesBoR)
		// filesToCheck.add(str);
		// for (String str : fileNamesBoW)
		// filesToCheck.add(str);
		// for (String str : fileNamesBoB)
		// filesToCheck.add(str);

		// check to make sure npcs exist
		file = Gdx.files.internal("Game Files/NPCListByLocation.mao");
		String byLoc = file.readString();
		file = Gdx.files.internal("Game Files/NPCsbyNum.mao");
		String byNum = file.readString();
		file = Gdx.files.internal("Game Files/NPCs.mao");
		String npcs = file.readString();
		int start = 0;
		while (byLoc.substring(start).contains("|")) {
			start = byLoc.indexOf("|", start) + 1;
			String areaNPCs = byLoc.substring(start, byLoc.indexOf("|", start + 1));
			while (areaNPCs.length() > 1) {
				if (!byNum.contains(areaNPCs.substring(0, areaNPCs.indexOf(","))))
					System.out.println("AREA NPC ERR:" + areaNPCs.substring(0, areaNPCs.indexOf(",")));
				areaNPCs = areaNPCs.substring(areaNPCs.indexOf(",") + 1);
			}
			start = byLoc.indexOf("|", start) + 1;
		}
		while (byNum.contains("|")) {
			String npc = byNum.substring(byNum.indexOf("|") + 1, byNum.indexOf(":"));
			if (!npcs.contains("[/" + npc + "]") || !npcs.contains("[" + npc + "/]"))
				System.out.println("NPC NUM ERR:" + npc);
			byNum = byNum.substring(byNum.indexOf(",") + 1);
		}
		// Special Event Cues
		HashMap<String, ArrayList<String>> cues;
		// loads special event cues
		cues = fileRW.getEventCues();
		cuesVsMulti(cues);

		for (String fn : filesToCheck) {
			file = Gdx.files.internal("Game Files/Dialogue/" + fn);
			String fileStr = file.readString();

			// counts * to make sure the dialogue/description is appropriately
			// enveloped in these
			countStars(fileStr);
			System.out.println("@Count Stars for " + fn + " complete");
			// makes sure there is only one instance of any given address (init
			// and final)
			checkUniqueAdd(fileStr);
			System.out.println("@Check Unique Add for " + fn + " complete");
			// looks for inaccessible addresses, i.e., ones that are not
			// displayed in any choice
			checkDeadAdd(fileStr);
			System.out.println("@Check Dead Add for " + fn + " complete");
			// looks to make sure there are an appropriate number of ! in each
			// address
			checkExclamationMarks(fileStr);
			System.out.println("@Check Exclamation Marks for " + fn + " complete");
			// simulated player, whereby you click every possible action
			// can use validAddress method from dialogue class
			doublecheckIsValid(fileStr, true);
			System.out.println("@Double Check isValid for " + fn + " complete");
			// check to make sure parenthesis is there at the beginning of each
			// choice
			checkParenthesis(fileStr);
			System.out.println("@Check Parenthesis for " + fn + " complete");
		}
		// checks to make sure all default addresses are up-to-date inNPC file
		file = Gdx.files.internal("Game Files/NPCs.mao");
		String fileStr = file.readString();
		doublecheckIsValid(fileStr, false);
		System.out.println("@Double Check isValid for NPCFile complete");
		// checks to see if all Multichecker addresses are in cues file
		// TODO
		// checks multichecker files for mistakes
		for (String fn : checkerFileNames) {
			file = Gdx.files.internal("Game Files/Multichecker/" + fn);
			fileStr = file.readString();

			if (fileStr.contains("{")) {
				// makes sure all | : , are equal
				correctMarkup(fileStr);
				doublecheckIsValidMulti(fileStr);
				System.out.println("@Correct Markup for " + fn + " complete");

				while (fileStr.contains("}")) {
					// isolate first address
					String address = fileStr.substring(fileStr.indexOf("{") + 1, fileStr.indexOf("}"));
					String content = fileStr.substring(fileStr.indexOf(address), fileStr.lastIndexOf(address));

					while (content.contains(",")) {
						// isolate to first entry within address
						String entry = content.substring(content.indexOf("|") + 1, content.indexOf(","));

						try {
							String event = entry.substring(0, entry.indexOf(":"));
							// checks to make sure events are numbers
							if (event.length() > 1)
								if (event.contains("$")) {
									while (true) {
										if (event.length() < 2)
											break;
										if (event.contains("*"))
											Integer.parseInt(event.substring(2, event.indexOf("$", 1)));
										else
											Integer.parseInt(event.substring(1, event.indexOf("$", 1)));
										event = event.substring(event.indexOf("$", 1));
									}
								} else if (event.contains("^")) {
									while (true) {
										if (event.length() < 2)
											break;
										if (event.contains("*"))
											Integer.parseInt(event.substring(2, event.indexOf("^", 1)));
										else
											Integer.parseInt(event.substring(1, event.indexOf("^", 1)));
										event = event.substring(event.indexOf("^", 1));
									}
								} else if (event.contains("#")) {
									Integer.parseInt(event.substring(0, event.indexOf("#")));
									Integer.parseInt(event.substring(event.indexOf("#") + 1));
								} else if (event.contains("*")) {
									Integer.parseInt(event.substring(1));
								} else
									Integer.parseInt(event);

							// checks to make sure all addresses have 5
							// exclamation marks
							if (entry.substring(entry.indexOf(":") + 1).length() > 1
									&& fiveExclamationMarks(entry.substring(entry.indexOf(":") + 1)) < 5)
								System.out.println("EX - Address: " + address + "; Entry: " + entry);
							// checks to make sure it's a valid address
							doublecheckIsValid(entry.substring(entry.indexOf(":") + 1), false);

						} catch (Exception e) {
							System.out.println("ERR - Address: " + address + "; Entry: " + entry);
						}

						// trims to next entry
						content = content.substring(content.indexOf(",") + 1);
					}
					fileStr = fileStr.substring(fileStr.indexOf("}", fileStr.lastIndexOf(address)) + 1);
				}
			}
		}
	}

	private void doublecheckIsValidMulti(String fileStr) {
		DialogueFileReaderWriter dialogueFileRW = new DialogueFileReaderWriter();
		while (fileStr.contains(":")) {
			int end = 0;
			int start = fileStr.indexOf(":", end) + 1;
			end = fileStr.indexOf(",", start);
			String currentAddress = fileStr.substring(start, end);

			if (!dialogueFileRW.validAddress(currentAddress)) {
				System.out.println("CHECKER-ERR: " + currentAddress);
			}
			fileStr = fileStr.substring(end);
		}
	}

	private void checkParenthesis(String fileStr) {
		int ind = 0;
		String section = "";

		while (true) {
			try {
				section = fileStr.substring(fileStr.indexOf("[/", ind), fileStr.indexOf("/]", ind));
				int parCntOpen = 0, parCntClosed = 0, curlCntOpen = 0, curlCntClosed = 0;
				parCntOpen = cntPar("(", section);
				parCntClosed = cntPar(")", section);
				if (parCntOpen != parCntClosed)
					System.out.println("PAR: " + section);
				curlCntOpen = cntPar("{", section);
				curlCntClosed = cntPar("}", section);
				if (curlCntOpen != curlCntClosed)
					System.out.println("CURL: " + section);
			} catch (Exception e) {
				break;
			}
			ind = fileStr.indexOf("/]", ind) + 2;
		}

	}

	private int cntPar(String parType, String section) {
		int cnt = 0, parCntInd = 0;
		while (true) {
			if (section.indexOf(parType, parCntInd) > 0) {
				cnt++;
				parCntInd = section.indexOf(parType, parCntInd) + 1;
			} else
				break;
		}
		return cnt;
	}

	private void correctMarkup(String fileStr) {
		int bar = count("|", fileStr, false);
		int colon = count(":", fileStr, false);
		int comma = count(",", fileStr, false);
		if (bar != colon || colon != comma || comma != bar) {
			System.out.println("Missing Marker - bar = " + bar + " colon = " + colon + " comma = " + comma);
			findMissingElement(fileStr);
		}
	}

	private void findMissingElement(String fileStr) {
		String lastAdd = "";
		int ind = fileStr.indexOf("{");
		int otherInd = 0;
		while (fileStr.indexOf("|", ind + 1) > 1) {
			ind = fileStr.indexOf("|", ind + 1);
			otherInd = fileStr.indexOf(",", ind);
			lastAdd = fileStr.substring(ind, otherInd);
			if (count("|", lastAdd, true) != 1 || count(":", lastAdd, true) != 1 || count(",", lastAdd, true) != 0)
				System.out.println(lastAdd);

		}
	}

	private int count(String character, String fileStr, boolean missingElement) {
		int ind = (missingElement) ? 0 : fileStr.indexOf("{");
		int cnt = 0;
		while (true) {
			try {
				if (fileStr.substring(ind).contains(character)) {
					cnt++;
					ind = fileStr.indexOf(character, ind) + 1;
				} else
					break;
			} catch (Exception e) {
				break;
			}
		}
		return cnt;
	}

	private void doublecheckIsValid(String fileStr, boolean isDialogueFile) {
		DialogueFileReaderWriter dialogueFileRW = new DialogueFileReaderWriter();
		if (isDialogueFile)
			while (fileStr.contains("{")) {
				int start = fileStr.indexOf(",", fileStr.indexOf("{")) + 1;
				int end = fileStr.indexOf("}", start);
				String currentAddress = fileStr.substring(start, end);

				if (!dialogueFileRW.validAddress(currentAddress)) {
					boolean printOut = true;
					for (String s : exceptionList)
						if (s.equals("DIA-ERR: " + currentAddress) || currentAddress.contains("JER!"))
							printOut = false;
					if (printOut)
						System.out.println("DIA-ERR-1: " + currentAddress);
				}
				fileStr = fileStr.substring(end);
			}
		else if (fileStr.length() < 100) {
			if (!dialogueFileRW.validAddress(fileStr)
					&& !(fileStr.contains("!END!") || fileStr.contains("NO ADDRESS") || fileStr.contains("FIGHT"))) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("DIA-ERR: " + fileStr) || fileStr.contains("JER!"))
						printOut = false;
				if (printOut)
					System.out.println("DIA-ERR-2: " + fileStr);
			}
		} else {
			while (fileStr.contains("(")) {
				int start = fileStr.indexOf(":", fileStr.indexOf(",")) + 1;
				int end = fileStr.indexOf(",", start);
				String currentAddress = fileStr.substring(start, end);
				if (!dialogueFileRW.validAddress(currentAddress)) {
					boolean printOut = true;
					for (String s : exceptionList)
						if (s.equals("NPC-ERR: " + currentAddress) || currentAddress.contains("JER!"))
							printOut = false;
					if (printOut)
						System.out.println("NPC-ERR: " + currentAddress);
				}
				fileStr = fileStr.substring(end);
			}
		}

	}

	private void cuesVsMulti(HashMap<String, ArrayList<String>> cues) {
		for (int fileID = -1; fileID < 6; fileID++) {
			// Set filepath based on fileID
			String path = "";
			String multiCue = "";
			switch (fileID) {
			case -1:
				path = "Game Files/Multichecker/Mind.mao";
				multiCue = "MIND!";
				break;
			case CommonVar.BIR:
				path = "Game Files/Multichecker/Urugh.mao";
				multiCue = "URUGH!";
				break;
			case CommonVar.EID:
				path = "Game Files/Multichecker/Kabu.mao";
				multiCue = "KABU!";
				break;
			case CommonVar.RIK:
				path = "Game Files/Multichecker/Kavu.mao";
				multiCue = "KAVU!";
				break;
			case CommonVar.THE:
				path = "Game Files/Multichecker/Jer.mao";
				multiCue = "JER!";
				break;
			case CommonVar.TOR:
				path = "Game Files/Multichecker/Enclave.mao";
				multiCue = "ENCLAVE!";
				break;
			case CommonVar.WUL:
				path = "Game Files/Multichecker/Thiuda.mao";
				multiCue = "THIUDA!";
				break;
			}

			// open relevant file
			String checkerFile = Gdx.files.internal(path).readString();

			for (String cue : cues.get("MULTICHECKER")) {
				if (cue.contains(multiCue))
					// trim to specific address
					if (!checkerFile.contains(cue)) {
					boolean printOut = true;
					for (String s : exceptionList)
					if (s.equals("CUE: " + cue))
					printOut = false;
					if (printOut)
					System.out.println("CUE: " + cue);
					}
			}
		}
	}

	private void countStars(String fileStr) {
		int ind = 1;

		// count * and print out
		while (true) {
			ind = fileStr.indexOf("[/", ind);
			if (ind < 0)
				break;
			String str = fileStr.substring(ind, fileStr.indexOf("[", ind + 1));
			String currAdd = str.substring(2, str.indexOf("]"));
			try {
				String trigEvent = str.substring(str.indexOf("|"), str.lastIndexOf("|"));
				if (trigEvent.length() > 2 && !trigEvent.contains(":")) {
					boolean printOut = true;
					for (String s : exceptionList)
						if (s.equals("BARS: " + trigEvent))
							printOut = false;
					if (printOut)
						System.out.println("BARS: " + trigEvent);
				}
			} catch (Exception e) {
				System.out.println(str);
			}
			str = str.substring(str.indexOf("]"));
			if (str.contains(currAdd)) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("LOOP: " + currAdd))
						printOut = false;
				if (printOut)
					System.out.println("LOOP: " + currAdd);
			}
			int ind_two = str.indexOf("*");
			if (ind_two < 0) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("STAR: " + str))
						printOut = false;
				if (printOut)
					System.out.println("STAR: " + str);
			}
			int ind_three = str.indexOf("*", ind_two + 1);
			if (ind_three < 0) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("STAR: " + str))
						printOut = false;
				if (printOut)
					System.out.println("STAR: " + str);
			}
			ind++;
		}
	}

	private void checkUniqueAdd(String fileStr) {
		int ind = 1;

		while (true) {
			ind = fileStr.indexOf("[/", ind);
			if (ind < 0)
				break;
			String str = fileStr.substring(ind, fileStr.indexOf("]", ind));
			fileStr = fileStr.substring(0, ind) + fileStr.substring(ind + str.length());
			if (fileStr.contains(str)) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("DUPLICATE: " + str))
						printOut = false;
				if (printOut)
					System.out.println("DUPLICATE: " + str);
			}
			ind++;
		}

		ind = 1;

		while (true) {
			ind = fileStr.indexOf("[", fileStr.indexOf("[/", ind));
			if (ind < 0)
				break;
			try {
				String str = fileStr.substring(ind, fileStr.indexOf("]", ind));
				fileStr = fileStr.substring(0, ind) + fileStr.substring(ind + str.length());
				if (fileStr.contains(str)) {
					boolean printOut = true;
					for (String s : exceptionList)
						if (s.equals("DUPLICATE: " + str))
							printOut = false;
					if (printOut)
						System.out.println("DUPLICATE: " + str);
				}
			} catch (Exception e) {
				System.out.println(fileStr.substring(ind));
				break;
			}

		}
		ind++;
	}

	private void checkDeadAdd(String fileStr) {
		int ind = 0;
		String[] checkerFileNames = new String[] { "Enclave.mao", "Jer.mao", "Kabu.mao", "Kavu.mao", "Mind.mao",
				"Thiuda.mao", "Urugh.mao" };
		// load a list of all destination addresses in multichecker
		// TODO expand to all multicheckers
		FileHandle file;
		String multiAddsStr = "";
		for (String checker : checkerFileNames) {
			file = Gdx.files.internal("Game Files/Multichecker/" + checker);
			multiAddsStr += file.readString();
		}
		ArrayList<String> multiAdds = new ArrayList<String>();
		int start = 0;
		while (true) {
			start = multiAddsStr.indexOf(":", start);
			if (start < 0)
				break;
			multiAdds.add(multiAddsStr.substring(++start, multiAddsStr.indexOf(",", start)));
		}

		// looks to make sure NPCs init address in NPCs file is consistent with
		// the NPCs file
		file = Gdx.files.internal("Game Files/NPCs.mao");
		String NPCsstr = file.readString();

		while (true) {
			ind = fileStr.indexOf("[/", ind);
			if (ind < 0)
				break;
			String add = fileStr.substring(ind + 2, fileStr.indexOf("]", ind));
			int cnt = 0;
			int ind_two = 0;
			while (true) {
				ind_two = fileStr.indexOf(add, ++ind_two);
				if (cnt > 2 || ind_two < 0)
					break;
				else
					cnt++;
			}
			if (cnt < 3 && !multiAdds.contains(add) && !NPCsstr.contains(add)) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("DEAD: " + add))
						printOut = false;
				if (printOut)
					System.out.println("DEAD: " + add);
			}
			ind++;
		}
	}

	private void checkExclamationMarks(String fileStr) {
		int ind = 0;

		while (true) {
			ind = fileStr.indexOf("[/", ind);
			if (ind < 0)
				break;
			String add = fileStr.substring(ind + 2, fileStr.indexOf("]", ind));
			// System.out.println("BEFORE CRASH: " + add);
			String block = fileStr.substring(fileStr.indexOf("[/" + add), fileStr.indexOf(add + "/]") + add.length());
			// look at top
			add = block.substring(block.indexOf("[/") + 2, block.indexOf("]"));
			if (fiveExclamationMarks(add) < 5) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("EXCLAMATION TOP: " + add))
						printOut = false;
				if (printOut)
					System.out.println("EXCLAMATION TOP: " + add);
			}
			// look between parallel bars
			if (!block.contains("#npcSwitch")
					&& block.substring(block.indexOf("|"), block.lastIndexOf("|")).length() > 5) {
				add = block.substring(block.indexOf(":", block.indexOf("|")) + 1, block.lastIndexOf("|"));
				if (fiveExclamationMarks(add) < 5) {
					boolean printOut = true;
					for (String s : exceptionList)
						if (s.equals("EXCLAMATION BARS: " + add))
							printOut = false;
					if (printOut)
						System.out.println("EXCLAMATION BARS: " + add);
				}
			}
			// look at choices
			checkChoices(block);
			// look at bottom
			add = block.substring(block.indexOf("[", block.lastIndexOf("}")) + 1);
			if (fiveExclamationMarks(add) < 5) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("EXCLAMATION BOTTOM: " + add))
						printOut = false;
				if (printOut)
					System.out.println("EXCLAMATION BOTTOM: " + add);
			}
			ind++;
		}
	}

	private void checkChoices(String block) {
		while (true) {
			if (!block.contains("{"))
				break;
			block = block.substring(block.indexOf("{"));
			String add = block.substring(block.indexOf(":") + 1, block.indexOf("}"));
			if (!(add.contains("NO ADDRESS") || add.contains("FIGHT") || add.contains("END GAME"))
					&& fiveExclamationMarks(add) < 5) {
				boolean printOut = true;
				for (String s : exceptionList)
					if (s.equals("EXCLAMATION CHOICE: " + add))
						printOut = false;
				if (printOut)
					System.out.println("EXCLAMATION CHOICE: " + add);
			}
			block = block.substring(block.indexOf("}") + 1);
		}
	}

	private int fiveExclamationMarks(String add) {
		int cnt = 0;
		for (int i = 0; i < add.length(); i++)
			if (add.charAt(i) == '!')
				cnt++;
		return cnt;
	}

	private String[] generateAddressExceptionList() {
		// list of addresses that aren't broken and are exceptional
		return new String[] { "DEAD: KABU!CANYON!SUNLESS GROTTO!1.201!DESCRIPTION!",
				"DEAD: KABU!CANYON!SUNLESS GROTTO!99.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!AMPHITHEATRE!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!AMPHITHEATRE!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!ARCHIVES!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!ARCHIVES!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!BARRACKS!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!BARRACKS!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!COURTYARD!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!COURTYARD!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!GATES OF DAWN!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!MAGE'S ABODE!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!PROVING GROUNDS!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!PROVING GROUNDS!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!SUN KEEP!77.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!SUN KEEP!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!SUN KEEP!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!WALLS!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!WALLS!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAWN FORTRESS!COURTYARD!9990.X15105!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ARBORETUM!0099.000!DESCRIPTION!", "DEAD: KABU!DAZIR!ARBORETUM!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ARTISANS' MANSION!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ARTISANS' MANSION!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ARTISANS' PLAZA!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ARTISANS' PLAZA!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!ATRIUM!0099.000!DESCRIPTION!", "DEAD: KABU!DAZIR!ATRIUM!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!GATES OF DAZIR!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!GATES OF DAZIR!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!MAGE'S ABODE!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!MAGE'S ABODE!999.X000!DESCRIPTION!", "DEAD: KABU!DAZIR!SLUMS!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!SLUMS!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!WORKSHOP!0099.000!DESCRIPTION!", "DEAD: KABU!DAZIR!WORKSHOP!999.X000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!WORKSHOP EXTERIOR!0099.000!DESCRIPTION!",
				"DEAD: KABU!DAZIR!WORKSHOP EXTERIOR!999.X000!DESCRIPTION!",
				"DEAD: KABU!SANCTUARY!HALL OF SERVANTS!100.X000!DESCRIPTION!",
				"DEAD: KABU!SANCTUARY!STAR BRIDGE!1.X000!DESCRIPTION!", "DIA-ERR: END GAME!END GAME!END GAME!",
				"DEAD: KABU!TAMBUL!ARCHIVES!0099.000!DESCRIPTION!", "DEAD: KABU!TAMBUL!ARCHIVES!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!ATRIUM!0099.000!DESCRIPTION!", "DEAD: KABU!TAMBUL!ATRIUM!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!HYDROPONIC FARM!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!HYDROPONIC FARM!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!MOON KEEP!77.000!DESCRIPTION!", "DEAD: KABU!TAMBUL!MOON KEEP!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!MOON KEEP!999.X000!DESCRIPTION!", "DEAD: KABU!TAMBUL!MOON TOWER!10.001!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!MOON TOWER!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!MOON TOWER!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!MOON TOWER!999.001!DESCRIPTION!", "DEAD: KABU!TAMBUL!RAMPARTS!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!RAMPARTS!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!RESIDENCE DISTRICT!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!RESIDENCE DISTRICT!999.X000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!TEMPLE OF BIRACUL!0099.000!DESCRIPTION!",
				"DEAD: KABU!TAMBUL!TEMPLE OF BIRACUL!999.X000!DESCRIPTION!",
				"DEAD: KABU!UR'RUK!GATES OF AWAKENING!0.X0011!DESCRIPTION!",
				"DEAD: KABU!UR'RUK!GREENHOUSE!20.000!DESCRIPTION!",
				"DEAD: KABU!UR'RUK!LIVING QUARTERS!20.000!DESCRIPTION!",
				"DEAD: KABU!UR'RUK!PORTAL OF WISDOM!20.000!DESCRIPTION!",
				"DEAD: KABU!UR'RUK!STONE CIRCLE!20.000!DESCRIPTION!", "DIA-ERR: END GAME!END GAME!END GAME!",
				"DIA-ERR: END GAME!END GAME!END GAME!", "DEAD: KABU!WASTELAND!ARK'S BEACON!90.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!CRATER'S EDGE!10.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!CRESCENT CANYON!1.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!CRESCENT CANYON!90.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!FLATLANDS!100.000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!GATES OF DAWN!90.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!OBSIDIAN RUIN!0.X000!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!SALT FOREST!99.X00!DESCRIPTION!",
				"DEAD: KABU!WASTELAND!THE VEDT!90.X000!DESCRIPTION!",
				"LOOP: KABU!WELLSPRING!SANCTUM OF GULGANNA!90.0000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!FARCASTER CHAMBER!10.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!FLOOD GATE!20.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!GATE OF THE HEATHEN!502.200!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!LIVING QUARTERS!502.200!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!SANCTUM OF GULGANNA!1.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!SANCTUM OF GULGANNA!2.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!SANCTUM OF GULGANNA!15.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!SANCTUM OF GULGANNA!15.2000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!SANCTUM OF GULGANNA!20.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!THRONE ROOM!40.X000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!THRONE ROOM!50.000!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!GATE OF THE HEATHEN!9990.X11434!DESCRIPTION!",
				"DEAD: KABU!WELLSPRING!LIVING QUARTERS!9990.X11434!DESCRIPTION!",
				"DEAD: KABU!WILDERNESS!GATE OF THE HEATHEN!10.X000!DESCRIPTION!",
				"DEAD: KABU!WILDERNESS!MOUNTAIN PASS!0.100!DESCRIPTION!", "LOOP: MIND!MINDSCAPE!NEPHILHEIM!0.000!HEL!",
				"DEAD: MIND!MINDSCAPE!GRAYLANDS!0000.009!DESCRIPTION!",
				"DEAD: MIND!MINDSCAPE!THE VOID!6.500!DESCRIPTION!",
				"NPC-ERR: KABU!WELLSPRING!GATE OF THE HEATHEN!0.X000!CLOCKWORK CROWS!",
				"NPC-ERR: KABU!WELLSPRING!TEMPLE!0.X000!CLOCKWORK CROWS!",
				"NPC-ERR: KABU!WELLSPRING!HALL OF ETERNAL ATONEMENT!0.X000!VALVORTHR!" };
	}

	public void checkNPCFile() {
		// Load a list of all possible locations
		FileHandle file = Gdx.files.internal("Game Files/NPCListByLocation.mao");
		String npcLocString = file.readString();

		ArrayList<Integer> checkedNPCs = new ArrayList<Integer>();
		int start = 0, end = 0;
		Integer currNPC = 0;
		MainGameScreen mgScr = new MainGameScreen(this.consc);

		while (npcLocString.contains("|")) {
			start = npcLocString.indexOf("|") + 1;
			end = npcLocString.indexOf("|", start) + 1;
			// parse out a line of NPCs
			String listToCheck = npcLocString.substring(start, end);
			while (listToCheck.contains(",")) {
				currNPC = Integer.parseInt(listToCheck.substring(0, listToCheck.indexOf(",")));
				if (!checkedNPCs.contains(currNPC)) {
					checkedNPCs.add(currNPC);
					// loads NPC
					System.out.println(currNPC);
					NPC npc = new NPC(currNPC, mgScr);
				}
				listToCheck = listToCheck.substring(listToCheck.indexOf(",") + 1);
			}
			npcLocString = npcLocString.substring(++end);
		}
		System.out.println(checkedNPCs.size() + " NPCs checked.");
	}

	public void countWords() {
		int totalWordCnt = 0;

		String[] fileNamesBoE = new String[] { "EIDOS_CANYON.mao", "EIDOS_DAWN_FORTRESS.mao", "EIDOS_DAZIR.mao",
				"EIDOS_SANCTUARY.mao", "EIDOS_TAMBUL.mao", "EIDOS_UR'RUK.mao", "EIDOS_WASTELAND.mao",
				"EIDOS_WELLSPRING.mao", "EIDOS_WILDERNESS.mao", "MIND_MINDSCAPE.mao", "MIND_NETHER_EDGE.mao",
				"MIND_THE_BOOKS.mao" };

		String[] fileNamesBoT = new String[] { "TORMA_ARCHIVES.mao", "TORMA_HALLS_OF_THE_ADEPTI.mao",
				"TORMA_THE_THRESHOLD.mao", "TORMA_THE_NAVE.mao", "TORMA_THE_PATH_OF_DISCIPLINE.mao",
				"TORMA_THE_VAULT.mao", "TORMA_UNDERHALLS.mao", "MIND_PALACE_OF_MEMORY.mao" };

		boolean error = false;

		ArrayList<String[]> lists = new ArrayList<String[]>();
		lists.add(fileNamesBoE);
		lists.add(fileNamesBoT);
		for (String[] list : lists) {
			int bookWordCnt = 0;
			for (String fileName : list) {
				int start = 1, end = 1;
				// Load a list of all possible locations
				FileHandle file = Gdx.files.internal("Game Files/Dialogue/" + fileName);

				String fileStrToCheck = file.readString();
				while (true) {
					start = fileStrToCheck.indexOf("*", ++end) + 1;
					end = fileStrToCheck.indexOf("*", (start));
					if ((start <= 0) || (end <= 0) || (fileStrToCheck.substring(start, end).contains("EVENT WRITER")))
						break;
					int wordCnt = countWords(fileStrToCheck.substring(start, end));
					totalWordCnt += wordCnt;
					bookWordCnt += wordCnt;
					// System.out.println(fileStrToCheck.substring(start, end));
					// if (fileStrToCheck.substring(start, end).contains("/")
					// || fileStrToCheck.substring(start, end).contains("\\")
					// || fileStrToCheck.substring(start, end).contains("[")
					// || fileStrToCheck.substring(start, end).contains("]")
					// || fileStrToCheck.substring(start, end).contains("(")
					// || fileStrToCheck.substring(start, end).contains(")"))
					// error = true;
					if (error)
						break;
				}
				if (error)
					break;
			}
			if (error)
				break;
			System.out.println("B: " + bookWordCnt);
		}
		System.out.println("Tot: " + totalWordCnt);
	}

	public static int countWords(String s) {

		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;
	}

	public void loadAllNPCS() {
		int[] npcs = new int[150];
		int cnt = 0, start = 0;
		FileHandle file = Gdx.files.internal("Game Files/NPCsbyNum.mao");
		String npc_file_str = file.readString();
		while (true) {
			start = npc_file_str.indexOf("|") + 1;
			if (start < 1)
				break;
			npc_file_str = npc_file_str.substring(start);
			npcs[cnt++] = Integer
					.parseInt(npc_file_str.substring(npc_file_str.indexOf(":") + 1, npc_file_str.indexOf(",")));
		}
		for (int i : npcs) {
			try {
				NPC noob = new NPC(i, null);
			} catch (Exception e) {
				System.out.println(i);
			}
		}
	}
}
