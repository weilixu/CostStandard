package masterformat.squarefoot;

/**
 * based on the data provided from RSMeans, the assumptions for the module is
 * the distribution is Normal distribution. The interquartiles are used in this
 * class to generate variance, which is 1.35 standard deviation.
 * 
 * @author Weili
 *
 */
public final class DistParametersGenerator {
    
    public static Double[] normalDistParameter(double lowerInterQuartile, double higherInterQuartile, double median){
	double variance = (higherInterQuartile-lowerInterQuartile)/1.35;
	Double[] parameter = {median, variance};
	return parameter;
    }
    
}
