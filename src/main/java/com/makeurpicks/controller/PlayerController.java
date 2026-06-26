package com.makeurpicks.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.Player;
import com.makeurpicks.service.PlayerService;

@RestController
@RequestMapping(value="/players")
public class PlayerController {

	@Autowired
	private PlayerService playerService;
	
	@RequestMapping(method=RequestMethod.POST, value="/login")
	public @ResponseBody Player login(@RequestBody Player user) {
		return playerService.login(user);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/password")
	public @ResponseBody Player updatePassword(@RequestBody Player user) {
		return playerService.updatePassword(user);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/password")
	public @ResponseBody boolean initiateUpdatePasswordRequest(@RequestBody Player user) {
		playerService.initiateUpdatePasswordRequest(user);
		return true;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public @ResponseBody Player getPlayerById(@PathVariable String id) {
		return playerService.getPlayer(id);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/username/{username}")
	public @ResponseBody Player getPlayerByUserName(@PathVariable String username) {
		return playerService.getPlayerByUserName(username);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/")
	public @ResponseBody Player register(@RequestBody Player player) {
		return playerService.register(player);
	}
	
	@RequestMapping("/userinfo")
	public String userinfo(Principal principal) throws Exception {
		if (principal == null) {
			return null;
		}
		return principal.getName();
	}
}
