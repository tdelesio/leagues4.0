(function () {
	var app = angular.module('makepicks', ['leagueservice']);
	
	app.controller('MakePicksController', function ($scope, $http, $window, $log, leagueService) { 
		
		$scope.pickMap = {};
		$scope.gameMetaMap = {};
		$scope.showGS = true;
		
		var loadGamesAndPicks = function() {
			if (!$scope.week || !$scope.week.weekId) {
				return;
			}
			$log.debug('MakePickController: loadGamesAndPicks weekId=' + $scope.week.weekId);
			
			//load all the games
			leagueService.getGames($scope.week.weekId).then(function(games) {
				$log.debug('MakePickController:games=' + JSON.stringify(games));
				$scope.games = games;
			});
			
			//load all the picks
			var leagueId = ($scope.league && $scope.league.id) ? $scope.league.id : ($scope.leagues && $scope.leagues.length > 0 ? $scope.leagues[0].id : null);
			leagueService.getMyPicks(leagueId, $scope.week.weekId).then(function(picks) {
				$log.debug('MakePickController:picks=' + JSON.stringify(picks));
				$scope.pickMap = picks;
				
				// Fetch the active double pick once the picks map has loaded
				leagueService.getDoublePick(leagueId, $scope.week.weekId).then(function(doublePick) {
					$log.debug('MakePickController:doublePick=' + JSON.stringify(doublePick));
					$scope.doublePick = doublePick;
				});
			});
		};
		
		$scope.toggleDoublePick = function(gameId, event) {
			if (event) {
				event.stopPropagation();
			}
			$log.debug("MakePicksController:toggleDoublePick gameId=" + gameId);
			
			var activePick = $scope.pickMap[gameId];
			if (!activePick) {
				alert("Please make a pick for this game first before doubling it!");
				return;
			}
			
			leagueService.makeDoublePick(activePick.id).then(function(savedDoublePick) {
				$scope.doublePick = savedDoublePick;
			}, function(error) {
				$log.error('makeDoublePick failed:', error);
				var msg = (error && error.data && error.data.message) ? error.data.message : 'Double pick update failed.';
				alert(msg);
			});
		};
		
		// Load immediately if week is already loaded (e.g., returning from another tab/state)
		if ($scope.week && $scope.week.weekId) {
			loadGamesAndPicks();
		}
		
		$scope.$on('weekLoaded', function (event) {
			$log.debug(event);
			loadGamesAndPicks();
		});
		
		$scope.makePick = function(gameid, teamid, pickid) {
			$log.debug("MakePicksController:makePick gameId="+gameid+" teamid="+teamid+" pickId="+pickid);
			
			// Find game to check if started
			var game = null;
			if ($scope.games) {
				for (var i = 0; i < $scope.games.length; i++) {
					if ($scope.games[i].id === gameid) {
						game = $scope.games[i];
						break;
					}
				}
			}
			
			if (game && game.hasGameStarted) {
				$log.debug("Block change: game started");
				return; // Block client-side changes once game starts
			}
			
			var local_model = {};
			local_model.teamId = teamid;
			local_model.gameId = gameid; // Corrected camelCase
			local_model.weekId = $scope.week.weekId;
			local_model.leagueId = ($scope.league && $scope.league.id) ? $scope.league.id : ($scope.leagues && $scope.leagues.length > 0 ? $scope.leagues[0].id : null);
			
			var method;
			if (pickid == undefined || pickid == null) {
				method = "POST";
			} else {
				method = "PUT";
				local_model.id = pickid;
			}
			
			$log.debug("MakePicksController:makePick submittedModel="+JSON.stringify(local_model));
			$http({
				method : method,
				url : '/picks/',
				data : local_model
			}).then(function(response) { 
				$scope.pickMap[gameid] = response.data;
			}, function(error) {
				$log.error('makePick failed:', error);
				alert('fail');
			});
		};	

		$scope.getRemainingPicksCount = function() {
			if (!$scope.games) {
				return 0;
			}
			var count = 0;
			for (var i = 0; i < $scope.games.length; i++) {
				var gameId = $scope.games[i].id;
				if (!$scope.pickMap || !$scope.pickMap[gameId] || !$scope.pickMap[gameId].teamId) {
					count++;
				}
			}
			return count;
		};

		$scope.isDoublePickSelected = function() {
			return !!($scope.doublePick && $scope.doublePick.gameId);
		};
	});

	app.controller('ViewPicksController', function ($scope, $http, $log) {
		$scope.viewPicksGrid = [];
		$scope.loading = false;

		$scope.loadViewPicks = function() {
			if (!$scope.leagues || !$scope.week || !$scope.week.weekId) {
				return;
			}
			
			var activeLeague = null;
			if ($scope.leagues) {
				for (var i = 0; i < $scope.leagues.length; i++) {
					if ($scope.leagues[i].seasonId === $scope.league.seasonId) {
						activeLeague = $scope.leagues[i];
						break;
					}
				}
			}
			
			var leagueId = activeLeague ? activeLeague.id : $scope.leagues[0].id;
			var weekId = $scope.week.weekId;

			$scope.loading = true;
			$http.get('/viewpicks/leagueid/' + leagueId + '/weekid/' + weekId)
				.success(function(data) {
					$scope.viewPicksGrid = data;
					$scope.loading = false;
				})
				.error(function(err) {
					$log.error('Error loading view picks:', err);
					$scope.loading = false;
				});
		};

		$scope.$on('weekLoaded', function() {
			$scope.loadViewPicks();
		});

		if ($scope.leagues && $scope.week && $scope.week.weekId) {
			$scope.loadViewPicks();
		}
	});

	app.controller('WinsummaryController', function ($scope, $http, $log) {
		$scope.standings = [];
		$scope.weekWinners = [];
		$scope.loading = false;

		$scope.loadStandings = function() {
			if (!$scope.leagues || !$scope.week || !$scope.week.weekId) {
				return;
			}
			
			var activeLeague = null;
			if ($scope.leagues) {
				for (var i = 0; i < $scope.leagues.length; i++) {
					if ($scope.leagues[i].seasonId === $scope.league.seasonId) {
						activeLeague = $scope.leagues[i];
						break;
					}
				}
			}
			
			var leagueId = activeLeague ? activeLeague.id : $scope.leagues[0].id;

			$scope.loading = true;
			$http.get('/leaders/winsummary/leagueid/' + leagueId)
				.success(function(data) {
					$scope.standings = data;
					$scope.loading = false;
				})
				.error(function(err) {
					$log.error('Error loading standings:', err);
					$scope.loading = false;
				});

			$http.get('/leaders/winnersbyweek/leagueid/' + leagueId)
				.success(function(data) {
					var list = [];
					angular.forEach(data, function(value, key) {
						list.push({ weekNumber: key, winners: value });
					});
					list.sort(function(a, b) {
						return parseInt(b.weekNumber) - parseInt(a.weekNumber);
					});
					$scope.weekWinners = list;
				})
				.error(function(err) {
					$log.error('Error loading week winners:', err);
				});
		};

		$scope.$on('weekLoaded', function() {
			$scope.loadStandings();
		});

		if ($scope.leagues && $scope.week && $scope.week.weekId) {
			$scope.loadStandings();
		}
	});

})();	