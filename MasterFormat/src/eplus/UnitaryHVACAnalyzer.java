package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;
/**
 * Unitary system is selected by their cooling capacity and heating capacity.
 * However in EnergyPlus, these two capacity is sized in the coils connects to the system.
 * Therefore, a slightly changed version is implemented in this class which, instead of searching idf file for the inputs,
 * we search the html file directly.
 * @author Weili
 *
 */
public class UnitaryHVACAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, Unitary> unitaryMap;

    // size shows the data string size in unitary system segment
    private final int stringArraySize = 2;
    private final DecimalFormat df = new DecimalFormat("###.##");

    // energyplub objects (Currently only AirLoopHVAC:Unitary:Furnace:HeatCool
    // and AirLoopHVAC:UnitaryHeatPump:AirToAir are included)
    private final static String furnaceHeatCool = "AirLoopHVAC:Unitary:Furnace:HeatCool";
    private final static String heatpumpAirToAir = "AirLoopHVAC:UnitaryHeatPump:AirToAir";

    // the standard data format for mapping EnergyPlus model and MasterFormat
    protected final int coolingPowerIndex = 0;
    protected final int heatingPowerIndex = 1;

    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public UnitaryHVACAnalyzer(IdfReader reader, EnergyPlusHTMLParser p) {
	this.reader = reader;
	parser = p;
	unitaryMap = new HashMap<String, Unitary>();
	
	processUnitarySystemRawDataFromUnitarySystem();
    }
    
    protected String[][] getCostListForUnitary(String unitary){
 	Unitary u = unitaryMap.get(unitary);
 	String[][] costList = new String[1][rowElement];
 	
 	String generalUnit = u.getUnitaryUnit();
 	
 	String[] costVector = new String[rowElement];
 	Double[] costInfo = u.getCostInformation();
 	if(costInfo!=null){
 	    //the first element in a vector is the unitary name;
 	    
 	    costVector[0] = u.getUnitaryDescription();
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
     
     protected String[] getUnitaryList(){
  	Set<String> unitaries = unitaryMap.keySet();
  	String[] unitaryArray = new String[unitaries.size()];
  	Iterator<String> unitaryIterator = unitaries.iterator();
  	
  	int counter = 0;
  	while(unitaryIterator.hasNext() && counter<unitaries.size()){
  	  unitaryArray[counter]=unitaryIterator.next();
  	    counter++;
  	}
  	return unitaryArray;
      }
      
      protected String getUnitaryType(String unitaryName){
  	return unitaryMap.get(unitaryName).getUnitaryType();
      }
      
      protected void setUnitaryMasterFormat(String unitaryName, MasterFormat mf){
	  unitaryMap.get(unitaryName).setUnitary(mf);
      }
      
      protected Unitary getUnitary(String unitaryName){
  	return unitaryMap.get(unitaryName);
      }
      
      protected void setUserInput(HashMap<String, String> map,String unitaryName){
  	Unitary u = unitaryMap.get(unitaryName);
  	u.setUserInputs(map);
      }
    
    private void processUnitarySystemRawDataFromUnitarySystem(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> furnaceHC = reader.getObjectList(furnaceHeatCool);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> heatpumpAtA = reader.getObjectList(heatpumpAirToAir);
	if(furnaceHC!=null){
	    processFurnaceHC(furnaceHC);
	}
	
	if(heatpumpAtA!=null){
	    processHeatPumpAirToAir(heatpumpAtA);
	}

    }
    
    private void processFurnaceHC(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> unitaryCount = list.get(furnaceHeatCool).keySet();
	Iterator<String> unitaryIterator = unitaryCount.iterator();
	while(unitaryIterator.hasNext()){
	    String count = unitaryIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(furnaceHeatCool).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Unitary u = new Unitary(furnaceHeatCool,name);

	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("HEATING COIL OBJECT TYPE")){
		    String object = vn.getAttribute();
		    if(object.equalsIgnoreCase("COIL:HEATING:WATER")){
			u.setCoilProperty("HeatCoilType", "Hot Water");
		    }else if(object.equalsIgnoreCase("COIL:HEATING:STEAM")){
			u.setCoilProperty("HeatCoilType","Steam");
		    }
		}else if(vn.getDescription().equalsIgnoreCase("HEATING COIL NAME")){
		    String heatCoilName = vn.getAttribute();
		    u.setUnitaryHeatingPower(parser.getHeatCoilSummary(heatCoilName)[0]);
		}else if(vn.getDescription().equalsIgnoreCase("COOLING COIL NAME")){
		    String coolCoilName = vn.getAttribute();
		    u.setUnitaryCoolingPower(parser.getCoolCoilSummary(coolCoilName)[0]);
		}
	    }
	    unitaryMap.put(name, u);
	}
    }
    
    private void processHeatPumpAirToAir(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> unitaryCount = list.get(heatpumpAirToAir).keySet();
	Iterator<String> unitaryIterator = unitaryCount.iterator();
	while(unitaryIterator.hasNext()){
	    String count = unitaryIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(heatpumpAirToAir).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Unitary u = new Unitary(heatpumpAirToAir,name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("HEATING COIL NAME")){
		    String heatCoilName = vn.getAttribute();
		    u.setUnitaryHeatingPower(parser.getHeatCoilSummary(heatCoilName)[0]);
		}else if(vn.getDescription().equalsIgnoreCase("COOLING COIL NAME")){
		    String coolCoilName = vn.getAttribute();
		    u.setUnitaryCoolingPower(parser.getCoolCoilSummary(coolCoilName)[0]);
		}
	    }
	    unitaryMap.put(name, u);
	}
    }
    
    

    /**
     * This class represents unitary sytem objects in EnergyPlus for mapping
     * purpose
     * 
     * @author Weili
     *
     */
    public class Unitary {
	// EnergyPlus object's name
	private final String type;
	// EnergyPlus objects' Name Field
	private final String unitaryName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat unitary;
	private HashMap<String, String> coilProperty;

	public Unitary(String type, String name) {
	    this.type = type;
	    unitaryName = name;
	    coilProperty = new HashMap<String, String>();
	}

	public void setUnitaryCoolingPower(String coolingPower) {
	    properties[coolingPowerIndex] = coolingPower;
	}

	public void setUnitaryHeatingPower(String heatingPower) {
	    properties[heatingPowerIndex] = heatingPower;
	}
	
	public void setCoilProperty(String key, String value){
	    coilProperty.put(key,value);
	}

	public String getName() {
	    return unitaryName;
	}

	public String getUnitaryDescription() {
	    if (unitary == null) {
		return unitaryName;
	    }
	    return unitary.getDescription();
	}

	public String getUnitaryUnit() {
	    if (unitary == null) {
		return "";
	    }
	    return unitary.getUnit();
	}

	public String getUnitaryType() {
	    return type;
	}

	public void setUnitary(MasterFormat m) {
	    unitary = m;
	    properties = getProperties();
	    unitary.setVariable(properties);
	    if(coilProperty!=null){
		unitary.setUserInputs(coilProperty);
	    }
	}
	
	public ArrayList<String> getUserInputs(){
	    return unitary.getUserInputs();
	}
	
	public void setUserInputs(HashMap<String, String> map){
	    unitary.setUserInputs(map);
	}
	
	public ArrayList<String> getOptionList(){
	    return unitary.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return unitary.getQuantitiesFromObjects();
	}
	
	public Double[] getCostInformation(){
	    if(unitary == null){
		return null;
	    }
	    try {

		unitary.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (unitary.getCostVector() == null) {
		return null;
	    }
	    return unitary.getCostVector();
	}

	public Unitary clone() {
	    Unitary temp = new Unitary(this.type, this.unitaryName);
	    temp.setUnitaryCoolingPower(properties[coolingPowerIndex]);
	    temp.setUnitaryHeatingPower(properties[heatingPowerIndex]);
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
