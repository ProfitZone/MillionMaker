package wealth.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wealth.vo.Report;

public class ReportReader {
	
	private static String SEPERATOR = ",";
	
	private Map<String, List<Report>> dataMap = new HashMap<String, List<Report>>();
	

	public ReportReader(String directoryName)	{
		
		String []files = new File(directoryName).list();
		
		for(String file : files)	{
			if(file.contains(".csv"))	{
				try(BufferedReader reader = new BufferedReader(new FileReader(new File(directoryName,file))))	{
					
					String line;
					
		            while ((line = reader.readLine()) != null) {
		            	if(line.contains("DATE")){
		            		continue;
		            	}
		            	
		            	String []values = line.split(SEPERATOR);
		            	
		            	if(values.length < 3)	{
		            		continue;
		            	}
		            	
		            	List<Report> reportList = null;
		            	
		            	if(dataMap.get(values[0]) != null)	{
		            		reportList = dataMap.get(values[0]) ;
		            	}else	{
		            		reportList = new ArrayList<>();
		            	}
		            	
		            	Report report = new Report();
		            	
		            	report.setDate(values[0]);
		            	report.setStockName(values[1]);
		            	//report.setClosingPrice(Float.valueOf(values[2]));
		            	//report.setPercentageChange(Float.valueOf(values[3]));
		            	
		            	reportList.add(report);
		            	
		            	dataMap.put(values[0], reportList);
		            	
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
	}
	
	public List<Report> getNDaysOldReport(int n) {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-YY");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -n);
		
		int i = 1;
		
		do {
			
			String key = simpleDateFormat.format(cal.getTime());
			if(dataMap.get(key) != null)	{
				return dataMap.get(key);
			}
			cal.add(Calendar.DATE, -1);
			i++;
			
		}while( i < 5);
		
		return null;
	}
	
	public Map<String, List<Report>> getAllReports()	{
		return dataMap;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ReportReader reader = new ReportReader("/Users/jayanderk/Documents/Personal/Millions");
		
		System.out.println(reader.getNDaysOldReport(2));

	}

}
