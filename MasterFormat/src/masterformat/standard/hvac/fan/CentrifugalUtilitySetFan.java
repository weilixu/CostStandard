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
 * 
 * @author Weili
 *
 */
public class CentrifugalUtilitySetFan extends AbstractFan {

    /**
     * have options V-belt drive or Direct Drive
     */
    private String drives;
    private Double flowRate;

    private Double[] flowRateVector = { 0.47, 0.94, 1.89, 3.78, 5.66, 0.57, 0.72, 0.87, 1.03, 1.70, 2.00,
	    2.30};

    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public CentrifugalUtilitySetFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.103500 Centrifugal, airfoil, motor and drive, complete";
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

	Double[][] costsMatrix = { { 1900.0, 410.0, 0.0, 2310.0, 2700.0 },
		{ 2150.0, 510.0, 0.0, 2660.0, 3125.0 },
		{ 2725.0, 570.0, 0.0, 3295.0, 3875.0 },
		{ 4100.0, 730.0, 0.0, 4830.0, 5625.0 },
		{ 5450.0, 1025.0, 0.0, 6475.0, 7550.0 },
		{ 1750.0, 171.0, 0.0, 1921.0, 2175.0 },
		{ 2225.0, 205.0, 0.0, 2430.0, 2750.0 },
		{ 2200.0, 256.0, 0.0, 2456.0, 2825.0 },
		{ 2600.0, 340.0, 0.0, 2940.0, 3400.0 },
		{ 2700.0, 510.0, 0.0, 3210.0, 3750.0 },
		{ 3300.0, 640.0, 0.0, 3940.0, 4600.0 },
		{ 4000.0, 730.0, 0.0, 4730.0, 5500.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Centrifugal, airfoil, motor and drive, complete 0.47m3/s 373Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, airfoil, motor and drive, complete 0.94m3/s 746Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, airfoil, motor and drive, complete 1.89m3/s 2237Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, airfoil, motor and drive, complete 3.78m3/s 5593Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, airfoil, motor and drive, complete 5.66m3/s 7457Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 63Pa, 0.57m3/s 186Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 63Pa, 0.72m3/s 249Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 63Pa, 0.87m3/s 373Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 63Pa, 1.03m3/s 560Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 125Pa, 1.70m3/s 746Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 125Pa, 2.00m3/s 1118Watt");
	optionQuantities.add(0);
	optionLists
		.add("Centrifugal, Utility set, V belt drive, motor 125Pa, 2.30m3/s 1491Watt");
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
	if (drives.equals("Direct dirve")) {
	    if (flowRate <= 0.47) {
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.47 && flowRate <= 0.94) {
		upperIndex = 1;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.94 && flowRate <= 1.89) {
		upperIndex = 2;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 1.89 && flowRate <= 3.78) {
		upperIndex = 3;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 3.78 && flowRate <= 5.66) {
		upperIndex = 4;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 5;
		description = "Centrifugal, airfoil, motor and drive, complete grouped";
		fittingFlowRate(upperIndex, lowerIndex);
	    }
	} else if (drives.equals("V-belt drive")) {
	    lowerIndex = 6;
	    if (flowRate <= 0.57) {
		upperIndex = 6;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);

	    } else if (flowRate > 0.57 && flowRate <= 0.72) {
		upperIndex = 7;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else if (flowRate > 0.72 && flowRate <= 0.87) {
		upperIndex = 8;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);

	    } else if (flowRate > 0.87 && flowRate <= 1.03) {
		upperIndex = 9;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);

	    } else if (flowRate > 1.03 && flowRate <= 1.70) {
		upperIndex = 10;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);

	    } else if (flowRate > 1.70 && flowRate <= 2.00) {
		upperIndex = 11;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);

	    } else if (flowRate > 2.00 && flowRate <= 2.30) {
		upperIndex = 12;
		description = optionLists.get(upperIndex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperIndex);
		optionQuantities.set(upperIndex, i + 1);
	    } else {
		upperIndex = 12;
		description = "Centrifugal, Utility set, V belt drive, motor 125Pa, grouped";
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
