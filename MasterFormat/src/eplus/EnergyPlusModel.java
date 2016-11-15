package eplus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import baseline.util.BaselineUtils;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIMutationAdaptive;
import jmetal.metaheuristics.nsgaII.pNSGAII;
import jmetal.metaheuristics.nsgaII.pNSGAIIAdaptive;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import jmetal.util.offspring.BitFlipMutationOffspring;
import jmetal.util.offspring.DifferentialEvolutionOffspring;
import jmetal.util.offspring.Offspring;
import jmetal.util.offspring.SinglePointOffSpring;
import jmetal.util.parallel.IParallelEvaluator;
import jmetal.util.parallel.MultithreadedEvaluator;
import eplus.MaterialAnalyzer.Material;
import eplus.construction.BuildingComponent;
import eplus.construction.ExteriorWall;
import eplus.construction.HVACSimple;
import eplus.construction.Lighting;
import eplus.construction.Roof;
import eplus.construction.Window;
import eplus.htmlparser.EnergyPlusHTMLParser;
import eplus.htmlparser.ZoneHTMLParser;
import eplus.optimization.OPT1;
import eplus.optimization.OPT2;
import eplus.optimization.OPT3;
import eplus.optimization.OPT4;
import eplus.optimization.OPT5;
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
    private MasterFormatModel masterformat;
    private MaterialAnalyzer materialModule;
    private BoilerAnalyzer boilerModule;
    private FanAnalyzer fanModule;
    private CondenserUnitAnalyzer condenserUnitModule;
    private FurnaceAnalyzer furnaceModule;
    private UnitaryHVACAnalyzer unitaryModule;
    private ConvectionUnitAnalyzer unitModule;
    private PumpAnalyzer pumpModule;
    private ElectricalAnalyzer electricalModule;
    private TransparentMaterialAnalyzer transparentModule;

    // files locations etc.
    private final File eplusFile;
    private final File parentFolder;
    private File outputFile;
    private final EnergyPlusBuildingForHVACSystems bldg;

    // useful data
    private final String[] domainList = { "Opaque Construction", "Transparent Construction","Boiler", "Fan",
	    "Condenser Unit", "Furnace","Pump","Unitary System","Convection Unit","Lights" };// comboBox

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
	try {
	    masterformat = new MasterFormatModel();
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

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
	bldg = new EnergyPlusBuildingForHVACSystems(idfDomain);
	processHTML();
	setUpMaterialAnalyzer();
	setUpBoilerAnalyzer();
	setUpFanAnalyzer();
	setUpCondenserUnitAnalyzer();
	setUpFurnaceAnalyzer();
	setUpPumpAnalyzer();
	setUpUnitaryAnalyzer();
	setUpConvectionUnitAnalyzer();
	setUpElectricalAnalyzer();
	setUpOpeningAnazlyer();
    }
    
    public double calculateBudget(){
	double total = 0.0;
	try{
	    FileWriter writer = new FileWriter("C:\\Users\\Weili\\Desktop\\test.csv");
	    writer.append("Construction");
	    writer.append(",");
	    writer.append("Electric");
	    writer.append(",");
	    writer.append("Fan");
	    writer.append(",");
	    writer.append("Boiler");
	    writer.append(",");
	    writer.append("Pump");
	    writer.append(",");
	    writer.append("Furnace");
	    writer.append(",");
	    writer.append("CU");
	    writer.append(",");
	    writer.append("ConvectionUnit");
	    writer.append(",");
	    writer.append("Windows");
	    writer.append("\n");
	
	for(int i=0; i<1000; i++){
		double construction = materialModule.getTotalCostForConstruction();
		writer.append(construction+"");
		    writer.append(",");
		double electric = electricalModule.getTotalCostForLighting();
		writer.append(electric+"");
		    writer.append(",");

		double fan = fanModule.getTotalCostForFan();
		writer.append(fan+"");
		    writer.append(",");

		double boiler = boilerModule.getTotalCostForBoiler();
		writer.append(boiler+"");
		    writer.append(",");

		double pump = pumpModule.getTotalCostForPump();
		writer.append(pump+"");
		    writer.append(",");

		double furnace = furnaceModule.getTotalCostForFurnace();
		writer.append(furnace+"");
		    writer.append(",");

		double cu = condenserUnitModule.getTotalCostForCU();
		writer.append(cu+"");
		    writer.append(",");

		double convecunit = unitModule.getTotalCostForConvectionUnit();
		writer.append(convecunit+"");
		    writer.append(",");

		double window = transparentModule.getTotalCostForEnvelope();
		writer.append(window+"");
		    writer.append("\n");

		total = construction+electric+fan+boiler+pump+furnace+cu+convecunit+window;
		

	}
	writer.flush();
	writer.close();
	}catch(IOException e){
	    e.printStackTrace();
	}
	return total;
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
    
    public String[] getPumpList(){
	return pumpModule.getPumpList();
    }
    
    public String[] getUnitaryList(){
	return unitaryModule.getUnitaryList();
    }
    
    public String[] getConvectionUnitList(){
	return unitModule.getConvectionUnitList();
    }
    
    public String[] getElectricalList(){
	return electricalModule.getElectricList();
    }
    
    public String[] getTransparentEnvelopeList(){
	return transparentModule.getTransparentEnvelopeList();
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
     * @throws Exception 
     */
    public void setConstructionMasterFormat(String type, String description,
	    String item, Integer index) throws Exception {
	MasterFormat mf = masterformat.getUserInputFromMap(type, description);
	materialModule.getMaterialList(item).get(index).setMaterial(mf);
    }

    /**
     * For boilers mapping
     * 
     * @param description
     * @throws Exception 
     */
    public void setBoilerMasterFormat(String description) throws Exception {
	String type = boilerModule.getBoilerType(description);
	MasterFormat mf = masterformat.getUserInputFromMap("BOILER", type);
	boilerModule.setBoilerMasterFormat(description, mf);
    }

    /**
     * For fans mapping
     * 
     * @param description
     *            : type of the object in energyplus
     * @throws Exception 
     */
    public void setFanMasterFormat(String fanName, String description) throws Exception {
	MasterFormat mf = masterformat.getUserInputFromMap("FAN", description);
	fanModule.setFanMasterFormat(fanName, mf);
    }

    public void setCondenserMasterFormat(String condenserName,
	    String description) throws Exception {
	MasterFormat mf = masterformat.getUserInputFromMap("CONDENSERUNIT",
		description);
	condenserUnitModule.setCondenserUnitMasterFormat(condenserName, mf);
    }

    public void setFurnaceMasterFormat(String furnaceName) throws Exception {
	String type = furnaceModule.getFurnaceType(furnaceName);
	MasterFormat mf = masterformat.getUserInputFromMap("FURNACE", type);
	furnaceModule.setFurnaceMasterFormat(furnaceName, mf);
    }
    
    public void setPumpMasterFormat(String pumpName,String description) throws Exception{
	MasterFormat mf = masterformat.getUserInputFromMap("Pump", description);
	pumpModule.setPumpMasterFormat(pumpName, mf);
    }
    
    public void setUnitaryMasterFormat(String unitaryName, String description) throws Exception{
	MasterFormat mf = masterformat.getUserInputFromMap("UnitaryHVAC", description);
	//System.out.println(mf==null);
	unitaryModule.setUnitaryMasterFormat(unitaryName, mf);
    }
    
    public void setConvectionUnitMasterFormat(String unitName, String description) throws Exception{
	MasterFormat mf = masterformat.getUserInputFromMap("ConvectionUnit", description);
	unitModule.setConvectionUnitMasterFormat(unitName, mf);
    }
    
    public void setElectricalMasterFormat(String electric, String description) throws Exception{
	MasterFormat mf = masterformat.getUserInputFromMap("Electrical", description);
	electricalModule.setElectricMasterFormat(electric, mf);
    }
    
    public void setTransparentMaterialMasterFormat(String cons, String description)throws Exception{
	MasterFormat mf = masterformat.getUserInputFromMap("Openings", description);
	transparentModule.setOpeningMaterFormat(cons, mf);
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
    
    public ArrayList<String> getPumpUserInputs(String pumpName) {
	return pumpModule.getPump(pumpName).getUserInputs();
    }
    
    public ArrayList<String> getUnitaryUserInputs(String unitaryName){
	return unitaryModule.getUnitary(unitaryName).getUserInputs();
    }
    
    public ArrayList<String> getConvectionUserInputs(String unitName){
	return unitModule.getConvectionUnit(unitName).getUserInputs();
    }
    
    public ArrayList<String> getElectricalUserInputs(String electricName){
	return electricalModule.getElectric(electricName).getUserInputs();
    }
    
    public ArrayList<String> getOpeningsUserInputs(String openings){
	return transparentModule.getEnvelope(openings).getUserInputs();
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
    
    public void getPumpCostVector(String item){
	costData = pumpModule.getCostListForPump(item);
	updateCostVectorInformation();
    }
    
    public void getUnitaryCostVector(String item){
	costData = unitaryModule.getCostListForUnitary(item);
	updateCostVectorInformation();
    }
    
    public void getConvectionUnitCostVector(String item){
	costData = unitModule.getCostListForConvectionUnit(item);
	updateCostVectorInformation();
    }
    
    public void getElectricalCostVector(String item){
	costData = electricalModule.getCostListForElectric(item);
	updateCostVectorInformation();
    }
    
    public void getOpeningsCostVector(String item){
	costData = transparentModule.getCostListForTransparentEnvelope(item);
	updateCostVectorInformation();
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
    
    public void getPumpOptionList(String pumpName) {
	ArrayList<String> list = pumpModule.getPump(pumpName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }

    // All the methods to get the options quantities under one category
    public void getPumpOptionQuantities(String pumpName) {
	ArrayList<Integer> list = pumpModule.getPump(pumpName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }
    
    
    public void getUnitaryOptionList(String unitaryName) {
	ArrayList<String> list = unitaryModule.getUnitary(unitaryName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }

    // All the methods to get the options quantities under one category
    public void getUnitaryOptionQuantities(String unitaryName) {
	ArrayList<Integer> list = unitaryModule.getUnitary(unitaryName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }
    
    public void getConvectionUnitOptionList(String unitName){
	ArrayList<String> list = unitModule.getConvectionUnit(unitName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }
    
    // All the methods to get the options quantities under one category
    public void getConvectionUnitOptionQuantities(String unitName) {
	ArrayList<Integer> list = unitModule.getConvectionUnit(unitName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }
    
    public void getElectricalOptionList(String electricName){
	ArrayList<String> list = electricalModule.getElectric(electricName).getOptionList();
	String[] temp = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }
    
    // All the methods to get the options quantities under one category
    public void getElectricalOptionQuantities(String electricName) {
	ArrayList<Integer> list = electricalModule.getElectric(electricName)
		.getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    temp[i] = list.get(i);
	}
	selectionOptionQuantities = temp;
    }
    
    public void getOpeningsOptionList(String openings){
	ArrayList<String> list = transparentModule.getEnvelope(openings).getOptionList();
	String[] temp = new String[list.size()];
	for(int i=0; i<list.size(); i++){
	    temp[i] = list.get(i);
	}
	selectionOptions = temp;
    }
    
    public void getOpeningsOptionQuantities(String openings){
	ArrayList<Integer> list = transparentModule.getEnvelope(openings).getOptionQuantities();
	Integer[] temp = new Integer[list.size()];
	for(int i=0; i<list.size(); i++){
	    temp[i] = list.get(i);
	}
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
    
    public void setPumpUserInput(HashMap<String, String> map, String pumpName) {
	pumpModule.setUserInput(map, pumpName);
	getPumpCostVector(pumpName);
    }
    
    public void setUnitaryUserInput(HashMap<String, String> map, String unitaryName){
	unitaryModule.setUserInput(map, unitaryName);
	getUnitaryCostVector(unitaryName);
    }
    
    public void setConvectionUnitUserInput(HashMap<String, String> map, String unitName){
	unitModule.setUserInput(map, unitName);
	getConvectionUnitCostVector(unitName);
    }
    
    public void setElectricalUserInput(HashMap<String, String> map, String electricName){
	electricalModule.setUserInput(map, electricName);
	getElectricalCostVector(electricName);
    }
    
    public void setOpeningsUserInput(HashMap<String, String> map, String openings){
	transparentModule.setUserInput(map, openings);
	getOpeningsCostVector(openings);
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
	if (category.equalsIgnoreCase("OPAQUE CONSTRUCTION")) {
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
	} else if (category.equalsIgnoreCase("PUMP")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("UNITARYHVAC")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if(category.equalsIgnoreCase("CONVECTIONUNIT")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if(category.equalsIgnoreCase("LIGHTS")){
	    String[] value = { item.toUpperCase(), "", "Lights", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	}else if(category.equalsIgnoreCase("EQUIPMENT")){
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
	} else if (category.equalsIgnoreCase("PUMP")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if (category.equalsIgnoreCase("UNITARYHVAC")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if(category.equalsIgnoreCase("CONVECTIONUNIT")){
	    String[] value = { item.toUpperCase(), "", "General", item, "",
		    cost.toString(), "", "", "", "", "", "", "1" };
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value,
		    description);
	} else if(category.equalsIgnoreCase("EQUIPMENT")){
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
    
    /*
     * Optimization Related Functions
     */
   public void BudgetEUIOptimization() throws JMException, ClassNotFoundException{
       Operator  crossover ; // Crossover operator
       Operator  mutation  ; // Mutation operator
       Operator  selection ; // Selection operator
       HashMap<String, Double>  parameters ; // Operator parameters

//       ArrayList<BuildingComponent> componentList = new ArrayList<BuildingComponent>();
//       ExteriorWall ew = new ExteriorWall();
//       Roof rf = new Roof();
//       Lighting lt = new Lighting();
//       Window wd = new Window();
//       HVACSimple hvac = new HVACSimple(bldg);
//       componentList.add(ew);
//       componentList.add(wd);
//       componentList.add(rf);
//       componentList.add(lt);
//       componentList.add(hvac);
       int realSimuN = 3;
       int circleDivider = 20;
       int pop = 30;
       int ammMaxEvaluation = 2490;
       int regularEvaluation = 900;
       //Problem problem = new OPT2(bldg,idfDomain,parentFolder);
       //System.out.println(parentFolder.getAbsolutePath());
       
       Problem problem = new OPT5(bldg,idfDomain,parentFolder,realSimuN,circleDivider,pop);
       int threads = 6;
       IParallelEvaluator parallelEvaluator = new MultithreadedEvaluator(threads);
       //Algorithm algorithm = new pNSGAII(problem, parallelEvaluator);
       Algorithm algorithm = new pNSGAIIAdaptive(problem, parallelEvaluator,realSimuN,circleDivider); // adaptive nsgaII
       //Algorithm algorithm = new NSGAII(problem);
       //Algorithm algorithm = new NSGAIIMutationAdaptive(problem, parallelEvaluator); //mutation adaptive
       
       /*Algorithm parameters */
       algorithm.setInputParameter("populationSize", pop);
       algorithm.setInputParameter("maxEvaluations", ammMaxEvaluation);
       
//       // Mutation and Crossover for Real codification 
       parameters = new HashMap<String, Double>() ;
       parameters.put("realProbability", 0.9);
       parameters.put("intProbability", 0.9);
       parameters.put("distributionIndex", 20.0) ;
       crossover = CrossoverFactory.getCrossoverOperator("MixedSBXSinglePointCrossover", parameters);                   

       parameters = new HashMap<String, Double>() ;
       parameters.put("realProbability", 0.5) ;
       parameters.put("intProbability", 0.5);
       parameters.put("distributionIndex", 20.0) ;
       mutation = MutationFactory.getMutationOperator("MixedBitFlipPolynomialMutation", parameters);                    

       // Selection Operator 
       parameters = null ;
       selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);                           

       // Add the operators to the algorithm
       algorithm.addOperator("crossover",crossover);
       algorithm.addOperator("mutation",mutation);
       algorithm.addOperator("selection",selection);
       
       //adaptive nsga ii operation
       //Offspring[] getOffspring = new Offspring[2];
       //getOffspring[0] = new SinglePointOffSpring(0.9,20.0);
       //getOffspring[1] = new BitFlipMutationOffspring(1.0/problem.getNumberOfVariables(),20.0);
      //algorithm.setInputParameter("offspringsCreators", getOffspring);
       
       // Execute the Algorithm
       long initTime = System.currentTimeMillis();
       SolutionSet population = algorithm.execute();
       long estimatedTime = System.currentTimeMillis() - initTime;
       System.out.println("Total execution time: " + estimatedTime);

	/* Log messages */
	System.out.println("Objectives values have been writen to file FUN");
	population.printObjectivesToFile("FUN");
	System.out.println("Variables values have been writen to file VAR");
	population.printVariablesToFile("VAR");
	bldg.writeOutResults();
   }

    private void processHTML() {
	File[] fList = parentFolder.listFiles();
	for (File file : fList) {
	    if (file.isFile()) {
		String fileName = eplusFile.getName().substring(0,eplusFile.getName().indexOf("."))+"Table";
		String htmlFileName = file.getName().substring(0,file.getName().indexOf("."));
		if (fileName.equals(htmlFileName) && file.getName().endsWith(".html")) {
		    outputFile = file;
		    ZoneHTMLParser.processOutputs(outputFile);
		    htmlParser = new EnergyPlusHTMLParser(file);
		    break;
		}
	    }
	}
	bldg.processOutputs(outputFile);
    }

    private void setUpMaterialAnalyzer() {
	materialModule = new MaterialAnalyzer(idfDomain, htmlParser);
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
    
    private void setUpPumpAnalyzer(){
	pumpModule = new PumpAnalyzer(idfDomain, htmlParser);
    }
    
    private void setUpUnitaryAnalyzer(){
	unitaryModule = new UnitaryHVACAnalyzer(idfDomain, htmlParser);
    }
    
    private void setUpConvectionUnitAnalyzer(){
	unitModule = new ConvectionUnitAnalyzer(idfDomain,htmlParser);
    }
    
    private void setUpElectricalAnalyzer(){
	electricalModule = new ElectricalAnalyzer(idfDomain,htmlParser);
    }
    
    private void setUpOpeningAnazlyer(){
	transparentModule = new TransparentMaterialAnalyzer(idfDomain, htmlParser);
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
