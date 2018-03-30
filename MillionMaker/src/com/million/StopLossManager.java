package com.million;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

import com.million.config.WealthConfig;
import com.million.kite.login.KiteHelper;
import com.rainmatter.kitehttp.exceptions.KiteException;

public class StopLossManager {

	private static Logger logger = Logger.getLogger(StopLossManager.class);
	
	public static void main(String[] args) {
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(
				WealthConfig.getInstance().getProperty("EXISTING_POSITIONS"))))){
			
			KiteHelper kiteHelper = new KiteHelper();
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("SCRIP_NAME"))	{
					continue;
				}
				
				String []values = line.split(",");
				String scripName = values[0].trim();
				logger.debug("Found - " + scripName + " in the file.");
				
				int quantity = Integer.valueOf(values[1].trim());
				
				float stopLossPrice = Float.valueOf(values[2]);
				
				try {
					kiteHelper.placeSLSellOrder("NSE", scripName, stopLossPrice, quantity, stopLossPrice);
					
					logger.info("Placed StopLoss order for [" + scripName + "] for [" + quantity +"] at [" + stopLossPrice +"]"  );
					
				} catch (KiteException e) {
					logger.error("Exception occured ", e);
				}
				
			}
		} catch (Exception e) {
			logger.error("Exception occured ", e);
			System.exit(-1);
		}

	}

}
