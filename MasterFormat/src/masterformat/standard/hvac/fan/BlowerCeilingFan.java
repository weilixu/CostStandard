package masterformat.standard.hvac.fan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class BlowerCeilingFan extends AbstractFan {

    private Double flowRate;
    private boolean speedControl;
    private String mountMethod;
    
    private final Double[] speedControlAddition = {164.0,27.50,0.0,191.50,222.0};
    private final Double[] wallcap = {300.0,28.0,0.0,328.0,375.0};
    private final Double straightfan = 1.1;
    
    
    public BlowerCeilingFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233414.102500 Ceiling Fan";
	
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("SpeedControl")){
		String control = userInputsMap.get(temp);
		if(control.equals("true")){
		    speedControl = true;
		}else{
		    speedControl = false;
		}
	    }else if(temp.equals("Mount")){
		mountMethod = userInputsMap.get(temp);
	    }else if(temp.equals("Flow Rate")){
		flowRate = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    /**
     * Only flow rate can be extracted from this surface properties, the other
     * two attributes are physical design issues, and is currently not included
     * in the data array
     */
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
	Double[][] costsMatrix = { {300.0,51.0,0.0,351.0,410.0},
		{355.0,54.0,0.0,409.0,470.0},
		{450.0,57.0,0.0,507.0,580.0},
		{890.0,64.0,0.0,954.0,1075.0},
		{1225.0,79.0,0.0,1304.0,1475.0},
		{1650.0,93.0,0.0,1743.0,1950.0}};
	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,0.05 m3/s");
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,0.10 m3/s");
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,0.18 m3/s");
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,0.42 m3/s");
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,0.78 m3/s");
	typesOne.add("Ceiling fan, right angle, extra quiet, 25 pa,1.40 m3/s");

	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	}
	
	userInputs.add("OPTION:Mount:None");
	userInputs.add("OPTION:Mount:For wall or roof cap");
	userInputs.add("OPTION:Mount:For straight thru fan");
	userInputs.add("BOOL:SpeedControl:Speed Control Switch");
    }
    
    @Override
    public void selectCostVector() {
	if(flowRate<=0.05){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,0.05 m3/s";
	}else if(flowRate>0.05 && flowRate<=0.10){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,0.10 m3/s";
	}else if(flowRate>0.10 && flowRate<=0.18){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,0.18 m3/s";
	}else if(flowRate>0.18 && flowRate<=0.42){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,0.42 m3/s";
	}else if(flowRate>0.42 && flowRate<=0.78){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,0.78 m3/s";
	}else if(flowRate>0.78){
	    description = "Ceiling fan, right angle, extra quiet, 25 pa,1.40 m3/s";
	}
	costVector = priceData.get(description);

	if(speedControl){
	    addAdditions(speedControlAddition);
	}
	
	if(mountMethod.equals("For wall or roof cap")){
	    addAdditions(wallcap);
	}else if(mountMethod.equals("For straight thru fan")){
	    multiplyMaterial(straightfan);
	}
    }
    
    private void addAdditions(Double[] additions){
	for(int i=0; i<costVector.length; i++){
	    costVector[i] = costVector[i]+additions[i];
	}
    }
    
    private void multiplyMaterial(Double percent){
	costVector[totalIndex] = costVector[totalIndex] - costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex] - costVector[materialIndex];

	costVector[materialIndex] = costVector[materialIndex]*percent;
	
	costVector[totalIndex] = costVector[totalIndex] + costVector[materialIndex];
	costVector[totalOPIndex] = costVector[totalOPIndex] + costVector[materialIndex];
	
    }
}