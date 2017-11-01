package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;

public class ConvectionUnitAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    private HashMap<String, ConvectionUnit> convectionUnitMap;

    // size shows the data string size in ConvectionUnit system segment
    private final int stringArraySize = 2;
    private final DecimalFormat df = new DecimalFormat("###.##");

    // Energyplub objects (Currently only ZoneHVAC:FourPipeFanCoil
    // )
    private final static String fanCoilUnit = "ZoneHVAC:FourPipeFanCoil";

    // the standard data format for mapping EnergyPlus model and MasterFormat
    protected final int coolingPowerIndex = 0;
    protected final int heatingPowerIndex = 1;

    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public ConvectionUnitAnalyzer(IdfReader reader, EnergyPlusHTMLParser p) {
	this.reader = reader;
	parser = p;
	convectionUnitMap = new HashMap<String, ConvectionUnit>();
	processConvectionUnitRawData();
    }
    
    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForConvectionUnit() {
	Double totalCUCost = 0.0;
	Set<String> cuList = convectionUnitMap.keySet();
	Iterator<String> cuIterator = cuList.iterator();
	while (cuIterator.hasNext()) {
	    String cu = cuIterator.next();
	    double totalcost = convectionUnitMap.get(cu).getRandomTotalCost();
	    totalCUCost+=totalcost;
	    System.out.println("This "+cu+" unit cost of "+ totalcost+" and the cumulative total is: "+totalCUCost);
	}
	return totalCUCost;
    }

    protected String[][] getCostListForConvectionUnit(String unit) {
	ConvectionUnit c = convectionUnitMap.get(unit);
	String[][] costList = new String[1][rowElement];

	String generalUnit = c.getConvectionUnitUnit();

	String[] costVector = new String[rowElement];
	Double[] costInfo = c.getCostInformation();
	if (costInfo != null) {
	    // the first element in a vector is the unitary name;

	    costVector[0] = c.getConvectionUnitDescription();
	    costVector[1] = generalUnit;
	    for (int j = 0; j < costInfo.length; j++) {
		costVector[j + 2] = df.format(costInfo[j]);
	    }
	    costList[0] = costVector;
	} else {
	    costList[0] = defaultCostData;
	}
	return costList;
    }

    protected String[] getConvectionUnitList() {
	Set<String> units = convectionUnitMap.keySet();
	String[] unitArray = new String[units.size()];
	Iterator<String> unitIterator = units.iterator();

	int counter = 0;
	while (unitIterator.hasNext() && counter < units.size()) {
	    unitArray[counter] = unitIterator.next();
	    counter++;
	}
	return unitArray;
    }

    protected String getConvectionUnitType(String unit) {
	return convectionUnitMap.get(unit).getConvectionUnitType();
    }

    protected void setConvectionUnitMasterFormat(String unit, MasterFormat mf) {
	convectionUnitMap.get(unit).setConvectionUnit(mf);
    }

    protected ConvectionUnit getConvectionUnit(String unit) {
	return convectionUnitMap.get(unit);
    }

    protected void setUserInput(HashMap<String, String> map, String unit) {
	ConvectionUnit c = convectionUnitMap.get(unit);
	c.setUserInputs(map);
    }

    private void processConvectionUnitRawData() {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> fanCoilUnitList = reader
		.getObjectList(fanCoilUnit);

	if (fanCoilUnitList != null) {
	    processFanCoil(fanCoilUnitList);
	}
    }

    private void processFanCoil(
	    HashMap<String, HashMap<String, ArrayList<ValueNode>>> list) {
	Set<String> convectionUnitCount = list.get(fanCoilUnit).keySet();
	Iterator<String> convectionUnitIterator = convectionUnitCount
		.iterator();
	while (convectionUnitIterator.hasNext()) {
	    String count = convectionUnitIterator.next();
	    ArrayList<ValueNode> tempNodeList = list.get(fanCoilUnit)
		    .get(count);
	    String name = tempNodeList.get(0).getAttribute();
	    ConvectionUnit c = new ConvectionUnit(fanCoilUnit, name);

	    for (ValueNode vn : tempNodeList) {
		// supposedly, fan coil could have chilled water coil and dx
		// coil
		// however, in energyplus, only water coil is allowed in fan
		// coil unit,
		// therefore, we can just use the default setting (water coil)
		// in the fan coil unit
		if (vn.getDescription().equalsIgnoreCase("COOLING COIL NAME")) {
		    String coolCoilName = vn.getAttribute();
		    c.setConvectionUnitCoolingPower(parser
			    .getCoolCoilSummary(coolCoilName)[0]);
		}else if(vn.getDescription().equalsIgnoreCase("Heating Coil Name")){
		    String heatCoilName = vn.getAttribute();
		    c.setConvectionUnitHeatingPower(parser.getHeatCoilSummary(heatCoilName)[0]);
		}
	    }
	    convectionUnitMap.put(name, c);
	}
    }

    /**
     * This class represents convection units objects in EnergyPlus for mapping
     * purpose
     * 
     * @author Weili
     *
     */
    public class ConvectionUnit {
	// EnergyPlus object's name
	private final String type;
	// EnergyPlus objects' Name Field
	private final String unitName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat convectionUnit;
	private HashMap<String, String> coilProperty;

	public ConvectionUnit(String type, String name) {
	    this.type = type;
	    unitName = name;
	    coilProperty = new HashMap<String, String>();
	}

	public void setConvectionUnitCoolingPower(String coolingPower) {
	    properties[coolingPowerIndex] = coolingPower;
	}

	public void setConvectionUnitHeatingPower(String heatingPower) {
	    properties[heatingPowerIndex] = heatingPower;
	}

	public void setCoilProperty(String key, String value) {
	    coilProperty.put(key, value);
	}

	public String getName() {
	    return unitName;
	}

	public String getConvectionUnitDescription() {
	    if (convectionUnit == null) {
		return unitName;
	    }
	    return convectionUnit.getDescription();
	}

	public String getConvectionUnitUnit() {
	    if (convectionUnit == null) {
		return "";
	    }
	    return convectionUnit.getUnit();
	}

	public String getConvectionUnitType() {
	    return type;
	}

	public void setConvectionUnit(MasterFormat m) {
	    convectionUnit = m;
	    properties = getProperties();
	    convectionUnit.setVariable(properties);
	    if (coilProperty != null) {
		convectionUnit.setUserInputs(coilProperty);
	    }
	}

	public ArrayList<String> getUserInputs() {
	    return convectionUnit.getUserInputs();
	}

	public void setUserInputs(HashMap<String, String> map) {
	    convectionUnit.setUserInputs(map);
	}

	public ArrayList<String> getOptionList() {
	    return convectionUnit.getOptionListFromObjects();
	}

	public ArrayList<Integer> getOptionQuantities() {
	    return convectionUnit.getQuantitiesFromObjects();
	}

	public Double[] getCostInformation() {
	    if (convectionUnit == null) {
		return null;
	    }
	    try {

		convectionUnit.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (convectionUnit.getCostVector() == null) {
		return null;
	    }
	    return convectionUnit.getCostVector();
	}
	
	public double getRandomTotalCost() {
	    return convectionUnit.randomDrawTotalCost();
	}

	@Override
	public ConvectionUnit clone() {
	    ConvectionUnit temp = new ConvectionUnit(this.type, this.unitName);
	    temp.setConvectionUnitCoolingPower(properties[coolingPowerIndex]);
	    temp.setConvectionUnitHeatingPower(properties[heatingPowerIndex]);
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
