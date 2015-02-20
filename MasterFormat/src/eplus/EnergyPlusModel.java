package eplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eplus.MaterialAnalyzer.Material;
import masterformat.api.MasterFormat;
import masterformat.listener.CostTableListener;
import masterformat.standard.model.MasterFormatModel;

/**
 * This is the EnergyPlus-Cost mode wrapper model. This class interacts with
 * <link>IdfReader<link>, <link>MasterFormatModel<link>,
 * <link>MaterialAnalyzer<link> etc. together. pass and receive and post process
 * data
 * 
 * @author Weili
 *
 */
public class EnergyPlusModel {

    // models
    private final IdfReader idfDomain;
    private final MasterFormatModel masterformat;
    private MaterialAnalyzer materialModule;
    private BoilerAnalyzer boilerModule;

    // files locations etc.
    private final File eplusFile;
    private final File parentFolder;

    // useful data
    private final String[] domainList = {"Construction","Boiler" };// comboBox
								   // list.
								   // Shows the
								   // category
								   // from
								   // EnergyPlus
								   // economic
								   // model
    private String[][] costData;
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";
    // count the generated idf file
    private Integer generatedCounter = 0;

    // listeners
    private List<CostTableListener> tableListeners;

    public EnergyPlusModel(File file) {
	eplusFile = file;
	parentFolder = eplusFile.getParentFile();
	masterformat = new MasterFormatModel();
	tableListeners = new ArrayList<CostTableListener>();

	idfDomain = new IdfReader();
	idfDomain.setFilePath(eplusFile.getAbsolutePath());
	try {
	    idfDomain.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	setUpMaterialAnalyzer();
	setUpBoilerAnalyzer();
    }

    /**
     * adding listeners from this class
     * 
     * @param ct
     */
    public void addTableListener(CostTableListener ct) {
	tableListeners.add(ct);
    }

    /**
     * get the domain list which indicates the domain items according to
     * EnergyPlus specifications
     * 
     * @return
     */
    public String[] getDomainList() {
	return domainList;
    }

    // Construction domain methods
    /**
     * retrieve the material list from a specific construction This is used for
     * the material domain
     * 
     * @param construction
     * @return
     */
    public ArrayList<Material> getMaterialList(String construction) {
	return materialModule.getMaterialList(construction);
    }

    /**
     * get the construction list found in the EnergyPlus file
     * 
     * @return
     */
    public String[] getConstructionList() {
	return materialModule.getConstructionList();
    }
    
    public String[] getBoilerList(){
	return boilerModule.getBoilerList();
    }

    /**
     * get the material cost data from the material analyzer
     * 
     * @param cons
     * @return
     */
    public String[][] getMaterialTableData(String cons) {
	return materialModule.getCostListForConstruction(cons);
    }

    /**
     * For constructions mapping
     * 
     * @param type
     * @param description
     * @param construction
     * @param index
     */
    public void setConstructionMasterFormat(String type, String description, String item,
	    Integer index) {
	MasterFormat mf = masterformat.getUserInputFromMap(type, description);
	materialModule.getMaterialList(item).get(index).setMaterial(mf);
    }
    
    /**
     * For boilers mapping
     * @param description
     */
    public void setBoilerMasterFormat(String description){
	String type = boilerModule.getBoilerType(description);
	MasterFormat mf = masterformat.getUserInputFromMap("BOILER", type);
	boilerModule.setBoilerMasterFormat(description,mf);
    }

    /**
     * gets the user inputs from the masterformt object
     * 
     * @param construction
     * @param index
     * @return
     */
    public ArrayList<String> getConstructionUserInputs(String construction, Integer index) {
	return materialModule.getMaterialList(construction).get(index)
		.getUserInputs();
    }
    
    public ArrayList<String> getBoilerUserInputs(String boilerName){
	return boilerModule.getBoiler(boilerName).getUserInputs();
    }

    // temp, only has materials
    /**
     * for construction cost vector extraction
     * 
     * @param item
     */
    public void getConstructionCostVector(String item) {
	costData = materialModule.getCostListForConstruction(item);
	updateCostVectorInformation();
    }
    
    /**
     * for boiler cost vector extraction
     * 
     * @param item
     */
    public void getBoilerCostVector(String item) {
	costData = boilerModule.getCostListForBoiler(item);
	updateCostVectorInformation();
    }

    /**
     * set the user inputs to the masterformat item. This method will later be
     * expanded into all the other domains
     * 
     * @param map
     * @param construction
     * @param index
     */
    public void setConstructionUserInput(HashMap<String, String> map, String construction,
	    Integer index) {
	materialModule.setUserInput(map, construction, index);
	getConstructionCostVector(construction);
    }
    
    /**
     * set the user inputs to the masterformat item. This method will later be
     * expanded into all the other domains
     * 
     * @param map
     * @param construction
     * @param index
     */
    public void setBoilerUserInput(HashMap<String, String> map, String boilerName) {
	boilerModule.setUserInput(map, boilerName);
	getBoilerCostVector(boilerName);
    }
    
    
    
    /**
     * add the total cost data to EnergyPlus file
     * @param item
     * @param category
     */
    public void addTotalCostToComponentCost(String item, String category) {
	Integer totalCostIndex = 4;
	Double cost = Double
		.parseDouble(costData[costData.length - 1][totalCostIndex]);

	String[] description = componentCostDescription.split(":");
	if (category.equalsIgnoreCase("CONSTRUCTION")) {
	    String[] value = { item.toUpperCase(), "", category, item, "", "",
		    cost.toString(), "", "", "", "", "", "" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}else if(category.equalsIgnoreCase("BOILER")){
	    String[] value = { item.toUpperCase(), "", "General", item,"", cost.toString(),
		    "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}
    }
    
    /**
     * add the total cost plus overhead and profit to the component cost
     * @param item
     * @param category
     */
    public void addTotalOPCostToComponentCost(String item, String category) {
	Integer totalCostIndex = 5;
	Double cost = Double
		.parseDouble(costData[costData.length - 1][totalCostIndex]);

	String[] description = componentCostDescription.split(":");
	if (category.equalsIgnoreCase("CONSTRUCTION")) {
	    String[] value = { item.toUpperCase(), "", category, item, "", "",
		    cost.toString(), "", "", "", "", "", "" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}else if(category.equalsIgnoreCase("BOILER")){
	    String[] value = { item.toUpperCase(), "", category, item,"", cost.toString(),
		    "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}
    }
    
    /**
     * write out the idf model
     */
    public void writeIdf() {
	idfDomain.WriteIdf(parentFolder.getAbsolutePath(),
		generatedCounter.toString());
	generatedCounter++;
    }

    private void setUpMaterialAnalyzer() {
	materialModule = new MaterialAnalyzer(idfDomain);
    }
    
    private void setUpBoilerAnalyzer(){
	boilerModule = new BoilerAnalyzer(idfDomain);
    }

    private void updateCostVectorInformation() {
	for (CostTableListener ct : tableListeners) {
	    ct.onCostTableUpdated(costData);
	}
    }
}
