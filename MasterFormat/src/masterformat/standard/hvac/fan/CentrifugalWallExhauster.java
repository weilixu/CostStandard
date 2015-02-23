package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.standard.model.CostMultiRegressionModel;

public class CentrifugalWallExhauster extends AbstractFan {
    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private CostMultiRegressionModel regressionModel;

    public CentrifugalWallExhauster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.108500 Wall exhausters";
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
	regressionModel = new CostMultiRegressionModel();
	Double[] flowRateVector = { 0.29, 0.38, 0.39, 0.62, 1.32, 1.76 };
	Double[][] costsMatrix = { { 425.0, 73.0, 0.0, 498.0, 575.0 },
		{ 880.0, 79.0, 0.0, 959.0, 1075.0 },
		{ 1075.0, 85.5, 0.0, 1160.50, 1300.0 },
		{ 1250.0, 85.50, 0.0, 1335.5, 1500.0 },
		{ 1925.0, 114.0, 0.0, 2039.0, 2300.0 },
		{ 2000.0, 128.0, 0.0, 2128.0, 2400.0 } };

	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.29m3/s, 37Watts");
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.38m3/s, 62Watts");
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.39m3/s, 124Watts");
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.62m3/s, 186Watts");
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.32m3/s, 186Watts");
	typesOne.add("Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.76m3/s, 373Watts");

	for (int i = 0; i < typesOne.size(); i++) {
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	    regressionModel.addMaterialCost(flowRateVector[i],
		    costsMatrix[i][materialIndex]);
	    regressionModel.addLaborCost(flowRateVector[i],
		    costsMatrix[i][laborIndex]);
	    regressionModel.addEquipmentCost(flowRateVector[i],
		    costsMatrix[i][equipIndex]);
	    regressionModel.addTotalCost(flowRateVector[i],
		    costsMatrix[i][totalIndex]);
	    regressionModel.addTotalOPCost(flowRateVector[i],
		    costsMatrix[i][totalOPIndex]);
	}

	userInputs.add("OPTION:Drive:Direct drive");
	userInputs.add("OPTION:Drive:V-belt drive");
    }

    @Override
    public void selectCostVector() {
	if (drives.equals("Direct drive")) {
	    if (flowRate <= 0.29) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.29m3/s, 37Watts";
		selected = true;
	    } else if (flowRate > 0.29 && flowRate <= 0.38) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.38m3/s, 62Watts";
		selected = true;

	    } else if (flowRate > 0.38 && flowRate <= 0.39) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.39m3/s, 124Watts";
		selected = true;

	    } else if (flowRate > 0.39) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, Direct drive, 0.62m3/s, 186Watts";
		selected = true;

	    }
	} else if (drives.equals("V-belt drive")) {
	    if (flowRate <= 1.32) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.32m3/s, 186Watts";
		selected = true;

	    } else if (flowRate > 1.32) {
		description = "Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, 1.76m3/s, 373Watts";
		selected = true;

	    }
	}

	if (selected == false) {
	    description = "Wall exhauster, centrifugal, auto damper, 31Pa, V-belt drive 3 phase, above 1.76m3/s, above 373Watts";
	    costVector = regressionCosts();
	} else {
	    costVector = deepCopyCost(priceData.get(description));
	}
	selected = false;
    }

    private Double[] regressionCosts() {
	return regressionModel.predictCostVector(flowRate);
    }
    
    private Double[] deepCopyCost(Double[] costVector){
	Double[] temp = new Double[costVector.length];
	for(int i=0; i<costVector.length; i++){
	    temp[i]= costVector[i];
	}
	return temp;
    }
}
