set BASE_DIR=C:/Users/Jayander/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\Jayander\Dropbox\Million\stop
del C:\Users\Jayander\git\MillionMaker\MillionMaker\stop

start "Application" javaw com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-NIFTY.CSV -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=0.25

timeout 60 > nul

rem java com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-INTRADAY.csv,C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=1

java com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv,C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-IIT.csv -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=1