package com.makeurpicks.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.Player.MemberLevel;
import com.makeurpicks.domain.Player.PlayerStatus;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.Week;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.repository.PlayerRepository;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;
import com.makeurpicks.repository.PlayerLeagueRepository;
import com.makeurpicks.repository.LeagueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.makeurpicks.service.GameService;
import com.makeurpicks.service.LeagueService;
import com.makeurpicks.service.SeasonService;
import com.makeurpicks.service.WeekService;
import com.makeurpicks.service.TeamService;
import com.makeurpicks.service.game.GameView;
import com.makeurpicks.service.league.LeagueView;
import com.makeurpicks.service.pick.DoublePickView;
import com.makeurpicks.service.pick.PickView;
import com.makeurpicks.service.week.WeekView;

@Service
public class AdminService {

	@Autowired
	private WeekService weekService;
	
	@Autowired 
	private SeasonService seasonService;
	
	@Autowired
	private LeagueService leagueService;
	
	@Autowired
	private GameService gameService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PickRepository pickRepository;

	@Autowired
	private DoublePickRepository doublePickRepository;

	@Autowired
	private PicksByWeekRepository picksByWeekRepository;

	@Autowired
	private PlayerLeagueRepository playerLeagueRepository;

	@Autowired
	private LeagueRepository leagueRepository;
	
	public List<Season> getSeasons() {
		return seasonService.getCurrentSeasons();
	}
	
	public List<Season> getCurrentSeasons() {
		return seasonService.getCurrentSeasons();
	}

	public Dummy createDummyWeeks()
	{
		teamService.getTeamMap(); // ensures teams are initialized
		
		Dummy dummy = new Dummy();
		
		Season season = null;
		List<Season> currentSeasons = seasonService.getCurrentSeasons();
		if (currentSeasons == null || currentSeasons.isEmpty()) {
			season = new Season();
			season.setLeagueType("pickem");
			season.setStartYear(2026);
			season.setEndYear(2027);
			season = seasonService.createSeason(season);
		} else {
			season = currentSeasons.get(0);
		}
		
		dummy.addSeason(season);
		
		League league = new League();
		league.setLeagueName("Dummy League");
		league.setSeasonId(season.getId());
		league.setAdminId("admin");
		
		league = leagueService.createLeague(league);
		if (league == null || league.getId() == null)
			throw new RuntimeException();
		dummy.addLeague(toLeagueView(league));
		
		Week week1 = new Week();
		week1.setWeekNumber(1);
		week1.setSeasonId(season.getId());
		
		week1 = weekService.createWeek(week1);
		if (week1 == null || week1.getId() == null)
			throw new RuntimeException();
		dummy.addWeek(toWeekView(week1));
		
		Week week2 = new Week();
		week2.setWeekNumber(2);
		week2.setSeasonId(season.getId());
		
		week2 = weekService.createWeek(week2);
		if (week2 == null || week2.getId() == null)
			throw new RuntimeException();
		dummy.addWeek(toWeekView(week2));
		
		Week week3 = new Week();
		week3.setWeekNumber(3);
		week3.setSeasonId(season.getId());
		
		week3 = weekService.createWeek(week3);
		if (week3 == null || week3.getId() == null)
			throw new RuntimeException();
		dummy.addWeek(toWeekView(week3));
		
		Week week4 = new Week();
		week4.setWeekNumber(4);
		week4.setSeasonId(season.getId());
		
		week4 = weekService.createWeek(week4);
		if (week4 == null || week4.getId() == null)
			throw new RuntimeException();
		dummy.addWeek(toWeekView(week4));
		
		Week week5 = new Week();
		week5.setWeekNumber(5);
		week5.setSeasonId(season.getId());
		
		week5 = weekService.createWeek(week5);
		if (week5 == null || week5.getId() == null)
			throw new RuntimeException();
		dummy.addWeek(toWeekView(week5));
		
		
		//double pick must be gb
		Game double_won = new Game();
		double_won.setWeekId(week1.getId());
		double_won.setFavId("gb");
		double_won.setDogId("det");
		double_won.setGameStart(ZonedDateTime.now().plusDays(1));
		double_won.setFavScore(35); 
		double_won.setDogScore(22);
		double_won = gameService.createGame(double_won);
		if (double_won == null || double_won.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(double_won));
		
		Pick double_won_pick = new Pick();
		double_won_pick.setGameId(double_won.getId());
		double_won_pick.setPlayerId("admin");
		double_won_pick.setWeekId(week1.getId());
		double_won_pick.setLeagueId(league.getId());
		double_won_pick.setTeamId("gb");
		double_won_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(double_won_pick);
		picksByWeekRepository.createPick(double_won_pick);
		dummy.addPick(toPickView(double_won_pick));
		
		DoublePick double_won_doublepick = new DoublePick(league.getId(), week1.getId(), "admin", double_won_pick.getId(), double_won.getId(), false);
		doublePickRepository.save(double_won_doublepick);
		
		double_won.setGameStart(ZonedDateTime.now().minusDays(2));
		gameService.updateGame(double_won);
		
		//pick must be tb
		Game won = new Game();
		won.setWeekId(week1.getId());
		won.setFavId("tb");
		won.setDogId("atl");
		won.setGameStart(ZonedDateTime.now().minusDays(1));
		won.setFavScore(22);
		won.setDogScore(7);
		won = gameService.createGame(won);
		if (won == null || won.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(won));
		
		Pick won_pick = new Pick();
		won_pick.setGameId(won.getId());
		won_pick.setPlayerId("admin");
		won_pick.setWeekId(week1.getId());
		won_pick.setTeamId("tb");
		won_pick.setLeagueId(league.getId());
		won_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(won_pick);
		picksByWeekRepository.createPick(won_pick);
		dummy.addPick(toPickView(won_pick));
		
		//double pick on nyg
		Game double_loss = new Game();
		double_loss.setWeekId(week2.getId());
		double_loss.setFavId("nyj");
		double_loss.setDogId("nyg");
		double_loss.setGameStart(ZonedDateTime.now().plusDays(1));
		double_loss.setFavScore(9);
		double_loss.setDogScore(0);
		double_loss = gameService.createGame(double_loss);
		if (double_loss == null || double_loss.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(double_loss));
		
		Pick double_loss_pick = new Pick();
		double_loss_pick.setGameId(double_loss.getId());
		double_loss_pick.setPlayerId("admin");
		double_loss_pick.setWeekId(week2.getId());
		double_loss_pick.setTeamId("nyg");
		double_loss_pick.setLeagueId(league.getId());
		double_loss_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(double_loss_pick);
		picksByWeekRepository.createPick(double_loss_pick);
		dummy.addPick(toPickView(double_loss_pick));
		
		DoublePick double_loss_doublepick = new DoublePick(league.getId(), week2.getId(), "admin", double_loss_pick.getId(), double_loss.getId(), false);
		doublePickRepository.save(double_loss_doublepick);
		
		double_loss.setGameStart(ZonedDateTime.now().minusDays(2));
		gameService.updateGame(double_loss);
		
		
		//pick jac
		Game loss = new Game();
		loss.setWeekId(week1.getId());
		loss.setFavId("ten");
		loss.setDogId("jac");
		loss.setGameStart(ZonedDateTime.now().minusDays(1));
		loss.setFavScore(42);
		loss.setDogScore(39);
		loss = gameService.createGame(loss);
		if (loss == null || loss.getId() == null)
			throw new RuntimeException(); 
		dummy.addGame(toGameView(loss));
		
		Pick loss_pick = new Pick();
		loss_pick.setGameId(loss.getId());
		loss_pick.setPlayerId("admin");
		loss_pick.setTeamId("jac");
		loss_pick.setWeekId(week1.getId());
		loss_pick.setLeagueId(league.getId());
		loss_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(loss_pick);
		picksByWeekRepository.createPick(loss_pick);
		dummy.addPick(toPickView(loss_pick));
		
		//double pick sf
		Game locked_double_pick = new Game();
		locked_double_pick.setWeekId(week3.getId());
		locked_double_pick.setFavId("chi");
		locked_double_pick.setDogId("sf");
		locked_double_pick.setGameStart(ZonedDateTime.now().plusDays(1));
		locked_double_pick = gameService.createGame(locked_double_pick);
		if (locked_double_pick == null || locked_double_pick.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(locked_double_pick));
		
		Pick locked_double_pick_pick = new Pick();
		locked_double_pick_pick.setGameId(locked_double_pick.getId());
		locked_double_pick_pick.setPlayerId("admin");
		locked_double_pick_pick.setTeamId("sf");
		locked_double_pick_pick.setLeagueId(league.getId());
		locked_double_pick_pick.setWeekId(week3.getId());
		locked_double_pick_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(locked_double_pick_pick);
		picksByWeekRepository.createPick(locked_double_pick_pick);
		dummy.addPick(toPickView(locked_double_pick_pick));
		
		DoublePick locked_double_pick_doublepick = new DoublePick(league.getId(), week3.getId(), "admin", locked_double_pick_pick.getId(), locked_double_pick.getId(), false);
		doublePickRepository.save(locked_double_pick_doublepick);
		
		locked_double_pick.setGameStart(ZonedDateTime.now().minusDays(2));
		gameService.updateGame(locked_double_pick);
		
		//pick buf
		Game locked_pick = new Game();
		locked_pick.setWeekId(week1.getId());
		locked_pick.setFavId("buf");
		locked_pick.setDogId("hou");
		locked_pick.setGameStart(ZonedDateTime.now().minusDays(1));
		locked_pick = gameService.createGame(locked_pick);
		if (locked_pick == null || locked_pick.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(locked_pick));
		
		Pick locked_pick_pick = new Pick();
		locked_pick_pick.setGameId(locked_pick.getId());
		locked_pick_pick.setPlayerId("admin");
		locked_pick_pick.setTeamId("buf");
		locked_pick_pick.setWeekId(week1.getId());
		locked_pick_pick.setLeagueId(league.getId());
		locked_pick_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(locked_pick_pick);
		picksByWeekRepository.createPick(locked_pick_pick);
		dummy.addPick(toPickView(locked_pick_pick));
		
		//no pick
		Game no_pick_start = new Game();
		no_pick_start.setWeekId(week1.getId());
		no_pick_start.setFavId("mia");
		no_pick_start.setDogId("bal");
		no_pick_start.setGameStart(ZonedDateTime.now().minusDays(1));
		no_pick_start = gameService.createGame(no_pick_start);
		if (no_pick_start == null || no_pick_start.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(no_pick_start));
		
		//no pick
		Game no_pick_over = new Game();
		no_pick_over.setWeekId(week1.getId());
		no_pick_over.setFavId("stl");
		no_pick_over.setDogId("ari");
		no_pick_over.setFavScore(10);
		no_pick_over.setDogScore(20);
		no_pick_over.setGameStart(ZonedDateTime.now().minusDays(1));
		no_pick_over = gameService.createGame(no_pick_over);
		if (no_pick_over == null || no_pick_over.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(no_pick_over));
		
		//double pick cin
		Game double_pick = new Game();
		double_pick.setWeekId(week4.getId());
		double_pick.setFavId("cle");
		double_pick.setDogId("cin");
		double_pick.setGameStart(ZonedDateTime.now().plusDays(90));
		double_pick = gameService.createGame(double_pick);
		if (double_pick == null || double_pick.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(double_pick));
		
		Pick double_pick_pick = new Pick();
		double_pick_pick.setGameId(double_pick.getId());
		double_pick_pick.setPlayerId("admin");
		double_pick_pick.setTeamId("cin");
		double_pick_pick.setLeagueId(league.getId());
		double_pick_pick.setWeekId(week4.getId());
		double_pick_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(double_pick_pick);
		picksByWeekRepository.createPick(double_pick_pick);
		dummy.addPick(toPickView(double_pick_pick));
		
		DoublePick double_pick_doublepick = new DoublePick(league.getId(), week4.getId(), "admin", double_pick_pick.getId(), double_pick.getId(), false);
		doublePickRepository.save(double_pick_doublepick);
		
		
		//pick sea
		Game pick = new Game();
		pick.setWeekId(week1.getId());
		pick.setFavId("min");
		pick.setDogId("sea");
		pick.setGameStart(ZonedDateTime.now().plusDays(90));
		pick = gameService.createGame(pick);
		if (pick == null || pick.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(pick));
		
		Pick pick_pick = new Pick();
		pick_pick.setGameId(pick.getId());
		pick_pick.setPlayerId("admin");
		pick_pick.setTeamId("sea");
		pick_pick.setWeekId(week1.getId());
		pick_pick.setLeagueId(league.getId());
		pick_pick.setId(UUID.randomUUID().toString());
		pickRepository.save(pick_pick);
		picksByWeekRepository.createPick(pick_pick);
		dummy.addPick(toPickView(pick_pick));
		
		
		//no pick
		Game open = new Game();
		open.setWeekId(week1.getId());
		open.setFavId("oak");
		open.setDogId("kc");
		open.setGameStart(ZonedDateTime.now().plusDays(90));
		open = gameService.createGame(open);
		if (open == null || open.getId() == null)
			throw new RuntimeException();
		dummy.addGame(toGameView(open));
		
		return dummy;
	}

	private LeagueView toLeagueView(League league) {
		LeagueView view = new LeagueView();
		view.setId(league.getId());
		view.setLeagueId(league.getId());
		view.setLeagueName(league.getLeagueName());
		view.setSeasonId(league.getSeasonId());
		return view;
	}

	private WeekView toWeekView(Week week) {
		WeekView view = new WeekView();
		view.setId(week.getId());
		view.setWeekNumber(week.getWeekNumber());
		return view;
	}

	private GameView toGameView(Game game) {
		GameView view = new GameView();
		view.setId(game.getId());
		view.setSpread(game.getSpread());
		view.setSeasonId(game.getSeasonId());
		view.setFavId(game.getFavId());
		view.setDogId(game.getDogId());
		view.setWeekId(game.getWeekId());
		view.setFavScore(game.getFavScore());
		view.setDogScore(game.getDogScore());
		view.setFavHome(game.isFavHome());
		view.setGameStart(game.getGameStart());
		view.setFavFullName(game.getFavFullName());
		view.setDogFullName(game.getDogFullName());
		view.setFavShortName(game.getFavShortName());
		view.setDogShortName(game.getDogShortName());
		return view;
	}

	private PickView toPickView(Pick pick) {
		PickView view = new PickView();
		view.setId(pick.getId());
		view.setTeamId(pick.getTeamId());
		view.setLeagueId(pick.getLeagueId());
		view.setPlayerId(pick.getPlayerId());
		view.setWeekId(pick.getWeekId());
		view.setGameId(pick.getGameId());
		view.setNoPick(false);
		view.setPickLastUpdated(pick.getPickLastUpdated());
		return view;
	}

	@org.springframework.transaction.annotation.Transactional
	public void deletePlayer(String username) {
		if (username == null || username.trim().isEmpty() || "tdelesio".equals(username)) {
			return;
		}

		// 1. League Admin Ownership Transfer: reassign leagues owned by username to tdelesio
		List<League> leaguesOwned = leagueRepository.findByAdminId(username);
		if (leaguesOwned != null) {
			for (League league : leaguesOwned) {
				league.setAdminId("tdelesio");
				leagueRepository.save(league);
			}
		}

		// 2. Remove League Associations
		playerLeagueRepository.deleteByIdPlayerId(username);

		// 3. Purge Double Picks from Redis
		Map<String, String> allDoublePicks = doublePickRepository.getAllDoublePicks();
		if (allDoublePicks != null && !allDoublePicks.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			TypeReference<HashMap<String, DoublePick>> typeRef = new TypeReference<HashMap<String, DoublePick>>() {};
			for (Map.Entry<String, String> entry : allDoublePicks.entrySet()) {
				String key = entry.getKey();
				String json = entry.getValue();
				if (json != null && !json.isEmpty()) {
					try {
						Map<String, DoublePick> doublePicks = mapper.readValue(json, typeRef);
						if (doublePicks != null && doublePicks.containsKey(username)) {
							doublePicks.remove(username);
							String updatedJson = mapper.writeValueAsString(doublePicks);
							allDoublePicks.put(key, updatedJson);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
			doublePickRepository.saveAllDoublePicks(allDoublePicks);
		}

		// 4. Purge Picks from Redis and DB
		Map<String, Map<String, Map<String, Map<String, String>>>> picksByWeek = picksByWeekRepository.getAllPicksByWeek();
		if (picksByWeek != null && !picksByWeek.isEmpty()) {
			for (Map<String, Map<String, Map<String, String>>> weekMap : picksByWeek.values()) {
				if (weekMap != null) {
					for (Map<String, Map<String, String>> playersMap : weekMap.values()) {
						if (playersMap != null) {
							Map<String, String> playerGamesMap = playersMap.remove(username);
							if (playerGamesMap != null) {
								for (String pickId : playerGamesMap.values()) {
									if (pickId != null && !pickId.isEmpty()) {
										pickRepository.deleteById(pickId);
									}
								}
							}
						}
					}
				}
			}
			picksByWeekRepository.saveAllPicksByWeek(picksByWeek);
		}

		// 5. Delete Player Account
		Player player = playerRepository.findByUsername(username);
		if (player != null) {
			playerRepository.delete(player);
		}
	}

	public String resetPlayerPassword(String username) {
		Player player = playerRepository.findByUsername(username);
		if (player == null) {
			throw new RuntimeException("Player not found");
		}
		
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		java.security.SecureRandom random = new java.security.SecureRandom();
		for (int i = 0; i < 8; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		String tempPassword = sb.toString();
		
		player.setPassword(passwordEncoder.encode(tempPassword));
		player.setPasswordResetRequired(true);
		playerRepository.save(player);
		
		return tempPassword;
	}
}
