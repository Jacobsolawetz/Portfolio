import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class DateUtil
{
	//adds a trading day taking into account weekends and holidays when the markets are closed
    public static Date addTradingDay(Date date)
    {
        Calendar cal = Calendar.getInstance();
        Calendar holidayCalendar = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1); //minus number would decrement the days
        //handle holidays before weekends
        //New Year's day
        holidayCalendar.set(Calendar.MONTH, 0);
        holidayCalendar.set(Calendar.DATE, 0);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //MLK day
        holidayCalendar.set(Calendar.MONTH, 0);
        holidayCalendar.set(Calendar.DATE, 18);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Washington's Birthday
        holidayCalendar.set(Calendar.MONTH, 1);
        holidayCalendar.set(Calendar.DATE, 15);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Good Friday
        holidayCalendar.set(Calendar.MONTH, 3);
        holidayCalendar.set(Calendar.DATE, 2);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Memorial Day
        holidayCalendar.set(Calendar.MONTH, 4);
        holidayCalendar.set(Calendar.DATE, 24);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //July 3
        holidayCalendar.set(Calendar.MONTH, 6);
        holidayCalendar.set(Calendar.DATE, 2);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //July 4
        holidayCalendar.set(Calendar.MONTH, 6);
        holidayCalendar.set(Calendar.DATE, 3);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Labor Day
        holidayCalendar.set(Calendar.MONTH, 8);
        holidayCalendar.set(Calendar.DATE, 6);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Thanksgiving
        holidayCalendar.set(Calendar.MONTH, 10);
        holidayCalendar.set(Calendar.DATE, 25);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //Christmas
        holidayCalendar.set(Calendar.MONTH, 11);
        holidayCalendar.set(Calendar.DATE, 24);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR)){
        	cal.add(Calendar.DATE, 1);
        }
        //handle weekends
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        	cal.add(Calendar.DATE, 1);
        } else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
        	cal.add(Calendar.DATE, 2);
        }
        
        return cal.getTime(); 
    }
    
  //adds a trading day taking into account weekends and holidays when the markets are closed
    public static Date removeTradingDay(Date date)
    {
        Calendar cal = Calendar.getInstance();
        Calendar holidayCalendar = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1); //minus number would decrement the days
        //handle holidays before weekends
        //New Year's day
        holidayCalendar.set(Calendar.MONTH, 0);
        holidayCalendar.set(Calendar.DATE, 0);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //MLK day
        holidayCalendar.set(Calendar.MONTH, 0);
        holidayCalendar.set(Calendar.DATE, 18);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Washington's Birthday
        holidayCalendar.set(Calendar.MONTH, 1);
        holidayCalendar.set(Calendar.DATE, 15);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Good Friday
        holidayCalendar.set(Calendar.MONTH, 3);
        holidayCalendar.set(Calendar.DATE, 2);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Memorial Day
        holidayCalendar.set(Calendar.MONTH, 4);
        holidayCalendar.set(Calendar.DATE, 24);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //July 3
        holidayCalendar.set(Calendar.MONTH, 6);
        holidayCalendar.set(Calendar.DATE, 2);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //July 4
        holidayCalendar.set(Calendar.MONTH, 6);
        holidayCalendar.set(Calendar.DATE, 3);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Labor Day
        holidayCalendar.set(Calendar.MONTH, 8);
        holidayCalendar.set(Calendar.DATE, 6);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Thanksgiving
        holidayCalendar.set(Calendar.MONTH, 10);
        holidayCalendar.set(Calendar.DATE, 25);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //Christmas
        holidayCalendar.set(Calendar.MONTH, 11);
        holidayCalendar.set(Calendar.DATE, 24);
        holidayCalendar.set(Calendar.YEAR, 2015);
        holidayCalendar.set(Calendar.HOUR,12);
        holidayCalendar.set(Calendar.MINUTE,12);
        holidayCalendar.set(Calendar.SECOND,12);
        if(cal.get(Calendar.DAY_OF_YEAR) == holidayCalendar.get(Calendar.DAY_OF_YEAR) + 1){
        	cal.add(Calendar.DATE, -1);
        }
        //handle weekends
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        	cal.add(Calendar.DATE, -2);
        } else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
        	cal.add(Calendar.DATE, -1);
        }
        
        return cal.getTime(); 
    }
    
    //gets the number of business days from the current day to the next day
    public static int businessDaysFromToday(String endDate, String simpleDateFormat) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		Date myDate = format.parse(endDate);
		Calendar currentDate = Calendar.getInstance();
        Calendar futureDate = Calendar.getInstance();
        futureDate.setTime(myDate);
		int counter = 0;
		if(futureDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || futureDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			System.out.println("End date is on the weekend");
		} else {
			while(currentDate.get(Calendar.DAY_OF_YEAR) != futureDate.get(Calendar.DAY_OF_YEAR)){
				currentDate.setTime(addTradingDay(currentDate.getTime()));
				//System.out.println(currentDate.getTime());
		        //System.out.println(futureDate.getTime());
				counter++;
			}
			//checks whether the current day is a holiday
			if(counter > 364){
				counter = 0;
			}
		}
		
		return counter;
	}
}
