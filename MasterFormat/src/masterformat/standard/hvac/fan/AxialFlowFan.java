package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AxialFlowFan extends AbstractFan {

    private Double flowRate;

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

	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Air conditioning and process air handling, Vaneaxial, low pressure, 1 m3/s, 372 Watts");
	typesOne.add("Air conditioning and process air handling, Vaneaxial, low pressure, 1.9 m3/s, 746 Watts");
	typesOne.add("Air conditioning and process air handling, Vaneaxial, low pressure, 3.8 m3/s, 1491 Watts");
	
	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	}
    }
    
    @Override
    public void selectCostVector() {
	if(flowRate<=1.0){
	    description = "Air conditioning and process air handling, Vaneaxial, low pressure, 1 m3/s, 372 Watts";
	}else if(flowRate>1.0 && flowRate<=1.9){
	    description = "Air conditioning and process air handling, Vaneaxial, low pressure, 1.9 m3/s, 746 Watts";
	}else if(flowRate>1.9){
	    description = "Air conditioning and process air handling, Vaneaxial, low pressure, 3.8 m3/s, 1491 Watts";
	}
	costVector = priceData.get(description);
    }
}