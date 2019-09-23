package com.ec.conscientia.variables;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ec.conscientia.ingameoperations.TriggeredEvents;

public class ConscientiaVar {
	public SpriteBatch sb;

	public int currentSavedGameNum;

	// keeps track of current book
	public int bookID;

	// List of persistent items so that it doesn't activate the addItem function
	// incorrectly
	public ArrayList<Integer> persistentItemsAndEvents;

	// TRIGGERED EVENTS
	public TriggeredEvents triggeredEvents;

	public ConscientiaVar() {
		persistentItemsAndEvents = new ArrayList<Integer>();
		sb = new SpriteBatch();
	}
}
