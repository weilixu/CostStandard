package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;
import masterformat.api.MasterFormat;

public class CondenserUnitAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, CondenserUnit> condenserUnitMap;

    private final int stringArraySize = 3;
    private final DecimalFormat df = new DecimalFormat("###.##");

    private final static String singleSpeed = "Coil:Cooling:DX:SingleSpeed";
    private final static String twoSpeed = "Coil:Cooling:DX:TwoSpeed";
    private final static String multiSpeed = "Coil:Cooling:DX:MultiSpeed";
    private final static String variableSpeed = "Coil:Cooling:DX:VariableSpeed";
    private final static String chillerEIR = "Chiller:Electric:EIR";

    protected final int capacityIndex = 0;
    protected final int copIndex = 1;
    protected final int airFlowIndex = 2;

    private static final String[] defaultCostData = { "Unknown", "Ea", "0",
	    "0", "0", "0", "0" };

    private final Integer rowElement = 7;
    
    public CondenserUnitAnalyzer(IdfReader reader, EnergyPlusHTMLParser p){
	this.reader = reader;
	parser = p;
	condenserUnitMap = new HashMap<String, CondenserUnit>();
	processCondenserUnitData();
    }
    
    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForCU() {
	Double totalCUCost = 0.0;
	Set<String> cuList = condenserUnitMap.keySet();
	Iterator<String> cuIterator = cuList.iterator();
	while (cuIterator.hasNext()) {
	    String cu = cuIterator.next();
	    double totalcost = condenserUnitMap.get(cu).getRandomTotalCost();
	    totalCUCost+=totalcost;
	    System.out.println("This "+cu+" unit cost of "+ totalcost+" and the cumulative total is: "+totalCUCost);
	}
	return totalCUCost;
    }
    
    protected String[][] getCostListForCondenserUnit(String condenserName){
	CondenserUnit cu = condenserUnitMap.get(condenserName);
	String[][] costList = new String[1][rowElement];
	
	String generalUnit = cu.getCondenserUnit();
	
	String[] costVector = new String[rowElement];
	Double[] costInfo = cu.getCostInformation();
	if(costInfo!=null){
	    costVector[0] = cu.getCondenserDescription();
	    costVector[1] = generalUnit;
	    
	    for(int j=0; j<costInfo.length; j++){
		costVector[j+2]=df.format(costInfo[j]);
	    }
	    costList[0] = costVector;
	}else{
	    costList[0] = defaultCostData;
	}
	return costList;
    }
    
    protected String[] getCondenserList(){
	Set<String> condensers = condenserUnitMap.keySet();
	String[] condenserArray = new String[condensers.size()];
	Iterator<String> condenserIterator = condensers.iterator();
	
	int counter = 0;
	while(condenserIterator.hasNext() && counter<condensers.size()){
	    condenserArray[counter] = condenserIterator.next();
	    counter++;
	}
	return condenserArray;
    }
    
    protected String getCondenserType(String condenserName){
	return condenserUnitMap.get(condenserName).getType();
    }
    
    protected void setCondenserUnitMasterFormat(String condenserName, MasterFormat mf){
	condenserUnitMap.get(condenserName).setCondenserUnit(mf);
    }
    
    protected CondenserUnit getCondenser(String condenserName){
	return condenserUnitMap.get(condenserName);
    }
    
    protected void setUserInput(HashMap<String, String> map, String condenser){
	CondenserUnit cu = condenserUnitMap.get(condenser);
	cu.setUserInputs(map);
    }
    
    private void processCondenserUnitData(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> singleSpeedList = reader.getObjectList(singleSpeed);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> twoSpeedList = reader.getObjectList(twoSpeed);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> multiSpeedList = reader.getObjectList(multiSpeed);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> variableSpeedList = reader.getObjectList(variableSpeed);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> chillerList = reader.getObjectList(chillerEIR);
	
	if(singleSpeedList!=null){
	    processSingleSpeed(singleSpeedList);
	}
	
	if(twoSpeedList!=null){
	    processTwoSpeed(twoSpeedList);
	}
	
	if(multiSpeedList!=null){
	    processMultiSpeed(multiSpeedList);
	}
	
	if(variableSpeedList!=null){
	    processVariableSpeed(variableSpeedList);
	}
	
	if(chillerList!=null){
	    processAirCoolChiller(chillerList);
	}
    }
    
    private void processSingleSpeed(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> condenserCount = list.get(singleSpeed).keySet();
	Iterator<String> condenserIterator = condenserCount.iterator();
	while(condenserIterator.hasNext()){
	    String count = condenserIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(singleSpeed).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    
	    CondenserUnit cu = new CondenserUnit(singleSpeed,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("Gross Rated Total Cooling Capacity")){
		    cu.setCapacity(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("Gross Rated Cooling COP")){
		    cu.setCOP(vn.getAttribute());
		}else  if(vn.getDescription().equalsIgnoreCase("Rated Air Flow Rate")){
		    cu.setAirFlow(vn.getAttribute());
		}
	    }
	    condenserUnitMap.put(name, cu);
	}
    }
    
    private void processTwoSpeed(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> condenserCount = list.get(twoSpeed).keySet();
	Iterator<String> condenserIterator = condenserCount.iterator();
	while(condenserIterator.hasNext()){
	    String count = condenserIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(twoSpeed).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    
	    CondenserUnit cu = new CondenserUnit(twoSpeed,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("High Speed Gross Rated Total Cooling Capacity")){
		    cu.setCapacity(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("High Speed Gross Rated Cooling COP")){
		    cu.setCOP(vn.getAttribute());
		}else  if(vn.getDescription().equalsIgnoreCase("High Speed Rated Air Flow Rate")){
		    cu.setAirFlow(vn.getAttribute());
		}
	    }
	    condenserUnitMap.put(name, cu);
	}
    }
    
    private void processMultiSpeed(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
 	Set<String> condenserCount = list.get(multiSpeed).keySet();
 	Iterator<String> condenserIterator = condenserCount.iterator();
 	while(condenserIterator.hasNext()){
 	    String count = condenserIterator.next();
 	    ArrayList<ValueNode> tempNodeList = list.get(multiSpeed).get(count);
 	    String name = tempNodeList.get(0).getAttribute();
 	    
 	    CondenserUnit cu = new CondenserUnit(multiSpeed,name);
 	    
 	    for(ValueNode vn: tempNodeList){
 		if(vn.getDescription().equalsIgnoreCase("Speed 1 Gross Rated Total Cooling Capacity")){
 		    cu.setCapacity(vn.getAttribute());
 		}else if(vn.getDescription().equalsIgnoreCase("Speed 1 Gross Rated Cooling COP")){
 		    cu.setCOP(vn.getAttribute());
 		}else  if(vn.getDescription().equalsIgnoreCase("Speed 1 Rated Air Flow Rate")){
 		    cu.setAirFlow(vn.getAttribute());
 		}
 	    }
 	    condenserUnitMap.put(name, cu);
 	}
     }
    
    private void processVariableSpeed(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> condenserCount = list.get(variableSpeed).keySet();
	Iterator<String> condenserIterator = condenserCount.iterator();
	while(condenserIterator.hasNext()){
	    String count = condenserIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(variableSpeed).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    
	    CondenserUnit cu = new CondenserUnit(variableSpeed,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("Speed 1 Reference Unit Gross Rated Total Cooling Capacity")){
		    cu.setCapacity(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("Speed 1 Reference Unit Gross Rated Cooling COP")){
		    cu.setCOP(vn.getAttribute());
		}else  if(vn.getDescription().equalsIgnoreCase("Speed 1 Reference Unit Rated Air Flow Rate")){
		    cu.setAirFlow(vn.getAttribute());
		}
	    }
	    condenserUnitMap.put(name, cu);
	}
    }
    
    private void processAirCoolChiller(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> condenserCount = list.get(chillerEIR).keySet();
	Iterator<String> condenserIterator = condenserCount.iterator();
	while(condenserIterator.hasNext()){
	    String count = condenserIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(chillerEIR).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    //first loop, identify this is an aircool chiller
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("Condenser Type")){
		    if(vn.getAttribute().equalsIgnoreCase("AIRCOOLED")){
			    CondenserUnit cu = new CondenserUnit(chillerEIR,name);
			    //secondloop, extract data
			    for(ValueNode v: tempNodeList){
				if(v.getDescription().equalsIgnoreCase("Reference Capacity")){
				    String capacity = v.getAttribute();
				    if(capacity.equals("autosize")){
					capacity = parser.getCentralPlantSummary(name)[0];
				    }
				    cu.setCapacity(capacity);
				}else if(vn.getDescription().equalsIgnoreCase("Reference COP")){
				    cu.setCOP(v.getAttribute());
				}
			    }
			    condenserUnitMap.put(name, cu);
		    }
		}
	    }  
	}
    }
    
    public class CondenserUnit{
	//indicates the object type in Eplus
	private final String type;
	//indicates the object name in the Name Field
	private final String condenserName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat condenserUnit;
	
	public CondenserUnit(String type, String name){
	    condenserName = name;
	    this.type = type;
	}
	
	public void setCapacity(String capacity){
	    if(capacity.equalsIgnoreCase("autosize")){
		    properties[capacityIndex]="";
	    }else{
		    properties[capacityIndex]=capacity;

	    }
	}
	
	public void setCOP(String cop){
	    properties[copIndex]=cop;
	}
	
	public void setAirFlow(String airflow){
	    if(airflow.equalsIgnoreCase("autosize")){
		    properties[airFlowIndex]="";
	    }else{
		    properties[airFlowIndex]=airflow;
	    }
	}
	
	public String getName(){
	    return condenserName;
	}
	
	public void setCondenserUnit(MasterFormat m){
	    condenserUnit = m;
	    properties = getProperties();
	    condenserUnit.setVariable(properties);
	}
	
	public String getCondenserDescription(){
	    if(condenserUnit==null){
		return condenserName;
	    }
	    return condenserUnit.getDescription();
	}
	
	public String getType(){
	    return type;
	}
	
	public String getCondenserUnit(){
	    if(condenserUnit==null){
		return"";
	    }
	    return condenserUnit.getUnit();
	}
	
	public ArrayList<String> getUserInputs(){
	    return condenserUnit.getUserInputs();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    condenserUnit.setUserInputs(map);
	}
	
	public ArrayList<String> getOptionList(){
	    return condenserUnit.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return condenserUnit.getQuantitiesFromObjects();
	}
	
	public Double[] getCostInformation(){
	    if(condenserUnit == null){
		return null;
	    }
	    
	    try{
		condenserUnit.selectCostVector();
	    }catch(NullPointerException e){
		return null;
	    }
	    
	    if(condenserUnit.getCostVector() == null){
		return null;
	    }
	    
	    return condenserUnit.getCostVector();
	}
	
	public double getRandomTotalCost() {
	    return condenserUnit.randomDrawTotalCost();
	}
	
	@Override
	public CondenserUnit clone(){
	    CondenserUnit cu = new CondenserUnit(this.type,this.condenserName);
	    cu.setCapacity(properties[capacityIndex]);
	    cu.setCOP(properties[copIndex]);
	    cu.setAirFlow(properties[airFlowIndex]);
	    return cu;
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
