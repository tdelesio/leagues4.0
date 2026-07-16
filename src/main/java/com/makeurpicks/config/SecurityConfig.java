package com.makeurpicks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // Disable CSRF for legacy AngularJS API calls
			.cors().and()
			.authorizeRequests()
				// Permit static frontend assets needed for login/registration
				.antMatchers("/login.html", "/register.html", "/rules.html", "/css/**", "/js/**", "/partials/**", "/img/**", "/favicon.ico", "/assets/**", "/jquery-1.7.1.min.js").permitAll()
				// Permit player registration, password initiation, login
				.antMatchers(HttpMethod.POST, "/players/").permitAll()
				.antMatchers(HttpMethod.POST, "/players/login").permitAll()
				.antMatchers(HttpMethod.POST, "/players/password").permitAll()
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
			.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login.html")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
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
