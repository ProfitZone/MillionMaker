package wealth.build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wealth.config.WealthConfig;
import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.file.ReportReader;
import wealth.util.HelperUtil;
import wealth.vo.Report;
import wealth.vo.Stock;

public class TomorrowWinner {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
		
		ReportReader reader = new ReportReader(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"));
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		
		Date olderDate = cal.getTime();
		Map<String, List<Report>> dataMap = reader.getAllReports();
		
		Iterator<String> iterator = dataMap.keySet().iterator();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy");
		
		HistoricUtil hUtil = new HistoricUtil(WealthConfig.getInstance().getProperty("DAILY_FILES_STORED_LOC"));
		
		CsvWritter csvWritter = new CsvWritter(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC") +"/reports", "watch-list.csv",
				"REPORT-DATE","TODAY","STOCK-NAME","PRICE","MA");
		
		Set<String> todaySet = new HashSet<>();
		
		while(iterator.hasNext())	{
			
			try	{
				String date = iterator.next();
				
				Date parsedDate = simpleDateFormat.parse(date);
				
				if(parsedDate.before(olderDate))	{
					continue;
				}
				
				List<Report> reportList = dataMap.get(date);
				
				for(Report reportVO : reportList)	{
					
					Stock todayVO = hUtil.getStockVO(reportVO.getStockName(), 0);
					
					if(todaySet.contains(todayVO.getName())) 	{
						continue;
					}
					todaySet.add(todayVO.getName());
					
					float twoHundredDayMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 200);
					
					if(todayVO.getClosePrice() == todayVO.getLowPrice())	{
						continue;
					}
					
					if(HelperUtil.getPercentageChanged(todayVO.getClosePrice(), todayVO.getLowPrice(), todayVO.getLowPrice()) < 2 
							||  HelperUtil.getPercentageChanged(todayVO.getClosePrice(), todayVO.getLowPrice(), todayVO.getLowPrice()) > 3)   {
						continue;
					}
					
					if(todayVO.getClosePrice() > twoHundredDayMA &&  todayVO.getLowPrice() <= twoHundredDayMA && HelperUtil.isWithinRange(twoHundredDayMA, todayVO.getClosePrice(), 2))	{
						csvWritter.write(date,HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
								HelperUtil.getRoundedNumber(twoHundredDayMA),"200-DMA");
						//System.out.println(todayVO.getName());
						continue;
					}
					
					float fiftyDayMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 50);
					
					if(todayVO.getClosePrice() > fiftyDayMA && todayVO.getLowPrice() <= fiftyDayMA && HelperUtil.isWithinRange(fiftyDayMA, todayVO.getClosePrice(), 2))	{
						csvWritter.write(date,HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
								HelperUtil.getRoundedNumber(fiftyDayMA),"50-DMA");
						//System.out.println(todayVO.getName());
						continue;
					}
					
					float EightDayMA = hUtil.getSimpleMovingAverage(todayVO.getName(), 8);
					
					if(todayVO.getClosePrice() > EightDayMA && todayVO.getLowPrice() <= EightDayMA && HelperUtil.isWithinRange(EightDayMA, todayVO.getClosePrice(), 2))	{
						csvWritter.write(date,HelperUtil.getStringDate(),todayVO.getName(),HelperUtil.getRoundedNumber(todayVO.getClosePrice()),
								HelperUtil.getRoundedNumber(EightDayMA),"8-DMA");
						
					}
					
				}
			}catch(Exception e)	{
				
			}
		}
	}
}
