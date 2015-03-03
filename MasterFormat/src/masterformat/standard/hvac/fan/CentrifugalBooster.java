package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalBooster extends AbstractFan {

    private Double flowRate;
    
    private Double[] flowRateList = { 0.24, 0.65, 0.72, 1.21, 1.64, 2.40 };
    private static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};


    public CentrifugalBooster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233416.100200 In-Line centrifugal, supply or exhaust booster";
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

	Double[][] costsMatrix = { { 1300.0, 340.0, 0.0, 1640.0, 1950.0 },
		{ 1375.0, 510.0, 0.0, 1885.0, 2300.0 },
		{ 1500.0, 510.0, 0.0, 2010.0, 2425.0 },
		{ 1625.0, 1025.0, 0.0, 2650.0, 3350.0 },
		{ 1925.0, 1275.0, 0.0, 3200.0, 4075.0 },
		{ 2100.0, 1375.0, 0.0, 3475.0, 4400.0 } };
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.24m3/s, 0.25m diameter connection");
	optionQuantities.add(0);
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.65m3/s, 0.30m diameter connection");
	optionQuantities.add(0);
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.72m3/s, 0.41m diameter connection");
	optionQuantities.add(0);
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.21m3/s, 0.46m diameter connection");
	optionQuantities.add(0);
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.64m3/s, 0.51m diameter connection");
	optionQuantities.add(0);
	optionLists.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 2.40m3/s, 0.51m diameter connection");
	optionQuantities.add(0);

	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer index = 0;
	if (flowRate <= 0.24) {
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 0.24 && flowRate <= 0.65) {
	    index = 1;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 0.65 && flowRate <= 0.72) {
	    index = 2;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 0.72 && flowRate <= 1.21) {
	    index = 3;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 1.21 && flowRate <= 1.64) {
	    index = 4;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else if (flowRate > 1.64 && flowRate<=2.40) {
	    index = 5;
	    description = optionLists.get(index);
	    costVector = deepCopyCost(priceData.get(description));
	    Integer i = optionQuantities.get(index);
	    optionQuantities.set(index, i + 1);
	} else {
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, grouped";
	    fittingFlowRate();
	}
    }
    
    private void fittingFlowRate(){
	setToZero();
	//shows the best fit capacity
	Double fittedFlowRate=0.0;
	//shows the total capacity added
	Double totalFlowRate=0.0;
	costVector=deepCopyCost(Default_Cost_Vector);
	
	while(totalFlowRate<flowRate){
	    fittedFlowRate = findFittedFlowRate(totalFlowRate);
	    totalFlowRate+=fittedFlowRate;
	}
    }
    
    private Double findFittedFlowRate(Double total){
  	//the difference between capacity and total capacity
  	Double temp = flowRate;
  	//index shows the current best fit capacity
  	int criticalIndex = 0;
  	
  	for(int i=0; i<flowRateList.length; i++){
  	    Double residual = Math.abs(flowRate-total-flowRateList[i]);
  	    if(residual<temp){
  		temp = residual;
  		criticalIndex = i;
  	    }
  	}
  	//add to the cost vector
  	Double[] itemCost = priceData.get(optionLists.get(criticalIndex));
  	for(int j=0; j<costVector.length; j++){
  	    costVector[j]+=itemCost[j];
  	}
  	Integer q = optionQuantities.get(criticalIndex)+1;
  	optionQuantities.set(criticalIndex, q);
  	
  	return flowRateList[criticalIndex];
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
