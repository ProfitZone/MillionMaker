set BASE_DIR=C:/Users/Jayander/git/MillionMaker/WinnerTrade

del %BASE_DIR%/history/cm*.zip

echo "Deleted old zips"

cd %BASE_DIR%

java -cp %BASE_DIR%/bin wealth.file.HttpDownloadUtility

echo "File downloaded"

cd %BASE_DIR%/history 

jar xf cm*.zip 

echo "File unzipped"

del cm*.zip

echo "Deleted today zip"

cd %BASE_DIR%

java -cp %BASE_DIR%/bin wealth.make.WealthCreator

set FO_DOWNLOAD_DIR=%BASE_DIR%/historyFO

del %FO_DOWNLOAD_DIR%/fo*.zip

echo "Deleted old F&0 zips"

cd %BASE_DIR%

java -cp %BASE_DIR%/bin wealth.file.FOFileDownloader

echo "FO File downloaded"

cd %FO_DOWNLOAD_DIR%

jar xf %FO_DOWNLOAD_DIR%/fo*.zip 

echo "FO File unzipped"

del fo*.zip

echo "Deleted today FO zip"

cd %BASE_DIR%

REM java -cp %BASE_DIR%/bin wealth.build.v2.ReversalFinderFO