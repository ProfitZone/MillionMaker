package wealth.build;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class WinnerAboveMA {

	
	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null; 
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {			
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "ma-winner.csv",
				"DATE","STOCK-NAME","PRICE","CHANGE%","MA","ABOVE-MA%","VOLUME%");
		
		printAboveMA(50);
		
		printAboveMA(200);
	}
	
	public static void printAboveMA(int nDays) throws IOException	{
		
		Map<String, Stock> allStocks = hUtil.getStockVO(0);

		Iterator<String> stockIterator = allStocks.keySet().iterator();
		
		while(stockIterator.hasNext())	{
			
			try {
				String stockName = stockIterator.next();
				
				Stock todayVO = allStocks.get(stockName);
				
				float averageVolume = hUtil.getAverageVolumeforNDays(stockName, 60);
				
				if(averageVolume * todayVO.getClosePrice() < 5000000)	{
					continue;
				}
				if(todayVO.getChangePercentage() < 5){
					continue;
				}
				
				float movingAverage = hUtil.getSimpleMovingAverage(stockName, nDays);
				
				if(!HelperUtil.isBetween(movingAverage,todayVO.getOpenPrice(),todayVO.getClosePrice())){
					continue;
				}
				float diffPercentage = HelperUtil.getPercentageChanged(movingAverage, todayVO.getClosePrice(), movingAverage);
				if(diffPercentage < 2)	{
					continue;
				}
				float volumeToday = HelperUtil.getPercentageChanged(averageVolume, todayVO.getQuantityTraded(), averageVolume);
				
				if(volumeToday < 300)	{
					continue;
				}
				
				System.out.println(todayVO.getName());
				
				csvWritter.write(HelperUtil.getStringDate(),stockName,HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(movingAverage),
						HelperUtil.getRoundedNumber(diffPercentage),HelperUtil.getRoundedNumber(volumeToday),nDays+"-Days");
				
			}catch(Exception e)	{
				//e.printStackTrace();
			}
		}
		
	}

}
