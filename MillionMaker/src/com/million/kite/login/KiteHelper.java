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
import com.rainmatter.models.Order;

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
        		logger.debug("Instrument name is - " + instrument.getSegment() + " - " +
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
    		if(scripName.contains("NIFTY"))	{
    			instruments[i++] = "NFO:"+scripName;
    		}else	{
    			instruments[i++] = "NSE:"+scripName;
    		}
    	}
    	
    	Map<String, LTPQuote> zerodhaMap = kiteConnect.getLTP(instruments);
    	Map<String, LTPQuote> resultMap = new HashMap<>();
    	
    	Iterator<String> keys = zerodhaMap.keySet().iterator();
    	
    	while(keys.hasNext())	{
    		String prefixedName = keys.next();
    		logger.debug("Retrieved LTP from zerodha - " +prefixedName);
    		resultMap.put(prefixedName.replace("NSE:", ""), zerodhaMap.get(prefixedName));
    		resultMap.put(prefixedName.replace("NFO:", ""), zerodhaMap.get(prefixedName));
    		
    	}
    	logger.debug("Retrieved LTP for - " + resultMap.size());
		return resultMap;	
	}
	
    /**
     * Place normal After market order.
     * 
     * @param exchange
     * @param stockName
     * @param transactionType
     * @param price
     * @param quantity
     * @throws KiteException
     */
    public Order placeNormalAMOOrder(String exchange,String stockName,String transactionType, 
    		float price, int quantity) throws KiteException {
        
       return this.placeOrder(exchange, stockName, "LIMIT", "CNC", transactionType, price, quantity, 0, "amo");
    } 

    /**
     * Place normal After market order.
     * 
     * @param exchange
     * @param stockName
     * @param transactionType
     * @param price
     * @param quantity
     * @throws KiteException
     */
    public Order placeSLSellOrder(String exchange,String stockName,
    		float price, int quantity,float triggerPrice) throws KiteException {
        
       return this.placeOrder(exchange, stockName, "SL", "CNC", "SELL", price, quantity, triggerPrice, "regular");
    } 
    
    public Order placeOrder(String exchange,String stockName,String orderType,String productTye,
    		String transactionType, float price, int quantity,float triggerPrice, String RegularBOAMO) throws KiteException {
        
        Map<String, Object> param = new HashMap<String, Object>(){
            {
                put("quantity", ""+quantity);
                
                //put("order_type", "LIMIT");
                put("order_type", orderType);
                
                //put("tradingsymbol", "ASHOKLEY");
                put("tradingsymbol", stockName);
                
                //put("product", "CNC");
                put("product", productTye);
                
                //put("exchange", "NSE");
                put("exchange", exchange);
                
                //put("transaction_type", "BUY");
                put("transaction_type", transactionType);
                
                put("validity", "DAY");
                
                //put("price", "118.50");
                put("price", ""+price);
                
                //put("trigger_price", "0");
                put("trigger_price", ""+triggerPrice);
                
                //put("tag", "myTag");   //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed
            }
        };
        Order order = kiteConnect.placeOrder(param, RegularBOAMO);
        
       // System.out.println(order.orderId);
        logger.debug("Placed order for [" + order.tradingSymbol + "] ["  + order.transactionType 
        		+"] , Order status message is [" + order.statusMessage +"]");
        
        return order;
    }
    
	public static void main(String[] args) throws FileNotFoundException, IOException, KiteException {
		KiteHelper kiteHelper = new KiteHelper();
		
		kiteHelper.placeNormalAMOOrder("NSE", "ESCORTS", "BUY", 825, 10);
	}

}
