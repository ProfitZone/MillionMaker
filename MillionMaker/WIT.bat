set BASE_DIR=C:/Users/admin/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

set STORAGE_BASE_DIR=C:/Users/admin/Dropbox

cd %BASE_DIR%

echo OFF

del %STORAGE_BASE_DIR%/Million/stop
del stop

cls

echo ---------------------------------------------------------
echo -            THIS IS WIT - WEEKLY INCOME TRADE          -
echo ---------------------------------------------------------

java com.million.BaseAlertManager -inputFile=%STORAGE_BASE_DIR%/Million/Winners/OTA-WATCHLIST-WIT.csv,%STORAGE_BASE_DIR%/Million/Winners/OTA-WATCHLIST-WIT-YAMUNA.csv,%STORAGE_BASE_DIR%/Million/Winners/OTA-WATCHLIST-WIT-Vishwas.csv -repeatRuns=-1 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=1

