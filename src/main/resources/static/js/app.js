(function () {
	var app = angular.module('app', ['ui.router', 'navigation', 'makepicks']);
	
	app.config(function($stateProvider, $urlRouterProvider) {
		  //
		  // For any unmatched url, redirect to /state1
		  $urlRouterProvider.otherwise("/make");
		  //
		  // Now set up the states
		  $stateProvider
		    .state('make', {
		      url: "/make",
		      templateUrl: "makePicks.html"
		    })
		    .state('view', {
		      url: "/view",
		      templateUrl: "viewPicks.html"
		    })
		    .state('winsummary', {
		      url: "/winsummary",
		      templateUrl: "winsummary.html"
		    })
		});
	
	app.directive('settings', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/settings.html'
		};
	});
	
	app.directive('navigation', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/navigation.html'
		};
	});
	
	app.directive('makePicks', function() {
		return {
			restrict: 'E',
			templateUrl: 'partials/makePicks.html'
		};
	});
	
	
	
		
		
		
		//get the picks for the week/player
//		$http.get('/picks/leagueid/'+$scope.league.id+'/weekid/'+$scope.week.id+'/playerid/'+$scope.username).success(function(data) {
//			 angular.forEach(data, function (value) {
//				 $scope.pickMap[value.gameId] = value;
//			 });
//		});
	
//		var pickmap = {};
//		var points = 0;
//		var gt = ""
//		
//		_.each(picks,function(pick,i) {
//			pickmap[pick.game.id] = pick;
//		});
//
//		_.each(games,function(game, i) {
//
//			
//		var tz = moment(game.gameStart).tz("America/New_York");
//		var gsFormatted = tz.format("dddd, MMMM Do YYYY, h:mm A z");
//
//			if(gt != game.gameStartDisplay) {	%>
//				<div class="gametime"><%=gsFormatted%></div>
//			<% }
//
//			gt = game.gameStartDisplay;
//		//DETERMINE IF WIN OR NOT
//			var pick;
//			if (pickmap[game.id]) {
//				pick = pickmap[game.id];
//		//increament the pouints
//				points += pick.winValue;
//			}
		
	
})();