package com.makeurpicks.domain;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
public class Game extends AbstractModel{

	
//	"2015-10-25T20:18:47.031Z"
//	"1970-01-01T18:00:00.000Z"
	
	
	//game add attributes
	private double spread = 0.5;
	private String seasonId;
	private String favId;
	private String dogId;
	private String weekId;
	
	private int favScore=0;
	private int dogScore=0;
	private boolean favHome=true;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
//	@DateTimeFormat(pattern="YYYY-MM-DDTHH:mm:ss.sssZ")
	private ZonedDateTime gameStart;
	
	//aggegrated data
	private String favFullName;
	private String dogFullName;
	private String dogShortName;
	private String favShortName;
	
	
	
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public int getFavScore() {
		return favScore;
	}
	public void setFavScore(int favScore) {
		this.favScore = favScore;
	}
	public int getDogScore() {
		return dogScore;
	}
	public void setDogScore(int dogScore) {
		this.dogScore = dogScore;
	}
	public boolean isFavHome() {
		return favHome;
	}
	public void setFavHome(boolean favHome) {
		this.favHome = favHome;
	}
	public String getFavId() {
		return favId;
	}
	public void setFavId(String favId) {
		this.favId = favId;
	}
	public String getDogId() {
		return dogId;
	}
	public void setDogId(String dogId) {
		this.dogId = dogId;
	}

	
	public ZonedDateTime getGameStart() {
		return gameStart;
	}
	public void setGameStart(ZonedDateTime gameStart) {
		this.gameStart = gameStart;
	}
	public String getWeekId() {
		return weekId;
	}
	public void setWeekId(String weekId) {
		this.weekId = weekId;
	}
	public String getFavFullName() {
		return favFullName;
	}
	public void setFavFullName(String favFullName) {
		this.favFullName = favFullName;
	}
	public String getDogFullName() {
		return dogFullName;
	}
	public void setDogFullName(String dogFullName) {
		this.dogFullName = dogFullName;
	}
	public String getSeasonId() {
		return seasonId;
	}
	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}
	
	public String getGameStartFormated()
	{
		if (gameStart == null)
			return "";
		return gameStart.withZoneSameInstant(java.time.ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("EEE MM-dd-yyy hh:mm:ss a"));
	}
	public String getDogShortName() {
		return dogShortName;
	}
	public void setDogShortName(String dogShortName) {
		this.dogShortName = dogShortName;
	}
	public String getFavShortName() {
		return favShortName;
	}
	public void setFavShortName(String favShortName) {
		this.favShortName = favShortName;
	}
	
	public boolean hasScoresEntered()
	{
		if (favScore==0&&dogScore==0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean getHasScoresEntered()
	{
		return hasScoresEntered();
	}
	
	public boolean getHasGameStarted()
	{
		return gameStart.isBefore(ZonedDateTime.now());
	}
	
	public String getGameWinner()
	{
		if (getHasGameStarted() && hasScoresEntered())
		{
			if (dogScore+spread > favScore)
				return dogId;
			else
				return favId;
		}
		else
		{ 
			return null;
		}
	}
}
