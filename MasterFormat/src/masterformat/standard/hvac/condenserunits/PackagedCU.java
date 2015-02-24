package masterformat.standard.hvac.condenserunits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PackagedCU extends AbstractCondenserUnits {

    private Double capacity;

    private final Double[] capacityList = { 5275.3, 17584.3, 35168.5, 70337.1 };
    private static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    private ArrayList<String> typesOne;

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

	typesOne = new ArrayList<String>();
	typesOne.add("Condensing unit, Air cooled, compressor, standard controls, 5200 watts");
	typesOne.add("Condensing unit, Air cooled, compressor, standard controls, 17500 watts");
	typesOne.add("Condensing unit, Air cooled, compressor, standard controls, 35100 watts");
	typesOne.add("Condensing unit, Air cooled, compressor, standard controls, 70300 watts");

	for (int i = 0; i < typesOne.size(); i++) {
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	if (capacity <= 5275.3) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 5200 watts";
	    costVector = deepCopyCost(priceData.get(description));
	} else if (capacity > 5275.3 && capacity <= 17584.3) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 17500 watts";
	    costVector = deepCopyCost(priceData.get(description));
	} else if (capacity > 17584.3 && capacity <= 35168.5) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 35100 watts";
	    costVector = deepCopyCost(priceData.get(description));
	} else if (capacity > 35168.8 && capacity <= 70337.1) {
	    description = "Condensing unit, Air cooled, compressor, standard controls, 70300 watts";
	    costVector = deepCopyCost(priceData.get(description));
	} else {
	    description = "Condensing unit, Air cooled, compressor, standard controls, ";
	    fittingCapacity();
	}
    }
    
    private void fittingCapacity(){
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
	Double[] itemCost = priceData.get(typesOne.get(criticalIndex));
	for(int j=0; j<costVector.length; j++){
	    costVector[j]+=itemCost[j];
	}
	//modify the description
	description=description+capacityList[criticalIndex]+"watts, ";
	return capacityList[criticalIndex];
    }
    
    private Double[] deepCopyCost(Double[] costVector){
	Double[] temp = new Double[costVector.length];
	for(int i=0; i<costVector.length; i++){
	    temp[i]= costVector[i];
	}
	return temp;
    }
}
