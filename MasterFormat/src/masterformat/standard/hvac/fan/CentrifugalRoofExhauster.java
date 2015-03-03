package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalRoofExhauster extends AbstractFan {

    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private boolean speedWind;
    private boolean explosion;
    private boolean topdischarge;

    private final Double speedWindPercent = 1.15;
    private final Double[] explosionproofAddition = { 600.0, 0.0, 0.0, 600.0,
	    660.0 };
    private final Double topdischargePercent = 1.15;

    private Double[] flowRateVector = { 0.15, 0.28, 0.38, 0.68, 0.97,0.78, 1.30, 1.65, 2.32, 4.02, 6.50,
	    9.70};
        
    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,0.0};


    public CentrifugalRoofExhauster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.107000 Roof exhauster";
	speedWind = false;
	explosion = false;
	topdischarge = false;
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
	    } else if (temp.equals("For 2 speed winding")) {
		String speed = userInputsMap.get(temp);
		if (speed.equals("true")) {
		    speedWind = true;
		}
	    } else if (temp.equals("For explosionproof motor")) {
		String explode = userInputsMap.get(temp);
		if (explode.equals("true")) {
		    explosion = true;
		}
	    } else if (temp.equals("For belt driven, top discharge")) {
		String top = userInputsMap.get(temp);
		if (top.equals("true")) {
		    topdischarge = true;
		}
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

	Double[][] costsMatrix = { { 705.0, 146.0, 0.0, 851.0, 1000.0 },
		{ 900.0, 171.0, 0.0, 1071.0, 1250.0 },
		{ 900.0, 205.0, 0.0, 1105.0, 1300.0 },
		{ 1450.0, 244.0, 0.0, 1694.0, 1975.0 },
		{ 1725.0, 256.0, 0.0, 1981.0, 2300.0 },
		{ 1300.0, 171.0, 0.0, 1471.0, 1675.0 },
		{ 1550.0, 205.0, 0.0, 1755.0, 2000.0 },
		{ 1725.0, 228.0, 0.0, 1953.0, 2250.0 },
		{ 2125.0, 256.0, 0.0, 2381.0, 2725.0 },
		{ 2800.0, 340.0, 0.0, 3140.0, 3600.0 },
		{ 3925.0, 510.0, 0.0, 4435.0, 5100.0 },
		{ 7875.0, 1025.0, 0.0, 8900.0, 10200.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, 0.15m3/s 0.007sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, 0.28m3/s 0.007sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, 0.38m3/s 0.008sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, 0.68m3/s 0.008sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, 0.97m3/s 0.010sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 0.78m3/s 0.008sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 1.30m3/s 0.014sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 1.65m3/s 0.014sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 2.32m3/s 0.015sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 4.02m3/s 0.018sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 6.50m3/s 0.023sqm damper");
	optionQuantities.add(0);
	optionLists
		.add("Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive, 9.70m3/s 0.028sqm damper");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}

	userInputs.add("OPTION:Drive:Direct drive");
	userInputs.add("OPTION:Drive:V-belt drive");
	userInputs.add("BOOL:SpeedWind:For 2 speed winding");
	userInputs.add("BOOL:Explosion:For explosionproof motor");
	userInputs.add("BOOL:TopDist:For belt driven, top discharge");
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperIndex = 0;
	Integer lowerIndex = 0;
	if (drives.equals("Direct drive")) {
	    if (flowRate <= 0.15) {
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.15 && flowRate <= 0.28) {
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.28 && flowRate <= 0.38) {
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.38 && flowRate <= 0.68) {
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.68 && flowRate <= 0.97) {
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 4;
		description = "Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, Direct drive, grouped";
		fittingFlowRate(upperIndex,lowerIndex);
	    }
	} else if (drives.equals("V-belt drive")) {
	    lowerIndex = 5;
	    if (flowRate <= 0.78) {
		upperIndex = 5;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.78 && flowRate < 1.30) {
		upperIndex = 6;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 1.30 && flowRate <= 1.65) {
		upperIndex = 7;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 1.65 && flowRate <= 2.32) {
		upperIndex = 8;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 2.32 && flowRate <= 4.02) {
		upperIndex = 9;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 4.02 && flowRate <= 6.50) {
		upperIndex = 10;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 6.50 && flowRate <= 9.70) {
		upperIndex = 11;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    }else{
		upperIndex =11 ;
		description = "Roof exhauster, centrifugal, aluminum housin, 0.3m galvanized curb, bird screen, back draft damper, 63Pa, V-belt drive,grouped";
		fittingFlowRate(upperIndex,lowerIndex);
	    }
	}

	if (speedWind) {
	    multiplyMaterial(speedWindPercent);
	    speedWind = false;
	}

	if (explosion) {
	    addAdditions(explosionproofAddition);
	    explosion = false;
	}

	if (topdischarge) {
	    multiplyMaterial(topdischargePercent);
	    topdischarge = false;
	}

    }
    
    private void fittingFlowRate(Integer upper, Integer lower) {
	// shows the best fit capacity
	Double fittedFlowRate = 0.0;
	// shows the total capacity added
	Double totalFlowRate = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalFlowRate < flowRate) {
	    fittedFlowRate = findFittedFlowRate(totalFlowRate, upper, lower);
	    totalFlowRate += fittedFlowRate;
	}
    }

    private Double findFittedFlowRate(Double total, Integer upper, Integer lower) {
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


    private void addAdditions(Double[] additions) {
	for (int i = 0; i < costVector.length; i++) {
	    costVector[i] = costVector[i] + additions[i];
	}
    }

    private void multiplyMaterial(Double percent) {
	costVector[totalIndex] = costVector[totalIndex]
		- costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex]
		- costVector[materialIndex];

	costVector[materialIndex] = costVector[materialIndex] * percent;

	costVector[totalIndex] = costVector[totalIndex]
		+ costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex]
		+ costVector[materialIndex];

    }

    private Double[] deepCopyCost(Double[] costVector) {
	Double[] temp = new Double[costVector.length];
	for (int i = 0; i < costVector.length; i++) {
	    temp[i] = costVector[i];
	}
	return temp;
    }

}
