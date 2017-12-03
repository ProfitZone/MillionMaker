package wealth.make;

import java.io.IOException;
import java.text.ParseException;

import wealth.build.AccumulationWinner;
import wealth.build.JustAboveMAWinner;
import wealth.build.TomorrowWinner;
import wealth.build.WinnerAboveMA;
import wealth.build.v2.IgniteDailyWinner;
import wealth.build.v2.ReversalFinderBull;

public class WealthCreator {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		
		//WinnerAboveMA.main(args);
		
		//AccumulationWinner.main(args);
		
		//JustAboveMAWinner.main(args);
		
		//TomorrowWinner.main(args);
		
		//This is for FnO
		//GoingDownWinner.main(args);
		
		//ReversalFinderBull.main(args);
		
		//IgniteDailyWinner.main(args);
		
		OTAReporter.main(args);

	}

}
