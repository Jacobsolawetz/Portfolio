import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;

public class RSI{
	public double getRSI(int periodLength, ArrayList<Double> close) {  
		if(close.size() >= periodLength + 1){
        	/* 
		     * The RSI of the first calculation is different than subsequent calculations 
		     * The first calculation of RSI begins when you have a number of closing prices equal to the 
		     * period you're looking at.  In the default case, after 14 closing prices (Actually this will 
		     * be after 15 closing prices, because you have to compare the first closing price with something 
		     * to determine if there was a gain or loss. 
		     */
		    
        	//reverse the order of close
			ArrayList<Double> reversedClose = new ArrayList<Double>();
			for (int i = close.size() - 1; i >= 0; i--) {
				reversedClose.add(close.get(i));
		    }
        	
			ArrayList<Double> gainLossList = new ArrayList<Double>();
			int count = 0;
			for(Double price : reversedClose){
				if(count <= periodLength && count > 0){
					gainLossList.add(price);
				}
				count++;
			}
			ArrayList<Double> gains = new ArrayList<Double>();  
			ArrayList<Double> losses = new ArrayList<Double>();  
		      
			double previousPrice = reversedClose.get(0);  
			double previousAvgGain = 0.00;  
			double previousAvgLoss = 0.00;  
		      
		    for(int x = 0; x < gainLossList.size(); x++) {  
		            double closePrice = gainLossList.get(x);  
		            double priceChange = closePrice - previousPrice;  
		            if (priceChange < 0) {  
		                    losses.add(new Double(Math.abs(priceChange)));  
		            } else {  
		                    gains.add(new Double(priceChange));  
		            }  
		            // Set this price as the new previous price for the next iteration  
		            previousPrice = closePrice;         
		    } // end gainLostList...  
		      
		    double sum = 0.00;  
		    for(double g : gains) {  
		            sum += g;  
		    }  
		    double avgGain = sum / periodLength;  
		      
		    sum = 0.00;  
		    for(double l : losses) {  
		            sum += l;  
		    }  
		    double avgLoss = sum / periodLength;  
		      
		    previousAvgGain = avgGain;  
		    previousAvgLoss = avgLoss;  
		      
		    double firstRS = avgGain / avgLoss;  
		    double firstRSI = 0.00;  
		      
		    if (avgLoss == 0) {  
		            firstRSI = 0;  
		    } else {  
		            firstRSI = (100 - (100 / (1 + firstRS) ));  
		    }  
		      
		    if (reversedClose.size() == periodLength + 1) {  
		            return firstRSI;  
		    }  
		      
		    // Now that the first RSI value has been established, the running RSI calculation can begin  
		      
		    for(int x = 0; x < reversedClose.size(); x++) {  
		            if (x <= periodLength) {  
		                    continue;  
		            }  
		            double closePrice = reversedClose.get(x);  
		            double priceChange = closePrice - previousPrice;  
		              
		            /* 
		             *  Now that the price change has been calculated, set the current price  
		             *  to the previous price for the next iteration. 
		             */  
		            previousPrice = closePrice;  
		              
		            double gain = 0.00;  
		            double loss = 0.00;  
		            if (priceChange < 0) {  
		                    loss = Math.abs(priceChange); // losses are represented as positive numbers  
		            } else {  
		                    gain = priceChange;  
		            }  
		              
		            avgGain = ((previousAvgGain * (periodLength-1)) + gain) / periodLength;  
		            avgLoss = ((previousAvgLoss * (periodLength-1)) + loss) / periodLength;  
		              
		            /* 
		             * Now that the average gain/loss has been established, set the  
		             * current gain/loss to the previous_avg_gain/loss for the next iteration. 
		             */  
		            previousAvgGain = avgGain;  
		            previousAvgLoss = avgLoss;  
		              
		            double rs = avgGain / avgLoss;  
		            double rsi = 0;  
		            if (avgLoss == 0) {  
		                    rsi = 100;  
		            } else {  
		                    rsi = (100 - (100 / (1 + rs) ));  
		            }  
		              
		            if (x == reversedClose.size() - 1) {  
		            	return rsi;  
		            }  
		    } // end stockPrices...  
		    return 0.00;  // If for some reason a value hasn't been returned, set RSI to 0  
        } else {
        	return 0.00;
        }
	}
}

  