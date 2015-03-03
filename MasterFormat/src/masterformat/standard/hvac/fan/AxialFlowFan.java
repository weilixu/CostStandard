package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AxialFlowFan extends AbstractFan {

    private Double flowRate;
    private final Double[] flowRateList = { 1.0, 1.9, 3.8 };

    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public AxialFlowFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233413 Axial HVAC Fans";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Flow Rate")) {
		flowRate = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    flowRate = Double.parseDouble(surfaceProperties[flowRateIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Flow Rate:m3/s");
	}
    }

    @Override
    protected void initializeData() {

	Double[][] costsMatrix = { { 2225.0, 285.0, 0.0, 2510.0, 2875.0 },
		{ 2600.0, 320.0, 0.0, 2920.0, 3325.0 },
		{ 3275.0, 365.0, 0.0, 3640.0, 4175.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Air conditioning and process air handling, Vaneaxial, low pressure, 1 m3/s, 372 Watts");
	optionQuantities.add(0);
	optionLists
		.add("Air conditioning and process air handling, Vaneaxial, low pressure, 1.9 m3/s, 746 Watts");
	optionQuantities.add(0);
	optionLists
		.add("Air conditioning and process air handling, Vaneaxial, low pressure, 3.8 m3/s, 1491 Watts");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer index = 0;
	if (flowRate <= 1.0) {
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 1.0 && flowRate <= 1.9) {
	    index = 1;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 1.9 && flowRate <= 3.8) {
	    index = 2;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else {
	    description = "Air conditioning and process air handling, Vaneaxial, low pressure, groups";
	    fittingFlowRate();
	}
    }

    private void fittingFlowRate() {
	// shows the best fit capacity
	Double fittedFlowRate = 0.0;
	// shows the total capacity added
	Double totalFlowRate = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalFlowRate < flowRate) {
	    fittedFlowRate = findFittedFlowRate(totalFlowRate);
	    totalFlowRate += fittedFlowRate;
	}
    }

    private Double findFittedFlowRate(Double total) {
	// the difference between capacity and total capacity
	Double temp = flowRate;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = 0; i < flowRateList.length; i++) {
	    Double residual = Math.abs(flowRate - total - flowRateList[i]);
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

	return flowRateList[criticalIndex];
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

    // private Double[] regressionCosts() {
    // System.out.println("here?");
    // return regressionModel.predictCostVector(flowRate);
    // }
}