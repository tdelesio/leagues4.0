package com.makeurpicks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.makeurpicks.domain.LeagueMessage;
import com.makeurpicks.repository.LeagueMessageRepository;

@Service
public class LeagueMessageService {

	@Autowired
	private LeagueMessageRepository leagueMessageRepository;

	@Transactional(readOnly = true)
	public List<LeagueMessage> getMessagesForLeague(String leagueId) {
		if (leagueId == null || leagueId.trim().isEmpty()) {
			return List.of();
		}
		return leagueMessageRepository.findByLeagueIdOrderByCreatedAsc(leagueId.trim());
	}

	@Transactional
	public LeagueMessage postMessage(String leagueId, String username, String text) {
		if (leagueId == null || leagueId.trim().isEmpty()) {
			throw new IllegalArgumentException("League ID is required.");
		}
		if (text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("Message text cannot be empty.");
		}
		String cleanText = text.trim();
		if (cleanText.length() > 1000) {
			cleanText = cleanText.substring(0, 1000);
		}
		String cleanUser = (username != null && !username.trim().isEmpty()) ? username.trim() : "Anonymous";
		LeagueMessage msg = new LeagueMessage(leagueId.trim(), cleanUser, cleanText);
		return leagueMessageRepository.save(msg);
	}

	@Transactional
	public boolean deleteMessage(String leagueId, String messageId, String username, boolean isAdmin) {
		if (messageId == null || messageId.trim().isEmpty()) {
			return false;
		}
		LeagueMessage msg = leagueMessageRepository.findById(messageId.trim()).orElse(null);
		if (msg == null) {
			return false;
		}
		if (leagueId != null && !leagueId.trim().equalsIgnoreCase(msg.getLeagueId())) {
			return false;
		}
		if (isAdmin || (username != null && username.trim().equalsIgnoreCase(msg.getUsername()))) {
			leagueMessageRepository.delete(msg);
			return true;
		}
		return false;
	}
}
