package com.million;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.million.config.WealthConfig;
import com.million.gui.DisplayManager;
import com.million.kite.login.KiteHelper;
import com.million.kite.login.TokenManager;
import com.million.sound.SoundProducer;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.LTPQuote;


public class AlertManager {

	private static Logger logger = Logger.getLogger(AlertManager.class);
	
	
	public static void main(String[] args) {
		
		try {
			int interval = Integer.valueOf(WealthConfig.getInstance().getProperty("INTERVAL"));
			while(true)	{
				
				if (args.length > 0)	{
					logger.info("There is command line parameter [" + args[0] +"]");
					
					Calendar calendar = Calendar.getInstance();
					int hours = calendar.get(Calendar.HOUR_OF_DAY);
					
					if(hours >= Integer.valueOf(args[0]))	{
						logger.info("Breaking because current hour [" + hours + "]" + " is >= " +  "[" + args[0] + "]");
						break;
					}
					
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
				
				Thread.sleep(1000 * 60 * interval);
				
			}
		} catch (Exception e) {
			
			logger.error("Exception occurred" , e);
		}
		
	}
	public static void checkAndAlert() {
		
		List<String> scripNameList = new ArrayList<>();
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(
				WealthConfig.getInstance().getProperty("INTRADAY_FILE_NAME"))))){
			
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
				WealthConfig.getInstance().getProperty("INTRADAY_FILE_NAME"))))){
			
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
		
		
		try {
				if(!"".equals(foundScrips))	{
					DisplayManager.display(foundScrips);
					new SoundProducer().play();
					
					Thread.sleep( 1000 * 10);
					
					DisplayManager.close();
					
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Exception occured ", e);
			
		}
		
	}

	private static boolean isLTPWithinRange(double lastPrice, double recoPrice) {
		
		double lowerLimit = recoPrice - recoPrice * 0.01;
		double upperLimit = recoPrice + recoPrice * 0.01;
		
		if(lastPrice <= upperLimit && lastPrice >= recoPrice)	{
			return true;
		}
		if(lastPrice >= lowerLimit && lastPrice <= recoPrice)	{
			return true;
		}
		
		return false;
	}

}
