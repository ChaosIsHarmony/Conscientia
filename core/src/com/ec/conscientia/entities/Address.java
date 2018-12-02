package com.ec.conscientia.entities;

public class Address {

	private String address;
	private int rank, personalityType;

	public final static int CURRENT = 0, FIRST = 1, SECOND = 2, THIRD = 3,
			FOURTH = 4, FIFTH = 5, SIXTH = 6;
	public final int DIPLOMAT = 0, TRUTHSEEKER = 1, NEUTRAL = 2,
			SURVIVALIST = 3, TYRANT = 4, LOON = 5;

	public Address() {
	}

	public Address(String address, int rank) {
		this.address = address;
		this.rank = rank;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRank() {
		switch (this.rank) {
		case CURRENT:
			return "Current";
		case FIRST:
			return "First";
		case SECOND:
			return "Second";
		case THIRD:
			return "Third";
		case FOURTH:
			return "Fourth";
		case FIFTH:
			return "Fifth";
		case SIXTH:
			return "Sixth";
		default:
			return null;
		}
	}

	public int getRankInt() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getPersonalityType() {
		switch (personalityType) {
		case TRUTHSEEKER:
			return "Truthseeker";
		case DIPLOMAT:
			return "Diplomat";
		case TYRANT:
			return "Tyrant";
		case SURVIVALIST:
			return "Survivalist";
		case NEUTRAL:
			return "Neutral";
		case LOON:
			return "Loon";
		default:
			return null;
		}
	}

	public int getPersonalityTypeInt() {
		return personalityType;
	}

	public void setPersonalityType(int personalityType) {
		this.personalityType = personalityType;
	}

}
