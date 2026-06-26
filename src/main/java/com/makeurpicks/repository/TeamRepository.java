package com.makeurpicks.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.makeurpicks.domain.Team;

@Component
public class TeamRepository {

	private static Map<String, Team> teams = new HashMap<>(33);
	
	public void save(Team team)
	{
		teams.put(team.getId(), team);
		
	}
	
	public Team findOne(String id)
	{
		return teams.get(id);
	}
	
	public List<Team> getTeamsByLeagueType(String leagueType)
	{
		return new ArrayList<Team>(teams.values());
	}
	
	public Map<String, Team> getTeamMap()
	{
		return teams;
	}
}
