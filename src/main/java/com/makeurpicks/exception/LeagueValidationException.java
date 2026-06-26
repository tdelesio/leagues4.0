package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LeagueValidationException extends RuntimeException {

	public enum LeagueExceptions {LEAGUE_NAME_IS_NULL, LEAGUE_NAME_IN_USE, SEASON_ID_IS_NULL, ADMIN_NOT_FOUND, 
		PLAYER_NOT_FOUND, LEAGUE_NOT_FOUND, INVALID_LEAGUE_PASSWORD,INVALID_LEAGUE_ID}
	private List<LeagueExceptions> exceptions;
	
	public LeagueValidationException(LeagueExceptions... leagueExceptions)
	{
		exceptions = new ArrayList<LeagueExceptions>(Arrays.asList(leagueExceptions));
	}
	
	public boolean hasException()
	{
		if (exceptions.isEmpty())
			return false;
		else
			return true;
	}

	@Override
	public String getMessage() {
		return exceptions.toString();
	}
	
	
}
