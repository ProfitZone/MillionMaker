package com.million.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.million.common.Constants;

public class CSVReader {
	
	private String fileName = null;
	
	private Map<String , Map<String, String>> dataMap = new LinkedHashMap<>();
	
	public CSVReader(String fileName) throws IOException	{
		this.fileName = fileName;
		
		//read file and populate data.
		
		try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

			String sCurrentLine;
			boolean foundHeader = false;
			
			String[] headers = null;
			while ((sCurrentLine = br.readLine()) != null) {
				
				if(!foundHeader && sCurrentLine.startsWith(Constants.FIELD_NAME_SCRIP_NAME))	{
					
					foundHeader = true;
					headers = sCurrentLine.split(Constants.COMMA_SEPERATOR);
					continue;
				}
				String[] fields = sCurrentLine.split(Constants.COMMA_SEPERATOR);
				Map<String, String> valueMap =  new HashMap<>();
				
				for(int i=0; i<fields.length; i++)	{
					
					valueMap.put(headers[i], fields[i]);
				}
				
				if(valueMap.get(Constants.FIELD_NAME_SCRIP_NAME) != null)	{
					dataMap.put(valueMap.get(Constants.FIELD_NAME_SCRIP_NAME), valueMap);
				}
				
			}
			
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * It return all the scrip names from the file.
	 * 
	 * @return
	 */
	public String[] getAllScrips()	{
		
		return this.dataMap.keySet().toArray(new String[0]);
		
	}
	
	/**
	 * It return all the scrip names from the file.
	 * 
	 * @return
	 */
	public String[] getAllScripsByFieldName(String fieldName,String fieldValue)	{
		
		List<String> nameList = new ArrayList<>();
		
		while(this.dataMap.keySet().iterator().hasNext())	{
			
			String name = this.dataMap.keySet().iterator().next();
			
			if(fieldValue.equalsIgnoreCase(this.getValue(name, fieldName)))	{
				nameList.add(name);
			}
		}
		return nameList.toArray(new String[0]);
		
	}
	
	/**
	 * Returns specified field for the scrip.
	 * 
	 * @param scripName
	 * @param fieldName
	 * @return
	 */
	public String getValue(String scripName , String fieldName) {
		
		if(this.dataMap.get(scripName) == null)	{
			return null;
		}
		
		return this.dataMap.get(scripName).get(fieldName);
		
	}
	
	/**
	 * Returns specified field for the scrip.
	 * 
	 * @param scripName
	 * @param fieldName
	 * @return
	 */
	public int getIntValue(String scripName , String fieldName) {
		
		if(this.getValue(scripName, fieldName) == null)	{
			return 0;
		}
		return Integer.valueOf(this.getValue(scripName, fieldName));
	}
	
	/**
	 * Returns specified field for the scrip.
	 * 
	 * @param scripName
	 * @param fieldName
	 * @return
	 */
	public float getFloatValue(String scripName , String fieldName) {
		
		if(this.getValue(scripName, fieldName) == null)	{
			return 0;
		}
		
		return Float.valueOf(this.getValue(scripName, fieldName));
		
	}

}
