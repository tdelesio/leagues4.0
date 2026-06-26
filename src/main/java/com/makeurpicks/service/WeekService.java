package com.makeurpicks.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.Week;
import com.makeurpicks.service.LeagueService;
import com.makeurpicks.repository.WeekRepository;
import com.makeurpicks.repository.SeasonRepository;

@Component
public class WeekService {

	private Log log = LogFactory.getLog(WeekService.class);
	
	@Autowired
	private WeekRepository weekRepository;
	
	@Autowired
	private LeagueService leagueService;

	@Autowired
	private SeasonRepository seasonRepository;

	private Week populateSeasonDetails(Week week) {
		if (week != null && week.getSeasonId() != null && !week.getSeasonId().isEmpty()) {
			com.makeurpicks.domain.Season season = seasonRepository.findById(week.getSeasonId()).orElse(null);
			if (season != null) {
				week.setLeagueType(season.getLeagueType());
				week.setStartYear(season.getStartYear());
				week.setEndYear(season.getEndYear());
			}
		}
		return week;
	}
	
	public List<Week> getWeeksBySeason(String seasonId)
	{
		List<Week> weeks = weekRepository.findBySeasonId(seasonId);
		for (Week week : weeks) {
			populateSeasonDetails(week);
		}
		Collections.sort(weeks, (w1, w2) -> Integer.compare(w2.getWeekNumber(), w1.getWeekNumber()));
		
		return weeks;
	}

	public List<Week> getWeeksByLeague(String leaugeId)
	{
		log.debug("leagueId="+leaugeId);
		com.makeurpicks.domain.League league = leagueService.getLeagueById(leaugeId);
		return getWeeksBySeason(league.getSeasonId());
	}
	
	public Week createWeek(Week week)
	{
		
		String id = UUID.randomUUID().toString();
		
		week.setId(id);
		return weekRepository.save(week);
	}

	public void deleteWeek(String id)
	{
		weekRepository.deleteById(id);
	}
	
}
