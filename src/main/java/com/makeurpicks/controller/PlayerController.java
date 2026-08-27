package com.makeurpicks.controller;

import java.security.Principal;
import java.util.Map;

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

	@RequestMapping(method=RequestMethod.POST, value="/forgot-password")
	public @ResponseBody boolean forgotPassword(@RequestBody Map<String, String> request, javax.servlet.http.HttpServletRequest servletRequest) {
		String identifier = request.get("identifier");
		if (identifier == null || "".equals(identifier.trim())) {
			throw new RuntimeException("Identifier cannot be empty");
		}
		
		String scheme = servletRequest.getScheme();
		String serverName = servletRequest.getServerName();
		int serverPort = servletRequest.getServerPort();
		
		String baseUrl = scheme + "://" + serverName;
		if (serverPort != 80 && serverPort != 443) {
			baseUrl += ":" + serverPort;
		}
		
		return playerService.initiateForgotPassword(identifier, baseUrl);
	}

	@RequestMapping(method=RequestMethod.POST, value="/reset-password-with-token")
	public @ResponseBody boolean resetPasswordWithToken(@RequestBody Map<String, String> request) {
		String token = request.get("token");
		String password = request.get("password");
		if (token == null || "".equals(token.trim())) {
			throw new RuntimeException("Reset token is missing.");
		}
		if (password == null || "".equals(password.trim())) {
			throw new RuntimeException("Password cannot be empty.");
		}
		return playerService.resetPasswordWithToken(token, password);
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
