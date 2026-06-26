package com.makeurpicks.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.makeurpicks.domain.League;
import com.makeurpicks.domain.LeagueName;

public class HelperUtils {
	
	public static Set<LeagueName> getLeagueNameFromLeagues(Collection<League> leagues) {
		Set<LeagueName> leagueNames = new HashSet<LeagueName>();
		leagues.forEach(e-> {
			leagueNames.add(new LeagueName(e));
		});
		return leagueNames;
	}
}
