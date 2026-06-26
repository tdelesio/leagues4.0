package com.makeurpicks.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlayerValidationException extends RuntimeException {

	private Log log = LogFactory.getLog(PlayerValidationException.class);
	
	public enum PlayerExceptions {
		USER_NOT_FOUND,
		PASSWORD_DOES_NOT_MEET_REQ,
		PLAYER_IS_NULL,
		PASSWORD_IS_NULL,
		USERNAME_IS_NULL,
		USERNAME_TAKE,
		EMAIL_IS_NULL
	}
	
	private Iterable<PlayerExceptions> exceptions;
	
	public PlayerValidationException(PlayerExceptions... playerExceptions)
	{
		log.debug(playerExceptions);
		exceptions = new ArrayList<PlayerExceptions>(Arrays.asList(playerExceptions));
	}
	
	public boolean hasSpecificException(PlayerExceptions exception)
	{
		Iterator<PlayerExceptions> iter = exceptions.iterator();
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
