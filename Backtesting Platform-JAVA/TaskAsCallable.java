import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.*;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class TaskAsCallable implements Callable<HashMap<Integer, ArrayList<Trade>>>{
 
    private Date currentDay;
    private HashMap<Date, ArrayList<ArrayList<Object>>> output2;
    private ArrayList<Date> dateKeySet;
    private HashMap<String, ArrayList<ArrayList<Object>>> output;
    private int threadID;
     
    public TaskAsCallable(Integer threadID, Date currentDay, HashMap<Date, ArrayList<ArrayList<Object>>> output2, ArrayList<Date> dateKeySet, HashMap<String, ArrayList<ArrayList<Object>>> output) {
        this.currentDay = currentDay;
        this.output2 = output2;
        this.dateKeySet = dateKeySet;
        this.output = output;
        this.threadID = threadID;
    }
     
    public HashMap<Integer, ArrayList<Trade>> call() throws Exception {
    	BacktestingAlgoChecker bac = new BacktestingAlgoChecker(currentDay, output2, dateKeySet, output);
		
    	//determine which algos you want to run
		ArrayList<ArrayList<Object>> tradeTriggers = bac.checkAlgo1();
		tradeTriggers.addAll(bac.checkAlgo2());
		tradeTriggers.addAll(bac.checkAlgo3()); //outperforms but is never CALLED
		tradeTriggers.addAll(bac.checkAlgo4()); //outperforms
		tradeTriggers.addAll(bac.checkAlgo5()); //crazy outperform
		tradeTriggers.addAll(bac.checkAlgo6()); //crazy outperform
		tradeTriggers.addAll(bac.checkAlgo7()); //crazy outperform
		tradeTriggers.addAll(bac.checkAlgo8()); //under-perform
		tradeTriggers.addAll(bac.checkAlgo9()); //huge under-perform
		tradeTriggers.addAll(bac.checkAlgo10()); //outperform
		tradeTriggers.addAll(bac.checkAlgo11());  //slight outperform
		tradeTriggers.addAll(bac.checkAlgo12());  //outperforms
		tradeTriggers.addAll(bac.checkAlgo13()); //under-perform
		tradeTriggers.addAll(bac.checkAlgo14());  //heavy outperform
		tradeTriggers.addAll(bac.checkAlgo15());  //heavy outperform
		tradeTriggers.addAll(bac.checkAlgo16());  //decent performer
		//trades.addAll(bac.checkAlgo17());  //crazy under-performer - wtf
		//trades.addAll(bac.checkAlgo18()); //outperform???
		//trades.addAll(bac.checkAlgo19()); //decent performer	
		
		//arrayList of triggers
		ArrayList<Trigger> triggers = new ArrayList<Trigger>();
		
		//fill the arrayList with Trigger objects
		for(ArrayList<Object> trade : tradeTriggers){
			Trigger t = new Trigger((Date) trade.get(0), (Integer) trade.get(1), (String) trade.get(2), (String) trade.get(3), (Integer) trade.get(4));
			triggers.add(t);
		}
		
		//for randomizer later********
		//Puts triggers in a random order
		//List<String> keys = new ArrayList<String>(triggerMap.keySet());
		//ArrayList<String> randomKeySet = randomize(keys);
		
		//arrayList of Trades
		ArrayList<Trade> trades = new ArrayList<Trade>();
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		//fill the arrayList with Trade objects
		for(Trigger trigger : triggers){
			Trade trade = new Trade(trigger);
			trades.add(trade);
		}
		
		//max trades doesn't matter
		for(Trade trade : trades){ 
			Date startDate = trade.getDate();
			//int duration = trade.getDuration();
			
			//finds the index of the start date
			int startIndex = 0;
			for(Date date : dateKeySet){
				if(df.format(date).equals(df.format(startDate))){
					break;
				}
				startIndex++;
			}
			
			//puts the next ten days in the Trade object for future versatility -> can be changed for different strategies like intraday
			int duration = 10;
			int endIndex = startIndex - duration + 1; 
			Date endDate = dateKeySet.get(endIndex);
			
			//lowest index is newest close price and highest is close price at start of trade
			//all are adjusted for splits
			try{
				trade.setCloses(new ArrayList<Double>(getAdjCloses(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setVolumes(new ArrayList<Integer>(getVolumes(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setHighs(new ArrayList<Double>(getAdjHighs(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setLows(new ArrayList<Double>(getAdjLows(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setOpens(new ArrayList<Double>(getAdjOpens(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setDates(new ArrayList<Date>(getDates(trade.getSymbol(), output, endDate).subList(0, duration)));
				trade.setSpyCloses(new ArrayList<Double>(getSpyCloses(output, endDate).subList(0, duration)));
				trade.setSpyVolumes(new ArrayList<Integer>(getSpyVolumes(output, endDate).subList(0, duration)));
				trade.setSpyHighs(new ArrayList<Double>(getSpyHighs(output, endDate).subList(0, duration)));
				trade.setSpyLows(new ArrayList<Double>(getSpyLows(output, endDate).subList(0, duration)));
				trade.setSpyOpens(new ArrayList<Double>(getSpyOpens(output, endDate).subList(0, duration)));
				trade.setSpyDates(new ArrayList<Date>(getSpyDates(output, endDate).subList(0, duration)));
			} catch(IndexOutOfBoundsException ioobe){ //if there are less than duration values in the list
				trade.setCloses(new ArrayList<Double>(getAdjCloses(trade.getSymbol(), output, endDate)));
				trade.setVolumes(new ArrayList<Integer>(getVolumes(trade.getSymbol(), output, endDate)));
				trade.setHighs(new ArrayList<Double>(getAdjHighs(trade.getSymbol(), output, endDate)));
				trade.setLows(new ArrayList<Double>(getAdjLows(trade.getSymbol(), output, endDate)));
				trade.setOpens(new ArrayList<Double>(getAdjOpens(trade.getSymbol(), output, endDate)));
				trade.setDates(new ArrayList<Date>(getDates(trade.getSymbol(), output, endDate)));
				trade.setSpyCloses(new ArrayList<Double>(getSpyCloses(output, endDate)));
				trade.setSpyVolumes(new ArrayList<Integer>(getSpyVolumes(output, endDate)));
				trade.setSpyHighs(new ArrayList<Double>(getSpyHighs(output, endDate)));
				trade.setSpyLows(new ArrayList<Double>(getSpyLows(output, endDate)));
				trade.setSpyOpens(new ArrayList<Double>(getSpyOpens(output, endDate)));
				trade.setSpyDates(new ArrayList<Date>(getSpyDates(output, endDate)));
			}
			
			Beta beta = new Beta();
			
			int betaDuration = 126; //126 days is 6 months, half of the business year (252 days)
			
			
			//pass in all of the data as long as it matches up
			ArrayList<Double> equityAdjCloses = new ArrayList<Double>(getAdjCloses(trade.getSymbol(), output, startDate).subList(0, betaDuration));
			ArrayList<Double> spyAdjCloses = new ArrayList<Double>(getAdjCloses("SPY", output, startDate).subList(0, betaDuration));
			ArrayList<Date> equityDates = new ArrayList<Date>(getDates(trade.getSymbol(), output, startDate).subList(0, betaDuration));
			ArrayList<Date> spyDates = new ArrayList<Date>(getDates("SPY", output, startDate).subList(0, betaDuration));
			
			//*******Before intraday handle incorrect date match-up better
			if(enoughDates(equityDates, spyDates) && dateMatchup(equityDates, spyDates)){
				if(equityDates.size() == spyDates.size()){
					trade.setStockBeta(beta.calculate(equityAdjCloses, spyAdjCloses)); //calculates the beta if they are the same length
				} else{ //trim the larger list to the size of the smaller one
					int equitySize = equityAdjCloses.size();
			        int spySize = spyAdjCloses.size();
			        int smallestSize;
			    	if(equitySize < spySize){
			    		smallestSize = equitySize;
			    	} else{
			    		smallestSize = spySize;
			    	}
			    	trade.setStockBeta(beta.calculate(getReturnList(equityAdjCloses.subList(0, smallestSize)), (getReturnList(spyAdjCloses.subList(0, smallestSize))))); //calculates the beta while trimming the longer list
				}
			} else{
				trade.setStockBeta(null); //if beta is set as 0.0, no capital will be allocated to it
			}
			
			//here we're setting the trade's variance for realm 5. 
			int varianceDuration = 126;
			ArrayList<Double> equityAdjClosesVariance = new ArrayList<Double>(getAdjCloses(trade.getSymbol(), output, startDate).subList(0, varianceDuration));
			
			int length = equityAdjClosesVariance.size();
			//create a statistics list based on the returns of the list
			DescriptiveStatistics stats = new DescriptiveStatistics();
		    for(int i=1; i<length; i++) {
		        double item = (equityAdjClosesVariance.get(i)-equityAdjClosesVariance.get(i-1))/equityAdjClosesVariance.get(i-1);
		        stats.addValue(item);
		    }
			
		    trade.setVariance(stats.getStandardDeviation());
			
			
		}
        
		HashMap<Integer, ArrayList<Trade>> tradeMap = new HashMap<Integer, ArrayList<Trade>>();
		tradeMap.put(threadID, trades);
		
        return tradeMap;
    }
    
    //returns a list of returns
    public ArrayList<Double> getReturnList(List<Double> closes){
    	ArrayList<Double> returnList = new ArrayList<Double>();
    	
    	for(int i = 1; i < closes.size(); i++){
    		double change = (closes.get(i-1) - closes.get(i))/closes.get(i);
    		returnList.add(change);
    	}
    	return returnList;
    }
    
    //returns true if all of the dates match up
    public boolean dateMatchup(ArrayList<Date> one, ArrayList<Date> two){     
        int size1 = one.size();
        int size2 = two.size();
    	int smallestSize;
    	if(size1 < size2){
    		smallestSize = size1;
    	} else{
    		smallestSize = size2;
    	}
        
    	for(int i = 0; i < smallestSize; i++){
        	if(one.get(i).compareTo(two.get(i)) != 0){
        		return false;
        	}
        }
    	return true;
    }
    
    //returns true if there are more than 50 days worth of dates in both lists
    public boolean enoughDates(ArrayList<Date> one, ArrayList<Date> two){     
        if(one.size() > 50 && two.size() > 50){
        	return true;
        } else{
        	return false;
        }
    }
    
    public ArrayList<Double> getAdjCloses(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjCloses = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjCloses).add((Double) lineData.get(6));
			}
		}
		return adjCloses;
	}
	
	public ArrayList<Integer> getVolumes(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Integer> volumes = new ArrayList<Integer>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				volumes.add((Integer) lineData.get(7));
			}
		}
		return volumes;
	}
	
	public ArrayList<Date> getDates(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Date> dates = new ArrayList<Date>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				dates.add((Date) lineData.get(1));
			}
		}
		return dates;
	}
	
	public ArrayList<Double> getAdjOpens(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjOpens = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjOpens).add((Double) lineData.get(3));
			}
		}
		return adjOpens;
	}
	
	public ArrayList<Double> getAdjHighs(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjHighs = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjHighs).add((Double) lineData.get(4));
			}
		}
		return adjHighs;
	}
	
	public ArrayList<Double> getAdjLows(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjLows = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjLows).add((Double) lineData.get(5));
			}
		}
		return adjLows;
	}
	
	public ArrayList<Double> getSpyCloses(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjCloses = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjCloses).add((Double) lineData.get(6));
			}
		}
		return adjCloses;
	}
	
	public ArrayList<Integer> getSpyVolumes(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Integer> volumes = new ArrayList<Integer>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				volumes.add((Integer) lineData.get(7));
			}
		}
		return volumes;
	}
	
	public ArrayList<Date> getSpyDates(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Date> dates = new ArrayList<Date>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				dates.add((Date) lineData.get(1));
			}
		}
		return dates;
	}
	
	public ArrayList<Double> getSpyOpens(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjOpens = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjOpens).add((Double) lineData.get(3));
			}
		}
		return adjOpens;
	}
	
	public ArrayList<Double> getSpyHighs(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjHighs = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjHighs).add((Double) lineData.get(4));
			}
		}
		return adjHighs;
	}
	
	public ArrayList<Double> getSpyLows(HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjLows = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get("SPY")){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				((ArrayList<Double>) adjLows).add((Double) lineData.get(5));
			}
		}
		return adjLows;
	}
}
