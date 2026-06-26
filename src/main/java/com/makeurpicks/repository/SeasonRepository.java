package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.Season;

@Repository
public interface SeasonRepository extends JpaRepository<Season, String> {

	public List<Season> getSeasonsByLeagueType(String leagueType);

	default Season findOne(String id) {
		return findById(id).orElse(null);
	}
}
