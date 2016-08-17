import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class Data {
	private double currentClose;
	private int currentVolume;
	private double epsEstimate;
	private double previousEPSEstimate;
	private double previous2EPSEstimate;
	private double previous3EPSEstimate;
	private ArrayList<Integer> volume;
	private ArrayList<Double> close;
	private ArrayList<Double> adjClose;
	private ArrayList<Double> open;
	private ArrayList<Double> high;
	private ArrayList<Double> low;
	private ArrayList<String> date;
	private ArrayList<Double> historicalPlusCurrentADJClose;
	private ArrayList<Integer> historicalPlusCurrentVolume;
	
	//gets historical data only
	public Data(String symbol, int duration) throws IOException {
		
		//intializes the arrayLists
		volume = new ArrayList<Integer>();
		close = new ArrayList<Double>();
		adjClose = new ArrayList<Double>();
		open = new ArrayList<Double>();
		high = new ArrayList<Double>();
		low = new ArrayList<Double>();
		date = new ArrayList<String>();
		historicalPlusCurrentADJClose = new ArrayList<Double>();
		historicalPlusCurrentVolume = new ArrayList<Integer>();
		
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
					//date
					date.add((String) st.nextToken());
					//skip
					st.nextToken();
					st.nextToken();
					st.nextToken();
					//close - get(4)
					close.add(Double.parseDouble(st.nextToken()));
					st.nextToken();
					st.nextToken();
					st.nextToken();
					//open
					open.add(Double.parseDouble(st.nextToken()));
					//high 
					high.add(Double.parseDouble(st.nextToken()));
					//low - get(3)
					low.add(Double.parseDouble(st.nextToken()));
					//adjusted close - get(6)
					adjClose.add(Double.parseDouble(st.nextToken()));
					//volume - get(5)
					Double vol = Double.parseDouble(st.nextToken());
					volume.add(Integer.valueOf(vol.intValue()));
					count++;
					if(missing == true){
						close.add(close.get(close.size() - 1));
						open.add(open.get(open.size() - 1));
						high.add(high.get(high.size() - 1));
						low.add(low.get(low.size() - 1));
						adjClose.add(adjClose.get(adjClose.size() - 1));
						volume.add(volume.get(volume.size() - 1));
						missing = false;
						count++;
					}
				} catch(NoSuchElementException nsee){ //catches blank entries in the file, missing data
					missing = true;
					continue;
				}

			}
			//makes sure that the previous trading day's data is present
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			Date yesterday = DateUtil.removeTradingDay(currentDate);
			if(!df2.format(DateUtil.removeTradingDay(currentDate)).equals(date.get(0))){
				getYFday(symbol, df2.format(DateUtil.removeTradingDay(currentDate)));
				System.out.println("Flag #1 for " + symbol);
			} else if(!df2.format(DateUtil.removeTradingDay(currentDate)).equals(date.get(0)) && !df2.format(DateUtil.removeTradingDay(yesterday)).equals(date.get(0))){ //two prev days are missing
				getYFday(symbol, df2.format(DateUtil.removeTradingDay(yesterday)));
				getYFday(symbol, df2.format(DateUtil.removeTradingDay(currentDate)));
				System.out.println("Flag #2 for " + symbol);
			}
			
			
			try {
				//String address2 = "https://download.finance.yahoo.com/d/quotes.csv?s=%40%5EDJI," + symbol.toUpperCase() + "&f=nl1v0";
				String address2 = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol.toUpperCase() + "&f=nl1v";
				//address2 = address2.replaceFirst("^\uFFFE", "");
				
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
					st2.nextToken();
					String nextToken = st2.nextToken();
					try{
						currentClose = Double.parseDouble(nextToken);
						//added below
						currentVolume = Integer.parseInt(st2.nextToken());
					} catch (NumberFormatException nfe){
						currentClose = Double.parseDouble(st2.nextToken());
						currentVolume = Integer.parseInt(st2.nextToken());
					}
				}
				
			} catch(FileNotFoundException fnfe3){
				
			}
			
		} catch(FileNotFoundException fnfe){ //catch leads to Yahoo finance data as a backup
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
				//date
				date.add((String) st.nextToken());
				//open
				open.add(Double.parseDouble(st.nextToken()));
				//high 
				high.add(Double.parseDouble(st.nextToken()));
				//low - get(3)
				low.add(Double.parseDouble(st.nextToken()));
				//close - get(4)
				close.add(Double.parseDouble(st.nextToken()));
				//volume - get(5)
				volume.add(Integer.parseInt(st.nextToken()));
				//adjusted close - get(6)
				adjClose.add(Double.parseDouble(st.nextToken()));
			}
			//gets current data
			try {
				String address2 = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol.toUpperCase() + "&f=nl1v";
				//address2 = address2.replaceFirst("^\uFFFE", "");
				
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
					st2.nextToken();
					String nextToken = st2.nextToken();
					try{
						currentClose = Double.parseDouble(nextToken);
						//added below
						currentVolume = Integer.parseInt(st2.nextToken());
					} catch (NumberFormatException nfe){
						currentClose = Double.parseDouble(st2.nextToken());
						currentVolume = Integer.parseInt(st2.nextToken());
					}
				}
				
			} catch(FileNotFoundException fnfe4){
				
			}
		}
		historicalPlusCurrentADJClose.add(this.getCurrentClose());
		historicalPlusCurrentADJClose.addAll(this.getAdjClose());
		historicalPlusCurrentVolume.add(this.getCurrentVolume());
		historicalPlusCurrentVolume.addAll(this.getVolume());
	}
	
	//gets a specific historical day from Yahoo finance and adds the data
	public void getYFday(String symbol, String date2) throws IOException{
		
		String address = "http://ichart.finance.yahoo.com/table.csv?s=" + symbol + "&a=" + date2.substring(5,7) + "&b=" + date2.substring(8,10) + "&c=" + date2.substring(0,4) + "&d=" + date2.substring(5,7) + "&e=" + date2.substring(8,10) + "&f=" + date2.substring(0,4)  + "&g=d&ignore=.csv";
		
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
			//date
			date.add(0, (String) st.nextToken());
			//open
			open.add(0, Double.parseDouble(st.nextToken()));
			//high 
			high.add(0, Double.parseDouble(st.nextToken()));
			//low - get(3)
			low.add(0, Double.parseDouble(st.nextToken()));
			//close - get(4)
			close.add(0, Double.parseDouble(st.nextToken()));
			//volume - get(5)
			volume.add(0, Integer.parseInt(st.nextToken()));
			//adjusted close - get(6)
			adjClose.add(0, Double.parseDouble(st.nextToken()));
		}
	}
	
	//returns close
	public ArrayList<Double> getClose(){
		return close;
	}
	
	//returns adjusted close
	public ArrayList<Double> getAdjClose(){
		return adjClose;
	}
	//returns high
	public ArrayList<Double> getHigh(){
		return high;
	}
	//returns low
	public ArrayList<Double> getLow(){
		return low;
	}
	//returns low
	public ArrayList<String> getDate(){
		return date;
	}
	//returns volume
	public ArrayList<Integer> getVolume(){
		return volume;
	}
	//returns current volume, scaling it accordingly depending on how much longer the trading day will be (change times for different time zones)
	public int getCurrentVolume(){
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		int minutesSinceOpen = (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE)) - 510 - 10;  //10 is for data delay
		int normalDaysMinutes = 390;
		int correctedVolume = (int) (((double) normalDaysMinutes/minutesSinceOpen)*currentVolume);
		return correctedVolume;
	}
	//returns volume
	public double getCurrentClose(){
		return currentClose;
	}
	//sets EPS estimate
	public void setEPS(double eps){
		epsEstimate = eps;
	}
	//gets EPS estimate
	public double getEPS(){
		return epsEstimate;
	}
	//sets prev EPS estimate
	public void setPreviousEPS(double prevEps){
		previousEPSEstimate = prevEps;
	}
	//gets EPS estimate
	public double getPreviousEPS(){
		return previousEPSEstimate;
	}
	
	//sets prev 2 EPS estimate
	public void setPrevious2EPS(double prevEps){
		previous2EPSEstimate = prevEps;
	}
	//gets prev 2  EPS estimate
	public double getPrevious2EPS(){
		return previous2EPSEstimate;
	}
	
	//sets prev 3 EPS estimate
	public void setPrevious3EPS(double prevEps){
		previous3EPSEstimate = prevEps;
	}
	//gets prev 3 EPS estimate
	public double getPrevious3EPS(){
		return previous3EPSEstimate;
	}
	//gets historical adj closes + real time
	public ArrayList<Double> getHistoricalPlusCurrentADJClose(){
		return historicalPlusCurrentADJClose;
	}
	//gets historical adj closes + real time
	public ArrayList<Integer> getHistoricalPlusCurrentVolume(){
		return historicalPlusCurrentVolume;
	}
		
	
}
