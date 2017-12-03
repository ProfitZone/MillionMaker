package com.million.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class WealthConfig {

	private static WealthConfig myself = null; 
	private Properties prop = null;
	
	private WealthConfig() throws FileNotFoundException, IOException	{
		
		this.prop = new Properties();
		
		this.prop.load(new FileInputStream(new File("./config/config.properties")));
		
	}
	
	public static WealthConfig getInstance() throws FileNotFoundException, IOException	{
		
		if( myself == null){
			myself = new WealthConfig();
		}
		
		return myself;
	}
	
	public String getProperty(String key)	{
		
		return this.prop.getProperty(key);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			System.out.println(WealthConfig.getInstance().getProperty("GENERATED_REPORT_LOC"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
