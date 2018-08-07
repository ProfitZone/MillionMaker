package com.million;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.million.cache.ApplicationCache;
import com.million.common.Constants;
import com.million.config.WealthConfig;
import com.million.csv.CSVReader;
import com.million.kite.login.KiteHelper;
import com.million.kite.login.TokenManager;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Order;

public class AutoOrderManager {

	/**
	 * -inputFile=witWinners.csv
	 * -repeatRuns=5 // -1 for unlimited runs
	 * -stopAfterHours=16 // Stop is time is more than 16 hours
	 * -runEveryXMinutes=5 // Run every 5 minutes
	 * -alertRange=1 // alert range is percentage
	 * 
	 * @param args
	 */
	
	private static Logger logger = Logger.getLogger(AutoOrderManager.class);
	
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
				
				AutoOrderManager.checkAndPlaceOrder();
				
				if(runCount >= noOfIterations)	{
					break;
				}
					
				Thread.sleep(1000 * 60 * interval);
				
			}
		} catch (Exception | KiteException e) {
			
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

	public static void checkAndPlaceOrder() throws IOException, JSONException, KiteException {
		
		//handle more than one file in input
		String files = parametersMap.get(INPUT_FILE_PARAMETER);
		
		String[] fileNames =  new String[1];
		
		if(files.contains(",")){
			fileNames = files.split(",");
		}else	{
			fileNames[0] = files;
		}
		
		for(String fileName : fileNames)	{
			
			CSVReader csvReader = new CSVReader(fileName);
			
			csvReader.getAllScrips();
			KiteHelper kiteHelper = new KiteHelper();;
			
			Map<String, LTPQuote> LTPMap = null;
			try {
				LTPMap = kiteHelper.getLTP(csvReader.getAllScrips());
			
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception occured ", e);
				//System.exit(-1);
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
			}
			
			//get all the orders from Zerodha, check if order already exists.
			List<Order> placedOrderList = kiteHelper.getOrders();
			
			for(Order order : placedOrderList)	{
				ApplicationCache.getInstance().put(Constants.CACHE_GROUP_INTRADAY_ORDERS, order.tradingSymbol);
				logger.debug("Adding scrip to cache [" + order.tradingSymbol +"]");
			}
			
			for(String scripName : csvReader.getAllScrips())	{
				
				LTPQuote quote = LTPMap.get(scripName);
				
				if(quote == null)	{
					
					String loggerMessage = "SCRIP NOT FOUND - " + scripName;
					if(!ApplicationCache.getInstance().contains(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage))	{
						logger.error(loggerMessage);
						ApplicationCache.getInstance().put(Constants.CACHE_GROUP_LOG_MESSAGES, loggerMessage);
					}
					continue;
				}
				
				float entryPrice = csvReader.getFloatValue(scripName, Constants.FIELD_NAME_ENTRY_PRICE_LEVEL);
				
				logger.debug(scripName + " has LTP - " + quote.lastPrice);
				
				String action = csvReader.getValue(scripName, Constants.FIELD_NAME_TRADE_TYPE);
				logger.debug("Action is " + action);
				
				//PLACE BRACKET ORDER
				float stopLossPrice = csvReader.getFloatValue(scripName, Constants.FIELD_NAME_SL_PRICE);
				
				if(!Constants.ACTION_TYPE_SELL.equalsIgnoreCase(action) && !Constants.ACTION_TYPE_BUY.equalsIgnoreCase(action))	{
					logger.error("There is an error in entry of [" + scripName +"], action type is not correct - [" + action + "]");
					continue;
				}
					
				if(Constants.ACTION_TYPE_SELL.equalsIgnoreCase(action)  && stopLossPrice <= entryPrice)	{
					logger.error("There is an error in entry of [" + scripName +"], stoploss is less than entry price, for SHORT position");
					continue;
				}else if(Constants.ACTION_TYPE_BUY.equalsIgnoreCase(action)  && stopLossPrice >= entryPrice){
					logger.error("There is an error in entry of [" + scripName +"], stoploss is more than entry price, for LONG position");
					continue;
				}
				
				logger.debug("Processing [" + scripName + "], entry price is [" + entryPrice +"], LTP is [" + quote.lastPrice + "], action is [" + action +"]" );
				if(isLTPWithinRange(quote.lastPrice , entryPrice))	{
					
					//check if order was placed and is already available is local cache.
					if(!ApplicationCache.getInstance().contains(Constants.CACHE_GROUP_INTRADAY_ORDERS, scripName))	{
						

						float datrValue = csvReader.getFloatValue(scripName, Constants.FIELD_NAME_DATR);
						
						float actualSL = stopLossPrice;
						if(Constants.ACTION_TYPE_SELL.equalsIgnoreCase(action))	{
							actualSL = stopLossPrice + datrValue;
						}else	{
							actualSL = stopLossPrice - datrValue;
						}
						
						
						//logic to understand number of orders to be placed
						List<Float> profitRatioList = new ArrayList<>();
						
						int i=1;
						while(true)	{
							if(WealthConfig.getInstance().getProperty(i + "_PROFIT") != null)	{
								profitRatioList.add(Float.valueOf(WealthConfig.getInstance().getProperty(i + "_PROFIT")));
							}else	{
								break;
							}
							i++;
						}
						
						float totalLossAllowed = Float.valueOf(WealthConfig.getInstance().getProperty(Constants.INTRADAY_MAX_LOSS));
						
						//Total quantity 
						int totalQuantity = (int) (totalLossAllowed/Math.abs(entryPrice - actualSL));
						
						int quantityPerOrder = totalQuantity/profitRatioList.size();
						
						for(float profitRatio : profitRatioList)	{
							
							float stoploss =  Float.valueOf(new DecimalFormat("#.##").format( Math.abs(entryPrice - actualSL)));
							float target = Float.valueOf(new DecimalFormat("#.##").format(profitRatio * Math.abs(entryPrice - actualSL)));
							
							logger.info("placing order for [" + scripName +"] , at price [" + entryPrice +"] , quantity [" 
									+ quantityPerOrder +"] stoploss [" +stoploss +"] target [" + target +"]");
							
							
							kiteHelper.placeBracketOrder(csvReader.getValue(scripName, Constants.FIELD_NAME_EXCHANGE), 
									scripName, entryPrice, quantityPerOrder, 
									stoploss, target, csvReader.getValue(scripName, Constants.FIELD_NAME_TRADE_TYPE));
						    
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						ApplicationCache.getInstance().put(Constants.CACHE_GROUP_INTRADAY_ORDERS, scripName);
						
						
					}
				} 
			}
			
			try {
				//sleep for 5 sec after processing one file.
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
