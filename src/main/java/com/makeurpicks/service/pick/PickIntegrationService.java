package com.makeurpicks.service.pick;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Pick;
import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.service.PickService;

import rx.Observable;

@Service
public class PickIntegrationService {

	@Autowired
	private PickService pickService;

	public Observable<Map<String, PickView>> getPicksForPlayerForWeek(String leagueid, String weekid) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String playerId = (auth != null) ? auth.getName() : "";
		Map<String, Pick> picks = pickService.getPicksByWeekAndPlayer(leagueid, weekid, playerId);
		Map<String, PickView> views = new HashMap<>();
		if (picks != null) {
			for (Map.Entry<String, Pick> entry : picks.entrySet()) {
				views.put(entry.getKey(), fromPick(entry.getValue()));
			}
		}
		return Observable.just(views);
	}

	public Observable<DoublePickView> getDoublePickForPlayerForWeek(String leagueid, String weekid) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String playerId = (auth != null) ? auth.getName() : "";
		DoublePick doublePick = pickService.getDoublePickForPlayer(leagueid, weekid, playerId);
		return Observable.just(fromDoublePick(doublePick));
	}

	public Observable<Map<String, Map<String, PickView>>> getPicksForAllPlayerForWeek(String leagueid, String weekid) {
		Map<String, Map<String, Pick>> picks = pickService.getPicksByWeek(leagueid, weekid);
		Map<String, Map<String, PickView>> views = new HashMap<>();
		if (picks != null) {
			for (Map.Entry<String, Map<String, Pick>> playerEntry : picks.entrySet()) {
				Map<String, PickView> playerPickViews = new HashMap<>();
				for (Map.Entry<String, Pick> gameEntry : playerEntry.getValue().entrySet()) {
					playerPickViews.put(gameEntry.getKey(), fromPick(gameEntry.getValue()));
				}
				views.put(playerEntry.getKey(), playerPickViews);
			}
		}
		return Observable.just(views);
	}

	public Observable<Map<String, DoublePickView>> getAllDoublePickForPlayerForWeek(String leagueid, String weekid) {
		Map<String, DoublePick> doublePicks = pickService.getDoublePicks(leagueid, weekid);
		Map<String, DoublePickView> views = new HashMap<>();
		if (doublePicks != null) {
			for (Map.Entry<String, DoublePick> entry : doublePicks.entrySet()) {
				views.put(entry.getKey(), fromDoublePick(entry.getValue()));
			}
		}
		return Observable.just(views);
	}

	private PickView fromPick(Pick pick) {
		if (pick == null) {
			return null;
		}
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

	private DoublePickView fromDoublePick(DoublePick doublePick) {
		if (doublePick == null) {
			return null;
		}
		DoublePickView view = new DoublePickView();
		view.setId(doublePick.getId());
		view.setPickId(doublePick.getPickId());
		view.setGameId(doublePick.getGameId());
		view.setLeagueId(doublePick.getLeagueId());
		view.setHasDoubleGameStarted(doublePick.isHasDoubleGameStarted());
		view.setPreviousDoubleGameId(doublePick.getPreviousDoubleGameId());
		return view;
	}
}
