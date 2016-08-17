/**
 * The Trigger class is a data structure used to hold triggers before they become Trades (actually executed)
 * Some Triggers may become Trades; however, some may not actually be executed
 */

import java.util.Date;


public class Trigger {

	private Date dayTriggered;
	private int algoNumber;
	private String indication;
	private String symbol;
	private int duration;
	
	public Trigger(Date dayTriggered, int algoNumber, String indication, String symbol, int y1){
		this.dayTriggered = dayTriggered;
		this.algoNumber = algoNumber;
		this.indication = indication;
		this.symbol = symbol;
		this.duration = y1;
	}
	
	public Date getDate(){
		return dayTriggered;
	}
	public int getAlgoNumber(){
		return algoNumber;
	}
	public String getIndication(){
		return indication;
	}
	public String getSymbol(){
		return symbol;
	}
	public int getDuration(){
		return duration;
	}
}
