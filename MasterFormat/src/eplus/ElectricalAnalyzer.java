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
    private final static String exteriorlights = "Exterior:Lights";

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
	electricMap = new HashMap<String,Electric>();
	processElectricalEquipment();
    }
    
    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForLighting() {
	Double totalElectricCost = 0.0;
	Set<String> electricList = electricMap.keySet();
	Iterator<String> electricIterator = electricList.iterator();
	while (electricIterator.hasNext()) {
	    String electric = electricIterator.next();
	    double totalcost = electricMap.get(electric).getRandomTotalCost();
	    totalElectricCost+=totalcost;
	    System.out.println("This "+electric+" unit cost of "+ totalcost+" and the cumulative total is: "+totalElectricCost);
	}
	return totalElectricCost;
    }
    
    protected String[][] getCostListForElectric(String electric){
  	Electric e = electricMap.get(electric);
  	String[][] costList = new String[1][rowElement];
  	
  	String generalUnit = e.getElectricUnit();
  	
  	String[] costVector = new String[rowElement];
  	Double[] costInfo = e.getCostInformation();
  	if(costInfo!=null){
  	    //the first element in a vector is the electric name;
  	    
  	    costVector[0] = e.getElectricDescription();
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
      
      protected String[] getElectricList(){
   	Set<String> electrics = electricMap.keySet();
   	String[] electricArray = new String[electrics.size()];
   	Iterator<String> electricIterator = electrics.iterator();
   	
   	int counter = 0;
   	while(electricIterator.hasNext() && counter<electrics.size()){
   	 electricArray[counter]=electricIterator.next();
   	    counter++;
   	}
   	return electricArray;
       }
       
       protected String getElectricType(String electricName){
   	return electricMap.get(electricName).getElectricType();
       }
       
       protected void setElectricMasterFormat(String electricName, MasterFormat mf){
	   electricMap.get(electricName).setElectric(mf);
       }
       
       protected Electric getElectric(String electricName){
   	return electricMap.get(electricName);
       }
       
       protected void setUserInput(HashMap<String, String> map,String electricName){
   	Electric e = electricMap.get(electricName);
   	e.setUserInputs(map);
       }
    
    private void processElectricalEquipment(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> interiorLight = reader.getObjectList(lights);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> exteriorLight = reader.getObjectList(exteriorlights);
	
	if(interiorLight!=null){
	    processInteriorLight(interiorLight);
	}
	
	if(exteriorLight!=null){
	    processExteriorLight(exteriorLight);
	}
    }
    
    private void processExteriorLight(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> lightCount = list.get(exteriorlights).keySet();
	Iterator<String> lightIterator = lightCount.iterator();
	while(lightIterator.hasNext()){
	    String count = lightIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(exteriorlights).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Electric e = new Electric(exteriorlights,name);
	    for(ValueNode vn:tempNodeList){
		if(vn.getDescription().equalsIgnoreCase("DESIGN LEVEL")){
		    e.setPower(vn.getAttribute());
		}
	    }
	    electricMap.put(name, e);
	}
    }
    
    private void processInteriorLight(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> lightCount = list.get(lights).keySet();
	Iterator<String> lightIterator = lightCount.iterator();
	while(lightIterator.hasNext()){
	    String count = lightIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(lights).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Electric e = new Electric(lights,name);
	    //get the power from the html file
	    e.setPower(parser.getInteriorLightSummary(name)[0]);   
	    for(ValueNode vn: tempNodeList){
		//estimate the mounting method from the inputs. This is a rough estimation,
		//needs to be fixed later
		if(vn.getDescription().equalsIgnoreCase("FRACTION RADIANT")){
		    Double frac = Double.parseDouble(vn.getAttribute());
		    if(frac<0.5){
			e.setAdditionalProperty("Mount", "Recessed");
		    }else{
			e.setAdditionalProperty("Mount", "Surface");
		    }
		}
	    }
	    electricMap.put(name, e);
	}
    }
    
    public class Electric{
	// name in EnergyPlus object name
	private final String type;
	private final String electricName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat electric;
	private final HashMap<String, String> additionInput;
	
	public Electric(String type, String name){
	    this.type = type;
	    electricName = name;
	    additionInput = new HashMap<String, String>();
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
	
	public void setAdditionalProperty(String key, String value){
	    additionInput.put(key, value);
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
	    if(additionInput!=null){
		electric.setUserInputs(additionInput);
	    }
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
	
	public double getRandomTotalCost() {
	    return electric.randomDrawTotalCost();
	}
	
	@Override
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
