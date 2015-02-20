package masterformat.standard.model;

import java.util.Arrays;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class CostMultiRegressionModel extends OLSMultipleLinearRegression{
    
    public CostMultiRegressionModel(){
	super();
    }
    
    public Double[] generateCostResults(Double d){
	double[] beta = this.estimateRegressionParameters();
	double[] residual = this.estimateResiduals();
	Double[] costVector = new Double[beta.length];
	for(int i=0; i<beta.length; i++){
	    costVector[i] = d*beta[i]+residual[i];
	}
	return costVector;
	
    }
    
    public static void main(String[] args){
	CostMultiRegressionModel test = new CostMultiRegressionModel();
	
	double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
	double[][] x = new double[6][];
	x[0] = new double[]{0, 0, 0, 0, 0};
	x[1] = new double[]{2.0, 0, 0, 0, 0};
	x[2] = new double[]{0, 3.0, 0, 0, 0};
	x[3] = new double[]{0, 0, 4.0, 0, 0};
	x[4] = new double[]{0, 0, 0, 5.0, 0};
	x[5] = new double[]{0, 0, 0, 0, 6.0};
	test.newSampleData(y, x);
	System.out.println(Arrays.toString(test.estimateRegressionParameters()));
	
	
	double[][] costsMatrix = {{300.0,51.0,0.0,351.0,410.0},
		{355.0,54.0,0.0,409.0,470.0},
		{450.0,57.0,0.0,507.0,580.0},
		{890.0,64.0,0.0,954.0,1075.0},
		{1225.0,79.0,0.0,1304.0,1475.0},
		{1650.0,93.0,0.0,1743.0,1950.0}};
	
	double[] flowRateList = {0.05,0.10,0.18,0.42,0.78,1.40};
	test.newSampleData(flowRateList, costsMatrix);
	Double[] cost1 = test.generateCostResults(0.10);
	Double[] cost2 = test.generateCostResults(2.0);
	
	System.out.println(Arrays.toString(cost1));
	System.out.println(Arrays.toString(cost2));

    }
}
