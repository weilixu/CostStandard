package masterformat.standard.hvac.unitary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AirSourceHeatPump extends AbstractUnitarySystem {

    /**
     * indicates whether the heat pump is in package or condensing unit only
     */
    private String heatPumpPackage;
    private Double coolingCapacity;
    private Double heatingCapacity;

    private final Double[] coolingList = { 7033.0, 17584.0, 26376.0, 35169.0,
	    52753.0, 70337.0, 87921.0, 7033.0, 14067.0, 26376.0 };
    private final Double[] heatingList = { 2491.0, 7913.0, 9671.0, 14654.0,
	    18757.0, 24911.0, 34875.0, 1905.0, 3809.0, 10257.0 };

    public AirSourceHeatPump() {
	unit = "$/Ea";
	hierarchy = "238100 Decentralized Unitary HVAC Equipment:238143 Air-Source Unitary HVAC Equipment:238143.10 Air-Source Heat Pumps";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("hpPackage")) {
		heatPumpPackage = userInputsMap.get(temp);
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
	Double[][] costsMatrix = { { 2400.0, 430.0, 0.0, 2830.0, 3300.0 },
		{ 3600.0, 1725.0, 0.0, 5325.0, 6550.0 },
		{ 6425.0, 1900.0, 0.0, 8325.0, 9950.0 },
		{ 8500.0, 2100.0, 0.0, 10600.0, 12500.0 },
		{ 11800.0, 2675.0, 0.0, 14475.0, 17100.0 },
		{ 17000.0, 3825.0, 0.0, 20825.0, 24500.0 },
		{ 20400.0, 5350.0, 0.0, 25750.0, 30600.0 },
		{ 3100.0, 575.0, 0.0, 3675.0, 4300.0 },
		{ 4200.0, 895.0, 0.0, 5095.0, 5975.0 },
		{ 7325.0, 2150.0, 0.0, 9475.0, 11300.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,7033 watt cooling, 2491 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,17584 watt cooling, 7913 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,26376 watt cooling, 9671 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,35169 watt cooling, 14654 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,52753 watt cooling, 18757 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,70337 watt cooling, 24911 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only,87921 watt cooling, 34875 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, single package,7033 watt cooling, 1905 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, single package,14067 watt cooling, 3809 watt heating");
	optionQuantities.add(0);
	optionLists
		.add("Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, single package,26376 watt cooling, 10257 watt heating");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}

	userInputs.add("OPTION:hpPackage:Condenser Only");
	userInputs.add("OPTION:hpPackage:Single Package");
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer selectedIndex = -1;
	Integer index = 0;
	Integer upperLimit = 6;
	if (heatPumpPackage.equalsIgnoreCase("Condenser Only")) {
	    for (int i = index; i <= upperLimit; i++) {
		if (coolingCapacity <= coolingList[i]
			&& heatingCapacity <= heatingList[i]) {
		    selectedIndex = i;
		    break;
		}
	    }
	    // when it requires bigger size
	    if (selectedIndex < 0) {
		description = "Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, outside condensing unit only, grouped";
		fittingPower(index,upperLimit);
	    } else {
		description = optionLists.get(selectedIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(selectedIndex);
		optionQuantities.set(selectedIndex, i + 1);
	    }
	} else {
	    index = 7;
	    upperLimit = 9;
	    for (int i = index; i <= upperLimit; i++) {
		if (coolingCapacity > coolingList[i]
			&& heatingCapacity > heatingList[i]) {
		    selectedIndex = i;
		    break;
		}
	    }
	    //when it requires bigger size
	    if(selectedIndex<0){
		description = "Air-source heat pumps, air to air, split system, not including curbs, pads, fan coil and ductwork, single package, grouped";
		fittingPower(index,upperLimit);
	    }else{
		description = optionLists.get(selectedIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(selectedIndex);
		optionQuantities.set(selectedIndex, i + 1);
	    }
	}
    }

    private void fittingPower(Integer start, Integer end) {
	// shows the best fit capacity
	Integer fittedIndex = 0;
	// shows the total capacity added
	Double totalCoolingPower = 0.0-coolingCapacity;
	Double totalHeatingPower = 0.0 - heatingCapacity;
	costVector = deepCopyCost(Default_Cost_Vector);
	Double min = Math.min(totalCoolingPower, totalHeatingPower);
	while (min<0) {
	    fittedIndex = findFittedIndex(totalCoolingPower, totalHeatingPower,start,end);
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

    private Integer findFittedIndex(Double totalCool, Double totalHeat,Integer start, Integer end) {
	// the difference between capacity and total capacity
	Double tempCool = coolingCapacity;
	Double tempHeat = heatingCapacity;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = start; i <= end; i++) {
	    Double residualCool = coolingCapacity + totalCool - coolingList[i];
	    Double residualHeat = heatingCapacity + totalHeat - heatingList[i];
	    if (residualCool<0 && Math.abs(residualCool)<Math.abs(tempCool)) {
		tempCool = residualCool;
		criticalIndex = i;
	    }
	    if (residualHeat<0 && Math.abs(residualHeat)<Math.abs(tempHeat)) {
		tempHeat = residualHeat;
		criticalIndex = i;
	    }
	}
	return criticalIndex;
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
