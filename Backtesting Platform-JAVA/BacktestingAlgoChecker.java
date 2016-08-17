import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class BacktestingAlgoChecker {
	
	private ArrayList<String> symbols;
	private Map<String, Long> marketCapData;
	private Map<String, Data> data;
	private int spyAroon;
	private ArrayList<ArrayList<Object>> reportsIn1Day;
	private ArrayList<ArrayList<Object>> reportsIn2Days;
	private ArrayList<ArrayList<Object>> reportsIn3Days;
	private ArrayList<ArrayList<Object>> reportsIn4Days;
	private ArrayList<ArrayList<Object>> reportsIn5Days;
	private ArrayList<ArrayList<Object>> reportsIn6Days;
	private ArrayList<Double> historicalPlusCurrentSPY;
	private HashMap<String, ArrayList<ArrayList<Object>>> output;
	private HashMap<Date, ArrayList<ArrayList<Object>>> output2;
	private Date day;
	private ArrayList<Date> datekeySet;
	public ArrayList<String> errorInfo;
	
	public BacktestingAlgoChecker(Date day, HashMap<Date, ArrayList<ArrayList<Object>>> output2, ArrayList<Date> datekeySet, HashMap<String, ArrayList<ArrayList<Object>>> output) throws IOException{
		//generates the dates of the next 2 to 6 business days
		int index = datekeySet.indexOf(day);
		this.output = output;
		this.output2 = output2;
		this.day = day;
		this.datekeySet = datekeySet;
		
		Date oneDay = datekeySet.get(index - 1);
		Date twoDays = datekeySet.get(index - 2);
		Date threeDays = datekeySet.get(index - 3);
		Date fourDays = datekeySet.get(index - 4);
		Date fiveDays = datekeySet.get(index - 5);
		Date sixDays = datekeySet.get(index - 6);
		Date sevenDays = datekeySet.get(index - 7);

		//reads in the market cap data
		marketCapData = readMarketCapData();
		
		//calculate the SPY aroon value once
		historicalPlusCurrentSPY = getAdjCloses("SPY", output, day);
		spyAroon = Aroon.getAroon(50, historicalPlusCurrentSPY);
		
		reportsIn1Day = new ArrayList<ArrayList<Object>>();
		reportsIn2Days = new ArrayList<ArrayList<Object>>();
		reportsIn3Days = new ArrayList<ArrayList<Object>>();
		reportsIn4Days = new ArrayList<ArrayList<Object>>();
		reportsIn5Days = new ArrayList<ArrayList<Object>>();
		reportsIn6Days = new ArrayList<ArrayList<Object>>();
		
		
		//gets the scraped earnings calendar info for the next 1-7 days
		//reportsInXDays.get(0) = symbol, .get(1) = predicted, .get(2) = p-1
		if(output2.containsKey(oneDay)){
			for(ArrayList<Object> dataLine : output2.get(oneDay)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn1Day.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(twoDays)){
			for(ArrayList<Object> dataLine : output2.get(twoDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn2Days.add(tempArray);
				}
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					}
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn1Day.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(threeDays)){
			for(ArrayList<Object> dataLine : output2.get(threeDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn3Days.add(tempArray);
				}
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn2Days.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(fourDays)){
			for(ArrayList<Object> dataLine : output2.get(fourDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn4Days.add(tempArray);
				}
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn3Days.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(fiveDays)){
			for(ArrayList<Object> dataLine : output2.get(fiveDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn5Days.add(tempArray);
				}
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					}
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn4Days.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(sixDays)){
			for(ArrayList<Object> dataLine : output2.get(sixDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("After Market Close")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //gets the reported and checks if it has a value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn6Days.add(tempArray);
				}
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					} 
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn5Days.add(tempArray);
				}
			}
		}
		
		if(output2.containsKey(sevenDays)){
			for(ArrayList<Object> dataLine : output2.get(sevenDays)){
				//do not register if report time is not available
				if(((String) dataLine.get(4)).equals("Before Market Open")){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray.add((String) dataLine.get(0)); //symbol
					if(dataLine.get(2) instanceof Double){ //has value
						tempArray.add((Double) dataLine.get(2)); //estimate
					}
					if(dataLine.size() > 5){ //has value
						tempArray.add((Double) dataLine.get(5)); //reported
					} else {
						tempArray.add(new Double(.0001));
					}
					reportsIn6Days.add(tempArray);
				}
			}
		}
	}
	
	
	//reads a symbols file in the documents directory
	public ArrayList<String> getSymbols(String fileName) throws IOException{
		String filePath = "/Users/14price/Documents/" + fileName;
		ArrayList<String> symbols = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try{
			String line = br.readLine();
			while(line != null){
				symbols.add(line);
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return symbols;
	}
	
	//changes the second dimension of the array from a date to the number of trading days until the report
	//if earnings is before open, subtract a day 
	public void processArray(String[][] upcomingReports){
		DateUtil tradingDayGenerator = new DateUtil();
		//int tradingDaysTillReport = tradingDayGenerator.businessDaysFromToday(endDate, "yyyy-MM-dd");
		
	}
	
	//removes entries from the lists of upcoming earnings that are not in the symbol list
	public ArrayList<ArrayList<String>> filter(ArrayList<ArrayList<String>> reports, ArrayList<String> symbols){
		ArrayList<Integer> removeIndices = new ArrayList<Integer>();
		int count = 0;
		for(ArrayList<String> arrayList : reports){
			boolean found = false;
			for(String symbol : symbols){
				if(arrayList.get(1).equals(symbol)){
					found = true;
				}
			}
			if(found == false){
				removeIndices.add(count);
			}
			count++;
		}
		//reverses the indices list
		Collections.reverse(removeIndices);
		//casts the Integer indices to int indices and removes them in descending order
		for(Integer index : removeIndices){
			reports.remove((int) index);
		}
		
		
		return reports;
	}
	
	//quickly prints a 2d arraylist
	public void print2dArraylist(ArrayList<ArrayList<String>> list){
		for(ArrayList<String> arrayList : list){
			//get(0) = corporate name, get(1) = symbol, get(2) = EPS, get(3) = report time
			System.out.println(arrayList.get(0) + ", " + arrayList.get(1) + ", " + arrayList.get(2) + ", " + arrayList.get(3));
			//System.out.println(arrayList.get(1));
			//System.out.println(arrayList.get(2));
			//System.out.println(arrayList.get(3));
		}
	}
	
	//quickly prints an arraylist
	public void printArraylist(ArrayList<String> list){
		for(String item : list){
			System.out.println(item);
		}
	}
	
	public ArrayList<Double> getAdjCloses(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Double> adjCloses = new ArrayList<Double>();
		boolean found = false;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(df.format(day).equals(df.format(lineData.get(1)))){
				found = true;
			}
			if(found){
				adjCloses.add((Double) lineData.get(6));
			}
		}
		return adjCloses;
	}
	
	public ArrayList<Integer> getVolumes(String symbol, HashMap<String, ArrayList<ArrayList<Object>>> output, Date day){
		ArrayList<Integer> volumes = new ArrayList<Integer>();
		boolean found = false;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		for(ArrayList<Object> lineData : output.get(symbol)){
			//only add if date is after day and matches datekeyset date
			if(df.format(day).equals(df.format(lineData.get(1)))){
				found = true;
			}
			if(found){
				volumes.add((Integer) lineData.get(7));
			}
		}
		return volumes;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo1() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 5;
		String indication = "short Reg";
		int algoNumber = 1;
		
		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double vDBA = getVDBA(30, w1, volumes);
			double rSI = new RSI().getRSI(14, adjCloses);
			
			//**************CHANGE**************
			if(percentChange > 8 && pvPercentChange > 0 && rSI > 55 && rSI < 70 && vDBA >= .4 && vDBA <= 1.1){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo2() throws IOException{
		int y1 = 5;
		int x1 = 8;
		int w1 = 7;
		String indication = "long DN";
		int algoNumber = 2;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 150 || volumes.size() <= 150 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int spyAroon50 = spyAroon;
			double vDBA = getVDBA(30, w1, volumes);
			double minBefore = getMinBefore(150, adjCloses);
			double maxBefore = getMaxBefore(150, adjCloses);
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && spyAroon50 < 0 && vDBA >= .6 && vDBA <= 1.2){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo3() throws IOException{
		int y1 = 1;
		int x1 = 7;
		int w1 = 7;
		String indication = "long Reg";
		int algoNumber = 3;
		

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		}  
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < 0 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 2 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo4() throws IOException{
		int y1 = 3;
		int x1 = 10;
		int w1 = 8;
		String indication = "long DN";
		int algoNumber = 4;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 150 || volumes.size() <= 150 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int aroon25 = Aroon.getAroon(25, adjCloses);
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double minBefore = getMinBefore(150, adjCloses);
			double maxBefore = getMaxBefore(150, adjCloses);
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -3 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && avDBSP >= 3 && avDBSP <= 5 && aroon25 < 0){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo5() throws IOException{
		int y1 = 3;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 5;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}

			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -1 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 3 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo6() throws IOException{
		int y1 = 3;
		int x1 = 10;
		int w1 = 9;
		String indication = "long Reg";
		int algoNumber = 6;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int aroon25 = Aroon.getAroon(25, adjCloses);
			double rSI = new RSI().getRSI(14, adjCloses);
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < 0 && pvPercentChange < -1 && marketCap >= 4 && marketCap <= 5 && rSI >= 25 && rSI <= 40 && aroon25 < 0){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo7() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 7;
		String indication = "long DN";
		int algoNumber = 7;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			
			
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -1 && pvPercentChange < -1 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 2 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo8() throws IOException{
		int y1 = 4;
		int x1 = 6;
		int w1 = 8;
		String indication = "long Reg";
		int algoNumber = 8;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			double rSI = new RSI().getRSI(14, adjCloses);
			
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && rSI >= 55 && rSI <= 70 && spyAroon50 > 0 && avDBSP >= 3 && avDBSP <= 5 && vDBA >= .6 && vDBA <= 1.2){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo9() throws IOException{
		int y1 = 4;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 9;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int aroon25 = Aroon.getAroon(25, adjCloses);
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double rSI = new RSI().getRSI(14, adjCloses);
			
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && rSI >= 55 && rSI <= 70 && aroon25 >= 50 && avDBSP >= 3 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo10() throws IOException{
		int y1 = 2;
		int x1 = 8;
		int w1 = 5;
		String indication = "short DN";
		int algoNumber = 10;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			
			//**************CHANGE**************
			if(percentChange > 7 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA <= 1){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo11() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 2;
		String indication = "short Reg";
		int algoNumber = 11;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			
			//**************CHANGE**************
			if(percentChange > 7 && avDBSP >= 4 && avDBSP <= 5 && vDBA >= .6 && vDBA <= 1.1){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo12() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 2;
		String indication = "long Reg";
		int algoNumber = 12;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 150 || volumes.size() <= 150 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			double minBefore = getMinBefore(150, adjCloses);
			double maxBefore = getMaxBefore(150, adjCloses);
			
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && vDBA <= 1.1 && avDBSP >= 4 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo13() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 8;
		String indication = "short Reg";
		int algoNumber = 13;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			
			
			double vDBA = getVDBA(30, w1, volumes);
			double rSI = new RSI().getRSI(14, adjCloses);
			
			//**************CHANGE**************
			if(percentChange > 8 && pvPercentChange > 0 && rSI >= 55 && rSI <= 70 && vDBA >= .4 && vDBA <= 1.2){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo14() throws IOException{
		int y1 = 2;
		int x1 = 8;
		int w1 = 4;
		String indication = "short DN";
		int algoNumber = 14;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			
			//**************CHANGE**************
			if(percentChange > 7 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA >= .6 && vDBA <= 1.2){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo15() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 7;
		String indication = "long Reg";
		int algoNumber = 15;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 150 || volumes.size() <= 150 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			double vDBA = getVDBA(30, w1, volumes);
			double minBefore = getMinBefore(150, adjCloses);
			double maxBefore = getMaxBefore(150, adjCloses);
			
			
			//**************CHANGE**************
			if(percentChange < -2 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && vDBA <= 1.2 && vDBA >= .4){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo16() throws IOException{
		int y1 = 2;
		int x1 = 5;
		int w1 = 5;
		String indication = "short DN";
		int algoNumber = 16;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			
			
			//**************CHANGE**************
			if(percentChange > 6 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo17() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 3;
		String indication = "long Reg";
		int algoNumber = 17;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			
			
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));
			double vDBA = getVDBA(30, w1, volumes);
			
			
			//**************CHANGE**************
			if(percentChange < -2 && pvPercentChange < 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA <= 1.2 && vDBA >= .4){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo18() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 18;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 150 || volumes.size() <= 150 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			
			double minBefore = getMinBefore(150, adjCloses);
			double maxBefore = getMaxBefore(150, adjCloses);
			
			//**************CHANGE**************
			if(percentChange < -1 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<ArrayList<Object>> checkAlgo19() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 19;

		ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = reportsIn1Day;
		} else if(y1 == 2){
			reports = reportsIn2Days;
		} else if(y1 == 3){
			reports = reportsIn3Days;
		} else if(y1 == 4){
			reports = reportsIn4Days;
		} else if(y1 == 5){
			reports = reportsIn5Days;
		} else if(y1 == 6){
			reports = reportsIn6Days;
		} 
		
		ArrayList<ArrayList<Object>> trades = new ArrayList<ArrayList<Object>>();
		
		//checks the algorithm for each stock
		for(ArrayList<Object> reportEvent : reports){
			String symbol = (String) reportEvent.get(0);
			//equality is never selected for
			double epsPredicted;
			double epsPredictedMinus1;
			if((Double) reportEvent.get(1) == .0001 || (Double) reportEvent.get(2) == .0001){
				epsPredicted = 1.00;
				epsPredictedMinus1 = 1.00;
			} else{
				epsPredicted = (Double) reportEvent.get(1);
				epsPredictedMinus1 = (Double) reportEvent.get(2);
			}
			//get arrayList of adjusted closes
			ArrayList<Double> adjCloses;
			try{ //missing symbol
				adjCloses = getAdjCloses(symbol, output, day);
			} catch(NullPointerException npe){
				continue;
			}
			//get arrayList of volume
			ArrayList<Integer> volumes = getVolumes(symbol, output, day);
			
			if(adjCloses.size() <= 30 || volumes.size() <= 30 + w1){
				continue;
			}
			
			double percentChange;
			try{
				percentChange = getPercentChange(adjCloses, x1);
			} catch(IndexOutOfBoundsException ioobe){
				continue;
			}
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			int marketCap = 0; //unknown
			if(marketCapData.get(symbol) != null){
				marketCap = scaleMC(marketCapData.get(symbol));
			}
			int avDBSP = getAVDBSP(30, volumes, adjCloses.get(0));

			
			//**************CHANGE**************
			if(percentChange < -1 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 4 && avDBSP <= 5){
				ArrayList<Object> tradeLine = new ArrayList<Object>();
				tradeLine.add(day);
				tradeLine.add(algoNumber);
				tradeLine.add(indication);
				tradeLine.add(symbol);
				tradeLine.add(y1);
				trades.add(tradeLine);
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public double getMaxBefore(int duration, ArrayList<Double> closes) throws IOException{
		
		double maxValue = 0.00;
		
		int counter = 0;
		double currentAdjClose = closes.get(0);
		for(int i = 0; i < duration; i++){
			if(counter == 0){
				if(currentAdjClose > maxValue){
					maxValue = currentAdjClose;
				}
			} else{
				double adjClose = closes.get(i);
				if(adjClose > maxValue){
					maxValue = adjClose;
				}
			}
			
			counter++;
		}
		
		return Math.abs(((currentAdjClose - maxValue)/maxValue)*100);
		
	}
	
	public double getMinBefore(int duration, ArrayList<Double> closes) throws IOException{
		
		double minValue = 2000000.00;
		
		int counter = 0;
		double currentAdjClose = closes.get(0);
		
		for(int i = 0; i < duration; i++){
			if(counter == 0){
				if(currentAdjClose < minValue){
					minValue = currentAdjClose;
				}
			} else{
				double adjClose = closes.get(i);
				if(adjClose < minValue){
					minValue = adjClose;
				}
			}
			counter++;
		}
		
		return ((currentAdjClose - minValue)/minValue)*100;
		
	}
	
	public double getVDBA(int duration, int durationOfAverage, ArrayList<Integer> volumes) throws IOException{
		
		int volumeSum = 0;
		int avgVolumeSum = 0;
		int count = 0;
		
		for(int i = 0; i <= duration + durationOfAverage; i++){
			if(count < durationOfAverage){
				avgVolumeSum += volumes.get(i);
				count++;
			} else{
				volumeSum += volumes.get(i);
			}
		}
		
		int avgVolume = avgVolumeSum/durationOfAverage;
		int avgVolumeTotalDuration = volumeSum/duration;
		
		double vDBA = (double) avgVolume/avgVolumeTotalDuration;
		
		return vDBA;
		
	}
	
	
	public int getAVDBSP(int duration, ArrayList<Integer> volumes, double sharePrice) throws IOException{
		
		DataGatherer getData = new DataGatherer();
		
		int volumeSum = 0;
		
		//don't count the current volume
		for(int i = 1; i <= duration; i++){
			volumeSum += volumes.get(i);
		}
		
		int avgVolume = volumeSum/duration;
		int avDBSP = (int) (avgVolume/sharePrice);
		
		if(avDBSP > 100000){
			return 5;
		} else if(avDBSP > 50000){
			return 4;
		} else if(avDBSP > 20000){
			return 3;
		} else if(avDBSP > 50000){
			return 2;
		} else{
			return 1;
		}
		
		
	}
	
	public double getPercentChange(ArrayList<Double> closes, int duration){
		double newestClosePrice = closes.get(0);
		double oldestClosePrice = closes.get(duration - 1);
		return ((newestClosePrice - oldestClosePrice)/oldestClosePrice)*100;
	}
	
	public double getPVPercentChange(double percentChange, ArrayList<Double> spyData, int duration){
		double newestClosePrice = spyData.get(0);
		double oldestClosePrice = spyData.get(duration - 1);
		double spyChange = ((newestClosePrice - oldestClosePrice)/oldestClosePrice)*100;
		return percentChange - spyChange;
	}
	
	public int scaleMC(Long marketCap){
		if(marketCap > 20000000000L){
			return 5;
		} else if(marketCap > 7000000000L){
			return 4;
		} else if(marketCap > 3200000000L){
			return 3;
		} else if(marketCap > 1800000000){
			return 2;
		} else{
			return 1;
		}
	}
	
	public Map<String, Long> readMarketCapData(){
		String csvFile = "/Users/14price/Documents/MarketCapInfo.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Map<String, Long> marketCapData = new HashMap<String, Long>();
		
		try {
			 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] data = line.split(cvsSplitBy);
				marketCapData.put(data[0], Long.parseLong(data[1]));
	 
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return marketCapData;
	 
	}
	
	
	//ArrayList<ArrayList<String>> reportsIn2Days = new ArrayList<ArrayList<String>>(scraper.scrapeEarningsData(twoD));
	
	//public static String percentChange
	
	public ArrayList<String> generateSymbolList(ArrayList<ArrayList<String>> prev, ArrayList<ArrayList<String>> next){
		ArrayList<String> symbols = new ArrayList<String>();
		//takes the "After Market Close" in prev
		for(ArrayList<String> arrayList : prev){
			if(arrayList.get(3).equals("After Market Close")){
				symbols.add(arrayList.get(1));
			} 
		}
		//takes the "Before Market Open" in next
		for(ArrayList<String> arrayList : next){
			if(arrayList.get(3).equals("Before Market Open")){
				symbols.add(arrayList.get(1));
			} 
		}
		return symbols;
	}
	
	
}
