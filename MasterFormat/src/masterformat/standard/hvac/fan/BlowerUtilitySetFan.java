package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility ventilating set. This type of fans are self-contained units
 * consisting of a complete fan, motor and drive package. The arrangements offer
 * a compact and economical design.
 * 
 * This type of fan has two drivers option. V-belt drive: In a belt driven
 * configuration, the motor exists independently of the fan blades and at least
 * one belt-sometimes more- connects the motor to the fan's moving parts. This
 * offers greater flexibility in terms of RPM speed and such fans are cheaper
 * than direct drive fans of comparable size. However there is more friction
 * between moving parts which leads to higher maintenance and energy costs
 * 
 * Direct Drive: The fan motor that controls the movement of the fan blades is
 * connected either to a shaft or fan axle. Thus the fan blades will rotate at
 * the same speed as the motor rotates. Unlike belt-drive fans, it has greater
 * efficiency and also less energy loss from friction. However, it has lesser
 * flexibility compared to belt driven fans,and also more expensive.
 * 
 * @author Weili
 *
 */
public class BlowerUtilitySetFan extends AbstractFan {

    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private Double[] flowRateVector = { 0.07, 0.23, 0.92, 1.14, 1.57, 0.38,
	    0.61, 0.94, 1.37 };

    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public BlowerUtilitySetFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233414.107500 Utility Set";
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

	Double[][] costsMatrix = { { 870.0, 160.0, 0.0, 1030.0, 1200.0 },
		{ 1100.0, 177.0, 0.0, 1277.0, 1475.0 },
		{ 1275.0, 213.0, 0.0, 1488.0, 1725.0 },
		{ 2375.0, 233.0, 0.0, 2608.0, 2950.0 },
		{ 2625.0, 340.0, 0.0, 2965.0, 3425.0 },
		{ 980.0, 171.0, 0.0, 1151.0, 1325.0 },
		{ 1025.0, 205.0, 0.0, 1230.0, 1425.0 },
		{ 1225.0, 223.0, 0.0, 1448.0, 1700.0 },
		{ 1650.0, 244.0, 0.0, 1894.0, 2175.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.07 m3/s, 93 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.23 m3/s, 124 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.92 m3/s, 373 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.14 m3/s, 560 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.57 m3/s, 1120 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.38 m3/s, 186 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.61 m3/s, 248 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.94 m3/s, 746 watts");
	optionQuantities.add(0);
	optionLists
		.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 1.37 m3/s, 560 watts");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}

	userInputs.add("OPTION:Drive:Direct Drive");
	userInputs.add("OPTION:Drive:V-belt drive, drive cover, 3 phases");
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperIndex = 0;
	Integer lowerIndex = 0;
	if (drives.equals("Direct Drive")) {
	    if (flowRate <= 0.07) {
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.07 && flowRate <= 0.23) {
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.23 && flowRate <= 0.92) {
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.92 && flowRate <= 1.14) {
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 1.14 && flowRate <= 1.57) {
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 4;
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, grouped";
		fittingFlowRate(upperIndex, lowerIndex);
	    }
	} else if (drives.equals("V-belt drive, drive cover, 3 phases")) {
	    lowerIndex = 5;
	    if (flowRate <= 0.38) {
		upperIndex = 5;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.38 && flowRate <= 0.61) {
		upperIndex = 6;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.61 && flowRate <= 0.94) {
		upperIndex = 7;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.94 && flowRate <= 1.37) {
		upperIndex = 8;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 8;
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases, grouped";
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
