package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import masterformat.api.ComponentFactory;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtectionFactory;
import eplus.IdfReader;

public class Client {
    private static final String FILE_PATH = "C://Users//Weili//Desktop//New folder//JIH_ProposedCase.idf";

    private IdfReader reader;

    public Client() {
	reader = new IdfReader();
	reader.setFilePath(FILE_PATH);
	try {
	    reader.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public void findZoneHVAC(){
	String[] zoneList = reader.getListValue("Zone","Name");
	for(String zone: zoneList){
	    String ahu = findObject(zone.toUpperCase());
	    System.out.println(zone+ " " + ahu);
	}
    }

    public String findObject(String zoneName) {
	String inletNode = reader.getValue("ZoneHVAC:EquipmentConnections",zoneName,"Zone Air Inlet Node or NodeList Name");
	//System.out.println(inletNode);
	String equipNode = reader.getValue("AirTerminal:SingleDuct:VAV:Reheat",inletNode, "Air Inlet Node Name");
	//System.out.println(equipNode);
	String supplyName = reader.getValue("AirLoopHVAC:ZoneSplitter",equipNode, "Name");
	//System.out.println(supplyName);
	String pathInlet = reader.getValue("AirLoopHVAC:SupplyPath",supplyName, "Supply Air Path Inlet Node Name");
	//System.out.println(pathInlet);
	String ahu = reader.getValue("AirLoopHVAC",pathInlet, "Name");
	return ahu;
    }

    public String[] createSurfaceProperties() {
	// 0.To create a string array, we need surface object zone object,
	// construction object and material object
	String zoneName = reader.getValue("BuildingSurface:Detailed",
		"Zone Name");
	String construction = reader.getValue("BuildingSurface:Detailed",
		"Construction Name");
	String material = reader.getValue("Construction", construction,
		"Outside Layer");

	String floorArea = reader.getValue("Zone", zoneName, "Floor Area");
	String height = reader.getValue("Zone", zoneName, "Ceiling Height");
	String resistance = reader.getValue("Material:NoMass", material,
		"Thermal Resistance");

	String[] temp = { floorArea, height, "", "", "", "", resistance };
	return temp;
    }
    
    public static void main(String[] args){
	Client core = new Client();
	core.findZoneHVAC();
    }

//    public static void main(String[] args) {
//	HashMap<String, String> userInputs = new HashMap<String, String>();
//	Client core = new Client();
//	ComponentFactory productFactory = new ThermalMositureProtectionFactory();
//	ThermalMoistureProtection insulation = productFactory
//		.getThermalMoistureProtection("Rigid insulation");
//	insulation.setVariable(core.createSurfaceProperties());
//	if (insulation.isUserInputsRequired()) {
//	    ArrayList<String> temp = insulation.getUserInputs();
//	}
//	userInputs.put("InsulationType","Fiberglass, 0.085#/m3, Foil faced");
//	userInputs.put("Thickness","0.04");
//
//	insulation.setUserInputs(userInputs);
//	insulation.selectCostVector();
//	
//	Double total = insulation.getTotalPrice();
//
//	System.out.println(total);
//    }

}
