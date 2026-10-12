package com.makeurpicks.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.makeurpicks.domain.LeagueMessage;
import com.makeurpicks.service.LeagueMessageService;

@RestController
@RequestMapping(value = "/leagues/{leagueId}/messages")
public class LeagueMessageController {

	@Autowired
	private LeagueMessageService leagueMessageService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<LeagueMessage> getMessages(@PathVariable String leagueId) {
		return leagueMessageService.getMessagesForLeague(leagueId);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> postMessage(
			@PathVariable String leagueId,
			Principal principal,
			@RequestBody Map<String, String> payload) {
		String text = payload != null ? payload.get("message") : null;
		if (text == null || text.trim().isEmpty()) {
			text = payload != null ? payload.get("text") : null;
		}
		if (text == null || text.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Message content cannot be empty.");
		}
		String username = principal != null ? principal.getName() : "Anonymous";
		LeagueMessage created = leagueMessageService.postMessage(leagueId, username, text);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{messageId}")
	public ResponseEntity<?> deleteMessage(
			@PathVariable String leagueId,
			@PathVariable String messageId,
			Principal principal) {
		String username = principal != null ? principal.getName() : "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth != null && auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()));

		boolean deleted = leagueMessageService.deleteMessage(leagueId, messageId, username, isAdmin);
		if (deleted) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to delete message.");
		}
	}
}
