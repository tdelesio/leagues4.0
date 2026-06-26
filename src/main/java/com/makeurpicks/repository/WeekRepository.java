package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.Week;

@Repository
public interface WeekRepository extends CrudRepository<Week, String> {

	public List<Week> findBySeasonId(String seasonId);

	default Week findOne(String id) {
		return findById(id).orElse(null);
	}
}
