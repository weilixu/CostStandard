package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.standard.model.CostMultiRegressionModel;

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

    private Double[] flowRateDDVector = { 0.47, 0.94, 1.89, 3.78, 5.66 };
    private Double[] flowRateVBVector = { 0.57, 0.72, 0.87, 1.03, 1.70, 2.00,
	    2.30 };

    private final int VBIndex = 5;

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
	if (drives.equals("Direct dirve")) {
	    if (flowRate <= 0.47) {
		description = "Centrifugal, airfoil, motor and drive, complete 0.47m3/s 373Watt";
		costVector = deepCopyCost(priceData.get(description));
	    } else if (flowRate > 0.47 && flowRate <= 0.94) {
		description = "Centrifugal, airfoil, motor and drive, complete 0.94m3/s 746Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 0.94 && flowRate <= 1.89) {
		description = "Centrifugal, airfoil, motor and drive, complete 1.89m3/s 2237Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 1.89 && flowRate <= 3.78) {
		description = "Centrifugal, airfoil, motor and drive, complete 3.78m3/s 5593Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 3.78 && flowRate <= 5.66) {
		description = "Centrifugal, airfoil, motor and drive, complete 5.66m3/s 7457Watt";
		costVector = deepCopyCost(priceData.get(description));
	    } else {
		description = "Centrifugal, airfoil, motor and drive, complete grouped";
		fittingFlowRate(flowRateDDVector, true);
	    }
	} else if (drives.equals("V-belt drive")) {
	    if (flowRate <= 0.57) {
		description = "Centrifugal, Utility set, V belt drive, motor 63Pa, 0.57m3/s 186Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 0.57 && flowRate <= 0.72) {
		description = "Centrifugal, Utility set, V belt drive, motor 63Pa, 0.72m3/s 249Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 0.72 && flowRate <= 0.87) {
		description = "Centrifugal, Utility set, V belt drive, motor 63Pa, 0.87m3/s 373Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 0.87 && flowRate <= 1.03) {
		description = "Centrifugal, Utility set, V belt drive, motor 63Pa, 1.03m3/s 560Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 1.03 && flowRate <= 1.70) {
		description = "Centrifugal, Utility set, V belt drive, motor 125Pa, 1.70m3/s 746Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 1.70 && flowRate <= 2.00) {
		description = "Centrifugal, Utility set, V belt drive, motor 125Pa, 2.00m3/s 1118Watt";
		costVector = deepCopyCost(priceData.get(description));

	    } else if (flowRate > 2.00 && flowRate <= 2.30) {
		description = "Centrifugal, Utility set, V belt drive, motor 125Pa, 2.30m3/s 1491Watt";
		costVector = deepCopyCost(priceData.get(description));
	    } else {
		description = "Centrifugal, Utility set, V belt drive, motor 125Pa, grouped";
		fittingFlowRate(flowRateVBVector, false);
	    }
	}
    }

    private void fittingFlowRate(Double[] flowList, boolean DD) {
	setToZero();
	// shows the best fit capacity
	Double fittedFlowRate = 0.0;
	// shows the total capacity added
	Double totalFlowRate = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalFlowRate < flowRate) {
	    fittedFlowRate = findFittedFlowRate(totalFlowRate, flowList, DD);
	    totalFlowRate += fittedFlowRate;
	}
    }

    private Double findFittedFlowRate(Double total, Double[] flowRateList, boolean DD) {
	// the difference between capacity and total capacity
	Double temp = flowRate;
	Double fittedFlow = 0.0;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = 0; i < flowRateList.length; i++) {
	    Double residual = Math.abs(flowRate - total - flowRateList[i]);
	    if (residual < temp) {
		temp = residual;
		criticalIndex = i;
		fittedFlow = flowRateList[i];
	    }
	}
	// add to the cost vector
	if(!DD){
	    criticalIndex = criticalIndex+VBIndex;
	}
	Double[] itemCost = priceData.get(optionLists.get(criticalIndex));
	for (int j = 0; j < costVector.length; j++) {
	    costVector[j] += itemCost[j];
	}
	Integer q = optionQuantities.get(criticalIndex) + 1;
	optionQuantities.set(criticalIndex, q);

	return fittedFlow;
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
