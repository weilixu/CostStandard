package masterformat.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * This is a subclass of normal distribution class.
 * The class performs a truncated normal distribution
 * @author weilix
 *
 */
public class TruncatedNormalDistribution extends NormalDistribution{
    
    private double lower;
    private double higher;
    
    public TruncatedNormalDistribution(double m, double std, double l, double h){
	super(m,std);
	lower = l;
	higher = h;
    }
    
    public double truncatedSample(){
	double rnd = sample();
	while(rnd<lower||rnd>higher){
	    rnd = sample();
	}
	return rnd;
    }
}
