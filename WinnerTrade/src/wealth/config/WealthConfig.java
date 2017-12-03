package wealth.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class WealthConfig {

	private static WealthConfig myself = null; 
	private Properties prop = null;
	
	private Set<String> FnOSet = new HashSet<>();
	
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
	
	public boolean isDayTradingStock(String name){
		
		if(FnOSet.isEmpty())	{
			try(BufferedReader reader = new BufferedReader(new FileReader(
					new File(WealthConfig.getInstance().getProperty("MASTER_DATA_LOC"),"FnO.csv"))))	{
				
				String line;
				
	            while ((line = reader.readLine()) != null) {
	            	
	            	FnOSet.add(line);
	            }
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return FnOSet.contains(name);
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
