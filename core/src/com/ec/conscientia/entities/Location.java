package com.ec.conscientia.entities;

public class Location {
	private String areaName;
	private int ID;
	private boolean isDisplayed;
	private int[] size;
	private int[] coords;

	public Location(String areaName, int ID, boolean isDisplayed, int[] size, int[] coords) {
		this.areaName = areaName;
		this.ID = ID;
		this.isDisplayed = isDisplayed;
		this.size = size;
		this.coords = coords;
	}

	public String getAreaName() {
		return this.areaName;
	}

	// for use with List class when displaying in a list
	public String toString() {
		return this.ID + " : " + getAreaName();
	}

	public int getID() {
		return this.ID;
	}

	public boolean isDisplayed() {
		return this.isDisplayed;
	}

	public int[] getSize() {
		return this.size;
	}

	public int[] getCoords() {
		return this.coords;
	}
}
