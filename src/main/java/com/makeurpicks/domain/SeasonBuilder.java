package com.makeurpicks.domain;

import java.util.UUID;

public class SeasonBuilder {

	private String id;
	private int startYear;
	private int endYear;
	private String leagueType;
	
	public SeasonBuilder()
	{
		
		this.id = UUID.randomUUID().toString();
	}
	
	public SeasonBuilder(String id)
	{
		this.id = id;
	}
	
	public Season build()
	{
		Season season = new Season();
		season.setId(id);
		season.setEndYear(endYear);
		season.setStartYear(startYear);
		season.setLeagueType(leagueType);
		return season;
	}
	
	public SeasonBuilder withStartYear(int s)
	{
		this.startYear = s;
		return this;
	}
	
	public SeasonBuilder withEndYear(int e)
	{
		this.endYear = e;
		return this;
	}
	
	public SeasonBuilder withLeagueType(LeagueType leagueType)
	{
		this.leagueType = leagueType.toString();
		return this;
	}
	
}
