package com.makeurpicks.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.Week;

public class DatabaseBackupDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Player> players;
	private List<Season> seasons;
	private List<Week> weeks;
	private List<Game> games;
	private List<League> leagues;
	private List<PlayerLeague> playerLeagues;
	private List<Pick> picks;
	private Map<String, String> doublePicks;
	private Map<String, Map<String, Map<String, Map<String, String>>>> picksByWeek;

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Season> getSeasons() {
		return seasons;
	}

	public void setSeasons(List<Season> seasons) {
		this.seasons = seasons;
	}

	public List<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Week> weeks) {
		this.weeks = weeks;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public List<League> getLeagues() {
		return leagues;
	}

	public void setLeagues(List<League> leagues) {
		this.leagues = leagues;
	}

	public List<PlayerLeague> getPlayerLeagues() {
		return playerLeagues;
	}

	public void setPlayerLeagues(List<PlayerLeague> playerLeagues) {
		this.playerLeagues = playerLeagues;
	}

	public List<Pick> getPicks() {
		return picks;
	}

	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}

	public Map<String, String> getDoublePicks() {
		return doublePicks;
	}

	public void setDoublePicks(Map<String, String> doublePicks) {
		this.doublePicks = doublePicks;
	}

	public Map<String, Map<String, Map<String, Map<String, String>>>> getPicksByWeek() {
		return picksByWeek;
	}

	public void setPicksByWeek(Map<String, Map<String, Map<String, Map<String, String>>>> picksByWeek) {
		this.picksByWeek = picksByWeek;
	}
}
