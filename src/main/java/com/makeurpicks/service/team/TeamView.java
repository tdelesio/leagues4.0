package com.makeurpicks.service.team;

public class TeamView  {

	private String id;
	private String teamName;
	private String city;
	private String shortName;
	private String theme;
	private String feedName;
	private String leagueType;
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getFeedName() {
		return feedName;
	}
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}
	public String getLeagueType() {
		return leagueType;
	}
	public void setLeagueType(String leagueType) {
		this.leagueType = leagueType;
	}
	
	public String getFullTeamName()
	{
		return city+" "+teamName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}


