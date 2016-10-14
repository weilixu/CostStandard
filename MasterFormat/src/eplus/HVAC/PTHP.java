package eplus.HVAC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import baseline.idfdata.EplusObject;
import baseline.idfdata.thermalzone.ThermalZone;
import baseline.log.GenerationLog;
import eplus.EnergyPlusBuildingForHVACSystems;

public class PTHP implements HVACSystem{
    // recording all the required data for PTHP
    private HashMap<String, ArrayList<EplusObject>> objectLists;
    
    // building object contains building information and energyplus data
    private EnergyPlusBuildingForHVACSystems buildings;
    
    
    private static final String sizingTable = "Component Sizing Summary%Coil:Cooling:DX:SingleSpeed";
    private static final String TAG = "tableID";
    private int numberOfSystem;
    
    
    public PTHP(HashMap<String, ArrayList<EplusObject>>objects, EnergyPlusBuildingForHVACSystems bldg){
	objectLists = objects;
	buildings = bldg;
	
	processSystems();
    }

    @Override
    public HashMap<String, ArrayList<EplusObject>> getSystemData() {
	return objectLists;
    }

    @Override
    public double getTotalLoad(Document doc) {
	Elements coilList = doc.getElementsByAttributeValue(TAG, sizingTable).get(0).getElementsByTag("td");
	Double load = 0.0;
	for(int i=0; i<coilList.size();i++){
	    if(coilList.get(i).text().contains("Cooling Coil")){
		load = load + Double.parseDouble(coilList.get(i+2).text());
	    }
	}
	return load;
    }

    @Override
    public String getSystemName() {
	return "PTHP";
    }

    @Override
    public int getNumberOfSupplySystem() {
	return numberOfSystem;
    }

    @Override
    public int getNumberOfDemandSystem() {
	return numberOfSystem;
    }
    
    private void processSystems(){
	ArrayList<EplusObject> supplySideSystem = new ArrayList<EplusObject>();
	ArrayList<EplusObject> demandSideSystem = new ArrayList<EplusObject>();
	
	ArrayList<EplusObject> supplySideSystemTemplate = objectLists.get("Supply Side System");
	ArrayList<EplusObject> demandSideSystemTemplate = objectLists.get("Demand Side System");
	
	HashMap<String, ArrayList<ThermalZone>> zoneMap = buildings.getZoneSystemMap();
	
	Set<String> zoneMapSet = zoneMap.keySet();
	Iterator<String> zoneMapIterator = zoneMapSet.iterator();
	
	int roomCounter = 0;
	while(zoneMapIterator.hasNext()){
	    String zoneName = zoneMapIterator.next();
	    //first process the demand side system and their connection to supply side system
	    ArrayList<ThermalZone> zones = zoneMap.get(zoneName);
	    for(ThermalZone zone:zones){
		roomCounter++;
		demandSideSystem.addAll(processDemandTemp(zone.getFullName(),demandSideSystemTemplate));
		// add the outdoor air object for demand zone
		demandSideSystem.add(zone.getOutdoorAirObject());
		supplySideSystem.addAll(processSupplyTemp(zone.getFullName(), supplySideSystemTemplate));
	    }
	}
	numberOfSystem = roomCounter;
	objectLists.put("Supply Side System", supplySideSystem);
	objectLists.put("Demand Side System", demandSideSystem);
    }
    
    /**
     * process the HVAC supply air side system
     * 
     * @param zone
     * @param supplySideSystemTemplate
     * @return
     */
    private ArrayList<EplusObject> processSupplyTemp(String zone,
	    ArrayList<EplusObject> supplySideSystemTemplate) {
	
	ArrayList<EplusObject> supplyTemp = new ArrayList<EplusObject>();
	for (EplusObject eo : supplySideSystemTemplate) {
	    EplusObject temp = eo.clone();

	    /*
	     * replace the special characters that contains floors
	     */
	    if (temp.hasSpecialCharacters()) {
		temp.replaceSpecialCharacters(zone);
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
