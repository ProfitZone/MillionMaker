package wealth.build;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class JustAboveMAWinner {

	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {			
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "ja-ma-winner.csv",
				"DATE","STOCK-NAME","PRICE","CHANGE%","MA");
		
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
				
				float movingAverage = hUtil.getSimpleMovingAverage(stockName, nDays);
				
				if(HelperUtil.isWithinRange(movingAverage, todayVO.getClosePrice(), 3) 
						&& todayVO.getClosePrice() > movingAverage 
						&& todayVO.getLowPrice() <= movingAverage
						&& todayVO.getOpenPrice() > movingAverage)	{
					
					float perChangedlast5days = hUtil.getHighforNDays(stockName, 10);
					
					//System.out.println(stockName + "-" + perChangedlast5days);
					if(HelperUtil.getPercentageChanged(perChangedlast5days, todayVO.getClosePrice(), perChangedlast5days) < 10)	{
						//continue;
					}
					
					
					float low5days = hUtil.getLowforNDays(stockName, 5);
					
					if(HelperUtil.getPercentageChanged(low5days, todayVO.getClosePrice(), low5days)  > 3)	{
						continue;
					}
					
					float high30days = hUtil.getHighforNDays(stockName, 45);
					
					if(HelperUtil.getPercentageChanged(todayVO.getClosePrice(), high30days, todayVO.getClosePrice())  < 10)	{
						continue;
					}
						
					float oldMA = hUtil.getOlderSimpleMovingAverage(stockName, nDays, 50);
					
					if(movingAverage < oldMA)	{
						continue;
					}
					
					csvWritter.write(HelperUtil.getStringDate(),stockName,HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
							HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(movingAverage),nDays+"-Days"); 
				}
				
			}catch(Exception e)	{
				//e.printStackTrace();
			}
		}
		
	}

}
