import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

public class DataGatherer {
	
//	public static void main(String[] args) throws IOException {
//		ArrayList<Object> data = new ArrayList<Object>(historicalData("GOOG", "20121123"));
//		
//		System.out.println("Date: " + data.get(0));
//		System.out.println("Open: " + data.get(1));
//		System.out.println("High: " + data.get(2));
//		System.out.println("Low: " + data.get(3));
//		System.out.println("Close: " + data.get(4));
//		System.out.println("Volume: " + data.get(5));
//		System.out.println("Adj Close: " + data.get(6));
//		
//		
//	}
	
	//input dates in YYYYMMDD format
	public static ArrayList<Object> historicalData(String symbol, String date) throws IOException {
		
		//months - 1, day, year
		String year = date.substring(0, 4); 
		String month = (String) Integer.toString((Integer.parseInt(date.substring(4, 6)) - 1));
		String day = date.substring(6, 8);
		String address = "http://ichart.yahoo.com/table.csv?s=" + symbol.toUpperCase() + "&a=" + month + "&b=" + day + "&c=" + year + "&d=" + month + "&e=" + day + "&f=" + year + "&g=d&ignore=.csv";
		
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
		} catch (FileNotFoundException fnfe) {
			dr = new InputStreamReader(connection.getInputStream());
		}
		BufferedReader br = new BufferedReader(dr);
		
		
		StringTokenizer st;
		String s;
		
		// read title - Date,Open,High,Low,Close,Volume,Adj Close
		br.readLine();
		
		ArrayList<Object> data = new ArrayList<Object>();
		
		while ((s = br.readLine()) != null) {
			st = new java.util.StringTokenizer(s, ",");
			
			//date - get(0)
			data.add((String) st.nextToken());
			//open - get(1)
			data.add(Double.parseDouble(st.nextToken()));
			//high - get(2)
			data.add(Double.parseDouble(st.nextToken()));
			//low - get(3)
			data.add(Double.parseDouble(st.nextToken()));
			//close - get(4)
			data.add(Double.parseDouble(st.nextToken()));
			//volume - get(5)
			data.add(Integer.parseInt(st.nextToken()));
			//adjusted close - get(6)
			data.add(Double.parseDouble(st.nextToken()));
			
		}
		
		return data;
	}
	
	//input dates in YYYYMMDD format
	public static ArrayList<ArrayList<Object>> extendedHistoricalData(String symbol, String startDate, String endDate) throws IOException {
		
		//months - 1, day, year
		String year = startDate.substring(0, 4); 
		String month = (String) Integer.toString((Integer.parseInt(startDate.substring(4, 6)) - 1));
		String day = startDate.substring(6, 8);
		String year2 = endDate.substring(0, 4); 
		String month2 = (String) Integer.toString((Integer.parseInt(endDate.substring(4, 6)) - 1));
		String day2 = endDate.substring(6, 8);
		String address = "http://ichart.yahoo.com/table.csv?s=" + symbol.toUpperCase() + "&a=" + month + "&b=" + day + "&c=" + year + "&d=" + month2 + "&e=" + day2 + "&f=" + year2 + "&g=d&ignore=.csv";
		
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
		} catch (FileNotFoundException fnfe) {
			dr = new InputStreamReader(connection.getInputStream());
		}
		BufferedReader br = new BufferedReader(dr);
		
		
		StringTokenizer st;
		String s;
		
		// read title - Date,Open,High,Low,Close,Volume,Adj Close
		br.readLine();
		
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		
		while ((s = br.readLine()) != null) {
			st = new java.util.StringTokenizer(s, ",");
			ArrayList<Object> lineData = new ArrayList<Object>();
			//date - get(0)
			lineData.add((String) st.nextToken());
			//open - get(1)
			lineData.add(Double.parseDouble(st.nextToken()));
			//high - get(2)
			lineData.add(Double.parseDouble(st.nextToken()));
			//low - get(3)
			lineData.add(Double.parseDouble(st.nextToken()));
			//close - get(4)
			lineData.add(Double.parseDouble(st.nextToken()));
			//volume - get(5)
			lineData.add(Integer.parseInt(st.nextToken()));
			//adjusted close - get(6)
			lineData.add(Double.parseDouble(st.nextToken()));
			data.add(lineData);
			
		}
		
		return data;
	}
}
