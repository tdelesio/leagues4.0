package com.makeurpicks.service.team;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Team;
import com.makeurpicks.service.TeamService;
import rx.Observable;

@Service
public class TeamIntegrationService {

	@Autowired
	private TeamService teamService;
	
	public Observable<Map<String, TeamView>> getTeams() {
		Map<String, Team> teams = teamService.getTeamMap();
		Map<String, TeamView> views = new HashMap<>();
		if (teams != null) {
			for (Map.Entry<String, Team> entry : teams.entrySet()) {
				views.put(entry.getKey(), fromTeam(entry.getValue()));
			}
		}
		return Observable.just(views);
	}

	private TeamView fromTeam(Team team) {
		if (team == null) {
			return null;
		}
		TeamView view = new TeamView();
		view.setId(team.getId());
		view.setTeamName(team.getTeamName());
		view.setCity(team.getCity());
		view.setShortName(team.getShortName());
		view.setTheme(team.getTheme());
		view.setFeedName(team.getFeedName());
		view.setLeagueType(team.getLeagueType());
		return view;
	}
}
