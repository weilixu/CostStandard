package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import thermalmoistureprotection.ThermalMoistureProtection;
import thermalmoistureprotection.ThermalMositureProtectionFactory;
import masterformat.api.ComponentFactory;
import eplus.IdfReader;

public class Client {
    private static final String FILE_PATH = "C://Users//Weili//Desktop//New folder//test.idf";

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

    public String findObject() {
	return reader.getValue("Material", "Specific Heat");
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
