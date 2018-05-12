package com.million.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWritter {

	private static String SEPERATOR = ",";
	
	private String directoryPath = "";
	
	private String fileName = "";
	
	private boolean dontWrite = false;
	
	public CSVWritter(String dirPath,String fileName,String ...headers) {
		
		this.directoryPath = dirPath;
		this.fileName = fileName;
				
		if(!new File(dirPath, fileName).exists())	{
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dirPath,fileName))))	{
				
				String header = "";
				
				for(String text : headers)	{
					header = header + text + SEPERATOR;
				}
				bw.append(header +"\n");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isDontWrite() {
		return dontWrite;
	}

	public void setDontWrite(boolean dontWrite) {
		this.dontWrite = dontWrite;
	}

	public void write(String...fields)	{
		
		if(dontWrite)	{
			return;
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.directoryPath,this.fileName),true)))	{
			
			String header = "";
			//bw.append("\n");
			for(String text : fields)	{
				header = header + text + SEPERATOR;
			}
			bw.append(header + "\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CSVWritter csvWritter = new CSVWritter("/Users/jayanderk/Documents/Personal/Millions", "ac-winner.csv",
				"DATE","STOCK-NAME","PRICE","CHANGE%","VOLUME%");
		
		csvWritter.write("23-Sep-16","PENINLAND","8.45","8.45","8.45");

	}

}
