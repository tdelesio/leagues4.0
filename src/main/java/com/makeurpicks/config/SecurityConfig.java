package com.makeurpicks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.makeurpicks.service.PlayerService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final String REMEMBER_ME_KEY = "makeurpicks-secure-remember-me-key-2026";
	public static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 30 * 24 * 60 * 60; // 30 days

	@Bean
	public RememberMeServices rememberMeServices(@Lazy PlayerService playerService) {
		TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(
				REMEMBER_ME_KEY, playerService);
		rememberMeServices.setAlwaysRemember(true);
		rememberMeServices.setTokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS);
		rememberMeServices.setCookieName("remember-me");
		return rememberMeServices;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {
		http
			.csrf().disable() // Disable CSRF for legacy AngularJS API calls
			.cors().and()
			.authorizeRequests()
				// Permit static frontend assets needed for login/registration
				.antMatchers("/login.html", "/register.html", "/rules.html", "/css/**", "/js/**", "/partials/**", "/img/**", "/favicon.ico", "/assets/**", "/jquery-1.7.1.min.js").permitAll()
				// Permit player registration, password initiation, login, userinfo check, and forgot/reset flows
				.antMatchers(HttpMethod.POST, "/players/").permitAll()
				.antMatchers(HttpMethod.POST, "/players/login").permitAll()
				.antMatchers(HttpMethod.GET, "/players/userinfo").permitAll()
				.antMatchers(HttpMethod.POST, "/players/password").permitAll()
				.antMatchers(HttpMethod.POST, "/players/forgot-password").permitAll()
				.antMatchers(HttpMethod.POST, "/players/reset-password-with-token").permitAll()
				// Protect admin dashboard
				.antMatchers("/admin", "/admin/**").hasRole("ADMIN")
				// All other requests require authentication
				.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login.html")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/index.html", true)
				.permitAll()
			.and()
			.rememberMe()
				.rememberMeServices(rememberMeServices)
				.key(REMEMBER_ME_KEY)
			.and()
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login.html")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID", "remember-me")
				.permitAll();
		
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
