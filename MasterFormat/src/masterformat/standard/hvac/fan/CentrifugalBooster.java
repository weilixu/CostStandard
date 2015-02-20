package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalBooster extends AbstractFan{
    private Double flowRate;
    
    public CentrifugalBooster(){
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233416.100200 In-Line centrifugal, supply or exhaust booster";
    }


    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Flow Rate")){
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
	Double[][] costsMatrix = { {1300.0,340.0,0.0,1640.0,1950.0},
		{1375.0,510.0,0.0,1885.0,2300.0},
		{1500.0,510.0,0.0,2010.0,2425.0},
		{1625.0,1025.0,0.0,2650.0,3350.0},
		{1925.0,1275.0,0.0,3200.0,4075.0},
		{2100.0,1375.0,0.0,3475.0,4400.0}	
	};
	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.24m3/s, 0.25m diameter connection");
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.65m3/s, 0.30m diameter connection");
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.72m3/s, 0.41m diameter connection");
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.21m3/s, 0.46m diameter connection");
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.64m3/s, 0.51m diameter connection");
	typesOne.add("In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 2.40m3/s, 0.51m diameter connection");
	
	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	}
    }
    

    @Override
    public void selectCostVector() {
	if(flowRate<=0.24){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.24m3/s, 0.25m diameter connection";
	}else if(flowRate>0.24 && flowRate<=0.65){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.65m3/s, 0.30m diameter connection";
	}else if(flowRate>0.65 && flowRate<=0.72){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 0.72m3/s, 0.41m diameter connection";
	}else if(flowRate>0.72 && flowRate<=1.21){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.21m3/s, 0.46m diameter connection";
	}else if(flowRate>1.21 && flowRate<=1.64){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 1.64m3/s, 0.51m diameter connection";
	}else if(flowRate>1.64){
	    description = "In-line centrifugal, supply or exhaust booster, aluminum wheel or hub, disconnect switch, 62Pa, 2.40m3/s, 0.51m diameter connection";
	}
	costVector = priceData.get(description);
    }

}
