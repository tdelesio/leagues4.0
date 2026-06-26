#!/bin/bash
set -e

SRC="/Users/tim/.gemini/antigravity/scratch/leagues_ms_src"
DEST="/Users/tim/.gemini/antigravity/scratch/leagues_mono/src/main/java/com/makeurpicks"

# Ensure target directories exist
mkdir -p $DEST/{domain,repository,service,controller,config,exception}

echo "Copying from auth-server..."
cp $SRC/auth-server/src/main/java/com/makeurpicks/domain/Authority.java $DEST/domain/
cp $SRC/auth-server/src/main/java/com/makeurpicks/dao/PlayerDao.java $DEST/repository/

echo "Copying from league..."
cp $SRC/league/src/main/java/com/makeurpicks/domain/PlayerLeague*.java $DEST/domain/
cp $SRC/league/src/main/java/com/makeurpicks/domain/League*.java $DEST/domain/
cp $SRC/league/src/main/java/com/makeurpicks/domain/Season*.java $DEST/domain/
cp $SRC/league/src/main/java/com/makeurpicks/repository/*Repository.java $DEST/repository/
cp $SRC/league/src/main/java/com/makeurpicks/service/SeasonService.java $DEST/service/
cp $SRC/league/src/main/java/com/makeurpicks/service/LeagueService.java $DEST/service/
cp $SRC/league/src/main/java/com/makeurpicks/controller/SeasonController.java $DEST/controller/
cp $SRC/league/src/main/java/com/makeurpicks/controller/LeagueController.java $DEST/controller/
cp $SRC/league/src/main/java/com/makeurpicks/exception/*Exception.java $DEST/exception/

echo "Copying from game..."
cp $SRC/game/src/main/java/com/makeurpicks/domain/Team*.java $DEST/domain/
cp $SRC/game/src/main/java/com/makeurpicks/domain/Week*.java $DEST/domain/
cp $SRC/game/src/main/java/com/makeurpicks/domain/Game*.java $DEST/domain/
cp $SRC/game/src/main/java/com/makeurpicks/domain/NFL*.java $DEST/domain/
cp $SRC/game/src/main/java/com/makeurpicks/repository/GameRepository.java $DEST/repository/
cp $SRC/game/src/main/java/com/makeurpicks/repository/TeamRepository.java $DEST/repository/
cp $SRC/game/src/main/java/com/makeurpicks/repository/WeekRepository.java $DEST/repository/
cp $SRC/game/src/main/java/com/makeurpicks/service/WeekService.java $DEST/service/
cp $SRC/game/src/main/java/com/makeurpicks/service/GameService.java $DEST/service/
cp $SRC/game/src/main/java/com/makeurpicks/service/TeamService.java $DEST/service/
cp $SRC/game/src/main/java/com/makeurpicks/controller/GameController.java $DEST/controller/
cp $SRC/game/src/main/java/com/makeurpicks/controller/TeamController.java $DEST/controller/
cp $SRC/game/src/main/java/com/makeurpicks/controller/WeekController.java $DEST/controller/
cp $SRC/game/src/main/java/com/makeurpicks/exception/GameValidationException.java $DEST/exception/

echo "Copying from pick..."
cp $SRC/pick/src/main/java/com/makeurpicks/domain/Pick.java $DEST/domain/
cp $SRC/pick/src/main/java/com/makeurpicks/domain/PickRedis.java $DEST/domain/
cp $SRC/pick/src/main/java/com/makeurpicks/domain/PickBuilder.java $DEST/domain/
cp $SRC/pick/src/main/java/com/makeurpicks/domain/DoublePick.java $DEST/domain/
cp $SRC/pick/src/main/java/com/makeurpicks/repository/*Repository.java $DEST/repository/
mkdir -p $DEST/repository/redis
cp $SRC/pick/src/main/java/com/makeurpicks/repository/redis/*.java $DEST/repository/redis/
cp $SRC/pick/src/main/java/com/makeurpicks/service/PickService.java $DEST/service/
cp $SRC/pick/src/main/java/com/makeurpicks/controller/PickController.java $DEST/controller/
cp $SRC/pick/src/main/java/com/makeurpicks/exception/PickValidationException.java $DEST/exception/

echo "Copying from leader..."
cp $SRC/leader/src/main/java/com/makeurpicks/domain/ViewPickColumn.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/domain/LeagueView.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/domain/WeekWinner.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/domain/WinSummary.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/domain/WeekView.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/domain/WeekStats.java $DEST/domain/
cp $SRC/leader/src/main/java/com/makeurpicks/service/LeaderService.java $DEST/service/
cp $SRC/leader/src/main/java/com/makeurpicks/controller/LeaderController.java $DEST/controller/
cp $SRC/leader/src/main/java/com/makeurpicks/exception/LeaderClientException.java $DEST/exception/

echo "Copying from gateway..."
cp $SRC/gateway/src/main/java/com/makeurpicks/controller/GatewayController.java $DEST/controller/
cp $SRC/gateway/src/main/java/com/makeurpicks/service/GatewayService.java $DEST/service/
cp $SRC/gateway/src/main/java/com/makeurpicks/domain/MakePicks.java $DEST/domain/
cp $SRC/gateway/src/main/java/com/makeurpicks/domain/PlayerWins.java $DEST/domain/
cp $SRC/gateway/src/main/java/com/makeurpicks/domain/NavigationView.java $DEST/domain/
cp $SRC/gateway/src/main/java/com/makeurpicks/domain/ViewPicks.java $DEST/domain/

# Copy gateway integration and command services
mkdir -p $DEST/service/gateway
cp -r $SRC/gateway/src/main/java/com/makeurpicks/service/game $DEST/service/gateway/
cp -r $SRC/gateway/src/main/java/com/makeurpicks/service/league $DEST/service/gateway/
cp -r $SRC/gateway/src/main/java/com/makeurpicks/service/pick $DEST/service/gateway/
cp -r $SRC/gateway/src/main/java/com/makeurpicks/service/week $DEST/service/gateway/
cp -r $SRC/gateway/src/main/java/com/makeurpicks/team $DEST/service/gateway/

echo "Copying static assets from UI..."
mkdir -p /Users/tim/.gemini/antigravity/scratch/leagues_mono/src/main/resources/static
cp -r $SRC/ui/src/main/resources/static/* /Users/tim/.gemini/antigravity/scratch/leagues_mono/src/main/resources/static/

echo "Done copying files!"
