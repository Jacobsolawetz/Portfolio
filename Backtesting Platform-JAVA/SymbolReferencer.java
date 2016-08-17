import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;


public class SymbolReferencer {
	private HashMap<String, ArrayList<String>> symbolInfo;
	
	
	public SymbolReferencer() throws IOException, ParseException{
		symbolInfo = readSymbolInfo();
	}
	
	public Symbol getSymbol(String ticker) throws IOException{
		if(symbolInfo.containsKey(ticker)){
			ArrayList<String> gicsMatchSymbols = new ArrayList<String>();
			
			//find all other symbols with the same gics code
			for(String key : symbolInfo.keySet()){
				if(symbolInfo.get(key).get(1).equals(symbolInfo.get(ticker).get(1))){
					gicsMatchSymbols.add(key);
				}
			}
			
			return new Symbol(ticker, symbolInfo.get(ticker).get(0), symbolInfo.get(ticker).get(1), symbolInfo.get(ticker).get(2), symbolInfo.get(ticker).get(3), gicsMatchSymbols);
		} else {
			throw new IOException("No symbol data for " + ticker);
		}
	}
	
	public static HashMap<String, ArrayList<String>> readSymbolInfo() throws IOException, ParseException{
		HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();
		String filePath = "/Users/14price/Documents/GICSReadInFile.csv";
		
		String symbol = "";
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try{
			String line = br.readLine(); //skips the header line
			while(line != null){
				StringTokenizer tokenizer = new java.util.StringTokenizer(line, ",");
				//read symbol
				symbol = new String(tokenizer.nextToken().trim());
				//read full company name
				String fullCompanyName = new String(tokenizer.nextToken().trim());
				//read GICS code
				String gicsCode = new String(tokenizer.nextToken().trim());
				//read sector ETF
				String sectorETF = new String(tokenizer.nextToken().trim());
				//read industry ETF
				String industryETF = new String(tokenizer.nextToken().trim());
				
				ArrayList<String> currentLine = new ArrayList<String>();
				currentLine.add(fullCompanyName);
				currentLine.add(gicsCode);
				currentLine.add(sectorETF);
				currentLine.add(industryETF);
				
				returnMap.put(symbol, currentLine);
				
				//move to next line
				line = br.readLine();
			}
		} catch (OutOfMemoryError oome){
			System.out.println(symbol);
		} finally {
			br.close();
			
		}
		return returnMap;
	}
}
