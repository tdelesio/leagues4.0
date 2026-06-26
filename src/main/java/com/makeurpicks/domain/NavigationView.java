package com.makeurpicks.domain;

import java.util.List;

import com.makeurpicks.service.league.LeagueView;
import com.makeurpicks.service.week.WeekView;

public class NavigationView {

	private String username;
	private List<LeagueView> leagues;
	private List<WeekView> weeks;
	
	public String getDefaultWeekId()
	{
		if (weeks!=null && !weeks.isEmpty())
			return weeks.get(0).getId();
		else
			return "";
	}
	
	public String getDefaultLeagueId()
	{
		if (leagues!=null && !leagues.isEmpty())
			return leagues.get(0).getLeagueId();
		else
			return "";
	}
	
	public String getDefaultSeasonId()
	{
		if (leagues!=null && !leagues.isEmpty())
			return leagues.get(0).getSeasonId();
		else
			return "";
	}
//	private String selectedSeasonId;
//	private String selectedWeekId;
//	private String selectedLeagueId;
	


//	public String getSelectedSeasonId() {
//		return selectedSeasonId;
//	}
//
//	public void setSelectedSeasonId(String selectedSeasonId) {
//		this.selectedSeasonId = selectedSeasonId;
//	}

//	public String getSelectedWeekId() {
////		if (selectedWeekId == null)
////			if (weeks != null && !weeks.isEmpty())
////				return weeks.get(0).getWeekId();
////			else
////				return null;
////		else
//			return selectedWeekId;
//	}
//
//	public void setSelectedWeekId(String selectedWeekId) {
//		this.selectedWeekId = selectedWeekId;
//	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<LeagueView> getLeagues() {
		return leagues;
	}
	public void setLeagues(List<LeagueView> leagues) {
		this.leagues = leagues;
	}
	public List<WeekView> getWeeks() {
		return weeks;
	}
	public void setWeeks(List<WeekView> weeks) {
		this.weeks = weeks;
	}

//	public String getSelectedLeagueId() {
//		return selectedLeagueId;
//	}
//
//	public void setSelectedLeagueId(String selectedLeagueId) {
//		this.selectedLeagueId = selectedLeagueId;
//	}
	
	
}
