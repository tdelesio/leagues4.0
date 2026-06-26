package com.makeurpicks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

	default Player findByUsername(String username) {
		return findById(username).orElse(null);
	}
}
