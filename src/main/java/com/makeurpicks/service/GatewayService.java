package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.controller.GatewayController;
import com.makeurpicks.domain.PlayerWins;
import com.makeurpicks.domain.ViewPickColumn;
import com.makeurpicks.service.game.GameIntegrationService;
import com.makeurpicks.service.game.GameView;
import com.makeurpicks.service.league.LeagueIntegrationService;
import com.makeurpicks.service.league.PlayerView;
import com.makeurpicks.service.pick.DoublePickView;
import com.makeurpicks.service.pick.PickIntegrationService;
import com.makeurpicks.service.pick.PickView;
import com.makeurpicks.service.team.TeamIntegrationService;
import com.makeurpicks.service.team.TeamView;

import rx.Observable;

@Service
public class GatewayService {


	private Log log = LogFactory.getLog(GatewayService.class);
	
	@Autowired
	private LeagueIntegrationService leagueIntegrationService;
	
	@Autowired
	private PickIntegrationService pickIntegrationService;
	
	@Autowired
	private GameIntegrationService gameIntegrationService;
	
	@Autowired
	private TeamIntegrationService teamIntegrationService;
	
	
	private String matrixKey(String playerId, String gameId)
	{
		return new StringBuilder(playerId).append("+").append(gameId).toString();
	}
	
	public Observable<List<List<ViewPickColumn>>> getPlayersPlusWinsInLeague(String leagueId, String weekId)
	{

		List<List<ViewPickColumn>> row = new ArrayList<>();
		
		return Observable.zip(
				leagueIntegrationService.getPlayersForLeague(leagueId),
				pickIntegrationService.getPicksForAllPlayerForWeek(leagueId, weekId),
				pickIntegrationService.getAllDoublePickForPlayerForWeek(leagueId, weekId),
				gameIntegrationService.getGamesForWeek(weekId),
				teamIntegrationService.getTeams(),
				(players, picks, doublePicks, games, teams) -> {
					
					log.debug("getPlayersPlusWinsInLeague:zip");				
					log.debug("players="+players);
					log.debug("picks="+picks);
					log.debug("doublePicks="+doublePicks);
					log.debug("games="+games);
					log.debug("teams="+teams);
					
					Map<String, PlayerWins> winsByPlayer = new HashMap<>();
					 
					ViewPickColumn pickColumn;
					Map<String, ViewPickColumn> pickMatrix = new HashMap<>();
					
					for (PlayerView player : players)
					{
						Map<String, PickView> picksForAllPlayers = picks.get(player.getId());
//						columns = new ArrayList<>(players.size());
						
						//init player wins to handle non-pickers
						int wins = 0;
						
						for (GameView game : games)
						{
							
							if (!game.getHasGameStarted())
							{
								pickColumn = ViewPickColumn.asNotStarted(game.getId(), player.getId());
								pickMatrix.put(matrixKey(player.getId(), game.getId()), pickColumn);
								continue;
							}
							
							//figure out gamewinner
							String gameWinner = game.getGameWinner();
							
							PickView pick = picksForAllPlayers != null ? picksForAllPlayers.get(game.getId()) : null;
							if (pick != null)
							{
								DoublePickView dpv = doublePicks.get(player.getId());
								boolean isDouble = false;
								if (dpv!=null && dpv.getGameId().equals(game.getId()))
									isDouble = true;
								
								if (gameWinner != null && gameWinner.equals(pick.getTeamId()))
								{
									PlayerWins playerWins = winsByPlayer.get(player.getId());
									if (playerWins == null)
										wins = 0;
									else
										wins = playerWins.getWins();
										
									if (isDouble)
									{
										wins = new Integer(wins+=2);
										pickColumn = ViewPickColumn.asDoubleWinner(game.getId(), player.getId(), teams.get(pick.getTeamId()).getShortName());
									}
									else
									{
										wins = new Integer(wins+=1);
										pickColumn = ViewPickColumn.asWinner(game.getId(), player.getId(), teams.get(pick.getTeamId()).getShortName());
									}
									
//									winsByPlayer.add(PlayerWins.build(player.getId(), wins));
									
								}
								else
								{
									//game loser
									if (isDouble)
										pickColumn = ViewPickColumn.asDoubleLoser(game.getId(), player.getId(), teams.get(pick.getTeamId()).getShortName());
									else
										pickColumn = ViewPickColumn.asLoser(game.getId(), player.getId(), teams.get(pick.getTeamId()).getShortName());
								}
							}
							else
							{
								//no pick
								pickColumn = ViewPickColumn.asNoPick(game.getId(), player.getId());
							}
							
							
							winsByPlayer.put(player.getId(), PlayerWins.build(player.getId(), wins));
							pickMatrix.put(matrixKey(player.getId(), game.getId()), pickColumn);
						}						
					}
					
					Collection<PlayerWins> pw = winsByPlayer.values();
					List<PlayerWins> sortedPlayers = new ArrayList<>(pw);
					Collections.sort(sortedPlayers, (w1, w2) -> new Integer(w2.getWins()).compareTo(new Integer(w1.getWins())));
					
					
					
					
					PlayerWins player;
					GameView game;
					List<ViewPickColumn> columns = new ArrayList<>(players.size());
					for (int j=0; j<sortedPlayers.size();j++)
					{
						player = sortedPlayers.get(j);
						if (j == 0)
						{
							//0,0 should have a blank space
							columns.add(ViewPickColumn.asBlank());
						}
						
						columns.add(ViewPickColumn.asColumnHeader(player.getPlayerId(), player.getWins()));
					}
					row.add(columns);
					
					
					for (int i=0; i<games.size();i++)
					{
						game = games.get(i);
						
						columns = new ArrayList<>(players.size());
						for (int j=0; j<sortedPlayers.size();j++)
						{
							player = sortedPlayers.get(j);
							
							if (j==0)
							{
								columns.add(ViewPickColumn.asRowHeader(game.getFavShortName(), game.getDogShortName()));
			
							}
							
							columns.add(pickMatrix.get(matrixKey(player.getPlayerId(), game.getId())));	
							
						}
						
						row.add(columns);
					}
					
					return row;
				});
		
	}
}
