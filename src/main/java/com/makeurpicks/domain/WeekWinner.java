package com.makeurpicks.domain;

import java.io.Serializable;

public class WeekWinner implements Serializable
{

	private int wins;
	private boolean isWinner=false;
	private int tiesForWeek=1;
	
	public WeekWinner(int wins)
	{
		this.wins = wins;
	}
	
	
	
	public int getTiesForWeek() {
		return tiesForWeek;
	}



	public void setTiesForWeek(int tiesForWeek) {
		this.tiesForWeek = tiesForWeek;
	}



	public int getWins()
	{
		return wins;
	}
	public void setWins(int wins)
	{
		this.wins = wins;
	}
	public boolean isWinner()
	{
		return isWinner;
	}
	public void setWinner(boolean isWinner)
	{
		this.isWinner = isWinner;
	}
	
	
}
