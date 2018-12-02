package com.ec.conscientia.entities;

public class Response {
	private String response;
	private int rank, personalityType;

	public final static int FIRST = 0, SECOND = 1, THIRD = 2,
			FOURTH = 3, FIFTH = 4, SIXTH = 5;
	public final static int DIPLOMAT = 0, TRUTHSEEKER = 1, NEUTRAL = 2,
			SURVIVALIST = 3, TYRANT = 4, LOON = 5;

	public Response() {
		this.rank = 10;
		this.personalityType = 10;
		this.response = "";
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getRank() {
		switch (this.rank) {
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
