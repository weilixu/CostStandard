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
    
    private HashMap<String, ArrayList<TransparentMaterial>> constructionMap;
    
    private final int stringArraySize = 6;
    private final DecimalFormat df = new DecimalFormat("###.##");
    
    private final static String construction = "Construction";
    private final static String simplewindowmaterial = "WindowMaterial:SimpleGlazingSystem";
    private final static String windowgas = "WindowMaterial:Gas";
    private final static String windowmaterial = "WindowMaterial:Glazing";
  
    // the standard data format for mapping Energyplus model and masterformat
    protected final int glazingSizeIndex = 0;
    protected final int numLayerIndex = 1;
    protected final int thicknessIndex = 2;
    protected final int uvalueIndex = 3;
    protected final int shgcIndex = 4;
    protected final int vtIndex = 5;
    
    private static final String[] defaultCostData = { "Unknown", "", "0", "0",
	    "0", "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;
    
    public TransparentMaterialAnalyzer(IdfReader reader, EnergyPlusHTMLParser p){
	this.reader = reader;
	parser = p;
	constructionMap = new HashMap<String, ArrayList<TransparentMaterial>>();
	processTransparentMaterialRawData();
    }
    
    /**
     * get the whole map
     * 
     * @return
     */
    protected HashMap<String, ArrayList<TransparentMaterial>> getMaterialData() {
	return constructionMap;
    }
    
    /**
     * get the material list under one particular construction
     * 
     * @param s
     * @return
     */
    protected ArrayList<TransparentMaterial> getMaterialList(String s) {
	return constructionMap.get(s);
    }
    
    /**
     * BELOW ARE PRE-PROCESSING CODE FOR THE OPAQUE CONSTRUCTION CATEGORY
     */
    private void processTransparentMaterialRawData(){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constructionList  = reader.getObjectList(construction);
	Set<String> constructionCount = constructionList.get(construction)
		.keySet();
	Iterator<String> constructionIterator = constructionCount.iterator();
	while(constructionIterator.hasNext()){
	    
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
	    TransparentMaterial temp = new TransparentMaterial(this.materialName);
	    temp.setGlazingArea(properties[glazingSizeIndex]);
	    temp.setNumberOfLayer(properties[numLayerIndex]);
	    temp.setThickness(properties[thicknessIndex]);
	    temp.setUValue(properties[uvalueIndex]);
	    temp.setSHGC(properties[shgcIndex]);
	    temp.setVt(properties[vtIndex]);
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
