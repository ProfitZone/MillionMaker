set BASE_DIR=C:/Users/Jayander/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\Jayander\Dropbox\Million\stop
del C:\Users\Jayander\git\MillionMaker\MillionMaker\stop

java -DWriteInCSV=false com.million.BaseAlertManager  -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-NIFTY.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=0.25

REM java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-INTRADAY.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

REM java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST-WIT-LT.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=2