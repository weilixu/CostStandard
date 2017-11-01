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
 * Currently, furnaces analyzer could extract the following information: furnace capacity and furnace efficiency
 * Above information will be organized in an String[] data structure in the order of
 * {Capacity, Efficiency}
 * @author Weili
 *
 */
public class FurnaceAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, Furnace> furnaceMap;
    
    private final int stringArraySize = 2;
    private final DecimalFormat df = new DecimalFormat("###.##");
    
    private final static String electric = "Coil:Heating:Electric";
    private final static String gas = "Coil:Heating:Gas";
    
    protected final int powerIndex = 0;
    protected final int efficiencyIndex = 1;
    
    private static final String[] defaultCostData = { "Unknown", "Ea","0", "0", "0",
	    "0", "0" };
    private final Integer rowElement = 7;
    
    public FurnaceAnalyzer(IdfReader reader, EnergyPlusHTMLParser p){
	this.reader = reader;
	parser = p;
	furnaceMap = new HashMap<String, Furnace>();
	processFurnaceData();
    }
    
    /**
     * get the random total cost for the furnaces
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForFurnace() {
	Double totalFurnaceCost = 0.0;
	Set<String> furnaceList = furnaceMap.keySet();
	Iterator<String> furnaceIterator = furnaceList.iterator();
	while (furnaceIterator.hasNext()) {
	    String furnace = furnaceIterator.next();
	    double totalcost = furnaceMap.get(furnace).getRandomTotalCost();
	    totalFurnaceCost+=totalcost;
	    System.out.println("This "+electric+" unit cost of "+ totalcost+" and the cumulative total is: "+totalFurnaceCost);
	}
	return totalFurnaceCost;
    }
    
    protected String[][] getCostListForFurnace(String furnace){
	Furnace f = furnaceMap.get(furnace);
	String[][] costList = new String[1][rowElement];
	
	String generalUnit = f.getFurnaceUnit();
	
	    String[] costVector = new String[rowElement];
	    Double[] costInfo = f.getCostInformation();
	    if(costInfo!=null){
		//the first element in a vector is the boiler name;
		costVector[0] = f.getFurnaceDescription();
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
    
    protected String[] getFurnaceList(){
	Set<String> furnaces = furnaceMap.keySet();
	String[] furnaceArray = new String[furnaces.size()];
	Iterator<String> furnaceIterator = furnaces.iterator();
	
	int counter = 0;
	while(furnaceIterator.hasNext()&& counter<furnaces.size()){
	    furnaceArray[counter] = furnaceIterator.next();
	    counter++;
	}
	return furnaceArray;
    }
    
    protected String getFurnaceType(String furnaceName){
	return furnaceMap.get(furnaceName).getType();
    }
    
    protected void setFurnaceMasterFormat(String furnaceName, MasterFormat mf){
	furnaceMap.get(furnaceName).setFurnace(mf);
    }
    
    protected Furnace getFurnace(String furnaceName){
	return furnaceMap.get(furnaceName);
    }
    
    /**
     * set the user input which is later be used to select the cost
     * 
     * @param map
     * @param construction
     * @param index
     */
    protected void setUserInput(HashMap<String, String> map, String furnaceName) {
	Furnace f = furnaceMap.get(furnaceName);
	f.setUserInputs(map);
    }
    
    private void processFurnaceData(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> electricCoils = reader.getObjectList(electric);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> gasCoils = reader.getObjectList(gas);
	
	if(electricCoils!=null){
	    processElectricCoil(electricCoils);
	}
	
	if(gasCoils!=null){
	    processGasCoil(gasCoils);
	}
    }
    
    private void processElectricCoil(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> furnaceCount = list.get(electric)
		.keySet();
	Iterator<String> furnaceIterator = furnaceCount.iterator();
	while(furnaceIterator.hasNext()){
	    String count = furnaceIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(electric).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Furnace f = new Furnace(electric, name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Efficiency")){
		    f.setEfficiency(vn.getAttribute());
		}else if(vn.getDescription().equals("Nominal Capacity")){
		    String p = vn.getAttribute();
		    if(p.equalsIgnoreCase("autosize")){
			p = parser.getHeatCoilSummary(name)[0];
		    }
		    f.setPower(p);
		}
	    }
	    furnaceMap.put(name, f);
	}
    }
    
    private void processGasCoil(HashMap<String, HashMap<String, ArrayList<ValueNode>>> list){
	Set<String> furnaceCount = list.get(gas)
		.keySet();
	Iterator<String> furnaceIterator = furnaceCount.iterator();
	while(furnaceIterator.hasNext()){
	    String count = furnaceIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(gas).get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    Furnace f = new Furnace(gas, name);
	    
	    for(ValueNode vn: tempNodeList){
		if(vn.getDescription().equals("Gas Burner Efficiency")){
		    f.setEfficiency(vn.getAttribute());
		}else if(vn.getDescription().equals("Nominal Capacity")){
		    String p = vn.getAttribute();
		    if(p.equalsIgnoreCase("autosize")){
			p = parser.getHeatCoilSummary(name)[0];
		    }
		    f.setPower(p);
		}
	    }
	    furnaceMap.put(name, f);
	}
    }
    
    /**
     * 
     * @author Weili
     *
     */
    public class Furnace{
	private final String type;
	private final String furnaceName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat furnace;
	
	public Furnace(String type, String name){
	    furnaceName = name;
	    this.type = type;
	}
	
	public void setPower(String p){
	    properties[powerIndex] = p;
	}
	
	public void setEfficiency(String s){
	    properties[efficiencyIndex] = s;
	}
	
	public String getName(){
	    return furnaceName;
	}
	
	public void setFurnace(MasterFormat m){
	    furnace = m;
	    properties = getProperties();
	    furnace.setVariable(properties);
	}
	
	public String getFurnaceDescription(){
	    if(furnace==null){
		return furnaceName;
	    }
	    return furnace.getDescription();
	}
	
	public String getType(){
	    return type;
	}
	
	public String getFurnaceUnit(){
	    if(furnace==null){
		return"";
	    }
	    return furnace.getUnit();
	}
	
	public ArrayList<String> getUserInputs(){
	    return furnace.getUserInputs();
	}
	
	public ArrayList<String> getOptionList(){
	    return furnace.getOptionListFromObjects();
	}
	
	public ArrayList<Integer> getOptionQuantities(){
	    return furnace.getQuantitiesFromObjects();
	}
	
	
	public void setUserInputs(HashMap<String, String> map){
	    furnace.setUserInputs(map);
	}
	
	public Double[] getCostInformation(){
	    if(furnace == null){
		return null;
	    }
	    
	    try{
		furnace.selectCostVector();
	    }catch(NullPointerException e){
		return null;
	    }
	    
	    if(furnace.getCostVector() == null){
		return null;
	    }
	    
	    return furnace.getCostVector();
	}
	
	public double getRandomTotalCost() {
	    return furnace.randomDrawTotalCost();
	}
	
	@Override
	public Furnace clone(){
	    Furnace f = new Furnace(this.type,this.furnaceName);
	    f.setPower(properties[powerIndex]);
	    f.setEfficiency(properties[efficiencyIndex]);
	    return f;
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
