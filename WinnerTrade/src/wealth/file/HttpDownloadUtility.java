package wealth.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import wealth.config.WealthConfig;
 
/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net
 *
 */
public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;
 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);

        
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        
        
        //httpConn.addRequestProperty("User-Agent", 
        	//	"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode != HttpURLConnection.HTTP_SERVER_ERROR) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String fileURL = "https://www.nseindia.com/content/historical/EQUITIES/";
        
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMYYYY");
		
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, -1);
		
		String prefix = simpleDateFormat.format(cal.getTime());
		
		String fileName = "cm" + prefix.toUpperCase() + "bhav.csv.zip";
		
		String year =  new SimpleDateFormat("YYYY").format(cal.getTime());
		
		String month = new SimpleDateFormat("MMM").format(cal.getTime()).toUpperCase();
		
		fileURL = fileURL + year + "/" + month +"/" + fileName;
		
		System.out.println(fileURL);
		
        String saveDir = WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC");
        
        if(args.length > 0){
        	saveDir = args[0];
        }
        
        try {
            HttpDownloadUtility.downloadFile(fileURL, saveDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}