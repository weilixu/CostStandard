package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;
import masterformat.api.MasterFormat;

/**
 * Currently, boiler analyzer could extract the following information: Boiler Source Type 
 * (electricity, natural gas, propane gas, gasoline, diesel), boiler capacity and boiler efficiency
 * Above information will be organized in an String[] data structure in the order of:
 * {boilerSourceType,boilerCapacity, boilerEfficiency}
 * @author Weili
 *
 */
public class BoilerAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, BoilerObject> boilerMap;
    
    
    private final int stringArraySize = 3;
    private final DecimalFormat df = new DecimalFormat("###.##");
    
    private final static String steamBoiler = "Boiler:Steam";
    private final static String waterBoiler = "Boiler:HotWater";
    
    protected final int sourceTypeIndex = 0;
    protected final int capacityIndex = 1;
    protected final int efficiencyIndex = 2;
    
    private static final String[] defaultCostData = { "Unknown", "Ea","0", "0", "0",
	    "0", "0" };
    
    private final Integer rowElement = 7;
    
    public BoilerAnalyzer(IdfReader reader, EnergyPlusHTMLParser p){
	this.reader = reader;
	parser = p;
	boilerMap = new HashMap<String, BoilerObject>();
	processBoilerData();
    }
    
    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForBoiler() {
	Double totalBoilerCost = 0.0;
	Set<String> boilerList = boilerMap.keySet();
	Iterator<String> boilerIterator = boilerList.iterator();
	while (boilerIterator.hasNext()) {
	    String boiler = boilerIterator.next();
	    double totalcost = boilerMap.get(boiler).getRandomTotalCost();
	    totalBoilerCost+=totalcost;
	    System.out.println("This "+boiler+" unit cost of "+ totalcost+" and the cumulative total is: "+totalBoilerCost);
	}
	return totalBoilerCost;
    }
    
    protected String[][] getCostListForBoiler(String boiler){
	BoilerObject boilerObject = boilerMap.get(boiler);
	String[][] costList = new String[1][rowElement];
	
	String generalUnit = boilerObject.getBoilerUnit();
	

	    String[] costVector = new String[rowElement];
	    Double[] costInfo = boilerObject.getCostInformation();
	    if(costInfo!=null){
		//the first element in a vector is the boiler name;
		costVector[0] = boilerObject.getBoilerDescription();
		costVector[1] = generalUnit;

		for(int j=0; j<costInfo.length;j++){
		    costVector[j+2] = df.format(costInfo[j]);
		}
		costList[0] = costVector;
	    }else{
		costList[0] = defaultCostData;
	    }
	return costList;
    }
    
    protected String[] getBoilerList(){
	Set<String> boilers = boilerMap.keySet();
	String[] boilerArray = new String[boilers.size()];
	Iterator<String> boilerIterator = boilers.iterator();
	
	int counter = 0;
	while(boilerIterator.hasNext()&& counter<boilers.size()){
	    boilerArray[counter] = boilerIterator.next();
	    counter++;
	}
	return boilerArray;
    }
    
    protected String getBoilerType(String boilerName){
	return boilerMap.get(boilerName).getType();
    }
    
    protected void setBoilerMasterFormat(String boilerName, MasterFormat mf){
	boilerMap.get(boilerName).setBoiler(mf);
    }
    
    protected BoilerObject getBoiler(String boilerName){
	return boilerMap.get(boilerName);
    }
    
    /**
     * set the user input which is later be used to select the cost
     * 
     * @param map
     * @param construction
     * @param index
     */
    protected void setUserInput(HashMap<String, String> map, String boiler) {
	BoilerObject b = boilerMap.get(boiler);
	b.setUserInputs(map);
    }
    
    private void processBoilerData(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> hotwaterBoilerList = reader.getObjectList(waterBoiler);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> steamBoilerList = reader.getObjectList(steamBoiler);
	
	if(hotwaterBoilerList!=null){
	    processWaterBoiler(hotwaterBoilerList);
	}
	
	if(steamBoilerList!=null){
	    processSteamBoiler(steamBoilerList);
	}
    }
    
    private void processWaterBoiler(HashMap<String, HashMap<String, ArrayList<ValueNode>>> boilerList){

	Set<String> boilerCount = boilerList.get(waterBoiler)
		.keySet();
	Iterator<String> boilerIterator = boilerCount.iterator();
	while(boilerIterator.hasNext()){
	    String count = boilerIterator.next();
	    ArrayList<ValueNode> tempNodeList = boilerList.get(waterBoiler).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    BoilerObject bo = new BoilerObject(waterBoiler,name);

	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Fuel Type")){
		    bo.setSourceType(vn.getAttribute());
		}else if(vn.getDescription().equals("Nominal Capacity")){
		    String capacity = vn.getAttribute();
		    if(capacity.equalsIgnoreCase("autosize")){
			capacity = parser.getCentralPlantSummary(name)[0];
		    }
		    bo.setCapacity(capacity);
		}else if(vn.getDescription().equals("Nominal Thermal Efficiency")){
		    bo.setEfficiency(vn.getAttribute());
		}
	    }
	    boilerMap.put(name, bo);
	}
    }
    
    private void processSteamBoiler(HashMap<String, HashMap<String, ArrayList<ValueNode>>> boilerList){

	Set<String> boilerCount = boilerList.get(steamBoiler)
		.keySet();
	Iterator<String> boilerIterator = boilerCount.iterator();
	while(boilerIterator.hasNext()){
	    String count = boilerIterator.next();
	    ArrayList<ValueNode> tempNodeList = boilerList.get(steamBoiler).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    
	    BoilerObject bo = new BoilerObject(steamBoiler,name);
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Fuel Type")){
		    bo.setSourceType(vn.getAttribute());
		}else if(vn.getDescription().equals("Nominal Capacity")){
		    bo.setCapacity(vn.getAttribute());
		}else if(vn.getDescription().equals("Theoretical Efficiency")){
		    bo.setEfficiency(vn.getAttribute());
		}
	    }
	    boilerMap.put(name, bo);
	}
    }
    
    /**
     * 
     * @author Weili
     *
     */
    public class BoilerObject{
	private final String type;
	private final String boilerName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat boiler;
	
	public BoilerObject(String type,String name){
	    boilerName = name;
	    this.type = type;
	}
	
	public void setSourceType(String st){
	    properties[sourceTypeIndex] = st;
	}
	
	public void setCapacity(String cp){
	    if(cp.equals("autosize")){
		properties[capacityIndex] = "";
	    }else{
		properties[capacityIndex]=cp;
	    }
	}
	
	public void setEfficiency(String e){
	    properties[efficiencyIndex] = e;
	}
	
	public String getName(){
	    return boilerName;
	}
	
	public void setBoiler(MasterFormat m){
	    boiler = m;
	    properties = getProperties();
	    boiler.setVariable(properties);
	}
	
	public String getBoilerDescription(){
	    if(boiler==null){
		return boilerName;
	    }
	    return boiler.getDescription();
	}
	
	public String getType(){
	    return type;
	}
	
	public String getBoilerUnit(){
	    if(boiler==null){
		return"";
	    }
	    return boiler.getUnit();
	}
	
	public ArrayList<String> getUserInputs(){
	    return boiler.getUserInputs();
	}
	
	public ArrayList<String> getOptionList(){
	    return boiler.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return boiler.getQuantitiesFromObjects();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    boiler.setUserInputs(map);
	}
	
	public Double[] getCostInformation(){
	    if(boiler == null){
		return null;
	    }
	    
	    try{
		boiler.selectCostVector();
	    }catch(NullPointerException e){
		return null;
	    }
	    
	    if(boiler.getCostVector() == null){
		return null;
	    }
	    
	    return boiler.getCostVector();
	}
	
	public double getRandomTotalCost() {
	    return boiler.randomDrawTotalCost();
	}
	
	@Override
	public BoilerObject clone(){
	    BoilerObject bo = new BoilerObject(this.type,this.boilerName);
	    bo.setSourceType(properties[sourceTypeIndex]);
	    bo.setCapacity(properties[capacityIndex]);
	    bo.setEfficiency(properties[efficiencyIndex]);
	    return bo;
	}
	
	private String[] getProperties() {
	    for (int i = 0; i < properties.length; i++) {
		if (properties[i] == null) {
		    properties[i] = "";
		}
	    }
	    return properties;
	}
	
    }
}
