package com.makeurpicks.service;

import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.League;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.PlayerLeague;
import com.makeurpicks.domain.Season;
import com.makeurpicks.domain.Team;
import com.makeurpicks.domain.Week;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;
import com.makeurpicks.service.GameService;
import com.makeurpicks.service.LeagueService;
import com.makeurpicks.service.PlayerService;
import com.makeurpicks.service.SeasonService;
import com.makeurpicks.service.TeamService;
import com.makeurpicks.service.WeekService;

@Service
public class GameRandonizor {

	private Log log = LogFactory.getLog(GameRandonizor.class);
	
	@Autowired
	private GameService gameService;
	
	@Autowired
	private TeamService teamService;
	
	@Autowired
	private WeekService weekService;
	
	@Autowired
	private LeagueService leagueService;
	
	@Autowired
	private SeasonService seasonService;
	
	@Autowired
	private PlayerService playerService;

	@Autowired
	private PickRepository pickRepository;

	@Autowired
	private DoublePickRepository doublePickRepository;

	@Autowired
	private PicksByWeekRepository picksByWeekRepository;
	
	public Game createRandomGame(Week week, Team fav, Team dog)
	{
		log.debug("createRandomGame");
		
		Game game = new Game();
		game.setWeekId(week.getId());
		game.setFavId(fav.getId());
		game.setFavFullName(fav.getFullTeamName());
		game.setFavShortName(fav.getShortName());
		game.setDogId(dog.getId());
		game.setDogFullName(dog.getFullTeamName());
		game.setDogShortName(dog.getShortName());
		game.setGameStart(ZonedDateTime.now().plusMinutes(5));
		game.setFavScore(new Random().nextInt(50)); 
		game.setDogScore(new Random().nextInt(50));
		
		int spread = new Random().nextInt(20);
		double halfpointspread = (double) spread + 0.5;
		game.setSpread(halfpointspread);
		game.setFavHome(new Random().nextBoolean());
		
		game = gameService.createGame(game);
		if (game == null || game.getId() == null)
			throw new RuntimeException();
		
		log.debug("createRandomGame game= " + game.toString());
		return game;
	}
	
	public void createRandomWeek(List<Player> players, Week week, String leagueId, int numberOfGames)
	{
		week = weekService.createWeek(week);
		log.debug("createRandomWeek Week= " + week.toString());
		
		List<Team> teams = new ArrayList<>(teamService.getTeamMap().values());
		Collections.shuffle(teams);
		
		Map<String, Integer> doubleMap = new HashMap<>();
		for (Player player : players)
		{
			int doubleIndex = new Random().nextInt(numberOfGames);
			if (numberOfGames == doubleIndex)
				continue;
			
			doubleMap.put(player.getUsername(), doubleIndex);
		}
		
		for (int i = 0; i < numberOfGames; i++)
		{
			if (teams.size() < 2) {
				break;
			}
			Team fav = teams.remove(0);
			Team dog = teams.remove(0);
			
			log.debug("createRandomWeek week=" + week + " fav=" + fav + " dog=" + dog);
			Game game = createRandomGame(week, fav, dog);
			 
			if (!game.getHasGameStarted())
			{	
				for (Player player : players)
				{
					int noPick = new Random().nextInt(10);
					if (noPick == 5)
					{
						// player skips game
						continue;
					}
					
					boolean isFavPick = new Random().nextBoolean();
					String teamId = game.getFavId();
					if (isFavPick)
						teamId = game.getDogId();
					
					Pick pick = new Pick();
					pick.setGameId(game.getId());
					pick.setLeagueId(leagueId);
					pick.setPlayerId(player.getUsername());
					pick.setWeekId(week.getId());
					pick.setTeamId(teamId);
					pick.setId(UUID.randomUUID().toString());
					
					pickRepository.save(pick);
					picksByWeekRepository.createPick(pick);
					
					Integer doubleGame = doubleMap.get(player.getUsername());
					if (doubleGame != null && doubleGame == i)
					{
						// game is double
						DoublePick doublePick = new DoublePick(leagueId, week.getId(), player.getUsername(), pick.getId(), game.getId(), false);
						doublePickRepository.save(doublePick);
					}
				}
			}
		}
	}
	
	public void createRandomLeague(int numberOfWeeks)
	{
		List<Season> seasons = seasonService.getCurrentSeasons();
		if (seasons == null || seasons.isEmpty()) {
			Season season = new Season();
			season.setLeagueType("pickem");
			season.setStartYear(2026);
			season.setEndYear(2027);
			season = seasonService.createSeason(season);
			seasons = Collections.singletonList(season);
		}
		
		String seasonId = seasons.get(0).getId();
		League league = new League();
		league.setLeagueName("demo " + new Random().nextInt(1000000));
		league.setSeasonId(seasonId);
		league.setAdminId("adminrandom");
		league.setPassword("12345");
		
		league = leagueService.createLeague(league);
		List<Player> players = createUsers(new Random().nextInt(10) + 5);
		// join league
		for (Player player : players)
		{
			PlayerLeague playerLeague = new PlayerLeague();
			playerLeague.setLeagueId(league.getId());
			playerLeague.setPlayerId(player.getUsername());
			playerLeague.setPassword("12345");
			leagueService.joinLeague(playerLeague);
		}
		
		log.debug("League: " + league.toString());
		
		if (numberOfWeeks == 0)
			numberOfWeeks = 17;
		
		for (int i = 0; i < numberOfWeeks; i++)
		{
			Week week = new Week();
			week.setSeasonId(seasonId);
			week.setWeekNumber(i + 1);
			
			int numberOfGame = 16;
			if (i > 2 && i < 9)
				numberOfGame = 14;
				
			createRandomWeek(players, week, league.getId(), numberOfGame);
		}
	}
	
	private String randomString()
	{
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
	
	public List<Player> createUsers(int num_users)
	{
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < num_users; i++)
		{
			String username = randomString();
			String email = username + "@gmail.com";
			String password = "12345";
			Player player = new Player();
			player.setEmail(email);
			player.setUsername(username);
			player.setPassword(password);
		
			player = playerService.createPlayer(player);
			players.add(player);
			
			log.info("Player: " + player.toString());
		}
		
		return players;
	}
}
