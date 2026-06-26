package com.makeurpicks.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.TeamBuilder;
import com.makeurpicks.exception.GameValidationException;
import com.makeurpicks.exception.GameValidationException.GameExceptions;
import com.makeurpicks.repository.TeamRepository;

@Component
public class TeamService {

	@Autowired
	private TeamRepository teamRepository;

	public List<Team> getTeams(String leagueType) {
		List<Team> teams = teamRepository.getTeamsByLeagueType(leagueType);
		if (teams == null || teams.isEmpty())
		{
			createAllTeams();
			return teamRepository.getTeamsByLeagueType(leagueType);
		}
		else
			return teams;
	}
	
	public Map<String, Team> getTeamMap() {
		Map<String, Team> teamMap = teamRepository.getTeamMap();
		if (teamMap == null || teamMap.isEmpty())
		{
			createAllTeams();
			teamMap = teamRepository.getTeamMap();
		}
		
		return teamMap;
		
		
		
	}

	public Team createTeam(Team team) {
		
		//String id = UUID.randomUUID().toString();
		team.setId(team.getShortName().toLowerCase());
		teamRepository.save(team);
		
		return team;
	}

	public Team getTeam(String id)
	{
		Team team = teamRepository.findOne(id);
		if (team == null)
		{
			createAllTeams();
			return teamRepository.findOne(id);
		}
		else
			return team;
		
	}
	
//	public Team getTeamByShortName(String leagueType, String sn)
//	{
//		List<Team> teams = getTeams(leagueType);
//		for (Team team:teams)
//		{ 
//			if (team.getShortName().equals(sn))
//				return team;
//		}
//		
//		throw new GameValidationException(sn, GameExceptions.TEAM_SHORT_TEAM_NOT_FOUND);
//	}
	
	protected void createAllTeams()
	{
		createTeams("pickem");
	}
	
	public List<Team> createTeams(String leagueType) {
		
		
			Team team = new TeamBuilder().withCity("Arizona").withShortName("ARI")
					.withTeamName("Cardinals").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Buffalo").withShortName("BUF")
					.withTeamName("Bills").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Miami").withShortName("MIA")
					.withTeamName("Dolphins").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("New England").withShortName("NE")
					.withTeamName("Patriots").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("New York").withShortName("NYJ")
					.withTeamName("Jets").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Baltimore").withShortName("BAL")
					.withTeamName("Ravins").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Cincinnati").withShortName("CIN")
					.withTeamName("Bengals").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Clevland").withShortName("CLE")
					.withTeamName("Browns").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Pittsburg").withShortName("PIT")
					.withTeamName("Steelers").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Houston").withShortName("HOU")
					.withTeamName("Texans").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Indianapolis").withShortName("IND")
					.withTeamName("Colts").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Jacksinville").withShortName("JAC")
					.withTeamName("Jaguars").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Tennessee").withShortName("TEN")
					.withTeamName("Titans").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Denver").withShortName("DEN")
					.withTeamName("Broncos").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Kansis City").withShortName("KC")
					.withTeamName("Chiefs").build();
			createTeam(team);
	
			team = new TeamBuilder().withCity("Oakland").withShortName("OAK")
					.withTeamName("Raiders").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("San Diego").withShortName("SD")
					.withTeamName("Chargers").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Dallas").withShortName("DAL")
					.withTeamName("Cowboys").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("New York").withShortName("NYG")
					.withTeamName("Giants").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Philladelpha").withShortName("PHI")
					.withTeamName("Eagles").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Washington").withShortName("WAS")
					.withTeamName("Redskins").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Chicago").withShortName("CHI")
					.withTeamName("Bears").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Detroit").withShortName("DET")
					.withTeamName("Lions").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Green Bay").withShortName("GB")
					.withTeamName("Packers").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Minnesota").withShortName("MIN")
					.withTeamName("Vikings").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Atlanta").withShortName("ATL")
					.withTeamName("Falcons").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Tampa Bay").withShortName("TB")
					.withTeamName("Bucineers").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("New Orleans").withShortName("NO")
					.withTeamName("Saints").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("San Fransico").withShortName("SF")
					.withTeamName("49ers").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Seattle").withShortName("SEA")
					.withTeamName("Seahawks").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("Carolina").withShortName("CAR")
					.withTeamName("Panthers").build();
			
			createTeam(team);
	
			team = new TeamBuilder().withCity("St. Loius").withShortName("STL")
					.withTeamName("Rams").build();
			
			createTeam(team);
			
			return getTeams(leagueType);
		
	}
}
