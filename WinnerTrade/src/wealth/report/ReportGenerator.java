package wealth.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import wealth.file.CsvWritter;
import wealth.file.HistoricUtil;
import wealth.file.ReportReader;
import wealth.util.HelperUtil;
import wealth.vo.Report;
import wealth.vo.Stock;

public class ReportGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		ReportReader reader = new ReportReader("/Users/jayanderk/Documents/Personal/Millions");
		
		HistoricUtil hUtil = new HistoricUtil("/Users/jayanderk/Documents/Personal/Workspace/WinnerTrade/history");
		
		CsvWritter csvWritter = new CsvWritter("/Users/jayanderk/Documents/Personal/Millions/Report", "report.csv",
				"REPORT-DATE","TODAY","STOCK-NAME","PRICE","BEST-ENTRY","ENTRY","BEST-EXIT","EXIT","10%-EXIT");
		
		int nDaysOldReport = 10;
		
		List<Report> reportList = reader.getNDaysOldReport(nDaysOldReport);
		
		if(reportList != null)	{
			
			for(Report report : reportList)	{
				
				float lowPrice = hUtil.getLowforNDays(report.getStockName(), nDaysOldReport-1);
				
				//System.out.println(report.getStockName() + "-" + lowPrice);
				float fivePerLess = (float) (report.getClosingPrice() - (report.getClosingPrice() * .05));
				
				float tenPerMore = (float) (report.getClosingPrice() + report.getClosingPrice() * .1);
				
				float buyPrice = report.getClosingPrice();
				if(lowPrice < fivePerLess )	{
					buyPrice = fivePerLess;
				}
				
				float highPrice = hUtil.getHighforNDays(report.getStockName(), nDaysOldReport-1);
				
				if(tenPerMore > highPrice)	{
					tenPerMore = 0;
				}
				
				Stock todayVO = hUtil.getStockVO(report.getStockName(),0);
				
				csvWritter.write(report.getDate(),HelperUtil.getStringDate(),report.getStockName(),""+report.getClosingPrice(),
						HelperUtil.getRoundedNumber(buyPrice),HelperUtil.getRoundedNumber(report.getClosingPrice()),HelperUtil.getRoundedNumber(highPrice),
						HelperUtil.getRoundedNumber(todayVO.getClosePrice()),HelperUtil.getRoundedNumber(tenPerMore));
			}
		}
		

	}

}
