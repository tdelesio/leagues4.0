package com.makeurpicks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.WinSummary;
import com.makeurpicks.service.LeaderService;

@RestController
@RequestMapping(value="/leaders")
public class LeaderController {

	@Autowired 
	private LeaderService leaderService;
	
	@RequestMapping(value="/winsummary/leagueid/{leagueid}")
	public @ResponseBody List<WinSummary> getWinSummaryByLeague(@PathVariable String leagueid)
	{
		return leaderService.getWinSummary(leagueid);
	}
	
	@RequestMapping(value="/winnersbyweek/leagueid/{leagueid}")
	public @ResponseBody Map<Integer, String> getWeekWinnersByLeague(@PathVariable String leagueid)
	{
		return leaderService.getWeekWinners(leagueid);
	}
}
