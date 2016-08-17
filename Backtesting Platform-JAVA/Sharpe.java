import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Sharpe {
	
	public ArrayList<Double> getDailyReturns (HashMap<Integer, ArrayList<Trade>> tradeMap, ArrayList<Date> dateKeySet) {
		
		ArrayList<Integer> intDateKeys = new ArrayList<Integer>();
		HashMap<Integer, Date> IntDateConverter = new HashMap<Integer, Date>();
		int count = 0;
		
		//goes through the date key set and converts it to integers and sets up a date to integer converter map
		
		for(Date datekey: dateKeySet) {
			count++;
			IntDateConverter.put(count, datekey);
		}
		
		//puts the specific days we are looking at in the TradeMap into the intDateKeys
		for (Integer key: tradeMap.keySet()) {
			int tradeCount = 0;
			for(Trade trade : tradeMap.get(key)){
				tradeCount++;
			}
			if(tradeCount > 0){
				intDateKeys.add(key);
			}
		}
		
		//the integer in the map is the date in integer form. The double is the daily return
		HashMap<Integer, Double> dailyReturns = new HashMap<Integer, Double>();
		
//		//initialize the dailyReturn map with 0.0
//		for(Integer dateKey : intDateKeys){
//			dailyReturns.put(dateKey, 0.0);
//		}
		
		//initialize hashMap of the specific dates we are looking at
		
		Collections.sort(intDateKeys);
		Collections.reverse(intDateKeys);
		//intDateKeys is sorted from oldest to newest
		
		for(Integer key: intDateKeys) {
			for(Trade trade : tradeMap.get(key)) {
			  int duration = trade.getDuration();
			  
			  try{
				  //gets the returns for each trade and each day of their duration
				  //------potential error: generates 5 returns for a duration of 5 days...
				  //------potential error: make sure the keys and ks are in the right place
				  for (int k=1; k<=duration; k++) {
					  //ask alex what (0,1) would give
					  double ret1 = trade.getCustomReturn(k-1, k)*trade.getLeverage();
					  
					  //assigns the return to the dailyReturns map 
					  if(dailyReturns.keySet().contains(key-k)){
						  double ret0 = dailyReturns.get(key-k);
						  dailyReturns.put(key-k, ret1 + ret0);
					  } else { //if the key was not previously in the map, it puts it in
						  dailyReturns.put(key-k, ret1);
					  }
				  }
				} catch(IOException ioe){
					continue;
				}
			  
			}
		
		}
		
		//sorts by natural order of the keys from newest to oldest
		SortedSet<Integer> keys = new TreeSet<Integer>(dailyReturns.keySet());
		
		
		ArrayList<Double> returns = new ArrayList<Double>();
		for(Integer key: keys) {
			returns.add(dailyReturns.get(key));
		}
		
		Collections.reverse(returns);
		
		return returns;
	}
	
	public void printReturns(ArrayList<Double> returns) {
		System.out.println(returns);
	}
	
	public ArrayList<Double> getMonthlyReturns(ArrayList<Double> returns) {
	
	//these monthly returns have a string of 0's at the beginning. Not sure why
		ArrayList<Double> monthlyReturns = new ArrayList<Double>();
	
		int length = returns.size();
		
		
		for (int c=0; c<length; c++) {
			
			double monthReturn=0;
			
			try{
			for(int d = 0; d<21; d++) {
				//21 trading days in a month.
				monthReturn += returns.get(c+d);
				
			}
			} catch(Exception e) {
				break;
			}

			monthlyReturns.add(monthReturn);
			
			c += 21;
			
		}
		
//			System.out.println(monthlyReturns);
		return monthlyReturns;
		
		}
		
		
		public double calculateMonthly (ArrayList<Double> monthlyReturns) {

			
			//going for monthly returns
			
//			ArrayList<Integer> months = new ArrayList<Integer>();
//			
//			ArrayList<Double> monthlyReturns = new ArrayList<Double>();
//			
//			months.add(daysReturned.get(0).getMonth());
//			int y;
//			for(y=1; y<returns.size(); y++) {
//				
//				if(daysReturned.get(y-1).getMonth()!=daysReturned.get(y).getMonth()) {
//					
//					months.add(daysReturned.get(y).getMonth());
//					
//				}
//				
//			}
//			
//			System.out.println(months);
//			
//			HashMap<Integer,Double> monthReturns = new HashMap<Integer,Double>();
//			
//			for(Integer i: months) {
//				
//				monthReturns.put(i, 0.0);
//			}
//			
//			
//			int u = returns.size();
//			int w;
//			for(w=0; w<u; w++) {
//				
//				double ret = returns.get(w);
//				int mon = daysReturned.get(w).getMonth();
//				 for (Integer i: monthReturns.keySet()) {
//				  if ( i == mon ) {
//				  
//				  double ret0 = monthReturns.get(i);
//				  double ret1 = ret0 + ret ;
//				  dailyReturns.put(i, ret1);
//			  } 
//				  
//				 }
//				 
//			}
//			
//			System.out.println(monthReturns);
//			//WHY FAILING????
			
			//calculate the monthly sharpe
			DescriptiveStatistics stats2 = new DescriptiveStatistics();
		    for( double item : monthlyReturns) {
		        stats2.addValue(item);
		    }
		    

		    double mean = stats2.getMean();

		    double std = stats2.getStandardDeviation();

		    double sharpeRatio = (mean - (.04/12) ) / std * Math.sqrt(12);

		    return Math.round(sharpeRatio*1000.0)/1000.0;
			
		}
		
		
		
		
		public double calculateDaily ( ArrayList<Double> returns) {
			
		
			//calculate the sharpe as usual
			
			DescriptiveStatistics stats = new DescriptiveStatistics();
		    for( double item : returns) {
		        stats.addValue(item);
		    }
		    

		    double mean1 = stats.getMean();

		    double std1 = stats.getStandardDeviation();

		    double sharpeRatio1 = ((mean1 - (.04/252) ) / std1) * Math.sqrt(252);

		    return Math.round(sharpeRatio1*1000.0)/1000.0;
		    
		    
		}
		
		public void printDrawDown (ArrayList<Double> returns) {
			ArrayList<Double> cumReturn = new ArrayList<Double>();
			
			cumReturn.add(returns.get(0));
			
			int i;
			int p;
			
			p = returns.size();
			
			for (i=1; i<p; i++) {
				
				cumReturn.add((1+returns.get(i))*(1+cumReturn.get(i-1))-1);
				
			}
			
			// gets the high water mark of the fund
			
			ArrayList<Double> highWater = new ArrayList<Double>();
			
			highWater.add(cumReturn.get(0));
			
			for (i=1; i<p; i++) {
				
				highWater.add(Math.max(highWater.get(i-1), cumReturn.get(i)));
				
			}
			
			// gets the drawdown from the high water mark.
			
			ArrayList<Double> drawDown = new ArrayList<Double>();
			
			for (i=0; i<p; i++) {	
				drawDown.add((1+highWater.get(i))/(1+cumReturn.get(i))-1);
			}
			
			
			//calculates the max of the drawdowns
			
			Collections.sort(drawDown);
			
			double maxDrawDown = drawDown.get(drawDown.size() - 1);
			
			//calculates the duration of each draw down period
			
			// reinstates the original draw down list
			
			drawDown.clear();
			
			for (i=0; i<p; i++) {
				
				drawDown.add((1+highWater.get(i))/(1+cumReturn.get(i))-1);
			}
			
			// now calculates the duration 
			
			ArrayList <Double> duration = new ArrayList<Double>();
			
			for (i=0; i<p; i++) {
				
				if (drawDown.get(i) == 0) {
					duration.add(0.0);
				}
				
				else {
					duration.add(duration.get(i-1)+1);			
				}
			}
				
			//calculate the max duration
			
			Collections.sort(duration);
			
			double maxDuration = duration.get(duration.size()-1);
			
			System.out.println("The max drawdown is " + maxDrawDown*100 + " % of your capital \nThe max duration of drawdown periods is " + maxDuration + " days");
		    
		    
			
		}
		
		public void printKelly (ArrayList<Double> returns) {
			
			DescriptiveStatistics stats1 = new DescriptiveStatistics();
		    for( double item : returns) {
		        stats1.addValue(item);
		    }
		    
		    double mean2 = stats1.getMean()*252;
		    
		    double stddev = stats1.getStandardDeviation()*Math.sqrt(252);
		    
		  	double meanExcess = mean2 - .04;
		  	
		  	double sharpe = meanExcess/stddev;
		  	
		  	double kelly = meanExcess/(stddev*stddev);
		  	
		  	double unlevReturn = mean2 - (stddev*stddev)/2;
		  	
		  	double levReturn = .04 + (sharpe*sharpe)/2;
			
			System.out.println("the mean return per year is: " + mean2);
			System.out.println("the mean excess return per year is" +meanExcess);
			System.out.println("the strategies annualized standard deviation is :" + stddev);
		  	System.out.println("The strategy's sharpe is: " + sharpe);
		  	System.out.println("The strategy's Kelly is and you should leverage to use this much of your capital: " + kelly);
		  	System.out.println("The strategy's compounded unleveraged returns: " + unlevReturn);
		  	System.out.println("The strategy's compounded leveraged returns: " + levReturn);
			
		}
		

		

	
	
}
