package masterformat.standard.hvac.condenserunits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RefrigerantCondenser extends AbstractCondenserUnits {
    
    /**
     * belt drive or direct drive
     */
    private String drives;
    private Double capacity;

    private final Double[] capacityList = { 175842.0, 207494.0, 256730.0,
	    302449.0, 309483.0, 3517.0, 5275.3, 7034.0, 17584.3, 35168.5,
	    56270.0, 91438.2, 144191.0, 221562.0 };
    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public RefrigerantCondenser() {
	unit = "$/Ea";
	hierarchy = "236200 Packaged Compressor and Condenser Units:236313 Air-Cooled Refrigerant Condensers";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Capacity")) {
		capacity = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("Drive")){
		drives = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    capacity = Double.parseDouble(properties[capacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Capacity:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = { { 10600.0,1950.0,0.0,12550.0,14600.0},
		{1270.0,2300.0,0.0,15000.0,17500.0},
		{16600.0,2850.0,0.0,19450.0,22600.0},
		{19200.0,3375.0,0.0,22575.0,26200.0},
		{20600.0,3450.0,0.0,24050.0,27900.0},
		{1650.0,226.0,0.0,1876.0,2175.0},
		{1975.0,239.0,0.0,2214.0,2525.0},
		{2175.0,269.0,0.0,2444.0,2800.0},
		{5400.0,430.0,0.0,5830.0,6575.0},
		{6325.0,615.0,0.0,6940.0,7875.0},
		{10200.0,780.0,0.0,10980.0,12400.0},
		{12500.0,1025.0,0.0,13525.0,15400.0},
		{17600.0,1725.0,0.0,19325.0,22000.0},
		{27700.0,2450.0,0.0,30150.0,34200.0}
		};
	
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Air cooled, belt drive, propeller fan, 175842 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, belt drive, propeller fan, 207494 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, belt drive, propeller fan, 256730 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, belt drive, propeller fan, 302449 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, belt drive, propeller fan, 309483 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 3517 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 5275 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 7034 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 17584 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 35168 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 56270 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 91438 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 144191 Watt");
	optionQuantities.add(0);
	optionLists.add("Air cooled, direct drive, propeller fan, 221562 Watt");
	optionQuantities.add(0);
	
	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
	
	userInputs.add("OPTION:Drive:Direct Drive");
	userInputs.add("OPTION:Drive:V-belt Drive");
    }
    
    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperIndex = 0;
	Integer lowerIndex = 0;
	if(drives.equals("V-belt Drive")){
	    if(capacity<=175842){
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);		
	    }else if(capacity>175842 && capacity<=207494){
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>207494 && capacity<=256730){
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>256730 && capacity<=302449){
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>302449 && capacity<=309483){
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex = 4;
		description = "Air cooled, belt drive, propeller fan,grouped";
		fittingCapacity(upperIndex, lowerIndex);
	    }
	}else if(drives.equals("Direct Drive")){
	    lowerIndex = 5;
	    if(capacity<=3517){
		upperIndex = 5;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>3517 && capacity<=5275.3){
		upperIndex = 6;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>5275.3 && capacity<=7034){
		upperIndex = 7;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>7034 && capacity<=17584.3){
		upperIndex = 8;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>17584.3 && capacity<=35168.5){
		upperIndex = 9;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>35168.5 && capacity<=56270){
		upperIndex = 10;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>56270 && capacity<=91438.2){
		upperIndex = 11;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>91438.2 && capacity<=144191){
		upperIndex = 12;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else if(capacity>144191 && capacity<=221562){
		upperIndex = 13;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex = 13;
		description = "Air cooled, direct drive, propeller fan,grouped";
		fittingCapacity(upperIndex, lowerIndex);
	    }
	}
    }
    
    private void fittingCapacity(Integer upper, Integer lower) {
	System.out.println(upper+" "+lower);
	// shows the best fit capacity
	Double fittedCapacity = 0.0;
	// shows the total capacity added
	Double totalCapacity = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalCapacity < capacity) {
	    fittedCapacity = findFittedPower(totalCapacity, upper, lower);
	    totalCapacity += fittedCapacity;
	}
    }

    private Double findFittedPower(Double total, Integer upper, Integer lower) {
	// the difference between capacity and total capacity
	Double temp = capacity;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = lower; i <= upper; i++) {
	    Double residual = Math.abs(capacity - total - capacityList[i]);
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
	return capacityList[criticalIndex];
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
