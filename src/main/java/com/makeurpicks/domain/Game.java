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
		if ("sd".equals(favId)) return "lac";
		if ("oak".equals(favId)) return "lv";
		if ("stl".equals(favId)) return "lar";
		return favId;
	}
	public void setFavId(String favId) {
		if ("sd".equals(favId)) favId = "lac";
		else if ("oak".equals(favId)) favId = "lv";
		else if ("stl".equals(favId)) favId = "lar";
		this.favId = favId;
	}
	public String getDogId() {
		if ("sd".equals(dogId)) return "lac";
		if ("oak".equals(dogId)) return "lv";
		if ("stl".equals(dogId)) return "lar";
		return dogId;
	}
	public void setDogId(String dogId) {
		if ("sd".equals(dogId)) dogId = "lac";
		else if ("oak".equals(dogId)) dogId = "lv";
		else if ("stl".equals(dogId)) dogId = "lar";
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
		if ("lac".equals(getFavId()) || "sd".equals(favId) || "SD".equalsIgnoreCase(favShortName)) {
			return "Los Angeles Chargers";
		} else if ("lv".equals(getFavId()) || "oak".equals(favId) || "OAK".equalsIgnoreCase(favShortName)) {
			return "Las Vegas Raiders";
		} else if ("lar".equals(getFavId()) || "stl".equals(favId) || "STL".equalsIgnoreCase(favShortName)) {
			return "Los Angeles Rams";
		}
		return favFullName;
	}
	public void setFavFullName(String favFullName) {
		this.favFullName = favFullName;
	}
	public String getDogFullName() {
		if ("lac".equals(getDogId()) || "sd".equals(dogId) || "SD".equalsIgnoreCase(dogShortName)) {
			return "Los Angeles Chargers";
		} else if ("lv".equals(getDogId()) || "oak".equals(dogId) || "OAK".equalsIgnoreCase(dogShortName)) {
			return "Las Vegas Raiders";
		} else if ("lar".equals(getDogId()) || "stl".equals(dogId) || "STL".equalsIgnoreCase(dogShortName)) {
			return "Los Angeles Rams";
		}
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
		if ("lac".equals(getDogId()) || "sd".equals(dogId) || "SD".equalsIgnoreCase(dogShortName)) {
			return "LAC";
		} else if ("lv".equals(getDogId()) || "oak".equals(dogId) || "OAK".equalsIgnoreCase(dogShortName)) {
			return "LV";
		} else if ("lar".equals(getDogId()) || "stl".equals(dogId) || "STL".equalsIgnoreCase(dogShortName)) {
			return "LAR";
		}
		return dogShortName;
	}
	public void setDogShortName(String dogShortName) {
		this.dogShortName = dogShortName;
	}
	public String getFavShortName() {
		if ("lac".equals(getFavId()) || "sd".equals(favId) || "SD".equalsIgnoreCase(favShortName)) {
			return "LAC";
		} else if ("lv".equals(getFavId()) || "oak".equals(favId) || "OAK".equalsIgnoreCase(favShortName)) {
			return "LV";
		} else if ("lar".equals(getFavId()) || "stl".equals(favId) || "STL".equalsIgnoreCase(favShortName)) {
			return "LAR";
		}
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
