package com.makeurpicks.controller;

import java.security.Principal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.DoublePick;
import com.makeurpicks.domain.Pick;
import com.makeurpicks.service.PickService;

@RestController
@RequestMapping(value="/picks")
public class PickController  {

	private Log log = LogFactory.getLog(PickController.class);
	
	@Autowired
	private PickService pickService;
	
	
	@RequestMapping(method=RequestMethod.GET, value="/leagueid/{leagueid}/weekid/{weekid}")
	public @ResponseBody Map<String, Map<String, Pick>> getPicksByWeek(@PathVariable String leagueid, @PathVariable String weekid)
	{
		
		Map<String, Map<String, Pick>> map =pickService.getPicksByWeek(leagueid, weekid);
		log.debug("getPicksByWeek return="+map);
		return map;
	}

	@RequestMapping(method=RequestMethod.GET, value="/self/leagueid/{leagueid}/weekid/{weekid}")
	public @ResponseBody Map<String, Pick> getPicksByWeekAndPlayer(Principal user, @PathVariable String leagueid, @PathVariable String weekid)
	{
		log.debug("getPicksByWeekAndPlayer called: leagueId=" + leagueid + ", weekId=" + weekid + ", playerId=" + user.getName());
		Map<String, Pick> map = pickService.getPicksByWeekAndPlayer(leagueid, weekid, user.getName());
		log.debug("getPicksByWeekAndPlayer return=" + map);
		return map;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/player/leagueid/{leagueid}/weekid/{weekid}/playerid/{playerid}")
	public @ResponseBody Map<String, Pick> getPicksByWeekAndPlayer(Principal user, @PathVariable String leagueid, @PathVariable String weekid, @PathVariable String playerid)
	{
		return pickService.getOtherPicksByWeekAndPlayer(leagueid, weekid, playerid);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody Pick makePick(@RequestBody Pick pick)
	{
//		pick.setPlayerId(user.getName());
		pick.setAdminOverride(true);
		
		log.debug(pick);
		
		return pickService.makePick(pick);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/")
	public @ResponseBody Pick makePick(Principal user, @RequestBody Pick pick)
	{
		pick.setPlayerId(user.getName());
		
		log.debug(pick);
		
		return pickService.makePick(pick);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/")
	public @ResponseBody Pick updatePick(Principal user, @RequestBody Pick pick)
	{
		pick.setPlayerId(user.getName());
		
		log.debug(pick);
		
		return pickService.updatePick(pick);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/double/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public @ResponseBody DoublePick makeDoublePick(@RequestBody DoublePick pick)
	{
		pick.setAdminOverride(true);
		
		log.debug(pick);
		
		return pickService.makeDoublePick(pick.getPickId(), "");
	}
	
	
	
	@RequestMapping(method=RequestMethod.PUT, value="/double")
	public @ResponseBody DoublePick makeDoublePick(Principal user, @RequestBody DoublePick pick)
	{
		log.debug(pick);
		
		return pickService.makeDoublePick(pick.getPickId(), user.getName());
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/double/leagueid/{leagueid}/weekid/{weekid}")
	public @ResponseBody DoublePick getDoublePick(Principal user, @PathVariable String leagueid, @PathVariable String weekid)
	{
		return pickService.getDoublePickForPlayer(leagueid, weekid, user.getName());
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/doubles/leagueid/{leagueid}/weekid/{weekid}")
	public @ResponseBody Map<String, DoublePick> getDoublePick(@PathVariable String leagueid, @PathVariable String weekid)
	{
		return pickService.getDoublePicks(leagueid, weekid);
	}
	

}