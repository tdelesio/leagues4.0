package com.makeurpicks.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.makeurpicks.domain.MakePicks;
import com.makeurpicks.domain.NavigationView;
import com.makeurpicks.domain.ViewPickColumn;
import com.makeurpicks.domain.ViewPicks;
import com.makeurpicks.service.GatewayService;
import com.makeurpicks.service.game.GameIntegrationService;
import com.makeurpicks.service.game.GameView;
import com.makeurpicks.service.league.LeagueIntegrationService;
import com.makeurpicks.service.league.PlayerView;
import com.makeurpicks.service.pick.PickIntegrationService;
import com.makeurpicks.service.pick.PickView;
import com.makeurpicks.service.week.WeekIntegrationService;

import rx.Observable;
import rx.Observer;

@RestController
public class GatewayController {

	private Log log = LogFactory.getLog(GatewayController.class);
	
	@Autowired
	private GameIntegrationService gameIntegrationService;

	@Autowired
	private PickIntegrationService pickIntegrationService;

	@Autowired
	private LeagueIntegrationService leagueIntegrationService;

	@Autowired
	private WeekIntegrationService weekIntegrationService;
	
	@Autowired
	private GatewayService gatewayService;

	@RequestMapping(value = "/oauth/token/revoke", method = RequestMethod.POST)
	public @ResponseBody void logout(HttpSession session) {
		session.invalidate();
	}
	
	@RequestMapping("/user")
    public Object home(Principal principal) {
		return principal;
    }
	
	@RequestMapping("/makepicks/leagueid/{leagueid}/weekid/{weekid}")
	 public DeferredResult<MakePicks> makePicksByWeekId(@PathVariable String leagueid, @PathVariable String weekid, Principal principal) {
//		return buildMakePicks(principal.getName(), weekId);
		return toDeferredResult(buildMakePicks(principal.getName(), leagueid, weekid));
	}
	
	@RequestMapping(value="/viewpicks/leagueid/{leagueid}/weekid/{weekid}")
	public @ResponseBody DeferredResult<List<List<ViewPickColumn>>> getPlayersPlusWinsInLeague(@PathVariable String leagueid, @PathVariable String weekid) 
	{
		DeferredResult<List<List<ViewPickColumn>>> result = new DeferredResult<>();
		gatewayService.getPlayersPlusWinsInLeague(leagueid, weekid).subscribe(new Observer<List<List<ViewPickColumn>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(List<List<ViewPickColumn>> weekStats) {
                result.setResult(weekStats);
            }
        });
        
		return result;
	}

	@RequestMapping("/header")
	public NavigationView getHeader(Principal user)
	{
		return buildNavigation(user.getName());
	}
	
//	@RequestMapping("/makepicks")
//	public DeferredResult<MakePicks> makePicks(Principal principal) {
//
////		return buildMakePicks(principal.getName(), null);
//		return toDeferredResult(buildMakePicks(principal.getName(), null));
//	}
//	
//	@RequestMapping("/makepicks2")
//	public MakePicks makePicks2(Principal principal) {
//
////		return buildMakePicks(principal.getName(), null);
//		return toNonDeferredResult(buildMakePicks(principal.getName(), null));
//	}

//	@RequestMapping("/viewpicks/leagueid/{leagueid}/weekid/{weekid}")
//	public DeferredResult<ViewPicks> viewPicks(Principal principal, @PathVariable String leagueid, @PathVariable String weekid) {
//
////		return buildMakePicks(principal.getName(), null);
//		return toViewDeferredResult(buildViewPicks(principal.getName(), leagueid, weekid));
//	}
//	
//
//	private Observable<ViewPicks> buildViewPicks(String userId, String leagueId, String weekId) {
//		
//		DeferredResult<ViewPicks> result = new DeferredResult<>();
//		ViewPicks viewPicksView = new ViewPicks();
//		
////		emmitNavigation(userId, weekId)
////			.subscribe(nav -> makePicksView.setNav(nav))
////		;
//
//		
//		return Observable.zip(
//				gameIntegrationService.getGamesForWeek(weekId),
//				pickIntegrationService.getPicksForPlayerForWeek(leagueId, weekId),
//				pickIntegrationService.getDoublePickForPlayerForWeek(leagueId, weekId),
//				leagueIntegrationService.getPlayersForLeague(leagueId),
//				(games, picks, doublePick, players) -> {
//					
//					List<List<String>> rows = new ArrayList<>(17);
//					List<String> columns;
//					
//					GameView game;
//					for (int i=0; i<games.size(); i++)
//					{
//						game = games.get(i);
//						columns = new ArrayList<>(players.size());
//						
//						//put header in first row
//						if (i==0)
//						{
//							//0,0 should have a blank space
//							columns.add("&nbsp;");
//							for (PlayerView player: players)
//							{
//								columns.add(player.getId());
//							}
//							
//							rows.add(columns);
//							continue;
//						}
//						
//						if (i==1)
//						{
//							columns.add("wins");
//						}
//							
//						columns.add(new StringBuilder(game.getFavShortName()).append(" vs ").append(game.getDogShortName()).toString());
//						
//						for (PlayerView player: players)
//						{
//							if (!game.getHasGameStarted()) {
//								columns.add("-");
//							}
//							else
//							{
//								PickView pick = picks.get(player.getId());
//								if (pick == null) 
//									columns.add("-");
//							}
//						}
//						
//					}
//
//					viewPicksView.setData(rows);
//					return viewPicksView;
//				});
//			
//	}
//	
	private Observable<MakePicks> buildMakePicks(String userId, String leagueId, String weekId) {
	
		DeferredResult<MakePicks> result = new DeferredResult<>();
		MakePicks makePicksView = new MakePicks();
		
//		emmitNavigation(userId, weekId)
//			.subscribe(nav -> makePicksView.setNav(nav))
		;

		
		return Observable.zip(
				gameIntegrationService.getGamesForWeek(weekId),
				pickIntegrationService.getPicksForPlayerForWeek(leagueId, weekId),
				pickIntegrationService.getDoublePickForPlayerForWeek(leagueId, weekId),
				(games, picks, doublePick) -> {
					
					makePicksView.setGames(games);
					
					if (picks != null && picks.get("failure")!=null)
					{
						makePicksView.setHystrixFailureOnPicks(true);
						makePicksView.setPicks(Collections.EMPTY_MAP);
					}
					else
						makePicksView.setPicks(picks);
					makePicksView.setDoublePick(doublePick);
	
						
					return makePicksView;
				});
			
	}
	
//	public MakePicks toNonDeferredResult(Observable<MakePicks> details)
//	{
//		MakePicks makePicks;
//		return details.toBlocking().last();
//
//	}

	public DeferredResult<MakePicks> toDeferredResult(Observable<MakePicks> details) {
        DeferredResult<MakePicks> result = new DeferredResult<>();
        details.subscribe(new Observer<MakePicks>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(MakePicks makePicks) {
                result.setResult(makePicks);
            }
        });
        return result;
    }
	
	public DeferredResult<ViewPicks> toViewDeferredResult(Observable<ViewPicks> details) {
        DeferredResult<ViewPicks> result = new DeferredResult<>();
        details.subscribe(new Observer<ViewPicks>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(ViewPicks viewPicks) {
                result.setResult(viewPicks);
            }
        });
        return result;
    }
	
	private NavigationView buildNavigation(String userId)
	{
		NavigationView navigationView = new NavigationView();
		navigationView.setUsername(userId);
		
	
			leagueIntegrationService.getLeaguesForPlayer(userId)
				.filter(leagues -> leagues != null)
				.doOnNext(leagues -> {
					log.debug(leagues);
//					navigationView.setSelectedLeagueId(leagues.get(0).getLeagueId());
//					navigationView.setSelectedSeasonId(leagues.get(0).getSeasonId());
				})
				.subscribe(leagues -> navigationView.setLeagues(leagues))			
			;
			
			
			weekIntegrationService.getWeeksForSeason(navigationView.getDefaultSeasonId())
				.filter(weeks -> weeks != null && !weeks.isEmpty())
//				.doOnNext(weeks -> navigationView.setSelectedWeekId())
				.subscribe(weeks -> navigationView.setWeeks(weeks))
			;
			
		return navigationView;		
//		return Observable.just(navigationView);	
	}
}
