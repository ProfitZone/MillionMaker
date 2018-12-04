set BASE_DIR=C:/Users/admin/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

set STORAGE_BASE_DIR=C:/Users/admin/Dropbox

cd %BASE_DIR%

echo on

del C:\Users\admin\Dropbox\Million\stop
del C:\Users\admin\git\MillionMaker\MillionMaker\stop

echo %STORAGE_BASE_DIR%

java -DWriteInCSV=false com.million.BaseAlertManager  -inputFile=%STORAGE_BASE_DIR%/Million/Winners/OTA-WATCHLIST-NIFTY.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=0.25

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-INTRADAY.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-WIT.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=2

java -DWriteInCSV=false com.million.BaseAlertManager -inputFile=C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT-YAMUNA.csv,C:/Users/admin/Dropbox/Million/Winners/OTA-WATCHLIST-DIT-Vishwas.csv -repeatRuns=1 -stopAfterHours=23 -runEveryXMinutes=1 -alertRange=1