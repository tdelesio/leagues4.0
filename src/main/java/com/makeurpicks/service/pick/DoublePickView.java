package com.makeurpicks.service.pick;

public class DoublePickView {

	private String id="";
	private String pickId="";
	private String gameId="";
	private String leagueId="";
	private boolean hasDoubleGameStarted=false;
	private String previousDoubleGameId;
	
	private boolean hystrixFailure=false;
	
	public DoublePickView()
	{
		
	}
	
	public DoublePickView(boolean failure)
	{
		this.hystrixFailure = failure;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPickId() {
		return pickId;
	}
	public void setPickId(String pickId) {
		this.pickId = pickId;
	}
	public String getGameId() {
		return gameId;
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
	public String getPreviousDoubleGameId() {
		return previousDoubleGameId;
	}
	public void setPreviousDoubleGameId(String previousDoubleGameId) {
		this.previousDoubleGameId = previousDoubleGameId;
	}
	public boolean isHystrixFailure() {
		return hystrixFailure;
	}
	public void setHystrixFailure(boolean hystrixFailure) {
		this.hystrixFailure = hystrixFailure;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	
	
}
