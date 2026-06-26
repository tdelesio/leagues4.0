package com.makeurpicks.domain;

import java.util.UUID;

public class PickBuilder {

	private String id;
	private String teamId;
	private String leagueId;
	private String playerId;
	private String weekId;
	private String gameId;
	
	public PickBuilder()
	{
		
		this.id = UUID.randomUUID().toString();
	}
	
	public PickBuilder(String id)
	{
		this.id = id;
	}
	
	public Pick build()
	{
		Pick pick = new Pick();
		pick.setId(id);
		pick.setTeamId(teamId);
		pick.setLeagueId(leagueId);
		pick.setWeekId(weekId);
		pick.setGameId(gameId);
		pick.setPlayerId(playerId);
		return pick;
	}
	
	public PickBuilder withTeamId(String tid)
	{
		this.teamId = tid;
		return this;
	}
	
	public PickBuilder withLeagueId(String lid)
	{
		this.leagueId  = lid;
		return this;
	}
	
	public PickBuilder withWeekId(String wid)
	{
		this.weekId = wid;
		return this;
	}
	
	public PickBuilder withGameId(String gid)
	{
		this.gameId = gid;
		return this;
	}
	
	public PickBuilder withPlayerId(String pid)
	{
		this.playerId = pid;
		return this;
	}
}
