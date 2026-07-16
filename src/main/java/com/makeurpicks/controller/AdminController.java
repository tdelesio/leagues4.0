package com.makeurpicks.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.Week;
import com.makeurpicks.service.AdminService;
import com.makeurpicks.service.GameRandonizor;
import com.makeurpicks.service.GameService;
import com.makeurpicks.service.LeagueService;
import com.makeurpicks.service.SeasonService;
import com.makeurpicks.service.TeamService;
import com.makeurpicks.service.WeekService;
import com.makeurpicks.service.Dummy;
import com.makeurpicks.repository.SeasonRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private GameRandonizor gameRandonizor;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private WeekService weekService;

	@Autowired
	private GameService gameService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private com.makeurpicks.repository.PlayerRepository playerRepository;

	@RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
	public void forwardToAdminIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getRequestDispatcher("/admin/index.html").forward(request, response);
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getUser(Principal principal) {
		Map<String, String> map = new HashMap<>();
		if (principal != null) {
			map.put("name", principal.getName());
		} else {
			map.put("name", "");
		}
		return map;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	@RequestMapping(value = "/leagues/", method = RequestMethod.GET)
	public @ResponseBody Iterable<League> getLeagues() {
		return leagueService.getAllLeagues();
	}

	@RequestMapping(value = "/leagues/", method = RequestMethod.POST)
	public @ResponseBody League createLeague(java.security.Principal principal, @RequestBody League league) {
		if (principal != null) {
			league.setAdminId(principal.getName());
		} else if (league.getAdminId() == null) {
			league.setAdminId("admin");
		}
		return leagueService.createLeague(league);
	}

	@RequestMapping(value = "/leagues/{id}", method = RequestMethod.DELETE)
	public @ResponseBody boolean deleteLeague(@PathVariable String id) {
		leagueService.deleteLeague(id);
		return true;
	}

	@RequestMapping(value = "/leagues/player/leagueid/{leagueId}", method = RequestMethod.GET)
	public @ResponseBody Set<String> getPlayersInLeague(@PathVariable String leagueId) {
		return leagueService.getPlayersInLeague(leagueId);
	}

	@RequestMapping(value = "/leagues/seasons/current", method = RequestMethod.GET)
	public @ResponseBody List<Season> getCurrentSeasons() {
		return seasonService.getCurrentSeasons();
	}

	@RequestMapping(value = "/leagues/types", method = RequestMethod.GET)
	public @ResponseBody LeagueType[] getLeagueTypes() {
		return LeagueType.values();
	}

	public static class PlayerAdminView {
		private String username;
		private String email;
		private List<String> leagues;
		private boolean admin;
		private String venmoId;
		private boolean paid;

		public PlayerAdminView(String username, String email, List<String> leagues, boolean admin, String venmoId, boolean paid) {
			this.username = username;
			this.email = email;
			this.leagues = leagues;
			this.admin = admin;
			this.venmoId = venmoId;
			this.paid = paid;
		}

		public String getUsername() { return username; }
		public String getEmail() { return email; }
		public List<String> getLeagues() { return leagues; }
		public boolean isAdmin() { return admin; }
		public String getVenmoId() { return venmoId; }
		public boolean isPaid() { return paid; }
	}

	@RequestMapping(value = "/players/", method = RequestMethod.GET)
	public @ResponseBody List<PlayerAdminView> getPlayers() {
		List<com.makeurpicks.domain.Player> players = playerRepository.findAll();
		List<PlayerAdminView> views = new ArrayList<>();
		for (com.makeurpicks.domain.Player player : players) {
			List<String> leagueNames = new ArrayList<>();
			try {
				Set<com.makeurpicks.domain.LeagueName> leagues = leagueService.getLeaguesForPlayer(player.getUsername());
				if (leagues != null) {
					for (com.makeurpicks.domain.LeagueName ln : leagues) {
						leagueNames.add(ln.getLeagueName());
					}
				}
			} catch (Exception e) {
				// Ignore or log
			}
			boolean isAdmin = "admin".equalsIgnoreCase(player.getAccountLevel());
			views.add(new PlayerAdminView(player.getUsername(), player.getEmail(), leagueNames, isAdmin, player.getVenmoId(), player.isPaid()));
		}
		return views;
	}

	@RequestMapping(value = "/players/{username}", method = RequestMethod.DELETE)
	public @ResponseBody boolean deletePlayer(@PathVariable String username) {
		adminService.deletePlayer(username);
		return true;
	}

	@RequestMapping(value = "/players/update-profile", method = RequestMethod.POST)
	public @ResponseBody boolean updatePlayerProfile(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		String venmoId = (String) request.get("venmoId");
		Boolean paid = (Boolean) request.get("paid");
		
		com.makeurpicks.domain.Player player = playerRepository.findByUsername(username);
		if (player != null) {
			player.setVenmoId(venmoId);
			if (paid != null) {
				player.setPaid(paid);
			}
			playerRepository.save(player);
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/players/reset-password", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> resetPassword(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String tempPassword = adminService.resetPlayerPassword(username);
		Map<String, String> response = new HashMap<>();
		response.put("username", username);
		response.put("tempPassword", tempPassword);
		return response;
	}

	@RequestMapping(value = "/leagues/seasons/", method = RequestMethod.POST)
	public @ResponseBody Season createSeason(@RequestBody Season season) {
		return seasonService.createSeason(season);
	}

	@RequestMapping(value = "/leagues/seasons/{id}", method = RequestMethod.DELETE)
	public @ResponseBody boolean deleteSeason(@PathVariable String id) {
		seasonService.deleteSeason(id);
		return true;
	}

	@RequestMapping(value = "/dummy", method = RequestMethod.POST)
	public @ResponseBody Dummy createDummyWeeks() {
		return adminService.createDummyWeeks();
	}

	@RequestMapping(value = "/random", method = RequestMethod.POST)
	public void createRandomLeague() {
		gameRandonizor.createRandomLeague(17);
	}

	@RequestMapping(value = "/weeks/seasonid/{seasonId}", method = RequestMethod.GET)
	public @ResponseBody Iterable<Week> getWeeksBySeason(@PathVariable String seasonId) {
		return weekService.getWeeksBySeason(seasonId);
	}

	@RequestMapping(value = "/weeks/", method = RequestMethod.POST)
	public @ResponseBody Week createWeek(@RequestBody Week week) {
		return weekService.createWeek(week);
	}

	@RequestMapping(value = "/weeks/{id}", method = RequestMethod.DELETE)
	public @ResponseBody boolean deleteWeek(@PathVariable String id) {
		weekService.deleteWeek(id);
		return true;
	}

	@RequestMapping(value = "/games/autosetup", method = RequestMethod.POST)
	public void callNFLandSetupWeek(@RequestBody Week week) {
		gameService.autoSetupWeek(week.getSeasonId());
	}

	@RequestMapping(value = "/games/", method = RequestMethod.POST)
	public @ResponseBody Game createGame(@RequestBody Game game) {
		return gameService.createGame(game);
	}

	@RequestMapping(value = "/games/", method = RequestMethod.PUT)
	public @ResponseBody Game updateGame(@RequestBody Game game) {
		return gameService.updateGame(game);
	}

	@RequestMapping(value = "/games/{gameId}", method = RequestMethod.GET)
	public @ResponseBody Game getGame(@PathVariable String gameId) {
		return gameService.getGameById(gameId);
	}

	@RequestMapping(value = "/games/{id}", method = RequestMethod.DELETE)
	public @ResponseBody boolean deleteGame(@PathVariable String id) {
		gameService.deleteGame(id);
		return true;
	}

	@RequestMapping(value = "/games/weekid/{weekId}", method = RequestMethod.GET)
	public @ResponseBody Iterable<Game> getGamesByWeek(@PathVariable String weekId) {
		return gameService.getGamesByWeek(weekId);
	}

	@RequestMapping(value = "/teams/leaguetype/{leagueType}", method = RequestMethod.GET)
	public @ResponseBody List<Team> getTeams(@PathVariable String leagueType) {
		return teamService.getTeams(leagueType);
	}

	@RequestMapping(value = "/teams/{id}", method = RequestMethod.GET)
	public @ResponseBody Team getTeamById(@PathVariable String id) {
		return teamService.getTeam(id);
	}

	@RequestMapping(value = "/games/autoscore", method = RequestMethod.POST)
	public void callNFLandAutoScore(@RequestBody Week week) {
		gameService.updateScoreFromNFL(week.getId());
	}

	public static class MockWeekRequest {
		private String seasonId;
		private String weekId;
		private int numberOfGames;
		private ZonedDateTime gameStart;

		public String getSeasonId() { return seasonId; }
		public void setSeasonId(String seasonId) { this.seasonId = seasonId; }
		public String getWeekId() { return weekId; }
		public void setWeekId(String weekId) { this.weekId = weekId; }
		public int getNumberOfGames() { return numberOfGames; }
		public void setNumberOfGames(int numberOfGames) { this.numberOfGames = numberOfGames; }
		public ZonedDateTime getGameStart() { return gameStart; }
		public void setGameStart(ZonedDateTime gameStart) { this.gameStart = gameStart; }
	}

	@RequestMapping(value = "/games/mock", method = RequestMethod.POST)
	public @ResponseBody List<Game> createMockWeekGames(@RequestBody MockWeekRequest request) {
		Season season = seasonRepository.findById(request.getSeasonId()).orElse(null);
		if (season == null) {
			throw new IllegalArgumentException("Season not found");
		}

		List<Team> teams = teamService.getTeams(season.getLeagueType());
		List<Team> shuffledTeams = new ArrayList<>(teams);
		Collections.shuffle(shuffledTeams);

		int gamesToCreate = request.getNumberOfGames();
		int maxGames = shuffledTeams.size() / 2;
		if (gamesToCreate > maxGames) {
			gamesToCreate = maxGames;
		}

		List<Game> createdGames = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < gamesToCreate; i++) {
			Team fav = shuffledTeams.get(i * 2);
			Team dog = shuffledTeams.get(i * 2 + 1);

			int integerPart = random.nextInt(21); // 0 to 20 inclusive
			double spread = integerPart + 0.5;

			Game game = new Game();
			game.setSeasonId(request.getSeasonId());
			game.setWeekId(request.getWeekId());
			game.setFavId(fav.getId());
			game.setFavFullName(fav.getFullTeamName());
			game.setFavShortName(fav.getShortName());
			game.setDogId(dog.getId());
			game.setDogFullName(dog.getFullTeamName());
			game.setDogShortName(dog.getShortName());
			game.setSpread(spread);
			game.setFavHome(random.nextBoolean());
			game.setGameStart(request.getGameStart());
			game.setFavScore(0);
			game.setDogScore(0);

			createdGames.add(gameService.createGame(game));
		}

		return createdGames;
	}

	@Autowired
	private com.makeurpicks.repository.LeagueRepository leagueRepository;

	@Autowired
	private com.makeurpicks.repository.WeekRepository weekRepository;

	@Autowired
	private com.makeurpicks.repository.GameRepository gameRepository;

	@Autowired
	private com.makeurpicks.repository.PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private com.makeurpicks.config.DataInitializer dataInitializer;

	@Autowired
	private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

	@RequestMapping(value = "/cache/clear", method = RequestMethod.POST)
	public void clearCache() {
		redisConnectionFactory.getConnection().flushDb();
	}

	@RequestMapping(value = "/db/reset", method = RequestMethod.POST)
	@org.springframework.transaction.annotation.Transactional
	public void resetDatabase() throws Exception {
		try {
			redisConnectionFactory.getConnection().flushDb();
		} catch (Exception e) {
			// Ignore if redis isn't connected or flush has issue
		}

		playerLeagueRepository.deleteAll();
		gameRepository.deleteAll();
		weekRepository.deleteAll();
		leagueRepository.deleteAll();
		seasonRepository.deleteAll();
		playerRepository.deleteAll();

		dataInitializer.run();
	}
}

