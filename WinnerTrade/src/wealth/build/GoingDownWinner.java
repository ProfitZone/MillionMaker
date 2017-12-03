package wealth.build;

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

public class GoingDownWinner {

	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "gd-winner.csv",
				"DATE","STOCK-NAME","PRICE","CHANGE%","MOVE-N-DAYS");
		

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
				
				Stock todayVO = allStocks.get(stockName);
				
				if(FnOSet.contains(todayVO.getName()))	{
					
					float twoHundredDayMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 200);
					
					float olderTwoHundredDayMA = hUtil.getOlderSimpleMovingAverage(todayVO.getName(), 200, 60);
					
					if(olderTwoHundredDayMA > twoHundredDayMA)	{
						//System.out.println(todayVO.getName());
						if(twoHundredDayMA > todayVO.getClosePrice() && todayVO.getChangePercentage() > 0 && 
								HelperUtil.getPercentageChanged(twoHundredDayMA, todayVO.getClosePrice(), twoHundredDayMA) < 2)	{
							
							csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
									HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(twoHundredDayMA),"200-DMA");
						}
					}
					
					
					if(todayVO.getOpenPrice() > twoHundredDayMA && todayVO.getClosePrice() < twoHundredDayMA 
							&& todayVO.getChangePercentage() < -3.0)	{
						
						csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
								HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(twoHundredDayMA),"200-DMA");
					} 
					
					float fiftyDayDMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 50);
					
					//float olderfiftyDayDMA = hUtil.getOlderSimpleMovingAverage(todayVO.getName(), 50, 30);
					
					if(olderTwoHundredDayMA > twoHundredDayMA)	{
						if(fiftyDayDMA > todayVO.getClosePrice() && todayVO.getChangePercentage() > 0 && 
								HelperUtil.getPercentageChanged(fiftyDayDMA, todayVO.getClosePrice(), fiftyDayDMA) < 2)	{
							
							csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
									HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(fiftyDayDMA),"50-DMA");
						}
					}
					
					if(todayVO.getOpenPrice() > fiftyDayDMA && todayVO.getClosePrice() < fiftyDayDMA 
							&& todayVO.getChangePercentage() < -3.0)	{
						
						csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
								HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(fiftyDayDMA),"50-DMA");
					} 
					
				}
			}catch(Exception e)	{
				
			}
			
			
			
		}
	}

}
