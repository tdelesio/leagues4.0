package com.makeurpicks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.Player.MemberLevel;
import com.makeurpicks.exception.PlayerValidationException;
import com.makeurpicks.exception.PlayerValidationException.PlayerExceptions;
import com.makeurpicks.repository.PlayerRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class PlayerService implements UserDetailsService {

	@Autowired
	private PlayerRepository playerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String trimmedUsername = username != null ? username.trim() : "";
		Player player = playerRepository.findByUsername(trimmedUsername);
		if (player == null) {
			player = playerRepository.findByEmail(trimmedUsername);
		}
		if (player == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}
		
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		if ("admin".equals(player.getAccountLevel())) {
			auth = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
		}		
		player.setAuthorities(auth);
		return player;
	}

	public Player getPlayer(String id) {
		return playerRepository.findById(id).orElse(null);
	}

	public Player getPlayerByUserName(String username) {
		return playerRepository.findByUsername(username);
	}

	public Player login(Player user) {
		String trimmedUsername = user.getUsername() != null ? user.getUsername().trim() : "";
		Player player = playerRepository.findByUsername(trimmedUsername);
		if (player == null) {
			player = playerRepository.findByEmail(trimmedUsername);
		}
		if (player == null) {
			throw new PlayerValidationException(PlayerExceptions.USER_NOT_FOUND);
		}
		if (!passwordEncoder.matches(user.getPassword(), player.getPassword())) {
			throw new PlayerValidationException(PlayerExceptions.PASSWORD_DOES_NOT_MEET_REQ);
		}
		
		// Establish the spring security context for session tracking
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		if ("admin".equals(player.getAccountLevel())) {
			auth = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
		}		
		player.setAuthorities(auth);
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(player, null, auth);
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		return player;
	}

	public Player updatePassword(Player user) {
		validatePassword(user.getPassword());
		Player player = playerRepository.findByUsername(user.getUsername());
		if (player == null) {
			throw new PlayerValidationException(PlayerExceptions.PLAYER_IS_NULL);
		}
		player.setPassword(passwordEncoder.encode(user.getPassword()));
		playerRepository.save(player);
		return user;
	}

	public boolean initiateUpdatePasswordRequest(Player user) {
		return true;
	}

	public Player createPlayer(Player player) {
		validatePlayerForRegistration(player);
		player.setPassword(passwordEncoder.encode(player.getPassword()));
		player.setMemberLevel(MemberLevel.USER);
		playerRepository.save(player);
		return player;
	}

	public Player register(Player player) {
		return createPlayer(player);
	}

	private void validatePassword(String password) {
		if (password == null || "".equals(password)) {
			throw new PlayerValidationException(PlayerExceptions.PASSWORD_IS_NULL);
		}
	}

	private void validatePlayerForRegistration(Player player) {
		List<PlayerExceptions> codes = new ArrayList<>();
		
		if (player == null) {
			throw new PlayerValidationException(PlayerExceptions.PLAYER_IS_NULL);
		}
		
		if (player.getUsername() == null || "".equals(player.getUsername())) {
			codes.add(PlayerExceptions.USERNAME_IS_NULL);
		} else {
			Player nameCheck = playerRepository.findByUsername(player.getUsername());
			if (nameCheck != null) {
				codes.add(PlayerExceptions.USERNAME_TAKE);
			}
		}

		if (player.getEmail() == null || "".equals(player.getEmail())) {
			codes.add(PlayerExceptions.EMAIL_IS_NULL);
		}
		
		if (player.getPassword() == null || "".equals(player.getPassword())) {
			codes.add(PlayerExceptions.PASSWORD_IS_NULL);
		}
		
		if (!codes.isEmpty()) {
			throw new PlayerValidationException(codes.toArray(new PlayerExceptions[0]));
		}
	}
}
