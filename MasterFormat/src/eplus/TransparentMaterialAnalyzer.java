package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.EnergyPlusHTMLParser;

public class TransparentMaterialAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;

    private HashMap<String, TransparentMaterial> constructionMap;

    private final int stringArraySize = 8;
    private final DecimalFormat df = new DecimalFormat("###.##");

    private final static String construction = "Construction";
    private final static String simplewindowmaterial = "WindowMaterial:SimpleGlazingSystem";
    private final static String windowgas = "WindowMaterial:Gas";
    private final static String windowmaterial = "WindowMaterial:Glazing";
    private final static String windowblind = "WindowMaterial:Blind";
    private final static String windowscreen = "WindowMaterial:Screen";

    // the standard data format for mapping Energyplus model and masterformat
    protected final int glazingSizeIndex = 0;
    protected final int numLayerIndex = 1;
    protected final int thicknessIndex = 2;
    protected final int uvalueIndex = 3;
    protected final int shgcIndex = 4;
    protected final int vtIndex = 5;
    protected final int blindIndex = 6;
    protected final int screenIndex = 7;

    private boolean isSimpleGlazing = false;

    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public TransparentMaterialAnalyzer(IdfReader reader, EnergyPlusHTMLParser p) {
	this.reader = reader;
	parser = p;
	constructionMap = new HashMap<String, TransparentMaterial>();
	processTransparentMaterialRawData();
    }

    /**
     * get the random total cost for the construction
     * 
     * @param cons
     * @return
     */
    protected double getTotalCostForEnvelope() {
	Double totalConstructionCost = 0.0;
	Set<String> constructionList = constructionMap.keySet();
	Iterator<String> constructionIterator = constructionList.iterator();
	while (constructionIterator.hasNext()) {
	    String construction = constructionIterator.next();
	    double totalcost = constructionMap.get(construction)
		    .getRandomTotalCost();
	    totalConstructionCost += totalcost;
	    System.out.println("This " + construction + " unit cost of "
		    + totalcost + " and the cumulative total is: "
		    + totalConstructionCost);
	}
	return totalConstructionCost;
    }

    protected String[][] getCostListForTransparentEnvelope(String envelope) {
	TransparentMaterial tm = constructionMap.get(envelope);
	String[][] costList = new String[1][rowElement];

	String generalUnit = tm.getMaterialUnit();

	String[] costVector = new String[rowElement];
	Double[] costInfo = tm.getCostInformation();
	if (costInfo != null) {
	    // the first element in a vector is the envelope name;

	    costVector[0] = tm.getMaterialDescription();
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

    protected String[] getTransparentEnvelopeList() {
	Set<String> construction = constructionMap.keySet();
	String[] constructionArray = new String[construction.size()];
	Iterator<String> constructionIterator = construction.iterator();

	int counter = 0;
	while (constructionIterator.hasNext() && counter < construction.size()) {
	    constructionArray[counter] = constructionIterator.next();
	    counter++;
	}
	return constructionArray;
    }

    /**
     * get the whole map
     * 
     * @return
     */
    protected HashMap<String, TransparentMaterial> getMaterialData() {
	return constructionMap;
    }

    protected String getEnvelopeDescription(String cons) {
	return constructionMap.get(cons).getMaterialDescription();
    }

    protected void setOpeningMaterFormat(String cons, MasterFormat mf) {
	constructionMap.get(cons).setTransparentMaterial(mf);
    }

    protected TransparentMaterial getEnvelope(String cons) {
	return constructionMap.get(cons);
    }

    protected void setUserInput(HashMap<String, String> map, String cons) {
	TransparentMaterial e = constructionMap.get(cons);
	e.setUserInputs(map);
    }

    /**
     * BELOW ARE PRE-PROCESSING CODE FOR THE OPAQUE CONSTRUCTION CATEGORY
     */
    private void processTransparentMaterialRawData() {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constructionList = reader
		.getObjectList(construction);
	Set<String> constructionCount = constructionList.get(construction)
		.keySet();
	Iterator<String> constructionIterator = constructionCount.iterator();
	while (constructionIterator.hasNext()) {
	    String count = constructionIterator.next();
	    ArrayList<ValueNode> materialList = constructionList.get(
		    construction).get(count);
	    String name = materialList.get(0).getAttribute(); // get the name

	    TransparentMaterial tempMaterial = new TransparentMaterial(name);
	    double thickness = 0.0;
	    Integer layer = 1;
	    Integer glazeLayer = 1;
	    for (ValueNode vn : materialList) {
		if (reader.getValue(windowmaterial, vn.getAttribute(), "Name") != null) {
		    thickness += Double.parseDouble(reader.getValue(
			    windowmaterial, vn.getAttribute(), "Thickness"));
		} else if (reader
			.getValue(windowgas, vn.getAttribute(), "Name") != null) {
		    thickness += Double.parseDouble(reader.getValue(windowgas,
			    vn.getAttribute(), "Thickness"));
		    glazeLayer++;
		} else if (reader.getValue(simplewindowmaterial,
			vn.getAttribute(), "Name") != null) {
		    isSimpleGlazing = true;
		    tempMaterial.setThickness("");
		    tempMaterial.setNumberOfLayer("1");
		} else if (reader.getValue(windowblind, vn.getAttribute(),
			"Name") != null) {
		    tempMaterial.setBlind(true);
		} else if (reader.getValue(windowscreen, vn.getAttribute(),
			"Name") != null) {
		    tempMaterial.setScreen(true);
		}
		layer++;
	    }
	    String[] material = parser.getTransparentMaterialSummary(name);
	    if (!isSimpleGlazing) {
		tempMaterial.setThickness("" + thickness);
		glazeLayer++; // add one more layer
		tempMaterial.setNumberOfLayer("" + glazeLayer);
	    }
	    tempMaterial.setGlazingArea(material[0]);
	    tempMaterial.setUValue(material[1]);
	    tempMaterial.setSHGC(material[2]);
	    tempMaterial.setVt(material[3]);
	    //if the material has area, then we can say this
	    //material is window material
	    if (!material[0].equals("0.0")) {
		constructionMap.put(name, tempMaterial);
	    }
	}
    }

    /**
     * material object. This object carries: 1. Material's name 2. material
     * property map, which contains the data extracted from EnergyPlus and will
     * be used to map into MasterFormat cost. 3. MasterFormt object generated
     * from MasterFormat factory that represents the correspondent MasterFormat
     * object that Energyplus map to
     * 
     * @author Weili
     *
     */
    public class TransparentMaterial {
	private final String materialName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat material;

	public TransparentMaterial(String name) {
	    materialName = name;
	}

	public void setGlazingArea(String ga) {
	    properties[glazingSizeIndex] = ga;
	}

	public void setNumberOfLayer(String l) {
	    properties[numLayerIndex] = l;
	}

	public void setThickness(String thick) {
	    properties[thicknessIndex] = thick;
	}

	public void setUValue(String u) {
	    properties[uvalueIndex] = u;
	}

	public void setSHGC(String shgc) {
	    properties[shgcIndex] = shgc;
	}

	public void setVt(String vt) {
	    properties[vtIndex] = vt;
	}

	public void setBlind(boolean blind) {
	    if (blind) {
		properties[blindIndex] = "Y";
	    }
	}

	public void setScreen(boolean screen) {
	    if (screen) {
		properties[screenIndex] = "Y";
	    }
	}

	public String getName() {
	    return materialName;
	}

	public String getMaterialDescription() {
	    if (material == null) {
		return materialName;
	    }
	    return material.getDescription();
	}

	public String getMaterialUnit() {
	    if (material == null) {
		return "";
	    }
	    return material.getUnit();
	}

	/**
	 * set the material from EnergyPlus model into a MasterFormat object
	 * This is start of the mapping, as part of the life cycle call. After
	 * this call, then user can start extract cost information.
	 * 
	 * This method also tries to derive resistance from the extracted data,
	 * if it is not available.
	 * 
	 * This method will try to fill the property list as full as possible.
	 * If there is no data available, then "" will be used
	 * 
	 * @param m
	 */
	public void setTransparentMaterial(MasterFormat m) {
	    // life cycle call, properties will always be filled before this
	    // method
	    material = m;
	    properties = getProperties();
	    material.setVariable(properties);
	}

	public ArrayList<String> getUserInputs() {
	    return material.getUserInputs();
	}

	public void setUserInputs(HashMap<String, String> map) {
	    material.setUserInputs(map);
	}

	public ArrayList<String> getOptionList() {
	    return material.getOptionListFromObjects();
	}

	public ArrayList<Integer> getOptionQuantities() {
	    return material.getQuantitiesFromObjects();
	}

	public double getRandomTotalCost() {
	    return material.randomDrawTotalCost();
	}

	/**
	 * get the cost information. This method checks whether: material object
	 * is null, if it is, then return null material cost vector is null, if
	 * it is, then return null
	 * 
	 * @return cost vector
	 */
	public Double[] getCostInformation() {
	    if (material == null) {
		return null;
	    }

	    try {
		material.selectCostVector();
	    } catch (NullPointerException e) {
		return null;
	    }

	    if (material.getCostVector() == null) {
		return null;
	    }
	    return material.getCostVector();
	}

	public TransparentMaterial clone() {
	    TransparentMaterial temp = new TransparentMaterial(
		    this.materialName);
	    temp.setGlazingArea(properties[glazingSizeIndex]);
	    temp.setNumberOfLayer(properties[numLayerIndex]);
	    temp.setThickness(properties[thicknessIndex]);
	    temp.setUValue(properties[uvalueIndex]);
	    temp.setSHGC(properties[shgcIndex]);
	    temp.setVt(properties[vtIndex]);
	    temp.setBlind(properties[blindIndex] != null);
	    temp.setScreen(properties[screenIndex] != null);
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
