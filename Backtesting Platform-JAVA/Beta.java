import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Beta {
	
	public double calculate(ArrayList<Double> stockReturns, ArrayList<Double> spyReturns) {
		
		DescriptiveStatistics stats2 = new DescriptiveStatistics();
		for (double item : stockReturns) {
			stats2.addValue(item);
		}

		DescriptiveStatistics stats1 = new DescriptiveStatistics();
		for (double item1 : spyReturns) {
			stats1.addValue(item1);
		}

		double var = stats1.getVariance();

		// now we need to get the covariance. There are APIs for this but
		// they're too annoying.

		double covar;

		double EX = stats1.getMean();

		double EY = stats2.getMean();

		double sum = 0;

		
		int b = stockReturns.size();

		for (int a = 0; a < b; a++) {

			sum += (spyReturns.get(a) - EX) * (stockReturns.get(a) - EY);

		}

		double denom = stockReturns.size() - 1;

		covar = sum / (denom);

		double beta = covar / var;

		return beta;
		
	}

}
