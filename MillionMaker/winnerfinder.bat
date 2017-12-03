set BASE_DIR=C:/Documents/Workspace/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

echo on

del C:\Users\Jayander\Dropbox\Million\stop
del C:\Documents\Workspace\MillionMaker\stop

java com.million.AlertManager 16