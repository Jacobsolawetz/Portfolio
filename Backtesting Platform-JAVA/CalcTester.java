import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CalcTester {
	
//	private static ArrayList<Integer> volume;
//	private static ArrayList<Double> close;
//	private static ArrayList<Double> adjClose;
//	private static ArrayList<Double> open;
//	private static ArrayList<Double> high;
//	private static ArrayList<Double> low;
//	private static ArrayList<String> date;

	public static void main(String[] args) throws ParseException, IOException, InterruptedException {
		
		String string = "long Reg";
		System.out.println(string.substring(0, 4));
//		Contact martin = new Contact("Martin Meusburger", "Vice President, Global Market Access", "32 Advisors");
//       martin.getPersonalInfo();
		
		//for(int i = 0; i < 20; i++){
//			int sleep = (int) (Math.random()*2000);
//			System.out.println(sleep);
//		}
		
		//ArrayList<String> symbols = new ArrayList<String>();
//		//ArrayList<String> symbolsFormatted = new ArrayList<String>();
//		for(int i = 1; i <= 10; i++){
//			symbols.addAll(getSymbols("SymbolsP" + i + ".txt"));
//		}
//		for(String symbol : symbols){
//			System.out.print(symbol.trim() + " US Equity, ");
//		}
		//reads in the list of symbols
//		ArrayList<String> symbols = getSymbols("CurrentPosition.txt");
//		
//		for(String symbol : symbols){
//			Data data = new Data(symbol, 100);
//			System.out.println(data.getCurrentClose());
//		}
		
		
//		//gets current date
//		Calendar cal = Calendar.getInstance();
//		Date currentDate = cal.getTime();
//		//DateUtil tradingDayGenerator = new DateUtil();
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		
//		//generates the dates of the next 2 to 6 business days in the correct string format for the earnings grabber
//		Date twoDays = DateUtil.addTradingDay(currentDate);
//		Date threeDays = DateUtil.addTradingDay(twoDays);
//		Date fourDays = DateUtil.addTradingDay(threeDays);
//		Date fiveDays = DateUtil.addTradingDay(fourDays);
//		Date sixDays = DateUtil.addTradingDay(fiveDays);
//		Date sevenDays = DateUtil.addTradingDay(sixDays);
//		
//		String oneD = df.format(twoDays);
//		String twoD = df.format(DateUtil.addTradingDay(twoDays));
//		String threeD = df.format(DateUtil.addTradingDay(threeDays));
//		String fourD = df.format(DateUtil.addTradingDay(fourDays));
//		String fiveD = df.format(DateUtil.addTradingDay(fiveDays));
//		String sixD = df.format(DateUtil.addTradingDay(sixDays));
//		String sevenD = df.format(DateUtil.addTradingDay(sevenDays));
//		
//		//gets the scraped earnings calendar info for the next 2-7 days
//		ArrayList<ArrayList<String>> reportsIn1Day = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(oneD));
//		ArrayList<ArrayList<String>> reportsIn2Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(twoD));
//		ArrayList<ArrayList<String>> reportsIn3Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(threeD));
//		ArrayList<ArrayList<String>> reportsIn4Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(fourD));
//		ArrayList<ArrayList<String>> reportsIn5Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(fiveD));
//		ArrayList<ArrayList<String>> reportsIn6Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(sixD));
//		ArrayList<ArrayList<String>> reportsIn7Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(sevenD));
//		
//		for(String symbol : symbols){
//			for(ArrayList<String> infoList : reportsIn1Day){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + oneD + " " + infoList.get(3));
//				}
//			}
//			for(ArrayList<String> infoList : reportsIn2Days){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + twoD + " " + infoList.get(3));
//				}
//			}
//			for(ArrayList<String> infoList : reportsIn3Days){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + threeD + " " + infoList.get(3));
//				}
//			}
//			for(ArrayList<String> infoList : reportsIn4Days){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + fourD + " " + infoList.get(3));
//				}
//			}
//			for(ArrayList<String> infoList : reportsIn5Days){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + fiveD + " " + infoList.get(3));
//				}
//			}
//			for(ArrayList<String> infoList : reportsIn6Days){
//				if(infoList.get(1).equals(symbol)){
//					System.out.println(infoList.get(1) + " reports on " + sixD + " " + infoList.get(3));
//				}
//			}
//		}
		
		
//		Data symbolData = new Data("CTRP", 200);
//		System.out.println(symbolData.getCurrentVolume());
//		ArrayList<Double> historicalPlusCurrentADJClose = symbolData.getAdjClose();
//		historicalPlusCurrentADJClose.add(0, symbolData.getCurrentClose());
		
//		Data spy = new Data("WOR", 200);
//		for(Double close : spy.getHistoricalPlusCurrentADJClose()){
//			System.out.println(close);
//		}
//		double b =  new RSI().getRSI(14, spy.getHistoricalPlusCurrentADJClose());
//		System.out.println();
//		System.out.println(b);
//		ArrayList<Object> dates = new ArrayList<Object>();
//		for(Object date : dates){
//			System.out.println("hey");
//		}
//		
//		Zacks zacks = new Zacks();
//		System.out.println(zacks.scrapePrevEarningsData("BAC").get(0));
//		System.out.println(zacks.scrapePrevEarningsData("BAC").get(1));
//		System.out.println(zacks.scrapePrevEarningsData("BAC").get(2));
//		HashMap<String, ArrayList<ArrayList<Object>>> returnMap = new HashMap<String, ArrayList<ArrayList<Object>>>();
//		ArrayList<ArrayList<Object>> container = new ArrayList<ArrayList<Object>>();
//		ArrayList<Object> line1 = new ArrayList<Object>();
//		line1.add("1, ");
//		line1.add("2, ");
//		line1.add("3, ");
//		line1.add("4");
//		ArrayList<Object> line2 = new ArrayList<Object>();;
//		line2.add("l1, ");
//		line2.add("l2, ");
//		line2.add("l3, ");
//		line2.add("l4");
//		container.add(line1);
//		container.add(line2);
//		returnMap.put("AAPL", container);
//		
//		ArrayList<Object> line3 = new ArrayList<Object>();;
//		line3.add("g1, ");
//		line3.add("g2, ");
//		line3.add("g3, ");
//		line3.add("g4");
//		
//		returnMap.get("AAPL").add(line3);
//		
//		for(Object string : returnMap.get("AAPL").get(0)){
//			System.out.print(string);
//		}
//		System.out.println();
//		for(Object string : returnMap.get("AAPL").get(1)){
//			System.out.print(string);
//		}
//		System.out.println();
//		for(Object string : returnMap.get("AAPL").get(2)){
//			System.out.print(string);
//		}
		
		//System.out.println(getPercentChange(historicalPlusCurrentADJClose, 7));
		//historicalPlusCurrentADJClose = symbolData.getAdjClose();
		//Collections.reverse(historicalPlusCurrentADJClose);
		//historicalPlusCurrentADJClose.add(symbolData.getCurrentClose());
		//System.out.println(getPercentChange(historicalPlusCurrentADJClose, 7));
		
		
		//Data data = new Data("GWRE", 200);
//		Data data1 = new Data("AAPL", 200);
//		Data data2 = new Data("GOOG", 200);
//		Data data3 = new Data("FNGN", 200);
		
		//gets current date
//		Calendar cal = Calendar.getInstance();
//		Date currentDate = cal.getTime();
//		//DateUtil tradingDayGenerator = new DateUtil();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		System.out.println();
//		System.out.println(df.format(cal.getTime()));
//		System.out.println(df.format(DateUtil.addTradingDay(currentDate)));
//		Data data = new Data("FNGN", 200);
//		System.out.println("Head: " + data.getAdjClose().get(0));
//		System.out.println("Tail: " + data.getAdjClose().get(data.getAdjClose().size()-1));
//		double b =  new RSI().getRSI(14, data.getAdjClose());
//		System.out.println(b);
		
//		 Zacks zacks = new Zacks();
//		 System.out.println(zacks.scrapePrevEarningsData("BRC").get(0));
//		 

//		 Data ge = new Data("GE", 250);
//		 for(Double open : ge.getHigh()){
//			 System.out.println(open);
//		 }
		// System.out.println(apple.getCurrentClose());

		// DataGatherer data = new DataGatherer();
		// ArrayList<ArrayList<Object>> contents =
		// data.extendedHistoricalData("AAPL", "20150203", "20141112");

//		 ArrayList<Double> closes = new ArrayList<Double>();
//		
//		 closes.add(45.15);
//		 closes.add(41.00);
//		 closes.add(45.15);
//		 closes.add(46.23);
//		 closes.add(46.08);
//		 closes.add(46.03);
//		 closes.add(46.83);
//		 closes.add(47.69);
//		 closes.add(47.54);
//		 closes.add(49.25);
//		 closes.add(49.23);
//		 closes.add(48.2);
//		 closes.add(47.57);
//		 closes.add(47.61);
//		 closes.add(48.08);
//		 closes.add(47.21);
//		 closes.add(46.76);
//		 closes.add(46.68);
//		 
//		
//		
//		 //RSI2 testRSI2 = new RSI2();
//		 double b =  new RSI().getRSI(14, closes);
//		 System.out.println(b);
		
	}
	
	
	
	public static double getMaxBefore(int duration, ArrayList<Double> closes) throws IOException{
		
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
	
	public static double getPercentChange(ArrayList<Double> closes, int duration){
		double newestClosePrice = closes.get(0);
		double oldestClosePrice = closes.get(duration - 1);
		System.out.println(oldestClosePrice);
		return ((newestClosePrice - oldestClosePrice)/oldestClosePrice)*100;
	}
	public static ArrayList<String> getSymbols(String fileName) throws IOException{
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
}
