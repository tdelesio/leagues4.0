package com.makeurpicks.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PlayerLeagueId implements Serializable {
	private static final long serialVersionUID = 1L;
	public PlayerLeagueId() {}
	public PlayerLeagueId(String leagueId,String PlayerId) {
		this.leagueId=leagueId;
		this.playerId=PlayerId;
	}
	@Column
	private String leagueId;

	@Column
	private String playerId;
	
	
	public String toString() {
		return leagueId+playerId;
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

}
