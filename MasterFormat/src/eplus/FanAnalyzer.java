package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;
import masterformat.api.MasterFormat;

public class FanAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, Fan> fanMap;

    private final int stringArraySize = 4;
    private final DecimalFormat df = new DecimalFormat("###.##");

    // energyplus objects
    private final static String constant = "Fan:ConstantVolume";
    private final static String variable = "Fan:VariableVolume";
    private final static String onoff = "Fan:OnOff";
    private final static String exhaust = "Fan:ZoneExhaust";

    // the standard data format for mapping EnergyPlus model and masterformat
    protected final int flowRateIndex = 0;
    protected final int staticPressureIndex = 1;
    protected final int powerIndex = 2;
    protected final int diameterIndex = 3;

    
    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public FanAnalyzer(IdfReader reader, EnergyPlusHTMLParser p) {
	this.reader = reader;
	parser = p;
	
	fanMap = new HashMap<String, Fan>();
	processFanRawDatafromFans();
    }
    
    protected String[][] getCostListForFan(String fan){
	Fan f = fanMap.get(fan);	
	String[][] costList = new String[1][rowElement];
	
	String generalUnit = f.getFanUnit();
	
	String[] costVector = new String[rowElement];
	Double[] costInfo = f.getCostInformation();
	if(costInfo!=null){
	    //the first element in a vector is the fan name;
	    
	    costVector[0] = f.getFanDescription();
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
    
    protected String[] getFanList(){
	Set<String> fans = fanMap.keySet();
	String[] fanArray = new String[fans.size()];
	Iterator<String> fanIterator = fans.iterator();
	
	int counter = 0;
	while(fanIterator.hasNext() && counter<fans.size()){
	    fanArray[counter]=fanIterator.next();
	    counter++;
	}
	return fanArray;
    }
    
    protected String getFanType(String fanName){
	return fanMap.get(fanName).getFanType();
    }
    
    protected void setFanMasterFormat(String fanName, MasterFormat mf){
	fanMap.get(fanName).setFan(mf);
    }
    
    protected Fan getFan(String fanName){
	return fanMap.get(fanName);
    }
    
    protected void setUserInput(HashMap<String, String> map,String fanName){
	Fan f = fanMap.get(fanName);
	f.setUserInputs(map);
    }

    private void processFanRawDatafromFans(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constantFanList = reader.getObjectList(constant);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> variableFanList = reader.getObjectList(variable);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> onoffFanList = reader.getObjectList(onoff);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> exhaustFanList = reader.getObjectList(exhaust);
	
	if(constantFanList!=null){
	    processConstantFan(constantFanList);
	}
	
	if(variableFanList!=null){
	    processVariableFan(variableFanList);
	}
	
	if(onoffFanList!=null){
	    processOnOffFan(onoffFanList);
	}
	
	if(exhaustFanList!=null){
	    processExhaustFan(exhaustFanList);
	}
    }
    
    private void processConstantFan(HashMap<String, HashMap<String, ArrayList<ValueNode>>> fanList){
	Set<String> fanCount = fanList.get(constant).keySet();
	Iterator<String> fanIterator = fanCount.iterator();
	while(fanIterator.hasNext()){
	    String count = fanIterator.next();
	    ArrayList<ValueNode> tempNodeList = fanList.get(constant).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Fan f = new Fan(constant,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Maximum Flow Rate")){
		    String rate = vn.getAttribute();
		    if(rate.equals("autosize")){
			rate = parser.getFanSummary(name)[3];
		    }
		    f.setFlowRate(rate);
		}
	    }
	    fanMap.put(name, f);
	}
    }
    
    private void processExhaustFan(HashMap<String, HashMap<String, ArrayList<ValueNode>>> fanList){
	Set<String> fanCount = fanList.get(exhaust).keySet();
	Iterator<String> fanIterator = fanCount.iterator();
	while(fanIterator.hasNext()){
	    String count = fanIterator.next();
	    ArrayList<ValueNode> tempNodeList = fanList.get(exhaust).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Fan f = new Fan(exhaust,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Maximum Flow Rate")){
		    String rate = vn.getAttribute();
		    if(rate.equals("autosize")){
			rate = parser.getFanSummary(name)[3];
		    }
		    f.setFlowRate(rate);
		}
	    }
	    fanMap.put(name, f);
	}
    }
    
    
    private void processOnOffFan(HashMap<String, HashMap<String, ArrayList<ValueNode>>> fanList){
	Set<String> fanCount = fanList.get(onoff).keySet();
	Iterator<String> fanIterator = fanCount.iterator();
	while(fanIterator.hasNext()){
	    String count = fanIterator.next();
	    ArrayList<ValueNode> tempNodeList = fanList.get(onoff).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Fan f = new Fan(onoff,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Maximum Flow Rate")){
		    String rate = vn.getAttribute();
		    if(rate.equals("autosize")){
			rate = parser.getFanSummary(name)[3];
		    }
		    f.setFlowRate(rate);
		}
	    }
	    fanMap.put(name, f);
	}
    }
    
    private void processVariableFan(HashMap<String, HashMap<String, ArrayList<ValueNode>>> fanList){
	Set<String> fanCount = fanList.get(variable).keySet();
	Iterator<String> fanIterator = fanCount.iterator();
	while(fanIterator.hasNext()){
	    String count = fanIterator.next();
	    ArrayList<ValueNode> tempNodeList = fanList.get(variable).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Fan f = new Fan(variable,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Maximum Flow Rate")){
		    String rate = vn.getAttribute();
		    if(rate.equalsIgnoreCase("autosize")){
			rate = parser.getFanSummary(name)[3];
		    }
		    f.setFlowRate(rate);
		}
	    }
	    fanMap.put(name, f);
	}
    }
    
    public class Fan {
	// name in EnergyPlus object name
	private final String type;
	private final String fanName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat fan;
	
	public Fan(String type,String name){
	    this.type = type;
	    fanName = name;
	}
	
	public void setFlowRate(String rate){
	     properties[flowRateIndex] = rate;
	}
	
	public void setStaticPressure(String pressure){
	    properties[staticPressureIndex] = pressure;
	}
	
	public void setPower(String power){
	    properties[powerIndex] = power;
	}
	
	public void setDiameter(String dia){
	    properties[diameterIndex] = dia;
	}
	
	public String getName(){
	    return fanName;
	}
	
	public String getFanDescription(){
	    if(fan == null){
		return fanName;
	    }
	    return fan.getDescription();
	}
	
	public String getFanUnit(){
	    if(fan==null){
		return "";
	    }
	    return fan.getUnit();
	}
	
	public String getFanType(){
	    return type;
	}
	
	/**
	 * set the fan from EnergyPlus model into masterformat object
	 * This is the start of the mapping, as part of the life cycle call. 
	 * After this call, then user can start extract cost information.
	 * 
	 * This method will try to fill the property list as full as possible
	 * If there is no data available, then "" will be used
	 */
	public void setFan(MasterFormat m){
	    fan = m;
	    properties = getProperties();
	    fan.setVariable(properties);
	}
	
	public ArrayList<String> getUserInputs(){
	    return fan.getUserInputs();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    fan.setUserInputs(map);
	}
	
	public ArrayList<String> getOptionList(){
	    return fan.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return fan.getQuantitiesFromObjects();
	}
	
	public Double[] getCostInformation(){
	    if(fan == null){
		return null;
	    }
	    try {
		fan.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (fan.getCostVector() == null) {
		return null;
	    }

	    return fan.getCostVector();
	}
	
	public Fan clone() {
	    Fan temp = new Fan(this.type, this.fanName);
	    temp.setFlowRate(properties[flowRateIndex]);
	    temp.setStaticPressure(properties[staticPressureIndex]);
	    temp.setPower(properties[powerIndex]);
	    temp.setDiameter(properties[diameterIndex]);
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
