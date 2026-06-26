package com.makeurpicks.repository.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import com.makeurpicks.domain.AbstractModel;

public abstract class AbstractRedisHashCRUDRepository<T extends AbstractModel> implements CrudRepository<T, String> {

	protected final HashOperations<String, String, T> hashOps;

	public abstract String getKey();

	public AbstractRedisHashCRUDRepository(RedisTemplate<String, T> redisTemplate) {
		this.hashOps = redisTemplate.opsForHash();
	}

	@Override
	public long count() {
		return hashOps.keys(getKey()).size();
	}

	@Override
	public void deleteById(String id) {
		hashOps.delete(getKey(), id);
	}

	@Override
	public void delete(T entity) {
		hashOps.delete(getKey(), entity.getId());
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		Set<String> ids = hashOps.keys(getKey());
		for (String id : ids) {
			deleteById(id);
		}
	}

	@Override
	public boolean existsById(String id) {
		return hashOps.hasKey(getKey(), id);
	}

	@Override
	public Iterable<T> findAll() {
		return hashOps.values(getKey());
	}

	@Override
	public Iterable<T> findAllById(Iterable<String> ids) {
		return hashOps.multiGet(getKey(), convertIterableToList(ids));
	}

	@Override
	public Optional<T> findById(String id) {
		T val = hashOps.get(getKey(), id);
		return Optional.ofNullable(val);
	}

	@Override
	public <S extends T> S save(S entity) {
		hashOps.put(getKey(), entity.getId(), entity);
		return entity;
	}

	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<S>();
		for (S entity : entities) {
			save(entity);
			result.add(entity);
		}
		return result;
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids) {
			deleteById(id);
		}
	}

	private <T> List<T> convertIterableToList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		for (T object : iterable) {
			list.add(object);
		}
		return list;
	}
}
