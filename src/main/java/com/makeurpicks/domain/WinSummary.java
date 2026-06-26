package com.makeurpicks.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class WinSummary implements Comparable<WinSummary>, Serializable
{

	private String playerId;
	private Map<Integer, WeekWinner> weekTotal;
	private int numberOfWins;
	private int place;
//	private double moneyWon=0;
	private double entryPrizeWon=0;
	private double weeklyMoneyWon=0;
	private int numberOfPeopleSplitWith=1;
	
	public WinSummary(String player)
	{
		this.playerId = player;
	}
	
	
	
	public double getEntryPrizeWon() {
		return entryPrizeWon;
	}



	public void setEntryPrizeWon(double entryPrizeWon) {
		this.entryPrizeWon = entryPrizeWon;
	}



	public double getWeeklyMoneyWon() {
		return weeklyMoneyWon;
	}



	public void setWeeklyMoneyWon(double weeklyMoneyWon) {
		this.weeklyMoneyWon = weeklyMoneyWon;
	}



	public int getNumberOfPeopleSplitWith() {
		return numberOfPeopleSplitWith;
	}



	public void setNumberOfPeopleSplitWith(int numberOfPeopleSplitWith) {
		this.numberOfPeopleSplitWith = numberOfPeopleSplitWith;
	}



	
	public Map<Integer, WeekWinner> getWeekTotal()
	{
		return weekTotal;
	}
	public void setWeekTotal(Map<Integer, WeekWinner> weekTotal)
	{
		this.weekTotal = weekTotal;
	}
	public int getNumberOfWins()
	{
		return numberOfWins;
	}
	public void setNumberOfWins(int numberOfWins)
	{
		this.numberOfWins = numberOfWins;
	}
	public int compareTo(WinSummary o)
	{
		if (numberOfWins < o.getNumberOfWins())
		{
			return 1;
		}
		else if (numberOfWins > o.getNumberOfWins())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	public int getPlace()
	{
		return place;
	}
	public void setPlace(int place)
	{
		this.place = place;
	}
	
	public double getMoneyWon() {
		return weeklyMoneyWon+entryPrizeWon;
	}
	
	public void addWeekMoney(double moneyToAdd)
	{
		weeklyMoneyWon+=moneyToAdd;
	}
	
	public String getWeeklyMoneyWonDisplay()
	{
		Double currency = new Double(weeklyMoneyWon);
		return NumberFormat.getCurrencyInstance(Locale.US).format(currency);

	}
	
	public String getEntryPrizeWonDisplay()
	{
		Double currency = new Double(entryPrizeWon);
		return NumberFormat.getCurrencyInstance(Locale.US).format(currency);
	}
	public String getTotalMoneyWon()
	{
		Double currency = new Double(getMoneyWon());
		return NumberFormat.getCurrencyInstance(Locale.US).format(currency);
	}



	public String getPlayerId() {
		return playerId;
	}



	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	
}
