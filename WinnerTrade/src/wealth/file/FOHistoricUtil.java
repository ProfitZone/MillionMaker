package wealth.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import wealth.vo.Stock;

public class FOHistoricUtil {
	
	private String dirName = null;
	
	private Map<String, Map<String, Stock>> stockCache = new HashMap<>();
	
	public FOHistoricUtil(String dirName){
		
		this.dirName = dirName;
	}
	
	private File getNdaysOldFile(int n){
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMYYYY");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -n);
		
		String prefix = simpleDateFormat.format(cal.getTime());
		
		String fileName = "fo" + prefix.toUpperCase() + "bhav.csv";
		
		File file = new File(this.dirName,fileName);
		
		while(!file.exists())	{
			
			cal.add(Calendar.DATE, -1);
			
			prefix = simpleDateFormat.format(cal.getTime());
			
			fileName = "fo" + prefix.toUpperCase() + "bhav.csv";
			
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

                
                if("INSTRUMENT".equals(details[0]))	{
                	continue;
                }
                
                if(stockMap.containsKey(details[1]))	{
                	continue;
                }
                Stock stock = new Stock();
                
                stock.setName(details[1]);
                stock.setOpenPrice(Float.valueOf(details[5]));
                stock.setHighPrice(Float.valueOf(details[6]));
                stock.setLowPrice(Float.valueOf(details[7]));
                stock.setClosePrice(Float.valueOf(details[8]));
                stock.setOptionType(details[4]);
                stock.setChangeInOI(Integer.valueOf(details[13]));
                stock.setOpenInterest(Integer.valueOf(details[12]));
                
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
	
	public Map<String, Stock> getStockVO(int nDaysOld) throws FileNotFoundException, IOException	{
		
		return getStockVO(getNdaysOldFile(nDaysOld));
	}
	
	public float getChangeInOIForNDays(String stockName,int nDays) throws FileNotFoundException, IOException	{
		
		float percentage = 0.0f;
		
		String preFileName = "";
		int counter = 1;
		
		for(int i = 0 ; counter <= nDays ; i++)	{
			
			File file = getNdaysOldFile(i);
			if(preFileName.equals(file.getName()))	{
				continue;
				
			}
			preFileName = file.getName();
			if(counter == 1)	{
				//counter++;
				//continue;
			}
			counter++;
			Map<String , Stock> testMap = getStockVO(file);
			
			Stock stock = testMap.get(stockName);
			
			if(stock.getOpenInterest() == 0){
				continue;
			}
			percentage = stock.getChangeInOI()*100/stock.getOpenInterest();
			//System.out.println(stockName + "-" + totalOI + "-" + totalChangeInOI + "-" + percentage);
			
		}
		return percentage;
		
	}
	
	public static void main(String[] args) {
		
		FOHistoricUtil foHistoricUtil = new FOHistoricUtil("/Users/jayanderk/Documents/Personal/Workspace/WinnerTrade/historyFO");
		
		try {
			Map<String, Stock> stokMap = foHistoricUtil.getStockVO(0);
			
			Iterator<String> iterator = stokMap.keySet().iterator();
			
			while(iterator.hasNext()){
				
				Stock stock = stokMap.get(iterator.next());
				
				//System.out.println(stock.getName() + "-" + stock.getChangeInOI() + "-" + stock.getOptionType() + "-" + stock.getOpenInterest());
				
				System.out.println(stock.getName() + "-" + foHistoricUtil.getChangeInOIForNDays(stock.getName(), 1));
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
