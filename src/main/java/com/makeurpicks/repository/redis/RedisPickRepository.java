package com.makeurpicks.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;

import com.makeurpicks.domain.Pick;
import com.makeurpicks.repository.PickRepository;

public class RedisPickRepository extends AbstractRedisHashCRUDRepository<Pick> implements PickRepository {

public static final String PICK_KEY = "picks";
	
	public RedisPickRepository(RedisTemplate<String, Pick> redisTemplate)
	{
		super(redisTemplate);
	}

	@Override
	public String getKey() {
		return PICK_KEY;
	}	

}
