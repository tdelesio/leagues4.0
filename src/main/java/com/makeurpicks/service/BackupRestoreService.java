package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makeurpicks.config.DataInitializer;
import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.Week;
import com.makeurpicks.dto.DatabaseBackupDto;
import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.GameRepository;
import com.makeurpicks.repository.LeagueRepository;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.repository.PlayerRepository;
import com.makeurpicks.repository.SeasonRepository;
import com.makeurpicks.repository.WeekRepository;

@Service
public class BackupRestoreService {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private WeekRepository weekRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private LeagueRepository leagueRepository;

	@Autowired
	private PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private PickRepository pickRepository;

	@Autowired
	private DoublePickRepository doublePickRepository;

	@Autowired
	private PicksByWeekRepository picksByWeekRepository;

	@Autowired
	private DataInitializer dataInitializer;

	public DatabaseBackupDto exportDatabase() {
		DatabaseBackupDto backup = new DatabaseBackupDto();
		
		backup.setPlayers(playerRepository.findAll());
		backup.setSeasons(seasonRepository.findAll());
		backup.setWeeks(toList(weekRepository.findAll()));
		backup.setGames(toList(gameRepository.findAll()));
		backup.setLeagues(leagueRepository.findAll());
		backup.setPlayerLeagues(playerLeagueRepository.findAll());
		backup.setPicks(toList(pickRepository.findAll()));
		backup.setDoublePicks(doublePickRepository.getAllDoublePicks());
		backup.setPicksByWeek(picksByWeekRepository.getAllPicksByWeek());
		
		return backup;
	}

	@Transactional
	public void importDatabase(DatabaseBackupDto backup) {
		// 1. Clear database in dependent order (child tables first, then parent tables)
		playerLeagueRepository.deleteAll();
		gameRepository.deleteAll();
		weekRepository.deleteAll();
		leagueRepository.deleteAll();
		seasonRepository.deleteAll();
		playerRepository.deleteAll();

		// Redis tables clear
		pickRepository.deleteAll();
		doublePickRepository.deleteAll();
		picksByWeekRepository.deleteAll();

		// 2. Import database in parent first order
		if (backup.getPlayers() != null && !backup.getPlayers().isEmpty()) {
			playerRepository.saveAll(backup.getPlayers());
		}

		if (backup.getSeasons() != null && !backup.getSeasons().isEmpty()) {
			seasonRepository.saveAll(backup.getSeasons());
		}

		if (backup.getLeagues() != null && !backup.getLeagues().isEmpty()) {
			leagueRepository.saveAll(backup.getLeagues());
		}

		if (backup.getWeeks() != null && !backup.getWeeks().isEmpty()) {
			weekRepository.saveAll(backup.getWeeks());
		}

		if (backup.getGames() != null && !backup.getGames().isEmpty()) {
			gameRepository.saveAll(backup.getGames());
		}

		if (backup.getPlayerLeagues() != null && !backup.getPlayerLeagues().isEmpty()) {
			playerLeagueRepository.saveAll(backup.getPlayerLeagues());
		}

		// Import Redis cache tables
		if (backup.getPicks() != null && !backup.getPicks().isEmpty()) {
			pickRepository.saveAll(backup.getPicks());
		}

		if (backup.getDoublePicks() != null && !backup.getDoublePicks().isEmpty()) {
			doublePickRepository.saveAllDoublePicks(backup.getDoublePicks());
		}

		if (backup.getPicksByWeek() != null && !backup.getPicksByWeek().isEmpty()) {
			picksByWeekRepository.saveAllPicksByWeek(backup.getPicksByWeek());
		}

		// 3. Safety Check: Always run the DataInitializer to ensure default admin exists and is synchronized
		try {
			dataInitializer.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new ArrayList<>();
		if (iterable != null) {
			for (T element : iterable) {
				list.add(element);
			}
		}
		return list;
	}
}
