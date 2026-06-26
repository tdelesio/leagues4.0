package com.makeurpicks.repository.redis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.repository.DoublePickRepository;

public class RedisDoublePlckRepository implements DoublePickRepository {

	protected final HashOperations<String, String, String> hashOps;
	
	public RedisDoublePlckRepository(RedisTemplate<String, String> redisTemplate)
	{
		this.hashOps = redisTemplate.opsForHash();
	}

//	@Override
	public String getKey() {
		return "double_pick";
	}
	
	private String buildKey(String leagueId, String weekId)
	{
		return new StringBuilder(leagueId).append("+").append(weekId).toString();
	}
	
	public Map<String, DoublePick> findAllForLeagueAndWeek(String leagueId, String weekId)
	{
		String hashkey = buildKey(leagueId, weekId);
		return findAllForLeagueAndWeek(hashkey);
	}
	
	public Map<String, DoublePick> findAllForLeagueAndWeek(String hashkey)
	{
		String json = hashOps.get(getKey(), hashkey);
		if (json == null)
			return null;
		
		TypeReference<HashMap<String,DoublePick>> typeRef  = new TypeReference<HashMap<String,DoublePick>>() {};
		Map<String, DoublePick> doublePicks;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			doublePicks = mapper.readValue(json, typeRef);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return doublePicks;
	}
	
	public DoublePick findDoubleForPlayer(String leagueId, String weekId, String playerId)
	{
		Map<String, DoublePick> doublePicks = findAllForLeagueAndWeek(leagueId, weekId);
		if (doublePicks==null || doublePicks.isEmpty())
			return null;
		
		return doublePicks.get(playerId);
		
	}
	
	public void deleteAll()
	{
		for (String key:hashOps.keys(getKey()))
		{
			hashOps.delete(getKey(), key);
		}
	}
	
	public void delete(DoublePick doublePick)
	{
		Map<String, DoublePick> doublePicks = findAllForLeagueAndWeek(doublePick.getId());
		doublePicks.remove(doublePick.getPlayerId());
	}
	
	public void save(DoublePick doublePick)
	{
		Map<String, DoublePick> doublePicks = findAllForLeagueAndWeek(doublePick.getId());
		if (doublePicks == null)
			doublePicks = new HashMap<>();
		
		doublePicks.put(doublePick.getPlayerId(), doublePick);
		
		try {
			String json = new ObjectMapper().writeValueAsString(doublePicks);
			hashOps.put(getKey(), doublePick.getId(), json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Map<String, String> getAllDoublePicks() {
		return hashOps.entries(getKey());
	}

	@Override
	public void saveAllDoublePicks(Map<String, String> doublePicks) {
		if (doublePicks != null && !doublePicks.isEmpty()) {
			hashOps.putAll(getKey(), doublePicks);
		}
	}
}


