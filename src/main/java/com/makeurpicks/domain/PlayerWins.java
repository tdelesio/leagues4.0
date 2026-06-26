package com.makeurpicks.domain;

public class PlayerWins {

	private String playerId;
	private int wins;
	public String getPlayerId() {
		return playerId;
	}
	public int getWins() {
		return wins;
	}
	
	
	public static PlayerWins build(String playerId, int wins)
	{
		PlayerWins pw = new PlayerWins();
		pw.playerId = playerId; 
		pw.wins = wins;
		return pw;
	}
}
