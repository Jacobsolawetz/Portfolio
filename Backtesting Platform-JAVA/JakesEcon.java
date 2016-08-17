import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class JakesEcon {
	
	public static void main(String args[]) throws IOException, InterruptedException{
		ArrayList<String> symbols = getSymbols("Symbols3.txt");
		ArrayList<String> errorSymbols = new ArrayList<String>();
		HashMap<String, ArrayList<ArrayList<Object>>> data = new HashMap<String, ArrayList<ArrayList<Object>>>();
		int count = 0;
		for(String symbol: symbols){
			try{
				count++;
				data.put(symbol, getHistoricalData(symbol, 2000)); 
				System.out.println(symbol);
				if(count >= 50){
					//heap dump
					writeTextFile(data);
					data.clear();
					count = 0;
				}
			} catch (Exception e) {
				errorSymbols.add(symbol);
			}
		}
		
		writeTextFile(data);
		for(String errorSymbol : errorSymbols) {
			System.out.println(errorSymbol);
		}
		
		
		//reads in the list of symbols
//		ArrayList<String> symbols = getSymbols("ErrorInfo.txt");
//		int count = 0;
//		for(String symbol: symbols){
//			try{
//				Data data = new Data(symbol, 201);
//				//reverse the order of close
//				ArrayList<Double> reversedClose = new ArrayList<Double>();
//				for (int i = data.getHistoricalPlusCurrentADJClose().size() - 1; i >= 0; i--) {
//					reversedClose.add(data.getHistoricalPlusCurrentADJClose().get(i));
//			    }
//				
//				ArrayList<Double> pxChangeList = new ArrayList<Double>();
//				double sum = 0;
//				double count2 = 0.00;
//				for(int i = 1; i < reversedClose.size(); i++){
//					double pxChange = Math.log(reversedClose.get(i)/reversedClose.get(i - 1));
//					pxChangeList.add(pxChange);
//					sum += pxChange;
//					count2++;
//				}
//				double mean = sum/count2;
//				
//				double sum2 = 0.00;
//				double count3 = 0.00;
//				for(Double px:pxChangeList){
//				   sum2+=Math.pow(px-mean,2);
//				   count3++;
//				}
//				double stdDev = Math.sqrt(sum2/count3);
//				System.out.println(symbol + "," + stdDev*Math.sqrt(252)*100); 
//				
//			} catch(Exception e){
//				continue;
//			}
//		}
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
	
	public static ArrayList<String> getData(String symbol) throws IOException{
		ArrayList<String> data = new ArrayList<String>();
		try {
			String address2 = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol.toUpperCase() + "&f=j1a2prkj";
			//address2 = address2.replaceFirst("^\uFFFE", "");
			
			java.net.URLConnection connection;
			java.net.URL url2 = new java.net.URL(address2);
			connection = url2.openConnection();
			connection.setConnectTimeout(15 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			
			InputStreamReader dr2;
			try {
				dr2 = new InputStreamReader(connection.getInputStream());
			} catch (FileNotFoundException fnfe2) {
				dr2 = new InputStreamReader(connection.getInputStream());
			}
			BufferedReader br2 = new BufferedReader(dr2);
			
			
			StringTokenizer st2;
			String s2;
			

			while ((s2 = br2.readLine()) != null) {
				st2 = new java.util.StringTokenizer(s2, ",");
				//.get(0) = marketCap
				data.add(st2.nextToken());
				//.get(1) = avg daily volume*share price
				double volume = Double.parseDouble(st2.nextToken());
				double sharePrice = Double.parseDouble(st2.nextToken());
				double vTimesS = volume*sharePrice;
				data.add(Double.toString(vTimesS));
				//.get(2) p/e ratio
				data.add(st2.nextToken());
				//.get(3) spread between 52 weeks high and 52 week low divided by share price
				double high = Double.parseDouble(st2.nextToken());
				double low = Double.parseDouble(st2.nextToken());
				double result = (high - low)/sharePrice;
				data.add(Double.toString(result));
				
			}
			
		} catch(FileNotFoundException fnfe4){
			
		}
		return data;
	}
	
	public static void writeTextFile(HashMap<String, ArrayList<ArrayList<Object>>> map) throws IOException {
		try {
	
			File file = new File("/Users/14price/Documents/Output.txt");
 
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String key: map.keySet()){
				for(ArrayList<Object> list : map.get(key)){
					bw.write(key + ", " + list.get(0) + ", " + list.get(1) + ", " + list.get(2) + ", " + list.get(3) + ", " + list.get(4) + ", " + list.get(5) + ", " + list.get(6) + "\n");
				}
			}
			
			bw.close();
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	public static ArrayList<ArrayList<Object>> getHistoricalData(String symbol, int duration) throws IOException {
		//initializes the arrayLists
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		
		//gets current date
		Calendar cal = Calendar.getInstance();
		Date currentDate = cal.getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		
		//gets the past x1 trading dates and adds them to an arrayList
		ArrayList<Date> dateRange = new ArrayList<Date>();
		Date oneDay = DateUtil.removeTradingDay(currentDate);
		for(int i = 0; i < duration; i++){
			dateRange.add(oneDay);
			oneDay = DateUtil.removeTradingDay(oneDay);
		}
		
		//converts date to string
		ArrayList<String> stringDateRange = new ArrayList<String>();
		for(Date date : dateRange){
			stringDateRange.add(df.format(date));
		}
		
		String startDate = stringDateRange.get(0);
		String endDate = stringDateRange.get(stringDateRange.size()-1);
		
		try{
			
			String address = "https://www.quandl.com/api/v1/datasets/WIKI/" + symbol.toUpperCase() + ".csv?auth_token=VzEsbSLxtutNG5d5DpxF";
			
			java.net.URL url = new java.net.URL(address);
			java.net.URLConnection connection;
			connection = url.openConnection();
			connection.setConnectTimeout(15 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			
			InputStreamReader dr;
			try {
				dr = new InputStreamReader(connection.getInputStream());
			} catch (FileNotFoundException fnfe2) {
				dr = new InputStreamReader(connection.getInputStream());
			}
			BufferedReader br = new BufferedReader(dr);
			
			
			StringTokenizer st;
			String s;
			
			// read title - Date,Open,High,Low,Close,Volume,Adj Close
			br.readLine();
			
			int count = 0;
			boolean missing = false;
			
			while ((s = br.readLine()) != null && count < duration) {
				st = new java.util.StringTokenizer(s, ",");
				try{
					ArrayList<Object> tempData = new ArrayList<Object>();
					//date
					tempData.add((String) st.nextToken());
					//skip
					st.nextToken();
					st.nextToken();
					st.nextToken();
					//close - get(4)
					tempData.add(Double.parseDouble(st.nextToken()));
					st.nextToken();
					st.nextToken();
					st.nextToken();
					//open
					tempData.add(Double.parseDouble(st.nextToken()));
					//high 
					tempData.add(Double.parseDouble(st.nextToken()));
					//low - get(3)
					tempData.add(Double.parseDouble(st.nextToken()));
					//adjusted close - get(6)
					tempData.add(Double.parseDouble(st.nextToken()));
					//volume - get(5)
					Double vol = Double.parseDouble(st.nextToken());
					tempData.add(Integer.valueOf(vol.intValue()));
					count++;
					if(missing == true){
						tempData.add(data.get(data.size() - 1).get(0));
						tempData.add(data.get(data.size() - 1).get(1));
						tempData.add(data.get(data.size() - 1).get(2));
						tempData.add(data.get(data.size() - 1).get(3));
						tempData.add(data.get(data.size() - 1).get(4));
						tempData.add(data.get(data.size() - 1).get(5));
						missing = false;
						count++;
					}
					data.add(tempData);
				} catch(NoSuchElementException nsee){ //catches blank entries in the file, missing data
					missing = true;
					continue;
				}
			
			}
		} catch(FileNotFoundException fnfe){
				//months - 1, day, year
				String year = startDate.substring(0, 4); 
				String month = (String) Integer.toString((Integer.parseInt(startDate.substring(4, 6)) - 1));
				String day = startDate.substring(6, 8);
				String year2 = endDate.substring(0, 4); 
				String month2 = (String) Integer.toString((Integer.parseInt(endDate.substring(4, 6)) - 1));
				String day2 = endDate.substring(6, 8);
				String address = "http://ichart.finance.yahoo.com/table.csv?s=" + symbol.toUpperCase() + "&a=" + month2 + "&b=" + day2 + "&c=" + year2 + "&d=" + month + "&e=" + day + "&f=" + year + "&g=d&ignore=.csv";
				
				java.net.URL url = new java.net.URL(address);
				java.net.URLConnection connection;
				connection = url.openConnection();
				connection.setConnectTimeout(15 * 1000);
				connection.setReadTimeout(20 * 1000);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				
				InputStreamReader dr;
				try {
					dr = new InputStreamReader(connection.getInputStream());
				} catch (FileNotFoundException fnfe2) {
					dr = new InputStreamReader(connection.getInputStream());
				}
				BufferedReader br = new BufferedReader(dr);
				
				
				StringTokenizer st;
				String s;
				
				// read title - Date,Open,High,Low,Close,Volume,Adj Close
				br.readLine();
				
				while ((s = br.readLine()) != null) {
					st = new java.util.StringTokenizer(s, ",");
					ArrayList<Object> tempData = new ArrayList<Object>();
					//date
					tempData.add((String) st.nextToken());
					//open
					tempData.add(Double.parseDouble(st.nextToken()));
					//high 
					tempData.add(Double.parseDouble(st.nextToken()));
					//low - get(3)
					tempData.add(Double.parseDouble(st.nextToken()));
					//close - get(4)
					tempData.add(Double.parseDouble(st.nextToken()));
					//volume - get(5)
					tempData.add(Integer.parseInt(st.nextToken()));
					//adjusted close - get(6)
					tempData.add(Double.parseDouble(st.nextToken()));
					data.add(tempData);
				}
		}
		return data;
	
	}
}
