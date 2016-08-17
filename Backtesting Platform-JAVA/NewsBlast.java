import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*; 

import javax.net.ssl.HttpsURLConnection;

public class NewsBlast {
	public static void main(String args[]) throws InterruptedException, IOException{
		
		
		
		getLaw360(1);
	}
	
	public static ArrayList<String> getLaw360(int daysBefore) throws InterruptedException, IOException{
		
		
		//With this you login and a session is created
	    Connection.Response res = Jsoup.connect("https://www.law360.com/account/login?return_url=http%3A%2F%2Fwww.law360.com%2F")
	        .data("email", "aprice@32advisors.com", "password", "basebill")
	        .method(Method.POST)
	        .execute();
	    Document doc = res.parse();
	    String sessionId = res.cookie("SESSIONID"); 
	
		ArrayList<String> linkList = new ArrayList<String>();
		try{
			//doc = Jsoup.connect("http://www.law360.com/").cookies(loginCookies).get();
			
			Document doc2 = Jsoup.connect("http://www.law360.com/").cookie("SESSIONID", sessionId).get();
			
	        System.out.println(doc.text());
	        //Elements links = doc.select("div[class=resultDisplayUrl]");
//	        Elements links = doc.select("a[class=resultTitle]");
//	        for (Element link : links) {
////	            Elements titles = link.select("h3[class=r]");
////	            String title = titles.text();
////
////	            Elements bodies = link.select("span[class=st]");
////	            String body = bodies.text();
////	            
////	            System.out.println("Title: " + title);
////	            System.out.println("Body: " + body + "\n");
//	        	String stringURL = link.toString();
//	        	stringURL = stringURL.substring(stringURL.indexOf("ru="));
//	        	stringURL = stringURL.split("&amp", 2)[0].replaceAll("ru=", "");
//	        	stringURL = java.net.URLDecoder.decode(stringURL, "UTF-8");
//	        	linkList.add(stringURL);
//	        	//System.out.println(stringURL);
//	        }
//	        Thread.sleep((int) Math.random()*2000);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
		return linkList;
	}
	
	private String GetPageContent(String url) throws Exception {
		 
		URL obj = new URL(url);
		URLConnection conn = (HttpsURLConnection) obj.openConnection();
	 
		// default is GET
		conn.setRequestMethod("GET");
	 
		conn.setUseCaches(false);
	 
		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
	 
		BufferedReader in = 
	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	 
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));
	 
		return response.toString();
	 
	  }
}
