//http://www.nfl.com/liveupdate/scorestrip/ss.xml
(function () {
	var app = angular.module('admin', ['ui.router']);
	
	app.config(function($stateProvider, $urlRouterProvider) {
		  //
		  // For any unmatched url, redirect to /state1
		  $urlRouterProvider.otherwise("/setup");
		  //
		  // Now set up the states
		  $stateProvider
		    .state('setup', {
		      url: "/setup",
		      templateUrl: "setupWeek.html"
		    })
		    .state('create', {
		      url: "/create",
		      templateUrl: "createWeek.html"
		    })
		    .state('leagues', {
		      url: "/leagues",
		      templateUrl: "createLeague.html"
		    })
		   .state('players', {
		      url: "/players",
		      templateUrl: "players.html"
		    })
		   .state('seasons', {
		      url: "/seasons",
		      templateUrl: "createSeasons.html"
		    })
		   .state('games', {
		      url: "/games",
		      templateUrl: "games.html"
		    })
		   .state('addScore', {
		      url: "/add_score",
		      templateUrl: "addScore.html"
		    })
		   .state('backup', {
		      url: "/backup",
		      templateUrl: "backup.html"
		    });
		});
	
	app.directive('chrome', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/chrome.html'
		};
	});

	app.directive('backupRestore', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/backup.html'
		};
	});

	
	app.directive('createWeek', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/createWeek.html'
		};
	});
	
	app.directive('createGame', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/createGame.html'
		};
	});
	
	app.directive('createLeague', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/createLeague.html'
		};
	});
	
	app.directive('createSeason', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/createSeason.html'
		};
	});
	
	app.directive('games', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/games.html'
		};
	});
	
	app.directive('leagues', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/leagues.html'
		};
	});
	
	app.directive('seasons', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/seasons.html'
		};
	});
	
	app.directive('weeks', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/weeks.html'
		};
	});
	
	app.directive('editGame', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/editGame.html'
		};
	});
	
	app.directive('playersInLeague', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/playersInLeague.html'
		};
	});
	
	app.directive('playersList', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/playersList.html'
		};
	});
	
	
	app.factory('leagueService', function ($http, $log) {
	$log.debug('leagueService');
		var service =  {
			getLeagues: function() {
				return $http.get('/admin/leagues/').then(function(result) {
			           return result.data;
			       });
			},
		
			getGames: function(seasonId) {
			return $http.get('/admin/games/weekid/'+seasonId).success(function(result) {
		           return result.data;
		       });
			}
		}
		return service;
	});
	
	app.controller('ChromeController', function ($http, $scope) {
		$http.get('/admin/user').success(function(data) {
			$scope.user = data.name;
		});
		
		$scope.logout = function () {
//			   console.log("I am here"+JSON.stringify($location));
			   $http.post('/admin/logout', {}).success(function() {
				   				console.log("logout sucess...");
			        }).error(function(data) {
			          console.log("Logout failed");
			          
			        });
			   
			    }
	});

	app.controller('PlayersController', function ($scope, $http, $window, $log) {
		$scope.players = [];
		$scope.loading = true;
		$scope.currentUser = '';

		$http.get('/admin/user').success(function(data) {
			$scope.currentUser = data.name;
		});

		$scope.loadPlayers = function() {
			$scope.loading = true;
			$http.get('/admin/players/').success(function(data) {
				$scope.players = data;
				$scope.loading = false;
			}).error(function(err) {
				$log.error('Error loading players:', err);
				$scope.loading = false;
			});
		};

		$scope.deletePlayer = function(player) {
			if (player.username === $scope.currentUser) {
				$window.alert('You cannot delete yourself!');
				return;
			}
			if ($window.confirm('Are you sure you want to permanently delete player ' + player.username + '? This will cascade and purge all of their picks and league records.')) {
				$http.delete('/admin/players/' + encodeURIComponent(player.username)).success(function() {
					var idx = $scope.players.indexOf(player);
					if (idx > -1) {
						$scope.players.splice(idx, 1);
					}
				}).error(function(err) {
					$window.alert('Error deleting player: ' + (err.message || 'unknown error'));
					$log.error('Error deleting player:', err);
				});
			}
		};

		$scope.loadPlayers();
	});
	
	//***************  Season  **************************
	app.controller('CreateSeasonController', function ($scope, $http, $window, $log, leagueService) {
		
		$scope.showseasons=true;
		$scope.season={};
		$scope.season.leagueType = "pickem";
		$scope.season.leagueTypes="pickem";
		$scope.season.startYear = 2017;
		$scope.season.endYear = 2018;
		
		$http.get('/admin/leagues/seasons/current').success(function(data) {
			$scope.seasons = data;
			if (data[0] === undefined)
				$scope.showseasons=false;
		});
		
		$http.get('/admin/leagues/types').success(function(data) {
			if (data[0] )
				$scope.season.leagueTypes=data;
		});
		
		this.deleteSeason = function(season) {
			$log.debug("CreateSeasonController:deleteSeason: season.id="+season.id);
			
			if (confirm("Are yoou sure you want to delete season?")) {
		    
				var url = '/admin/leagues/seasons/'+season.id;
				$http({
					method : "DELETE",
					url : url,
					contentType : "application/json",
					dataType : "json",
				}).success(function(res) { 
					
					$http.get('/admin/leagues/seasons/current').success(function(data) {
						$scope.seasons = data;
						if (data[0] === undefined)
							$scope.showseasons=false;
					});
					
				}).error(function(res) {
					alert('fail');
				});
			
		 
		    }
		}
		
		this.addSeason = function() {

		
			$log.debug("CreateSeasonController:addSeason");
			
			$http({
				method : "POST",
				url : '/admin/leagues/seasons/',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify($scope.season)
			}).success(function(res) { 
				
//				$scope.showgames = true;
//				$http.get('/admin/leagues/seasons/current').success(function(data) {
//					$scope.seasons = data;
//				});
				$window.location.href = '/admin/#/leagues';
				
			}).error(function(res) {
				alert('fail');
			});
		}
	});
	
	//***************  LEAGUES  **************************
	app.controller('CreateLeagueController', function ($scope, $http, $window, $log, leagueService) {
	
		$scope.league = {};
		$scope.season = {};
		$scope.showgames=true;
		$scope.hideplayers=false;
		
		leagueService.getLeagues().then(function(data) {
			$scope.leagues = data;
		});
		
		$http.get('/admin/leagues/seasons/current').success(function(data) {
			$scope.seasons = data;
			if (data[0] === undefined)
				$scope.showgames=false;
			else 
			 $scope.league.seasonId = data[0].id;
		});
		
		$scope.season.startYear = 2016;
		$scope.season.endYear = 2017;
		$scope.season.leagueType = "pickem";
		
		
		this.addLeague = function() {

			$log.debug("CreateLeagueController:addLeague");
			
			$http({
				method : "POST",
				url : '/admin/leagues/',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify($scope.league)
			}).success(function(res) { 
				
				leagueService.getLeagues().then(function(data) {
					$scope.leagues = data;
				});
				
			}).error(function(res) {
				alert('fail');
			});
		}
		
		$scope.showPlayers = function(leagueId) {
			
			$log.debug("CreateLeagueController:showPlayers: leagueId="+leagueId);
			$scope.hideplayers=true;
			
			$http.get('/admin/leagues/player/leagueid/'+leagueId).success(function(data) {
				$scope.players = data;
				
			});
		}
		
	
		
		$scope.deleteLeague = function(leagueId) {
			$log.debug("CreateLeagueController:deleteLeague: leagueId="+leagueId);
			$scope.hideplayers=true;
			
			if (confirm("Are you sure you want to delete league?")) {
		    
				var url = '/admin/leagues/'+leagueId;
				$http({
					method : "DELETE",
					url : url,
					contentType : "application/json",
					dataType : "json",
				}).success(function(res) { 
					
					leagueService.getLeagues().then(function(data) {
						$scope.leagues = data;
					});
					
				}).error(function(res) {
					alert('fail');
				});
			
		 
		    }
		}
		
		$scope.dummyLeague = function () {
			$http({
				method : "POST",
				url : '/admin/dummy',
				contentType : "application/json",
				dataType : "json"
//					,
//				data : JSON.stringify($scope.league)
			}).success(function(res) { 
				
				leagueService.getLeagues().then(function(data) {
					$scope.leagues = data;
				});
				
			}).error(function(res) {
				alert('fail');
			});
		}
		
		$scope.randomLeague = function () {
			$http({
				method : "POST",
				url : '/admin/random',
				contentType : "application/json",
				dataType : "json"
//					,
//				data : JSON.stringify($scope.league)
			}).success(function(res) { 
				
				leagueService.getLeagues().then(function(data) {
					$scope.leagues = data;
				});
				
			}).error(function(res) {
				alert('fail');
			});
		}
		
		
			
	});
	
	//***************  WEEKS  *************************
	app.controller('CreateWeekController', function ($scope, $http, $window, $log, leagueService) {
		$scope.week = {};
		$scope.weeks = {};
//		leagueService.getLeagues().then(function(data) {
//			$scope.leagues = data;
//			$scope.week.seasonId = data[0].seasonId;
//		});
		
		$http.get('/admin/leagues/seasons/current').success(function(data) {
			$scope.seasons = data;
			$scope.week.seasonId = data[0].id;
			
			var url = '/admin/weeks/seasonid/'+data[0].id;
			$http.get(url).success(function(data) {
				$scope.weeks = data;
			});
		});
		
		
		
		this.autoWeek = function(week) {
			
			if (week === undefined) {
				week = {};
				week.seasonId = $scope.week.seasonId;
			}
			
			$log.debug('autoWeek: week='+JSON.stringify(week));
			
			$http({
				method : "POST",
//				method : "GET",
//				beforeSend: function (request) {
//			        request.setRequestHeader(header, token);
//			     },
				url : '/admin/games/autosetup',
//				url : '/games/role',
				contentType : "application/json",
				dataType : "json",
				//data : $('form').serializeObject(),
				data : JSON.stringify(week)
			}).success(function(res) { 
				$window.location.href = 'index.html';
			}).error(function(res) {
				alert('fail');
			});
		}
		
		this.addWeek = function(week) {

			$http({
				method : "POST",
//				beforeSend: function (request) {
//			        request.setRequestHeader(header, token);
//			     },
				url : '/admin/weeks/',
				contentType : "application/json",
				dataType : "json",
				//data : $('form').serializeObject(),
				data : JSON.stringify($scope.week)
			}).success(function(res) { 
				
				$scope.createWeek.$setPristine();
				$scope.week = {};
				$window.location.href = 'index.html';
			}).error(function(res) {
				alert('fail');
			});
		}

		this.deleteWeek = function(week) {
			$log.debug("CreateWeekController:deleteWeek: week.id="+week.id);
			
			if (confirm("Are you sure you want to delete week?")) {
				var url = '/admin/weeks/' + week.id;
				$http({
					method : "DELETE",
					url : url,
					contentType : "application/json",
					dataType : "json",
				}).success(function(res) { 
					var urlWeeks = '/admin/weeks/seasonid/' + week.seasonId;
					$http.get(urlWeeks).success(function(data) {
						$scope.weeks = data;
					});
				}).error(function(res) {
					alert('fail');
				});
			}
		}
		
//		$http.get('leagues/').success(function(data) {
//			$scope.leagues = data;
//		});
	});
	
	//***************  GAMES  **************************
	app.controller('SetupWeekController', function ($scope, $http, $log, $window, $rootScope, leagueService) {
		$log.debug('SetupWeekController');
		$scope.add_game_model = {};
		$scope.mock_week_model = {};
		$scope.weeksSetup = false;
		$scope.showEdit = false;
		$scope.seasons = {};
		$scope.weeks = {};
		
		//set default spread
		$scope.add_game_model.spread = 3.5;
		$scope.mock_week_model.numberOfGames = 16;
		
		var today = new Date(),
        dow = today.getDay(),
        toAdd = dow === 0 ? 0 : 7 - dow,
        thisSunday = new Date(),
        dateFormat = 'mm/dd/yy';
		
		thisSunday.setDate(thisSunday.getDate()+toAdd);
		$scope.add_game_model.gameStart = thisSunday;
		$scope.add_game_model.gameStart.setHours(13);
		$scope.add_game_model.gameStart.setMinutes(0);
		$scope.add_game_model.gameStart.setMilliseconds(0);
		$scope.add_game_model.gameStart.setSeconds(0);
//		$scope.add_game_model.time = new Date(1970, 0, 1, 13, 0, 0);

		$scope.mock_week_model.gameStart = new Date(thisSunday.getTime());
	    
		$scope.add_game_model.favHome = true;
		
		//		leagueService.getLeagues().then(function(data) {
		$http.get('/admin/leagues/seasons/current').success(function(data) {
			$scope.seasons = data;
			
			$log.debug('SetupController:Leagues=' +JSON.stringify(data));
//			$http.get('leagues/').success(function(data) {
//			$scope.leagues = data;
			if (data[0] === undefined) {
				$window.location.href = '/admin/#/seasons';
				return;
			}	
				
			$scope.add_game_model.seasonId = data[0].id;
			$scope.mock_week_model.seasonId = data[0].id;
			
			$http.get('/admin/weeks/seasonid/'+$scope.add_game_model.seasonId).success(function(data) {
				
				$log.debug('SetupController:Weeks='+JSON.stringify(data))
				$scope.weeks = data;
				if (Object.keys(data).length > 0)
				{
					$scope.weeksSetup=true;
					$scope.add_game_model.weekId = data[0].id;
					$scope.mock_week_model.weekId = data[0].id;
					
					leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
						$log.debug('SetupController:Games='+JSON.stringify(data.data))
						$scope.games = data.data;
						
					});
				}
				else
				{
					$window.location.href = '/admin/#/create';
				}
			});
		});
	
		
		$http.get('/admin/teams/leaguetype/pickem').success(function(data) {
			$scope.teams = data;
			$scope.add_game_model.favId = data[0].id;
			$scope.add_game_model.dogId = data[0].id;
		});
		
//		$log.debug('AddGame='+JSON.stringify($scope.addgame));
		this.addGame = function(add_game_model) {

			var local_model = {};
			angular.copy(add_game_model, local_model);
			// Stored directly as a standard Date object to let JSON.stringify handle UTC serialization correctly
			
//			local_model.gameStart = localDate.toISOString().replace('Z', '');
			
//			var date = local_model.date;
//			local_model.gameStart = date.getTime();
//			local_model.date = 
			$log.debug('AddGame='+JSON.stringify(local_model));
			
			$http({
				method : "POST",
				url : '/admin/games/',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify(local_model)
			}).success(function(res) { 
//				$scope.local_model = {};
				//$scope.$digest();
				
				$scope.games.push(angular.copy(res));
			}).error(function(res) {
				alert('fail');
			});
	
		}
		
		this.showEditGame = function(game) {
			$log.debug('ShowEditGame: gameId='+game.id);
			$scope.edit_game_model = {};
			
			$http.get('/admin/games/'+game.id).success(function(data) {
				$log.debug('games/'+game.id+' = '+JSON.stringify(data));
				$scope.edit_game_model = {};
				
				// Bulletproof date parsing
				var gameStart;
				if (typeof data.gameStart === 'number') {
					gameStart = new Date(data.gameStart * 1000);
				} else if (typeof data.gameStart === 'string' && !isNaN(data.gameStart)) {
					gameStart = new Date(Number(data.gameStart) * 1000);
				} else if (data.gameStart) {
					gameStart = new Date(data.gameStart);
				} else {
					gameStart = new Date();
				}
				
				if (isNaN(gameStart.getTime())) {
					gameStart = new Date();
				}
				
				gameStart.setSeconds(0);
				gameStart.setMilliseconds(0);
				
				data.gameStart = {};
				
				$scope.edit_game_model.fav = {};
				$scope.edit_game_model.dog = {};
				
				$http.get('/admin/teams/'+data.favId).success(function(teamData) {
					$scope.edit_game_model.fav = teamData;
				});
				
				$http.get('/admin/teams/'+data.dogId).success(function(teamData) {
					$scope.edit_game_model.dog = teamData;
				});
				
				$scope.edit_game_model.gameStart = gameStart;
				$scope.edit_game_model.spread = data.spread;
				$scope.edit_game_model.favHome = data.favHome;
				
				// Sanitize default 0 scores for unscored games to load empty inputs
				if (data.favScore === 0 && data.dogScore === 0) {
					$scope.edit_game_model.favScore = null;
					$scope.edit_game_model.dogScore = null;
				} else {
					$scope.edit_game_model.favScore = data.favScore;
					$scope.edit_game_model.dogScore = data.dogScore;
				}
				
				$scope.edit_game_model.id = data.id;
				$scope.showEdit = true;
				
				$log.debug(JSON.stringify($scope.edit_game_model.fav));
			});
		};
		
		this.editGame = function(game) {
			
			
			game.favId = game.fav.id;
			game.dogId = game.dog.id;

			$log.debug('editGame game='+JSON.stringify(game));
			$http({
				method : "PUT",
				url : '/admin/games/',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify(game)
			}).success(function(res) { 
//				
				
//				$scope.games.push(angular.copy(res));
				$scope.showEdit = false;
				leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
					$log.debug('SetupController:editGame:Games='+JSON.stringify(data.data))
					$scope.games = data.data;
				
				});
				
			}).error(function(res) {
				alert('fail');
			});
		};
		
		this.autoScore = function(week) {
			
			var week = {};
			week.id = $scope.add_game_model.weekId;
			$log.debug('autoScore: week='+JSON.stringify(week));
			$http({
				method : "POST",
				url : '/admin/games/autoscore',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify(week)
			}).success(function(res) { 
//				$window.location.href = 'index.html';
			}).error(function(res) {
				alert('fail');
			});
		}

		this.createMockWeek = function(mock_week_model) {
			var local_model = {};
			angular.copy(mock_week_model, local_model);
			// Stored directly as a standard Date object to let JSON.stringify handle UTC serialization correctly

			$log.debug('CreateMockWeek='+JSON.stringify(local_model));

			$http({
				method : "POST",
				url : '/admin/games/mock',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify(local_model)
			}).success(function(res) { 
				// Switch active week view to the mock week's week
				$scope.add_game_model.weekId = mock_week_model.weekId;
				leagueService.getGames(mock_week_model.weekId).then(function(data) {
					$scope.games = data.data;
				});
				alert('Mock week games created successfully!');
			}).error(function(res) {
				alert('fail');
			});
		}
		
		this.deleteGame = function(gameId) {
			if (!$window.confirm('Are you sure you want to delete this game?')) {
				return;
			}
			$log.debug('deleteGame gameId=' + gameId);
			$http({
				method : "DELETE",
				url : '/admin/games/' + gameId
			}).success(function(res) {
				alert('Game deleted successfully!');
				leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
					$scope.games = data.data;
				});
			}).error(function(res) {
				alert('Failed to delete game');
			});
		};
		
		$scope.changeSeason = function() {
			$log.debug('changeSeason seasonId='+$scope.add_game_model.seasonId);
			$http.get('/admin/weeks/seasonid/'+$scope.add_game_model.seasonId).success(function(data) {
				$scope.weeks = data;
				if (Object.keys(data).length > 0) {
					$scope.weeksSetup = true;
					$scope.add_game_model.weekId = data[0].id;
					$scope.mock_week_model.weekId = data[0].id;
					
					leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
						$scope.games = data.data;
					});
				} else {
					$scope.weeksSetup = false;
					$scope.games = [];
				}
			});
		};
		
		$scope.changeWeek = function() {
			
			//$log.debug("NavigationController:changeWeek $scope.nav.selectedWeekId="+$scope.nav.selectedWeekId);
			//makePickPageService.setWeek($scope.nav.selectedWeekId);
			$log.debug('weekChanged week='+$scope.add_game_model.weekId);
			$rootScope.$broadcast('weekChanged', $scope.add_game_model.weekId);
			
		};
		
		$scope.$on('weekChanged', function (events, args) {
			$log.debug('week='+args);
			
			leagueService.getGames(args).then(function(data) {
				$log.debug('SetupController:weekChanged='+JSON.stringify(data.data))
				$scope.games = data.data;
			
			});
			
		});
		
		$scope.getWeekNumber = function(weekId) {
			if (!$scope.weeks || !weekId) return '';
			for (var i = 0; i < $scope.weeks.length; i++) {
				if ($scope.weeks[i].id === weekId) {
					return "Week " + $scope.weeks[i].weekNumber;
				}
			}
			return '';
		};

		$scope.hasScores = function(game) {
			if (!game) return false;
			return !(Number(game.favScore) === 0 && Number(game.dogScore) === 0);
		};

		$scope.isFavWinner = function(game) {
			if (!$scope.hasScores(game)) return false;
			var favScore = Number(game.favScore);
			var dogScore = Number(game.dogScore);
			var spread = Number(game.spread);
			var adjustedDogScore = dogScore + spread;
			return favScore > adjustedDogScore;
		};

		$scope.isFavLoser = function(game) {
			if (!$scope.hasScores(game)) return false;
			return !$scope.isFavWinner(game);
		};

		$scope.isDogWinner = function(game) {
			if (!$scope.hasScores(game)) return false;
			var favScore = Number(game.favScore);
			var dogScore = Number(game.dogScore);
			var spread = Number(game.spread);
			var adjustedDogScore = dogScore + spread;
			return adjustedDogScore > favScore;
		};

		$scope.isDogLoser = function(game) {
			if (!$scope.hasScores(game)) return false;
			return !$scope.isDogWinner(game);
		};
		
		
//		$http.get('teams/leaguetype/pickem').success(function(data) {
//			$scope.team = data;
//		});
	});
	
	app.controller('AddScoreController', function ($scope, $http, $log, leagueService) {
		$log.debug('AddScoreController');
		$scope.seasons = {};
		$scope.weeks = {};
		$scope.games = [];
		$scope.selectedGame = null;
		
		$scope.add_game_model = {}; // Shared structure to host seasonId and weekId
		
		$http.get('/admin/leagues/seasons/current').success(function(data) {
			$scope.seasons = data;
			if (data[0] === undefined) {
				return;
			}
			$scope.add_game_model.seasonId = data[0].id;
			$scope.loadWeeks($scope.add_game_model.seasonId);
		});
		
		$scope.loadWeeks = function(seasonId) {
			$http.get('/admin/weeks/seasonid/' + seasonId).success(function(data) {
				$scope.weeks = data;
				if (Object.keys(data).length > 0) {
					// Default to the last created week
					$scope.add_game_model.weekId = data[data.length - 1].id;
					$scope.loadGames($scope.add_game_model.weekId);
				} else {
					$scope.games = [];
					$scope.selectedGame = null;
				}
			});
		};
		
		$scope.loadGames = function(weekId) {
			$scope.selectedGame = null;
			leagueService.getGames(weekId).then(function(data) {
				$scope.games = data.data;
			});
		};
		
		$scope.isGameUnscored = function(game) {
			if (!game) return false;
			if ($scope.selectedGame && game.id === $scope.selectedGame.id) {
				return true;
			}
			var isFavEmpty = game.favScore === null || game.favScore === undefined || game.favScore === "" || game.favScore === 0;
			var isDogEmpty = game.dogScore === null || game.dogScore === undefined || game.dogScore === "" || game.dogScore === 0;
			return isFavEmpty && isDogEmpty;
		};

		var lastSelectedGameId = null;
		$scope.$watch('selectedGame', function(newVal) {
			if (newVal) {
				if (newVal.id !== lastSelectedGameId) {
					lastSelectedGameId = newVal.id;
					if (newVal.favScore === 0) newVal.favScore = null;
					if (newVal.dogScore === 0) newVal.dogScore = null;
				}
			} else {
				lastSelectedGameId = null;
			}
		});
		
		$scope.changeSeason = function() {
			$scope.loadWeeks($scope.add_game_model.seasonId);
		};
		
		$scope.changeWeek = function() {
			$scope.loadGames($scope.add_game_model.weekId);
		};
		
		$scope.submitScore = function() {
			if (!$scope.selectedGame) return;
			
			$log.debug('SubmitScore: game=' + JSON.stringify($scope.selectedGame));
			
			// Map values explicitly to ensure the PUT request structure is correct
			var gameToSubmit = angular.copy($scope.selectedGame);
			
			$http({
				method : "PUT",
				url : '/admin/games/',
				contentType : "application/json",
				dataType : "json",
				data : JSON.stringify(gameToSubmit)
			}).success(function(res) {
				// Refresh the games list to dynamically filter out the updated game
				$scope.loadGames($scope.add_game_model.weekId);
			}).error(function(res) {
				alert('fail');
			});
		};
	});

	app.controller('BackupRestoreController', function ($scope, $http, $log) {
		$log.debug('BackupRestoreController initialized');
		
		$scope.status = {
			loading: false,
			success: null,
			error: null
		};

		$scope.restoreFile = null;

		$scope.onFileChange = function(element) {
			$scope.$apply(function() {
				$scope.restoreFile = element.files[0];
				$scope.status.success = null;
				$scope.status.error = null;
			});
		};

		$scope.restoreDatabase = function() {
			if (!$scope.restoreFile) {
				$scope.status.error = "Please select a database backup file first.";
				return;
			}

			if (!confirm("WARNING: This will delete ALL current database records and overwrite the system with the contents of this backup file. Are you absolutely sure you want to proceed?")) {
				return;
			}

			$scope.status.loading = true;
			$scope.status.success = null;
			$scope.status.error = null;

			var fd = new FormData();
			fd.append('file', $scope.restoreFile);

			$http.post('/admin/backup/import', fd, {
				transformRequest: angular.identity,
				headers: {'Content-Type': undefined}
			}).success(function(data) {
				$scope.status.loading = false;
				$scope.status.success = "Database successfully restored from " + $scope.restoreFile.name;
				$scope.restoreFile = null;
				var fileInput = document.getElementById('backupFileInput');
				if (fileInput) {
					fileInput.value = '';
				}
			}).error(function(data, status) {
				$scope.status.loading = false;
				$scope.status.error = "Failed to restore database: " + (data || "Unknown Error") + " (Status: " + status + ")";
			});
		};
	});
})();