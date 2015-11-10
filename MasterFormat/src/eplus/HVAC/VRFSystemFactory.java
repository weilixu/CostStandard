package eplus.HVAC;

import java.util.ArrayList;
import java.util.HashMap;

import eplus.EnergyPlusBuildingForHVACSystems;
import baseline.generator.EplusObject;
import baseline.hvac.SystemParser;

public class VRFSystemFactory {
    //extract the template system
    private final SystemParser system = new SystemParser("VariableRefrigerantFlow Template");
    private HashMap<String,ArrayList<EplusObject>> systemObjects;
    private VRFSystem vrfSys;
        
    public VRFSystemFactory(EnergyPlusBuildingForHVACSystems building){
	systemObjects = new HashMap<String, ArrayList<EplusObject>>();
	
	processTemplate();
	//System.out.println("Receive the template, Size: " + systemObjects.size());
	vrfSys = new VRFSystem(systemObjects, building);
    }
    
    public HVACSystem getSystem(){
	return vrfSys;
    }
    
    /**
     * Separate the three systems into three data lists.
     */
    private void processTemplate(){
	ArrayList<EplusObject> template = system.getSystem();
	for(EplusObject eo: template){
	    if(eo.getReference().equals("Supply Side System")){
		if(!systemObjects.containsKey("Supply Side System")){
		    systemObjects.put("Supply Side System", new ArrayList<EplusObject>());
		}
		systemObjects.get("Supply Side System").add(eo);
	    }else if(eo.getReference().equals("Demand Side System")){
		if(!systemObjects.containsKey("Demand Side System")){
		    systemObjects.put("Demand Side System", new ArrayList<EplusObject>());
		}
		systemObjects.get("Demand Side System").add(eo);
	    }else if(eo.getReference().equals("Plant")){
		if(!systemObjects.containsKey("Plant")){
		    systemObjects.put("Plant", new ArrayList<EplusObject>());
		}
		systemObjects.get("Plant").add(eo);
	    }else if(eo.getReference().equals("Schedule")){
		if(!systemObjects.containsKey("Schedule")){
		    systemObjects.put("Schedule", new ArrayList<EplusObject>());
		}
		systemObjects.get("Schedule").add(eo);
	    }else if(eo.getReference().equals("Global")){
		if(!systemObjects.containsKey("Global")){
		    systemObjects.put("Global", new ArrayList<EplusObject>());
		}
		systemObjects.get("Global").add(eo);
	    }
	}
    }
}
