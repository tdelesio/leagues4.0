(function () {
	var app = angular.module('navigation', ['ui.router']);

	app.controller('NavigationController', function ($scope, $rootScope, $http, $window, $log, leagueService, $state) {
		
		$scope.league = {};
		$scope.week = {};
		$scope.selectedPage = $state.current.name || 'make';
		$scope.username = "";
		$scope.leagues = [];
		$scope.isAdminOfAnyLeague = false;

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

		$http.get('/user').success(function(userData) {
			$scope.username = userData.name;
			
			leagueService.getLeaguesForPlayer($scope.username).then(function(data) {
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
					$scope.league.id = leagues[0].id;
				} else {
					$scope.league = {};
					$scope.weeks = [];
					$scope.week = {};
					$rootScope.$broadcast('weekLoaded');
				}
			});
		});

		$scope.changePage = function(stateName) {
			if (stateName) {
				$state.go(stateName);
			}
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