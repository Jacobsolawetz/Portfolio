import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*; 

public class ThirtyTwo_Advisors {
	public int tits;
	public String fuckWolf;
	public String footballIsForPussies;
	
	public static void main(String args[]) throws InterruptedException, UnsupportedMimeTypeException{
		Document doc;
		//System.out.println(webCrawlerLinks("Newton").get(0));
		
		HashSet<String> names = getAllNames();
		String company = "32 Advisors";
		ArrayList<String> comprehensiveSearchStrings = generatePeopleSearches(company);

		HashSet<String> companyLinks = new HashSet<String>();
		for(String search : comprehensiveSearchStrings){
			int numLinkVisits = 0;
			for(String url : webCrawlerLinks(search)){
				if(numLinkVisits < 3){
					companyLinks.add(url);
				} else {
					continue;
				}
				numLinkVisits++;
			}
		}
		
		//System.out.println("checkpoint");
		
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		ArrayList<String> visitedLinks = new ArrayList<String>();
		for(String link : companyLinks){
			try{
				ArrayList<Contact> people = getNames(link, names, company);
				for(Contact person : people){
					person.print();
				}
				//contacts.addAll(getNames(link, names, company));	
			} catch(UnsupportedMimeTypeException umte){
				//e.printStackTrace();
				continue;
			}
			visitedLinks.add(link);
		}
		//print visited links
		for(String l : visitedLinks){
			System.out.println(l);
		}
		
		System.out.println("Woop");
		for(Contact contact : contacts){
			contact.print();
		}
		
		//filter to remove duplicates
//		Iterator<Contact> iterator = contacts.iterator();
//		ArrayList<String> namesInList = new ArrayList<String>();
//		while (iterator.hasNext()) {
//		    Contact contact = iterator.next();
//		    if(namesInList.contains(contact.getName())){ //removes duplicate
//		        iterator.remove();
//		        //below remove contact if it name contains a number 
//		    } else if(contact.getName().contains("1") || contact.getName().contains("2") || contact.getName().contains("3") || contact.getName().contains("4") || contact.getName().contains("5") || contact.getName().contains("6") || contact.getName().contains("7") || contact.getName().contains("8") || contact.getName().contains("9")) {
//		    	iterator.remove();
//		    } else if(webCrawlerLinks(contact.getName()).get(0).contains("wikipedia")){ //if first search result is a wikipedia page, probs a location
//		    	iterator.remove();
//		    } else{
//		    	boolean exists = false;
//		    	try{
//		    		exists = webCrawlerLinks(contact.getName()).get(0).contains("wikipedia");
//		    	} catch (Exception e){
//		    		namesInList.add(contact.getName());
//			    	contact.getPersonalInfo();
//			    	
//			    	
//			    	contact.print(); //print
//					System.out.println();
//		    	}
//		    	if(exists){
//		    		iterator.remove();
//		    	} else{
//		    		namesInList.add(contact.getName());
//			    	//contact.getPersonalInfo();
//			    	
//			    	
//			    	contact.print(); //print
//					System.out.println();
//		    	}
//		    }
//		}
		
		
		
		//System.out.println(getNames("http://www.3i.com/our-people/executive-committee", names));
		//System.out.println(getNames("http://www.abraaj.com/about-us/our-people/",  names));
		//System.out.println(getNames("http://www.altius-associates.com/Our-Team.htm", names));
		
	    
	}
	
	public static String getStringRepresentation(ArrayList<Character> list) {    
	    StringBuilder builder = new StringBuilder(list.size());
	    for(Character ch: list)
	    {
	        builder.append(ch);
	    }
	    return builder.toString();
	}
	
	public static ArrayList<String> webCrawlerLinks(String searchString) throws InterruptedException{
		Document doc;
		ArrayList<String> linkList = new ArrayList<String>();
		try{
	        doc = Jsoup.connect("https://www.webcrawler.com/search/web?fcoid=417&fcop=topnav&fpid=27&aid=d9d06eac-8aa2-410b-abde-21c42f4d9ef3&ridx=1&q=" + plusify(searchString) + "&ql=&ss=t").get();
	        
	        //System.out.println(doc.text());
	        //Elements links = doc.select("div[class=resultDisplayUrl]");
	        Elements links = doc.select("a[class=resultTitle]");
	        for (Element link : links) {
//	            Elements titles = link.select("h3[class=r]");
//	            String title = titles.text();
//
//	            Elements bodies = link.select("span[class=st]");
//	            String body = bodies.text();
//	            
//	            System.out.println("Title: " + title);
//	            System.out.println("Body: " + body + "\n");
	        	String stringURL = link.toString();
	        	stringURL = stringURL.substring(stringURL.indexOf("ru="));
	        	stringURL = stringURL.split("&amp", 2)[0].replaceAll("ru=", "");
	        	stringURL = java.net.URLDecoder.decode(stringURL, "UTF-8");
	        	linkList.add(stringURL);
	        	//System.out.println(stringURL);
	        }
	        Thread.sleep((int) Math.random()*2000);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
		return linkList;
	}
	
	public static ArrayList<String> googleLinks(String searchString){
		Document doc;
		ArrayList<String> linkList = new ArrayList<String>();
		try{
	        doc = Jsoup.connect("http://google.com/search?q=" + plusify(searchString)).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
	        
	        
	        
	        System.out.println(doc.text());
	        Elements links = doc.select("li[class=g]");
	        for (Element link : links) {
	            Elements titles = link.select("h3[class=r]");
	            String title = titles.text();

	            Elements bodies = link.select("span[class=st]");
	            String body = bodies.text();
	            
	            System.out.println("Title: " + title);
	            System.out.println("Body: " + body + "\n");
	        }
	        
	        
	        
	        
	        
            Elements titles = doc.select("h3.r > a");
            for(Element e: titles){
                String info = e.attr("href");
                if(info.contains("maps")){
                	continue;
                }
                int endIndex = info.indexOf("&sa");
                info = info.substring(0, endIndex);
                ArrayList<Character> newInfo = new ArrayList<Character>();
                int slashCount = 0;
                for(char charachter : info.toCharArray()){
                	if(charachter == '='){
                		slashCount++;
                	}
                	if(slashCount >= 1){
                		newInfo.add(charachter);
                	}
                }
                
                String link = getStringRepresentation(newInfo).substring(1);
                linkList.add(link);
            }
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
		return linkList;
	}
	
	public static ArrayList<Contact> getNames(String url, HashSet<String> names, String company) throws UnsupportedMimeTypeException{
		Document doc;
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		int nameCount = 0;
		try{
	        doc = Jsoup.connect(url).ignoreHttpErrors(true).timeout(0).get();
	        //content = doc.text();
	        boolean skipNext = false;
	        Elements docElements = doc.select("div");
	        int index = 0;
	        for(Element divElement : docElements){
	        	if(skipNext){
	        		index++;
	        		skipNext = false;
	        		continue;
	        	}
	        	
	        	if((spaceLength(divElement.text(), 2) || spaceLength(divElement.text(), 3)) && allCaps(divElement.text())){
	        		//now check Set to reduce latency
	        		String firstWord = divElement.text().split(" ", 2)[0];
	        		if(names.contains(firstWord)){
	        			nameCount++;
	        			contacts.add(new Contact(divElement.text(), docElements.get(index + 1).text(), company));
		        		System.out.println(divElement.text());
		        		skipNext = true;
	        		}
	        	}
	        	
	        	index++;
	   
	        }
	        //if nameCount is too low, keep on searching and try a new html element 
        	if(nameCount<= 2){
        		skipNext = false;
 		        docElements = doc.select("p");
 		        index = 0;
 		        for(Element pElement : docElements){
 		        	if(skipNext){
 		        		index++;
 		        		skipNext = false;
 		        		continue;
 		        	}
 		        	
 		        	String firstWord = pElement.text().split(" ", 2)[0];
 		        	if((spaceLength(pElement.text(), 2) || spaceLength(pElement.text(), 3)) && allCaps(pElement.text())){
 		        		//now check Set to reduce latency
 		        		if(names.contains(firstWord)){
 		        			nameCount++;
 		        			//String text = pElement.text();
 		        			//String[] parts = text.split('\u00A0');
 		        			contacts.add(new Contact(pElement.text(), docElements.get(index + 1).text(), company));
 		        			
 			        		System.out.println(pElement.text());
 			        		skipNext = true;
 		        		}
 		        	} else if(spaceLength(pElement.text()) > 3 && names.contains(firstWord) && allCaps(pElement.text())){
 		        		boolean oddWhitespaceFound = false;
 		        		int charIndex = 0;
 		        		for(Character character : pElement.text().toCharArray()){
 		        			charIndex++;
 		        			if(character == '\u00A0' || character == '\u2007' || character == '\u202F'){
 		        				oddWhitespaceFound = true;
 		        				
 		        				break;
 		        			}
 		        		}
 		        		if(oddWhitespaceFound){
 		        			String text = pElement.text().replace('\u00A0', '\u0000');
 		        			text = pElement.text().replace('\u2007', '\u0000');
 		        			text = pElement.text().replace('\u202F', '\u0000');
 		        			
 		        			
 		        			String ridOfOddChars = text.substring(charIndex);
 		        			if(ridOfOddChars.split(" ", 2)[0].length() < 3){
 		        				contacts.add(new Contact(text.substring(0, charIndex), ridOfOddChars.split(" ", 2)[1], company));
 		        			} else {
 		        				contacts.add(new Contact(text.substring(0, charIndex), text.substring(charIndex), company));
 		        			}
 		        			System.out.println(text.substring(0, charIndex));
 		        			
 		        		}
 		        	}
 		        	
 		        	index++;
 		        }
        	}
	  
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		for(Contact contact : contacts){
			contact.print();
		}
		
		return contacts;
	}
	
	public static String plusify(String spacedString){
		ArrayList<Character> plussedString = new ArrayList<Character>();
		for(char charachter : spacedString.toCharArray()){
			if(charachter != ' '){
				plussedString.add(charachter);
			} else{
				plussedString.add('+');
			}
		}
		return getStringRepresentation(plussedString);
	}
	
	public static boolean spaceLength(String sizeString, int desiredLength){
		int wordSize = 1;
		
		sizeString = sizeString.trim();
		sizeString = sizeString.replace('\u00A0',' ');
		sizeString = sizeString.replace('\u2007',' ');
		sizeString = sizeString.replace('\u202F',' ');
		for(Character character : sizeString.toCharArray()){
			if(Character.isWhitespace(character)){
				wordSize++;
			}
		}
		if(wordSize == desiredLength){
			return true;
		} else{
			return false;
		}
	}
	
	public static int spaceLength(String sizeString){
		int wordSize = 1;
		
		sizeString = sizeString.trim();
		sizeString = sizeString.replace('\u00A0',' ');
		sizeString = sizeString.replace('\u2007',' ');
		sizeString = sizeString.replace('\u202F',' ');
		for(Character character : sizeString.toCharArray()){
			if(Character.isWhitespace(character)){
				wordSize++;
			}
		}
		return wordSize;
	}
	
	
	public static boolean allCaps(String string){
		boolean startSearch = false; 
		boolean allCaps = true;
		string = string.trim();
		for(Character character : string.toCharArray()){
			if(character == ' '){
				startSearch = true;
			} else if(startSearch){
				if(Character.isLowerCase(character)){
					allCaps = false;
				}
				startSearch = false;
			}
		}
		return allCaps;
	}
	
	public static HashSet<String> getAllNames(){
		String line = "";
		BufferedReader br = null;
		HashSet<String> nameList = new HashSet<String>();
		final File folder = new File("/Users/14price/Documents/names");
		for(File file : listFilesForFolder(folder)){
			try {
				br = new BufferedReader(new FileReader(file));
				String lastNameAdded = "";
				while ((line = br.readLine()) != null) {
				    // use comma as separator
					String[] data = line.split(",");
					try{
						String name = data[0];
						if(Integer.parseInt((String) data[2]) > 20) { //frequency has to be greater than 20 for any given year
							nameList.add(name);
							lastNameAdded = name;
						}
					} catch(ArrayIndexOutOfBoundsException aiooebe){
						//System.out.println(file.toString());
						//System.out.println(lastNameAdded);
					}
					
					
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
		}
		return nameList;
	}
	
	public static ArrayList<File> listFilesForFolder(final File folder) {
		ArrayList<File> links = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	links.add(fileEntry);
	        }
	    }
		return links;
	}
	
	public static ArrayList<String> generatePeopleSearches(String baseSearch){
		ArrayList<String> betterSearches = new ArrayList<String>();
		betterSearches.add(baseSearch);
		betterSearches.add(baseSearch + " people");
		betterSearches.add(baseSearch + " fund manager");
		betterSearches.add(baseSearch + " management");
		betterSearches.add(baseSearch + " contact");
		betterSearches.add(baseSearch + " team");
		betterSearches.add(baseSearch + " managers");
		betterSearches.add(baseSearch + " directors");
		betterSearches.add(baseSearch + " governance");
		betterSearches.add(baseSearch + " management");
		betterSearches.add(baseSearch + " leadership");
		return betterSearches;
	}
	
	
}
