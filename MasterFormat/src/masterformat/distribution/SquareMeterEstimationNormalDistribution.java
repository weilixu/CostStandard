package masterformat.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * This normal distribution generator rejects negative values
 * @author Weili
 *
 */
public class SquareMeterEstimationNormalDistribution extends NormalDistribution{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SquareMeterEstimationNormalDistribution(double m, double std){
   	super(m,std);

       }
       
       public double squareMeterEstimationSample(){
   	double rnd = sample();
   	while(rnd<=0){
   	    rnd = sample();
   	}
   	return rnd;
       }
       
       /**
        * generate a number of samples
        * @param num
        * @return
        */
       public double[] squareMeterEstimationSample(int num){
	   double[] samples = new double[num];
	   for(int i=0;i<num;i++){
	      samples[i]=squareMeterEstimationSample(); 
	   }
	   return samples;
       }
}
