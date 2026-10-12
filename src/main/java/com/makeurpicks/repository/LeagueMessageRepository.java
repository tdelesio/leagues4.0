package com.makeurpicks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.LeagueMessage;

@Repository
public interface LeagueMessageRepository extends JpaRepository<LeagueMessage, String> {
	List<LeagueMessage> findByLeagueIdOrderByCreatedAsc(String leagueId);
}
