package com.makeurpicks.service.league;

public class LeagueView {

	private String id;
	private String leagueName;
	private String leagueId;
	private String seasonId;
	
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public String getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}
	public String getSeasonId() {
		return seasonId;
	}
	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "LeagueView [id=" + id + ", leagueName=" + leagueName + ", leagueId=" + leagueId + ", seasonId="
				+ seasonId + "]";
	}
	
	
}
