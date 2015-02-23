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
 * @author Weili
 *
 */
public class BlowerUtilitySetFan extends AbstractFan {

    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private CostMultiRegressionModel regressionDDModel;
    private CostMultiRegressionModel regressionVBModel;

    public BlowerUtilitySetFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233414.107500 Utility Set";
	selected = false;
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

	regressionDDModel = new CostMultiRegressionModel();
	regressionVBModel = new CostMultiRegressionModel();

	Double[] flowRateDDVector = { 0.07, 0.23, 0.92, 1.14, 1.57 };
	Double[] flowRateVBVector = { 0.38, 0.61, 0.94, 1.37 };
	Double[][] costsMatrix = { { 870.0, 160.0, 0.0, 1030.0, 1200.0 },
		{ 1100.0, 177.0, 0.0, 1277.0, 1475.0 },
		{ 1275.0, 213.0, 0.0, 1488.0, 1725.0 },
		{ 2375.0, 233.0, 0.0, 2608.0, 2950.0 },
		{ 2625.0, 340.0, 0.0, 2965.0, 3425.0 },
		{ 980.0, 171.0, 0.0, 1151.0, 1325.0 },
		{ 1025.0, 205.0, 0.0, 1230.0, 1425.0 },
		{ 1225.0, 223.0, 0.0, 1448.0, 1700.0 },
		{ 1650.0, 244.0, 0.0, 1894.0, 2175.0 } };

	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.07 m3/s, 93 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.23 m3/s, 124 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.92 m3/s, 373 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.14 m3/s, 560 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.57 m3/s, 1120 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.38 m3/s, 186 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.61 m3/s, 248 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.94 m3/s, 746 watts");
	typesOne.add("Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 1.37 m3/s, 560 watts");

	int ddCounter=0;
	int vbCounter=0;
	for (int i = 0; i < typesOne.size(); i++) {
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	    if (typesOne.get(i).contains("Direct drive")) {
		regressionDDModel.addMaterialCost(flowRateDDVector[ddCounter],
			costsMatrix[i][materialIndex]);
		regressionDDModel.addLaborCost(flowRateDDVector[ddCounter],
			costsMatrix[i][laborIndex]);
		regressionDDModel.addEquipmentCost(flowRateDDVector[ddCounter],
			costsMatrix[i][equipIndex]);
		regressionDDModel.addTotalCost(flowRateDDVector[ddCounter],
			costsMatrix[i][totalIndex]);
		regressionDDModel.addTotalOPCost(flowRateDDVector[ddCounter],
			costsMatrix[i][totalOPIndex]);
		ddCounter++;
	    } else {
		regressionVBModel.addMaterialCost(flowRateVBVector[vbCounter],
			costsMatrix[i][materialIndex]);
		regressionVBModel.addLaborCost(flowRateVBVector[vbCounter],
			costsMatrix[i][laborIndex]);
		regressionVBModel.addEquipmentCost(flowRateVBVector[vbCounter],
			costsMatrix[i][equipIndex]);
		regressionVBModel.addTotalCost(flowRateVBVector[vbCounter],
			costsMatrix[i][totalIndex]);
		regressionVBModel.addTotalOPCost(flowRateVBVector[vbCounter],
			costsMatrix[i][totalOPIndex]);
		vbCounter++;
	    }
	}

	userInputs.add("OPTION:Drive:Direct Drive");
	userInputs.add("OPTION:Drive:V-belt drive, drive cover, 3 phases");
    }

    @Override
    public void selectCostVector() {
	if (drives.equals("Direct Drive")) {
	    if (flowRate <= 0.07) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.07 m3/s, 93 watts";
		selected = true;
	    } else if (flowRate > 0.07 && flowRate <= 0.23) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.23 m3/s, 124 watts";
		selected = true;

	    } else if (flowRate > 0.23 && flowRate <= 0.92) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, 0.92 m3/s, 373 watts";
		selected = true;

	    } else if (flowRate > 0.92 && flowRate <= 1.14) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.14 m3/s, 560 watts";
		selected = true;

	    } else if (flowRate > 1.14 && flowRate <= 1.57) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, 1.57 m3/s, 1120 watts";
		selected = true;

	    }
	} else if (drives.equals("V-belt drive, drive cover, 3 phases")) {
	    if (flowRate <= 0.38) {
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.38 m3/s, 186 watts";
		selected = true;

	    } else if (flowRate > 0.38 && flowRate <= 0.61) {
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.61 m3/s, 248 watts";
		selected = true;

	    } else if (flowRate > 0.61 && flowRate <= 0.94) {
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 0.94 m3/s, 746 watts";
		selected = true;

	    } else if (flowRate > 0.94 && flowRate <= 1.37) {
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases 1.37 m3/s, 560 watts";
		selected = true;

	    }
	}

	if (selected == false) {
	    if (drives.equals("Direct Drive")) {
		description = "Utility set, steel construction, pedestal, 623Pa, Direct drive, above 1.57 m3/s, above 1120 watts (Predicted)";
		costVector = regressionDDModel.predictCostVector(flowRate);
	    } else {
		description = "Utility set, steel construction, pedestal, 623Pa, V-belt drive, drive cover, 3 phases above 1.37 m3/s, above 560 watts (Predicted)";
		costVector = regressionVBModel.predictCostVector(flowRate);
	    }
	} else {
	    costVector = priceData.get(description);
	}
	selected = false;
    }
}
