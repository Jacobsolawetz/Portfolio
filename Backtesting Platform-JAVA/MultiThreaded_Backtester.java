import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MultiThreaded_Backtester {
	
	
	private static ArrayList<Date> dateKeySet;
	private int startIndex;
	private int endIndex;
	@SuppressWarnings("unused")
	private HashMap<String, ArrayList<ArrayList<Object>>> output;
	@SuppressWarnings("unused")
	private HashMap<Date, ArrayList<ArrayList<Object>>> output2;
	@SuppressWarnings("unused")
	private HashMap<Date, ArrayList<ArrayList<Object>>> output2Assumption;
	private ArrayList<ArrayList<Object>> spyData;
	
	public static void main(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException {
		@SuppressWarnings("unused")
		MultiThreaded_Backtester mtb = new MultiThreaded_Backtester();
	}
	
	public MultiThreaded_Backtester() throws IOException, ParseException, InterruptedException{
		//read in AAPL keyset for date iteration (starts with newest date at get(0))
		//dateKeySet = getDateKeySet();
		//output = readOutput();
		//output2 = readOutput2(false);
		//output2Assumption = readOutput2(true);
		
		//offset for loop
		startIndex = 400; //for forward testing the value is 1600
		endIndex = 250; //for forward testing the value is 10
		
		dateKeySet = getDateKeySet();
		final HashMap<String, ArrayList<ArrayList<Object>>> output = readOutput();
		final HashMap<Date, ArrayList<ArrayList<Object>>> output2 = readOutput2(false);
		//HashMap<Date, ArrayList<ArrayList<Object>>> output2Assumption = readOutput2(true);
		
		//adds all data for SPY
		spyData = output.get("SPY");
		
		
		//Infrastructure set up print
		//System.out.println("HashMaps initialized via commission motherfucker");
		
		//create the appropriate number of threads
		ExecutorService executorService = Executors.newFixedThreadPool(dateKeySet.size() - startIndex - endIndex);
		
		//create list of callables
		List<Callable<HashMap<Integer, ArrayList<Trade>>>> callables = new ArrayList<Callable<HashMap<Integer, ArrayList<Trade>>>>();
		
		//loops through date key indexes in descending order since newest date is at position 0
		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){ //change this loop to change backtest duration
			int threadID = i;
			Date day = dateKeySet.get(threadID);
			callables.add(new TaskAsCallable(threadID, day, output2, dateKeySet, output));	
		}
		
		//returns a list of Futures holding their status and results when all complete
        List<Future<HashMap<Integer, ArrayList<Trade>>>> tasks = executorService.invokeAll(callables);
		    
        HashMap<Integer, ArrayList<Trade>> tradeMap = new HashMap<Integer, ArrayList<Trade>>();
        
        for(Future<HashMap<Integer, ArrayList<Trade>>> task : tasks)
        {
            try{
            	tradeMap.putAll(task.get());
            } catch(ExecutionException ee){
            	continue;
            }
        }
		
        SymbolReferencer sr = new SymbolReferencer();
        //for error testing
        Set<String> errorSymbols = new HashSet<String>();
		//go through the trade map and add symbol data
        for(Integer key : tradeMap.keySet()){
        	for(Trade trade : tradeMap.get(key)){
        		try{
        			trade.setSymbolData(sr.getSymbol(trade.getSymbol()));
        		} catch(Exception e){
        			//System.out.println("No symbol data for " + trade.getSymbol());
        			errorSymbols.add(trade.getSymbol());
        		}
        	}
        }
		
		
		//*****************Assumptions to be customized*****************
		double startValue = 1000000.0;
		int maxPositionsPerDay = 8; //per day! -> only applies if allTrades = false
		int maxPortfolioPositionsAtAnyTime = 20; // -> only applies if allTrades = false
		
//		getRealmOneResults(tradeMap, startValue);
//		System.out.println();
		getRealmTwoResults(tradeMap, startValue);
		System.out.println();
		getRealmThreeResults(tradeMap, startValue);
		System.out.println();
		getRealmFourResults(tradeMap, startValue);
		System.out.println();
//		getOldRealmThreeResults(tradeMap, startValue);
//		System.out.println();
//		getRealmFiveResults(tradeMap, startValue);
//		System.out.println();
//		getRealmSixResults(tradeMap, startValue);
		
		
//		System.out.println(tradeMap.get(100).get(0).getDates().get(0));
//		System.out.println(tradeMap.get(100).get(0).getDates().get(1));
//		System.out.println(tradeMap.get(100).get(0).getDates().get(2));
//		System.out.println(tradeMap.get(100).get(0).getDates().get(3));
		
		//shutdown the thread pool
        executorService.shutdown(); //can this be moved?
        
	}
				
	//Realm#1 -> Long/short and/or market adjusted with SPY strategy with constant investment/all trades executed with start value capital	
	public void getRealmOneResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = new HashMap<Integer, ArrayList<Trade>>(copyMap(tradeMap));
		
		//Realm#1 does not change any aspect of each trade -> leverage and portfolio start value remain the same
		
		Risk risk = new Risk(portfolioStartValue, tradeMapCopy, spyData);
		risk.setSectorRiskMap(portfolioStartValue, tradeMapCopy, spyData);
		
		System.out.println("Realm #1: Mixed Indication Constant Investment:");
		risk.getPrint();
		
		//eliminate some sectors - healthCare, financials, telecom
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		HashMap<Integer, ArrayList<Trade>> specificSectorTradeMap = new HashMap<Integer, ArrayList<Trade>>();
		
		for(Integer key : mapKeys){
			ArrayList<Trade> daysTrades = new ArrayList<Trade>();
			for(Trade trade : tradeMapCopy.get(key)){
				try{
					String sectorETF = trade.getSymbolData().getSectorETF();
					
					switch(sectorETF){
						case "XLE":
							daysTrades.add(trade);
							break;
						case "XLB":
							daysTrades.add(trade);
							break;
						case "XLI":
							daysTrades.add(trade);
							break;
						case "XLY":
							daysTrades.add(trade);
							break;
						case "XLP":
							break;
						case "XLV":
							daysTrades.add(trade);
							break;
						case "XLF":
							break;
						case "XLK":
							daysTrades.add(trade);
							break;
						case "IYZ":
							daysTrades.add(trade);
							break;
						case "XLU":
							break;
						default:
							throw new IOException("Invalid sector ETF " + sectorETF);
					}
				} catch(Exception e){
					continue; //no sector ETF
				}
			}
			specificSectorTradeMap.put(key, daysTrades);
		}
		
		Risk sectorRisk = new Risk(portfolioStartValue, specificSectorTradeMap, spyData);
		sectorRisk.setSectorRiskMap(portfolioStartValue, specificSectorTradeMap, spyData);
		
		System.out.println("Realm #1: Mixed Indication Constant Investment w/o XLF, XLV, IYZ:");
		sectorRisk.getPrint();
		
	}
	
	//Realm#2 -> Long/short only strategy with constant investment/all trades executed with start value capital	
	public void getRealmTwoResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = new HashMap<Integer, ArrayList<Trade>>(copyMap(tradeMap));
		
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		for(Integer key : mapKeys){
			for(Trade trade : tradeMapCopy.get(key)){
				if(trade.getIndication().substring(0,4).equals("long")){
					trade.setIndication("long Reg");
				} else {
					trade.setIndication("short Reg");
				}
			}
		}
		
		Risk risk = new Risk(portfolioStartValue, tradeMapCopy, spyData);
		risk.setSectorRiskMap(portfolioStartValue, tradeMapCopy, spyData);
		
		System.out.println("Realm #2: Long/Short Only Constant Investment:");
		risk.getPrint();
	}
	
	//Realm#3 -> Long/short Dollar Neutral only strategy with constant investment/all trades executed with start value capital	
	public void getRealmThreeResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		//copy the tradeMap so that changes don't change the original
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = new HashMap<Integer, ArrayList<Trade>>(copyMap(tradeMap));
		
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		for(Integer key : mapKeys){
			for(Trade trade : tradeMapCopy.get(key)){
				if(trade.getIndication().substring(0,4).equals("long")){
					trade.setIndication("long DN");
				} else {
					trade.setIndication("short DN");
				}
			}
		}
		
		Risk risk = new Risk(portfolioStartValue, tradeMapCopy, spyData);
		risk.setSectorRiskMap(portfolioStartValue, tradeMapCopy, spyData);
		
		System.out.println("Realm #3: Long/Short Dollar Neutral Only Constant Investment:");
		risk.getPrint();
	}
	
	//Realm#4 -> Long/short Market Beta Neutral only strategy with constant investment/all trades executed with start value capital	
	public void getRealmFourResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		//copy the tradeMap so that changes don't change the original
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = new HashMap<Integer, ArrayList<Trade>>(copyMap(tradeMap));
		
		//get keys of the tradeMap
		ArrayList<Integer> mapKeys = new ArrayList<Integer>(tradeMapCopy.keySet());
		//sorts in ascending order but need descending order
		Collections.sort(mapKeys);
		//reverse
		Collections.reverse(mapKeys);
		
		for(Integer key : mapKeys){
			for(Trade trade : tradeMapCopy.get(key)){
				if(trade.getIndication().substring(0,4).equals("long")){
					trade.setIndication("long MBN");
				} else {
					trade.setIndication("short MBN");
				}
			}
		}
		
		Risk risk = new Risk(portfolioStartValue, tradeMapCopy, spyData);
		risk.setSectorRiskMap(portfolioStartValue, tradeMapCopy, spyData);

		System.out.println("Realm #4: Market Beta Neutral Only Constant Investment:");
		risk.getPrint();
	}
	
	//Realm#3 -> Long/short Dollar Neutral only strategy with constant investment/all trades executed with start value capital	
	public void getOldRealmThreeResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		HashMap<Integer, ArrayList<Trade>> tradeMapR3 = new HashMap<Integer, ArrayList<Trade>>();
		
		//copy the tradeMap so that changes don't change the original
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
		
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
		
		Sharpe sharpe = new Sharpe();
		
		//Setting up R3 infrastructure
		Double averageProfitPerTrade = 0.0;
		Double profitabilityPercentage = 0.0;
		int totalTrades = 0;
		Double pToL = 0.0;
		
		ArrayList<Double> changeList = new ArrayList<Double>();
		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
		ArrayList<Double> neutralChangeList = new ArrayList<Double>();
		double totalPositiveChange = 0.0;
		double totalNegativeChange = 0.0;
		
		double changeSum = 0.0;
		
		//Realm#3 running
		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
			//double daysChange = 0.0;
			if(daysTrades == null){
				continue;
			}
			
			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
			//loop through the days trades
			for(Trade trade : daysTrades){
				double change = 0.0;
				if(trade.getIndication().equals("long DN")){
					try{
						change = trade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(trade);
					} catch(IOException ioe){
						continue;
					}
				} else if(trade.getIndication().equals("short DN")){
					try{
						change = trade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(trade);
					} catch(IOException ioe){
						continue;
					}
				} else if(trade.getIndication().equals("long Reg")){
					Trade tempTrade = trade;
					tempTrade.setIndication("long DN");
					try{
						change = tempTrade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(tempTrade);
					} catch(IOException ioe){
						continue;
					}
				} else if(trade.getIndication().equals("short Reg")){
					Trade tempTrade = trade;
					tempTrade.setIndication("short DN");
					try{
						change = tempTrade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(tempTrade);
					} catch(IOException ioe){
						continue;
					}
				}
				
				changeList.add(change);
				if(change > 0){
					positiveChangeList.add(change);
					totalPositiveChange = totalPositiveChange + change;
				} else if(change < 0){
					negativeChangeList.add(change);
					totalNegativeChange = totalNegativeChange + change;
				} else {
					neutralChangeList.add(change);
				}
				
				
				changeSum = changeSum + change;
				//daysChange = daysChange + change;
				//adds to the map of executed trades for each algorithm
				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
			}
			//add the alteredDaysTrades to the trademap for risk
			tradeMapR3.put(i, alteredDaysTrades);
		}
		
		//calculate average profit per trade
		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
		//calculate profitability percentage
		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
		//calculate total trades
		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
		//calculate profit to loss
		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
		
		//print R3 results
		System.out.println("Realm #3: Long/Short Market Adjusted Only Constant Investment:");
		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
		System.out.println("Profitability Percentage: " + profitabilityPercentage);
		System.out.println("Total Trades: " + totalTrades);
		System.out.println("Profit to Loss Ratio: " + pToL);
		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR3, dateKeySet))));
		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR3, dateKeySet)));
		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR3, dateKeySet));
		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR3, dateKeySet));
		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR3, dateKeySet));
		//print number of trades triggered per algorithm
		for(Integer algoNum : tradesTriggered.keySet()){
			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
		}
	}
	
	//Realm#4 -> Beta Leveraged Mixed indication strategy with constant investment/all trades executed with start value capital*Beta leverage
//	public void getRealmFourResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
//		//for the sharpeRatio and risk metrics to take into account changes in the trading style
//		HashMap<Integer, ArrayList<Trade>> tradeMapR4 = new HashMap<Integer, ArrayList<Trade>>();
//		
//		//copy the tradeMap so that changes don't change the original
//		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
//		
//		//to keep track of the number of trades executed by each algorithm
//		HashMap<Integer, Integer> tradesTriggered = new HashMap<Integer, Integer>();
//		
//		//find all algo numbers that were used -> in this realm every trade triggered is recorded
//		Set<Integer> keys = new HashSet<Integer>();
//		for(Integer key : tradeMapCopy.keySet()){
//			for(Trade trade : tradeMapCopy.get(key)){
//				keys.add(trade.getAlgoNumber());
//			}
//		}
//		
//		//initialize the hashMap
//		for(Integer key : keys){
//			tradesTriggered.put(key, 0);
//		}
//		
//		Sharpe sharpe = new Sharpe();
//		
//		//Setting up R4 infrastructure
//		Double averageProfitPerTrade = 0.0;
//		Double averageLeveragePerTrade = 0.0;
//		Double profitabilityPercentage = 0.0;
//		int totalTrades = 0;
//		Double pToL = 0.0;
//		
//		ArrayList<Double> changeList = new ArrayList<Double>();
//		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
//		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
//		ArrayList<Double> neutralChangeList = new ArrayList<Double>();
//		double totalPositiveChange = 0.0;
//		double totalNegativeChange = 0.0;
//		
//		ArrayList<Double> leverageList = new ArrayList<Double>();
//		
//		double changeSum = 0.0;
//		
//		//Realm#4 running
//		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
//			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
//			//double daysChange = 0.0;
//			if(daysTrades == null){
//				continue;
//			}
//			
//			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
//			//loop through the days trades
//			for(Trade trade : daysTrades){
//				double change = 0.0;
//				double leverage = (double) 2.0/((double) 1.0 + Math.abs(trade.getStockBeta()));
//				trade.setLeverage(leverage);
//				try{
//					change = trade.getIntededReturn(portfolioStartValue*leverage);
//					alteredDaysTrades.add(trade);
//				} catch(IOException ioe){
//					continue;
//				}
//				
//				leverageList.add(leverage);
//				changeList.add(change);
//				if(change > 0){
//					positiveChangeList.add(change);
//					totalPositiveChange = totalPositiveChange + change;
//				} else if(change < 0){
//					negativeChangeList.add(change);
//					totalNegativeChange = totalNegativeChange + change;
//				} else {
//					neutralChangeList.add(change);
//				}
//				
//				
//				changeSum = changeSum + change;
//				//daysChange = daysChange + change;
//				//adds to the map of executed trades for each algorithm
//				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
//			}
//			//add the alteredDaysTrades to the trademap for risk
//			tradeMapR4.put(i, alteredDaysTrades);
//		}
//		
//		//calculate average profit per trade
//		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
//		//calculate profitability percentage
//		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
//		//calculate total trades
//		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
//		//calculate profit to loss
//		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
//		//calculate average leverage per trade
//		averageLeveragePerTrade = getMean(leverageList);
//		
//		//print R3 results
//		System.out.println("Realm #4: Beta Leveraged Mixed Indication Constant Investment:");
//		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
//		System.out.println("Average Leverage Per Trade: "  + averageLeveragePerTrade);
//		System.out.println("Profitability Percentage: " + profitabilityPercentage);
//		System.out.println("Total Trades: " + totalTrades);
//		System.out.println("Profit to Loss Ratio: " + pToL);
//		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet))));
//		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR4, dateKeySet)));
//		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//print number of trades triggered per algorithm
//		for(Integer algoNum : tradesTriggered.keySet()){
//			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
//		}
//	}
//	
//	//Realm#5 -> Beta leveraged long/short only strategy with constant investment/all trades executed with start value capital	
//	public void getRealmFourResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
//		//for the sharpeRatio and risk metrics to take into account changes in the trading style
//		HashMap<Integer, ArrayList<Trade>> tradeMapR4 = new HashMap<Integer, ArrayList<Trade>>();
//		
//		//copy the tradeMap so that changes don't change the original
//		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
//		
//		//to keep track of the number of trades executed by each algorithm
//		HashMap<Integer, Integer> tradesTriggered = new HashMap<Integer, Integer>();
//		
//		//find all algo numbers that were used -> in this realm every trade triggered is recorded
//		Set<Integer> keys = new HashSet<Integer>();
//		for(Integer key : tradeMapCopy.keySet()){
//			for(Trade trade : tradeMapCopy.get(key)){
//				keys.add(trade.getAlgoNumber());
//			}
//		}
//		
//		//initialize the hashMap
//		for(Integer key : keys){
//			tradesTriggered.put(key, 0);
//		}
//		
//		Sharpe sharpe = new Sharpe();
//		
//		//Setting up R4 infrastructure
//		Double averageProfitPerTrade = 0.0;
//		Double averageLeveragePerTrade = 0.0;
//		Double profitabilityPercentage = 0.0;
//		int totalTrades = 0;
//		Double pToL = 0.0;
//		
//		ArrayList<Double> changeList = new ArrayList<Double>();
//		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
//		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
//		ArrayList<Double> neutralChangeList = new ArrayList<Double>();
//		double totalPositiveChange = 0.0;
//		double totalNegativeChange = 0.0;
//		
//		ArrayList<Double> leverageList = new ArrayList<Double>();
//		
//		double changeSum = 0.0;
//		
//		//Realm#4 running
//		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
//			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
//			//double daysChange = 0.0;
//			if(daysTrades == null){
//				continue;
//			}
//			
//			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
//			//loop through the days trades
//			for(Trade trade : daysTrades){
//				double change = 0.0;
//				double leverage = (double) 2.0/((double) 1.0 + Math.abs(trade.getStockBeta()));
//				trade.setLeverage(leverage);
//				try{
//					change = trade.getIntededReturn(portfolioStartValue*leverage);
//					alteredDaysTrades.add(trade);
//				} catch(IOException ioe){
//					continue;
//				}
//				
//				leverageList.add(leverage);
//				changeList.add(change);
//				if(change > 0){
//					positiveChangeList.add(change);
//					totalPositiveChange = totalPositiveChange + change;
//				} else if(change < 0){
//					negativeChangeList.add(change);
//					totalNegativeChange = totalNegativeChange + change;
//				} else {
//					neutralChangeList.add(change);
//				}
//				
//				
//				changeSum = changeSum + change;
//				//daysChange = daysChange + change;
//				//adds to the map of executed trades for each algorithm
//				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
//			}
//			//add the alteredDaysTrades to the trademap for risk
//			tradeMapR4.put(i, alteredDaysTrades);
//		}
//		
//		//calculate average profit per trade
//		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
//		//calculate profitability percentage
//		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
//		//calculate total trades
//		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
//		//calculate profit to loss
//		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
//		//calculate average leverage per trade
//		averageLeveragePerTrade = getMean(leverageList);
//		
//		//print R3 results
//		System.out.println("Realm #4: Beta Leveraged Mixed Indication Constant Investment:");
//		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
//		System.out.println("Average Leverage Per Trade: "  + averageLeveragePerTrade);
//		System.out.println("Profitability Percentage: " + profitabilityPercentage);
//		System.out.println("Total Trades: " + totalTrades);
//		System.out.println("Profit to Loss Ratio: " + pToL);
//		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet))));
//		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR4, dateKeySet)));
//		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//print number of trades triggered per algorithm
//		for(Integer algoNum : tradesTriggered.keySet()){
//			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
//		}
//	}
//		
//	//Realm#4 -> Mixed indication strategy with constant investment/all trades executed with start value capital	
//	public void getRealmFourResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
//		//copy the tradeMap so that changes don't change the original
//		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
//		
//		//Realm#4 running
//		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
//			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
//			//double daysChange = 0.0;
//			if(daysTrades == null){
//				continue;
//			}
//			
//			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
//			//loop through the days trades
//			for(Trade trade : daysTrades){
//				double change = 0.0;
//				double leverage = (double) 2.0/((double) 1.0 + Math.abs(trade.getStockBeta()));
//				trade.setLeverage(leverage);
//				try{
//					change = trade.getIntededReturn(portfolioStartValue*leverage);
//					alteredDaysTrades.add(trade);
//				} catch(IOException ioe){
//					continue;
//				}
//				
//				leverageList.add(leverage);
//				changeList.add(change);
//				if(change > 0){
//					positiveChangeList.add(change);
//					totalPositiveChange = totalPositiveChange + change;
//				} else if(change < 0){
//					negativeChangeList.add(change);
//					totalNegativeChange = totalNegativeChange + change;
//				} else {
//					neutralChangeList.add(change);
//				}
//				
//				
//				changeSum = changeSum + change;
//				//daysChange = daysChange + change;
//				//adds to the map of executed trades for each algorithm
//				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
//			}
//			//add the alteredDaysTrades to the trademap for risk
//			tradeMapR4.put(i, alteredDaysTrades);
//		}
//		
//		//calculate average profit per trade
//		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
//		//calculate profitability percentage
//		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
//		//calculate total trades
//		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
//		//calculate profit to loss
//		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
//		//calculate average leverage per trade
//		averageLeveragePerTrade = getMean(leverageList);
//		
//		//print R3 results
//		System.out.println("Realm #4: Beta Leveraged Mixed Indication Constant Investment:");
//		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
//		System.out.println("Average Leverage Per Trade: "  + averageLeveragePerTrade);
//		System.out.println("Profitability Percentage: " + profitabilityPercentage);
//		System.out.println("Total Trades: " + totalTrades);
//		System.out.println("Profit to Loss Ratio: " + pToL);
//		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet))));
//		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR4, dateKeySet)));
//		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR4, dateKeySet));
//		//print number of trades triggered per algorithm
//		for(Integer algoNum : tradesTriggered.keySet()){
//			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
//		}
//	}
	
	//Realm#5 -> Like Realm#4 but based on raw variance (standard deviation of returns) instead of beta. Mixed indication strategy with constant investment/all trades executed with start value capital	
	public void getRealmSevenResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		HashMap<Integer, ArrayList<Trade>> tradeMapR5 = new HashMap<Integer, ArrayList<Trade>>();
		
		//copy the tradeMap so that changes don't change the original
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
		
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
		
		Sharpe sharpe = new Sharpe();
		
		//Setting up R5 infrastructure
		Double averageProfitPerTrade = 0.0;
		Double averageLeveragePerTrade = 0.0;
		Double profitabilityPercentage = 0.0;
		int totalTrades = 0;
		Double pToL = 0.0;
		
		ArrayList<Double> changeList = new ArrayList<Double>();
		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
		ArrayList<Double> neutralChangeList = new ArrayList<Double>();
		double totalPositiveChange = 0.0;
		double totalNegativeChange = 0.0;
		
		ArrayList<Double> leverageList = new ArrayList<Double>();
		
		double changeSum = 0.0;
		
		//Realm#5 running
		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
			//double daysChange = 0.0;
			if(daysTrades == null){
				continue;
			}
			
			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
			//loop through the days trades
			for(Trade trade : daysTrades){
				double change = 0.0;
				//can change to hit the desired average leverage.
				double leverage = (double) 2.0/((double) 1.0 + 20*Math.abs(trade.getVariance()));
				trade.setLeverage(leverage);
				try{
					change = trade.getIntededReturn(portfolioStartValue*leverage);
					alteredDaysTrades.add(trade);
				} catch(IOException ioe){
					continue;
				}
				
				leverageList.add(leverage);
				changeList.add(change);
				if(change > 0){
					positiveChangeList.add(change);
					totalPositiveChange = totalPositiveChange + change;
				} else if(change < 0){
					negativeChangeList.add(change);
					totalNegativeChange = totalNegativeChange + change;
				} else {
					neutralChangeList.add(change);
				}
				
				
				changeSum = changeSum + change;
				//daysChange = daysChange + change;
				//adds to the map of executed trades for each algorithm
				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
			}
			//add the alteredDaysTrades to the trademap for risk
			tradeMapR5.put(i, alteredDaysTrades);
		}
		
		
		
		
		//calculate average profit per trade
		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
		//calculate profitability percentage
		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
		//calculate total trades
		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
		//calculate profit to loss
		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
		//calculate average leverage per trade
		averageLeveragePerTrade = getMean(leverageList);
		
		//print R5 results
		System.out.println("Realm #5: Variance Leveraged Mixed Indication Constant Investment: ");
		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
		System.out.println("Average Leverage Per Trade: "  + averageLeveragePerTrade);
		System.out.println("Profitability Percentage: " + profitabilityPercentage);
		System.out.println("Total Trades: " + totalTrades);
		System.out.println("Profit to Loss Ratio: " + pToL);
		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR5, dateKeySet))));
		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR5, dateKeySet)));
		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR5, dateKeySet));
		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR5, dateKeySet));
		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR5, dateKeySet));
		//print number of trades triggered per algorithm
		for(Integer algoNum : tradesTriggered.keySet()){
			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
		}
	}
		
	//Realm#6 -> Leveraged with variance. Long/short only strategy with constant investment/all trades executed with start value capital. 	
	public void getRealmEightResults(HashMap<Integer, ArrayList<Trade>> tradeMap, double portfolioStartValue) throws ParseException{
		//for the sharpeRatio and risk metrics to take into account changes in the trading style
		HashMap<Integer, ArrayList<Trade>> tradeMapR6 = new HashMap<Integer, ArrayList<Trade>>();
		
		//copy the tradeMap so that changes don't change the original
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = copyMap(tradeMap);
		
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
		
		Sharpe sharpe = new Sharpe();
		
		//Setting up R6 infrastructure
		Double averageProfitPerTrade = 0.0;
		Double profitabilityPercentage = 0.0;
		Double averageLeveragePerTrade = 0.0;
		int totalTrades = 0;
		Double pToL = 0.0;
		
		ArrayList<Double> changeList = new ArrayList<Double>();
		ArrayList<Double> positiveChangeList = new ArrayList<Double>();
		ArrayList<Double> negativeChangeList = new ArrayList<Double>();
		ArrayList<Double> neutralChangeList = new ArrayList<Double>();
		double totalPositiveChange = 0.0;
		double totalNegativeChange = 0.0;
		
		double changeSum = 0.0;
		ArrayList<Double> leverageList = new ArrayList<Double>();
		//Realm#6 running
		for(int i = dateKeySet.size() - startIndex; i >= endIndex; i--){
			ArrayList<Trade> daysTrades = tradeMapCopy.get(i);
			//double daysChange = 0.0;
			if(daysTrades == null){
				continue;
			}
			
			ArrayList<Trade> alteredDaysTrades = new ArrayList<Trade>();
			//loop through the days trades
			for(Trade trade : daysTrades){
				double change = 0.0;
				
				double leverage = (double) 2.0/((double) 1.0 + 20*Math.abs(trade.getVariance()));
				trade.setLeverage(leverage);
				
				
				leverageList.add(leverage);
				
				
				
				if(trade.getIndication().equals("long DN")){
					Trade tempTrade = trade;
					tempTrade.setIndication("long Reg");
					try{
						change = tempTrade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(tempTrade);
					} catch(IOException ioe){
						continue;
					}
					//trade.setIndication("long PV");
				} else if(trade.getIndication().equals("short DN")){
					Trade tempTrade = trade;
					tempTrade.setIndication("short Reg");
					try{
						change = tempTrade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(tempTrade);
					} catch(IOException ioe){
						continue;
					}
				} else if(trade.getIndication().equals("long Reg")){
					try{
						change = trade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(trade);
					} catch(IOException ioe){
						continue;
					}
				} else if(trade.getIndication().equals("short Reg")){
					try{
						change = trade.getIntededReturn(portfolioStartValue);
						alteredDaysTrades.add(trade);
					} catch(IOException ioe){
						continue;
					}
				}
				
				changeList.add(change);
				if(change > 0){
					positiveChangeList.add(change);
					totalPositiveChange = totalPositiveChange + change;
				} else if(change < 0){
					negativeChangeList.add(change);
					totalNegativeChange = totalNegativeChange + change;
				} else {
					neutralChangeList.add(change);
				}
				
				
				changeSum = changeSum + change;
				//daysChange = daysChange + change;
				//adds to the map of executed trades for each algorithm
				tradesTriggered.put(trade.getAlgoNumber(), tradesTriggered.get(trade.getAlgoNumber()) + 1);
			}
			//add the alteredDaysTrades to the tradeMap for risk
			tradeMapR6.put(i, alteredDaysTrades);
		}
		
		//calculate average profit per trade
		averageProfitPerTrade = Math.round(changeSum/changeList.size()*100.0)/100.0;
		//calculate profitability percentage
		profitabilityPercentage = Math.round((double) positiveChangeList.size()/(positiveChangeList.size() + negativeChangeList.size()) * 1000.0)/1000.0;
		//calculate total trades
		totalTrades = positiveChangeList.size() + negativeChangeList.size() + neutralChangeList.size();
		//calculate profit to loss
		pToL = -Math.round((totalPositiveChange/positiveChangeList.size())/(totalNegativeChange/negativeChangeList.size())*100.0)/100.0;
		//calculates average leverage
		averageLeveragePerTrade = getMean(leverageList);
		
		
		//print R6 results
		System.out.println("Realm #6: Long/Short Only Leveraged with Variance");
		System.out.println("Average Profit Per Trade: "  + averageProfitPerTrade);
		System.out.println("Average Leverage Per Trade: "  + averageLeveragePerTrade);
		System.out.println("Profitability Percentage: " + profitabilityPercentage);
		System.out.println("Total Trades: " + totalTrades);
		System.out.println("Profit to Loss Ratio: " + pToL);
		System.out.println("Monthly Sharpe Ratio: " + sharpe.calculateMonthly(sharpe.getMonthlyReturns(sharpe.getDailyReturns(tradeMapR6, dateKeySet))));
		System.out.println("Daily Sharpe Ratio: " + sharpe.calculateDaily(sharpe.getDailyReturns(tradeMapR6, dateKeySet)));
		//sharpe.printDrawDown(sharpe.getDailyReturns(tradeMapR6, dateKeySet));
		//sharpe.printKelly(sharpe.getDailyReturns(tradeMapR6, dateKeySet));
		//sharpe.printReturns(sharpe.getDailyReturns(tradeMapR6, dateKeySet));
		//print number of trades triggered per algorithm
		for(Integer algoNum : tradesTriggered.keySet()){
			System.out.println("Algorithm #" + algoNum  + ": " + tradesTriggered.get(algoNum));
		}
	}	
	
	
		
	//	
	public double getMean(ArrayList<Double> doubleList){
		double sum = 0.0;
		for(Double num : doubleList){
			sum += num;
		}
		return sum/(double) doubleList.size();
		
	}
	
	//returns a copy of the tradeMap where changes of the copy will not affect the orginal
	public HashMap<Integer, ArrayList<Trade>> copyMap(HashMap<Integer, ArrayList<Trade>> tradeMap) throws ParseException{
		HashMap<Integer, ArrayList<Trade>> tradeMapCopy = new HashMap<Integer, ArrayList<Trade>>();
		for(Integer key : tradeMap.keySet()){
			ArrayList<Trade> temp = new ArrayList<Trade>();
			for(Trade trade : tradeMap.get(key)){
				temp.add(new Trade(trade));
			}
			tradeMapCopy.put(new Integer(key), new ArrayList<Trade>(temp));
			
		}
		return tradeMapCopy;
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
	
	//returns a hashMap organized by date
	public static HashMap<Date, ArrayList<ArrayList<Object>>> readOutput2(boolean assumption) throws IOException, ParseException{
		HashMap<Date, ArrayList<ArrayList<Object>>> returnMap = new HashMap<Date, ArrayList<ArrayList<Object>>>();
		String dateFormat = "M/d/yyyy";
		String filePath;
		Date lastDate = new Date();
		String lastSymbol = new String();
		
		if(assumption){ //reads in output file with assumption
			filePath = "/Users/14price/Documents/Output2Assumption.txt";
		} else{  //reads in output file without assumption
			filePath = "/Users/14price/Documents/Output2.txt";
		}
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try{
			String line = br.readLine();
			while(line != null){
				StringTokenizer tokenizer = new java.util.StringTokenizer(line, ",");
				//read symbol
				String symbol = new String(tokenizer.nextToken().trim());
				//read date
				Date date = new SimpleDateFormat(dateFormat).parse(tokenizer.nextToken().trim());
				//read predicted
				Object predicted;
				String nextToken = tokenizer.nextToken().trim();
				if(nextToken.equals("null")){
					predicted = new Double(.0001);
				} else {
					predicted = new Double(Double.parseDouble(nextToken));
				}
				//read reported
				Object reported;
				String nextToken2 = tokenizer.nextToken().trim();
				if(nextToken2.equals("null")){
					reported = new Double(.0001);
				} else {
					reported = new Double(Double.parseDouble(nextToken2));
				}
				//add p-1 reported
				if(lastSymbol.equals(symbol)){
					int count = 0;
					boolean index = false;
					for(ArrayList<Object> dataLine : returnMap.get(lastDate)){
						if(dataLine.get(0).equals(symbol)){
							index = true;
							break;
						}
						count++;
					}
					if(index){
						returnMap.get(lastDate).get(count).add(reported);
					} else{
						returnMap.get(lastDate).get(count).add(new Double(.0001));
					}
				}
				
				//read report time
				String reportTime = new String(tokenizer.nextToken().trim());
				
				ArrayList<Object> currentLine = new ArrayList<Object>();
				currentLine.add(symbol);
				currentLine.add(date);
				currentLine.add(predicted);
				currentLine.add(reported);
				currentLine.add(reportTime);
				
				if(!returnMap.containsKey(date)){
					ArrayList<ArrayList<Object>> container = new ArrayList<ArrayList<Object>>();
					container.add(currentLine);
					returnMap.put(date, container);
				} else{
					returnMap.get(date).add(currentLine);
				}
				
				//move to next line
				line = br.readLine();
				lastDate = date;
				lastSymbol = symbol;
			}
		} finally {
			br.close();
		}
		return returnMap;
	}
	
	public static HashMap<String, ArrayList<ArrayList<Object>>> readOutput() throws IOException, ParseException{
		HashMap<String, ArrayList<ArrayList<Object>>> returnMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
		String dateFormat = "yyyy-MM-dd";
		String filePath = "/Users/14price/Documents/Output.txt";
		DecimalFormat df = new DecimalFormat("#0.000");
		
		String symbol = "";
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try{
			String line = br.readLine();
			while(line != null){
				StringTokenizer tokenizer = new java.util.StringTokenizer(line, ",");
				//read symbol
				symbol = new String(tokenizer.nextToken().trim());
				//read date
				Date date = new SimpleDateFormat(dateFormat).parse(tokenizer.nextToken().trim());
				//read close
				Double close = new Double(Double.parseDouble(tokenizer.nextToken().trim()));
				//read open
				Double open = new Double(Double.parseDouble(tokenizer.nextToken().trim()));
				//read high
				Double high = new Double(Double.parseDouble(tokenizer.nextToken().trim()));
				//read low
				Double low = new Double(Double.parseDouble(tokenizer.nextToken().trim()));
				//read volume
				Integer volume;
				Double adjClose;
				String nextToken = "";
				try{
					nextToken = tokenizer.nextToken().trim();
					volume = new Integer(Integer.parseInt(nextToken));
					adjClose = new Double(Double.parseDouble(tokenizer.nextToken().trim())); 
				} catch(NumberFormatException nfe){ //from quandl so adj close instead
					adjClose = new Double(Double.parseDouble(nextToken));
					volume = new Integer(Integer.parseInt(tokenizer.nextToken().trim()));
				}
				
				ArrayList<Object> currentLine = new ArrayList<Object>();
				currentLine.add(symbol);
				currentLine.add(date);
				currentLine.add(new Double(df.format(close)));
				currentLine.add(new Double(df.format(open)));
				currentLine.add(new Double(df.format(high)));
				currentLine.add(new Double(df.format(low)));
				currentLine.add(new Double(df.format(adjClose)));
				currentLine.add(volume);
				
				if(!returnMap.containsKey(symbol)){
					ArrayList<ArrayList<Object>> container = new ArrayList<ArrayList<Object>>();
					container.add(currentLine);
					returnMap.put(symbol, container);
				} else{
					returnMap.get(symbol).add(currentLine);
				}
				
				//move to next line
				line = br.readLine();
			}
		} catch (OutOfMemoryError oome){
			System.out.println(symbol);
		} finally {
			br.close();
			
		}
		return returnMap;
	}
	
	//finds all references in the data to ticker symbol
	public ArrayList<ArrayList<Object>> findAll(String symbol, ArrayList<ArrayList<Object>> output){
		ArrayList<ArrayList<Object>> matches = new ArrayList<ArrayList<Object>>();
		for(ArrayList<Object> line : output){
			if(line.get(0).equals(symbol)){
				ArrayList<Object> temp = new ArrayList<Object>();
				temp.add(line.get(1));
				temp.add(line.get(2));
				temp.add(line.get(3));
				temp.add(line.get(4));
				matches.add(temp);
			}
		}
		return matches;
	}
	
	public void getOutput2Assumption(HashMap<String, ArrayList<ArrayList<Object>>> dataMap) throws IOException{
		HashMap<String, ArrayList<ArrayList<Object>>> changeMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
		HashMap<String, ArrayList<ArrayList<Object>>> cloneMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
		
		for(String key : dataMap.keySet()){
			//loop through once to check out the report times
			boolean beforeExists = false;
			boolean afterExists = false;
			ArrayList<ArrayList<Object>> temp = new ArrayList<ArrayList<Object>>();
			
			for(ArrayList<Object> line : dataMap.get(key)){
				String time = (String) line.get(3);
				time = time.trim();
				if(time.equals("Before Market Open")){
					beforeExists = true;
				} else if(time.equals("After Market Close")){
					afterExists = true;
				}
			}
			if(beforeExists && afterExists){
				//carry on because no report time assumption can be made
			} else if(beforeExists) {
				for(ArrayList<Object> line : dataMap.get(key)){
					ArrayList<Object> lineTemp = new ArrayList<Object>();
					lineTemp.add(line.get(0));
					lineTemp.add(line.get(1));
					lineTemp.add(line.get(2));
					lineTemp.add("Before Market Open");
					temp.add(lineTemp);
				}
				changeMap.put(key, temp);
			} else if(afterExists) {
				for(ArrayList<Object> line : dataMap.get(key)){
					ArrayList<Object> lineTemp = new ArrayList<Object>();
					lineTemp.add(line.get(0));
					lineTemp.add(line.get(1));
					lineTemp.add(line.get(2));
					lineTemp.add("After Market Close");
					temp.add(lineTemp);
				}
				changeMap.put(key, temp);
			}
		}
		
		//make the changes to dataMap
		for(String key : dataMap.keySet()){
			if(changeMap.containsKey(key)){
				cloneMap.put(key, changeMap.get(key));
			} else {
				cloneMap.put(key, dataMap.get(key));
			}
		}
		
		writeTextFile(cloneMap);
	}
	
	public void writeTextFile(HashMap<String, ArrayList<ArrayList<Object>>> map) throws IOException {
		try {
	
			File file = new File("/Users/14price/Documents/Output2Assumption.txt");
			DateFormat df = new SimpleDateFormat("M/d/yyyy");
			
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String key: map.keySet()){
				for(ArrayList<Object> list : map.get(key)){
					bw.write(key + ", " + df.format(list.get(0)) + ", " + list.get(1) + ", " + list.get(2) + ", " + list.get(3) + "\n");
				}
			}
			
			bw.close();
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	//reads a date keyset file in the documents directory
	public static ArrayList<Date> getDateKeySet() throws IOException, ParseException{
		String filePath = "/Users/14price/Documents/DateKeySet.txt";
		ArrayList<Date> symbols = new ArrayList<Date>();
		String dateFormat = "yyyy-MM-dd";
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try{
			String line = br.readLine();
			while(line != null){
				symbols.add(new SimpleDateFormat(dateFormat).parse(line.trim()));
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return symbols;
	}
	
	//returns a randomized copy of a list as an arrayList
	public ArrayList<String> randomize(List<String> keys){
		Random randomGen = new Random();
		ArrayList<String> randomKeySet = new ArrayList<String>();
		for(int i = 0; i < keys.size(); i++){
			int r = randomGen.nextInt(keys.size());
			randomKeySet.add(keys.get(r));
		}
		return randomKeySet;
	}
}

