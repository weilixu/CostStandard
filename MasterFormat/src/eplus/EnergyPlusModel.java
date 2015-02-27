package eplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eplus.MaterialAnalyzer.Material;
import eplus.htmlparser.EnergyPlusHTMLParser;
import masterformat.api.MasterFormat;
import masterformat.listener.BoilerListener;
import masterformat.listener.CostTableListener;
import masterformat.listener.FurnaceListener;
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
    private EnergyPlusHTMLParser htmlParser;
    private final MasterFormatModel masterformat;
    private MaterialAnalyzer materialModule;
    private BoilerAnalyzer boilerModule;
    private FanAnalyzer fanModule;
    private CondenserUnitAnalyzer condenserUnitModule;
    private FurnaceAnalyzer furnaceModule;

    // files locations etc.
    private final File eplusFile;
    private final File parentFolder;

    // useful data
    private final String[] domainList = { "Construction", "Boiler", "Fan",
	    "Condenser Unit", "Furnace" };// comboBox

    private String[][] costData;
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";
    // count the generated idf file
    private Integer generatedCounter = 0;

    private String[] selectionOptions;
    private Integer[] selectionOptionQuantities;

    // listeners
    private List<CostTableListener> tableListeners;
    private List<FurnaceListener> furnaceListeners;
    private List<BoilerListener> boilerListeners;

    public EnergyPlusModel(File file) {
	eplusFile = file;
	parentFolder = eplusFile.getParentFile();
	masterformat = new MasterFormatModel();

	tableListeners = new ArrayList<CostTableListener>();
	furnaceListeners = new ArrayList<FurnaceListener>();
	boilerListeners = new ArrayList<BoilerListener>();

	idfDomain = new IdfReader();
	idfDomain.setFilePath(eplusFile.getAbsolutePath());
	try {
	    idfDomain.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	processHTML();
	setUpMaterialAnalyzer();
	setUpBoilerAnalyzer();
	setUpFanAnalyzer();
	setUpCondenserUnitAnalyzer();
	setUpFurnaceAnalyzer();
    }

    /**
     * adding listeners from this class
     * 
     * @param ct
     */
    public void addTableListener(CostTableListener ct) {
	tableListeners.add(ct);
    }

    public void addFurnaceListener(FurnaceListener fl) {
	furnaceListeners.add(fl);
    }
    
    public void addBoilerListener(BoilerListener bl){
	boilerListeners.add(bl);
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

    public String[] getOptionList() {
	return selectionOptions;
    }

    public Integer[] getQuantityList() {
	return selectionOptionQuantities;
    }

    // Below are all the methods to retrieve the object list from Energyplus
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

    public String[] getBoilerList() {
	return boilerModule.getBoilerList();
    }

    public String[] getFanList() {
	return fanModule.getFanList();
    }

    public String[] getCondenserUnitList() {
	return condenserUnitModule.getCondenserList();
    }

    public String[] getFurnaceList() {
	return furnaceModule.getFurnaceList();
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

    // All the methods to Map the masterformat to EnergyPlus domain

    /**
     * For constructions mapping
     * 
     * @param type
     * @param description
     * @param construction
     * @param index
     */
    public void setConstructionMasterFormat(String type, String description,
	    String item, Integer index) {
	MasterFormat mf = masterformat.getUserInputFromMap(type, description);
	materialModule.getMaterialList(item).get(index).setMaterial(mf);
    }

    /**
     * For boilers mapping
     * 
     * @param description
     */
    public void setBoilerMasterFormat(String description) {
	String type = boilerModule.getBoilerType(description);
	MasterFormat mf = masterformat.getUserInputFromMap("BOILER", type);
	boilerModule.setBoilerMasterFormat(description, mf);
    }

    /**
     * For fans mapping
     * 
     * @param description
     *            : type of the object in energyplus
     */
    public void setFanMasterFormat(String fanName, String description) {
	MasterFormat mf = masterformat.getUserInputFromMap("FAN", description);
	fanModule.setFanMasterFormat(fanName, mf);
    }

    public void setCondenserMasterFormat(String condenserName,
	    String description) {
	MasterFormat mf = masterformat.getUserInputFromMap("CONDENSERUNIT",
		description);
	condenserUnitModule.setCondenserUnitMasterFormat(condenserName, mf);
    }

    public void setFurnaceMasterFormat(String furnaceName) {
	String type = furnaceModule.getFurnaceType(furnaceName);
	MasterFormat mf = masterformat.getUserInputFromMap("FURNACE", type);
	furnaceModule.setFurnaceMasterFormat(furnaceName, mf);
    }

    // All the methods to retrieve user inputs from the mapping results
    /**
     * gets the user inputs from the masterformt object
     * 
     * @param construction
     * @param index
     * @return
     */
    public ArrayList<String> getConstructionUserInputs(String construction,
	    Integer index) {
	return materialModule.getMaterialList(construction).get(index)
		.getUserInputs();
    }

    public ArrayList<String> getBoilerUserInputs(String boilerName) {
	return boilerModule.getBoiler(boilerName).getUserInputs();
    }

    public ArrayList<String> getFanUserInputs(String fanName) {
	return fanModule.getFan(fanName).getUserInputs();
    }

    public ArrayList<String> getCondenserUnitInputs(String condenserName) {
	return condenserUnitModule.getCondenser(condenserName).getUserInputs();
    }

    public ArrayList<String> getFurnaceInputs(String furnaceName) {
	return furnaceModule.getFurnace(furnaceName).getUserInputs();
    }

    // All the methods to extract the cost vector from the masterformat
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
	updateBoilerQuantities(item);
    }

    /**
     * for fan cost vector extraction
     * 
     * @param item
     */
    public void getFanCostVector(String item) {
	costData = fanModule.getCostListForFan(item);
	updateCostVectorInformation();
    }

    public void getCondenserUnitCostVector(String item) {
	costData = condenserUnitModule.getCostListForCondenserUnit(item);
	updateCostVectorInformation();
    }

    public void getFurnaceCostVector(String item) {
	costData = furnaceModule.getCostListForFurnace(item);
	updateCostVectorInformation();
	//System.out.println("here? "+item);
	updateFurnaceQuantities(item);
    }

    /**
     * 
     * @param fanName
     */
    public void getBoilerOptionList(String boilerName){
	ArrayList<String> list = boilerModule.getBoiler(boilerName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }
    
    public void getBoilerOptionQuantities(String boilerName){
	ArrayList<Integer> list = boilerModule.getBoiler(boilerName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }
    
    // All the methods to get the options under one category and quantities
    public void getFanOptionList(String fanName) {
	System.out.println(fanName);
	ArrayList<String> list = fanModule.getFan(fanName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }

    // All the methods to get the options quantities under one category
    public void getFanOptionQuantities(String fanName) {
	ArrayList<Integer> list = fanModule.getFan(fanName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }

    public void getCondenserUnitOptionList(String condenserName) {
	ArrayList<String> list = condenserUnitModule
		.getCondenser(condenserName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }

    // All the methods to get the options quantities under one category
    public void getCondenserUnitOptionQuantities(String condenserName) {
	ArrayList<Integer> list = condenserUnitModule.getCondenser(
		condenserName).getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }

    public void getFurnaceOptionList(String furnaceName) {
	ArrayList<String> list = furnaceModule.getFurnace(furnaceName)
		.getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }

    public void getFurnaceOptionQuantities(String furnaceName) {
	ArrayList<Integer> list = furnaceModule.getFurnace(furnaceName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	// System.out.println(Arrays.toString(temp));
	selectionOptionQuantities = temp;
    }

    // All the methods that feed back user inputs to the masterformat for the
    // cost mapping
    /**
     * set the user inputs to the masterformat item. This method will later be
     * expanded into all the other domains
     * 
     * @param map
     * @param construction
     * @param index
     */
    public void setConstructionUserInput(HashMap<String, String> map,
	    String construction, Integer index) {
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
    public void setBoilerUserInput(HashMap<String, String> map,
	    String boilerName) {
	boilerModule.setUserInput(map, boilerName);
	getBoilerCostVector(boilerName);
    }

    public void setFanUserInput(HashMap<String, String> map, String fanName) {
	fanModule.setUserInput(map, fanName);
	getFanCostVector(fanName);
    }

    public void setCondenserUnitUserInput(HashMap<String, String> map,
	    String condenserName) {
	condenserUnitModule.setUserInput(map, condenserName);
	getCondenserUnitCostVector(condenserName);
    }

    public void setFurnaceUserInput(HashMap<String, String> map,
	    String furnaceName) {
	furnaceModule.setUserInput(map, furnaceName);
	getFurnaceCostVector(furnaceName);
    }

    // All the method that retrieve the cost information and put it down to
    // EnergyPlus Component line object
    /**
     * add the total cost data to EnergyPlus file
     * 
     * @param item
     * @param category
     */
    public void addTotalCostToComponentCost(String item, String category) {
	Integer totalCostIndex = 5;
	Double cost = Double
		.parseDouble(costData[costData.length - 1][totalCostIndex]);

	String[] description = componentCostDescription.split(":");
	if (category.equalsIgnoreCase("CONSTRUCTION")) {
	    String[] value = { item.toUpperCase(), "", category, item, "", "",
		    cost.toString(), "", "", "", "", "", "" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("BOILER")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("FAN")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("Condenser Unit")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("FURNACE")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}
    }

    /**
     * add the total cost plus overhead and profit to the component cost
     * 
     * @param item
     * @param category
     */
    public void addTotalOPCostToComponentCost(String item, String category) {
	Integer totalCostIndex = 6;
	Double cost = Double
		.parseDouble(costData[costData.length - 1][totalCostIndex]);

	String[] description = componentCostDescription.split(":");
	if (category.equalsIgnoreCase("CONSTRUCTION")) {
	    String[] value = { item.toUpperCase(), "", category, item, "", "",
		    cost.toString(), "", "", "", "", "", "" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("BOILER")) {
	    String[] value = { item.toUpperCase(), "", category, item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("FAN")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("CONDENSERUNIT")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("FURNACE")) {
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
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

    private void processHTML() {
	File[] fList = parentFolder.listFiles();
	for (File file : fList) {
	    if (file.isFile()) {
		if (file.getName().endsWith(".html")) {
		    htmlParser = new EnergyPlusHTMLParser(file);
		}
	    }
	}
    }

    private void setUpMaterialAnalyzer() {
	materialModule = new MaterialAnalyzer(idfDomain);
    }

    private void setUpBoilerAnalyzer() {
	boilerModule = new BoilerAnalyzer(idfDomain, htmlParser);
    }

    private void setUpFanAnalyzer() {
	fanModule = new FanAnalyzer(idfDomain, htmlParser);
    }

    private void setUpCondenserUnitAnalyzer() {
	condenserUnitModule = new CondenserUnitAnalyzer(idfDomain,htmlParser);
    }

    private void setUpFurnaceAnalyzer() {
	furnaceModule = new FurnaceAnalyzer(idfDomain, htmlParser);
    }

    private void updateCostVectorInformation() {
	for (CostTableListener ct : tableListeners) {
	    ct.onCostTableUpdated(costData);
	}
    }
    
    private void updateBoilerQuantities(String name){
	for (BoilerListener bl : boilerListeners) {
	    System.out.println(name+" "+bl.getName());
	    if (name.equals(bl.getName())) {
		bl.onQuanatitiesUpdates();
	    }
	}
    }

    private void updateFurnaceQuantities(String name) {
	for (FurnaceListener fl : furnaceListeners) {
	    if (name.equals(fl.getName())) {
		fl.onQuanatitiesUpdates();
	    }
	}
    }
}
