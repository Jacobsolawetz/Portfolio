public class MonthCycler2 {
	
	private int month;
	private int current;
	private int oneBefore;
	private int twoBefore;
	private int threeBefore;
	
	public MonthCycler2(int month){
		this.month = month;
		decrementMonth();
		decrementMonth();
		decrementMonth();
		int minus3 = getMonth();
		if(minus3 == 12 || minus3 == 11 || minus3 == 10){
			current = 1;
			oneBefore = 10;
			twoBefore = 7;
			threeBefore = 4;
		} else if(minus3 == 9 || minus3 == 8 || minus3 == 7){
			current = 10;
			oneBefore = 7;
			twoBefore = 4;
			threeBefore = 1;
		} else if(minus3 == 6 || minus3 == 5 || minus3 == 4){
			current = 7;
			oneBefore = 4;
			twoBefore = 1;
			threeBefore = 10;
		} else {
			current = 4;
			oneBefore = 1;
			twoBefore = 10;
			threeBefore = 7;
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