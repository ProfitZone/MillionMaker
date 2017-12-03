import java.util.Calendar;

import org.apache.log4j.Logger;

public class TestMillion {

	private static Logger logger = Logger.getLogger(TestMillion.class);
	
	public static void main(String[] args) {
		logger.info("Testing");
		
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		
		System.out.println(hour);

	}

}
