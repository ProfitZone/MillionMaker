package com.million;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.million.config.WealthConfig;
import com.million.kite.login.KiteHelper;
import com.million.kite.login.TokenManager;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.LTPQuote;

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
		
		try {
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
				
			}
		} catch (Exception e) {
			
			logger.error("Exception occurred" , e);
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

	public static void checkAndAlert() {
		
		List<String> scripNameList = new ArrayList<>();
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(parametersMap.get(INPUT_FILE_PARAMETER))))){
			
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("SCRIP_NAME"))	{
					continue;
				}
				
				String []values = line.split(",");
				String scripName = values[0].trim();
				
				logger.debug("Added " + scripName + " to list.");
				scripNameList.add(scripName);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Exception occured ", e);
			System.exit(-1);
		}

		KiteHelper kiteHelper;
		Map<String, LTPQuote> LTPMap = null;
		try {
			kiteHelper = new KiteHelper();
			LTPMap = kiteHelper.getLTP(scripNameList.toArray(new String[0]));
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured ", e);
			System.exit(-1);
		} catch (KiteException e) {
			e.printStackTrace();
			logger.error("Exception occured ", e);
			try {
				TokenManager.main(null);
			} catch (Exception e1) {
				logger.error("Request token has expired");
				
			} catch (KiteException e1) {
				logger.error("Request token has expired");
			}
			return;
		}
		
		String foundScrips = "";
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(
				parametersMap.get(INPUT_FILE_PARAMETER))))){
			
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("SCRIP_NAME"))	{
					continue;
				}
				
				String []values = line.split(",");
				String scripName = values[0].trim();
				
				double recoPrice = Double.valueOf(values[1]);
				
				LTPQuote quote = LTPMap.get(scripName);
				
				if(quote == null)	{
					logger.error("SCRIP NOT FOUND - " + scripName);
					continue;
				}
				
				logger.debug(scripName + " has LTP - " + quote.lastPrice);
				
				String action = values.length >= 3 ? " Go for " + values[2] : "";
				
				if(isLTPWithinRange(quote.lastPrice , recoPrice))	{
					logger.info(scripName + " has LTP " + quote.lastPrice + " within range " + recoPrice + action);
					foundScrips = foundScrips + scripName +",";
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Exception occured ", e);
			
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
