set BASE_DIR=C:/Users/admin/git/MillionMaker/MillionMaker

set CLASSPATH=%CLASSPATH%/;%BASE_DIR%/bin;%BASE_DIR%/lib/*

cd %BASE_DIR%

set userID=%1
set request_token=%2

echo on

echo %request_token%>C:/Users/admin/Dropbox/Million/KiteTokens/%userID%.request.token

java com.million.kite.login.TokenManager %userID%