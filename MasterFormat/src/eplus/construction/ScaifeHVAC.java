package eplus.construction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class ScaifeHVAC extends AbstractMasterFormatComponent
	implements BuildingComponent {

    private String[] selectedComponents = null;
    private String[] objectList;

    private Double systemCost = 0.0;

    private static final String FILE_NAME = "HVACObjects_csl.txt";

    public ScaifeHVAC() {
	selectedComponents = new String[3];
	selectedComponents[0] = "District";
	selectedComponents[1] = "VAV";
	selectedComponents[2] = "DOASVRF";
    }

    @Override
    public String getName() {
	return "ScaifeHVAC";
    }

    @Override
    public String[] getListAvailableComponent() {
	return selectedComponents;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {
	// TODO Auto-generated method stub

    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public String[] getSelectedComponentsForRetrofit() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	// TODO Auto-generated method stub
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	System.out.println(component);
	if (!component.equals("District")) {

	    // delete the old system
	    try {
		removeHVAC(reader);
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }

	    // insert the other system
	    IdfReader systemReader = new IdfReader();
	    if (component.equals("VAV")) {
		systemReader.setFilePath(
			"E:\\02_Weili\\01_Projects\\07_Toshiba\\Year 3\\Optimization\\VAVChiller.idf");
	    } else {
		systemReader.setFilePath(
			"E:\\02_Weili\\01_Projects\\07_Toshiba\\Year 3\\Optimization\\DOASVRF.idf");
	    }

	    try {
		systemReader.readEplusFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    HashMap<String, HashMap<String, ArrayList<ValueNode>>> systemMap = systemReader
		    .getMap();
	    Iterator<String> systemItr = systemMap.keySet().iterator();
	    while (systemItr.hasNext()) {
		String systemObj = systemItr.next();
		// System.out.println(systemObj);
		HashMap<String, ArrayList<ValueNode>> subMap = systemMap
			.get(systemObj);
		reader.writeInObjects(systemObj, subMap);
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	Double load = 0.0;
	Double systemUnitCost = 1233.23;
	Elements loadEquipments = doc.getElementsByAttributeValue("tableID",
		"Component Sizing Summary%Coil:Cooling:Water");
	Elements districCool = doc.getElementsByAttributeValue("tableID",
		"Component Sizing Summary%DistrictCooling");
	if(!districCool.isEmpty()){
	    systemUnitCost = 1011.12;
	}
	
	if(loadEquipments.size()==0){
	    systemUnitCost = 1541.53;
	    loadEquipments = doc.getElementsByAttributeValue("tableID", "Component Sizing Summary%AirConditioner:VariableRefrigerantFlow");
	}
	
	Elements loadList = loadEquipments.get(0).getElementsByTag("td");
	
	for(int i = 0; i < loadList.size(); i++){
	    if(loadList.get(i).text().contains("VRF OUTDOOR UNIT")){
		load = load + Double.parseDouble(loadList.get(i+1).text());
	    }else if(loadList.get(i).text().contains("PURCHASED COOLING")){
		load = load + Double.parseDouble(loadList.get(i + 1).text());
	    }else if(loadList.get(i).text().contains("AHU COOLING COIL")){
		load = load + Double.parseDouble(loadList.get(i + 1).text());
	    }
	}

	return load * systemUnitCost / 1000;
    }

    @Override
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// TODO Auto-generated method stub

    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub

    }

    public void removeHVAC(IdfReader eplusFile) throws IOException {
	processObjectLists();
	for (String s : objectList) {
	    eplusFile.removeEnergyPlusObject(s);
	}
    }

    // HVAC objects list is read from local list file
    private void processObjectLists() throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));

	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
		sb.append(line);
		sb.append("%");
		line = br.readLine();
	    }
	    objectList = sb.toString().split("%");
	} finally {
	    br.close();
	}
    }
}
