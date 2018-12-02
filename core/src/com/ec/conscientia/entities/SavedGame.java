package com.ec.conscientia.entities;

// used for load screen
public class SavedGame {

	private int savedGameNum, bookID;
	private boolean isCompletedGameFile;
	private String location;

	public SavedGame(int savedGameNum, String location, int bookID) {
		this.setSavedGameNum(savedGameNum);
		this.setLocation(location);
		this.setIsCompletedGameFile(location.contains("END GAME"));
		this.setBookID(bookID);
	}

	public int getSavedGameNum() {
		return savedGameNum;
	}

	public void setSavedGameNum(int savedGameNum) {
		this.savedGameNum = savedGameNum;
	}

	public void setLocation(String location) {
		if (location.contains("END GAME"))
			this.location = "SIMULATION COMPLETED";
		else
			this.location = location;
	}

	/*
	 * used so that a game that has already been completed cannot be reloaded
	 * would crash the game if we let this happen
	 */
	public boolean getIsCompletedGameFile() {
		return isCompletedGameFile;
	}

	public void setIsCompletedGameFile(boolean isCompletedGameFile) {
		this.isCompletedGameFile = isCompletedGameFile;
	}

	/*
	 * used to give title to game
	 */
	public String toString() {
		return "  v 1." + savedGameNum + ": " + location + "  ";
	}

	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}
}
