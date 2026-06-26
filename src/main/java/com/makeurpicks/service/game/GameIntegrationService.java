package com.makeurpicks.service.game;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Game;
import com.makeurpicks.service.GameService;

import rx.Observable;

@Service
public class GameIntegrationService {

	@Autowired
	private GameService gameService;

	public Observable<List<GameView>> getGamesForWeek(final String weekId) {
		List<Game> games = gameService.getGamesByWeek(weekId);
		List<GameView> views = new ArrayList<>();
		for (Game game : games) {
			views.add(fromGame(game));
		}
		return Observable.just(views);
	}

	private GameView fromGame(Game game) {
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
}
