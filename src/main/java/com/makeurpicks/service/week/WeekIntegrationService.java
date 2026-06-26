package com.makeurpicks.service.week;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.makeurpicks.domain.Week;
import com.makeurpicks.service.WeekService;

import rx.Observable;

@Service
public class WeekIntegrationService {

	@Autowired
	private WeekService weekService;

	public Observable<List<WeekView>> getWeeksForSeason(String seasonId) {
		List<Week> weeks = weekService.getWeeksBySeason(seasonId);
		List<WeekView> views = new ArrayList<>();
		for (Week week : weeks) {
			WeekView view = new WeekView();
			view.setId(week.getId());
			view.setWeekNumber(week.getWeekNumber());
			views.add(view);
		}
		return Observable.just(views);
	}
}
