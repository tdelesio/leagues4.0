package com.makeurpicks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.Week;
import com.makeurpicks.service.WeekService;

@RestController
@RequestMapping(value="/weeks")
public class WeekController  {

	@Autowired
	private WeekService weekService;

	@RequestMapping(method=RequestMethod.GET, value="/seasonid/{id}")
	public @ResponseBody Iterable<Week> getWeeksBySeason(@PathVariable String id)
	{
		return weekService.getWeeksBySeason(id);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/leagueid/{id}")
	public @ResponseBody Iterable<Week> getWeeksByLeague(@PathVariable String id)
	{
		return weekService.getWeeksByLeague(id);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/")
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody Week createWeek(@RequestBody Week week)
	{
		return weekService.createWeek(week);
	}
	
}
