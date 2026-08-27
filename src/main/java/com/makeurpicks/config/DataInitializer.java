package com.makeurpicks.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.Player.MemberLevel;
import com.makeurpicks.domain.Player.PlayerStatus;
import com.makeurpicks.repository.PlayerRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private static final Log log = LogFactory.getLog(DataInitializer.class);

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		String adminUsername = "tdelesio";
		Player player = playerRepository.findByUsername(adminUsername);
		if (player == null) {
			log.info("Creating default admin player: " + adminUsername);
			player = new Player();
			player.setUsername(adminUsername);
			player.setPassword(passwordEncoder.encode("rage311"));
			player.setEmail("tdelesio@gmail.com");
			player.setAccountLevel("admin");
			player.setMemberLevel(MemberLevel.ADMIN);
			player.setStatus(PlayerStatus.ACTIVE);
			player.setEnabled(true);
			player.setAccountNonLocked(true);
			playerRepository.save(player);
			log.info("Default admin player created successfully.");
		} else {
			log.info("Default admin player already exists. Verifying details...");
			boolean updated = false;
			
			// Always enforce the requested email
			if (!"tdelesio@gmail.com".equalsIgnoreCase(player.getEmail())) {
				player.setEmail("tdelesio@gmail.com");
				updated = true;
			}
			
			if (!"admin".equals(player.getAccountLevel())) {
				player.setAccountLevel("admin");
				updated = true;
			}
			if (player.getMemberLevel() != MemberLevel.ADMIN) {
				player.setMemberLevel(MemberLevel.ADMIN);
				updated = true;
			}
			if (player.getStatus() != PlayerStatus.ACTIVE) {
				player.setStatus(PlayerStatus.ACTIVE);
				updated = true;
			}
			if (!player.isEnabled()) {
				player.setEnabled(true);
				updated = true;
			}
			
			if (updated) {
				playerRepository.save(player);
				log.info("Default admin player details synchronized and verified.");
			} else {
				log.info("Default admin player details are fully synchronized.");
			}
		}
	}
}
