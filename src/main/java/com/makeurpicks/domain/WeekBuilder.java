package com.makeurpicks.domain;

import java.util.UUID;

public class WeekBuilder {

	private String id;
	private int weekNumber;
	private String seasonId;
	
	public WeekBuilder()
	{
		
		this.id = UUID.randomUUID().toString();
	}
	
	public Week build()
	{
		Week week = new Week();
		week.setId(id);
		week.setWeekNumber(weekNumber);
		week.setSeasonId(seasonId);
		return week;
	}
	
	public WeekBuilder withWeekNumber(int w)
	{
		this.weekNumber = w;
		return this;
	}
	
	public WeekBuilder withSeasonId(String s)
	{
		this.seasonId = s;
		return this;
	}
}
