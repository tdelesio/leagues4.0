package com.makeurpicks.service.league;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.LeagueName;
import com.makeurpicks.service.LeagueService;

import rx.Observable;

@Service
public class LeagueIntegrationService {

	@Autowired
	private LeagueService leagueService;

	public Observable<List<PlayerView>> getPlayersForLeague(String leagueId) {
		Set<String> playerIds = leagueService.getPlayersInLeague(leagueId);
		List<PlayerView> views = new ArrayList<>();
		for (String playerId : playerIds) {
			views.add(new PlayerView(playerId));
		}
		return Observable.just(views);
	}

	public Observable<List<LeagueView>> getLeaguesForPlayer(String playerId) {
		Set<LeagueName> leagueNames = leagueService.getLeaguesForPlayer(playerId);
		List<LeagueView> views = new ArrayList<>();
		for (LeagueName ln : leagueNames) {
			LeagueView lv = new LeagueView();
			lv.setId(ln.getLeagueId());
			lv.setLeagueId(ln.getLeagueId());
			lv.setLeagueName(ln.getLeagueName());
			lv.setSeasonId(ln.getSeasonId());
			views.add(lv);
		}
		return Observable.just(views);
	}
}
