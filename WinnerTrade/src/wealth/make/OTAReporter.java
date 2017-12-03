package wealth.make;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class OTAReporter {
	
	static private String INPUT_FILE_NAME = "C:/Users/Jayander/Dropbox/Million/Winners/OTA-WATCHLIST.csv";
	
	private static HistoricUtil hUtil = null;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		
		CsvWritter csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), 
				"ota-winner-" + dateFormat.format(date) +".csv","DATE","STOCK-NAME");
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(INPUT_FILE_NAME)))){
			
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("SCRIP_NAME"))	{
					continue;
				}
				
				String []values = line.split(",");
				String scripName = values[0].trim();
				try	{
					float recoPrice = Float.valueOf(values[1]);
					
					Stock stock = hUtil.getStockVO(scripName, 0);
					
					if(HelperUtil.isWithinRange(stock.getClosePrice(), recoPrice, 1)){
						csvWritter.write(HelperUtil.getStringDate(),stock.getName());
					}
				}catch(NullPointerException npe)	{
					csvWritter.write(HelperUtil.getStringDate(),scripName,"Scrip not found");
				}
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
