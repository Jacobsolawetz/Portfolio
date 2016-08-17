/**
 * The Trade class is a data structure used to hold trades that will most likely be executed
 * In addition to the Trigger class functionality, the Trade class allows the user to very easily
 * modify intra-trade strategy such as variable duration exits since it contains the total
 * percentage return for the stated duration of the trade including an arrayList of percentage changes
 * for each individual day in the duration
 */

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Trade {

	private Date dayTriggered;
	private int algoNumber;
	private String indication;
	private String symbol;
	private int duration;
	private int intendedDuration; //is static and cannot change
	private double beta;
	private double leverage;
	private double variance;
	private Symbol symbolData;
	
	private ArrayList<Double> closes;
	private ArrayList<Integer> volumes;
	private ArrayList<Double> highs;
	private ArrayList<Double> opens;
	private ArrayList<Double> lows;
	private ArrayList<Date> dates;
	
	//arrayLists for corresponding SPY data so that PV can be calculated
	private ArrayList<Double> spyCloses;
	private ArrayList<Integer> spyVolumes;
	private ArrayList<Double> spyHighs;
	private ArrayList<Double> spyOpens;
	private ArrayList<Double> spyLows;
	private ArrayList<Date> spyDates;
	private SimpleDateFormat df;
	
	public Trade(Trigger trigger){
		dayTriggered = trigger.getDate();
		algoNumber = trigger.getAlgoNumber();
		indication = trigger.getIndication();
		symbol = trigger.getSymbol();
		duration = trigger.getDuration();
		
		closes = new ArrayList<Double>();
		volumes = new ArrayList<Integer>();
		highs = new ArrayList<Double>();
		opens = new ArrayList<Double>();
		lows = new ArrayList<Double>();
		dates = new ArrayList<Date>();
		
		spyCloses = new ArrayList<Double>();
		spyVolumes = new ArrayList<Integer>();
		spyHighs = new ArrayList<Double>();
		spyOpens = new ArrayList<Double>();
		spyLows = new ArrayList<Double>();
		spyDates = new ArrayList<Date>();
		df = new SimpleDateFormat("MM/dd/yyyy");
		
		leverage = 1.0;
		beta = 1.0;
		variance = 1.0;
		
		intendedDuration = duration;
	}
	
	//a constructor for copying trade objects
	public Trade(Trade trade) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		String date = sdf.format(trade.getDate());
		dayTriggered = sdf.parse(date);
		algoNumber = new Integer(trade.getAlgoNumber());
		indication = new String(trade.getIndication());
		symbol = new String(trade.getSymbol());
		duration = new Integer(trade.getDuration());
		
		closes = new ArrayList<Double>(trade.getCloses());
		volumes = new ArrayList<Integer>(trade.getVolumes());
		highs = new ArrayList<Double>(trade.getHighs());
		opens = new ArrayList<Double>(trade.getOpens());
		lows = new ArrayList<Double>(trade.getLows());
		dates = new ArrayList<Date>(trade.getDates());
		
		spyCloses = new ArrayList<Double>(trade.getSpyCloses());
		spyVolumes = new ArrayList<Integer>(trade.getSpyVolumes());
		spyHighs = new ArrayList<Double>(trade.getSpyHighs());
		spyOpens = new ArrayList<Double>(trade.getSpyOpens());
		spyLows = new ArrayList<Double>(trade.getSpyLows());
		spyDates = new ArrayList<Date>(trade.getSpyDates());
		df = new SimpleDateFormat("MM/dd/yyyy");
		
		leverage = trade.getLeverage();
		beta = trade.getStockBeta();
		variance = trade.getVariance();
		
		//symbolData has to be added
		symbolData = trade.getSymbolData();
		
		intendedDuration = duration;
	}
	
	public void setSymbolData(Symbol sym){
		symbolData = sym;
	}
	
	public Symbol getSymbolData(){
		return symbolData;
	}
	
	public double getVariance(){
		return variance;
	}
	
	public void setVariance(Double varianceValue) {
		variance = varianceValue;
	}
	
	//If the indication is shortPV or longPV, the returned beta should factor in the SPY beta of 1
	//If the indication is short, the returned beta should be negative
	public double getTradeBeta(){
		if(getIndication().equals("short DN")){
			return beta + 1.0;
		} else if(getIndication().equals("long DN")){
			return beta - 1.0;
		} else if(getIndication().equals("short MBN")){
			return 0;
		} else if(getIndication().equals("long MBN")){
			return 0;
		} else if(getIndication().equals("short Reg")){
			return -beta;
		} else{ //long reg
			return beta;
		}
	}
	
	public double getStockBeta(){
		return beta;
	}
	
	//Important: when beta is set, it is referring to the beta 
	//of the primary stock not the beta of the stock and SPY in a pairs trade
	public void setStockBeta(Double betaValue){
		beta = betaValue;
	}
	
	public Date getDate(){
		return dayTriggered;
	}
	
	public void setDate(Date date){
		dayTriggered = date;
	}
	
	public int getAlgoNumber(){
		return algoNumber;
	}
	
	public void setAlgoNumber(Integer num){
		algoNumber = num;
	}
	
	public String getIndication(){
		return indication;
	}
	
	public void setIndication(String ind){
		indication = ind;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public void setSymbol(String sym){
		symbol = sym;
	}
	
	public int getDuration(){
		return duration;
	}
	
	public void setDuration(Integer dur){
		duration = dur;
	}
	
	//***********************************
	//For open, high, low, close, and volume, .get(0) is the day of the trade 
	//and .get(1) is the next day in the future etc.
	
	
	//loads in an arrayList of the following X close prices, where X can be changed in the multi-threaded backtester
	public void setCloses(ArrayList<Double> listOfCloses){
		Collections.reverse(listOfCloses);
		closes = listOfCloses;
	}
	
	//loads in an arrayList of the following X SPY close prices
	public void setSpyCloses(ArrayList<Double> listOfSpyCloses){
		Collections.reverse(listOfSpyCloses);
		spyCloses = listOfSpyCloses;
	}
	
	//getter method for closes
	public ArrayList<Double> getCloses(){
		return closes;
	}
	
	//getter method for SPY closes
	public ArrayList<Double> getSpyCloses(){
		return spyCloses;
	}
	
	//loads in an arrayList of the following X volumes
	public void setVolumes(ArrayList<Integer> listOfVolumes){
		Collections.reverse(listOfVolumes);
		volumes = listOfVolumes;
	}
	
	//loads in an arrayList of the following X SPY volumes
	public void setSpyVolumes(ArrayList<Integer> listOfSpyVolumes){
		Collections.reverse(listOfSpyVolumes);
		spyVolumes = listOfSpyVolumes;
	}
	
	//getter method for volumes
	public ArrayList<Integer> getVolumes(){
		return volumes;
	}
	
	//getter method for SPY volumes
	public ArrayList<Integer> getSpyVolumes(){
		return spyVolumes;
	}
	
	//loads in an arrayList of the following X highs
	public void setHighs(ArrayList<Double> listOfHighs){
		Collections.reverse(listOfHighs);
		highs = listOfHighs;
	}
	
	//loads in an arrayList of the following X SPY highs
	public void setSpyHighs(ArrayList<Double> listOfSpyHighs){
		Collections.reverse(listOfSpyHighs);
		spyHighs = listOfSpyHighs;
	}
	
	//getter method for highs
	public ArrayList<Double> getHighs(){
		return highs;
	}
	
	//getter method for SPY highs
	public ArrayList<Double> getSpyHighs(){
		return spyHighs;
	}
	
	//loads in an arrayList of the following X lows
	public void setLows(ArrayList<Double> listOfLows){
		Collections.reverse(listOfLows);
		lows = listOfLows;
	}
	
	//loads in an arrayList of the following X SPY lows
	public void setSpyLows(ArrayList<Double> listOfSpyLows){
		Collections.reverse(listOfSpyLows);
		spyLows = listOfSpyLows;
	}
	
	//getter method for lows
	public ArrayList<Double> getLows(){
		return lows;
	}
	
	//getter method for SPY lows
	public ArrayList<Double> getSpyLows(){
		return spyLows;
	}

	//loads in an arrayList of the following X opens
	public void setOpens(ArrayList<Double> listOfOpens){
		Collections.reverse(listOfOpens);
		opens = listOfOpens;
	}
	
	//loads in an arrayList of the following X opens
	public void setSpyOpens(ArrayList<Double> listOfSpyOpens){
		Collections.reverse(listOfSpyOpens);
		spyOpens = listOfSpyOpens;
	}
	
	//getter method for opens
	public ArrayList<Double> getOpens(){
		return opens;
	}
	
	//getter method for SPY opens
	public ArrayList<Double> getSpyOpens(){
		return spyOpens;
	}

	//loads in an arrayList of the following X dates
	public void setDates(ArrayList<Date> listOfDates){
		Collections.reverse(listOfDates);
		dates = listOfDates;
	}
	
	//loads in an arrayList of the following X dates
	public void setSpyDates(ArrayList<Date> listOfSpyDates){
		Collections.reverse(listOfSpyDates);
		spyDates = listOfSpyDates;
	}
	
	//getter method for dates
	public ArrayList<Date> getDates(){
		return dates;
	}
	
	//getter method for SPY dates
	public ArrayList<Date> getSpyDates(){
		return spyDates;
	}
	
	//return the immutable intended/initial duration of the trade
	public int getIntendedDuration(){
		return intendedDuration;
	}
	
	//sets the leverage -> i.e. 1.5x
	public void setLeverage(double setLeverage){
		leverage = setLeverage;
	}
	
	public double getLeverage(){
		return leverage;
	}
	
	//checks to ensure that the trading dates for the SPY line up with the trading dates for the underlying equity
	//if not, this method will return false and all of the generated returns will be suspect
	//thus throwing an error
	public boolean dateLineUp(int dur){
		boolean datesLineUp = true;
		for(int i = 0; i < dur; i++){
			try{
				if(spyDates.get(i).compareTo(dates.get(i)) != 0){ //returns 0 if dates are equal
					datesLineUp = false;
				}
			} catch(IndexOutOfBoundsException ioobe){
				return false;
			}
		}
		return datesLineUp;
	}
	
	public boolean dateLineUp(int startDuration, int endDuration){
		boolean datesLineUp = true;
		for(int i = startDuration; i < endDuration; i++){
			try{
				if(spyDates.get(i).compareTo(dates.get(i)) != 0){ //returns 0 if dates are equal
					datesLineUp = false;
				}
			} catch(IndexOutOfBoundsException ioobe){
				return false;
			}
		}
		return datesLineUp;
	}
	
	//Is this in (new-old)/old?
	//returns the percentage return of the trade as a decimal for the original duration rounded to 5 decimal places
	public double getIntededReturn() throws IOException{		
		return getCustomReturn(getIntendedDuration());
	}
	
	//returns the cash return of the trade for the original duration rounded to two decimal places. CASHHHHHHH
	public double getIntededReturn(double tradeAmount) throws IOException {
		return Math.round(tradeAmount*getIntededReturn()*100.0)/100.0;
	}
	
	//does this give us decimal returns or percentage returns
	public double getCustomReturn(int customDuration) throws IOException{
		if(dateLineUp(customDuration)){ //ensure that dates match
			try{
				Double change = 0.0;
				if(getIndication().equals("long Reg")){
					change = ((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
				} else if(getIndication().equals("short Reg")){
					change = -((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
				} else if(getIndication().equals("long DN")){
					change = ((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
					Double spyChange = ((getSpyCloses().get(customDuration) - getSpyCloses().get(0))/getSpyCloses().get(0));
					change = change - spyChange;
				} else if(getIndication().equals("short DN")){
					change = ((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
					Double spyChange = ((getSpyCloses().get(customDuration) - getSpyCloses().get(0))/getSpyCloses().get(0));
					change = -change + spyChange;
				} else if(getIndication().equals("long MBN")){
					double betaTotal = 1.0 + Math.abs(getStockBeta());
					double stockWeight = 1.0/betaTotal;
					double spyWeight = 1.0 - stockWeight;
					if(getStockBeta() < 0){ //if negative, each side of the pairs trade will have the same indication, therefore SPY weight is negative
						spyWeight = -spyWeight;
					}
					change = ((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
					Double spyChange = ((getSpyCloses().get(customDuration) - getSpyCloses().get(0))/getSpyCloses().get(0));
					change = change*stockWeight - spyChange*spyWeight;
				} else if(getIndication().equals("short MBN")){
					double betaTotal = 1.0 + Math.abs(getStockBeta());
					double stockWeight = 1.0/betaTotal;
					double spyWeight = 1.0 - stockWeight;
					if(getStockBeta() < 0){ //if negative, each side of the pairs trade will have the same indication, therefore SPY weight is negative
						spyWeight = -spyWeight;
					}
					change = ((getCloses().get(customDuration) - getCloses().get(0))/getCloses().get(0));
					Double spyChange = ((getSpyCloses().get(customDuration) - getSpyCloses().get(0))/getSpyCloses().get(0));
					change = -change*stockWeight + spyChange*spyWeight;
				}
				return Math.round(change * 100000.0)/100000.0; // rounds to five decimal places
			} catch(IndexOutOfBoundsException ioobe){
				throw new IOException("Index Out Of Bounds Problem with " + getSymbol() + " on " + df.format(getDate()));
			}
		} else{ 
			throw new IOException("Date LineUp Problem with " + getSymbol() + " on " + df.format(getDate()));
		}
	}
	
	//returns the cash return of the trade for the original duration
	public double getCustomReturn(int customDuration, double tradeAmount) throws IOException{
		return Math.round(tradeAmount*getCustomReturn(customDuration)*100.0)/100.0;
	}
	
	public double getCustomReturn(int startCustomDuration, int endCustomDuration) throws IOException{
		if(dateLineUp(startCustomDuration, endCustomDuration)){ //ensure that dates match
			try{
				Double change = 0.0;
				//potential error: looks like. is endCustom duration returning the the new close. and startCustom the old close 
				if(getIndication().equals("long Reg")){
					change = ((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
				} else if(getIndication().equals("short Reg")){
					change = -((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
				} else if(getIndication().equals("long DN")){
					change = ((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
					Double spyChange = ((getSpyCloses().get(endCustomDuration) - getSpyCloses().get(startCustomDuration))/getSpyCloses().get(startCustomDuration));
					change = change - spyChange;
				} else if(getIndication().equals("short DN")){
					change = ((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
					Double spyChange = ((getSpyCloses().get(endCustomDuration) - getSpyCloses().get(startCustomDuration))/getSpyCloses().get(startCustomDuration));
					change = -change + spyChange;
				} else if(getIndication().equals("long MBN")){
					double betaTotal = 1.0 + Math.abs(getStockBeta());
					double stockWeight = 1.0/betaTotal;
					double spyWeight = 1 - stockWeight;
					if(getStockBeta() < 0){ //if negative, each side of the pairs trade will have the same indication, therefore SPY weight is negative
						spyWeight = -spyWeight;
					}
					change = ((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
					Double spyChange = ((getSpyCloses().get(endCustomDuration) - getSpyCloses().get(startCustomDuration))/getSpyCloses().get(startCustomDuration));
					change = change*stockWeight - spyChange*spyWeight;
				} else if(getIndication().equals("short MBN")){
					double betaTotal = 1.0 + Math.abs(getStockBeta());
					double stockWeight = 1.0/betaTotal;
					double spyWeight = 1 - stockWeight;
					if(getStockBeta() < 0){ //if negative, each side of the pairs trade will have the same indication, therefore SPY weight is negative
						spyWeight = -spyWeight;
					}
					change = ((getCloses().get(endCustomDuration) - getCloses().get(startCustomDuration))/getCloses().get(startCustomDuration));
					Double spyChange = ((getSpyCloses().get(endCustomDuration) - getSpyCloses().get(startCustomDuration))/getSpyCloses().get(startCustomDuration));
					change = -change*stockWeight + spyChange*spyWeight;
				}
				return Math.round(change * 100000.0)/100000.0; // rounds to five decimal places
			} catch(IndexOutOfBoundsException ioobe){
				throw new IOException("Index Out Of Bounds Problem with " + getSymbol() + " on " + df.format(getDate()));
			}
		} else{ 
			throw new IOException("Date LineUp Problem with " + getSymbol() + " on " + df.format(getDate()));
		}
	}
}
