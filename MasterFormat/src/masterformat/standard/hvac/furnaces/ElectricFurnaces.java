package masterformat.standard.hvac.furnaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ElectricFurnaces extends AbstractFurnace {

    private Double power;
    
    private final Double[] powerList = {9999.3};
    
    public ElectricFurnaces(){
	unit = "$/Ea.";
	hierarchy = "235400 Furnaces:235413 Electric-Resistance Furnaces:235413.10 Electric Furnaces";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try{
	    power = Double.parseDouble(surfaceProperties[powerIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = {{455.0,233.0,0.0,688.0,855.0}};
	
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Electric Furnaces, Hot air, blowers, std.controls not including gas, oil or flue piping, 10,000 watts");
	optionQuantities.add(0);
	
	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }
    
    @Override
    public void selectCostVector() {
	setToZero();
	if(power<=9993.7){
	    description = optionLists.get(0);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(0);
	    optionQuantities.set(0, i+1);
	}else{
	    description = "Electric Furnaces, Hot air, blowers, std.controls not including gas, oil or flue piping, grouped";
	    fittingPower();
	}
    }
    
    private void fittingPower(){
	//shows the best fit capacity
	Double fittedPower=0.0;
	//shows the total capacity added
	Double totalPower=0.0;
	costVector=deepCopyCost(Default_Cost_Vector);
	
	while(totalPower<power){
	    fittedPower = findFittedPower(totalPower);
	    totalPower+=fittedPower;
	}
    }
    
    private Double findFittedPower(Double total){
	//the difference between capacity and total capacity
	Double temp = power;
	//index shows the current best fit capacity
	int criticalIndex = 0;
	
	for(int i=0; i<powerList.length; i++){
	    Double residual = Math.abs(power-total-powerList[i]);
	    if(residual<temp){
		temp = residual;
		criticalIndex = i;
	    }
	}
	//add to the cost vector
	Double[] itemCost = priceData.get(optionLists.get(criticalIndex));
	for(int j=0; j<costVector.length; j++){
	    costVector[j]+=itemCost[j];
	}
	Integer q = optionQuantities.get(criticalIndex)+1;
	optionQuantities.set(criticalIndex, q);
	
	return powerList[criticalIndex];
    }
    
    private void setToZero(){
	for(int i=0; i<optionQuantities.size(); i++){
	    optionQuantities.set(i, 0);
	}
    }

    private Double[] deepCopyCost(Double[] costVector){
	Double[] temp = new Double[costVector.length];
	for(int i=0; i<costVector.length; i++){
	    temp[i]= costVector[i];
	}
	return temp;
    }

}
