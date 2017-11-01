package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;

public class PumpAnalyzer {

    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, Pump> pumpMap;

    // size shows the data string size in pump segment
    private final int stringArraySize = 3;
    private final DecimalFormat df = new DecimalFormat("###.##");

    // energyplus objects (pump:variablespeed:condensate are not included)
    private final static String constant = "Pump:ConstantSpeed";
    private final static String variable = "Pump:VariableSpeed";
    private final static String headeredConstant = "HeaderedPumps:ConstantSpeed";
    private final static String headeredVariable = "HeaderedPumps:VariableSpeed";

    // the standard data format for mapping EnergyPlus model and MasterFormat
    protected final int powerIndex = 0;
    protected final int headIndex = 1;
    protected final int flowRateIndex = 2;

    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public PumpAnalyzer(IdfReader reader, EnergyPlusHTMLParser p) {
	this.reader = reader;
	parser = p;
	pumpMap = new HashMap<String, Pump>();
	
	processPumpRawDatafromPumps();
    }
    
    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForPump() {
	Double totalPumpCost = 0.0;
	Set<String> pumpList = pumpMap.keySet();
	Iterator<String> pumpIterator = pumpList.iterator();
	while (pumpIterator.hasNext()) {
	    String pump = pumpIterator.next();
	    double totalcost = pumpMap.get(pump).getRandomTotalCost();
	    totalPumpCost+=totalcost;
	    System.out.println("This "+pump+" unit cost of "+ totalcost+" and the cumulative total is: "+totalPumpCost);
	}
	return totalPumpCost;
    }
    
    protected String[][] getCostListForPump(String fan){
	Pump p = pumpMap.get(fan);	
	String[][] costList = new String[1][rowElement];
	
	String generalUnit = p.getPumpUnit();
	
	String[] costVector = new String[rowElement];
	Double[] costInfo = p.getCostInformation();
	if(costInfo!=null){
	    //the first element in a vector is the fan name;
	    
	    costVector[0] = p.getPumpDescription();
	    costVector[1] = generalUnit;
	    for(int j=0; j<costInfo.length; j++){
		costVector[j+2] = df.format(costInfo[j]);
	    }
	    costList[0] = costVector;
	}else{
	    costList[0] = defaultCostData;
	}
	return costList;
    }
    
    protected String[] getPumpList(){
 	Set<String> pumps = pumpMap.keySet();
 	String[] pumpArray = new String[pumps.size()];
 	Iterator<String> pumpIterator = pumps.iterator();
 	
 	int counter = 0;
 	while(pumpIterator.hasNext() && counter<pumps.size()){
 	    pumpArray[counter]=pumpIterator.next();
 	    counter++;
 	}
 	return pumpArray;
     }
     
     protected String getPumpType(String pumpName){
 	return pumpMap.get(pumpName).getPumpType();
     }
     
     protected void setPumpMasterFormat(String pumpName, MasterFormat mf){
 	pumpMap.get(pumpName).setPump(mf);
     }
     
     protected Pump getPump(String pumpName){
 	return pumpMap.get(pumpName);
     }
     
     protected void setUserInput(HashMap<String, String> map,String pumpName){
 	Pump p = pumpMap.get(pumpName);
 	p.setUserInputs(map);
     }
    
    private void processPumpRawDatafromPumps(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constantList = reader.getObjectList(constant);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> variableList = reader.getObjectList(variable);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> headeredConstantList = reader.getObjectList(headeredConstant);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> headeredVariableList = reader.getObjectList(headeredVariable);
	
	if(constantList!=null){
	    processConstantPump(constantList);
	}
	
	if(variableList!=null){
	    processVariablePump(variableList);
	}
	
	if(headeredConstantList!=null){
	    processHeaderedConstantList(headeredConstantList);
	}
	
	if(headeredVariableList!=null){
	    processHeaderedVariableList(headeredVariableList);
	}
    }
    
    private void processConstantPump(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> pumpCount = list.get(constant).keySet();
	Iterator<String> pumpIterator = pumpCount.iterator();
	while(pumpIterator.hasNext()){
	    String count = pumpIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(constant).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Pump p = new Pump(constant,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("RATED FLOW RATE")){
		    String rate = vn.getAttribute();
		    if(rate.equalsIgnoreCase("AUTOSIZE")){
			rate = parser.getPumpSummary(name)[2];
		    }
		    p.setPumpFlowRate(rate);
		}else if(vn.getDescription().equalsIgnoreCase("RATED PUMP HEAD")){
		    p.setPumpHead(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("RATED POWER CONSUMPTION")){
		    String power = vn.getAttribute();
		    if(power.equalsIgnoreCase("AUTOSIZE")){
			power = parser.getPumpSummary(name)[0];
		    }
		    p.setPumpPower(power);
		}
	    }
	    pumpMap.put(name, p);
	}
    }
    
    private void processVariablePump(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> pumpCount = list.get(variable).keySet();
	Iterator<String> pumpIterator = pumpCount.iterator();
	while(pumpIterator.hasNext()){
	    String count = pumpIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(variable).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Pump p = new Pump(variable,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("RATED FLOW RATE")){
		    String rate = vn.getAttribute();
		    if(rate.equalsIgnoreCase("AUTOSIZE")){
			rate = parser.getPumpSummary(name)[2];
		    }
		    p.setPumpFlowRate(rate);
		}else if(vn.getDescription().equalsIgnoreCase("RATED PUMP HEAD")){
		    p.setPumpHead(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("RATED POWER CONSUMPTION")){
		    String power = vn.getAttribute();
		    if(power.equalsIgnoreCase("AUTOSIZE")){
			power = parser.getPumpSummary(name)[0];
		    }
		    p.setPumpPower(power);
		}
	    }
	    pumpMap.put(name, p);
	}
    }
    
    private void processHeaderedVariableList(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> pumpCount = list.get(headeredVariable).keySet();
	Iterator<String> pumpIterator = pumpCount.iterator();
	while(pumpIterator.hasNext()){
	    String count = pumpIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(headeredVariable).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Pump p = new Pump(headeredVariable,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("TOTAL RATED FLOW RATE")){
		    String rate = vn.getAttribute();
		    if(rate.equalsIgnoreCase("AUTOSIZE")){
			rate = parser.getPumpSummary(name)[2];
		    }
		    p.setPumpFlowRate(rate);
		}else if(vn.getDescription().equalsIgnoreCase("RATED PUMP HEAD")){
		    p.setPumpHead(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("RATED POWER CONSUMPTION")){
		    String power = vn.getAttribute();
		    if(power.equalsIgnoreCase("AUTOSIZE")){
			power = parser.getPumpSummary(name)[0];
		    }
		    p.setPumpPower(power);
		}
	    }
	    pumpMap.put(name, p);
	}
    }
    
    private void processHeaderedConstantList(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> pumpCount = list.get(headeredConstant).keySet();
	Iterator<String> pumpIterator = pumpCount.iterator();
	while(pumpIterator.hasNext()){
	    String count = pumpIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(headeredConstant).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Pump p = new Pump(headeredConstant,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("TOTAL RATED FLOW RATE")){
		    String rate = vn.getAttribute();
		    if(rate.equalsIgnoreCase("AUTOSIZE")){
			rate = parser.getPumpSummary(name)[2];
		    }
		    p.setPumpFlowRate(rate);
		}else if(vn.getDescription().equalsIgnoreCase("RATED PUMP HEAD")){
		    p.setPumpHead(vn.getAttribute());
		}else if(vn.getDescription().equalsIgnoreCase("RATED POWER CONSUMPTION")){
		    String power = vn.getAttribute();
		    if(power.equalsIgnoreCase("AUTOSIZE")){
			power = parser.getPumpSummary(name)[0];
		    }
		    p.setPumpPower(power);
		}
	    }
	    pumpMap.put(name, p);
	}
    }

    /**
     * The class represents pump objects in EnergyPlus for mapping purpose
     * 
     * @author Weili
     *
     */
    public class Pump {
	//EnergyPlus object's name
	private final String type;
	//EnergyPlus objects' Name Field
	private final String pumpName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat pump;
	
	public Pump(String type, String name){
	    this.type = type;
	    pumpName = name;
	}
	
	public void setPumpPower(String power){
	    properties[powerIndex] = power;
	}
	
	public void setPumpHead(String head){
	    properties[headIndex] = head;
	}
	
	public void setPumpFlowRate(String flowRate){
	    properties[flowRateIndex] = flowRate;
	}
	
	public String getName(){
	    return pumpName;
	}
	
	public String getPumpDescription(){
	    if(pump==null){
		return pumpName;
	    }
	    return pump.getDescription();
	}
	
	public String getPumpUnit(){
	    if(pump==null){
		return "";
	    }
	    return pump.getUnit();
	}
	
	public String getPumpType(){
	    return type;
	}
	
	/**
	 * set the pump from EnergyPlus model into masterformat object
	 * This is the start of the mapping, as part of the life cycle call. 
	 * After this call, then user can start extract cost information.
	 * 
	 * This method will try to fill the property list as full as possible
	 * If there is no data available, then "" will be used
	 */
	public void setPump(MasterFormat m){
	    pump = m;
	    properties = getProperties();
	    pump.setVariable(properties);
	}
	
	public ArrayList<String> getUserInputs(){
	    return pump.getUserInputs();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    pump.setUserInputs(map);
	}
	
	public ArrayList<String> getOptionList(){
	    return pump.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return pump.getQuantitiesFromObjects();
	}
	
	public Double[] getCostInformation(){
	    if(pump == null){
		return null;
	    }
	    try {
		pump.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (pump.getCostVector() == null) {
		return null;
	    }

	    return pump.getCostVector();
	}
	
	public double getRandomTotalCost() {
	    return pump.randomDrawTotalCost();
	}
	
	@Override
	public Pump clone() {
	    Pump temp = new Pump(this.type, this.pumpName);
	    temp.setPumpFlowRate(properties[flowRateIndex]);
	    temp.setPumpPower(properties[powerIndex]);
	    temp.setPumpHead(properties[headIndex]);
	    return temp;
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
