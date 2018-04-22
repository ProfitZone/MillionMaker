import java.io.IOException;

import com.million.common.Constants;
import com.million.csv.CSVReader;

public class CSVReaderTest {

	public static void main(String[] args) throws IOException {
		
		CSVReader csvReader = new CSVReader("/Users/jayanderk/Dropbox/Million/Winners/OTA-WATCHLIST-INTRADAY.csv");
		
		String []stocks = csvReader.getAllScrips();
		
		for(String stock : stocks)	{
			
			System.out.println("Stock is - " + stock);
			
			System.out.println("Entry level is - " + csvReader.getFloatValue(stock, Constants.FIELD_NAME_ENTRY_PRICE_LEVEL));
			
			System.out.println("OTA trade type is - " + csvReader.getValue(stock, Constants.FIELD_NAME_OTA_TRADE_TYPE));
			
			System.out.println("QUANTITY is - " + csvReader.getIntValue(stock, Constants.FIELD_NAME_QUANTITY));
			
		}
		
		

	}

}
