package wealth.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import wealth.config.WealthConfig;

public class FOFileDownloader {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String fileURL = "https://www.nseindia.com/content/historical/DERIVATIVES/";
        
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMYYYY");
		
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.DATE, -2);
		
		String prefix = simpleDateFormat.format(cal.getTime());
		
		String fileName = "fo" + prefix.toUpperCase() + "bhav.csv.zip";
		
		String year =  new SimpleDateFormat("YYYY").format(cal.getTime());
		
		String month = new SimpleDateFormat("MMM").format(cal.getTime()).toUpperCase();
		
		fileURL = fileURL + year + "/" + month +"/" + fileName;
		
		System.out.println(fileURL);
		
        String saveDir = WealthConfig.getInstance().getProperty("DAILY_FO_FILES_STORED_LOC");
        
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
