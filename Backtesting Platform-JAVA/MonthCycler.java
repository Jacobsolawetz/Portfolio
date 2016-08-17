
public class MonthCycler {
	
	private int month;
	private int current;
	private int oneBefore;
	private int twoBefore;
	private int threeBefore;
	
	public MonthCycler(int month){
		this.month = month;
		decrementMonth();
		decrementMonth();
		decrementMonth();
		int minus3 = getMonth();
		if(minus3 == 12 || minus3 == 11 || minus3 == 10){
			current = 12;
			oneBefore = current - 3;
			twoBefore = current - 6;
			threeBefore = current - 9;
		} else if(minus3 == 9 || minus3 == 8 || minus3 == 7){
			current = 9;
			oneBefore = 6;
			twoBefore = 3;
			threeBefore = 12;
		} else if(minus3 == 6 || minus3 == 5 || minus3 == 4){
			current = 6;
			oneBefore = 3;
			twoBefore = 12;
			threeBefore = 9;
		} else {
			current = 3;
			oneBefore = 12;
			twoBefore = 9;
			threeBefore = 6;
		}
	}
	
	public void incrementMonth(){
		if(month < 12){
			month++;
		} else{
			month = 1;
		}
	}
	
	public void decrementMonth(){
		if(month > 1){
			month--;
		} else{
			month = 12;
		}
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getOneBefore(){
		return oneBefore;
	}
	
	public int getTwoBefore(){
		return twoBefore;
	}
	
	public int getThreeBefore(){
		return threeBefore;
	}
	
	public int getCurrent(){
		return current;
	}
	
}
