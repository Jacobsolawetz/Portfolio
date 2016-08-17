import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SharpeRatio {
	private static Map<Date, ArrayList<Double>> dateMap;
	private static ArrayList<Date> keyList;

	
	public static void main(String[] args) throws ParseException, IOException {
		readDatesandAPPT();
		runSimulation2(1000000.00, 4);
		//printDateMap();
//		ArrayList<ArrayList<Double>> returnSet = new ArrayList<ArrayList<Double>>();
//		for(int i = 0; i < 100; i++){
//			ArrayList<Double> returns = runSimulation(1000000.00);
//			returnSet.add(returns);
//		}
//		int total = 0;
//		int y1Total = 0;
//		int y2Total = 0;
//		int y3Total = 0;
//		int count = 0;
//		for(ArrayList<Double> returnData : returnSet){
//			total += returnData.get(3);
//			y1Total += returnData.get(0);
//			y2Total += returnData.get(1);
//			y3Total += returnData.get(2);
//			count++;
//		}
//		
//		System.out.println("Average Ending Balance: " + total/count);
		//System.out.println("Average Year 1 Change: " + y1Total/count + "%");
		//System.out.println("Average Year 2 Change: " + y2Total/count + "%");
		//System.out.println("Average Year 3 Change: " + y3Total/count + "%");
		
		
	}
	
	//assuming one trigger randomly choosed per day
	public static ArrayList<Double> runSimulation(double startSum, double leverage){
		Random myRandomizer = new Random();
		DateFormat df = new SimpleDateFormat("M/d/yyyy");
		int count = 0;
		ArrayList<Double> yearlyReturns = new ArrayList<Double>();
		double yearOneSum = 0.00;
		double yearTwoSum = 0.00;
		double sum = startSum;
		for(Date date : keyList){
			if(count < 800){
				double randomizedAlgoChange = dateMap.get(date).get(myRandomizer.nextInt(dateMap.get(date).size()));
				sum += sum*leverage*(randomizedAlgoChange*.01);
				System.out.println(df.format(date) + ": " + randomizedAlgoChange + "   " + Math.round(sum));
			}
			if(count == 184){
				yearOneSum = sum;
				
			} else if(count == 366){
				yearTwoSum = sum;
			}
			count++;
		}
		//System.out.println("Year 1 Return: " + ((yearOneSum - startSum)/startSum)*100);
		//System.out.println("Year 2 Return: " + ((yearTwoSum - yearOneSum)/yearOneSum)*100);
		//System.out.println("Year 3 Return: " + ((sum - yearTwoSum)/yearTwoSum)*100);
		yearlyReturns.add(((yearOneSum - startSum)/startSum)*100);
		yearlyReturns.add(((yearTwoSum - yearOneSum)/yearOneSum)*100);
		yearlyReturns.add(((sum - yearTwoSum)/yearTwoSum)*100);
		yearlyReturns.add(sum);
		return yearlyReturns;
	}
	
	//assuming one trigger randomly choosed per day
	public static ArrayList<Double> runSimulation2(double startSum, double leverage){
		int tradesForM1 = 0;
		int tradesForM2 = 0;
		int tradesForM3 = 0;
		int tradesForM4 = 0;
		int tradesForM5 = 0;
		int tradesForM6 = 0;
		int tradesForM7 = 0;
		int tradesForM8 = 0;
		int tradesForM9 = 0;
		int tradesForM10 = 0;
		int tradesForM11 = 0;
		int tradesForM12 = 0;
		
		Random myRandomizer = new Random();
		DateFormat df = new SimpleDateFormat("M/d/yyyy");
		int count = 0;
		ArrayList<Double> yearlyReturns = new ArrayList<Double>();
		double yearOneSum = 0.00;
		double yearTwoSum = 0.00;
		double sum = startSum;
		for(Date date : keyList){
			int tradeCount = 0;
			if(count < 800){
				double total = 0.00;
				for(Double change : dateMap.get(date)){
					total += change;
					tradeCount++;
				}
				double algoChange = total/dateMap.get(date).size();
				
				if(date.getMonth() + 1 == 1){
					tradesForM1 += tradeCount;
				} else if(date.getMonth() + 1 == 2){
					tradesForM2 += tradeCount;
				} else if(date.getMonth() + 1 == 3){
					tradesForM3 += tradeCount;
				} else if(date.getMonth() + 1 == 4){
					tradesForM4 += tradeCount;
				} else if(date.getMonth() + 1 == 5){
					tradesForM5 += tradeCount;
				} else if(date.getMonth() + 1 == 6){
					tradesForM6 += tradeCount;
				} else if(date.getMonth() + 1 == 7){
					tradesForM7 += tradeCount;
				} else if(date.getMonth() + 1 == 8){
					tradesForM8 += tradeCount;
				} else if(date.getMonth() + 1 == 9){
					tradesForM9 += tradeCount;
				} else if(date.getMonth() + 1 == 10){
					tradesForM10 += tradeCount;
				} else if(date.getMonth() + 1 == 11){
					tradesForM11 += tradeCount;
				} else if(date.getMonth() + 1 == 12){
					tradesForM12 += tradeCount;
				}
				
				sum += sum*leverage*(algoChange*.01);
				System.out.println(df.format(date) + ": " + (algoChange) + "   " + Math.round(sum));
			}
			if(count == 184){
				yearOneSum = sum;
				
			} else if(count == 366){
				yearTwoSum = sum;
			}
			count++;
		}
		System.out.println("Year 1 Return: " + ((yearOneSum - startSum)/startSum)*100);
		System.out.println("Year 2 Return: " + ((yearTwoSum - yearOneSum)/yearOneSum)*100);
		System.out.println("Year 3 Return: " + ((sum - yearTwoSum)/yearTwoSum)*100);
		yearlyReturns.add(((yearOneSum - startSum)/startSum)*100);
		yearlyReturns.add(((yearTwoSum - yearOneSum)/yearOneSum)*100);
		yearlyReturns.add(((sum - yearTwoSum)/yearTwoSum)*100);
		yearlyReturns.add(sum);
		return yearlyReturns;
	}

	public static void readDatesandAPPT() throws ParseException, IOException{
		String dateFile = "/Users/14price/Documents/Dates.csv";
		String apptFile = "/Users/14price/Documents/APPT.csv";
		BufferedReader brDates = null;
		BufferedReader brAPPT = null;
		String dateLine = "";
		String apptLine = "";
		String cvsSplitBy = ",";
		dateMap = new HashMap<Date, ArrayList<Double>>();
		keyList = new ArrayList<Date>();
		
			 
		brDates = new BufferedReader(new FileReader(dateFile));
		brAPPT = new BufferedReader(new FileReader(apptFile));
		
		while ((dateLine = brDates.readLine()) != null) {
		    // use comma as separator
			apptLine = brAPPT.readLine();
			String[] dateData = dateLine.split(cvsSplitBy);
			String[] apptData = apptLine.split(cvsSplitBy);
			Date date = null;
			for(int i = 0; i < dateData.length; i++){
				if(dateData[i].equals("#N/A")){
					continue;
				} else {
					DateFormat format = new SimpleDateFormat("M/d/yyyy");
					date = format.parse(dateData[i]);
				}
				
				if(dateMap.get(date) != null){
					dateMap.get(date).add(Double.parseDouble(apptData[i]));
				} else {
					dateMap.put(date, new ArrayList<Double>());
					dateMap.get(date).add(Double.parseDouble(apptData[i]));
					keyList.add(date);
				}
			}
	 
		}
		
		brDates.close();
		brAPPT.close();
		Collections.sort(keyList);
	}
	
	public static void printDateMap() {
		DateFormat df = new SimpleDateFormat("M/d/yyyy");
		for(Date date : keyList){
			System.out.print(df.format(date) + ": ");
			for(Double change : dateMap.get(date)){
				System.out.print(change + ", ");
			}
			System.out.println();
		}
	}
}