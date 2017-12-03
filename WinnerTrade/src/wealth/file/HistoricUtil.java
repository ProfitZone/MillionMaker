package wealth.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import wealth.util.HelperUtil;
import wealth.vo.Stock;

public class HistoricUtil {
	
	private String dirName = null;
	
	private Map<String, Map<String, Stock>> stockCache = new HashMap<>();
	
	public HistoricUtil(String dirName){
		
		this.dirName = dirName;
	}
	
	private File getNdaysOldFile(int n){
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMYYYY");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -n);
		
		String prefix = simpleDateFormat.format(cal.getTime());
		
		String fileName = "cm" + prefix.toUpperCase() + "bhav.csv";
		
		File file = new File(this.dirName,fileName);
		
		while(!file.exists())	{
			
			cal.add(Calendar.DATE, -1);
			
			prefix = simpleDateFormat.format(cal.getTime());
			
			fileName = "cm" + prefix.toUpperCase() + "bhav.csv";
			
			file = new File(this.dirName,fileName);
		}
		return file;
	}
	
	private Map<String, Stock> getStockVO(File file) throws FileNotFoundException, IOException{
		
		if(stockCache.get(file.getName()) != null)	{
			return stockCache.get(file.getName());
		}
		Map<String , Stock> stockMap = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String line;
			String cvsSplitBy = ",";
			
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] details = line.split(cvsSplitBy);

                if("SYMBOL".equals(details[0]))	{
                	continue;
                }
                if(!"EQ".equals(details[1]))	{
                	continue;
                }
                
                Stock stock = new Stock();
                
                stock.setName(details[0]);
                stock.setOpenPrice(Float.valueOf(details[2]));
                stock.setHighPrice(Float.valueOf(details[3]));
                stock.setLowPrice(Float.valueOf(details[4]));
                stock.setClosePrice(Float.valueOf(details[5]));
                stock.setPrevClosePrice(Float.valueOf(details[7]));
                stock.setQuantityTraded(Integer.valueOf(details[8]));
                
                stockMap.put(stock.getName(), stock);

            }
		}
		stockCache.put(file.getName(), stockMap);
		return stockMap;
		
	}

	public Stock getStockVO(String symbol , int nDays) throws FileNotFoundException, IOException	{
		
		File file = getNdaysOldFile(nDays);
			
		Map<String , Stock> testMap = getStockVO(file);
			
		return testMap.get(symbol);
		
	}
	
	public float getPercentageChangedforNDays(String symbol , int nDays) throws FileNotFoundException, IOException	{
		
		Stock historialVO = getNDaysOldStock(symbol, nDays);
		
		Stock currentVO = getNDaysOldStock(symbol, 0);
		
		float percentageChanged = HelperUtil.getPercentageChanged(historialVO.getClosePrice(), currentVO.getClosePrice(), historialVO.getClosePrice());
		
		if(historialVO.getClosePrice() < currentVO.getClosePrice())	{
			return percentageChanged;
		}
		
		return -1 * percentageChanged;
		
	}
	
	public float getHighforNDays(String symbol , int nDays) throws FileNotFoundException, IOException	{
		
		float highPrice = 0.0f;
		
		String preFileName = "";
		int counter = 1;
		
		for(int i = 0 ; counter <= nDays ; i++)	{
			
			File file = getNdaysOldFile(i);
			
			if(preFileName.equals(file.getName()))	{
				continue;
				
			}
			preFileName = file.getName();
			if(counter == 1)	{
				counter++;
				continue;
			}
			counter++;
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(symbol);
			
			if(stock.getHighPrice() > highPrice)	{
				highPrice = stock.getHighPrice();
			}
			
		}
		return highPrice;
		
	}
	
	public float getLowforNDays(String symbol , int nDays) throws FileNotFoundException, IOException	{
		
		float lowPrice = 50000.0f;
		
		for(int i = 0 ; i<= nDays ; i++)	{
			
			File file = getNdaysOldFile(i);
			//ignore latest file
			if(i == 1 )	{
				continue;
			}
			
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(symbol);
			
			if(stock.getLowPrice() < lowPrice)	{
				lowPrice = stock.getLowPrice();
			}
		}
		return lowPrice;
		
	}
	
	public float getMaxVolumeforNDays(String symbol,int nDays) throws FileNotFoundException, IOException	{
		
		float highVolume = 0.0f;
		
		int count = 0;
		for(int i = 0 ; i<= nDays ; i++){
			
			File file = getNdaysOldFile(i);

			
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(symbol);
			
			if(stock.getQuantityTraded() > highVolume)	{
				if(count == 0 ){
					count ++;
					continue;
				}
				highVolume = stock.getQuantityTraded();
			}
			
		}
		return highVolume;
	}
	
	public float getLowVolumeforNDays(String symbol,int nDays) throws FileNotFoundException, IOException	{
		
		float lowVolume = Float.MAX_VALUE;
		
		int count = 0;
		for(int i = 0 ; i<= nDays ; i++){
			
			File file = getNdaysOldFile(i);

			
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(symbol);
			
			if(stock.getQuantityTraded() < lowVolume)	{
				if(count == 0 ){
					count ++;
					continue;
				}
				lowVolume = stock.getQuantityTraded();
			}
			
		}
		return lowVolume;
	}
	
	public float getAverageVolumeforNDays(String symbol,int nDays) throws FileNotFoundException, IOException	{
		
		float totalVolume = 0;
		
		int count = 0;
		String preFileName = "";
		for(int i = 0 ; count <= nDays ; i++){
			
			File file = getNdaysOldFile(i);

			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(symbol);
			
			if(count == 0 ){
				count ++;
				continue;
			}
			totalVolume += stock.getQuantityTraded();
			count++;
		}
		return totalVolume/nDays;
	}
	
	public float getVolumePercentageLatest(String symbol,int nDays) throws FileNotFoundException, IOException	{
		
		float averageVolume = getAverageVolumeforNDays(symbol,nDays);
		
		Stock today = getNDaysOldStock(symbol, 0);
		
		float percentage = HelperUtil.getPercentageChanged(averageVolume, today.getQuantityTraded(), averageVolume);
		
		if(today.getQuantityTraded() > averageVolume)	{
			return percentage;
		}
		
		return -1 * percentage;
		
	}
	
	public float getSimpleMovingAverage(String stockName,int ndays) throws FileNotFoundException, IOException{
		
		float totalSum = 0;
		int counter = 1;
		
		String preFileName = "";
		
		for(int i = 0 ; counter<= ndays ; i++)	{
			
			File file = getNdaysOldFile(i);
			
			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			counter++;
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(stockName);
			
			totalSum += stock.getClosePrice();
			
		}
	
		return totalSum/ndays;
	}
	
	public float getOlderSimpleMovingAverage(String stockName,int ndays,int howOld) throws FileNotFoundException, IOException{
		
		float totalSum = 0;
		int counter = 1;
		
		String preFileName = "";
		
		for(int i = howOld ; counter<= ndays ; i++)	{
			
			File file = getNdaysOldFile(i);
			
			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			counter++;
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(stockName);
			
			totalSum += stock.getClosePrice();
			
		}
	
		return totalSum/ndays;
	}
	
	public Map<String, Stock> getNDaysOldStocks(int nDays) throws FileNotFoundException, IOException	{
		
		int counter = 0;
		
		String preFileName = "";
		File file = null;
		
		for(int i = 0 ; counter<= nDays ; i++)	{
			
			 file = getNdaysOldFile(i);
			
			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			counter++;
		}
		
		return getStockVO(file);
	}
	
	public Stock getNDaysOldStock(String stockName,int nDays) throws FileNotFoundException, IOException	{
		
		return getNDaysOldStocks(nDays).get(stockName);
	}
	
	/* This method determines stock trend.
	 * It compares today's 200 Day DMA with 6 month back 200 DAY DMA.
	 * If today's 200 DMA is greater than 6 month old 200 DMA , it returns true , else false.
	 */
	public boolean isStockMovingUp(String stockName) throws FileNotFoundException, IOException{
		
		float currentDMA = getSimpleMovingAverage(stockName, 200);
		
		float oldDMA = getOlderSimpleMovingAverage(stockName, 200, 132);
		
		if(currentDMA > oldDMA && HelperUtil.getPercentageChanged(oldDMA, currentDMA, oldDMA) > 15)	{
			return true;
		}
		
		return false;
	}
	
	public float getStockMovementForNDays(String stockName, int nDays) throws FileNotFoundException, IOException{
		
		float nDayHigh = getHighforNDays(stockName, nDays);
		
		float nDayLow = getLowforNDays(stockName, nDays);
		
		return HelperUtil.getPercentageChanged(nDayLow, nDayHigh, nDayLow);
		
	}
	
	public float getStockMovementWRTToday(String stockName, int nDays,boolean upMove) throws FileNotFoundException, IOException	{
		
		Stock todayVO = getStockVO(stockName, 0);
		if(upMove){
			
			float nDayLow = getLowforNDays(stockName, nDays); 
			
			return HelperUtil.getPercentageChanged(nDayLow, todayVO.getClosePrice(), nDayLow);
			
		}else	{
			float nDayHigh = getHighforNDays(stockName, nDays);
			
			return HelperUtil.getPercentageChanged(todayVO.getClosePrice(), nDayHigh, todayVO.getClosePrice());
		}
	}
	
	private Date getLastFriday()	{
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK); 

		if(day == Calendar.FRIDAY)	{
			return cal.getTime();
		}
		if(day == Calendar.SATURDAY)	{
	    	cal.add(Calendar.DAY_OF_YEAR, -1);
	    	return cal.getTime();
		}
		if(day == Calendar.SUNDAY)	{
	    	cal.add(Calendar.DAY_OF_YEAR, -2);
	    	return cal.getTime();
		}
		
		cal.add(Calendar.DAY_OF_YEAR,-1 * day);
		return cal.getTime();
		
	}
	
	public float getWeeklyMovingAverage(String stockName,int nWeeks) throws FileNotFoundException, IOException	{
		
		Map<String , Stock> testMap = getStockVO(getNdaysOldFile(0));
		
		float totalSum = testMap.get(stockName).getClosePrice();
		
		int initialStart;
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK); 

		if(day == Calendar.SUNDAY)	{
			initialStart = 8;
		}else	{
			initialStart =   day;
		}
		
		int counter = 2;
		
		String preFileName = "";
		
		for(int i = initialStart ; counter<= nWeeks ; i = i+7)	{
			
			File file = getNdaysOldFile(i);
			
			
			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			counter++;
			testMap = getStockVO(file);
			
			Stock stock = testMap.get(stockName);
			
			totalSum += stock.getClosePrice();
			
		}
	
		return totalSum/nWeeks;
		
	}
	
	public float getMonthlyMovingAverage(String stockName, int nMonths)	throws FileNotFoundException, IOException	{
		
		Calendar cal = Calendar.getInstance();
		
		Map<String , Stock> testMap = getStockVO(getNdaysOldFile(0));
		
		float totalSum = testMap.get(stockName).getClosePrice();
		
		int counter = 2;
		
		String preFileName = "";
		
		for(int i = cal.get(Calendar.DAY_OF_MONTH); counter<= nMonths ; i = i+cal.get(Calendar.DAY_OF_MONTH))	{
			
			cal.add(Calendar.DAY_OF_YEAR, -1 * cal.get(Calendar.DAY_OF_MONTH));
			
			File file = getNdaysOldFile(i);
			
			//System.out.println(file.getName());
			
			if(preFileName.equals(file.getName()))	{
				continue;
			}
			preFileName = file.getName();
			counter++;
			testMap = getStockVO(file);
			
			Stock stock = testMap.get(stockName);
			
			totalSum += stock.getClosePrice();
			
		}
	
		return totalSum/nMonths;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		HistoricUtil hUtil = new HistoricUtil("/Users/jayanderk/Documents/Personal/Workspace/WinnerTrade/history");
		
		System.out.println(hUtil.getMonthlyMovingAverage("GOACARBON", 50));
		
	}
	
	public Map<String, Stock> getStockVO(int nDaysOld) throws FileNotFoundException, IOException	{
		
		return getStockVO(getNdaysOldFile(nDaysOld));
	}

}
