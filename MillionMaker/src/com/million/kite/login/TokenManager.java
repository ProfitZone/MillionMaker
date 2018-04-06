package com.million.kite.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.million.config.WealthConfig;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

public class TokenManager {
	
	private static Logger logger = Logger.getLogger(TokenManager.class);
	
	private String userID = "SJ0899";
	
	public TokenManager()	{
		
	}
	/**
	 * Kite user ID
	 * @param userID
	 */
	public TokenManager(String userID)	{
		
		this.userID = userID;
	}
	public void getAndStoreAccessTokens() throws Exception, KiteException	{
		
		WealthConfig wealthConfig = WealthConfig.getInstance();
		
		//This is my API Key
		KiteConnect kiteConnect = new KiteConnect(wealthConfig.getProperty("API_KEY"));
		
		// set userId
        kiteConnect.setUserId(this.userID);
        
        kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("session expired");
            }
        });
        
        logger.info("Request token - " + getToken(TokenType.REQUEST));
        //First parameter is request_token, it is obtained by login to https://kite.trade/connect/login?api_key=byxwwjusrsbt88yi
        //Second parameter is API secret
        //UserModel userModel =  kiteConnect.requestAccessToken(getToken(TokenType.REQUEST), wealthConfig.getProperty("API_SECRET"));
        
        User user =  kiteConnect.generateSession(getToken(TokenType.REQUEST), wealthConfig.getProperty("API_SECRET"));
        //kiteConnect.setAccessToken(user.accessToken);
        //kiteConnect.setPublicToken(user.publicToken);
        
        saveToken(user.accessToken , TokenType.ACCESS);
        
        saveToken(user.publicToken , TokenType.PUBLIC); 
        
	}

	private void saveToken(String accessToken, TokenType tokenType) throws FileNotFoundException, IOException {

		WealthConfig wealthConfig = WealthConfig.getInstance();
		
		String tokenFile = "";
		
		if(tokenType == TokenType.REQUEST)	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("REQUEST_TOKEN_FILE");
		}else if ((tokenType == TokenType.ACCESS))	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("ACCESS_TOKEN_FILE");
		}else if ((tokenType == TokenType.PUBLIC))	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("PUBLIC_TOKEN_FILE");
		}
		
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				new File(wealthConfig.getProperty("TOKEN_LOCATION"),tokenFile)))){
			
			bufferedWriter.write(accessToken);
			logger.info(tokenType + " - " + accessToken + " is stored in file - " + tokenFile);
		}
		
	}

	public String getToken(TokenType tokenType) throws FileNotFoundException, IOException {
		
		WealthConfig wealthConfig = WealthConfig.getInstance();
		
		String tokenFile = "";
		
		if(tokenType == TokenType.REQUEST)	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("REQUEST_TOKEN_FILE");
		}else if ((tokenType == TokenType.ACCESS))	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("ACCESS_TOKEN_FILE");
		}else if ((tokenType == TokenType.PUBLIC))	{
			tokenFile = this.userID + "." + wealthConfig.getProperty("PUBLIC_TOKEN_FILE");
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(
				wealthConfig.getProperty("TOKEN_LOCATION"),tokenFile)))){
			
			String token = reader.readLine();
			logger.info("Returning - " + tokenType + " - " + token);
			return token;
		}
	}
	
	public static void main(String[] args) throws Exception, KiteException {
		
		TokenManager tokenManager = null;
		
		if(args.length > 1)	{
			tokenManager = new TokenManager(args[0]);
		}else	{
			tokenManager = new TokenManager();
		}
		
		tokenManager.getAndStoreAccessTokens();
		
		System.out.println(tokenManager.getToken(TokenType.REQUEST));
		System.out.println(tokenManager.getToken(TokenType.ACCESS));
		System.out.println(tokenManager.getToken(TokenType.PUBLIC));
	}

}
