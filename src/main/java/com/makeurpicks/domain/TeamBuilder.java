package com.makeurpicks.domain;

import java.util.UUID;

public class TeamBuilder {

	private String id;
	private String teamName;
	private String city;
	private String shortName;
	private String leagueType = "pickem";
	
	public TeamBuilder()
	{
		
		this.id = UUID.randomUUID().toString();
	}
	
	public Team build()
	{
		Team team = new Team();
		team.setId(id);
		team.setTeamName(teamName);
		team.setCity(city);
		team.setShortName(shortName);
		team.setLeagueType(leagueType);
		return team;
	}
	
	public TeamBuilder withTeamName(String tm)
	{
		this.teamName = tm;
		return this;
	}
	
	public TeamBuilder withCity(String c)
	{
		this.city = c;
		return this;
	}
	
	public TeamBuilder withShortName(String s)
	{
		this.shortName = s;
		return this;
	}
	
	public TeamBuilder withLeagueType(String leagueType)
	{
		this.leagueType = leagueType;
		return this;
	}
}
