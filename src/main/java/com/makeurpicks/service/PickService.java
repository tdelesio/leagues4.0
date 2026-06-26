package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.exception.PickValidationException;
import com.makeurpicks.exception.PickValidationException.PickExceptions;
import com.makeurpicks.domain.Game;
import com.makeurpicks.service.GameService;
import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;

@Component
public class PickService {

	private Log log = LogFactory.getLog(PickService.class);
	
	@Autowired
	private PickRepository pickRepository;
	
	@Autowired
	private GameService gameService;
	
//	@Autowired 
//	private LeagueIntegrationService leagueIntegrationService;
	
	@Autowired
	private DoublePickRepository doublePickRepository;
	
	@Autowired
	private PicksByWeekRepository picksByWeekRepository;

	public Pick makePick(Pick pick)
	{
		//make sure all the parms are set
		validatePick(pick, false);  
		
		pick.setId(UUID.randomUUID().toString());
		
		//save pick by pick id
		pickRepository.save(pick);
		
		picksByWeekRepository.createPick(pick);		
		return pick;
	}


	public Pick updatePick(Pick pick)
	{
		//make sure all the parms are set
		validatePick(pick, true);  
		
		Pick pickFromDS = pickRepository.findById(pick.getId()).orElse(null);
		if (pickFromDS == null)
			throw new PickValidationException(PickExceptions.PICK_IS_NULL);
		if (!pickFromDS.getPlayerId().equals(pick.getPlayerId()))
			throw new PickValidationException(PickExceptions.UNAUTHORIZED_USER);
		//check to see if the pick is the existing double pick
//		DoublePick doublePick = doublePickRepository.findDoubleForPlayer(pick.getLeagueId(), pick.getWeekId(), pick.getPlayerId());
		DoublePick doublePick = getDoublePickForPlayer(pick.getLeagueId(), pick.getWeekId(), pick.getPlayerId()); 
				
		if (doublePick != null && doublePick.getPickId().equals(pick.getId()))
		{
			//the game being updated is the double so we need to clear it out
			doublePickRepository.delete(doublePick);
		}
		
		//save pick by pick id
		pickRepository.save(pick);
		
		return pick;
	}
	
	public Map<String, Pick>getPicksByWeekAndPlayer(String leagueId, String weekId, String playerId)
	{
		Map<String, Map<String, String>> map = picksByWeekRepository.findPlayersByWeek(leagueId, weekId);
		return getPicksByWeekAndPlayer(map, weekId, playerId);
	}
	
	public Map<String, Pick> getOtherPicksByWeekAndPlayer(String leagueId, String weekId, String playerId)
	{
		Map<String, Pick> picks = getPicksByWeekAndPlayer(leagueId, weekId, playerId);
//		Map<String, Pick> filteredpicks = new HashMap<>(picks.size());
//		gameClient.
//		for ()
		
		return picks;
	}
	
	private Map<String, Pick>getPicksByWeekAndPlayer(Map<String, Map<String, String>> map, String weekId, String playerId)
	{
//		Map<String, Map<String, String>> map = picksByWeekRepository.getPlayersByWeek(weekId);
		if (map==null || map.isEmpty())
			return Collections.emptyMap();
		Map<String, String> games = map.get(playerId);
		Set<String> subkeys = new TreeSet<>();
		if (games != null)
			subkeys = games.keySet();
		
		Map<String, Pick> pickMap = new HashMap<>();
		for (String gameId : subkeys) {
			String pickId = games.get(gameId);
			Pick pick = pickRepository.findById(pickId).orElse(null);
			pickMap.put(gameId, pick);
		}
		return pickMap;
		
	}
	
//	Map<String, Map<String, Pick>>
	
	public Map<String, Map<String, Pick>>getPicksByWeek(String leagueId, String weekId)
	{
		Map<String, Map<String, String>> map = picksByWeekRepository.findPlayersByWeek(leagueId, weekId);
		if (map==null)
			return Collections.emptyMap();
		
		Set<String>players = map.keySet();
		
		Map<String, Map<String, Pick>> gameMap = new HashMap<>();
		for (String playerId:players)
		{
			
			Map<String, Pick> pickMap = getPicksByWeekAndPlayer(map, weekId, playerId);
			gameMap.put(playerId, pickMap);
			
		}
		
		
		return gameMap;
	}
	
//	public Iterable<Pick> getPicksByLeagueAndWeek(String leagueId, String weekId)
//	{ 
//		Iterable<String> ids = picksByLeagueWeekRepository.getPicksForLeagueAndWeek(leagueId, weekId);
//		return pickRepository.findAll(ids);
//	}
//	
//	public Iterable<Pick> getPicksByLeagueWeekAndPlayer(String leagueId, String weekId, String playerId)
//	{ 
//		Iterable<String> ids = picksByLeagueWeekAndPlayerRepository.getPicksForLeagueWeekAndPlayer(leagueId, weekId, playerId);
//		return pickRepository.findAll(ids);
//	}
	
	
	
//	private List<LeagueResponse> getLeaguesForPlayer(String playerId) 
//	{
//		return leagueIntegrationService.getLeaguesForPlayer(playerId);
//	}
	
	
 void validatePick(Pick pick, boolean isUpdate)
	{
		List<PickExceptions> codes = new ArrayList<PickExceptions>();
		
		
		if (pick == null)
		{
			throw new PickValidationException(PickExceptions.PICK_IS_NULL);
		}
		
		if (pick.getGameId()==null || "".equals(pick.getGameId()))
			codes.add(PickExceptions.GAME_IS_NULL);
		
		if (pick.getTeamId()==null || "".equals(pick.getTeamId()))
			codes.add(PickExceptions.TEAM_IS_NULL);
		
		if (pick.getWeekId()==null||"".equals(pick.getWeekId()))
			codes.add(PickExceptions.WEEK_IS_NULL);
		
		if (pick.getLeagueId()==null||"".equals(pick.getLeagueId()))
			codes.add(PickExceptions.LEAGUE_IS_NULL);
				
		if (pick.getPlayerId()==null||"".equals(pick.getPlayerId()))
			codes.add(PickExceptions.PLAYER_IS_NUll);
		
		if (!codes.isEmpty())
			throw new PickValidationException(codes.toArray(new PickExceptions[codes.size()]));
		
		Game game = gameService.getGameById(pick.getGameId());
//		Game game = dao.loadByPrimaryKey(Game.class, pick.getGame().getId());
		if (game == null)
			codes.add(PickExceptions.GAME_IS_NULL);
//		
		if (!codes.isEmpty())
			throw new PickValidationException(codes.toArray(new PickExceptions[codes.size()]));
		
//		//load the game to make sure that the team passed is actually playing in the game
		if (!game.getFavId().equals(pick.getTeamId()) && !game.getDogId().equals(pick.getTeamId()))
			codes.add(PickExceptions.TEAM_NOT_PLAYING_IN_GAME);
//				
//		//check to make sure that the game hasn't started
		if (!pick.isAdminOverride() && game.getHasGameStarted())
			codes.add(PickExceptions.GAME_HAS_ALREADY_STARTED);
//		
//		//make sure the week matches the game
////		if (game.getWeek().getId()!=pick.getWeek().getId())
//		if (!game.getWeekId().equals(pick.getWeekId()))
//			codes.add(PickExceptions.WEEK_IS_NOT_VALID);
//				
		if (!codes.isEmpty())
			throw new PickValidationException(codes.toArray(new PickExceptions[codes.size()]));
	}
	
	
	public DoublePick getDoublePickForPlayer(String leagueId, String weekId, String playerId)
	{
		return doublePickRepository.findDoubleForPlayer(leagueId, weekId, playerId);
	}
	
	public Map<String, DoublePick> getDoublePicks(String leagueId, String weekId)
	{
//		String key = new StringBuilder(leagueId).append("+").append(weekId).append("+").toString();
//		Map<String, DoublePick> doublePicks = new HashMap<>();
//		for (DoublePick dp : doublePickRepository.findAll())
//		{
//			if (dp.getId().startsWith(key))
//			{
//				String playerId = dp.getId().substring(dp.getId().indexOf(key));
//				doublePicks.put(playerId, dp);
//			}
//		}
//		
//		return doublePicks;
		return doublePickRepository.findAllForLeagueAndWeek(leagueId, weekId);
	}

	public DoublePick makeDoublePick(String pickId, String loggedInPlayerId)
	{
//		Picks pick = dao.loadByPrimaryKey(Picks.class, pickId);
		Pick pick = pickRepository.findById(pickId).orElse(null);
		if (pick==null)
			throw new PickValidationException(PickExceptions.PICK_IS_NULL);
		
		Game game = gameService.getGameById(pick.getGameId());
		//check to see if the newly selected double has started
		if (game.getHasGameStarted())
		{
			//you can't change to a game that has started
			throw new PickValidationException(PickExceptions.GAME_HAS_ALREADY_STARTED);
		}
		
		if (!pick.isAdminOverride() && !pick.getPlayerId().equals(loggedInPlayerId))
			throw new PickValidationException(PickExceptions.UNAUTHORIZED_USER);
		
		//	Picks oldPick = getDoublePickForPlayerLeagueAndWeek(pick.getName(), pick.getLeague(), pick.getWeek());
		DoublePick orginialDoublePick = doublePickRepository.findDoubleForPlayer(pick.getLeagueId(), pick.getWeekId(), pick.getPlayerId());
		
		
		// need to check to see if the orginal pick game has started
		if (orginialDoublePick!=null)
		{
			String orginalPickId = orginialDoublePick.getPickId();
			Pick orginalPick = pickRepository.findById(orginalPickId).orElse(null);
			Game orginalGame = gameService.getGameById(orginalPick.getGameId());
			if (orginalGame.getHasGameStarted())
			{
				throw new PickValidationException(PickExceptions.GAME_HAS_ALREADY_STARTED);
			}
			
			orginialDoublePick.setPickId(pickId);
			orginialDoublePick.setGameId(pick.getGameId());
			orginialDoublePick.setPlayerId(pick.getPlayerId());
			orginialDoublePick.setHasDoubleGameStarted(false);
			orginialDoublePick.setPreviousDoubleGameId(orginalGame.getId());
			doublePickRepository.save(orginialDoublePick);			
			
			return orginialDoublePick;
		}
		// there is no orginal pick, so create a new one
		DoublePick doublePick = new DoublePick(pick.getLeagueId(), pick.getWeekId(), pick.getPlayerId(), pickId, game.getId(), game.getHasGameStarted());
		doublePickRepository.save(doublePick);
		
		return doublePick;
	}
}
