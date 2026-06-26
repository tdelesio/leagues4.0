package com.makeurpicks.repository;

import java.util.Map;

import org.springframework.data.repository.Repository;

import com.makeurpicks.domain.DoublePick;

public interface DoublePickRepository extends Repository<String, String> {

	public Map<String, DoublePick> findAllForLeagueAndWeek(String leagueId, String weekId);
	public DoublePick findDoubleForPlayer(String leagueId, String weekId, String playerId);
	public void delete(DoublePick doublePick);
	public void save(DoublePick doublePick);
	public void deleteAll();
	public Map<String, String> getAllDoublePicks();
	public void saveAllDoublePicks(Map<String, String> doublePicks);
}

