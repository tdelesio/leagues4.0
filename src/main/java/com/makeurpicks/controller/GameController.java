package com.makeurpicks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.Game;
import com.makeurpicks.domain.Week;
import com.makeurpicks.service.GameService;

@RestController
@RequestMapping(value="/games")
public class GameController {
	@Autowired
	private GameService gameService;

	@RequestMapping(method=RequestMethod.POST, value="/")
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody Game createGame(@RequestBody Game game)
	{
		return gameService.createGame(game);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/")
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody Game updateGame(@RequestBody Game game)
	{
		return gameService.updateGame(game);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/score")
	public @ResponseBody Game updateGameScore(@RequestBody Game game)
	{
		return gameService.updateGameScore(game);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value="/weekid/{id}")
	public @ResponseBody Iterable<Game> getGamesByWeek(@PathVariable String id)
	{
		return gameService.getGamesByWeek(id);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public @ResponseBody Game getGame(@PathVariable String id)
	{
		Game game =  gameService.getGameById(id);
		return game;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/autosetup")
	@PreAuthorize("hasRole('ADMIN')")
	public void callNFLandSetupWeek(@RequestBody Week week)
	{
		gameService.autoSetupWeek(week.getSeasonId());
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/autoscore")
	@PreAuthorize("hasRole('ADMIN')")
	public void callNFLandAutoScore(@RequestBody Week week)
	{
		gameService.updateScoreFromNFL(week.getId());
	}
	
	
	
	
}
