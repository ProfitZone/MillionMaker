package com.million;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.million.cache.ApplicationCache;
import com.million.common.Constants;
import com.million.config.WealthConfig;
import com.million.csv.CSVReader;
import com.million.csv.CSVWritter;
import com.million.kite.login.KiteHelper;
import com.million.kite.login.TokenManager;
import com.million.sound.SoundProducer;
import com.onnea.million.util.HelperUtil;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;

public class BaseAlertManager {

	/**
	 * -inputFile=witWinners.csv
	 * -repeatRuns=5 // -1 for unlimited runs
	 * -stopAfterHours=16 // Stop is time is more than 16 hours
	 * -runEveryXMinutes=5 // Run every 5 minutes
	 * -alertRange=1 // alert range is percentage
	 * 
	 * @param args
	 */
	
	private static Logger logger = Logger.getLogger(BaseAlertManager.class);
	
	private static String INPUT_FILE_PARAMETER = "inputFile";
	
	private static String REPEAT_RUNS_PARAMETER = "repeatRuns";
	
	private static String STOP_AFTER_HOURS_PARAMETER = "stopAfterHours";
	
	private static String RUN_EVERY_X_MINUTES_PARAMETER = "runEveryXMinutes";
	
	private static String ALERT_RANGE_PARAMETER = "alertRange";
	
	
	private static Map<String, String> parametersMap= new HashMap<String, String>();
	
	public static void main(String[] args) {

		readParameters(args);
		
		int interval = 5;
		if(parametersMap.get(RUN_EVERY_X_MINUTES_PARAMETER) != null){
			interval = Integer.valueOf(parametersMap.get(RUN_EVERY_X_MINUTES_PARAMETER));
		}
		logger.debug("Going to run every - " + interval + " minutes");
		
		int noOfIterations = 1;
		if(parametersMap.get(REPEAT_RUNS_PARAMETER) != null){
			int itr = Integer.valueOf(parametersMap.get(REPEAT_RUNS_PARAMETER));
			noOfIterations = itr;
			if(itr == -1)	{
				noOfIterations = Integer.MAX_VALUE;
			}
		}
		logger.debug("Going to run - " + noOfIterations + " iterations");
		
		int runCount = 0; 
		while(runCount++ < noOfIterations)	{
			
			try	{
				logger.debug("Running iteration - " + runCount);
				
				Calendar calendar = Calendar.getInstance();
				int hours = calendar.get(Calendar.HOUR_OF_DAY);
				
				if(parametersMap.get(STOP_AFTER_HOURS_PARAMETER)!= null && hours >= Integer.valueOf(parametersMap.get(STOP_AFTER_HOURS_PARAMETER)))	{
					logger.info("Breaking because current hour [" + hours + "]" + " is >= " +  "[" + parametersMap.get(STOP_AFTER_HOURS_PARAMETER) + "]");
					break;
				}
					
				if(new File(WealthConfig.getInstance().getProperty("STOP_FILE_DROPBOX")).exists())	{
					logger.info("Found dropbox stop file, hence stopping");
					break;
				}
				if(new File(WealthConfig.getInstance().getProperty("STOP_FILE_LOCAL")).exists())	{
					logger.info("Found local stop file, hence stopping");
					break;
				}
				checkAndAlert();
				
				if(runCount >= noOfIterations)	{
					break;
				}
				Thread.sleep(1000 * 60 * interval);
				
			}catch (Exception e) {
				
				logger.debug("Exception occurred" , e);
			}
		}
	}


	private static void readParameters(String[] args) {
		
		for(String parameter : args){
			
			String[] splitedParameters = parameter.split("=");
			
			if(splitedParameters[0].contains(INPUT_FILE_PARAMETER))	{
				parametersMap.put(INPUT_FILE_PARAMETER, splitedParameters[1]);
				
			}else if(splitedParameters[0].contains(REPEAT_RUNS_PARAMETER))	{
				parametersMap.put(REPEAT_RUNS_PARAMETER, splitedParameters[1]);
				
			}else if(splitedParameters[0].contains(STOP_AFTER_HOURS_PARAMETER))	{
				parametersMap.put(STOP_AFTER_HOURS_PARAMETER, splitedParameters[1]);
				
			}else if(splitedParameters[0].contains(RUN_EVERY_X_MINUTES_PARAMETER))	{
				parametersMap.put(RUN_EVERY_X_MINUTES_PARAMETER, splitedParameters[1]);
				
			}else if(splitedParameters[0].contains(ALERT_RANGE_PARAMETER))	{
				parametersMap.put(ALERT_RANGE_PARAMETER, splitedParameters[1]);
				
			}else	{
				System.out.println("Usage is BaseAlertManager -inputFile=abc.csv -repeatRuns=5 -stopAfterHours=16 -runEveryXMinutes=5 -alertRange=1");
			}
			
		}
		
		logger.debug("Running program with following parametrs = " + parametersMap);
		
	}

	public static void checkAndAlert() throws Exception {
		
		//handle more than one file in input
		String files = parametersMap.get(INPUT_FILE_PARAMETER);
		
		CSVWritter trackingSheet = new CSVWritter(WealthConfig.getInstance().getProperty("HOME_DIR") 
				+ "/Records/" , "Onnea-records-" + HelperUtil.getStringDate() + ".csv", "DATE","STOCK-NAME","PRICE","TYPE");
				
		String writeInCSV = System.getProperty(Constants.WRITE_IN_CSV);
		
		if("false".equalsIgnoreCase(writeInCSV)){
			trackingSheet.setDontWrite(true);
		}
		
		String[] fileNames =  new String[1];
		
		if(files.contains(",")){
			fileNames = files.split(",");
		}else	{
			fileNames[0] = files;
		}
		
		for(String fileName : fileNames)	{
			
			CSVReader csvReader = new CSVReader(fileName);
			
			csvReader.getAllScrips();
			KiteHelper kiteHelper;
			
			Map<String, LTPQuote> LTPMap = null;
			try {
				kiteHelper = new KiteHelper();
				LTPMap = kiteHelper.getLTP(csvReader.getAllScrips());
			
			} catch (Exception e) {
				//e.printStackTrace();
				logger.debug("Exception occured ", e);
				//System.exit(-1);
			} catch (KiteException e) {
				//e.printStackTrace();
				logger.debug("Exception occured ", e);
				try {
					TokenManager.main(null);
				} catch (Exception e1) {
					logger.debug("Request token has expired");
				} catch (KiteException e1) {
					logger.debug("Request token has expired");
				}
			}
			
			for(String scripName : csvReader.getAllScrips())	{
				
				if(null == scripName)	{
					continue;
				}
				
				LTPQuote quote = LTPMap.get(scripName);
				
				if(quote == null)	{
					
					String loggerMessage = "SCRIP NOT FOUND - " + scripName;
					if(!ApplicationCache.getInstance().contains(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage))	{
						logger.error(loggerMessage);
						ApplicationCache.getInstance().put(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage);
					}
					continue;
				}
				
				double recoPrice = csvReader.getFloatValue(scripName, Constants.FIELD_NAME_ENTRY_PRICE_LEVEL);
				
				logger.debug(scripName + " has LTP - " + quote.lastPrice);
				
				String action = " Go for " + csvReader.getValue(scripName, Constants.FIELD_NAME_OTA_TRADE_TYPE);
				
				if(csvReader.getValue(scripName, Constants.FIELD_NAME_OTA_TRADE_TYPE).contains("SHORT") && quote.lastPrice >= recoPrice)	{
					action += " ***";
				}
				
				if(csvReader.getValue(scripName, Constants.FIELD_NAME_OTA_TRADE_TYPE).contains("LONG") && quote.lastPrice <= recoPrice)	{
					action += " ***";
				}
				
				if(isLTPWithinRange(quote.lastPrice , recoPrice))	{
					//String loggerMessage = scripName + " has LTP " + quote.lastPrice + " within range " + new DecimalFormat("#.##").format(recoPrice) + action;
					String loggerMessage = scripName + " has LTP " + "{0}" + " within range " + "{1}" + action;
					
					if(!ApplicationCache.getInstance().contains(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage))	{
						ApplicationCache.getInstance().put(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage);
						
						loggerMessage = MessageFormat.format(loggerMessage, quote.lastPrice,new DecimalFormat("#.##").format(recoPrice));
						logger.info(loggerMessage);
						
						trackingSheet.write(HelperUtil.getStringDateTime(),scripName,""+recoPrice,csvReader.getValue(scripName, Constants.FIELD_NAME_OTA_TRADE_TYPE));
						
						SoundProducer.play();
						ApplicationCache.getInstance().put(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage);
					}
				}
			}
			
			try {
				//sleep for 15 sec after processing one file.
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static boolean isLTPWithinRange(double lastPrice, double recoPrice) {
		
		float alertPercentage= 1f/100;
		
		if(parametersMap.get(ALERT_RANGE_PARAMETER) != null)	{
			alertPercentage = Float.valueOf(parametersMap.get(ALERT_RANGE_PARAMETER))/100;
		}
		
		double lowerLimit = recoPrice - recoPrice * alertPercentage;
		double upperLimit = recoPrice + recoPrice * alertPercentage;
		
		if(lastPrice <= upperLimit && lastPrice >= recoPrice)	{
			return true;
		}
		if(lastPrice >= lowerLimit && lastPrice <= recoPrice)	{
			return true;
		}
		
		return false;
	}
}
