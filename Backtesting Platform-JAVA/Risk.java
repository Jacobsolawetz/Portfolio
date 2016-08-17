import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class Risk {

	private double averageProfitPerTrade;
	private double averageReturnPerTrade;
	private double averageDuration;
	private double averageProfitPerDay;
	private double averageReturnPerDay;
	private double averageLeveragePerTrade;
	private double profitabilityPercentage;
	private double sharpeRatio;
	private double maxDrawdown;
	private double maxDrawdownDuration;
	private double portfolioBeta;
	private HashMap<String, Risk> sectorRiskMap;
	private int totalTrades;
	private double pToL;
	private double kelly;
	//Jake's testing
	private ArrayList<Double> cumReturns;
	private ArrayList<Double> dailyReturns;
	private ArrayList<ArrayList<Object>> spyData;
	private ArrayList<Double> returnList;
	private ArrayList<Double> spyDailyReturns;
	private ArrayList<Double> weightedDailyReturns;//daily returns changed from percent to decimal

	
	public Risk(Double portfolioStartValue, HashMap<Integer, ArrayList<Trade>> tradeMapCopy, ArrayList<ArrayList<Object>> spyData){
		
		
		//to keep track of the number of trades executed by each algorithm
		HashMap<Integer, Integer> tradesTriggered = new HashMap<Integer, Integer>();
		
		//find all algo numbers that were used -> in this realm every trade triggered is recorded
		Set<Integer> keys = new HashSet<Integer>();
		for(Integer key : tradeMapCopy.keySet()){
			for(Trade trade : tradeMapCopy.get(key)){
				keys.add(trade.getAlgoNumber());
			}
		}
		
		//initialize the hashMap
		for(Integer key : keys){
			tradesTriggered.put(key, 0);
		}
		
		ArrayList<Double> leverageList = new ArrayList<Double>();
		ArrayList<Integer> durationList = new ArrayList<Integer>();
		ArrayList<Double> changeList = new ArrayList<Double>(); //cash
		returnList = new ArrayList<Double>(); //percentage
		ArrayList<Date> dateList = new ArrayList<Date>();
		
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		//iterates through the map in chronological order from oldest date to newest
		for(Integer i : mapKeys){
			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
			
			if(daysTrades == null){
				continue;
			}
			
			//loop through the days trades
			for(Trade trade : daysTrades){
				try{
					double change = trade.getCustomReturn(trade.getDuration(), portfolioStartValue*(int)trade.getLeverage());
					double returnPercentage = trade.getCustomReturn(trade.getDuration());
					
					durationList.add(trade.getDuration());
					leverageList.add(trade.getLeverage());
					changeList.add(change);
					returnList.add(returnPercentage);
					dateList.add(trade.getDate());
					
					//adds to the map of executed trades for each algorithm
					tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
				} catch(IOException ioe){
					continue;
				}
			}
		}
		
		//calculate average profit per trade -> swayed by changing return per trade
		averageProfitPerTrade = round(getDoubleMean(changeList));
		//calculate average return per trade -> average return does not get influenced by changing capital allocations per trade
		//multiply by 100 for a percentage
		averageReturnPerTrade = getDoubleMean(returnList);
		//calculates average duration
		averageDuration = getIntMean(durationList);
		//calculates average profit per day
		averageProfitPerDay = round(averageProfitPerTrade/(double) averageDuration);
		//calculates average return per day
		//averageReturnPerDay = round(averageReturnPerTrade/(double) averageDuration);
		averageReturnPerDay = averageReturnPerTrade/(double) averageDuration;
		//calculate profitability percentage
		profitabilityPercentage = round(getProfitabilityPercentage(changeList));
		//calculate total trades
		totalTrades = changeList.size();
		//calculate profit to loss
		pToL = getPnL(changeList);
		//calculate average leverage per trade
		averageLeveragePerTrade = getDoubleMean(leverageList);
		//calculate sharpe ratio
		sharpeRatio = getSharpeRatio(getDailyReturns(tradeMapCopy));
		//calculate kelly
		kelly = kelly(returnList, getDailyReturns(tradeMapCopy));
		//calculate drawdowns
		calculateDrawdown(getDailyReturns(tradeMapCopy));
		this.spyData = spyData;
		
		//calculate daily returns to print //get spy daily returns
		getDailyReturns(tradeMapCopy);
		//calculate beta
//		weightedDailyReturns = new ArrayList<Double>();
//		for(Double entry: dailyReturns){
//			weightedDailyReturns.add(entry/100);
//		}
		portfolioBeta = calculateBeta(dailyReturns,spyDailyReturns);
		
	}
	
	//will not print sector analysis if sectorRiskMap is not set
	public void setSectorRiskMap(Double portfolioStartValue, HashMap<Integer, ArrayList<Trade>> tradeMapCopy, ArrayList<ArrayList<Object>> spyData){
		sectorRiskMap = calculateSectorAnalysis(portfolioStartValue, tradeMapCopy, spyData);
	}
	
	public double getPortfolioBeta(){
		return Math.round(portfolioBeta*100.0)/100.0;
	}
	
	public double pToL(){
		return pToL;
	}
	
	public int getTotalTrades(){
		return totalTrades;
	}
	
	public double getProfitabilityPercentage(){
		return profitabilityPercentage;
	}
	
	public double getAverageReturnPerDay(){
		return Math.round(averageReturnPerDay*10000.0)/100.0;
	}
	
	public double getAverageProfitPerDay(){
		return averageProfitPerDay;
	}
	
	public double getAverageDuration(){
		return round(averageDuration);
	}
	
	//is returned as a percentage
	public double getAverageReturnPerTrade(){
		return Math.round(averageReturnPerTrade*10000.0)/100.0;
	}
	
	public double getAverageProfitPerTrade(){
		return averageProfitPerTrade;
	}
	
	public double getAverageLeveragePerTrade(){
		return round(averageLeveragePerTrade);
	}
	
	public double getSharpeRatio(){
		return sharpeRatio;
	}
	
	public double getMaxDrawdown(){
		return maxDrawdown;
	}
	
	public double getMaxDrawdownDuration(){
		return maxDrawdownDuration;
	}
	
	//sector analysis creates Risk objects for each sector and returns a hashMap of those objects
	//with the corresponding sector ETF as the key
	public HashMap<String, Risk> calculateSectorAnalysis(Double portfolioStartValue, HashMap<Integer, ArrayList<Trade>> tradeMapCopy, ArrayList<ArrayList<Object>> spyData){
		HashMap<String, Risk> sectorAnalysisMap = new HashMap<String, Risk>();
		//create sector maps
		HashMap<Integer, ArrayList<Trade>> energyMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> materialsMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> industrialsMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> consumerDiscretionaryMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> consumerStaplesMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> healthcareMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> financialsMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> informationTechnologyMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> telecommunicationsMap = new HashMap<Integer, ArrayList<Trade>>();
		HashMap<Integer, ArrayList<Trade>> utilitiesMap = new HashMap<Integer, ArrayList<Trade>>();
		
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		//iterates through the map in chronological order from oldest date to newest
		for(Integer i : mapKeys){
			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
			
			if(daysTrades == null){
				continue;
			}
			
			ArrayList<Trade> energyTradeList = new ArrayList<Trade>();
			ArrayList<Trade> materialsTradeList = new ArrayList<Trade>();
			ArrayList<Trade> industrialsTradeList = new ArrayList<Trade>();
			ArrayList<Trade> consumerDiscretionaryTradeList = new ArrayList<Trade>();
			ArrayList<Trade> consumerStaplesTradeList = new ArrayList<Trade>();
			ArrayList<Trade> healthcareTradeList = new ArrayList<Trade>();
			ArrayList<Trade> financialsTradeList = new ArrayList<Trade>();
			ArrayList<Trade> informationTechnologyTradeList = new ArrayList<Trade>();
			ArrayList<Trade> telecommunicationsTradeList = new ArrayList<Trade>();
			ArrayList<Trade> utilitiesTradeList = new ArrayList<Trade>();
		
			//loop through the days trades
			for(Trade trade : daysTrades){
				try{
					String sectorETF = trade.getSymbolData().getSectorETF();
					
					switch(sectorETF){
						case "XLE":
							energyTradeList.add(trade);
							break;
						case "XLB":
							materialsTradeList.add(trade);
							break;
						case "XLI":
							industrialsTradeList.add(trade);
							break;
						case "XLY":
							consumerDiscretionaryTradeList.add(trade);
							break;
						case "XLP":
							consumerStaplesTradeList.add(trade);
							break;
						case "XLV":
							healthcareTradeList.add(trade);
							break;
						case "XLF":
							financialsTradeList.add(trade);
							break;
						case "XLK":
							informationTechnologyTradeList.add(trade);
							break;
						case "IYZ":
							telecommunicationsTradeList.add(trade);
							break;
						case "XLU":
							utilitiesTradeList.add(trade);
							break;
						default:
							throw new IOException("Invalid sector ETF " + sectorETF);
					}
					
				} catch(Exception e){ //happens when there is no sector ETF
					continue;
				}
			}
			
			energyMap.put(i, energyTradeList);
			materialsMap.put(i, materialsTradeList);
			industrialsMap.put(i, industrialsTradeList);
			consumerDiscretionaryMap.put(i, consumerDiscretionaryTradeList);
			consumerStaplesMap.put(i, consumerStaplesTradeList);
			healthcareMap.put(i, healthcareTradeList);
			financialsMap.put(i, financialsTradeList);
			informationTechnologyMap.put(i, informationTechnologyTradeList);
			telecommunicationsMap.put(i, telecommunicationsTradeList);
			utilitiesMap.put(i, utilitiesTradeList);
		}
		
		sectorAnalysisMap.put("XLE", new Risk(portfolioStartValue, energyMap, spyData));
		sectorAnalysisMap.put("XLB", new Risk(portfolioStartValue, materialsMap, spyData));
		sectorAnalysisMap.put("XLI", new Risk(portfolioStartValue, industrialsMap, spyData));
		sectorAnalysisMap.put("XLY", new Risk(portfolioStartValue, consumerDiscretionaryMap, spyData));
		sectorAnalysisMap.put("XLP", new Risk(portfolioStartValue, consumerStaplesMap, spyData));
		sectorAnalysisMap.put("XLV", new Risk(portfolioStartValue, healthcareMap, spyData));
		sectorAnalysisMap.put("XLF", new Risk(portfolioStartValue, financialsMap, spyData));
		sectorAnalysisMap.put("XLK", new Risk(portfolioStartValue, informationTechnologyMap, spyData));
		sectorAnalysisMap.put("IYZ", new Risk(portfolioStartValue, telecommunicationsMap, spyData));
		sectorAnalysisMap.put("XLU", new Risk(portfolioStartValue, utilitiesMap, spyData));
		
		return sectorAnalysisMap;
	}
	
	public HashMap<String, Risk> getSectorRiskMap(){
		return sectorRiskMap;
	}
	
	public double getDoubleMean(ArrayList<Double> doubleList){
		double sum = 0.0;
		for(Double num : doubleList){
			sum += num;
		}
		return sum/(double) doubleList.size();
	}
	
	public String getPrint(){
		
		
		System.out.println("the beta is: " + portfolioBeta);
		 
		String printString = "Profitability Percentage: " + getProfitabilityPercentage() + "%" + "\n";
		printString += "Average Profit Per Trade: " + getAverageProfitPerTrade() + "\n";
		printString += "Average Return Per Trade: " + getAverageReturnPerTrade() + "%" + "\n";
		printString += "Average Duration: " + getAverageDuration() + " days" + "\n";
		printString += "Average Profit Per Day: " + getAverageProfitPerDay() + "\n";
		printString += "Average Return Per Day: " + getAverageReturnPerDay() + "%" + "\n";
		printString += "Total Trades: " + getTotalTrades() + "\n";
		printString += "Average Leverage Per Trade: " + getAverageLeveragePerTrade() + "\n";
		printString += "Annualized Daily Sharpe Ratio: " + getSharpeRatio() + "\n";
		printString += "Portfolio Beta: " + getPortfolioBeta() + "\n";
		printString += "Max Drawdown: " + (getMaxDrawdown()*10000.0)/100.0 + "%" + "\n";
		printString += "Max Drawdown Duration: " + (int) getMaxDrawdownDuration() + " days" + "\n";
		printString += "Kelly : " + getKelly() + "\n";
		System.out.print(printString);
		
		//sector analysis table
		if(sectorRiskMap != null){
			final Object[][] table = new String[4][];
			table[0] = new String[] { "", "Energy", "Industrials", "Consumer Disc.", "Consumer Staples", "HealthCare",  "Financials", "Information Tech.", "Telecom", "Utilities", "Materials"};
			table[1] = new String[] { "ARPD:", Double.toString(sectorRiskMap.get("XLE").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLI").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLY").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLP").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLV").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLF").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLK").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("IYZ").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLU").getAverageReturnPerDay()), Double.toString(sectorRiskMap.get("XLB").getAverageReturnPerDay())};
			table[2] = new String[] { "Total Trades:", Integer.toString(sectorRiskMap.get("XLE").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLI").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLY").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLP").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLV").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLF").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLK").getTotalTrades()), Integer.toString(sectorRiskMap.get("IYZ").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLU").getTotalTrades()), Integer.toString(sectorRiskMap.get("XLB").getTotalTrades())};
			table[3] = new String[] { "Sharpe Ratio:", Double.toString(sectorRiskMap.get("XLE").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLI").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLY").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLP").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLV").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLF").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLK").getSharpeRatio()), Double.toString(sectorRiskMap.get("IYZ").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLU").getSharpeRatio()), Double.toString(sectorRiskMap.get("XLB").getSharpeRatio())};
			
			for (final Object[] row : table) {
			    System.out.format("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", row);
			}
		}
		
		return printString;
	}
	
	public double getIntMean(ArrayList<Integer> intList){
		double sum = 0.0;
		for(Integer num : intList){
			sum += (double) num;
		}
		return sum/intList.size();
	}
	
	public double getPnL(ArrayList<Double> changeList){
		double totalPositiveChange = 0.0;
		double totalNegativeChange = 0.0;
		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
		
		for(Double change : changeList){
			if(change > 0){
				positiveChangeList.add(change);
				totalPositiveChange = totalPositiveChange + change;
			} else if(change < 0){
				negativeChangeList.add(change);
				totalNegativeChange = totalNegativeChange + change;
			}
		}
		
		//calculate profit to loss
		return -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;	
	}
	
	public double getKelly() {
		return kelly;
	}
	
	public double getProfitabilityPercentage(ArrayList<Double> changeList){
		int positiveChangeCount = 0;
		for(Double change : changeList){
			if(change > 0){
				positiveChangeCount++;
			}
		}
		return ((double) positiveChangeCount / (double) changeList.size())*100.0;
	}
	
	//round to two decimal places
	public double round(Double num){
		return Math.round(num*100.0)/100.0;
	}
	
	//The Kelly formula will return the fraction of capital we should allocate to EACH trade
	//That is, if the kelly returns .3 and there are 10 trades on the day, we should have .3*10=3, 3 times leverage total in the brokerage account
	public double kelly(ArrayList<Double> returnList, ArrayList<Double> dailyReturns) {
		//first we calculate the factor by which to adjust the risk free rate. 
		double numTrades = returnList.size();
		double numDaysTraded = dailyReturns.size();
		double tradesToDays = numTrades/numDaysTraded;
		//calculate the number of trades in a year. x trades/y days = z trades/252 days... z trades = 252*tradesTodays
		double tradesPerYear = 252*tradesToDays;
		//we're assuming a risk free rate of 4%. Alex thinks this is a bit high haha. 
		DescriptiveStatistics kellyCalc = new DescriptiveStatistics();
		for(Double ret: returnList) {
			//subtract the risk free rate
			double entry = ret - (.04/tradesPerYear);
			kellyCalc.addValue(entry);
		}
		double mean = kellyCalc.getMean();
		double variance = kellyCalc.getVariance();
		double kelly = mean/variance;
		return kelly;
	}
	
	//I'm keeping this in here because it may be interesting to run and see if it yeilds the same result
	//provides the recommended leverage amount per trade by fortune's formula
	public double oldKelly (ArrayList<Double> returnList) {
		DescriptiveStatistics positiveReturnList = new DescriptiveStatistics();
		DescriptiveStatistics negativeReturnList = new DescriptiveStatistics();
		double countPositive=0.0;
		double countNegative=0.0;

		for(Double ret : returnList){
			if(ret > 0){
				positiveReturnList.addValue(ret);
				countPositive++;
				
			} else if(ret < 0){
				negativeReturnList.addValue(ret);
				countNegative++;
			}
		}
		double winningPercentage = countPositive/(countNegative+countPositive);
		double averageGain = positiveReturnList.getMean();
		double averageLoss = negativeReturnList.getMean();
		double gainRatio = Math.abs(averageGain/averageLoss);
		double kelly = winningPercentage - ((1-winningPercentage)/gainRatio);
		return kelly;
		
	}
	
	//provides a daily annualized sharpe ratio
	public double getSharpeRatio (ArrayList<Double> dailyReturns){
		DescriptiveStatistics stats = new DescriptiveStatistics();
	    for(double item : dailyReturns) {
	        stats.addValue(item);
	    }
	    
	    double mean1 = stats.getMean();
	    double std1 = stats.getStandardDeviation();
	    double sharpeRatio1 = ((mean1 - (.04/252) ) / std1) * Math.sqrt(252);
	    return Math.round(sharpeRatio1*1000.0)/1000.0;
	}
	
	public void calculateDrawdown(ArrayList<Double> dailyReturnsUnweighted) {
		
		ArrayList<Double> dailyReturns = new ArrayList<Double>();
		for(Double entry: dailyReturnsUnweighted) {
			double entry2 = entry/100; //adjusts daily returns from a percent to a decimal for the following script
			dailyReturns.add(entry2);
		}
		//first calculate cumulative returns. 
		cumReturns = new ArrayList<Double>();
		cumReturns.add(dailyReturns.get(0));
		int length = dailyReturns.size();
		for(int i = 1; i<length;i++) {
			double entry = (1+cumReturns.get(i-1))*(1+dailyReturns.get(i))-1;//the previous cumulative returns multiplied by the daily
			cumReturns.add(entry);
		}
		// next we calculate the highwater mark for each day. This is the highest value the cumulative returns have been at. 
		ArrayList<Double> highWater = new ArrayList<Double>();
		highWater.add(cumReturns.get(0));
		int length2 = cumReturns.size();
		for(int j = 1; j<length2; j++){
			double entry;
			if(highWater.get(j-1)>cumReturns.get(j)){
				entry = highWater.get(j-1); }
			else{ entry=cumReturns.get(j); }
			highWater.add(entry);
		}
		//next we create a list of drawdowns for each day by comparing the cumulative return to the high water mark.
		//if they are the same the portfolio is increasing and the drawdown is 0/nonexistent 
		ArrayList<Double> drawDowns = new ArrayList<Double>();
		DescriptiveStatistics maxCalc = new DescriptiveStatistics();
		for(int j=0; j<length2; j++){
			double entry = (1+highWater.get(j))/(1+cumReturns.get(j))-1;
			drawDowns.add(entry);
			maxCalc.addValue(entry);
		}
		
		//we look at the max of the draw down list to see which is the maximum draw down in the strategy
		maxDrawdown = maxCalc.getMax();
		//next we're going to calculate the maximum drawdown duration
		//if the entry in the drawdown array list is 0, we know the portfolio has made a cumulative profit
		//thus we'll reset the days of drawdown count. Then we'll take a max of the list. 
		ArrayList<Double> daysDown = new ArrayList<Double>();
		DescriptiveStatistics downCalc = new DescriptiveStatistics();
		for(int j=0; j<length2; j++){
			if(drawDowns.get(j)==0.0){
				daysDown.add(0.0);
				downCalc.addValue(0.0);
			}
			else{
				double entry = daysDown.get(j-1)+1;//add a day to the count
				daysDown.add(entry);
				downCalc.addValue(entry);
			}
		}
		
		maxDrawdownDuration = downCalc.getMax();
	}
	
	public ArrayList<Double> getSpyDailyReturns (HashMap<Integer, ArrayList<Trade>> tradeMap) {
		
		ArrayList<Integer> intDateKeys = new ArrayList<Integer>();
		
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
		
		ArrayList<Double> spyReturns = new ArrayList<Double>();
		
		Collections.sort(intDateKeys);
		Collections.reverse(intDateKeys);
		//intDateKeys is sorted from oldest to newest
		
		for(Integer key: intDateKeys) {
			int count = 0;
			
			for(ArrayList<Object> lineData : spyData){
				if(((Date) lineData.get(1)).compareTo(tradeMap.get(key).get(0).getDate()) == 0){
					double newClose = (Double) lineData.get(6);
					double oldClose = (Double) spyData.get(count).get(6);
					spyReturns.add((Double) lineData.get(6));
					
				}
				count++;
			
			}
			
		}
		
		return spyReturns;
	}
	
	public ArrayList<Double> getDailyReturns (HashMap<Integer, ArrayList<Trade>> tradeMap) {
		
		ArrayList<Integer> intDateKeys = new ArrayList<Integer>();
		
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
		HashMap<Integer, Double> returnsMap = new HashMap<Integer, Double>();
		
		//initialize hashMap of the specific dates we are looking at
		
		Collections.sort(intDateKeys);
		Collections.reverse(intDateKeys);
		//intDateKeys is sorted from oldest to newest
		spyDailyReturns = new ArrayList<Double>();
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
					  if(returnsMap.keySet().contains(key-k)){
						  double ret0 = returnsMap.get(key-k);
						  returnsMap.put(key-k, ret1 + ret0);
					  } else { //if the key was not previously in the map, it puts it in
						  returnsMap.put(key-k, ret1);
						  //if the key was not in the map then it needs an SpyReturn
						  double spyRet= (trade.getSpyCloses().get(k) - trade.getSpyCloses().get(k-1))/trade.getSpyCloses().get(k-1);
						  spyDailyReturns.add(spyRet);
					  }
				  }
				} catch(IOException ioe){
					continue;
				}
			  
			}
		
		}
		
		//sorts by natural order of the keys from newest to oldest
		SortedSet<Integer> keys = new TreeSet<Integer>(returnsMap.keySet());
		
		
		dailyReturns = new ArrayList<Double>();
		for(Integer key: keys) {
			dailyReturns.add(returnsMap.get(key));
		}
		
		Collections.reverse(dailyReturns);
		Collections.reverse(spyDailyReturns);
		
		return dailyReturns;
	}
	
	public double calculateBeta(ArrayList<Double> stockReturns, ArrayList<Double> spyReturns) {
		
		DescriptiveStatistics stats2 = new DescriptiveStatistics();
		for (double item : stockReturns) {
			stats2.addValue(item);
		}

		DescriptiveStatistics stats1 = new DescriptiveStatistics();
		for (double item1 : spyReturns) {
			stats1.addValue(item1);
		}

		double var = stats1.getVariance();
		double covar;
		double EX = stats1.getMean();
		double EY = stats2.getMean();
		double sum = 0;

		
		int b = stockReturns.size();

		for (int a = 0; a < b; a++) {
			sum += (spyReturns.get(a) - EX) * (stockReturns.get(a) - EY);
		}

		double denom = stockReturns.size() - 1;

		covar = sum / (denom);

		double beta = covar / var;

		return beta;
		
	}
}
