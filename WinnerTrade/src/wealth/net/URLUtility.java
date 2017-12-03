package wealth.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLUtility {
	
	public static String readURL(String URLString) throws IOException, InterruptedException	{
		
		URL url = new java.net.URL(URLString);
		
		URLConnection conn = url.openConnection();

		// open the stream and put it into BufferedReader
		BufferedReader br = new BufferedReader(
                           new InputStreamReader(conn.getInputStream()));
		
		StringBuilder content = new StringBuilder();
		
		String inputLine = "";
		
		Thread.sleep(1000 * 5);
		
		while ((inputLine = br.readLine()) != null) {
			content.append(inputLine);
		}
		
		return content.toString();
	}
	
	public static String readURLNoDelay(String URLString) throws IOException, InterruptedException	{
		
		URL url = new java.net.URL(URLString);
		
		URLConnection conn = url.openConnection();

		// open the stream and put it into BufferedReader
		BufferedReader br = new BufferedReader(
                           new InputStreamReader(conn.getInputStream()));
		
		StringBuilder content = new StringBuilder();
		
		String inputLine = "";
		
		Thread.sleep(500 * 1);
		
		while ((inputLine = br.readLine()) != null) {
			content.append(inputLine);
		}
		
		return content.toString();
	}

}
