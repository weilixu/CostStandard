package masterformat.standard.hvac.furnaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FuelFiredFurnaces extends AbstractFurnace{
    
    private Double power;
    
    private final Double[] powerList = {13188.2,17584.3,21980.3,29307.1};
    
    public FuelFiredFurnaces(){
	unit = "$/Ea.";
	hierarchy = "235400 Furnaces:235416 Fuel-Fired Furnaces:235416.13 Gas-Fired Furnaces";
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
	Double[][] costsMatrix = {{535.0,201.0,0.0,736.0,900.0},
		{535.0,212.0,0.0,747.0,915.0},
		{575.0,224.0,0.0,799.0,975.0},
		{625.0,252.0,0.0,877.0,1075.0}
	};
	
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 13188 watts");
	optionQuantities.add(0);
	optionLists.add("Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 17584 watts");
	optionQuantities.add(0);
	optionLists.add("Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 21980 watts");
	optionQuantities.add(0);
	optionLists.add("Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 29307 watts");
	optionQuantities.add(0);
	
	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }
    
    @Override
    public void selectCostVector() {
	if(power<=13188.2){
	    description = "Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 13188 watts";
	    costVector = deepCopyCost(priceData.get(description));    
	}else if(power>13188.2 && power<=17584.3){
	    description = "Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 17584 watts";
	    costVector = deepCopyCost(priceData.get(description));  
	}else if(power>17584.3 && power<=21980.3){
	    description = "Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 21980 watts";
	    costVector = deepCopyCost(priceData.get(description));  
	}else if(power>21980.3 && power<=29307.1){
	    description = "Gas-Fired Furnaces, AGA certified, upflow, direct drive models, 29307 watts";
	    costVector = deepCopyCost(priceData.get(description)); 
	}else{
	    description = "Gas-Fired Furnaces, AGA certified, upflow, direct drive models, grouped";
	    fittingPower();
	}
    }
    
    private void fittingPower(){
	setToZero();
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
