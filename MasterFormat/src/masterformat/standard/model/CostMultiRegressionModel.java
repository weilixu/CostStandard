package masterformat.standard.model;

import java.util.Arrays;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class CostMultiRegressionModel {
    
    
    private SimpleRegression materialRegression;
    private SimpleRegression laborRegression;
    private SimpleRegression equipmentRegression;
    private SimpleRegression totalRegression;
    private SimpleRegression totalOPRegression;
    
    public CostMultiRegressionModel(){
	materialRegression = new SimpleRegression();
	laborRegression = new SimpleRegression();
	equipmentRegression = new SimpleRegression();
	totalRegression = new SimpleRegression();
	totalOPRegression = new SimpleRegression();
    }
    
    public void addMaterialCost(Double input, Double cost){
	materialRegression.addData(input, cost);
    }
    
    public void addLaborCost(Double input, Double cost){
	laborRegression.addData(input,cost);
    }
    
    public void addEquipmentCost(Double input, Double cost){
	equipmentRegression.addData(input,cost);
    }
    
    public void addTotalCost(Double input, Double cost){
	totalRegression.addData(input,cost);
    }
    
    public void addTotalOPCost(Double input, Double cost){
	totalOPRegression.addData(input,cost);
    }
     
    public Double[] predictCostVector(Double input){
	Double material = materialRegression.predict(input);
	Double labor = laborRegression.predict(input);
	Double equipment = equipmentRegression.predict(input);
	Double total = totalRegression.predict(input);
	Double totalOP = totalOPRegression.predict(input);
	Double[] costVector = {material, labor, equipment,total,totalOP};
	return costVector;
    }
//    
//    public static void main(String[] args){
//	
//	CostMultiRegressionModel model = new CostMultiRegressionModel();
//	
//	Double[][] costsMatrix = { {300.0,51.0,0.0,351.0,410.0},
//		{355.0,54.0,0.0,409.0,470.0},
//		{450.0,57.0,0.0,507.0,580.0},
//		{890.0,64.0,0.0,954.0,1075.0},
//		{1225.0,79.0,0.0,1304.0,1475.0},
//		{1650.0,93.0,0.0,1743.0,1950.0}};
//	Double[] flowRateVector = {0.05,0.10,0.18,0.42,0.78,1.40};
//	
//	for(int i=0; i<flowRateVector.length; i++){
//	    model.addMaterialCost(flowRateVector[i], costsMatrix[i][0]);
//	    model.addLaborCost(flowRateVector[i], costsMatrix[i][1]);
//	    model.addEquipmentCost(flowRateVector[i], costsMatrix[i][2]);
//	    model.addTotalCost(flowRateVector[i], costsMatrix[i][3]);
//	    model.addTotalOPCost(flowRateVector[i], costsMatrix[i][4]);
//	}
//	System.out.println(Arrays.toString(model.predictCostVector(2.0)));
//    }
    
}
