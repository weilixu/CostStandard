package eplus.optimization;

import java.util.ArrayList;

public class OptResult {
	
	double firstcost;
	double operationcost;
	double pvCost;
	double hvacCost;
	double eui;
	ArrayList<String> componentSelection;
	ArrayList<Double> numericValueList;
	boolean regressionMode = false;
	
	public OptResult(){
		firstcost = 0.0;
		operationcost = 0.0;
		eui = 0.0;
		componentSelection = new ArrayList<String>();
		numericValueList = new ArrayList<Double>();
	}
	
	public void setEUI(double eui){
	    this.eui = eui;
	}
	
	public void setPVCost(double pv){
	    pvCost = pv;
	}
	
	public void setHVACCost(double hvac){
	    hvacCost= hvac;
	}
	
	public void setFirstCost(double first){
	    firstcost = first;
	}
	
	public void setOperationCost(double operation){
	    operationcost = operation;
	}
	
	public double getEUI(){
	    return eui;
	}
	
	public double getFirstCost(){
		return firstcost;
	}
	
	public double getOperationCost(){
		return operationcost;
	}
	
	public int getComponentLength(){
	    return componentSelection.size();
	}
	
	public String getComponent(int index){
	    return componentSelection.get(index);
	}
	
	public void addComponent(String component){
	    componentSelection.add(component);
	}
	
	public void addNumericValues(Double value){
	    numericValueList.add(value);
	}
	
	public int getNumericLength(){
	    return numericValueList.size();
	}
	
	public Double getNumericValue(int index){
	    return numericValueList.get(index);
	}
	
	public boolean equals(OptResult another){
	    int size = componentSelection.size();
	    for(int i=0; i<size; i++){
		if(!componentSelection.get(i).equals(another.getComponent(i))){
		    return false;
		}
	    }
	    
	    for(int j=0; j<numericValueList.size(); j++){
		if(!numericValueList.get(j).equals(another.getNumericValue(j))){
		    return false;
		}
	    }
	    return true;
	}
	
	public boolean getRegressionMode(){
	    return regressionMode;
	}
	
	public void setRegressionMode(){
	    regressionMode = true;
	}
}
