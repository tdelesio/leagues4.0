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
		
		$http.get('/user').success(function(data) {
			$scope.username = data.name;
			checkAdminStatus();
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

		//get the leagues
		leagueService.getLeagues().then(function(data) {
			$log.debug('SettingsController:Leagues=' +JSON.stringify(data));

			$scope.leagues = data;
			checkAdminStatus();
			
			// Set league id and seasonId properly
			if (data && data.length > 0) {
				$scope.league.id = data[0].id;
				$scope.league.seasonId = data[0].seasonId;
				
				$http.get('/weeks/seasonid/'+$scope.league.seasonId).success(function(data) {
					$log.debug('SettingsController:Weeks='+JSON.stringify(data));
					$scope.weeks = data;
					if (data && data.length > 0) {
						$scope.week.weekId = data[0].id;
					}
					$rootScope.$broadcast('weekLoaded');
				});
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