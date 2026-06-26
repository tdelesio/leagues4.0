package com.makeurpicks.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

public class GameBuilder {

	private String id;
	private double spread = 0.5;
	private int favScore=0;
	private int dogScore=0;
	private boolean favHome=true;
	private String favId;
	private String dogId;
	private ZonedDateTime gameStart;
	private String weekId;
	
	public GameBuilder()
	{
		
		this.id = UUID.randomUUID().toString();
		
		gameStart = ZonedDateTime.now();
	}
	
	public Game build()
	{
		Game game = new Game();
		game.setId(id);
		game.setSpread(spread);
		game.setFavScore(favScore);
		game.setDogScore(dogScore);
		game.setFavHome(favHome);
		game.setFavId(favId);
		game.setDogId(dogId);
		game.setGameStart(gameStart);
		game.setWeekId(weekId);
		return game;
	}
	
	public GameBuilder withSpread(double s)
	{
		this.spread = s;
		return this;
	}
	
	public GameBuilder withFavScore(int f)
	{
		this.favScore = f;
		return this;
	}
	
	public GameBuilder withDogScore(int d)
	{
		this.dogScore = d;
		return this;
	}
	
	public GameBuilder withDogHome()
	{
		this.favHome=false; 
		return this;
	}
	
	public GameBuilder withFavId(String id)
	{
		this.favId = id;
		return this;
	}
	
	public GameBuilder withDogId(String id)
	{
		this.dogId = id;
		return this;
	}
	
	public GameBuilder withGameStartTime(ZonedDateTime gs)
	{
		this.gameStart = gs;
		return this;
	}
	
	public GameBuilder withWeekId(String id)
	{
		this.weekId = id; 
		return this;
	}
	
}
