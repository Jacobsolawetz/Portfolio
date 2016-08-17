import java.util.HashSet;

public class NewsArticle {
	private static String headline;
	private static String summary;
	private static String source;
	private static String link;
	
	public NewsArticle(String headline, String source, String link){
		this.source = source.trim();
		this.headline = headline.trim();
		this.summary = summary.trim();
		this.link = link.trim();
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