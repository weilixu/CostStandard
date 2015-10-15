package eplus.HVAC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.EnergyPlusBuildingForHVACSystems;
import baseline.generator.EplusObject;
import baseline.generator.KeyValuePair;
import baseline.idfdata.ThermalZone;

public class VRFSystem implements HVACSystem{
    
    // recording all the required data for HVAC system type 3
    private HashMap<String, ArrayList<EplusObject>> objectLists;

    // building object contains building information and energyplus data
    private EnergyPlusBuildingForHVACSystems building;
    
    public VRFSystem(HashMap<String, ArrayList<EplusObject>> objects,
	    EnergyPlusBuildingForHVACSystems bldg){
	objectLists = objects;
	building = bldg;
	processSystems();
    }
    
    @Override
    public HashMap<String, ArrayList<EplusObject>> getSystemData() {
	return objectLists;
    }
    
    private void processSystems(){
	ArrayList<EplusObject> supplySideSystem = new ArrayList<EplusObject>();
	ArrayList<EplusObject> demandSideSystem = new ArrayList<EplusObject>();
	/*
	 * supply side relates to the condenser, typically one condenser link to 
	 * several indoor terminal units
	 */
	ArrayList<EplusObject> supplySideSystemTemplate = objectLists
		.get("Supply Side System");
	/*
	 * demand side relates to the evaporator, one zone has one evaporator 
	 */
	ArrayList<EplusObject> demandSideSystemTemplate = objectLists
		.get("Demand Side System");
	HashMap<String, ArrayList<ThermalZone>> vrfMap = building.getZoneSystemMap();
	Set<String> vrfMapSet = vrfMap.keySet();
	Iterator<String> vrfMapIterator = vrfMapSet.iterator();
	
	//every zone has one set of the system
	while(vrfMapIterator.hasNext()){
	    String vrfCondenser = vrfMapIterator.next();
	    ArrayList<ThermalZone> zones = vrfMap.get(vrfCondenser);
	    ArrayList<String> zoneNames = new ArrayList<String>();
	    for(ThermalZone zone: zones){
		demandSideSystem.addAll(processDemandTemp(zone.getFullName(),
			demandSideSystemTemplate));
		zoneNames.add(zone.getFullName());

	    }
	    supplySideSystem.addAll(processSupplyTemp(vrfCondenser,
			supplySideSystemTemplate,zoneNames));
	    
	}
	objectLists.put("Supply Side System", supplySideSystem);
	objectLists.put("Demand Side System", demandSideSystem);
    }
    
    /**
     * process the HVAC supply air side system
     * @param zone
     * @param supplySideSystemTemplate
     * @return
     */
    private ArrayList<EplusObject> processSupplyTemp(String condenser,
	    ArrayList<EplusObject> supplySideSystemTemplate,ArrayList<String> zoneNameList) {
	ArrayList<EplusObject> supplyTemp = new ArrayList<EplusObject>();
	for (EplusObject eo : supplySideSystemTemplate) {
	    EplusObject temp = eo.clone();

	    /*
	     * replace the special characters that contains floors
	     */
	    if (temp.hasSpecialCharacters()) {
		temp.replaceSpecialCharacters(condenser);
	    }
	    
	    if(temp.getObjectName().equalsIgnoreCase("ZoneTerminalUnitList")){
		for(String s: zoneNameList){
		    String vrfTUName = s+ " VRF Unit";
		    KeyValuePair ternminalUnitPair = new KeyValuePair("Zone Terminal Unit Name",vrfTUName);
		    temp.addField(ternminalUnitPair);
		}
	    }

	    supplyTemp.add(temp);
	}
	return supplyTemp;
    }
    
    /**
     * process the demand side system
     * 
     * @param zone
     * @param zoneTemp
     * @return
     */
    private ArrayList<EplusObject> processDemandTemp(String zone,
	    ArrayList<EplusObject> zoneTemp) {
	ArrayList<EplusObject> demandTemp = new ArrayList<EplusObject>();
	for (EplusObject eo : zoneTemp) {
	    EplusObject temp = eo.clone();
	    // check special characters to avoid useless loop inside the replace
	    // special characters
	    if (temp.hasSpecialCharacters()) {
		temp.replaceSpecialCharacters(zone);
	    }
	    demandTemp.add(temp);
	}
	return demandTemp;
    }
}
