package com.makeurpicks.domain;

public class LeagueView extends AbstractModel {

	private String seasonId;
	private boolean banker;

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public boolean isBanker() {
		return banker;
	}

	public void setBanker(boolean banker) {
		this.banker = banker;
	}
	
	
}
