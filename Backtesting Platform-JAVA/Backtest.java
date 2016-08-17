import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;


public class Backtest {
	
	private static ArrayList<Date> dateKeySet;
	private static HashMap<String, ArrayList<ArrayList<Object>>> output;
	private static HashMap<Date, ArrayList<ArrayList<Object>>> output2;
	private HashMap<Date, ArrayList<ArrayList<Object>>> output2Assumption;
	
	
	public Backtest() throws IOException, ParseException{
		//read in AAPL keyset for date iteration (starts with latest date at get(0))
		dateKeySet = getDateKeySet();
		output = readOutput();
		output2 = readOutput2(false);
		output2Assumption = readOutput2(true);
	}

	public static void main(String[] args) throws IOException, ParseException {
		
		//assumptions
		int maxPositionsPerDay = 5; //per day!
		int maxPortfolioPositionsAtAnyTime = 20;
		double startValue = 1000000.0;
		boolean random = true; //all trades or random sample
		
		
		double portfolio = startValue;
		double portfolioPV = startValue;
		int monthCount = 0; //23 business days in month
		double oldPortfolioValue;
		Backtest b = new Backtest();
		
		ArrayList<Integer> tradesTriggered = new ArrayList<Integer>();
		
		for(int i = 0; i < 19; i++){
			tradesTriggered.add(0);
		}
		
		ArrayList<Date> dateKeySet = getDateKeySet();
		HashMap<String, ArrayList<ArrayList<Object>>> output = readOutput();
		HashMap<Date, ArrayList<ArrayList<Object>>> output2 = readOutput2(false);
		HashMap<Date, ArrayList<ArrayList<Object>>> output2Assumption = readOutput2(true);
		
		double maxSize = 0.00;
		ArrayList<Integer> currentPositions = new ArrayList<Integer>();
		
		for(int i = dateKeySet.size()-1600; i >= 7; i--){
			//stop at 3/17 with 7 days left in keyset
			double currentTradeSize = portfolio/2; ///////
			
//			if(currentTradeSize > maxSize){
//				maxSize = currentTradeSize;
//			}
//			if(currentTradeSize < maxSize){
//				currentTradeSize = maxSize;
//			}
			int newTradeNum;
			
			Date day = dateKeySet.get(i);
			BacktestingAlgoChecker bac = new BacktestingAlgoChecker(day, output2, dateKeySet, output);
			ArrayList<ArrayList<Object>> trades = bac.checkAlgo1();
			//ArrayList<ArrayList<Object>> trades = bac.checkAlgo3();
			trades.addAll(bac.checkAlgo2());
			trades.addAll(bac.checkAlgo3()); //outperforms but is never CALLED
			trades.addAll(bac.checkAlgo4()); //outperforms
			trades.addAll(bac.checkAlgo5()); //crazy outperform
			trades.addAll(bac.checkAlgo6()); //crazy outperform
			trades.addAll(bac.checkAlgo7()); //crazy outperform
			trades.addAll(bac.checkAlgo8()); //under-perform
			trades.addAll(bac.checkAlgo9()); //huge under-perform
			trades.addAll(bac.checkAlgo10()); //outperform
			trades.addAll(bac.checkAlgo11());  //slight outperform
			trades.addAll(bac.checkAlgo12());  //outperforms
			trades.addAll(bac.checkAlgo13()); //under-perform
			trades.addAll(bac.checkAlgo14());  //heavy outperform
			trades.addAll(bac.checkAlgo15());  //heavy outperform
			trades.addAll(bac.checkAlgo16());  //decent performer
//			trades.addAll(bac.checkAlgo17());  //crazy under-performer - wtf
//			trades.addAll(bac.checkAlgo18()); //outperform???
//			trades.addAll(bac.checkAlgo19()); //decent performer
			
			////////trade each algo equally???////
			
			newTradeNum = tradesTriggered.get(0) + bac.checkAlgo1().size();
			tradesTriggered.set(0, newTradeNum);
			newTradeNum = tradesTriggered.get(1) + bac.checkAlgo2().size();
			tradesTriggered.set(1, newTradeNum);
			newTradeNum = tradesTriggered.get(2) + bac.checkAlgo3().size();
			tradesTriggered.set(2, newTradeNum);
			newTradeNum = tradesTriggered.get(3) + bac.checkAlgo4().size();
			tradesTriggered.set(3, newTradeNum);
			newTradeNum = tradesTriggered.get(4) + bac.checkAlgo5().size();
			tradesTriggered.set(4, newTradeNum);
			newTradeNum = tradesTriggered.get(5) + bac.checkAlgo6().size();
			tradesTriggered.set(5, newTradeNum);
			newTradeNum = tradesTriggered.get(6) + bac.checkAlgo7().size();
			tradesTriggered.set(6, newTradeNum);
			newTradeNum = tradesTriggered.get(7) + bac.checkAlgo8().size();
			tradesTriggered.set(7, newTradeNum);
			newTradeNum = tradesTriggered.get(8) + bac.checkAlgo9().size();
			tradesTriggered.set(8, newTradeNum);
			newTradeNum = tradesTriggered.get(9) + bac.checkAlgo10().size();
			tradesTriggered.set(9, newTradeNum);
			newTradeNum = tradesTriggered.get(10) + bac.checkAlgo11().size();
			tradesTriggered.set(10, newTradeNum);
			newTradeNum = tradesTriggered.get(11) + bac.checkAlgo12().size();
			tradesTriggered.set(11, newTradeNum);
			newTradeNum = tradesTriggered.get(12) + bac.checkAlgo13().size();
			tradesTriggered.set(12, newTradeNum);
			newTradeNum = tradesTriggered.get(13) + bac.checkAlgo14().size();
			tradesTriggered.set(13, newTradeNum);
			newTradeNum = tradesTriggered.get(14) + bac.checkAlgo15().size();
			tradesTriggered.set(14, newTradeNum);
			newTradeNum = tradesTriggered.get(15) + bac.checkAlgo16().size();
			tradesTriggered.set(15, newTradeNum);
//			newTradeNum = tradesTriggered.get(16) + bac.checkAlgo17().size();
//			tradesTriggered.set(16, newTradeNum);
//			newTradeNum = tradesTriggered.get(17) + bac.checkAlgo18().size();
//			tradesTriggered.set(17, newTradeNum);
//			newTradeNum = tradesTriggered.get(18) + bac.checkAlgo19().size();
//			tradesTriggered.set(18, newTradeNum);
			
			HashMap<String, ArrayList<Object>> hash = new HashMap<String, ArrayList<Object>>();
			ArrayList<String> dailySymbolList = new ArrayList<String>();
			
			for(ArrayList<Object> trade : trades){
				hash.put(((String) trade.get(3)), trade);
			}
			
			int indexCount = 0;
			ArrayList<Integer> removeIndices = new ArrayList<Integer>();
			for(Integer duration : currentPositions){
				duration = duration - 1;
				if(duration == 0){
					removeIndices.add(indexCount);
				}
				indexCount++;
			}
			for(Integer index : removeIndices){
				currentPositions.remove(index);
			}
			
			if(random){
				Random randomGen = new Random();
				List<String> keys = new ArrayList<String>(hash.keySet());
			
				ArrayList<String> randomKeySet = new ArrayList<String>();
				for(String key : hash.keySet()){
					int randomIndex = randomGen.nextInt(keys.size());
					randomKeySet.add(keys.get(randomIndex));
					keys.remove(randomIndex);
				}
				
				int count = 0;
				for(String key : randomKeySet){ //randomKeySet substitute or hash.keySet() for no variation
					if(count >= maxPositionsPerDay && currentPositions.size() >= maxPortfolioPositionsAtAnyTime){
						break;
					}
//					if(hash.size() < 6){ //breaks if high volume of triggers**************
//						break;
//					}
					
					double halfTrade = b.generatePartialReturn(hash.get(key), currentTradeSize);
					double halfTradePV = b.generatePartialReturnPV(hash.get(key), currentTradeSize);
					double fullTrade = b.generateReturn(hash.get(key), currentTradeSize);
					double fullTradePV = b.generateReturnPV(hash.get(key), currentTradeSize);
					
					double change;
					double pvChange;
					
					if(halfTrade < 0){ //currentTradeSize*(-.02)
						change = halfTrade + 2*(fullTrade - halfTrade);
					} else {
						change = fullTrade;
					}
					
					if(halfTradePV < 0){
						pvChange = halfTradePV + 2*(fullTradePV - halfTradePV);
					} else {
						pvChange = fullTrade;
					}
					
					if(change == 0.00){
						continue; //error so continue
					}
					portfolio += change; //change to "change" for other mods
					portfolioPV += pvChange; //change to "change"for other mods
					count++;
					currentPositions.add((Integer) hash.get(key).get(4)); //puts in duration
				}
				
			} else{ //max trades doesn't matter
				for(String key : hash.keySet()){ 
					double halfTrade = b.generatePartialReturn(hash.get(key), currentTradeSize);
					double halfTradePV = b.generatePartialReturnPV(hash.get(key), currentTradeSize);
					double fullTrade = b.generateReturn(hash.get(key), currentTradeSize);
					double fullTradePV = b.generateReturnPV(hash.get(key), currentTradeSize);
					
					double change;
					double pvChange;
					
//					if(halfTrade < currentTradeSize*(-.02)){
//						change = halfTrade + 2*(fullTrade - halfTrade);
//					} else if(halfTrade > currentTradeSize*(.02)){ //accept loss
//						change = halfTrade + 2*(halfTrade - fullTrade);
//					} else {
//						change = halfTrade;
//					}
//					
//					if(halfTradePV > currentTradeSize*(-.02) && halfTradePV < 0){
//						pvChange = halfTradePV + 2*(fullTradePV - halfTradePV);
//					} else if(halfTradePV < 0){
//						pvChange = halfTradePV;
//					} else {
//						pvChange = halfTradePV;
//					}
					change = fullTradePV;
					pvChange = halfTradePV;
					
					
					
					if(change == 0.00){
						continue; //error so continue
					}
					portfolio += change;
					portfolioPV += change;
					currentPositions.add((Integer) hash.get(key).get(4)); //puts in duration
				}
			}
			
			System.out.println(day + ": " + portfolio + "          vs.          " + portfolioPV);
			
				
		}
		
		//print trade triggers
		for(int i = 0; i < 19; i++){
			System.out.println("Algo #" + (i + 1) + ": " + tradesTriggered.get(i));
		}
	}
	
	public static double generatePartialReturn(ArrayList<Object> trade, double tradeSize){
		Date startDate = ((Date) trade.get(0));
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		int duration = ((Integer) trade.get(4));
		
		//////
		if(duration == 2){
			//duration = 1;
		} else if(duration == 3){
			duration = 1;
		} else if(duration == 4){
			duration = 2;
		} else if(duration == 5){
			duration = 2;
		}
		//////
		
		int startIndex = 0;
		for(Date date : dateKeySet){
			if(df.format(date).equals(df.format(startDate))){
				break;
			}
			startIndex++;
		}
		
		int endIndex = startIndex - (duration);
		Date endDate = dateKeySet.get(endIndex);
		ArrayList<Double> closes = getAdjCloses(((String) trade.get(3)).trim(), output, endDate);
		
		
		
		Double change = 0.00;
		try{
			change = ((closes.get(0) - closes.get(duration))/closes.get(duration));
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println(((String) trade.get(3)) + " " + endDate + " *********************************");
			//returns change as 0
		}
		ArrayList<Double> spyCloses = getAdjCloses(new String("SPY"), output, endDate);
		Double spyChange = ((spyCloses.get(0) - spyCloses.get(duration))/spyCloses.get(duration));
		
		double endGainOrLoss = 0.00;
		if(((String) trade.get(2)).trim().equals("long Reg")){
			endGainOrLoss = change*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("short Reg")){
			endGainOrLoss = -change*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("long PV")){
			endGainOrLoss = (change - spyChange)*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("short PV")){
			endGainOrLoss = (-change + spyChange)*tradeSize;
		}
		
		System.out.println(((String) trade.get(3)).trim() + " " + duration + " " + endDate + ": " + endGainOrLoss);
		
		return endGainOrLoss;
	}
	
	public static double generatePartialReturnPV(ArrayList<Object> trade, double tradeSize){
		Date startDate = ((Date) trade.get(0));
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		int duration = ((Integer) trade.get(4));
		
		//////
		if(duration == 2){
			//duration = 1;
		} else if(duration == 3){
			duration = 1;
		} else if(duration == 4){
			duration = 2;
		} else if(duration == 5){
			duration = 2;
		}
		//////
		
		
		int startIndex = 0;
		for(Date date : dateKeySet){
			if(df.format(date).equals(df.format(startDate))){
				break;
			}
			startIndex++;
		}
		
		int endIndex = startIndex - (duration);
		Date endDate = dateKeySet.get(endIndex);
		ArrayList<Double> closes = getAdjCloses(((String) trade.get(3)).trim(), output, endDate);
		
		Double change = 0.00;
		try{
			change = ((closes.get(0) - closes.get(duration))/closes.get(duration));
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println(((String) trade.get(3)) + " " + endDate + " *********************************");
			//returns change as 0
		}
		ArrayList<Double> spyCloses = getAdjCloses(new String("SPY"), output, endDate);
		Double spyChange = ((spyCloses.get(0) - spyCloses.get(duration))/spyCloses.get(duration));
		
		double endGainOrLoss = 0.00;
		if(((String) trade.get(2)).trim().equals("long Reg") || ((String) trade.get(2)).trim().equals("long PV")){
			endGainOrLoss = ((change - spyChange)*tradeSize); //return divided by two because of pairs trade
		} else if(((String) trade.get(2)).trim().equals("short Reg") || ((String) trade.get(2)).trim().equals("short PV")){
			endGainOrLoss = ((-change + spyChange)*tradeSize);  //return divided by two because of pairs trade
		}
		
		//System.out.println(((String) trade.get(3)).trim() + " " + duration + " " + endDate + ": " + endGainOrLoss);
		
		return endGainOrLoss;
	}
	
	public static ArrayList<Double> getAdjCloses(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjCloses = new ArrayList<Double>();
		boolean found = false;
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(lineData.get(1).equals(day)){
				found = true;
			}
			if(found){
				adjCloses.add((Double) lineData.get(6));
			}
		}
		return adjCloses;
	}
	
	public static double generateReturn(ArrayList<Object> trade, double tradeSize){
		Date startDate = ((Date) trade.get(0));
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		int startIndex = 0;
		for(Date date : dateKeySet){
			if(df.format(date).equals(df.format(startDate))){
				break;
			}
			startIndex++;
		}
		
		int endIndex = startIndex - ((Integer) trade.get(4));
		Date endDate = dateKeySet.get(endIndex);
		ArrayList<Double> closes = getAdjCloses(((String) trade.get(3)).trim(), output, endDate);
		int duration = ((Integer) trade.get(4));
		Double change = 0.00;
		try{
			change = ((closes.get(0) - closes.get(duration))/closes.get(duration));
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println(((String) trade.get(3)) + " " + endDate + " *********************************");
			//returns change as 0
		}
		ArrayList<Double> spyCloses = getAdjCloses(new String("SPY"), output, endDate);
		Double spyChange = ((spyCloses.get(0) - spyCloses.get(duration))/spyCloses.get(duration));
		
		double endGainOrLoss = 0.00;
		if(((String) trade.get(2)).trim().equals("long Reg")){
			endGainOrLoss = change*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("short Reg")){
			endGainOrLoss = -change*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("long PV")){
			endGainOrLoss = (change - spyChange)*tradeSize;
		} else if(((String) trade.get(2)).trim().equals("short PV")){
			endGainOrLoss = (-change + spyChange)*tradeSize;
		}
		
		System.out.println(((String) trade.get(3)).trim() + " " + duration + " " + endDate + ": " + endGainOrLoss);
		
		return endGainOrLoss;
	}
	
	public static double generateReturnPV(ArrayList<Object> trade, double tradeSize){
		Date startDate = ((Date) trade.get(0));
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		int startIndex = 0;
		for(Date date : dateKeySet){
			if(df.format(date).equals(df.format(startDate))){
				break;
			}
			startIndex++;
		}
		
		int endIndex = startIndex - ((Integer) trade.get(4));
		Date endDate = dateKeySet.get(endIndex);
		ArrayList<Double> closes = getAdjCloses(((String) trade.get(3)).trim(), output, endDate);
		int duration = ((Integer) trade.get(4));
		Double change = 0.00;
		try{
			change = ((closes.get(0) - closes.get(duration))/closes.get(duration));
		} catch(IndexOutOfBoundsException ioobe){
			System.out.println(((String) trade.get(3)) + " " + endDate + " *********************************");
			//returns change as 0
		}
		ArrayList<Double> spyCloses = getAdjCloses(new String("SPY"), output, endDate);
		Double spyChange = ((spyCloses.get(0) - spyCloses.get(duration))/spyCloses.get(duration));
		
		double endGainOrLoss = 0.00;
		if(((String) trade.get(2)).trim().equals("long Reg") || ((String) trade.get(2)).trim().equals("long PV")){
			endGainOrLoss = ((change - spyChange)*tradeSize); //return divided by two because of pairs trade
		} else if(((String) trade.get(2)).trim().equals("short Reg") || ((String) trade.get(2)).trim().equals("short PV")){
			endGainOrLoss = ((-change + spyChange)*tradeSize);  //return divided by two because of pairs trade
		}
		
		//System.out.println(((String) trade.get(3)).trim() + " " + duration + " " + endDate + ": " + endGainOrLoss);
		
		return endGainOrLoss;
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
	public static ArrayList<ArrayList<Object>> findAll(String symbol, ArrayList<ArrayList<Object>> output){
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
	
	public static void getOutput2Assumption(HashMap<String, ArrayList<ArrayList<Object>>> dataMap) throws IOException{
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
	
	public static void writeTextFile(HashMap<String, ArrayList<ArrayList<Object>>> map) throws IOException {
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
}
