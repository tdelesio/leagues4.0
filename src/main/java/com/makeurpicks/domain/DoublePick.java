package com.makeurpicks.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoublePick extends AbstractModel {

	private String pickId;
	private String gameId;
	private String playerId;
	private boolean hasDoubleGameStarted=false;
	private String previousDoubleGameId;
	private boolean adminOverride=false;
	
	public DoublePick()
	{
		
	}
	
	public DoublePick(String leagueId, String weekId, String playerId, String pickId, String gameId, boolean gameStarted)
	{
		this.id = new StringBuilder(leagueId).append("+").append(weekId).toString();
		this.gameId = gameId;
		this.pickId = pickId;
		this.playerId = playerId;
		this.hasDoubleGameStarted = gameStarted;
	}
	
	@JsonIgnore
	public String getLeagueId() {
		if (id == null) return null;
		int idx = id.indexOf('+');
		return idx != -1 ? id.substring(0, idx) : id;
	}

	@JsonIgnore
	public String getWeekId() {
		if (id == null) return null;
		int idx = id.indexOf('+');
		return idx != -1 ? id.substring(idx + 1) : null;
	}
	
	public String getPickId() {
		return pickId;
	}

	public void setPickId(String pickId) {
		this.pickId = pickId;
	}
	
	
	
//	public String getGameId() {
//		return gameId;
//	}

//	public void setGameId(String gameId) {
//		this.gameId = gameId;
//	}

	
	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getGameId() {
		return gameId;
	}

	public String getPreviousDoubleGameId() {
		return previousDoubleGameId;
	}

	public void setPreviousDoubleGameId(String previousDoubleGameId) {
		this.previousDoubleGameId = previousDoubleGameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public boolean isHasDoubleGameStarted() {
		return hasDoubleGameStarted;
	}

	public void setHasDoubleGameStarted(boolean hasDoubleGameStarted) {
		this.hasDoubleGameStarted = hasDoubleGameStarted;
	}

	@Override
	public String toString() {
		return "DoublePick [pickId=" + pickId + ", gameId=" + gameId + ", playerId=" + playerId
				+ ", hasDoubleGameStarted=" + hasDoubleGameStarted + ", previousDoubleGameId=" + previousDoubleGameId
				+ "]";
	}

	public boolean isAdminOverride() {
		return adminOverride;
	}

	public void setAdminOverride(boolean adminOverride) {
		this.adminOverride = adminOverride;
	}
	
	

}
