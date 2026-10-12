package com.makeurpicks.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "league_messages")
public class LeagueMessage extends AbstractModel {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String leagueId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, length = 1000)
	private String message;

	private long created = System.currentTimeMillis();

	public LeagueMessage() {
		generateId();
	}

	public LeagueMessage(String leagueId, String username, String message) {
		generateId();
		this.leagueId = leagueId;
		this.username = username;
		this.message = message;
		this.created = System.currentTimeMillis();
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
}
