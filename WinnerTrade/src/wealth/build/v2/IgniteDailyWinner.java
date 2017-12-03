package wealth.build.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class IgniteDailyWinner {
	
	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "daily-winner.csv",
				"DATE","STOCK-NAME","PRICE","REMARKS");
		
		Set<String> FnOSet = new HashSet<>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(
				new File(WealthConfig.getInstance().getProperty("MASTER_DATA_LOC"),"FnO.csv"))))	{
			
			String line;
			
            while ((line = reader.readLine()) != null) {
            	
            	FnOSet.add(line);
            }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Stock> allStocks = hUtil.getStockVO(0);

		Iterator<String> stockIterator = allStocks.keySet().iterator();
		
		while(stockIterator.hasNext())	{
			
			try {
				String stockName = stockIterator.next();
				
				
				if(!FnOSet.contains(stockName))	{
					continue;
				}
				Stock todayVO = allStocks.get(stockName);
				
				findBullStockToTrade(todayVO);
				findBearStockToTrade(todayVO);

			}catch(Exception e)	{
				
			}
		}
	}

	private static void findBullStockToTrade(Stock todayVO) throws FileNotFoundException, IOException {
		
		if(!hUtil.isStockMovingUp(todayVO.getName()))	{
			return;
		}
		
		float twoHundredDMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 200);
		
		if(todayVO.getClosePrice() < twoHundredDMA)	{
			
			return;
		}
		
		float fiftyDMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 50);
		
		if(todayVO.getClosePrice() < fiftyDMA)	{
			
			return;
		}
		
		if( todayVO.getChangePercentage() < 0 
				&& hUtil.getStockMovementWRTToday(todayVO.getName(), 10, false) > 10)	{
			
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),
					HelperUtil.getRoundedNumber(todayVO.getClosePrice()),"BULL");
		}
		
	}
	
	private static void findBearStockToTrade(Stock todayVO) throws FileNotFoundException, IOException {
		
		if(hUtil.isStockMovingUp(todayVO.getName()))	{
			return;
		}
		
		float twoHundredDMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 200);
		
		if(todayVO.getClosePrice() > twoHundredDMA)	{
			
			return;
		}
		
		float fiftyDMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 50);
		
		if(todayVO.getClosePrice() > fiftyDMA)	{
			
			return;
		}
		System.out.println(todayVO.getName() + "-" + todayVO.getChangePercentage() 
				+"-" + hUtil.getStockMovementWRTToday(todayVO.getName(), 10, true));
		
		if( todayVO.getChangePercentage() > 0 
				&& hUtil.getStockMovementWRTToday(todayVO.getName(), 10, true) > 5)	{
			
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),
					HelperUtil.getRoundedNumber(todayVO.getClosePrice()),"BEAR");
		}
		
	}

}
