package com.onnea.million.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperUtil {
	
	public static String getStringDate()	{
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-YY");
		
		Date curDate = new Date();
		
		return simpleDateFormat.format(curDate);
	}
	
	public static String getStringDateTime()	{
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-YY HH:mm:ss");
		
		Date curDate = new Date();
		
		return simpleDateFormat.format(curDate);
	}
	
	public static String getRoundedNumber(float number){
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		
		return df.format(number);
	}
	
	public static float getPercentageChanged(float price1,float price2,float basePrice)	{
		
		float change = Math.abs(price1 - price2);
		
		return (change * 100)/basePrice;
	}
	
	public static boolean isBetween(float movingAverage, float openPrice,
			float closePrice) {
		
		if(openPrice <= movingAverage && closePrice > movingAverage)	{
			return true;
		}
		return false;
	}
	
	public static boolean isWithinRange(float baseValue,float compareValue,int range)	{
		
		float lowValue = (float) (baseValue - (baseValue * (.01 * range)));
		
		float highValue = (float) (baseValue + (baseValue * (.01 * range)));
		
		if(compareValue >= lowValue && compareValue <= highValue)	{
			return true;
		}
		
		return false;
		
	}

}
