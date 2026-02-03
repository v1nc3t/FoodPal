@echo off

echo Starting FoodPal server...
start "" mvn -pl server -am spring-boot:run

echo Waiting for server to start...
timeout /t 15 > nul

echo Starting FoodPal client...
start "" mvn -pl client -am javafx:run