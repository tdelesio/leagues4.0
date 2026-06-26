package com.makeurpicks.domain;

import java.io.Serializable;

public class LeagueName implements Serializable {

	private String leagueName;
	private String leagueId;
	private String seasonId;
	
	public LeagueName()
	{
		
	}
	
	
	
	public LeagueName(League league)
	{
		this.leagueName = league.getLeagueName();
		this.leagueId = league.getId();
		this.seasonId = league.getSeasonId();
	}
	
	 
	
	public String getSeasonId() {
		return seasonId;
	}



	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}



	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public String getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leagueId == null) ? 0 : leagueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LeagueName other = (LeagueName) obj;
		if (leagueId == null) {
			if (other.leagueId != null)
				return false;
		} else if (!leagueId.equals(other.leagueId))
			return false;
		return true;
	}
	
	
}
