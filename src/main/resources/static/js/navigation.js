(function () {
	var app = angular.module('navigation', ['ui.router']);

	app.controller('NavigationController', function ($scope, $rootScope, $http, $window, $log, leagueService, $state) {
		
		$scope.league = {};
		$scope.week = {};
		$scope.selectedPage = $state.current.name || 'make';
		$scope.username = "";
		$scope.leagues = [];
		$scope.isAdminOfAnyLeague = false;

		// Force Password Reset modal state
		$scope.forceResetModal = {
			show: false,
			newPassword: '',
			confirmPassword: '',
			updating: false,
			error: '',
			success: ''
		};

		function checkAdminStatus() {
			if ($scope.username && $scope.leagues && $scope.leagues.length > 0) {
				for (var i = 0; i < $scope.leagues.length; i++) {
					if ($scope.leagues[i].adminId === $scope.username) {
						$scope.isAdminOfAnyLeague = true;
						break;
					}
				}
			}
		}
		
		function loadWeeksForSeason(seasonId) {
			$http.get('/weeks/seasonid/' + seasonId).success(function(weeksData) {
				$log.debug('SettingsController:Weeks=' + JSON.stringify(weeksData));
				$scope.weeks = weeksData;
				if (weeksData && weeksData.length > 0) {
					$scope.week.weekId = weeksData[0].id;
				} else {
					$scope.week = {};
				}
				$rootScope.$broadcast('weekLoaded');
			});
		}

		$scope.$watch('league.id', function(newVal, oldVal) {
			if (newVal) {
				var selectedLeague = null;
				for (var i = 0; i < $scope.leagues.length; i++) {
					if ($scope.leagues[i].id === newVal) {
						selectedLeague = $scope.leagues[i];
						break;
					}
				}
				if (selectedLeague) {
					$scope.league.seasonId = selectedLeague.seasonId;
					$scope.league.leagueName = selectedLeague.leagueName;
					loadWeeksForSeason(selectedLeague.seasonId);
				}
			}
		});

		function loadPlayerLeagues(username, selectJoinedLeagueId) {
			return leagueService.getLeaguesForPlayer(username).then(function(data) {
				$log.debug('Leagues for player=' + JSON.stringify(data));
				var leagues = [];
				if (data) {
					for (var i = 0; i < data.length; i++) {
						leagues.push({
							id: data[i].leagueId,
							leagueName: data[i].leagueName,
							seasonId: data[i].seasonId,
							adminId: data[i].adminId
						});
					}
				}
				$scope.leagues = leagues;
				checkAdminStatus();
				
				if (leagues.length > 0) {
					if (selectJoinedLeagueId) {
						$scope.league.id = selectJoinedLeagueId;
					} else {
						$scope.league.id = leagues[0].id;
					}
				} else {
					$scope.league = {};
					$scope.weeks = [];
					$scope.week = {};
					$rootScope.$broadcast('weekLoaded');
				}
			});
		}

		$http.get('/user').success(function(userData) {
			$scope.username = userData.name;
			
			$http.get('/players/username/' + $scope.username).success(function(player) {
				if (player) {
					if (player.accountLevel === 'admin' || player.memberLevel === 'ADMIN') {
						$scope.isAdminOfAnyLeague = true;
					}
					if (player.passwordResetRequired) {
						$scope.forceResetModal.show = true;
					}
				}
			});
			
			loadPlayerLeagues($scope.username);
		});

		// Join League Modal Logic
		$scope.showJoinModal = false;
		$scope.availableLeagues = [];
		$scope.joinLeagueData = {
			leagueId: '',
			password: 'giants'
		};
		$scope.joinLeagueError = '';

		$scope.openJoinLeagueModal = function() {
			$scope.showJoinModal = true;
			$scope.joinLeagueData.leagueId = '';
			$scope.joinLeagueData.password = 'giants';
			$scope.joinLeagueError = '';
			$scope.availableLeagues = [];

			$http.get('/leagues/').success(function(allLeagues) {
				var joinedMap = {};
				for (var i = 0; i < $scope.leagues.length; i++) {
					joinedMap[$scope.leagues[i].id] = true;
				}
				
				var available = [];
				if (allLeagues) {
					for (var j = 0; j < allLeagues.length; j++) {
						if (!joinedMap[allLeagues[j].id]) {
							available.push(allLeagues[j]);
						}
					}
				}
				$scope.availableLeagues = available;
			}).error(function() {
				$scope.joinLeagueError = "Failed to load available leagues. Please try again.";
			});
		};

		$scope.closeJoinLeagueModal = function() {
			$scope.showJoinModal = false;
		};

		$scope.joinLeague = function() {
			$scope.joinLeagueError = '';
			
			if (!$scope.joinLeagueData.leagueId) {
				$scope.joinLeagueError = "Please select a league to join.";
				return;
			}

			var selectedLeague = null;
			for (var i = 0; i < $scope.availableLeagues.length; i++) {
				if ($scope.availableLeagues[i].id === $scope.joinLeagueData.leagueId) {
					selectedLeague = $scope.availableLeagues[i];
					break;
				}
			}

			var payload = {
				leagueId: $scope.joinLeagueData.leagueId,
				leagueName: selectedLeague ? selectedLeague.leagueName : null,
				password: $scope.joinLeagueData.password
			};

			$http.post('/leagues/player', payload).success(function() {
				var joinedLeagueId = payload.leagueId;
				$scope.showJoinModal = false;
				loadPlayerLeagues($scope.username, joinedLeagueId);
			}).error(function(xhr) {
				var msg = "Failed to join league. Please verify the password.";
				if (xhr && xhr.message) {
					msg = xhr.message;
				}
				$scope.joinLeagueError = msg;
			});
		};

		$scope.changePage = function(stateName) {
			if (stateName) {
				$state.go(stateName);
			}
		};

		$scope.submitForcePasswordChange = function() {
			$scope.forceResetModal.error = '';
			$scope.forceResetModal.success = '';
			
			if (!$scope.forceResetModal.newPassword || !$scope.forceResetModal.confirmPassword) {
				$scope.forceResetModal.error = 'Please enter and confirm your password.';
				return;
			}
			
			if ($scope.forceResetModal.newPassword !== $scope.forceResetModal.confirmPassword) {
				$scope.forceResetModal.error = 'Passwords do not match.';
				return;
			}
			
			if ($scope.forceResetModal.newPassword.length < 5) {
				$scope.forceResetModal.error = 'Password must be at least 5 characters long.';
				return;
			}
			
			$scope.forceResetModal.updating = true;
			
			var payload = {
				username: $scope.username,
				password: $scope.forceResetModal.newPassword
			};
			
			$http.put('/players/password', payload).success(function() {
				$scope.forceResetModal.updating = false;
				$scope.forceResetModal.success = 'Password successfully updated!';
				setTimeout(function() {
					$scope.$apply(function() {
						$scope.forceResetModal.show = false;
					});
				}, 1500);
			}).error(function(err) {
				$scope.forceResetModal.updating = false;
				$scope.forceResetModal.error = 'Failed to update password: ' + (err.message || 'unknown error');
			});
		};

		$scope.logout = function() {
			$http.post('/logout', {}).then(function() {
				$window.location.href = '/login.html';
			}, function(err) {
				$log.error('Logout failed:', err);
				$window.location.href = '/login.html'; // Fallback redirect
			});
		};

		$rootScope.$on('$stateChangeSuccess', function(event, toState) {
			$scope.selectedPage = toState.name;
		});

		$scope.$watch('week.weekId', function(newVal, oldVal) {
			if (newVal && newVal !== oldVal) {
				$rootScope.$broadcast('weekLoaded');
			}
		});
		
		this.settings = function getSettings() {
			/* CLOSE PANEL */
			if ($("#settings-panel").css("margin-top") == "0px") {
				$("#settings-panel").css("margin-top", "-150px");
				$(".nav").css("margin-top", "0px");
				$("#results").css("padding-top", "90px");
				$("li#nav-settings a").css("color", "#FFB415");
				/* OPEN PANEL */
			} else {
				$("#settings-panel").css("margin-top", "0px");
				$(".nav").css("margin-top", "150px");
				$("#results").css("padding-top", "240px");
				$("li#nav-settings a").css("color", "#FFFFFF");
			}
		};
	});

})();