package com.makeurpicks.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.PlayerLeagueId;
import com.makeurpicks.exception.LeagueValidationException;
import com.makeurpicks.exception.LeagueValidationException.LeagueExceptions;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.repository.SeasonRepository;
import com.makeurpicks.utils.HelperUtils;

@Component
public class LeagueService {

	private LeagueRepository leagueRepository;
	private PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private SeasonRepository seasonRepository;
	@Autowired
	public LeagueService(LeagueRepository leagueRepository,PlayerLeagueRepository playerLeagueRepository) {
		this.leagueRepository=leagueRepository;
		this.playerLeagueRepository=playerLeagueRepository;
		
	}
	public League createLeague(League league) throws LeagueValidationException {
		validateLeague(league);
		/*String id = UUID.randomUUID().toString();
		league.set(id);*/
		leagueRepository.save(league);
		addPlayerToLeague(league, league.getAdminId());
		return league;
	}

	public League updateLeague(League league) throws LeagueValidationException {
		validateLeague(league);
		League leagueDS = leagueRepository.findById(league.getId()).orElse(null);
		if (leagueDS == null)
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		leagueRepository.save(league);
		return league;
	}

	public Set<LeagueName> getLeaguesForPlayer(String playerId) throws LeagueValidationException {
		List<String> leagueIds = playerLeagueRepository.findIdLeagueIdsByIdPlayerId(playerId);
		if(leagueIds==null||leagueIds.size()==0) {
			return new HashSet<LeagueName>();
		}
		List<League> leagues = leagueRepository.findAllById(leagueIds);
		return HelperUtils.getLeagueNameFromLeagues(leagues);
	}
	
	public Set<String> getPlayersInLeague(String leagueid) throws LeagueValidationException {
		 return new HashSet<String> (playerLeagueRepository.findIdPlayerIdsByIdLeagueId(leagueid));
		
	}

	public void joinLeague(PlayerLeague playerLeague)
	{
		if (playerLeague.getLeagueId() == null)
		{
			if (playerLeague.getLeagueName()==null)
			{
				throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
			}
			else
			{
				League league = getLeagueByName(playerLeague.getLeagueName());
				if (league == null)
					throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
				playerLeague.setLeagueId(league.getId());
			}
		}
		
		joinLeague(playerLeague.getLeagueId(), playerLeague.getPlayerId(), playerLeague.getPassword());
	
	}

	protected void joinLeague(String leagueId, String playerId, String password) throws LeagueValidationException {

		// if (!isValidPlayer(playerId))
		// throw new
		// LeagueValidationException(LeagueExceptions.PLAYER_NOT_FOUND);

		League league = leagueRepository.findById(leagueId).orElse(null);
		if (league == null)
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);

		if (league.getPassword() != null && !"".equals(league.getPassword()) && !league.getPassword().equals(password))
			throw new LeagueValidationException(LeagueExceptions.INVALID_LEAGUE_PASSWORD);

		addPlayerToLeague(league, playerId);

	}
	
	 PlayerLeague addPlayerToLeague(League league, String playerId)
	{
		//TODO: need to create playerleague builder
		PlayerLeague playerLeague = new PlayerLeague(new PlayerLeagueId(league.getId(),playerId));
		playerLeague.setLeagueId(league.getId());
		playerLeague.setLeagueName(league.getLeagueName());
		playerLeague.setPassword(league.getPassword());
		playerLeague.setPlayerId(playerId);
		return playerLeagueRepository.save(playerLeague);
	}

	private League populateSeasonDetails(League league) {
		if (league != null && league.getSeasonId() != null && !league.getSeasonId().isEmpty()) {
			com.makeurpicks.domain.Season season = seasonRepository.findById(league.getSeasonId()).orElse(null);
			if (season != null) {
				league.setLeagueType(season.getLeagueType());
				league.setStartYear(season.getStartYear());
				league.setEndYear(season.getEndYear());
			}
		}
		return league;
	}

	public League getLeagueById(String leagueId) {
		return populateSeasonDetails(leagueRepository.findById(leagueId).orElse(null));
	}
	
	public League getLeagueByName(String leagueName) {
		return populateSeasonDetails(leagueRepository.findByLeagueName(leagueName));
	}

	/*public League getLeagueByName(String name) {
		Iterable<League> leagues = leagueRepository.findAll();
		for (League league:leagues)
		{
			if (league.getLeagueName().equals(name))
				return league;
		}
		
		return null;
	}*/

	public void removePlayerFromLeague(String leagueId, String playerId) {
		League league = getLeagueById(leagueId);
		if (league == null)
			throw new LeagueValidationException(LeagueExceptions.LEAGUE_NOT_FOUND);
		PlayerLeague playerLeague = playerLeagueRepository.findByIdLeagueIdAndIdPlayerId(league.getId(),playerId);
		if(playerLeague!=null)
			playerLeagueRepository.delete(playerLeague);
		
	}
	
	/*
	 * 
	 * 
	 */
	protected void validateLeague(League league) throws LeagueValidationException {
		if (league.getLeagueName() == null || league.getLeagueName().equals(""))
			throw new LeagueValidationException(
					LeagueExceptions.LEAGUE_NAME_IS_NULL);
		if (getLeagueByName(league.getLeagueName()) != null)
			throw new LeagueValidationException(
					LeagueExceptions.LEAGUE_NAME_IN_USE);
		if (league.getSeasonId()==null||league.getSeasonId().isEmpty())
			throw new LeagueValidationException(
					LeagueExceptions.SEASON_ID_IS_NULL);
		if (league.getAdminId()==null || league.getAdminId().isEmpty())
			throw new LeagueValidationException(
					LeagueExceptions.ADMIN_NOT_FOUND);

		if (!isValidPlayer(league.getAdminId()))
			throw new LeagueValidationException(
					LeagueExceptions.ADMIN_NOT_FOUND);
	}

	private boolean isLeagueValid(String leagueId) {
		League league = getLeagueById(leagueId);
		if (league == null)
			return false;
		else
			return true;
	}

	
	protected boolean isValidPlayer(String playerId) {
		// InstanceInfo instance =
		// discoveryClient.getNextServerFromEureka("player", false);
//		URI uri = UriComponentsBuilder.fromHttpUrl(instance.getHomePageUrl())
//				.path("player").path("/id/").path(playerId).build().encode()
//				.toUri();

		
//		ServiceInstance instance = loadBalancer.choose("player");
//		URI uri = UriComponentsBuilder.fromHttpUrl(String.format("http://%s:%d",
//				instance.getHost(), instance.getPort()))
//				.path("player").path("/id/").path(playerId).build().encode()
//				.toUri();
				

//		RestTemplate restTemplate = new RestTemplate();
//		
//		
//		PlayerResponse response = restTemplate.getForObject(uri,
//				PlayerResponse.class);

//		PlayerResponse response = getPlayer(playerId);
//		if (response != null && response.getId() != null)
			return true;
//		else
//			return false;

	}
	
	
	public Iterable<League> getAllLeagues()
	{
		Iterable<League> leagues = leagueRepository.findAll();
		for (League league : leagues) {
			populateSeasonDetails(league);
		}
		return leagues;
	}

//	public void setPlayerClient(PlayerClient playerClient) {
//		this.playerClient = playerClient;
//	}

	public void deleteLeague(String leagueId)
	{
		Set<String> playerIds = getPlayersInLeague(leagueId);
		for (String playerId:playerIds)
		{
			try {removePlayerFromLeague(leagueId, playerId);} catch (Exception e) {e.getMessage();}
		}
		
		leagueRepository.deleteById(leagueId);
	}
	
	
}
