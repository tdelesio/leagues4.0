package com.makeurpicks.repository.redis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.makeurpicks.domain.Pick;
import com.makeurpicks.repository.PicksByWeekRepository;

public class RedisPicksByWeekRepository implements PicksByWeekRepository {

	
	protected final HashOperations<String, String, Map<String, Map<String, Map<String, String>>>> hashOps;
	
	public RedisPicksByWeekRepository(RedisTemplate<String, Map<String, Map<String, Map<String, String>>>> redisTemplate) {
		this.hashOps = redisTemplate.opsForHash();
	}
	
	private static final String key = "PICKS_BY_LEAGUE";
	
	public void deleteAll()
	{
		Set<String> ids = hashOps.keys(key);
		for (String id : ids) {
			hashOps.delete(key, id);
		}
	}
	
	public Map<String, Map<String, Map<String, String>>> findWeeksByLeague(String leagueId)
	{
		Map<String, Map<String, Map<String, String>>> weekMap = hashOps.get(key, leagueId);
		return weekMap;
	}
	//return a list of players
	public Map<String, Map<String, String>> findPlayersByWeek(String leagueId, String weekId)
	{
	//	 Map<String, Map<String, String>> playerMap = hashOps.get(key, weekId);
		Map<String, Map<String, Map<String, String>>> weeksByLeague = findWeeksByLeague(leagueId);
		if (weeksByLeague == null || weeksByLeague.isEmpty()) 
			return Collections.EMPTY_MAP;
		
		Map<String, Map<String, String>> playerMap = weeksByLeague.get(weekId);
		 return playerMap;
	}
	
	public Map<String, String> findGamesByPlayer(String leagueId, String weekId, String playerId)
	{
		Map<String, Map<String, String>> gamesByPlayer = findPlayersByWeek(leagueId, weekId);
		if (gamesByPlayer == null || gamesByPlayer.isEmpty())
			return Collections.EMPTY_MAP;
		Map<String, String> gameMap = gamesByPlayer.get(playerId);
		return gameMap;
	}
	
	public String findPicksByGame(String leaugeId, String weekId, String playerId, String gameId)
	{
		Map<String, String> picksByGame = findGamesByPlayer(leaugeId, weekId, playerId); 
		if (picksByGame == null || picksByGame.isEmpty())
			return "";
		String pickId  = picksByGame.get(gameId);
		return pickId;
	}

	public Pick createPick(Pick pick)
	{
		String weekId = pick.getWeekId();
		String playerId = pick.getPlayerId();
		String gameId = pick.getGameId();
		String pickId = pick.getId();
		String leagueId = pick.getLeagueId();
		
		Map<String, Map<String, Map<String, String>>> weekMap = findWeeksByLeague(leagueId);
		if (weekMap == null)
			weekMap = new HashMap<>();
		
		Map<String, Map<String, String>> playerMap = weekMap.get(weekId);
		if (playerMap==null)
			playerMap = new HashMap<>();
		
		Map<String, String> gameMap = playerMap.get(playerId);
		if (gameMap == null)
			gameMap = new HashMap<>();
		
		weekMap.put(weekId, playerMap);
		gameMap.put(gameId, pickId);
		playerMap.put(playerId, gameMap);
		
		hashOps.put(key, leagueId, weekMap);
		return pick;
	}

	@Override
	public Map<String, Map<String, Map<String, Map<String, String>>>> getAllPicksByWeek() {
		return hashOps.entries(key);
	}

	@Override
	public void saveAllPicksByWeek(Map<String, Map<String, Map<String, Map<String, String>>>> data) {
		if (data != null && !data.isEmpty()) {
			hashOps.putAll(key, data);
		}
	}

}

