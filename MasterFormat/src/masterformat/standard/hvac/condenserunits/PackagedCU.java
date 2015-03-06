package masterformat.standard.hvac.condenserunits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PackagedCU extends AbstractCondenserUnits {

    private Double capacity;

    private final Double[] capacityList = { 5275.3, 25584.3, 35168.5, 70337.1 };
    private static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    

    public PackagedCU() {
	unit = "$/Ea";
	hierarchy = "236200 Packaged Compressor and Condenser Units:236213 Packaged Air-Cooled Refrigerant Compressor and Condenser Units";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Capacity")) {
		capacity = Double.parseDouble(userInputsMap.get(temp));
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
	Double[][] costsMatrix = { { 1250.0, 345.0, 0.0, 1595.0, 1900.0 },
		{ 2375.0, 1425.0, 0.0, 3800.0, 4775.0 },
		{ 5000.0, 1725.0, 0.0, 6725.0, 8100.0 },
		{ 11600.0, 3350.0, 0.0, 14950.0, 17800.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Condensing unit, Air cooled, compressor, standard controls, 5200 watts");
	optionQuantities.add(0);
	optionLists.add("Condensing unit, Air cooled, compressor, standard controls, 25500 watts");
	optionQuantities.add(0);
	optionLists.add("Condensing unit, Air cooled, compressor, standard controls, 35100 watts");
	optionQuantities.add(0);
	optionLists.add("Condensing unit, Air cooled, compressor, standard controls, 70300 watts");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	if (capacity <= 5275.3) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 5200 watts";
	    costVector = deepCopyCost(priceData.get(description));
	    optionQuantities.set(0, optionQuantities.get(0)+1);
	} else if (capacity > 5275.3 && capacity <= 25584.3) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 25500 watts";
	    costVector = deepCopyCost(priceData.get(description));
	    optionQuantities.set(1, optionQuantities.get(1)+1);
	} else if (capacity > 17584.3 && capacity <= 35168.5) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 35100 watts";
	    costVector = deepCopyCost(priceData.get(description));
	    optionQuantities.set(2, optionQuantities.get(2)+1);
	} else if (capacity > 35168.8 && capacity <= 70337.1) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 70300 watts";
	    costVector = deepCopyCost(priceData.get(description));
	    optionQuantities.set(3, optionQuantities.get(3)+1);
	} else {
	    description = "Condensing unit, Air cooled, compressor, standard controls, Groups ";
	    fittingCapacity();
	}
    }
    
    private void fittingCapacity(){
	setToZero();
	//shows the best fit capacity
	Double fittedCapacity=0.0;
	//shows the total capacity added
	Double totalCapacity=0.0;
	costVector=deepCopyCost(Default_Cost_Vector);
	
	while(totalCapacity<capacity){
	    fittedCapacity = findFittedCapacity(totalCapacity);
	    totalCapacity+=fittedCapacity;
	}
    }
    
    private Double findFittedCapacity(Double total){
	//the difference between capacity and total capacity
	Double temp = capacity;
	//index shows the current best fit capacity
	int criticalIndex = 0;
	for(int i=0; i<capacityList.length; i++){
	    Double residual = Math.abs(capacity-total-capacityList[i]);
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
