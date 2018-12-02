package com.ec.conscientia.entities;

import java.util.ArrayList;

public class Player {

	// array and final int for personality stats
	private int[] personalityAffinity;
	public final static int DIPLOMAT = 0, TRUTHSEEKER = 1, NEUTRAL = 2, SURVIVALIST = 3, TYRANT = 4, LOON = 5;
	// array for meters
	private int[] meters;
	public final static int AWARENESS = 0, MAX_ENERGY = 1, WILLPOWER = 2, STAMINA = 3, TECHNE_LV = 4, ENERGY = 5;
	// array for items, techne & abilities
	private ArrayList<Integer> itemsAcquired;

	public Player() {
		personalityAffinity = new int[] { 0, 0, 0, 0, 0, 0 };
		meters = new int[] { 0, 0, 0, 0, 0, 0 };
		itemsAcquired = new ArrayList<Integer>();
	}

	public int[] getAllPersonalityStats() {
		return personalityAffinity;
	}

	public void setDiplomat(int increment) {
		personalityAffinity[DIPLOMAT] += increment;
	}

	public void setTruthseeker(int increment) {
		personalityAffinity[TRUTHSEEKER] += increment;
	}

	public void setNeutral(int increment) {
		personalityAffinity[NEUTRAL] += increment;
	}

	public void setSurvivalist(int increment) {
		personalityAffinity[SURVIVALIST] += increment;
	}

	public void setTyrant(int increment) {
		personalityAffinity[TYRANT] += increment;
	}

	public void setLoon(int increment) {
		personalityAffinity[LOON] += increment;
	}

	public int getDiplomat() {
		return personalityAffinity[DIPLOMAT];
	}

	public int getTruthseeker() {
		return personalityAffinity[TRUTHSEEKER];
	}

	public int getNeutral() {
		return personalityAffinity[NEUTRAL];
	}

	public int getSurvivalist() {
		return personalityAffinity[SURVIVALIST];
	}

	public int getTyrant() {
		return personalityAffinity[TYRANT];
	}

	public int getLoon() {
		return personalityAffinity[LOON];
	}

	public int getMaxEnergy() {
		return 0;
	}

	public ArrayList<Integer> getItemsAcquired() {
		return itemsAcquired;
	}

	public void setItemsAcquired(ArrayList<Integer> itemsAcquired) {
		this.itemsAcquired = itemsAcquired;
	}

	public int[] getMeters() {
		return meters;
	}

	public void setMeters(int[] meters) {
		this.meters = meters;
	}

	public void setAwareness(int awareness) {
		this.meters[AWARENESS] += awareness;
	}

	public void setMaxEnergy(int maxEnergy) {
		this.meters[MAX_ENERGY] = maxEnergy;
	}

	public void setWillpower(int willpower) {
		this.meters[WILLPOWER] += willpower;
	}

	public void setStamina(int stamina) {
		this.meters[STAMINA] += stamina;
	}

	public void setTechneLevel(int techneLevel) {
		this.meters[TECHNE_LV] += techneLevel;
	}

	public void setEnergy(int energy) {
		this.meters[ENERGY] = energy;
	}
}
