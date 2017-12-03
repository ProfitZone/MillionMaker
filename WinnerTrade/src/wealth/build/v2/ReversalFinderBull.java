package wealth.build.v2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class ReversalFinderBull {
	
	private static HistoricUtil hUtil = null;
	
	private static CsvWritter csvWritter = null;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"), "bull-winner.csv",
				"DATE","STOCK-NAME","PRICE","REMARKS");
		
		csvWritter.write("");
		Map<String, Stock> allStocks = hUtil.getStockVO(0);

		Iterator<String> stockIterator = allStocks.keySet().iterator();
		
		WealthConfig.getInstance();
		
		while(stockIterator.hasNext())	{
			
			try {
				String stockName = stockIterator.next();
				
				Stock todayVO = allStocks.get(stockName);
				
				float averageVolume = hUtil.getAverageVolumeforNDays(stockName, 60);
				
				if((todayVO.getClosePrice() *  averageVolume) < 5000000 /* 50 Lakh */ || todayVO.getClosePrice() < 10)	{
					continue;
				}
				
				csvWritter.setDontWrite(false);
				
				nearDMAScripts(todayVO);
				
				nearWeeklyDMA(todayVO);
				
				nearMonthlyDMA(todayVO);
				
				//stoppedFallingScript(todayVO);
				
				//recoveryFromBottom(todayVO);
				
				//priceBreakout(todayVO);
				
			}catch(Exception e)	{
				//e.printStackTrace();
			}
		}
	}
	
	private static void nearMonthlyDMA(Stock todayVO) throws FileNotFoundException, IOException {
		
		float fiftyDayWeeklyMA = hUtil.getMonthlyMovingAverage(todayVO.getName(), 50);
		
		float fallenPer = hUtil.getStockMovementWRTToday(todayVO.getName(), 132,false);
		
		if(HelperUtil.isWithinRange(fiftyDayWeeklyMA, todayVO.getClosePrice(), 3) && fallenPer > 30)	{
			
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					"Near 50 Monthly-DMA");
		}
		
	}

	private static void nearWeeklyDMA(Stock todayVO) throws FileNotFoundException, IOException {
		
		float fiftyDayWeeklyMA = hUtil.getWeeklyMovingAverage(todayVO.getName(), 50);
		
		float fallenPer = hUtil.getStockMovementWRTToday(todayVO.getName(), 22,false);
		
		if(HelperUtil.isWithinRange(fiftyDayWeeklyMA, todayVO.getClosePrice(), 2) && fallenPer > 20)	{
			
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					"Near 50 Weekly-DMA");
		}
		
		float Two100DayWeeklyMA = hUtil.getWeeklyMovingAverage(todayVO.getName(), 200);
		
		fallenPer = hUtil.getStockMovementWRTToday(todayVO.getName(), 22,false);
		
		if(HelperUtil.isWithinRange(Two100DayWeeklyMA, todayVO.getClosePrice(), 2) && fallenPer > 20)	{
			
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					"Near 200 Weekly-DMA");
		}
		
	}

	private static void priceBreakout(Stock todayVO) throws FileNotFoundException, IOException {
		
		if(todayVO.getChangePercentage() > 5)	{
			
				System.out.println("$$$$$$$$ ->" + todayVO.getName());
			
		}
		
	}

	private static void nearDMAScripts(Stock todayVO) throws FileNotFoundException, IOException {
		
		String stockName = todayVO.getName();
		
		/* Find all the stocks which are close to 200 DMA.
		 * They are in up trend
		 * They have fallen down more than 5% is last 5 days.
		 * 
		 */
		float two100DMA = hUtil.getSimpleMovingAverage(stockName, 200);
		
		if(todayVO.getLowPrice() <= two100DMA && todayVO.getClosePrice() > two100DMA)	{
			if(hUtil.isStockMovingUp(stockName))	{
				float highForNdays = hUtil.getHighforNDays(stockName, 15);
				if(highForNdays > todayVO.getClosePrice() && hUtil.getStockMovementWRTToday(stockName, 15, false) > 10)	{
					System.out.println("**" + stockName + "-" + todayVO.getClosePrice() + "-" + highForNdays + "-" + two100DMA);
					csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
							"Near 200 DMA");
				}
				
			}
		}
		
		/* Find all the stocks which are close to 200 DMA.
		 * They are in up trend
		 * They have fallen down more than 5% is last 5 days.
		 * 
		 */
		float fiftyDMA = hUtil.getSimpleMovingAverage(stockName, 50);
		
		if(todayVO.getLowPrice() <= fiftyDMA && todayVO.getClosePrice() > fiftyDMA)	{
			
			if(hUtil.isStockMovingUp(stockName))	{
				
				float highForNdays = hUtil.getHighforNDays(stockName, 5);
				if(highForNdays > todayVO.getClosePrice() && hUtil.getStockMovementWRTToday(stockName, 5, false) > 5)	{
					System.out.println(stockName + "-" + todayVO.getClosePrice() + "-" + highForNdays + "-" + fiftyDMA);
					csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
							"Near 50 DMA");
				}
				
			}
		}
		
	}

	private static void recoveryFromBottom(Stock todayVO) throws FileNotFoundException, IOException {
		
		float fallenPer = hUtil.getStockMovementWRTToday(todayVO.getName(), 100, false);
		if( fallenPer > 30 && fallenPer < 110)	{
			
			float averageVolumeWeek = hUtil.getAverageVolumeforNDays( todayVO.getName(), 3);
			float averageVolumeMonth = hUtil.getAverageVolumeforNDays( todayVO.getName(), 20);
			
			float volumePer = HelperUtil.getPercentageChanged(averageVolumeMonth, averageVolumeWeek, averageVolumeMonth);
			
			if(HelperUtil.getPercentageChanged(averageVolumeMonth, averageVolumeWeek, averageVolumeMonth) > 100)	{
				System.out.println(fallenPer + "~~~~" + todayVO.getName() + "~~~~~" + volumePer);
			}
		}
		
	}

	private static void stoppedFallingScript(Stock todayVO) throws FileNotFoundException, IOException {
		
		WealthConfig wc = WealthConfig.getInstance();
		
		if(wc.isDayTradingStock(todayVO.getName()) 
				&& hUtil.getStockMovementWRTToday(todayVO.getName(), 10, false) > 7
				&& hUtil.isStockMovingUp(todayVO.getName())
				&& todayVO.getChangePercentage() < 0)	{
			csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
					"Stopped Falling");
		}
		if(wc.isDayTradingStock(todayVO.getName()) && todayVO.getOpenPrice() < todayVO.getPrevClosePrice() 
				&& HelperUtil.getPercentageChanged(todayVO.getOpenPrice(), todayVO.getClosePrice(), todayVO.getOpenPrice()) < 0.5f 
				&& HelperUtil.getPercentageChanged(todayVO.getLowPrice(),todayVO.getHighPrice(),todayVO.getLowPrice()) < 1.5f 
				&& todayVO.getClosePrice() < todayVO.getPrevClosePrice() )	{
			
			if(hUtil.isStockMovingUp(todayVO.getName()) && hUtil.getStockMovementWRTToday(todayVO.getName(), 5, false) > 5)	{
				System.out.println(" ########  " + todayVO.getName());
				csvWritter.write(HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
						"Stopped Falling");
			}
		}
	}

}
