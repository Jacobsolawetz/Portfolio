import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Zacks {
	
	//returns the earnings calendar data for the given date in "YYYYMMDD" format
	public static ArrayList<ArrayList<String>> scrapeEarningsData(String stringDate) throws IOException {
		
		String url = "http://biz.yahoo.com/research/earncal/" + stringDate + ".html";
		
		//creates the first dimension of the arrayList
		ArrayList<ArrayList<String>> earningsData = new ArrayList<ArrayList<String>>();
		
		try{
			Document doc = Jsoup.connect(url).get();
			Element script = doc.select("table").get(6);
			Elements tds = script.getElementsByTag("tr");
			int ntmAmount = 0;
			for (@SuppressWarnings("unused") Element div : tds) {
			  ntmAmount++;
			}
			
			for(int i = 1; i < ntmAmount - 1; i++){
				try{
					Element script2 = script.select("tr").get(i);
					Element name = script2.select("td").get(0);
					Element symbol = script2.select("td").get(1).select("a").get(0);
					Element epsEstimate = script2.select("td").get(2);
					Element time = script2.select("td").get(3).select("small").get(0);
					
					//does not record data if the EPS is "N/A"
					if(epsEstimate.html().equals("N/A")) {
						continue;
					} else if(time.html().equals("Time Not Supplied")){
						continue;
					}
					
					
					ArrayList<String> individualData = new ArrayList<String>();
					individualData.add(name.html());
					individualData.add(symbol.html());
					individualData.add(epsEstimate.html());
					
					//filter by changing specific time to "Before Market Open" or "After Market Close"
					String temp = time.html().replaceAll(" ET","");
					if(temp.endsWith("pm")){
						temp = "After Market Close";
					} else if(temp.endsWith("am")){
						temp = "Before Market Open";
					} 
					
					individualData.add(temp);
					earningsData.add(individualData);
					
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
				
			}
		} catch (HttpStatusException hse) {
			Document doc = Jsoup.connect(url).get();
			Element script = doc.select("table").get(6);
			Elements tds = script.getElementsByTag("tr");
			int ntmAmount = 0;
			for (@SuppressWarnings("unused") Element div : tds) {
			  ntmAmount++;
			}
			
			for(int i = 1; i < ntmAmount - 1; i++){
				try{
					Element script2 = script.select("tr").get(i);
					Element name = script2.select("td").get(0);
					Element symbol = script2.select("td").get(1).select("a").get(0);
					Element epsEstimate = script2.select("td").get(2);
					Element time = script2.select("td").get(3).select("small").get(0);
					
					//does not record data if the EPS is "N/A"
					if(epsEstimate.html().equals("N/A")) {
						continue;
					} else if(time.html().equals("Time Not Supplied")){
						continue;
					}
					
					ArrayList<String> individualData = new ArrayList<String>();
					individualData.add(name.html());
					individualData.add(symbol.html());
					individualData.add(epsEstimate.html());
					//filter by changing specific time to "Before Market Open" or "After Market Close"
					String temp = time.html().replaceAll(" ET","");
					if(temp.endsWith("pm")){
						temp = "After Market Close";
					} else if(temp.endsWith("am")){
						temp = "Before Market Open";
					} 
					individualData.add(temp);
					earningsData.add(individualData);	
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
				
			}
			
		}
		return earningsData;	
	} 
	
	
	//get(0) returns P - 1, get(1) returns P - 2, get(2) returns P - 3
	public static ArrayList<Double> scrapePrevEarningsData(String symbol) throws IOException {
	
		ArrayList<Double> earningsData = new ArrayList<Double>();
		
		try {
		
		String url = "http://www.zacks.com/stock/research/" + symbol.toUpperCase() + "/earnings-announcements";
		
		Document doc = Jsoup.connect(url).timeout(0).get();
		Element script = doc.select("script").get(18); //sometimes 19
		
		//Element zachsRank = doc.select("p").get(8); 
		//If you ever wanna use Zack's Rank, here it is!
		//@SuppressWarnings("unused")
		//String zr = zachsRank.html().substring(13,14);
	    
		String stuff = script.html();
		//below is for error checking
		//System.out.println(stuff);
	    
	    int pi = stuff.indexOf(": [  {");
	    
	    String result = stuff.substring(pi+6);
	    
	    //System.out.println(result);
	    
		String delims = "[,]";
		String[] tokens = result.split(delims);
		
		for(int g = 0; g < tokens.length; g++) {
			tokens[g] = tokens[g].replace("\"","");
			tokens[g] = tokens[g].replace(" { ","");
			tokens[g] = tokens[g].replace(" }","");
		}
		int outputCounter = 0;
		
		//gets current month and puts it in 3, 6, 9, 12
//		Calendar cal = Calendar.getInstance();
//		Date currentDate = cal.getTime();
//		DateUtil tradingDayGenerator = new DateUtil();
//		DateFormat df = new SimpleDateFormat("MM");
//		Integer currentMonthDate  = Integer.parseInt(df.format(currentDate));
//		MonthCycler mc = new MonthCycler(currentMonthDate);
//		MonthCycler2 mc2 = new MonthCycler2(currentMonthDate);
//		
//		
//		for(int count = 0; count < tokens.length; count++) {
//			if(tokens[count].contains("Date")) {
//				Integer monthDate = Integer.parseInt(tokens[count+1].substring(17).split("/")[0]);
//				if(mc.getOneBefore() == monthDate || mc.getTwoBefore() == monthDate || mc.getThreeBefore() == monthDate){
//					if(tokens[count+3].substring(14,15).equals("-")) {
//						earningsData.add(null);
//					} else if(tokens[count+3].substring(14,15).equals("$")) {
//						earningsData.add(Double.parseDouble(tokens[count+3].substring(15)));
//					}  else {
//						earningsData.add(Double.parseDouble(tokens[count+3].substring(14)));
//					}
//				} else if(mc2.getOneBefore() == monthDate || mc2.getTwoBefore() == monthDate || mc2.getThreeBefore() == monthDate){
//					if(tokens[count+3].substring(14,15).equals("-")) {
//						earningsData.add(null);
//					} else if(tokens[count+3].substring(14,15).equals("$")) {
//						earningsData.add(Double.parseDouble(tokens[count+3].substring(15)));
//					}  else {
//						earningsData.add(Double.parseDouble(tokens[count+3].substring(14)));
//					}
//				}
//				outputCounter++;
//				//stops after X number of prints
//				if(outputCounter >= 4){
//					break;
//				}
//			}
//		}	
		int count2 = 0;
		for(int count = 0; count < tokens.length; count++) {
			if(tokens[count].contains("Date")) {
				if(count2 == 1 || count2 == 2 || count2 == 3){
					if(tokens[count+3].substring(14,15).equals("-")) {
						earningsData.add(null);
					} else if(tokens[count+3].substring(14,15).equals("$")) {
						earningsData.add(Double.parseDouble(tokens[count+3].substring(15)));
					}  else {
						earningsData.add(Double.parseDouble(tokens[count+3].substring(14)));
					}
				}
				count2++;
			}
		}
		
		} catch(HttpStatusException hse){
			System.out.println("HTTP Error");
		}
		
		return earningsData;
	}
	
	//returns an ArrayList of ArrayList<Object> (size of 3) -> Date object, consensus estimate, and reported w/ null value for missing data
	public static ArrayList<ArrayList<Object>> scrapeAllHistoricalEarningsData(String symbol) throws IOException {
	
		ArrayList<ArrayList<Object>> earningsData = new ArrayList<ArrayList<Object>>();
		
		try {
		
		String url = "http://www.zacks.com/stock/research/" + symbol.toUpperCase() + "/earnings-announcements";
		
		Document doc = Jsoup.connect(url).timeout(0).get();
		Element script = doc.select("script").get(18);
		
		//Element zachsRank = doc.select("p").get(8); 
		//If you ever wanna use Zack's Rank, here it is!
		//@SuppressWarnings("unused")
		//String zr = zachsRank.html().substring(13,14);
	    
		String stuff = script.html();
		//below is for error checking
		//System.out.println(stuff);
	    
	    int pi = stuff.indexOf(": [  {");
	    
	    String result = stuff.substring(pi+6);
	    
		String delims = "[,]";
		String[] tokens = result.split(delims);
		
		for(int g = 0; g < tokens.length; g++) {
			tokens[g] = tokens[g].replace("\"","");
			tokens[g] = tokens[g].replace(" { ","");
			tokens[g] = tokens[g].replace(" }","");
		}
		
		
		for(int count = 0; count < tokens.length; count++) {
			if(tokens[count].contains("Date")) {
				ArrayList<Object> tempData= new ArrayList<Object>();
				
				//adds date
				tempData.add(tokens[count].substring(9));
				System.out.print(tokens[count].substring(9) + ", ");
				
				//adds analyst consensus if available else null
				if(tokens[count+2].substring(14,15).equals("-")) {
					earningsData.add(null);
					System.out.print("null, ");
				} else if(tokens[count+2].substring(14,15).equals("$")) {
					tempData.add(Double.parseDouble(tokens[count+2].substring(15)));
					System.out.print(tokens[count+2].substring(15) + ", ");
				}  else {
					tempData.add(Double.parseDouble(tokens[count+2].substring(14)));
					System.out.print(tokens[count+2].substring(14) + ", ");
				}
				
				//adds reported EPS if available else null
				if(tokens[count+3].substring(14,15).equals("-")) {
					tempData.add(null);
					System.out.print("null");
				} else if(tokens[count+3].substring(14,15).equals("$")) {
					tempData.add(Double.parseDouble(tokens[count+3].substring(15)));
					System.out.print(tokens[count+3].substring(15));
				}  else {
					tempData.add(Double.parseDouble(tokens[count+3].substring(14)));
					System.out.print(tokens[count+3].substring(14));
				}
				System.out.println();
				earningsData.add(tempData);
				
			}
		}	
		
		} catch(HttpStatusException hse){
			System.out.println("HTTP Error");
		}
		
		return earningsData;
	}
	
	
}
