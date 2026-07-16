(function () {
	var app = angular.module('makepicks', ['leagueservice']);
	
	app.controller('MakePicksController', function ($scope, $http, $window, $log, $q, leagueService) { 
		
		$scope.pickMap = {};
		$scope.gameMetaMap = {};
		$scope.showGS = true;
		$scope.picksLoaded = false;
		
		var loadGamesAndPicks = function() {
			if (!$scope.week || !$scope.week.weekId) {
				return;
			}
			$scope.picksLoaded = false;
			$log.debug('MakePickController: loadGamesAndPicks weekId=' + $scope.week.weekId);
			
			//load all the games
			leagueService.getGames($scope.week.weekId).then(function(games) {
				$log.debug('MakePickController:games=' + JSON.stringify(games));
				$scope.games = games;
			});
			
			//load all the picks and double pick concurrently to prevent race conditions
			var leagueId = ($scope.league && $scope.league.id) ? $scope.league.id : ($scope.leagues && $scope.leagues.length > 0 ? $scope.leagues[0].id : null);
			
			var picksPromise = leagueService.getMyPicks(leagueId, $scope.week.weekId);
			var doublePickPromise = leagueService.getDoublePick(leagueId, $scope.week.weekId);
			
			$q.all([picksPromise, doublePickPromise]).then(function(results) {
				var picks = results[0];
				var doublePick = results[1];
				
				$log.debug('MakePickController:picks=' + JSON.stringify(picks));
				$log.debug('MakePickController:doublePick=' + JSON.stringify(doublePick));
				
				$scope.pickMap = picks;
				$scope.doublePick = doublePick;
				$scope.picksLoaded = true;
			}, function(error) {
				$log.error('MakePickController: failed to load picks or double pick', error);
				$scope.picksLoaded = true; // Fallback to allow rendering
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
		
		$scope.getGameStatusClass = function(game) {
			if (!game) return 'status-not-started';
			
			var classes = [];
			
			// Check if double pick (using loose comparison to prevent type mismatch issues)
			if ($scope.doublePick && $scope.doublePick.gameId == game.id) {
				classes.push('status-double');
			}
			
			// 1. Check if game started
			if (!game.hasGameStarted) {
				classes.push('status-not-started');
				return classes.join(' ');
			}
			
			// 2. Check if in progress (started but both scores are 0)
			if (game.favScore === 0 && game.dogScore === 0) {
				classes.push('status-in-progress');
				return classes.join(' ');
			}
			
			// 3. Completed - check win/loss
			// Prevent flash on initial load while picks are still loading
			if (!$scope.picksLoaded) {
				return classes.join(' ');
			}
			
			var pick = $scope.pickMap[game.id];
			if (!pick) {
				// No pick made is counted as a loss
				classes.push('status-loss');
			} else {
				// Determine winner client-side for maximum reliability (robust numeric parsing)
				var dogScore = Number(game.dogScore);
				var favScore = Number(game.favScore);
				var spread = Number(game.spread);
				var winnerId = (dogScore + spread > favScore) ? game.dogId : game.favId;
				if (pick.teamId === winnerId) {
					classes.push('status-win');
				} else {
					classes.push('status-loss');
				}
			}
			
			return classes.join(' ');
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

		$scope.submitPicksVerification = function() {
			var remainingCount = $scope.getRemainingPicksCount();
			var doubleSelected = $scope.isDoublePickSelected();
			
			if (remainingCount === 0 && doubleSelected) {
				alert("Success! All of your picks have been successfully made and your Double Pick is active. You are 100% good to go!");
			} else {
				var missing = [];
				if (remainingCount > 0) {
					missing.push("• You still have " + remainingCount + " game(s) left to pick.");
				}
				if (!doubleSelected) {
					missing.push("• You have not selected a Double Pick yet.");
				}
				alert("Picks Verification Incomplete:\n\n" + missing.join("\n") + "\n\nPlease complete these before you are fully locked in!");
			}
		};
	});

	app.controller('ViewPicksController', function ($scope, $http, $log, $q, leagueService) {
		$scope.viewPicksGrid = [];
		$scope.transposedColumns = [];
		$scope.transposedRows = [];
		$scope.playerStandings = [];
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
			
			var gamesPromise = leagueService.getGames(weekId);
			var viewPicksPromise = $http.get('/viewpicks/leagueid/' + leagueId + '/weekid/' + weekId);

			$q.all([gamesPromise, viewPicksPromise]).then(function(results) {
				var games = results[0];
				var viewPicksResponse = results[1];
				var viewPicksData = viewPicksResponse.data;

				$scope.viewPicksGrid = viewPicksData;

				if (!viewPicksData || viewPicksData.length === 0 || !viewPicksData[0] || viewPicksData[0].length === 0) {
					$scope.transposedColumns = [];
					$scope.transposedRows = [];
					$scope.playerStandings = [];
					$scope.loading = false;
					return;
				}

				// Helper to parse long date "Sun 06-28-2026 09:00:00 AM" to short structures
				function makeCompactTime(formattedTime) {
					if (!formattedTime) return { day: '', date: '', time: '' };
					var parts = formattedTime.split(' ');
					if (parts.length < 4) return { day: formattedTime, date: '', time: '' };
					
					var day = parts[0]; // e.g. "Sun"
					var dateStr = parts[1]; // e.g. "06-28-2026"
					var timeStr = parts[2]; // e.g. "09:00:00"
					var ampm = parts[3]; // e.g. "AM"
					
					// Convert "06-28-2026" to "6/28"
					var dateParts = dateStr.split('-');
					var compactDate = '';
					if (dateParts.length >= 2) {
						compactDate = parseInt(dateParts[0], 10) + '/' + parseInt(dateParts[1], 10);
					}
					
					// Convert "09:00:00" to "9:00 AM" or "9 AM"
					var timeParts = timeStr.split(':');
					var compactTime = '';
					if (timeParts.length >= 2) {
						var hour = parseInt(timeParts[0], 10);
						var minute = timeParts[1];
						if (minute === '00') {
							compactTime = hour;
						} else {
							compactTime = hour + ':' + minute;
						}
					}
					
					return {
						day: day,
						date: compactDate,
						time: compactTime + ' ' + ampm
					};
				}

				// Build a map of gameId to game details
				var gameMap = {};
				if (games && games.length) {
					for (var g = 0; g < games.length; g++) {
						var game = games[g];
						gameMap[game.id] = game;
					}
				}

				var originalRowsCount = viewPicksData.length;
				var originalColsCount = viewPicksData[0].length;

				// 1. Build game columns (transposed columns)
				var colHeaders = [
					{ value: 'Player', isPlayerHeader: true }
				];

				for (var r = 1; r < originalRowsCount; r++) {
					var originalRow = viewPicksData[r];
					var gameId = null;
					// Search for gameId on columns 1 to N
					for (var c = 1; c < originalRow.length; c++) {
						if (originalRow[c] && originalRow[c].gameId) {
							gameId = originalRow[c].gameId;
							break;
						}
					}

					var gameObj = gameId ? gameMap[gameId] : null;
					var matchup = originalRow[0] ? originalRow[0].value : 'Game';

					var hasStarted = gameObj ? gameObj.hasGameStarted : false;
					var scoresEntered = gameObj ? (gameObj.hasScoresEntered || (gameObj.favScore !== 0 || gameObj.dogScore !== 0)) : false;

					colHeaders.push({
						gameId: gameId,
						matchup: matchup,
						favShortName: gameObj ? gameObj.favShortName : '',
						dogShortName: gameObj ? gameObj.dogShortName : '',
						gameStartFormated: gameObj ? gameObj.gameStartFormated : '',
						gameStartShort: makeCompactTime(gameObj ? gameObj.gameStartFormated : ''),
						hasGameStarted: hasStarted,
						hasScoresEntered: scoresEntered,
						favScore: gameObj ? gameObj.favScore : 0,
						dogScore: gameObj ? gameObj.dogScore : 0,
						spread: gameObj ? gameObj.spread : 0.5
					});
				}
				$scope.transposedColumns = colHeaders;

				// 2. Build player rows (transposed rows)
				var playerRows = [];
				for (var c = 1; c < originalColsCount; c++) {
					var playerHeaderCell = viewPicksData[0][c];
					var headerVal = playerHeaderCell ? playerHeaderCell.value : '';
					var playerName = headerVal;
					var parenIndex = headerVal.indexOf('(');
					if (parenIndex !== -1) {
						playerName = headerVal.substring(0, parenIndex);
					}

					var playerRow = {
						playerName: playerName,
						picks: [],
						totalPoints: 0,
						totalWins: 0
					};

					for (var r = 1; r < originalRowsCount; r++) {
						var pickCell = viewPicksData[r][c];
						var colHeaderObj = colHeaders[r]; // mapped 1-to-1 index with transposedColumns

						var pickObj = {
							value: pickCell ? pickCell.value : '-',
							attribute: pickCell ? pickCell.attribute : 'ns',
							gameId: colHeaderObj.gameId,
							playerId: playerHeaderCell ? playerHeaderCell.playerId : '',
							hasGameStarted: colHeaderObj.hasGameStarted,
							hasScoresEntered: colHeaderObj.hasScoresEntered,
							isDoublePick: pickCell ? (pickCell.attribute === 'dw' || pickCell.attribute === 'dl') : false,
							isWin: pickCell ? (pickCell.attribute === 'w' || pickCell.attribute === 'dw') : false,
							isLoss: pickCell ? (pickCell.attribute === 'l' || pickCell.attribute === 'dl') : false
						};

						playerRow.picks.push(pickObj);

						// Points summation (1 for win, 2 for double pick win)
						if (pickObj.hasScoresEntered && pickObj.isWin) {
							if (pickObj.isDoublePick) {
								playerRow.totalWins += 2;
								playerRow.totalPoints += 2;
							} else {
								playerRow.totalWins += 1;
								playerRow.totalPoints += 1;
							}
						}
					}

					playerRows.push(playerRow);
				}

				// Sort player rows descending by totalWins (primary) and totalPoints (secondary)
				playerRows.sort(function(a, b) {
					if (b.totalWins !== a.totalWins) {
						return b.totalWins - a.totalWins;
					}
					return b.totalPoints - a.totalPoints;
				});

				$scope.transposedRows = playerRows;

				// 3. Build player standings list sorted descending
				var standings = [];
				for (var p = 0; p < playerRows.length; p++) {
					standings.push({
						playerName: playerRows[p].playerName,
						totalPoints: playerRows[p].totalPoints
					});
				}
				standings.sort(function(a, b) {
					return b.totalPoints - a.totalPoints;
				});
				$scope.playerStandings = standings;

				$scope.loading = false;
			}, function(err) {
				$log.error('Error loading view picks transpose data:', err);
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

		$scope.calculateWinnings = function() {
			if (!$scope.standings || !$scope.standings.length || !$scope.weekWinners) {
				return;
			}
			
			var N = $scope.standings.length;
			// Weekly payout is $100 if N >= 20, else scaled to $5 * N
			var W = Math.min(100, 5 * N);
			
			// Season pool is the remaining pot after 17 weeks of payouts
			var seasonPool = N >= 20 ? (N * 100 - 1700) : (15 * N);
			
			var winningsMap = {};
			angular.forEach($scope.standings, function(player) {
				winningsMap[player.playerId] = 0;
			});
			
			// 1. Process weekly winners and split tied prizes
			angular.forEach($scope.weekWinners, function(weekData) {
				if (!weekData.winners) return;
				// Format is typically: "player1, player2 (12 wins)" or "No games completed yet"
				var winTextIndex = weekData.winners.indexOf(' (');
				if (winTextIndex !== -1) {
					var winnersPart = weekData.winners.substring(0, winTextIndex);
					var winners = winnersPart.split(',').map(function(s) { return s.trim(); });
					var validWinners = winners.filter(function(w) { return w.length > 0; });
					if (validWinners.length > 0) {
						var splitPrize = W / validWinners.length;
						angular.forEach(validWinners, function(winnerId) {
							if (winningsMap[winnerId] !== undefined) {
								winningsMap[winnerId] += splitPrize;
							} else {
								winningsMap[winnerId] = splitPrize;
							}
						});
					}
				}
			});
			
			// 2. Process season-end prizes (1st: 60%, 2nd: 30%, 3rd: 10%) with tie splitting
			var placeGroups = {};
			angular.forEach($scope.standings, function(player) {
				var p = player.place;
				if (!placeGroups[p]) {
					placeGroups[p] = [];
				}
				placeGroups[p].push(player);
			});
			
			var uniquePlaces = Object.keys(placeGroups).map(Number).sort(function(a, b) { return a - b; });
			var shares = [0.60, 0.30, 0.10];
			var currentShareIndex = 0;
			
			angular.forEach(uniquePlaces, function(place) {
				var playersInPlace = placeGroups[place];
				var numPlayers = playersInPlace.length;
				
				var totalShareForPlace = 0;
				for (var i = 0; i < numPlayers; i++) {
					if (currentShareIndex < shares.length) {
						totalShareForPlace += shares[currentShareIndex];
						currentShareIndex++;
					}
				}
				
				if (totalShareForPlace > 0) {
					var individualShare = totalShareForPlace / numPlayers;
					var prize = individualShare * seasonPool;
					angular.forEach(playersInPlace, function(player) {
						winningsMap[player.playerId] += prize;
					});
				}
			});
			
			// Map winnings back to standings array
			angular.forEach($scope.standings, function(player) {
				player.winnings = winningsMap[player.playerId] || 0;
			});
		};

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
					$scope.calculateWinnings();
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
					$scope.calculateWinnings();
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