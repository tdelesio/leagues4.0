package com.makeurpicks.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.makeurpicks.service.game.GameView;
import com.makeurpicks.service.pick.DoublePickView;
import com.makeurpicks.service.pick.PickView;

public class MakePicks implements Serializable {

//	private NavigationView nav;
	private List<GameView> games;
	private Map<String, PickView> picks;
	private DoublePickView doublePick;

	private boolean hystrixFailureOnPicks = false;

//	public NavigationView getNav() {
//		return nav;
//	}
//
//	public void setNav(NavigationView nav) {
//		this.nav = nav;
//	}

	public List<GameView> getGames() {
		return games;
	}

	public void setGames(List<GameView> games) {
		this.games = games;
	}

	public Map<String, PickView> getPicks() {
		return picks;
	}

	public void setPicks(Map<String, PickView> picks) {
		this.picks = picks;
	}

	public DoublePickView getDoublePick() {
		return doublePick;
	}

	public void setDoublePick(DoublePickView doublePick) {
		this.doublePick = doublePick;
	}

	public boolean isHystrixFailureOnPicks() {
		return hystrixFailureOnPicks;
	}

	public void setHystrixFailureOnPicks(boolean hystrixFailureOnPicks) {
		this.hystrixFailureOnPicks = hystrixFailureOnPicks;
	}

	
	
	
}
