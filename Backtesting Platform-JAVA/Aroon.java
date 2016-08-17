import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;


public class Aroon {	
	//most recent data at the top
	public static int getAroon(int duration, ArrayList<Double> closes) throws IOException{
		double maxValue = 0.00;
		double minValue = 100000000.00;
		int count = 0;
		int indexMin = -1;
		int indexMax = -1;
		
		for(int i = 0; i < duration; i++){
			if(closes.get(i) < minValue){
				minValue = closes.get(i);
				indexMin = count;
			} 
			if(closes.get(i) > maxValue){
				maxValue = closes.get(i);
				indexMax = count;
			}
			count++;
		}
		int aroonUp = 100*(duration - indexMax)/duration;
		int aroonDown = 100*(duration - indexMin)/duration;
		int aroonOscillator = aroonUp - aroonDown;
		return aroonOscillator;	
	}
}
