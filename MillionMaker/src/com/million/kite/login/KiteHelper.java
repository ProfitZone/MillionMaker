package com.million.kite.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.million.config.WealthConfig;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;

public class KiteHelper {

	KiteConnect kiteConnect = null;
	
	private static Logger logger = Logger.getLogger(KiteHelper.class);
	
	public KiteHelper() throws FileNotFoundException, IOException	{
		
		WealthConfig wealthConfig = WealthConfig.getInstance();
		
		kiteConnect = new KiteConnect(wealthConfig.getProperty("API_KEY"));

        // set userId
        kiteConnect.setUserId(wealthConfig.getProperty("USER_NAME"));

        // Set session expiry callback.
        kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
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
    
    public void logout() throws KiteException, JSONException, IOException	{
    	/** Logout user and kill session. */
        JSONObject jsonObject = kiteConnect.logout();
        
        logger.info("logged out - " + jsonObject.toString());
    }
	
    public Map<String,LTPQuote>getLTP(String ... scripNames) throws KiteException, JSONException, IOException	{
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
     * @throws IOException 
     * @throws JSONException 
     */
    public Order placeNormalAMOOrder(String exchange,String stockName,String transactionType, 
    		float price, int quantity) throws KiteException, JSONException, IOException {
        
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
     * @throws IOException 
     * @throws JSONException 
     */
    public Order placeSLSellOrder(String exchange,String stockName,
    		float price, int quantity,float triggerPrice) throws KiteException, JSONException, IOException {
        
       return this.placeOrder( Constants.EXCHANGE_NSE, stockName, Constants.ORDER_TYPE_SL,Constants.PRODUCT_CNC ,Constants.TRANSACTION_TYPE_SELL, 
    		   price, quantity, triggerPrice, Constants.VARIETY_REGULAR);
    } 
    
    public Order placeOrder(String exchange,String stockName, String orderType,String productTye,
    		String transactionType, float price, int quantity,float triggerPrice, String RegularBOAMO) throws KiteException, JSONException, IOException {
        
    	OrderParams orderParams = new OrderParams();
    	
    	orderParams.quantity = quantity;
    	orderParams.orderType = orderType;
    	orderParams.tradingsymbol = stockName;
        orderParams.product = productTye; 
        orderParams.exchange = exchange;
        orderParams.transactionType = transactionType;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = (double) price;
        orderParams.triggerPrice = (double) triggerPrice;
         
        
        Order order = kiteConnect.placeOrder(orderParams, RegularBOAMO);
        
       // System.out.println(order.orderId);
        
        logger.debug("Placing order\n"
        		+ "exchange - " + orderParams.exchange + "\n"
        		+ "tradingsymbol - " + orderParams.tradingsymbol + "\n"
        		+ "product - " + orderParams.product + "\n"
        		+ "orderType - " + orderParams.orderType + "\n"
        		+ "transactionType - " + orderParams.transactionType + "\n"
        		+ "validity - " + orderParams.validity + "\n"
        		+ "quantity - " + orderParams.quantity + "\n"
        		+ "price - " + orderParams.price + "\n"
        		+ "triggerPrice - " + orderParams.triggerPrice + "\n" 
        		);
        
        return order;
    }
    
	public static void main(String[] args) throws FileNotFoundException, IOException, KiteException {
		KiteHelper kiteHelper = new KiteHelper();
		
		kiteHelper.placeSLSellOrder( Constants.EXCHANGE_NSE, "UFLEX",
	    		   346, 120, 346);
	}

}
