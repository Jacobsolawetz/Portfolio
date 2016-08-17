import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Contact {
	private static String name;
	private static String position;
	private static String company;
	private static HashSet<String> number;
	private static HashSet<String> personalEmail;
	private static HashSet<String>  companyEmail;
	
	public Contact(String name, String company, String position){
		this.company = company.trim();
		this.name = name.trim();
		this.position = position.trim();
		number = new HashSet<String>();
		personalEmail = new HashSet<String>();
		companyEmail = new HashSet<String>();
		
	}
	
	public static void setNumber(HashSet<String> numberSet){
		number = numberSet;
	}
	
	public static String getName(){
		return name;
	}
	
	public static String getPosition(){
		return position;
	}
	
	public static String getCompany(){
		return company;
	}
	
	public static HashSet<String> getPersonalEmail(){
		return personalEmail;
	}
	
	public static HashSet<String> getCompanyEmail(){
		return companyEmail;
	}
	
	public static void setPersonalEmail(HashSet<String> emails){
		personalEmail = emails;
	}
	
	public static void setCompanyEmail(HashSet<String> emails){
		companyEmail = emails;
	}
	
	public static HashSet<String> getNumber(){
		return number;
	}
	
	public void print(){
		if(!name.equals("")){
			System.out.println(name);
		}
		if(!company.equals("")){
			System.out.println(company);
		}
		if(!position.equals("")){
			System.out.println(position);
		}
		if(!personalEmail.isEmpty()){
			for(String email : personalEmail){
				System.out.println(email);
			} //only print company email if personal email is unavailable
		} else if(!companyEmail.isEmpty()){
			for(String email : companyEmail){
				System.out.println(companyEmail);
			}
		}
		if(!number.isEmpty()){
			for(String num : number){
				System.out.println(num);
			}
		}
	}
	
	public boolean equals(Object o){
		if(o instanceof Contact){
			if(((Contact) o).getName().equals(this.name)){
				return true;
			}
		}
		return false;
	}
	
	//adds email and phone number to contact
	public static void getPersonalInfo() throws UnsupportedMimeTypeException, InterruptedException{
		Document doc;
		
		String searchString1 = getName() + " " + getCompany();
		String searchString2 = getName() + " " + getCompany() + " contact";
		String searchString3 = getName() + " " + getCompany() + " " + getPosition();
		
		ArrayList<String> possibleEmails = new ArrayList<String>();
		HashSet<String> possibleNumbers = new HashSet<String>();
		
		ArrayList<String> urls = ThirtyTwo_Advisors.webCrawlerLinks(searchString1);
		urls.addAll(ThirtyTwo_Advisors.webCrawlerLinks(searchString2));
		urls.addAll(ThirtyTwo_Advisors.webCrawlerLinks(searchString3));
		
		//removes duplicate urls
		HashSet<String> urlsNoDuplicates = new HashSet<String>();
		for(String url : urls){
			urlsNoDuplicates.add(url);
		}
		
		//filter to get rid of emails that do not end in .com or start with the person's first name
        HashSet<String> filteredPossibleEmails = new HashSet<String>();
        HashSet<String> filteredPossibleCorporateEmails = new HashSet<String>();
		
		for(String url : urlsNoDuplicates){
			try{
				//first look through doc text
		        doc = Jsoup.connect(url).ignoreHttpErrors(true).timeout(0).get();
		        
		        Thread.sleep((int) Math.random()*2000);
		        
		        String content = doc.text();
		      
		        String [] splitContent = content.split(" ");
		        for(String s : splitContent){
		        	if(s.contains("@")){ //if it contains an @ symbol add it to the list of possible emails
		        		possibleEmails.add(s);
		        	}
		        	//see if there is a 9 or 10 digit number in one line
		        	int numberCount = 0;
		        	for(char letter : s.toCharArray()){
		        		if(Character.isDigit(letter)){
		        			numberCount++;
		        		}
		        	}
		        	if(numberCount <= 15 && numberCount >= 9){
		        		possibleNumbers.add(s);
		        	}
		        	
		        }
		        
		        Elements links = doc.select("a[href]");
		        links.addAll(doc.select("[src]"));
		        links.addAll(doc.select("link[href]"));
		        
		   
		     
		        for(Element link : links){
		        	String[] splitLink = link.toString().split(" ");
		        	if(link.toString().contains("@")){
		        		for(String s : splitLink){
		        			if(s.contains("@")){
		        				int index = s.indexOf(":") + 1;
		        				possibleEmails.add(s.substring(index).replaceAll("\"", ""));
		        				
		        			}
		        			
		        		}
		        	}
		        }
		        
		        
		        for(String email : possibleEmails){
		        	if(email.trim().endsWith(".com") && getName().substring(0,1).toLowerCase().equals(email.substring(0,1).toLowerCase())){
		        		filteredPossibleEmails.add(email);
		        		System.out.println(email);
		        	} else if(email.trim().endsWith(".com") && email.split("@")[1].startsWith(getCompany().substring(0,1).toLowerCase())){ //probably a corporate email
		        		filteredPossibleCorporateEmails.add(email);
		        		setPersonalEmail(filteredPossibleEmails);
		        		System.out.println(email);
		        	}
		        }
		        
		  
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
		}

		setPersonalEmail(filteredPossibleEmails);
		setCompanyEmail(filteredPossibleCorporateEmails);
		setNumber(possibleNumbers);
		}
}
