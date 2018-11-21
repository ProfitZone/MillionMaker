set BASE_DIR=C:/Users/admin/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\admin\Dropbox\Million\stop
del C:\Users\admin\git\MillionMaker\MillionMaker\stop

start "Application" javaw com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-NIFTY.CSV -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=0.25

timeout 60 > nul

cls

java com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv,C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT.csv,C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT-YAMUNA.csv,C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT-Vishwas.csv -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=1

