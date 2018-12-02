package com.ec.conscientia.ingameoperations;

import java.util.HashMap;

import com.ec.conscientia.Conscientia;
import com.ec.conscientia.FileReaderWriter;
import com.ec.conscientia.screens.MainGameScreen;

public class TriggeredEvents {
	public HashMap<Integer, Boolean> triggeredEvents;

	public TriggeredEvents(Conscientia consc, MainGameScreen mgScr) {
		FileReaderWriter fileRW = new FileReaderWriter(consc, mgScr);
		triggeredEvents = fileRW.loadTriggeredEvents();
	}

	public boolean get(Integer event) {
		return triggeredEvents.get(event);
	}

	public void put(int event, boolean tORf) {
		triggeredEvents.put(event, tORf);
	}
}
