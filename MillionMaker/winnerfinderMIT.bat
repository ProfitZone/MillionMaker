set BASE_DIR=C:/Users/Jayander/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\Jayander\Dropbox\Million\stop
del C:\Users\Jayander\git\MillionMaker\MillionMaker\stop

java com.million.BaseAlertManager -inputFile=C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=30 -alertRange=2