package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalWallExhauster extends AbstractFan {
    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private Double[] flowRateVector = { 0.29, 0.38, 0.39, 0.62,1.32, 1.76 };

    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public CentrifugalWallExhauster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.108500 Wall exhausters";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Flow Rate")) {
		flowRate = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("Drive")) {
		drives = userInputsMap.get(temp);
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
	Double[][] costsMatrix = { { 425.0, 73.0, 0.0, 498.0, 575.0 },
		{ 880.0, 79.0, 0.0, 959.0, 1075.0 },
		{ 1075.0, 85.5, 0.0, 1160.50, 1300.0 },
		{ 1250.0, 85.50, 0.0, 1335.5, 1500.0 },
		{ 1925.0, 114.0, 0.0, 2039.0, 2300.0 },
		{ 2000.0, 128.0, 0.0, 2128.0, 2400.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.29m3/s, 37Watts");
	optionQuantities.add(0);
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.38m3/s, 62Watts");
	optionQuantities.add(0);
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.39m3/s, 124Watts");
	optionQuantities.add(0);
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.62m3/s, 186Watts");
	optionQuantities.add(0);
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.32m3/s, 186Watts");
	optionQuantities.add(0);
	optionLists
		.add("Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.76m3/s, 373Watts");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}

	userInputs.add("OPTION:Drive:Direct drive");
	userInputs.add("OPTION:Drive:V-belt drive");
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperIndex = 0;
	Integer lowerIndex = 0;
	if (drives.equals("Direct drive")) {
	    if (flowRate <= 0.29) {
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.29 && flowRate <= 0.38) {
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.38 && flowRate <= 0.39) {
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.39) {
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 3;
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, grouped";
		fittingFlowRate(upperIndex, lowerIndex);
	    }
	} else if (drives.equals("V-belt drive")) {
	    lowerIndex = 4;
	    if (flowRate <= 1.32) {
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 1.32) {
		upperIndex = 5;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 5;
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, grouped";
		fittingFlowRate(upperIndex, lowerIndex);
	    }
	}
    }

    private void fittingFlowRate(Integer upper, Integer lower) {
	// shows the best fit capacity
	Double fittedFlowRate = 0.0;
	// shows the total capacity added
	Double totalFlowRate = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalFlowRate < flowRate) {
	    fittedFlowRate = findFittedPower(totalFlowRate, upper, lower);
	    totalFlowRate += fittedFlowRate;
	}
    }

    private Double findFittedPower(Double total, Integer upper, Integer lower) {
	// the difference between capacity and total capacity
	Double temp = flowRate;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = lower; i <= upper; i++) {
	    Double residual = Math.abs(flowRate - total - flowRateVector[i]);
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
	return flowRateVector[criticalIndex];
    }
    
    private void setToZero(){
	for(int i=0; i<optionQuantities.size(); i++){
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
