package masterformat.standard.hvac.pump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalPumps extends AbstractPump{
    /**
     * types: bronze sweat connection, flange connection, cast iron flange connection, pumps circulating, varied by size";
     */
    private String pumpType;
    private boolean nonferrous;
    private Double power;
    
    private final Double nonFerrousPercent = 1.3;
    
    private final Double[] powerList = {18.6,62.1,93.2,248.6,124.3,186.4,62.2,248.6,248.6,124.3,186.4,186.4};
    
    public CentrifugalPumps(){
	unit = "$/Ea.";
	hierarchy = "232100 Hydronic Piping and Pump:232123 Hydronic Pumps:232123.13 In-Line Centrifugal Hydronic Pumps";
	nonferrous = false;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("PUMPTYPE")) {
		pumpType = userInputsMap.get(temp);
	    } else if (temp.equals("Ferrous")) {
		String ferrou = userInputsMap.get(temp);
		if(ferrou.equalsIgnoreCase("true")){
		    nonferrous = true;
		}
	    }
	}	
    }

    @Override
    public void setVariable(String[] properties) {
	try{
	    power = Double.parseDouble(properties[powerIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Power:Watt");
	}	
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = {{218.0,53.0,0.0,271.0,320.0},
		{565.0,141.0,0.0,706.0,835.0},
		{970.0,141.0,0.0,1111.0,1300.0},
		{1400.0,169.0,0.0,1569.0,1775.0},
		{1775.0,169.0,0.0,1944.0,2200.0},
		{1075.0,141.0,0.0,1216.0,1425.0},
		{365.0,141.0,0.0,506.0,615.0},
		{680.0,141.0,0.0,821.0,960.0},
		{745.0,169.0,0.0,914.0,1075.0},
		{960.0,169.0,0.0,1129.0,1300.0},
		{975.0,169.0,0.0,1186.0,1400.0},
		{790.0,141.0,0.0,931.0,1075.0}
	};
	
	
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Bronze, sweat connections, 18.6Watts, in line, 0.02m size");
	optionQuantities.add(0);
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 62.1Watts");
	optionQuantities.add(0);
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 93.2Watts");
	optionQuantities.add(0);
	optionLists.add("Bronze, flange connection, 0.05m size, 124.3Watts");
	optionQuantities.add(0);
	optionLists.add("Bronze, flange connection, 0.06m size, 186.4Watts");
	optionQuantities.add(0);
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 248.6Watts");
	optionQuantities.add(0);
	optionLists.add("Cast iron, flange connection, 0.02m-0.04m size, 62.2Watts");
	optionQuantities.add(0);
	optionLists.add("Cast iron, flange connection, 0.02m-0.04m size, 248.6Watts");
	optionQuantities.add(0);
	optionLists.add("Pumps, circulating, 0.05m size, 124.3Watts");
	optionQuantities.add(0);
	optionLists.add("Pumps, circulating, 0.06m size, 186Watts");
	optionQuantities.add(0);
	optionLists.add("Pumps, circulating, 0.08m size, 187Watts");
	optionQuantities.add(0);
	optionLists.add("Pumps, circulating, 0.02m-0.04m size, 248.6Watts");
	optionQuantities.add(0);
	
	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
	
	userInputs.add("OPTION:PUMPTYPE:Bronze Sweat Connection");
	userInputs.add("OPTION:PUMPTYPE:Bronze Flange Connection");
	userInputs.add("OPTION:PUMPTYPE:Cast Iron Flange Connection");
	userInputs.add("OPTION:PUMPTYPE:Pumps Circulating");
	userInputs.add("BOOL:Ferrous:Nonferrous Impeller");
    }
    

    
    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperIndex = 0;
	Integer lowerIndex = 0;
	if(pumpType.equalsIgnoreCase("Bronze Sweat Connection")){
	    if(power<=18.6){
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else {
		description = "Bronze, sweat connections, 18.6Watts, in line, 0.02m size, grouped";
		fittingPower(upperIndex,lowerIndex);
	    }
	}else if(pumpType.equalsIgnoreCase("Bronze Flange Connection")){
	    lowerIndex = 1;
	    if(power<=62.1){
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>62.1 && power<=93.2){
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>93.2 && power<=124.3){
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>124.3 && power<=186.4){
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>186.4 && power<=248.6){
		upperIndex = 5;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex=5;
		description = "Bronze, flange connection, grouped";
		fittingPower(upperIndex,lowerIndex);
	    }
	}else if(pumpType.equalsIgnoreCase("Cast Iron Flange Connection")){
	    lowerIndex = 6;
	    if(power<=62.2){
		upperIndex = 6;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>62.2 && power<=248.6){
		upperIndex = 7;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex =7;
		description = "Cast Iron Flange Connection, grouped";
		fittingPower(upperIndex,lowerIndex);
	    }
	}else if(pumpType.equalsIgnoreCase("Pumps Circulating")){
	    lowerIndex = 8;
	    if(power<=124.3){
		upperIndex = 8;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>124.3 && power<=186){
		upperIndex = 9;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>186 && power<=187){
		upperIndex = 10;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(power>187 && power<=248.6){
		upperIndex = 11;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex=11;
		description = "Pumps, circulating, grouped";
		fittingPower(upperIndex,lowerIndex);
	    }
	}
	
	if(nonferrous){
	    multiplyMaterial();
	    nonferrous = false;
	}
    }
    
    private void setToZero(){
	for(int i=0; i<optionQuantities.size(); i++){
	    optionQuantities.set(i, 0);
	}
    }
    
    private void fittingPower(Integer upper, Integer lower) {
	// shows the best fit capacity
	Double fittedPower = 0.0;
	// shows the total capacity added
	Double totalPower = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalPower < power) {
	    fittedPower = findFittedPower(totalPower, upper, lower);
	    totalPower += fittedPower;
	}
    }

    private Double findFittedPower(Double total, Integer upper, Integer lower) {
	// the difference between capacity and total capacity
	Double temp = power;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = lower; i <= upper; i++) {
	    Double residual = Math.abs(power - total - powerList[i]);
	    if (residual < temp) {
		temp = residual;
		criticalIndex = i;
	    }
	}
	// add to the cost vector
	Double[] itemCost = priceData.get(optionLists.get(criticalIndex));
	for (int j = 0; j < costVector.length; j++) {
	    costVector[j] += itemCost[j];
	}
	Integer q = optionQuantities.get(criticalIndex) + 1;
	optionQuantities.set(criticalIndex, q);
	return powerList[criticalIndex];
    }
    
    private Double[] deepCopyCost(Double[] costVector) {
	Double[] temp = new Double[costVector.length];
	for (int i = 0; i < costVector.length; i++) {
	    temp[i] = costVector[i];
	}
	return temp;
    }
    
    private void multiplyMaterial(){
	costVector[totalIndex] = costVector[totalIndex] - costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex]
		- costVector[materialIndex];
	
	costVector[materialIndex] = costVector[materialIndex] * nonFerrousPercent;

	costVector[totalIndex] = costVector[totalIndex]
		+ costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex]
		+ costVector[materialIndex];
    }
    

}
