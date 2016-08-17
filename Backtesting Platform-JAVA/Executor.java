import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;


public class Executor {
	
	private static String USER_NAME = "robisonpriceexecutionclient";  // GMail user name (just the part before "@gmail.com")
    private static String PASSWORD = "Stratus124"; // GMail password
    private static String RECIPIENT = "alexanderprice@wustl.edu, krobison@stanford.edu";
    
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		ArrayList<String> tradeList = new ArrayList<String>();
		
		Algorithm_Checker ac; 
		
		for(int i = 1; i <= 10; i++){
			ac = new Algorithm_Checker("SymbolsP" + i + ".txt");
			tradeList.addAll(ac.checkAlgo1());
			tradeList.addAll(ac.checkAlgo2());
			tradeList.addAll(ac.checkAlgo3());
			tradeList.addAll(ac.checkAlgo4());
			tradeList.addAll(ac.checkAlgo5());
			tradeList.addAll(ac.checkAlgo6());
			tradeList.addAll(ac.checkAlgo7());
			tradeList.addAll(ac.checkAlgo8());
			tradeList.addAll(ac.checkAlgo9());
			tradeList.addAll(ac.checkAlgo10());
			tradeList.addAll(ac.checkAlgo11());
			tradeList.addAll(ac.checkAlgo12());
			tradeList.addAll(ac.checkAlgo13());
			tradeList.addAll(ac.checkAlgo14());
			tradeList.addAll(ac.checkAlgo15());
			tradeList.addAll(ac.checkAlgo16());
			//tradeList.addAll(ac.checkAlgo17());
			//tradeList.addAll(ac.checkAlgo18());
			//tradeList.addAll(ac.checkAlgo19());
		}
		
//		
//		//pass in all report stuff and symbol data to an algo parser
//		tradeList.addAll(ac.checkAlgo1());
//		tradeList.addAll(ac.checkAlgo2());
//		tradeList.addAll(ac.checkAlgo3());
//		tradeList.addAll(ac.checkAlgo4());
//		tradeList.addAll(ac.checkAlgo5());
//		tradeList.addAll(ac.checkAlgo6());
//		tradeList.addAll(ac.checkAlgo7());
//		tradeList.addAll(ac.checkAlgo8());
//		tradeList.addAll(ac.checkAlgo9());
//		tradeList.addAll(ac.checkAlgo10());
//		tradeList.addAll(ac.checkAlgo11());
//		tradeList.addAll(ac.checkAlgo12());
//		tradeList.addAll(ac.checkAlgo13());
//		tradeList.addAll(ac.checkAlgo14());
//		tradeList.addAll(ac.checkAlgo15());
//		tradeList.addAll(ac.checkAlgo16());
//		tradeList.addAll(ac.checkAlgo17());
//		tradeList.addAll(ac.checkAlgo18());
//		tradeList.addAll(ac.checkAlgo19());
		
		//gets current date
		Calendar cal = Calendar.getInstance();
		Date currentDate = cal.getTime();
		//DateUtil tradingDayGenerator = new DateUtil();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
		String from = USER_NAME;
        String pass = PASSWORD;
        String[] to = { RECIPIENT }; // list of recipient email addresses
        String subject = "Trades for " + df.format(currentDate);
        String body = "";
        
        int count = 0;
        for(String trade : tradeList){
			System.out.println(trade);
        	body += trade + "\n";
        	count++;
		}
        if(count == 0){
        	body = "No triggers for current trading session";
        	System.out.println(body);
        }
        
        //sendFromGMail(from, pass, to, subject, body);
        
        
	}
	
	private static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("alexanderprice@wustl.edu"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("krobison@stanford.edu"));
            
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            System.out.println();
            System.out.println("Trade email has been sent");	
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }
}
