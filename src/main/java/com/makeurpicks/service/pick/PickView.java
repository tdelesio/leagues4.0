package com.makeurpicks.service.pick;

import java.io.Serializable;

public class PickView implements Serializable {

	private String id;
	private String teamId;
	private String leagueId;
	private String playerId;
	private String weekId;
	private String gameId;
	private boolean noPick=false;
	private long pickLastUpdated = System.currentTimeMillis();
	
	private boolean hystrixFailure=false;
	
	public PickView()
	{
		
	}
	
	public PickView(boolean failure)
	{
		this.hystrixFailure = failure;
	}
	

	public boolean isHystrixFailure() {
		return hystrixFailure;
	}

	public void setHystrixFailure(boolean hystrixFailure) {
		this.hystrixFailure = hystrixFailure;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPickLastUpdated(long pickLastUpdated) {
		this.pickLastUpdated = pickLastUpdated;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getWeekId() {
		return weekId;
	}

	public void setWeekId(String weekId) {
		this.weekId = weekId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public boolean isNoPick() {
		return noPick;
	}

	public void setNoPick(boolean noPick) {
		this.noPick = noPick;
	}

	public long getPickLastUpdated() {
		return pickLastUpdated;
	}
	
}
