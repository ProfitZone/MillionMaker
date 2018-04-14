set BASE_DIR=C:/Users/Jayander/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\Jayander\Dropbox\Million\stop
del C:\Users\Jayander\git\MillionMaker\MillionMaker\stop

java com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-INTRADAY.csv,C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

java com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=2