package wealth.build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class AccumulationWinner {

	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "ac-winner.csv",
				"DATE","STOCK-NAME","PRICE","CHANGE%","VOLUME%","MOVE-N-DAYS");
		
		Map<String, Stock> allStocks = hUtil.getStockVO(0);

		Iterator<String> stockIterator = allStocks.keySet().iterator();
		
		csvWritter.write("");
		
		while(stockIterator.hasNext())	{
			
			try {
				String stockName = stockIterator.next();
				
				Stock todayVO = allStocks.get(stockName);
				
				float perChangedToday = todayVO.getChangePercentage();
				
				//If today's movement is less than 5% , move to next.
				if(perChangedToday < 5)	{
					continue;
				}
				//IF closing price was less than opening price , move to next.
				if(todayVO.getClosePrice() < todayVO.getOpenPrice())	{
					continue;
				}
				
				//IF closing price was less than previous day close price , move to next.
				if(todayVO.getClosePrice() < todayVO.getPrevClosePrice())	{
					continue;
				}
				
				int duration = 60;
				
				float high30Days = hUtil.getHighforNDays(stockName, duration);
				
				float low30Days = hUtil.getLowforNDays(stockName, duration);
				
				if(todayVO.getClosePrice() < high30Days){
					continue;
				}
				
				float todayAbove60DayshighPer = HelperUtil.getPercentageChanged(high30Days, todayVO.getClosePrice(), high30Days);
				
				if(todayAbove60DayshighPer > 10)	{
					continue;
				}
				
				float todayAbove60DaysLowPer = HelperUtil.getPercentageChanged(low30Days, todayVO.getClosePrice(), low30Days);
				
				if(todayAbove60DaysLowPer > 30){
					continue;
				}
				
				float averageVolume = hUtil.getAverageVolumeforNDays(stockName, 60);
				
				if((todayVO.getClosePrice() *  averageVolume) < 5000000)	{
					continue;
				}
				
				float volumeToday = HelperUtil.getPercentageChanged(averageVolume, todayVO.getQuantityTraded(), averageVolume);
				
				if(volumeToday < 500){
					continue;
				}
				csvWritter.write(HelperUtil.getStringDate(),stockName,HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					HelperUtil.getRoundedNumber(todayVO.getChangePercentage()),HelperUtil.getRoundedNumber(volumeToday),
						HelperUtil.getRoundedNumber(todayAbove60DayshighPer));
				
			}catch(Exception e)	{
				
			}
		}
	}

}
