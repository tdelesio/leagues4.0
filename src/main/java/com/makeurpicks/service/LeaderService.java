package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.Week;
import com.makeurpicks.domain.WinSummary;

@Service
public class LeaderService {

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private WeekService weekService;

	@Autowired
	private PickService pickService;

	@Autowired
	private GameService gameService;

	public List<WinSummary> getWinSummary(String leagueId) {
		try {
			League league = leagueService.getLeagueById(leagueId);
			if (league == null) {
				return Collections.emptyList();
			}

			Set<String> players = leagueService.getPlayersInLeague(leagueId);
			List<Week> weeks = weekService.getWeeksBySeason(league.getSeasonId());

			List<WinSummary> summaries = new ArrayList<>();
			for (String playerId : players) {
				WinSummary summary = new WinSummary(playerId);
				int totalWins = 0;

				for (Week week : weeks) {
					Map<String, Pick> picks = pickService.getPicksByWeekAndPlayer(leagueId, week.getId(), playerId);
					com.makeurpicks.domain.DoublePick doublePick = pickService.getDoublePickForPlayer(leagueId, week.getId(), playerId);
					List<Game> games = gameService.getGamesByWeek(week.getId());

					for (Game game : games) {
						if (game.getHasGameStarted()) {
							String winner = game.getGameWinner();
							if (winner != null && !winner.isEmpty()) {
								Pick pick = picks.get(game.getId());
								if (pick != null && winner.equals(pick.getTeamId())) {
									boolean isDouble = doublePick != null && game.getId().equals(doublePick.getGameId());
									totalWins += isDouble ? 2 : 1;
								}
							}
						}
					}
				}
				summary.setNumberOfWins(totalWins);
				summaries.add(summary);
			}

			Collections.sort(summaries);

			int place = 1;
			int prevWins = -1;
			int tieCount = 0;
			for (int i = 0; i < summaries.size(); i++) {
				WinSummary summary = summaries.get(i);
				if (summary.getNumberOfWins() == prevWins) {
					tieCount++;
				} else {
					place += tieCount;
					tieCount = 1;
					prevWins = summary.getNumberOfWins();
				}
				summary.setPlace(place);
			}

			return summaries;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public Map<Integer, String> getWeekWinners(String leagueId) {
		try {
			League league = leagueService.getLeagueById(leagueId);
			if (league == null) {
				return Collections.emptyMap();
			}

			List<Week> weeks = weekService.getWeeksBySeason(league.getSeasonId());
			Set<String> players = leagueService.getPlayersInLeague(leagueId);

			Map<Integer, String> weekWinners = new HashMap<>();

			for (Week week : weeks) {
				int highestScore = -1;
				List<String> winners = new ArrayList<>();

				for (String playerId : players) {
					Map<String, Pick> picks = pickService.getPicksByWeekAndPlayer(leagueId, week.getId(), playerId);
					com.makeurpicks.domain.DoublePick doublePick = pickService.getDoublePickForPlayer(leagueId, week.getId(), playerId);
					List<Game> games = gameService.getGamesByWeek(week.getId());

					int score = 0;
					for (Game game : games) {
						if (game.getHasGameStarted()) {
							String winner = game.getGameWinner();
							if (winner != null && !winner.isEmpty()) {
								Pick pick = picks.get(game.getId());
								if (pick != null && winner.equals(pick.getTeamId())) {
									boolean isDouble = doublePick != null && game.getId().equals(doublePick.getGameId());
									score += isDouble ? 2 : 1;
								}
							}
						}
					}
				}

				if (highestScore > 0) {
					weekWinners.put(week.getWeekNumber(), String.join(", ", winners));
				} else {
					weekWinners.put(week.getWeekNumber(), "No picks or games completed yet");
				}
			}

			return weekWinners;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
}
