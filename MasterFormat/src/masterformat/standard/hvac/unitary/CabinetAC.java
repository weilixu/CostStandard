package masterformat.standard.hvac.unitary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CabinetAC extends AbstractUnitarySystem {

    /**
     * Heat coil type includes hotwater, electric and steam, default is electric or gas
     */
    private String heatCoilType;
    private Double heatingCapacity;
    private Double coolingCapacity;

    private final Double hotWaterHeatPercent = 1.1;
    private final Double steamHeatPercent = 1.3;

    private final Double[] coolingList = { 1758.4, 2637.6, 3516.9, 4396.1 };
    private final Double[] heatingList = { 2579.0, 4073.7, 4073.7, 4073.7 };

    public CabinetAC() {
	unit = "$/Ea";
	hierarchy = "238100 Decentralized Unitary HVAC Equipment:238113 Packaged Terminal Air-Conditioners:238113.10 Packaged Cabinet Type Air-Conditioners";
	heatCoilType = "Electric";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("HeatCoilType")) {
		heatCoilType = userInputsMap.get(temp);
	    } else if (temp.equals("coolPower")) {
		coolingCapacity = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("heatPower")) {
		heatingCapacity = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    coolingCapacity = Double
		    .parseDouble(properties[coolingCapacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:coolPower:Watt");
	}
	try {
	    heatingCapacity = Double
		    .parseDouble(properties[heatingCapacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:heatPower:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = { { 755.0, 143.0, 0.0, 918.0, 1075.0 },
		{ 1200.0, 172.0, 0.0, 1372.0, 1575.0 },
		{ 1350.0, 215.0, 0.0, 1565.0, 1825.0 },
		{ 1450.0, 287.0, 0.0,1737.0, 2025.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Cabinet, wall sleeve, louver,thermostat, manual changeover, 208 V, 1758Watts Cooling, 2579Watts Heating");
	optionQuantities.add(0);
	optionLists
		.add("Cabinet, wall sleeve, louver,thermostat, manual changeover, 208 V, 2637Watts Cooling, 4073Watts Heating");
	optionQuantities.add(0);
	optionLists
		.add("Cabinet, wall sleeve, louver,thermostat, manual changeover, 208 V, 3517Watts Cooling, 4073Watts Heating");
	optionQuantities.add(0);
	optionLists
		.add("Cabinet, wall sleeve, louver,thermostat, manual changeover, 208 V, 4396Watts Cooling, 4073Watts Heating");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer selectedIndex = -1;
	
	for (int i = 0; i < coolingList.length; i++) {
	    Double cooling = coolingList[i];
	    Double heating = heatingCapacityAdjustment(heatingList[i]);

	    if (coolingCapacity <= cooling && heatingCapacity <= heating) {
		selectedIndex = i;
		break;
	    }
	}
	
	// when it require bigger size
	if (selectedIndex < 0) {
	    description = "Cabinet, wall sleeve, louver,thermostat, manual changeover, 208 V, grouped";
	    fittingPower();
	} else {
	    description = optionLists.get(selectedIndex);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(selectedIndex);
	    optionQuantities.set(selectedIndex, i + 1);
	}
	
	//multiply the cost function
	if(heatCoilType.equals("Hot Water")){
	    multiplyCost(1.05,1.10);
	}else if(heatCoilType.equals("Steam")){
	    multiplyCost(1.08,1.10);
	}
    }

    private void fittingPower() {
	// shows the best fit capacity
	Integer fittedIndex = 0;
	// shows the total capacity added
	Double totalCoolingPower = 0.0-coolingCapacity;
	Double totalHeatingPower = 0.0 - heatingCapacity;
	costVector = deepCopyCost(Default_Cost_Vector);
	Double min = Math.min(totalCoolingPower, totalHeatingPower);
	while (min < 0) {
	    fittedIndex = findFittedIndex(totalCoolingPower, totalHeatingPower);
	    totalCoolingPower += coolingList[fittedIndex];
	    totalHeatingPower += heatingList[fittedIndex];
	    
	    //write in the cost vector and optionQuantities
	    Double[] itemCost = priceData.get(optionLists.get(fittedIndex));
	    for(int j=0; j<costVector.length; j++){
		costVector[j]+=itemCost[j];
	    }
	    Integer q = optionQuantities.get(fittedIndex)+1;
	    optionQuantities.set(fittedIndex, q);
	    
	    //update the min
	    min = Math.min(totalCoolingPower, totalHeatingPower);
	}
    }

    private Integer findFittedIndex(Double totalCool, Double totalHeat) {
	// the difference between capacity and total capacity
	Double tempCool = coolingCapacity;
	Double tempHeat = heatingCapacity;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = 0; i < coolingList.length; i++) {
	    Double residualCool = coolingCapacity + totalCool - coolingList[i];
	    Double residualHeat = heatingCapacity + totalHeat - heatingCapacityAdjustment(heatingList[i]);
	    if (residualCool < 0 && Math.abs(residualCool) < Math.abs(tempCool)) {
		tempCool = residualCool;
		criticalIndex = i;
	    }
	    if (residualHeat < 0 && Math.abs(residualHeat) < Math.abs(tempHeat)) {
		tempHeat = residualHeat;
		criticalIndex = i;
	    }
	}
	return criticalIndex;
    }

    /*
     * Adjust the heating capacity according to the object used
     */
    private Double heatingCapacityAdjustment(Double capacity) {
	if (heatCoilType.equals("Hot Water")) {
	    capacity = capacity * hotWaterHeatPercent;
	} else if (heatCoilType.equals("Steam")) {
	    capacity = capacity * steamHeatPercent;
	}
	return capacity;
    }
    
    private void multiplyCost(Double materialMultiplier, Double laborMultiplier){
	Double materialCost = costVector[materialIndex];
	Double laborCost = costVector[laborIndex];
	
	costVector[totalIndex] = costVector[totalIndex]-materialCost-laborCost;
	costVector[totalOPIndex] = costVector[totalOPIndex]-materialCost-laborCost;
	
	materialCost = materialCost*materialMultiplier;
	laborCost = laborCost*laborMultiplier;
	
	costVector[totalIndex] = costVector[totalIndex]+materialCost+laborCost;
	costVector[totalOPIndex] = costVector[totalOPIndex]+materialCost+laborCost;
	
    }

    private void setToZero() {
	for (int i = 0; i < optionQuantities.size(); i++) {
	    optionQuantities.set(i, 0);
	}
    }

    private Double[] deepCopyCost(Double[] costVector) {
	Double[] temp = new Double[costVector.length];
	for (int i = 0; i < costVector.length; i++) {
	    temp[i] = costVector[i];
	}
	return temp;
    }

}
