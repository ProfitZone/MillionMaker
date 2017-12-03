package wealth.build.v2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.FOHistoricUtil;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class ReversalFinderFO {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		FOHistoricUtil foHistoricUtil = 
				new FOHistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FO_FILES_STORED_LOC"));
		
		HistoricUtil hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		CsvWritter csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "fo-winner.csv",
				"DATE","STOCK-NAME","OI-PERCENTAGE","STOCK-MOVEMENT","REMARKS");
		
		csvWritter.write("");
		
		try {
			Map<String, Stock> stokMap = foHistoricUtil.getStockVO(0);
			
			Iterator<String> iterator = stokMap.keySet().iterator();
			
			while(iterator.hasNext()){
				
				try {
					Stock stock = stokMap.get(iterator.next());
					
					float openInterestChange = foHistoricUtil.getChangeInOIForNDays(stock.getName(), 1);
					
					if(openInterestChange > 5){
						
						csvWritter.write(HelperUtil.getStringDate(),stock.getName(),HelperUtil.getRoundedNumber(openInterestChange),
								HelperUtil.getRoundedNumber(hUtil.getStockMovementForNDays(stock.getName(), 5)),"1-DAY");
					}
					
					openInterestChange = foHistoricUtil.getChangeInOIForNDays(stock.getName(), 2);
					
					if(openInterestChange > 5){
						
						csvWritter.write(HelperUtil.getStringDate(),stock.getName(),HelperUtil.getRoundedNumber(openInterestChange),
								HelperUtil.getRoundedNumber(hUtil.getStockMovementForNDays(stock.getName(), 5)),"2-DAY");
					}
				}catch(Exception e)	{
					
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
