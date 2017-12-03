package com.million.kite.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.million.config.WealthConfig;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.SessionExpiryHook;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.Instrument;
import com.rainmatter.models.LTPQuote;

public class KiteHelper {

	KiteConnect kiteConnect = null;
	
	private static Logger logger = Logger.getLogger(KiteHelper.class);
	
	public KiteHelper() throws FileNotFoundException, IOException	{
		
		WealthConfig wealthConfig = WealthConfig.getInstance();
		
		kiteConnect = new KiteConnect(wealthConfig.getProperty("API_KEY"));

        // set userId
        kiteConnect.setUserId(wealthConfig.getProperty("USER_NAME"));

        // Set session expiry callback.
        kiteConnect.registerHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("session expired");
                logger.info("Session expired");
                
            }
        });

        TokenManager tokenManager = new TokenManager();
        
        kiteConnect.setAccessToken(tokenManager.getToken(TokenType.ACCESS));
        kiteConnect.setPublicToken(tokenManager.getToken(TokenType.PUBLIC));
        
        logger.info("kiteConnect created with access token - " + tokenManager.getToken(TokenType.ACCESS) 
        	+ " and public token - " + tokenManager.getToken(TokenType.PUBLIC));
        
	}
	
    /** Get all instruments that can be traded using kite connect.*/
    public void getAllInstruments() throws KiteException, IOException {
        // Get all instruments list. This call is very expensive as it involves downloading of large data dump.
        // Hence, it is recommended that this call be made once and the results stored locally once every morning before market opening.
        List<Instrument> instruments = kiteConnect.getInstruments();
        
        for(Instrument instrument : instruments)	{
        	
        	if("NSE".equals(instrument.getSegment()))	{
        		logger.info("Instrument name is - " + instrument.getSegment() + " - " +
        				instrument.getTradingsymbol());
        	}
        }
    }
    
    public void logout() throws KiteException	{
    	/** Logout user and kill session. */
        JSONObject jsonObject = kiteConnect.logout();
        
        logger.info("logged out - " + jsonObject.toString());
    }
	
    public Map<String,LTPQuote>getLTP(String ... scripNames) throws KiteException	{
    	String[] instruments = new String[scripNames.length];
    	
    	int i = 0;
    	for(String scripName : scripNames){
    		instruments[i++] = "NSE:"+scripName;
    	}
    	
    	Map<String, LTPQuote> zerodhaMap = kiteConnect.getLTP(instruments);
    	Map<String, LTPQuote> resultMap = new HashMap<>();
    	
    	Iterator<String> keys = zerodhaMap.keySet().iterator();
    	
    	while(keys.hasNext())	{
    		String prefixedName = keys.next();
    		logger.info("Retrieved LTP from zerodha - " +prefixedName);
    		resultMap.put(prefixedName.replace("NSE:", ""), zerodhaMap.get(prefixedName));
    		
    	}
    	logger.info("Retrieved LTP for - " + resultMap.size());
		return resultMap;	
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, KiteException {
		KiteHelper kiteHelper = new KiteHelper();
		
		Map<String, LTPQuote> resultMap = kiteHelper.getLTP("INFY","TCS");
		
		Iterator<String> keys = resultMap.keySet().iterator();
    	
    	while(keys.hasNext())	{
    		String name = keys.next();
    		
    		logger.info("LTP of "+ name + " is " + resultMap.get(name).lastPrice);
    		
    	}
	}

}
