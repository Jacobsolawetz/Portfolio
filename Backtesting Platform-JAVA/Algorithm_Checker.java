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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Algorithm_Checker {
	
	//private Zacks scraper;
	private ArrayList<String> symbols;
	private static Map<String, Long> marketCapData;
	private static ConcurrentHashMap<String, Data> data;
	private static int spyAroon;

	private static ArrayList<String> symbolsReportingIn1Day;
	private static ArrayList<String> symbolsReportingIn2Days;
	private static ArrayList<String> symbolsReportingIn3Days;
	private static ArrayList<String> symbolsReportingIn4Days;
	private static ArrayList<String> symbolsReportingIn5Days;
	private static ArrayList<String> symbolsReportingIn6Days;
	private static ArrayList<Double> historicalPlusCurrentSPY;
	public ArrayList<String> errorInfo;
	public CopyOnWriteArrayList<String> removeSymbols;
	
	public Algorithm_Checker(String symbolsList) throws IOException, InterruptedException{
		//creates a Zacks object
		//scraper = new Zacks();
		
		//for error checking
		errorInfo = new ArrayList<String>();
		
		//reads in the list of symbols
		symbols = getSymbols(symbolsList);
		
		//gets current date
		Calendar cal = Calendar.getInstance();
		Date currentDate = cal.getTime();
		//DateUtil tradingDayGenerator = new DateUtil();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		//generates the dates of the next 2 to 6 business days in the correct string format for the earnings grabber
		Date twoDays = DateUtil.addTradingDay(currentDate);
		Date threeDays = DateUtil.addTradingDay(twoDays);
		Date fourDays = DateUtil.addTradingDay(threeDays);
		Date fiveDays = DateUtil.addTradingDay(fourDays);
		Date sixDays = DateUtil.addTradingDay(fiveDays);
		Date sevenDays = DateUtil.addTradingDay(sixDays);
		
		String oneD = df.format(twoDays);
		String twoD = df.format(DateUtil.addTradingDay(twoDays));
		String threeD = df.format(DateUtil.addTradingDay(threeDays));
		String fourD = df.format(DateUtil.addTradingDay(fourDays));
		String fiveD = df.format(DateUtil.addTradingDay(fiveDays));
		String sixD = df.format(DateUtil.addTradingDay(sixDays));
		String sevenD = df.format(DateUtil.addTradingDay(sevenDays));
		
//		revisit this to check dates later
//		System.out.println(twoD);
//		System.out.println(threeD);
//		System.out.println(fourD);
//		System.out.println(fiveD);
//		System.out.println(sixD);
//		System.out.println(sevenD);
		
		
		//reads in the market cap data
		marketCapData = readMarketCapData();
		
		//calculate the SPY aroon value once
		Data spy = new Data("SPY", 100);
		historicalPlusCurrentSPY = spy.getAdjClose();
		historicalPlusCurrentSPY.add(0, spy.getCurrentClose());
		spyAroon = Aroon.getAroon(50, historicalPlusCurrentSPY);
		
		//gets the scraped earnings calendar info for the next 2-7 days
		ArrayList<ArrayList<String>> reportsIn1Day = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(oneD));
		ArrayList<ArrayList<String>> reportsIn2Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(twoD));
		ArrayList<ArrayList<String>> reportsIn3Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(threeD));
		ArrayList<ArrayList<String>> reportsIn4Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(fourD));
		ArrayList<ArrayList<String>> reportsIn5Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(fiveD));
		ArrayList<ArrayList<String>> reportsIn6Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(sixD));
		ArrayList<ArrayList<String>> reportsIn7Days = new ArrayList<ArrayList<String>>(Zacks.scrapeEarningsData(sevenD));
		
		//get(0) = corporate name, get(1) = symbol, get(2) = EPS, get(3) = report time
		
		//removes entries from the lists of upcoming earnings that are not in the symbol list
		reportsIn1Day = filter(reportsIn1Day, symbols);
		reportsIn2Days = filter(reportsIn2Days, symbols);
		reportsIn3Days = filter(reportsIn3Days, symbols);
		reportsIn4Days = filter(reportsIn4Days, symbols);
		reportsIn5Days = filter(reportsIn5Days, symbols);
		reportsIn6Days = filter(reportsIn6Days, symbols);
		reportsIn7Days = filter(reportsIn7Days, symbols);
		
		//Show block
//		System.out.println("List 1");
//		print2dArraylist(reportsIn2Days);
//		System.out.println("List 2");
//		print2dArraylist(reportsIn3Days);
//		System.out.println("List 3");
//		print2dArraylist(reportsIn4Days);
//		System.out.println("List 4");
//		print2dArraylist(reportsIn5Days);
//		System.out.println("List 5");
//		print2dArraylist(reportsIn6Days);
//		System.out.println("List 6");
//		print2dArraylist(reportsIn7Days);
		
		
		//if before market open, pass to previous day list and delete from old list
		symbolsReportingIn1Day = new ArrayList<String>(generateSymbolList(reportsIn1Day, reportsIn2Days));
		symbolsReportingIn2Days = new ArrayList<String>(generateSymbolList(reportsIn2Days, reportsIn3Days));
		symbolsReportingIn3Days = new ArrayList<String>(generateSymbolList(reportsIn3Days, reportsIn4Days));
		symbolsReportingIn4Days = new ArrayList<String>(generateSymbolList(reportsIn4Days, reportsIn5Days));
		symbolsReportingIn5Days = new ArrayList<String>(generateSymbolList(reportsIn5Days, reportsIn6Days));
		symbolsReportingIn6Days = new ArrayList<String>(generateSymbolList(reportsIn6Days, reportsIn7Days));
		
//		for(String ticker : symbolsReportingIn2Days){
//			System.out.println(ticker);
//		}
//		
		//gets the EPS estimates for the reports
		data = new ConcurrentHashMap<String, Data>();
		//int progressCount = 1;
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		removeSymbols = new CopyOnWriteArrayList<String>();
		//ArrayList<String> removeSymbols = new ArrayList<String>();
		for(final ArrayList<String> dataItems : reportsIn1Day){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn2Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn3Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn4Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn5Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn6Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				public String getSymbol(){
					return dataItems.get(1);
				}
			};
			threads.add(t);
		}
		for(final ArrayList<String> dataItems : reportsIn7Days){
			Thread t = new Thread(){
				public void run(){
					try{
						Data tempData = new Data(dataItems.get(1), 200);
						System.out.println(dataItems.get(1));
						tempData.setEPS(Double.parseDouble(dataItems.get(2)));
						ArrayList<Double> prevEPSData = Zacks.scrapePrevEarningsData(dataItems.get(1));
						//when there is no prev EPS data or not enough
						try{
							tempData.setPreviousEPS(prevEPSData.get(0));
							tempData.setPrevious2EPS(prevEPSData.get(1));
							tempData.setPrevious3EPS(prevEPSData.get(2));
						} catch(IndexOutOfBoundsException ioooe){
							try{
								tempData.setPreviousEPS(prevEPSData.get(0));
								tempData.setPrevious2EPS(prevEPSData.get(1));
								tempData.setPrevious3EPS(tempData.getPrevious2EPS());
							} catch(IndexOutOfBoundsException ioooe2){
								//changes them all to zero
								tempData.setPreviousEPS(0.00);
								tempData.setPrevious2EPS(0.00);
								tempData.setPrevious3EPS(0.00);
							}
						} catch(NullPointerException npe){
							//changes them all to zero
							tempData.setPreviousEPS(0.00);
							tempData.setPrevious2EPS(0.00);
							tempData.setPrevious3EPS(0.00);
						}
						data.put(dataItems.get(1), tempData);
						//System.out.println(dataItems.get(1) + "  -  " + progressCount + "/" + (reportsIn2Days.size() + reportsIn3Days.size() + reportsIn4Days.size() + reportsIn5Days.size() + reportsIn6Days.size() + reportsIn7Days.size()));
						//progressCount++;
					} catch(NumberFormatException nfe){
						System.out.println("NumberFormatException error with " + dataItems.get(1));
						//progressCount++;
						nfe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NoSuchElementException nsee){
						System.out.println("NoSuchElementException error with " + dataItems.get(1));
						//progressCount++;
						nsee.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(IndexOutOfBoundsException iobe){
						System.out.println("IndexOutOfBoundsException error with " + dataItems.get(1));
						//progressCount++;
						iobe.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					} catch(NullPointerException npe){
						System.out.println("NullPointerException error with " + dataItems.get(1));
						npe.printStackTrace();
						//progressCount++;
						removeSymbols.add(dataItems.get(1));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						removeSymbols.add(dataItems.get(1));
					}
				
				}
				
			};
			threads.add(t);
		}
		
		int count = 0;
		//start all threads
		for(Thread thread : threads){
			if(count % 30 == 0){
				Thread.sleep(3000);
			}
			//System.out.println(thread.getSymbol());
			thread.start();
			count++;
		}
		
		//wait for them to finish
		for(Thread thread : threads){
			thread.join();
		}
		
		//remove symbols from the report lists when the data cannot be collected
		for(String symbol : removeSymbols){
			if(symbolsReportingIn1Day.contains(new String(symbol))){
				System.out.println(symbolsReportingIn1Day.get(symbolsReportingIn1Day.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn1Day.remove(symbolsReportingIn1Day.indexOf(new String(symbol)));
			}
			if(symbolsReportingIn2Days.contains(new String(symbol))){
				System.out.println(symbolsReportingIn2Days.get(symbolsReportingIn2Days.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn2Days.remove(symbolsReportingIn2Days.indexOf(new String(symbol)));
			} else if(symbolsReportingIn3Days.contains(new String(symbol))){
				System.out.println(symbolsReportingIn3Days.get(symbolsReportingIn3Days.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn3Days.remove(symbolsReportingIn3Days.indexOf(new String(symbol)));
			} else if(symbolsReportingIn4Days.contains(new String(symbol))){
				System.out.println(symbolsReportingIn4Days.get(symbolsReportingIn4Days.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn4Days.remove(symbolsReportingIn4Days.indexOf(new String(symbol)));
			} else if(symbolsReportingIn5Days.contains(new String(symbol))){
				System.out.println(symbolsReportingIn5Days.get(symbolsReportingIn5Days.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn5Days.remove(symbolsReportingIn5Days.indexOf(new String(symbol)));
			} else if(symbolsReportingIn6Days.contains(new String(symbol))){
				System.out.println(symbolsReportingIn6Days.get(symbolsReportingIn6Days.indexOf(new String(symbol))) + " was removed due to a data collection error");
				symbolsReportingIn6Days.remove(symbolsReportingIn6Days.indexOf(new String(symbol)));
			}
		}
		
	}
	
	
	//reads a symbols file in the documents directory
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
	
	//changes the second dimension of the array from a date to the number of trading days until the report
	//if earnings is before open, subtract a day 
	public static void processArray(String[][] upcomingReports){
		DateUtil tradingDayGenerator = new DateUtil();
		//int tradingDaysTillReport = tradingDayGenerator.businessDaysFromToday(endDate, "yyyy-MM-dd");
		
	}
	
	//removes entries from the lists of upcoming earnings that are not in the symbol list
	public static ArrayList<ArrayList<String>> filter(ArrayList<ArrayList<String>> reports, ArrayList<String> symbols){
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
	public static void print2dArraylist(ArrayList<ArrayList<String>> list){
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
	
	public ArrayList<String> checkAlgo1() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 5;
		String indication = "short Reg";
		int algoNumber = 1;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			
			//**************CHANGE**************
			if(percentChange > 8 && pvPercentChange > 0 && rSI > 55 && rSI < 70 && vDBA >= .4 && vDBA <= 1.1){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo2() throws IOException{
		int y1 = 5;
		int x1 = 8;
		int w1 = 7;
		String indication = "long PV";
		int algoNumber = 2;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && spyAroon50 < 0 && vDBA >= .6 && vDBA <= 1.2){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo3() throws IOException{
		int y1 = 1;
		int x1 = 7;
		int w1 = 7;
		String indication = "long Reg";
		int algoNumber = 3;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 1){
			reports = symbolsReportingIn1Day;
		} else if(y1 == 2){ 
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < 0 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 2 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo4() throws IOException{
		int y1 = 3;
		int x1 = 10;
		int w1 = 8;
		String indication = "long PV";
		int algoNumber = 4;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -3 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && avDBSP >= 3 && avDBSP <= 5 && aroon25 < 0){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo5() throws IOException{
		int y1 = 3;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 5;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -1 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 3 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo6() throws IOException{
		int y1 = 3;
		int x1 = 10;
		int w1 = 9;
		String indication = "long Reg";
		int algoNumber = 6;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < 0 && pvPercentChange < -1 && marketCap >= 4 && marketCap <= 5 && rSI >= 25 && rSI <= 40 && aroon25 < 0){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo7() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 7;
		String indication = "long PV";
		int algoNumber = 7;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(epsPredicted < epsPredictedMinus1 && percentChange < -1 && pvPercentChange < -1 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 2 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo8() throws IOException{
		int y1 = 4;
		int x1 = 6;
		int w1 = 8;
		String indication = "long Reg";
		int algoNumber = 8;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && rSI >= 55 && rSI <= 70 && spyAroon50 > 0 && avDBSP >= 3 && avDBSP <= 5 && vDBA >= .4 && vDBA <= 1.2){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo9() throws IOException{
		int y1 = 4;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 9;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && rSI >= 55 && rSI <= 70 && aroon25 >= 50 && avDBSP >= 3 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo10() throws IOException{
		int y1 = 2;
		int x1 = 8;
		int w1 = 5;
		String indication = "short PV";
		int algoNumber = 10;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange > 7 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA <= 1){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo11() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 2;
		String indication = "short Reg";
		int algoNumber = 11;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange > 7 && avDBSP >= 4 && avDBSP <= 5 && vDBA >= .6 && vDBA <= 1.1){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo12() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 2;
		String indication = "long Reg";
		int algoNumber = 12;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < 0 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && vDBA <= 1.1 && avDBSP >= 4 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo13() throws IOException{
		int y1 = 2;
		int x1 = 7;
		int w1 = 8;
		String indication = "short Reg";
		int algoNumber = 13;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange > 8 && pvPercentChange > 0 && rSI >= 55 && rSI <= 70 && vDBA >= .4 && vDBA <= 1.2){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo14() throws IOException{
		int y1 = 2;
		int x1 = 8;
		int w1 = 4;
		String indication = "short PV";
		int algoNumber = 14;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange > 7 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA >= .6 && vDBA <= 1.2){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo15() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 7;
		String indication = "long Reg";
		int algoNumber = 15;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < -2 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore && vDBA <= 1.2 && vDBA >= .4){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo16() throws IOException{
		int y1 = 2;
		int x1 = 5;
		int w1 = 5;
		String indication = "short PV";
		int algoNumber = 16;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange > 6 && pvPercentChange > 0 && avDBSP >= 4 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo17() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 3;
		String indication = "long Reg";
		int algoNumber = 17;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < -2 && pvPercentChange < 0 && avDBSP >= 4 && avDBSP <= 5 && vDBA <= 1.2 && vDBA >= .4){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo18() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 18;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < -1 && marketCap >= 4 && marketCap <= 5 && maxBefore > minBefore){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
	}
	
	public ArrayList<String> checkAlgo19() throws IOException{
		int y1 = 5;
		int x1 = 6;
		int w1 = 6;
		String indication = "long Reg";
		int algoNumber = 19;
		
		ArrayList<String> reports = new ArrayList<String>();
		
		//matches the y1 with the appropriate report frame
		if(y1 == 2){
			reports = symbolsReportingIn2Days;
		} else if(y1 == 3){
			reports = symbolsReportingIn3Days;
		} else if(y1 == 4){
			reports = symbolsReportingIn4Days;
		} else if(y1 == 5){
			reports = symbolsReportingIn5Days;
		} else if(y1 == 6){
			reports = symbolsReportingIn6Days;
		} 
		
		ArrayList<String> trades = new ArrayList<String>();
		//delete
		int count = 0;
		
		//checks the algorithm for each stock
		for(String symbol : reports){
			Data symbolData = data.get(symbol);
			
			double percentChange = getPercentChange(symbolData.getHistoricalPlusCurrentADJClose(), x1);
			double pvPercentChange = getPVPercentChange(percentChange, historicalPlusCurrentSPY, x1);
			double epsPredicted = symbolData.getEPS();
			double epsPredictedMinus1 = symbolData.getPreviousEPS();
			double epsPredictedMinus2 = symbolData.getPrevious2EPS();
			int marketCap = scaleMC(marketCapData.get(symbol));
			int aroon25 = Aroon.getAroon(25, symbolData.getHistoricalPlusCurrentADJClose());
			int spyAroon50 = spyAroon;
			int avDBSP = getAVDBSP(30, symbolData.getHistoricalPlusCurrentVolume(), symbolData.getHistoricalPlusCurrentADJClose().get(0));
			double vDBA = getVDBA(30, w1, symbolData.getHistoricalPlusCurrentVolume());
			double minBefore = getMinBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double maxBefore = getMaxBefore(150, symbolData.getHistoricalPlusCurrentADJClose());
			double rSI = new RSI().getRSI(14, symbolData.getHistoricalPlusCurrentADJClose());
			
			//delete
			if(count < reports.size()){
				if(symbol.equals("FIVE")){
					errorInfo.add(symbolData.getHistoricalPlusCurrentADJClose().get(0) + " to  " + symbolData.getHistoricalPlusCurrentADJClose().get(x1 - 1));
				}
				
				System.out.print(y1 + ", ");
				System.out.print(x1 + ", ");
				System.out.print(w1 + ", ");
				System.out.print(symbol + ", ");
				System.out.print(percentChange + ", ");
				System.out.print(pvPercentChange + ", ");
				System.out.print(epsPredicted + ", ");
				System.out.print(epsPredictedMinus1 + ", ");
				System.out.print(epsPredictedMinus2 + ", ");
				System.out.print(marketCap + ", ");
				System.out.print(aroon25 + ", ");
				System.out.print(spyAroon50 + ", ");
				System.out.print(avDBSP + ", ");
				System.out.print(vDBA + ", ");
				System.out.print(minBefore + ", ");
				System.out.print(maxBefore + ", ");
				System.out.print(rSI + ", ");
				System.out.println();
				count++;
			}
			//**************CHANGE**************
			if(percentChange < -1 && pvPercentChange < 0 && marketCap >= 4 && marketCap <= 5 && avDBSP >= 4 && avDBSP <= 5){
				if(indication.equals("short Reg")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days");
				} else if(indication.equals("long Reg")){
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days");
				} else if(indication.equals("short PV")){
					trades.add("Algorithm #" + algoNumber + ": Short " + symbol + " for " + y1 + " days and long SPY with the same capital");
				} else {
					trades.add("Algorithm #" + algoNumber + ": Long " + symbol + " for " + y1 + " days and short SPY with the same capital");
				}
			}
			//**************CHANGE**************
		}
		return trades;
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
	
	public static double getMinBefore(int duration, ArrayList<Double> closes) throws IOException{
		
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
	
	public static double getVDBA(int duration, int durationOfAverage, ArrayList<Integer> volumes) throws IOException{
		
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
	
	
	public static int getAVDBSP(int duration, ArrayList<Integer> volumes, double sharePrice) throws IOException{
		
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
	
	public static double getPercentChange(ArrayList<Double> closes, int duration){
		double newestClosePrice = closes.get(0);
		double oldestClosePrice = closes.get(duration - 1);
		return ((newestClosePrice - oldestClosePrice)/oldestClosePrice)*100;
	}
	
	public static double getPVPercentChange(double percentChange, ArrayList<Double> spyData, int duration){
		double newestClosePrice = spyData.get(0);
		double oldestClosePrice = spyData.get(duration - 1);
		double spyChange = ((newestClosePrice - oldestClosePrice)/oldestClosePrice)*100;
		return percentChange - spyChange;
	}
	
	public static int scaleMC(Long marketCap){
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
	
	public static Map<String, Long> readMarketCapData(){
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
	
	public static ArrayList<String> generateSymbolList(ArrayList<ArrayList<String>> prev, ArrayList<ArrayList<String>> next){
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
