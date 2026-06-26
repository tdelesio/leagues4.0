package com.makeurpicks.repository.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.makeurpicks.domain.Pick;

public abstract class AbstractRedisSetCRUDRepository {

	protected final SetOperations<String, String> setOps;
	
	public AbstractRedisSetCRUDRepository(RedisTemplate<String, String> redisTemplate)
	{
		this.setOps = redisTemplate.opsForSet();
	}

	 
	public long count(String key) {
		return setOps.size(key);
	}

	public void delete(String key, String value) {
		setOps.remove(key, value);
	}

//	public void delete(T league) {
//		setOps.delete(getKey(), league.getId());
//	}

	public void delete(String key, String... values) {
		for (String value : values) {
			delete(key, value);
		}
	}

	public void deleteAll(String key) {
		Set<String> ids = setOps.members(key);
		for (String id : ids) {
			delete(key, id);
		}
	}

	public boolean exists(String key, String value) {
		return setOps.isMember(key, value);
	}

	public Iterable<String> findAll(String key) {
		return setOps.members(key);
	}

//	public Iterable<T> findAll(Iterable<String> ids) {
//		return setOps.multiGet(getKey(), convertIterableToList(ids));
//	}

//	public String findOne(String id) {
//		return setOps.pop(getKey(), id);
//	}

	public void add(String key, String... values) {
		setOps.add(key, values);
		return;
	}

//	public <S extends T> Iterable<S> save(Iterable<S> leagues) {
//		List<S> result = new ArrayList<S>();
//		for (S entity : leagues) {
//			save(entity);
//			result.add(entity);
//		}
//		return result;
//	}

}

