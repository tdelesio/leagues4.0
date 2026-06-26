package com.makeurpicks.domain;

import java.util.List;

public class NFLWeek extends AbstractModel {

	private int w;
	private int y;
	
	private List<NFLGame> gms;
	
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public List<NFLGame> getGms() {
		return gms;
	}
	public void setGms(List<NFLGame> gms) {
		this.gms = gms;
	}
	
	
}
