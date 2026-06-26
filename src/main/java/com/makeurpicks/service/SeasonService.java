package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.LeagueType;
import com.makeurpicks.domain.Season;
import com.makeurpicks.repository.SeasonRepository;

@Component
public class SeasonService {
	
	@Autowired 
	private SeasonRepository seasonRepository;
	
	
	public List<Season> getCurrentSeasons()
	{
		List<Season> s = new ArrayList<Season>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		
		for (LeagueType lt : LeagueType.values())
		{
			Iterable<Season> seasons = seasonRepository.getSeasonsByLeagueType(lt.toString());
			for (Season season:seasons)
			{
				if (season.getStartYear() >= currentYear)
					s.add(season);
			}
		}
		
		return s;
	}
	
	public Season createSeason(Season season)
	{
	
		String id = UUID.randomUUID().toString();
		
		season.setId(id);
		
		return seasonRepository.save(season);
	}
	
	public Season updateSeason(Season season)
	{
		return seasonRepository.save(season);
	}
	
	public void deleteSeason(String seasonId)
	{
		seasonRepository.deleteById(seasonId);
	}
}
