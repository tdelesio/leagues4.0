package com.makeurpicks.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PlayerLeague  {
	@EmbeddedId
	private PlayerLeagueId id;
	private String leagueName;
	private String password;
	public PlayerLeague() {
		this.id = new PlayerLeagueId();
	}
	public PlayerLeague(PlayerLeagueId playerLeagueId) {
		id = playerLeagueId;
	}
	public String getLeagueId() {
		return id.getLeagueId();
	}
	public void setLeagueId(String leagueId) {
		this.id.setLeagueId(leagueId); 
	}
	public PlayerLeagueId getId() {
		return id;
	}
	public void setId(PlayerLeagueId id) {
		this.id = id;
	}
	public String getPlayerId() {
		return id.getPlayerId();
	}
	public void setPlayerId(String playerId) {
		this.id.setPlayerId(playerId);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	
	
}
