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
		    })
		   .state('clean', {
		      url: "/clean",
		      templateUrl: "clean.html"
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

	app.directive('cleanData', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/clean.html'
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

		// Broadcast Email state
		$scope.broadcastModal = {
			show: false,
			subject: '',
			body: '',
			sending: false,
			error: '',
			success: ''
		};

		// Reset Success modal state
		$scope.resetSuccessModal = {
			show: false,
			username: '',
			tempPassword: '',
			copied: false
		};

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
			$window.customConfirm('Are you sure you want to permanently delete player ' + player.username + '? This will cascade and purge all of their picks and league records.', function() {
				$http.delete('/admin/players/' + encodeURIComponent(player.username)).success(function() {
					var idx = $scope.players.indexOf(player);
					if (idx > -1) {
						$scope.players.splice(idx, 1);
					}
				}).error(function(err) {
					$window.alert('Error deleting player: ' + (err.message || 'unknown error'));
					$log.error('Error deleting player:', err);
				});
			});
		};

		$scope.resetPassword = function(player) {
			$window.customConfirm('Are you sure you want to reset the password for ' + player.username + '? This will immediately generate a new temporary password and save it in the database.', function() {
				$http.post('/admin/players/reset-password', { username: player.username }).success(function(response) {
					$scope.resetSuccessModal.username = player.username;
					$scope.resetSuccessModal.tempPassword = response.tempPassword || response.password || '';
					$scope.resetSuccessModal.copied = false;
					$scope.resetSuccessModal.show = true;
				}).error(function(err) {
					$window.alert('Error resetting password: ' + (err.message || 'unknown error'));
					$log.error('Error resetting password:', err);
				});
			});
		};

		$scope.copyResetPassword = function() {
			var text = $scope.resetSuccessModal.tempPassword;
			if (navigator.clipboard && navigator.clipboard.writeText) {
				navigator.clipboard.writeText(text).then(function() {
					$scope.$apply(function() {
						$scope.resetSuccessModal.copied = true;
					});
				});
			} else {
				// Fallback
				var textArea = document.createElement("textarea");
				textArea.value = text;
				textArea.style.position = "fixed";  // Avoid scrolling to bottom
				document.body.appendChild(textArea);
				textArea.focus();
				textArea.select();
				try {
					document.execCommand('copy');
					$scope.resetSuccessModal.copied = true;
				} catch (err) {
					$log.error('Fallback: Oops, unable to copy', err);
				}
				document.body.removeChild(textArea);
			}
		};

		$scope.closeResetSuccessModal = function() {
			$scope.resetSuccessModal.show = false;
		};

		$scope.openBroadcastModal = function() {
			$scope.broadcastModal.subject = '';
			$scope.broadcastModal.body = '';
			$scope.broadcastModal.sending = false;
			$scope.broadcastModal.error = '';
			$scope.broadcastModal.success = '';
			$scope.broadcastModal.show = true;
		};

		$scope.closeBroadcastModal = function() {
			if (!$scope.broadcastModal.sending) {
				$scope.broadcastModal.show = false;
			}
		};

		$scope.sendBroadcastEmail = function() {
			$scope.broadcastModal.sending = true;
			$scope.broadcastModal.error = '';
			$scope.broadcastModal.success = '';

			var payload = {
				subject: $scope.broadcastModal.subject,
				body: $scope.broadcastModal.body
			};

			$http.post('/admin/players/email-all', payload).success(function() {
				$scope.broadcastModal.sending = false;
				$scope.broadcastModal.success = 'Emails have been successfully sent to all players!';
				setTimeout(function() {
					$scope.$apply(function() {
						$scope.broadcastModal.show = false;
					});
				}, 1800);
			}).error(function(err) {
				$scope.broadcastModal.sending = false;
				$scope.broadcastModal.error = 'Failed to send emails: ' + (err.message || 'unknown error');
			});
		};

		$scope.updatePlayer = function(player) {
			var payload = {
				username: player.username,
				venmoId: player.venmoId,
				paid: !!player.paid
			};
			$http.post('/admin/players/update-profile', payload).success(function() {
				// Success, silently updated!
			}).error(function(err) {
				$window.alert('Error updating player settings: ' + (err.message || 'unknown error'));
				$log.error('Error updating player settings:', err);
			});
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
			
			$window.customConfirm("Are you sure you want to delete this season?", function() {
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
			});
		};
		
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
			
			$window.customConfirm("Are you sure you want to delete this league?", function() {
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
			});
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
			
			$window.customConfirm("Are you sure you want to delete this week?", function() {
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
			});
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
						$scope.resetAvailableTeamsSelection();
					});
				}
				else
				{
					$window.location.href = '/admin/#/create';
				}
			});
		});
	
		$scope.getAvailableTeams = function(selectedTeamId) {
			if (!$scope.teams) return [];
			
			var unavailableIds = {};
			if ($scope.games) {
				for (var i = 0; i < $scope.games.length; i++) {
					var g = $scope.games[i];
					if (g.favId) unavailableIds[g.favId] = true;
					if (g.dogId) unavailableIds[g.dogId] = true;
				}
			}
			
			if (selectedTeamId) {
				unavailableIds[selectedTeamId] = true;
			}
			
			return $scope.teams.filter(function(team) {
				return !unavailableIds[team.id];
			});
		};

		$scope.resetAvailableTeamsSelection = function() {
			var available = $scope.getAvailableTeams();
			if (available && available.length >= 2) {
				$scope.add_game_model.favId = available[0].id;
				$scope.add_game_model.dogId = available[1].id;
			} else if (available && available.length >= 1) {
				$scope.add_game_model.favId = available[0].id;
				$scope.add_game_model.dogId = null;
			} else {
				$scope.add_game_model.favId = null;
				$scope.add_game_model.dogId = null;
			}
		};
		
		$http.get('/admin/teams/leaguetype/pickem').success(function(data) {
			$scope.teams = data;
			$scope.resetAvailableTeamsSelection();
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
				$scope.resetAvailableTeamsSelection();
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
			if (!$scope.games || $scope.games.length === 0) {
				alert("No games are currently listed for this week. Please fetch or add some games first!");
				return;
			}

			$window.customConfirm("Are you sure you want to retrieve and automatically update scores for all games in the selected week?", function() {
				$scope.importing = true;
				var apiKey = "0655b9dbb7b49726390fef3e109b84af";
				var url = "https://api.the-odds-api.com/v4/sports/americanfootball_nfl/scores/?apiKey=" + apiKey + "&daysFrom=3";

				$log.debug('Fetching NFL live and recently completed scores...');
				$http.get(url).success(function(apiScores) {
					var updateQueue = [];

					angular.forEach($scope.games, function(g) {
						// Find matching api score
						var matchedApiGame = null;
						for (var i = 0; i < apiScores.length; i++) {
							var apiGame = apiScores[i];
							var apiHomeId = teamNameToIdMap[apiGame.home_team];
							var apiAwayId = teamNameToIdMap[apiGame.away_team];

							var dbFavId = g.fav ? g.fav.id : g.favId;
							var dbDogId = g.dog ? g.dog.id : g.dogId;

							if ((dbFavId === apiHomeId && dbDogId === apiAwayId) ||
								(dbFavId === apiAwayId && dbDogId === apiHomeId)) {
								matchedApiGame = apiGame;
								break;
							}
						}

						if (matchedApiGame && matchedApiGame.scores && matchedApiGame.scores.length > 0) {
							var homeScore = null;
							var awayScore = null;

							angular.forEach(matchedApiGame.scores, function(s) {
								if (s.name === matchedApiGame.home_team) {
									homeScore = Number(s.score);
								} else if (s.name === matchedApiGame.away_team) {
									awayScore = Number(s.score);
								}
							});

							if (homeScore !== null && awayScore !== null && !isNaN(homeScore) && !isNaN(awayScore)) {
								// Deep copy local game object to modify
								var updatedGame = {};
								angular.copy(g, updatedGame);

								updatedGame.favId = g.fav ? g.fav.id : g.favId;
								updatedGame.dogId = g.dog ? g.dog.id : g.dogId;

								if (g.favHome) {
									updatedGame.favScore = homeScore;
									updatedGame.dogScore = awayScore;
								} else {
									updatedGame.favScore = awayScore;
									updatedGame.dogScore = homeScore;
								}

								updateQueue.push(updatedGame);
							}
						}
					});

					if (updateQueue.length === 0) {
						$scope.importing = false;
						alert("No matching live or completed game scores were found on the API for the current week's games.");
						return;
					}

					var successCount = 0;
					var failCount = 0;

					var updateNextGameScore = function(index) {
						if (index >= updateQueue.length) {
							$scope.importing = false;
							alert("Auto-score completion summary:\n\n• Successfully scored " + successCount + " game(s)\n• Failed to update " + failCount + " game(s)\n\nThe table will now refresh with the latest scored results!");
							
							// Refresh local games list
							leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
								$scope.games = data.data;
							});
							return;
						}

						var gameToUpdate = updateQueue[index];
						$http({
							method: "PUT",
							url: '/admin/games/',
							contentType: "application/json",
							dataType: "json",
							data: JSON.stringify(gameToUpdate)
						}).success(function() {
							successCount++;
							updateNextGameScore(index + 1);
						}).error(function() {
							failCount++;
							updateNextGameScore(index + 1);
						});
					};

					updateNextGameScore(0);

				}).error(function(err) {
					$scope.importing = false;
					alert("Failed to retrieve live scores from the API: " + JSON.stringify(err));
				});
			}, true);
		};

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
			$window.customConfirm('Are you sure you want to delete this game?', function() {
				$log.debug('deleteGame gameId=' + gameId);
				$http({
					method : "DELETE",
					url : '/admin/games/' + gameId
				}).success(function(res) {
					alert('Game deleted successfully!');
					leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
						$scope.games = data.data;
						$scope.resetAvailableTeamsSelection();
					});
				}).error(function(res) {
					alert('Failed to delete game');
				});
			});
		};
		
		// --- THE-ODDS-API INTEGRATION FOR WEEK GAMES IMPORT ---
		$scope.apiGames = [];
		$scope.importing = false;
		$scope.selectAll = true;
		$scope.importWeeks = [];
		
		$scope.import_week_model = {
			seasonId: "",
			weekId: "",
			startDate: null,
			endDate: null
		};

		var teamNameToIdMap = {
			"Arizona Cardinals": "ari",
			"Buffalo Bills": "buf",
			"Miami Dolphins": "mia",
			"New England Patriots": "ne",
			"New York Jets": "nyj",
			"Baltimore Ravens": "bal",
			"Cincinnati Bengals": "cin",
			"Cleveland Browns": "cle",
			"Pittsburgh Steelers": "pit",
			"Houston Texans": "hou",
			"Indianapolis Colts": "ind",
			"Jacksonville Jaguars": "jac",
			"Tennessee Titans": "ten",
			"Denver Broncos": "den",
			"Kansas City Chiefs": "kc",
			"Las Vegas Raiders": "lv",
			"Los Angeles Chargers": "lac",
			"Dallas Cowboys": "dal",
			"New York Giants": "nyg",
			"Philadelphia Eagles": "phi",
			"Washington Commanders": "was",
			"Washington Redskins": "was",
			"Chicago Bears": "chi",
			"Detroit Lions": "det",
			"Green Bay Packers": "gb",
			"Minnesota Vikings": "min",
			"Atlanta Falcons": "atl",
			"Tampa Bay Buccaneers": "tb",
			"New Orleans Saints": "no",
			"San Francisco 49ers": "sf",
			"Seattle Seahawks": "sea",
			"Carolina Panthers": "car",
			"Los Angeles Rams": "lar"
		};

		var parseBoundaryDate = function(input, isEnd) {
			if (!input) return null;
			if (angular.isDate(input)) {
				var d = new Date(input.getTime());
				if (isEnd) {
					d.setHours(23, 59, 59, 999);
				} else {
					d.setHours(0, 0, 0, 0);
				}
				return d;
			}
			if (typeof input === 'string') {
				if (input.indexOf('-') !== -1) {
					var parts = input.split('-');
					if (isEnd) {
						return new Date(parts[0], parts[1] - 1, parts[2], 23, 59, 59, 999);
					} else {
						return new Date(parts[0], parts[1] - 1, parts[2], 0, 0, 0, 0);
					}
				}
				if (input.indexOf('/') !== -1) {
					var parts = input.split('/');
					if (isEnd) {
						return new Date(parts[2], parts[0] - 1, parts[1], 23, 59, 59, 999);
					} else {
						return new Date(parts[2], parts[0] - 1, parts[1], 0, 0, 0, 0);
					}
				}
			}
			var parsed = new Date(input);
			if (!isNaN(parsed.getTime())) {
				if (isEnd) {
					parsed.setHours(23, 59, 59, 999);
				} else {
					parsed.setHours(0, 0, 0, 0);
				}
				return parsed;
			}
			return null;
		};

		$scope.$watch('seasons', function(newSeasons) {
			if (newSeasons && Object.keys(newSeasons).length > 0) {
				var firstSeason = newSeasons[0] || newSeasons[Object.keys(newSeasons)[0]];
				if (firstSeason) {
					$scope.import_week_model.seasonId = firstSeason.id;
					$scope.onImportSeasonChange();
				}
			}
		}, true);

		$scope.onImportSeasonChange = function() {
			if ($scope.import_week_model.seasonId) {
				$http.get('/admin/weeks/seasonid/' + $scope.import_week_model.seasonId).success(function(data) {
					$scope.importWeeks = data;
					if (data && data.length > 0) {
						$scope.import_week_model.weekId = data[0].id;
					}
				});
			}
		};

		this.onStartDateChange = function() {
			if ($scope.import_week_model.startDate) {
				var start = $scope.import_week_model.startDate;
				if (!angular.isDate(start)) {
					var parts = String(start).split('-');
					if (parts.length === 3) {
						start = new Date(parts[0], parts[1] - 1, parts[2]);
					} else {
						start = new Date(start);
					}
				}
				
				if (start && !isNaN(start.getTime())) {
					var end = new Date(start.getTime() + 5 * 24 * 60 * 60 * 1000);
					if (angular.isDate($scope.import_week_model.startDate)) {
						$scope.import_week_model.endDate = end;
					} else {
						var yyyy = end.getFullYear();
						var mm = String(end.getMonth() + 1).padStart(2, '0');
						var dd = String(end.getDate()).padStart(2, '0');
						$scope.import_week_model.endDate = yyyy + '-' + mm + '-' + dd;
					}
				}
			}
		};

		this.toggleSelectAll = function() {
			angular.forEach($scope.apiGames, function(g) {
				if (g.favId && g.dogId) {
					g.selected = $scope.selectAll;
				}
			});
		};

		this.hasSelectedGames = function() {
			if (!$scope.apiGames) return false;
			for (var i = 0; i < $scope.apiGames.length; i++) {
				if ($scope.apiGames[i].selected) return true;
			}
			return false;
		};

		this.fetchApiGames = function() {
			$scope.importing = true;
			$scope.apiGames = [];
			$scope.selectAll = true;
			
			var apiKey = "0655b9dbb7b49726390fef3e109b84af";
			var url = "https://api.the-odds-api.com/v4/sports/americanfootball_nfl/odds/?apiKey=" + apiKey + "&regions=us&markets=spreads&bookmakers=draftkings";
			
			$log.debug('Fetching NFL schedule & spreads from the-odds-api...');
			$http.get(url).success(function(data) {
				$scope.importing = false;
				var parsedGames = [];
				
				angular.forEach(data, function(game) {
					var commenceDate = new Date(game.commence_time);
					
					// Apply start and end date boundary filtering
					var startBoundary = parseBoundaryDate($scope.import_week_model.startDate, false);
					if (startBoundary && commenceDate < startBoundary) {
						return;
					}
					
					var endBoundary = parseBoundaryDate($scope.import_week_model.endDate, true);
					if (endBoundary && commenceDate > endBoundary) {
						return;
					}
					
					var spreadMarket = null;
					if (game.bookmakers && game.bookmakers[0] && game.bookmakers[0].markets) {
						spreadMarket = game.bookmakers[0].markets[0];
					}
					
					if (spreadMarket && spreadMarket.outcomes && spreadMarket.outcomes.length === 2) {
						var outcomes = spreadMarket.outcomes;
						var favName, dogName, rawSpread;
						
						// Identify Favorite (negative/lowest points value) and Underdog (positive points value)
						if (outcomes[0].point < 0) {
							favName = outcomes[0].name;
							dogName = outcomes[1].name;
							rawSpread = Math.abs(outcomes[0].point);
						} else if (outcomes[1].point < 0) {
							favName = outcomes[1].name;
							dogName = outcomes[0].name;
							rawSpread = Math.abs(outcomes[1].point);
						} else {
							// Spread is even/0, treat the first outcome team as favorite
							favName = outcomes[0].name;
							dogName = outcomes[1].name;
							rawSpread = Math.abs(outcomes[0].point) || 0.0;
						}
						
						// Force spread to end in .5 (e.g. 3 -> 3.5, 3.5 -> 3.5, 0 -> 0.5)
						var spreadVal = Math.floor(rawSpread) + 0.5;
						
						var favId = teamNameToIdMap[favName];
						var dogId = teamNameToIdMap[dogName];
						var favHome = (favName === game.home_team);
						
						parsedGames.push({
							favId: favId,
							dogId: dogId,
							favHome: favHome,
							spread: spreadVal,
							gameStart: commenceDate,
							favName: favName,
							dogName: dogName,
							selected: !!favId && !!dogId
						});
					}
				});
				
				$scope.apiGames = parsedGames;
				if (parsedGames.length === 0) {
					alert('No NFL games with valid spreads found within the selected date range.');
				}
			}).error(function(err) {
				$scope.importing = false;
				alert('Failed to retrieve spreads: ' + JSON.stringify(err));
			});
		};

		this.importSelectedGames = function() {
			var gamesToImport = [];
			angular.forEach($scope.apiGames, function(g) {
				if (g.selected) {
					gamesToImport.push(g);
				}
			});
			
			if (gamesToImport.length === 0) {
				alert('No games selected.');
				return;
			}
			
			$window.customConfirm('Are you sure you want to import ' + gamesToImport.length + ' games into the selected week?', function() {
				$scope.importing = true;
				var importCount = 0;
				var failCount = 0;
				
				var saveNextGame = function(index) {
					if (index >= gamesToImport.length) {
						$scope.importing = false;
						$scope.apiGames = []; // Clear preview on completion
						alert('Successfully imported ' + importCount + ' games! ' + (failCount > 0 ? (failCount + ' games failed.') : ''));
						
						// If the active viewed week in admin.js is the same as the imported week, refresh the active games list
						if ($scope.add_game_model && $scope.add_game_model.weekId === $scope.import_week_model.weekId) {
							leagueService.getGames($scope.add_game_model.weekId).then(function(data) {
								$scope.games = data.data;
								$scope.resetAvailableTeamsSelection();
							});
						}
						return;
					}
					
					var g = gamesToImport[index];
					var payload = {
						seasonId: $scope.import_week_model.seasonId,
						weekId: $scope.import_week_model.weekId,
						favId: g.favId,
						dogId: g.dogId,
						favHome: g.favHome,
						spread: g.spread,
						gameStart: g.gameStart.toISOString() // Maintain standard UTC formatting expected by parser
					};
					
					$http({
						method : "POST",
						url : '/admin/games/',
						contentType : "application/json",
						dataType : "json",
						data : JSON.stringify(payload)
					}).success(function(res) {
						importCount++;
						saveNextGame(index + 1);
					}).error(function(err) {
						failCount++;
						$log.error('Failed to import game: ' + JSON.stringify(g) + ' error: ' + JSON.stringify(err));
						saveNextGame(index + 1);
					});
				};
				
				saveNextGame(0);
			}, true);
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
						$scope.resetAvailableTeamsSelection();
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
				$scope.resetAvailableTeamsSelection();
			
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

			$window.customConfirm("WARNING: This will delete ALL current database records and overwrite the system with the contents of this backup file. Are you absolutely sure you want to proceed?", function() {
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
			});
		};
	});

	app.controller('CleanController', function ($scope, $http, $log) {
		$log.debug('CleanController initialized');

		$scope.status = {
			loading: false,
			success: null,
			error: null
		};

		// Modal state
		$scope.modal = {
			show: false,
			title: '',
			target: '', // 'picks', 'games', 'weeks', 'seasons', 'leagues'
			message: '',
			dependencies: []
		};

		// Dependency map for each target
		var dependencyMap = {
			'picks': {
				title: 'Clear Picks & Double Picks',
				message: 'Are you sure you want to clear all picks and double picks?',
				dependencies: ['All player predictions and pick history']
			},
			'games': {
				title: 'Clear Games',
				message: 'Are you sure you want to delete all games? This will also clear all dependent objects.',
				dependencies: ['All Games', 'All Picks & Double Picks placed on those games']
			},
			'weeks': {
				title: 'Clear Weeks',
				message: 'Are you sure you want to delete all weeks? This will cascade delete games and picks.',
				dependencies: ['All Weeks', 'All Games inside those weeks', 'All Picks & Double Picks placed on those games']
			},
			'seasons': {
				title: 'Clear Seasons',
				message: 'Are you sure you want to delete all seasons? This will cascade delete weeks, games, and picks.',
				dependencies: ['All Seasons', 'All Weeks in those seasons', 'All Games in those weeks', 'All Picks & Double Picks']
			},
			'leagues': {
				title: 'Clear Leagues',
				message: 'Are you sure you want to delete all leagues? This will cascade delete player memberships and picks.',
				dependencies: ['All Leagues', 'All Player-League Registrations / Enrollments', 'All Picks & Double Picks inside those leagues']
			}
		};

		$scope.openModal = function(target) {
			var config = dependencyMap[target];
			if (!config) return;

			$scope.modal.target = target;
			$scope.modal.title = config.title;
			$scope.modal.message = config.message;
			$scope.modal.dependencies = config.dependencies;
			$scope.modal.show = true;

			$scope.status.success = null;
			$scope.status.error = null;
		};

		$scope.closeModal = function() {
			$scope.modal.show = false;
		};

		$scope.confirmPurge = function() {
			var target = $scope.modal.target;
			if (!target) return;

			$scope.closeModal();
			$scope.status.loading = true;
			$scope.status.success = null;
			$scope.status.error = null;

			$http.post('/admin/clean/' + target)
				.success(function() {
					$scope.status.loading = false;
					$scope.status.success = "Successfully cleaned " + target + " and all of its dependent objects!";
				})
				.error(function(data, status) {
					$scope.status.loading = false;
					$scope.status.error = "Failed to clean " + target + ": " + (data || "Unknown Error") + " (Status: " + status + ")";
				});
		};
	});
})();