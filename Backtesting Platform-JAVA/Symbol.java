/**The Symbol class holds final variables that are set by SymbolReference
 * These variables include full company name, sector, sub-sector, GICS code
 */

import java.util.ArrayList;

public class Symbol {

	private final String symbol;
	private final String fullCompanyName;
	private final String sectorETF;
	private final String industryETF;
	private final String gicsCode;
	private final ArrayList<String> similarCompanies;
	
	public Symbol(String symbol, String fullCompanyName, String gicsCode, String sectorETF, String industryETF, ArrayList<String> similarCompanies){
		this.symbol = symbol;
		this.sectorETF = sectorETF;
		this.gicsCode = gicsCode;
		this.industryETF = industryETF;
		this.fullCompanyName = fullCompanyName;
		this.similarCompanies = similarCompanies;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public String getFullCompanyName(){
		return fullCompanyName;
	}
	
	//if there is no GICS code, sector ETF is SPY
	public String getSectorETF(){
		return sectorETF;
	}
	
	//if there is no GICS code, sector ETF is SPY
	public String getIndustryETF(){
		return industryETF;
	}
	
	public String getGICS(){
		return gicsCode;
	}
	
	public ArrayList<String> getSimilarCompanies(){
		return similarCompanies;
	}
}
