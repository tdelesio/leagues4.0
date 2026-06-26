package com.makeurpicks.domain;

import java.util.UUID;


public class LeagueBuilder {

	private String id;
	private String leagueName;
	private String adminId;
	private boolean spreads;
	private String password;
	private String seasonId;
	
	public LeagueBuilder()
	{
	}
	
	public LeagueBuilder(String id)
	{
		this.id = id;
	}
	
	public League build()
	{
		League league = new League();
		league.setId(id);
		league.setLeagueName(leagueName);
		league.setAdminId(adminId);
		league.setSpreads(spreads);
		league.setPassword(password);
		league.setSeasonId(seasonId);
		return league;
	}
	
	public LeagueBuilder withName(String name)
	{
		this.leagueName = name;
		return this;
	}
	
	public LeagueBuilder withPassword(String password)
	{
		this.password = password;
		return this;
	}
	
	public LeagueBuilder withNoSpreads()
	{
		this.spreads=false;
		return this;
	}
	
	public LeagueBuilder withAdminId(String adminId)
	{
		this.adminId = adminId;
		return this;
	}
	
	public LeagueBuilder withSeasonId(String id)
	{
		this.seasonId = id;
		return this;
	}
}
