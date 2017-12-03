set BASE_DIR=C:/Documents/Workspace/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

set request_token=%1

echo on

echo %request_token%>C:/Users/Jayander/Dropbox/Million/KiteTokens/request.token

java com.million.kite.login.TokenManager