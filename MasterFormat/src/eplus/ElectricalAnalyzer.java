package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;

public class ElectricalAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, Electric> electricMap;

    // size shows the data string size in fan segment
    private final int stringArraySize = 2;
    private final DecimalFormat df = new DecimalFormat("###.##");

    // energyplus objects
    private final static String lights = "Lights";

    // the standard data format for mapping EnergyPlus model and masterformat
    protected final int totalPowerIndex = 0;
    protected final int kvarIndex = 1;
    protected final int voltageIndex = 2;
    protected final int ampIndex = 3;

    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;
    
    public ElectricalAnalyzer(IdfReader reader, EnergyPlusHTMLParser p){
	this.reader = reader;
	parser = p;
	
	processElectricalEquipment();
    }
    
    private void processElectricalEquipment(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> interiorLight = reader.getObjectList(lights);
	
	if(interiorLight!=null){
	    processInteriorLight(interiorLight);
	}
    }
    
    private void processInteriorLight(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> lightCount = list.get(lights).keySet();
	Iterator<String> lightIterator = lightCount.iterator();
	while(lightIterator.hasNext()){
	    String temp = lightIterator.next();
	}
    }
    
    public class Electric{
	// name in EnergyPlus object name
	private final String type;
	private final String electricName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat electric;
	
	public Electric(String type, String name){
	    this.type = type;
	    electricName = name;
	}
	
	public void setPower(String power){
	    properties[totalPowerIndex] = power;
	}
	
	public void setKvar(String kvar){
	    properties[kvarIndex] = kvar;
	}
	
	public void setVoltage(String v){
	    properties[voltageIndex]=v;
	}
	
	public void setAmps(String a){
	    properties[ampIndex] =a;
	}
	
	public String getName(){
	    return electricName;
	}
	
	public String getElectricDescription(){
	    if(electric == null){
		return electricName;
	    }
	    return electric.getDescription();
	}
	
	public String getElectricUnit(){
	    if(electric==null){
		return "";
	    }
	    return electric.getUnit();
	}
	
	public String getElectricType(){
	    return type;
	}
	
	/**
	 * set the Electric from EnergyPlus model into masterformat object
	 * This is the start of the mapping, as part of the life cycle call. 
	 * After this call, then user can start extract cost information.
	 * 
	 * This method will try to fill the property list as full as possible
	 * If there is no data available, then "" will be used
	 */
	public void setElectric(MasterFormat m){
	    electric = m;
	    properties = getProperties();
	    electric.setVariable(properties);
	}
	
	public ArrayList<String> getUserInputs(){
	    return electric.getUserInputs();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    electric.setUserInputs(map);
	}
	
	public ArrayList<String> getOptionList(){
	    return electric.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return electric.getQuantitiesFromObjects();
	}
	
	public Double[] getCostInformation(){
	    if(electric == null){
		return null;
	    }
	    try {
		electric.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (electric.getCostVector() == null) {
		return null;
	    }

	    return electric.getCostVector();
	}
	
	public Electric clone(){
	    Electric temp = new Electric(this.type, this.electricName);
	    temp.setPower(properties[totalPowerIndex]);
	    temp.setKvar(properties[kvarIndex]);
	    temp.setVoltage(properties[voltageIndex]);
	    temp.setAmps(properties[ampIndex]);
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
