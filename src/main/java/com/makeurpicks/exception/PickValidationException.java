package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.makeurpicks.service.PickService;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PickValidationException extends RuntimeException {

	private Log log = LogFactory.getLog(PickValidationException.class);
	
	public enum PickExceptions {PICK_IS_NULL, GAME_IS_NULL, TEAM_IS_NULL, WEEK_IS_NULL, LEAGUE_IS_NULL, 
		PLAYER_IS_NUll, TEAM_NOT_PLAYING_IN_GAME, GAME_HAS_ALREADY_STARTED, WEEK_IS_NOT_VALID, PLAYER_NOT_IN_LEAGUE,
		UNAUTHORIZED_USER, GAME_SERVICE_IS_DOWN, LEAGUE_SERVICE_IS_DOWN}
	
	private Iterable<PickExceptions> exceptions;
	
	public PickValidationException(PickExceptions... pickExceptions)
	{
		log.debug(pickExceptions);
		exceptions = new ArrayList<PickExceptions>(Arrays.asList(pickExceptions));
	}
	
	public boolean hasSpecificException(PickExceptions exception)
	{
		Iterator<PickExceptions> iter = exceptions.iterator();
		while(iter.hasNext())
		{
			if (iter.next().equals(exception))
				return true;
		}
		return false;
	}
	
	public boolean hasException()
	{
		if (exceptions.iterator().hasNext())
			return false;
		else
			return true;
	}

	@Override
	public String getMessage() {
		return exceptions.toString();
	}
}
