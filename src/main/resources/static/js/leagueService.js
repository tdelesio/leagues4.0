(function () {
	var app = angular.module('leagueservice', []);

	app.factory('leagueService', function ($http, $log) {
		$log.debug('leagueService');
			var service =  {
				getLeagues: function() {
					return $http.get('/leagues/').then(function(result) {
				           return result.data;
				       });
				},

				getLeaguesForPlayer: function(username) {
					return $http.get('/leagues/player/' + username).then(function(result) {
						return result.data;
					});
				},
			
				getGames: function(weekId) {
				
					$log.debug("leagueService:getGames weekId="+weekId);
					return $http.get('/games/weekid/'+weekId).then(function(result) {
				           return result.data;
				     });
				},
				
				getMyPicks: function(leagueId, weekId) {
					$log.debug("leagueService:getPicks leagueId="+leagueId+" weekId="+weekId);
					
					return $http.get('/picks/self/leagueid/'+leagueId+'/weekid/'+weekId).then(function(result) {
				           return result.data;
				     });
				},
				
				getDoublePick: function(leagueId, weekId) {
					$log.debug("leagueService:getDoublePick leagueId="+leagueId+" weekId="+weekId);
					return $http.get('/picks/double/leagueid/'+leagueId+'/weekid/'+weekId).then(function(result) {
						return result.data;
					});
				},

				makeDoublePick: function(pickId) {
					$log.debug("leagueService:makeDoublePick pickId="+pickId);
					return $http({
						method: 'PUT',
						url: '/picks/double',
						data: { pickId: pickId }
					}).then(function(result) {
						return result.data;
					});
				}
				
				
			}
			return service;
	});
})();	