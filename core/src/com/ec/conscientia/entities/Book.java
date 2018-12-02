package com.ec.conscientia.entities;

public class Book {
	private String title;
	private int ID;

	public Book(int id) {
		if (id == 0) {
			this.title = "THE BOOK OF BIRACUL";
			this.ID = 0;
		} else if (id == 1) {
			this.title = "THE BOOK OF EIDOS";
			this.ID = 1;
		} else if (id == 2) {
			this.title = "THE BOOK OF RIKHARR";
			this.ID = 2;
		} else if (id == 3) {
			this.title = "THE BOOK OF THETIAN";
			this.ID = 3;
		} else if (id == 4) {
			this.title = "THE BOOK OF TORMA";
			this.ID = 4;
		} else if (id == 5) {
			this.title = "THE BOOK OF WULFIAS";
			this.ID = 5;
		}
	}

	public int getID() {
		return this.ID;
	}

	/*
	 * used to give title to game
	 */
	public String toString() {
		return title;
	}

}
