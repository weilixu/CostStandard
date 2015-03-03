package masterformat.standard.hvac.convectionunits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FanCoilAC extends AbstractConvectionUnits {

    private String coolCoilType;
    private Double coolingCapacity;
    //private boolean hotWaterCoil;

    private final Double[] coolingList = { 1758.4, 3516.9, 5275.3, 7033.7,
	    10550.6, 5275.3, 17584.3, 35168.5, 70337.1 };

    public FanCoilAC() {
	unit = "$/Ea";
	hierarchy = "238200 Convection Heating and Cooling Units:238219 Fan Coil Units:238219.10 Fan Coil Air Conditioning";
	coolCoilType = "Water";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("CoolCoilType")) {
		coolCoilType = userInputsMap.get(temp);
	    } else if (temp.equals("coolPower")) {
		coolingCapacity = Double.parseDouble(userInputsMap.get(temp));
	    }
//	    } else if(temp.equals("HotWaterCoil")){
//		hotWaterCoil=true;
//	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	// this only needs cooling capacity
	try {
	    coolingCapacity = Double
		    .parseDouble(properties[coolingCapacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:coolPower:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = { { 815.0, 108.0, 0.0, 923.0, 1050.0 },
		{ 950.0, 143.0, 0.0, 1093.0, 1275.0 },
		{ 1050.0, 156.0, 0.0, 1206.0, 1375.0 },
		{ 1350.0, 164.0, 0.0, 1514.0, 1725.0 },
		{ 2175.0, 215.0, 0.0, 2390.0, 2725.0 },
		{ 680.0, 172.0, 0.0, 852.0, 1000.0 },
		{ 1300.0, 287.0, 0.0, 1587.0, 1875.0 },
		{ 2800.0, 515.0, 0.0, 3315.0, 3850.0 },
		{ 5400.0, 1900.0, 0.0, 7300.0, 8825.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Chilled Water, 1758.4Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Chilled Water, 3516.9Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Chilled Water, 5275.3Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Chilled Water, 7033.7Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Chilled Water, 10550.6Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Direct expansion, for use w/ air cooled condensing unit, 5275.3Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Direct expansion, for use w/ air cooled condensing unit, 17584.3Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Direct expansion, for use w/ air cooled condensing unit, 35168.5Watt");
	optionQuantities.add(0);
	optionLists
		.add("Fan coil AC, cabinet mounted, filters and controls, Direct expansion, for use w/ air cooled condensing unit, 70337.1Watt");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer selectedIndex = -1;
	Integer index = 0;
	Integer upperLimit = 4;
	//selection on chilled water coil
	if (coolCoilType.equals("Water")) {
	    for (int i = index; i <= upperLimit; i++) {
		Double cooling = coolingList[i];
		if (coolingCapacity <= cooling) {
		    selectedIndex = i;
		    break;
		}
	    }
	    if (selectedIndex < 0) {
		description = "Fan coil AC, cabinet mounted, filters and controls, Chilled Water,grouped";
		fittingPower(index, upperLimit);
	    }else{
		description = optionLists.get(selectedIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(selectedIndex);
		optionQuantities.set(selectedIndex, i + 1);
	    }
	//selection on DX coils
	} else if (coolCoilType.equals("DX")) {
	    index = 5;
	    upperLimit = 8;
	    for (int i = index; i <= upperLimit; i++) {
		Double cooling = coolingList[i];
		if (coolingCapacity <= cooling) {
		    selectedIndex = i;
		    break;
		}
	    }
	    if (selectedIndex < 0) {
		description = "Fan coil AC, cabinet mounted, filters and controls, Chilled Water,grouped";
		fittingPower(index, upperLimit);
	    }else{
		description = optionLists.get(selectedIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(selectedIndex);
		optionQuantities.set(selectedIndex, i + 1);
	    }
	}
	
	multiplyCost(1.4, 1.1);

	//boolean indicator to see if there is need to multiply the cost
//	if (hotWaterCoil) {
//	    multiplyCost(1.4, 1.1);
//	    //hotWaterCoil = false;
//	}
    }

    private void fittingPower(Integer lower, Integer upper) {
	// shows the best fit capacity
	Double fittedPower = 0.0;
	// shows the total capacity added
	Double totalPower = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalPower < coolingCapacity) {
	    fittedPower = findFittedPower(totalPower, lower, upper);
	    totalPower += fittedPower;
	}
    }

    private Double findFittedPower(Double total, Integer lower, Integer upper) {
	// the difference between capacity and total capacity
	Double temp = coolingCapacity;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = lower; i <= upper; i++) {
	    Double residual = Math
		    .abs(coolingCapacity - total - coolingList[i]);
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

	return coolingList[criticalIndex];
    }

    private void multiplyCost(Double materialMultiplier, Double laborMultiplier) {
	Double materialCost = costVector[materialIndex];
	Double laborCost = costVector[laborIndex];

	costVector[totalIndex] = costVector[totalIndex] - materialCost
		- laborCost;
	costVector[totalOPIndex] = costVector[totalOPIndex] - materialCost
		- laborCost;

	materialCost = materialCost * materialMultiplier;
	laborCost = laborCost * laborMultiplier;

	costVector[totalIndex] = costVector[totalIndex] + materialCost
		+ laborCost;
	costVector[totalOPIndex] = costVector[totalOPIndex] + materialCost
		+ laborCost;

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
