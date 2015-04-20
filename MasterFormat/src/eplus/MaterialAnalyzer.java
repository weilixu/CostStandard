package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.MasterFormat;
import eplus.IdfReader.ValueNode;

/**
 * Currently material Analyzer could extract the following information: Floor
 * Area, Height, surfacetype, thickness, conductivity, density, specificheat,
 * resistance Above information will be organized in an String[] data structure
 * in the order of: {floorArea, height, surfaceType, thickness, conductivity,
 * density, specificHeat, Resistance}
 * 
 * @author Weili
 *
 */
public class MaterialAnalyzer {
    private final IdfReader reader;
    private HashMap<String, ArrayList<Material>> constructionMap;
    private static final Set<String> surfaceTypeList = new HashSet<String>();
    private static final Set<String> ceilingHeightList = new HashSet<String>();
    private static String surfaceType=null;
    private static String floorArea = null;
    private static String ceilingHeight=null;

    private final int stringArraySize = 8;
    private final DecimalFormat df = new DecimalFormat("###.##");

    private final static String construction = "Construction";
    private final static String material = "Material";
    private final static String materialnomass = "Material:NoMass";
    private final static String zone = "Zone";
    private final static String surface = "BuildingSurface:Detailed";

    // the standard data format for mapping Energyplus model and masterformat
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex = 2;
    protected final int thicknessIndex = 3;
    protected final int conductivityIndex = 4;
    protected final int densityIndex = 5;
    protected final int specificHeatIndex = 6;
    protected final int resistanceIndex = 7;

    private static final String[] defaultCostData = { "Unknown", "","0", "0", "0",
	    "0", "0" };
    // the size of cost items (for the table display purpose)
    private final Integer rowElement = 7;

    public MaterialAnalyzer(IdfReader reader) {
	this.reader = reader;
	constructionMap = new HashMap<String, ArrayList<Material>>();
	processMaterialRawDatafromMaterials();
	processMaterialRawDatafromSurface();
    }

    /**
     * get the whole map
     * 
     * @return
     */
    protected HashMap<String, ArrayList<Material>> getMaterialData() {
	return constructionMap;
    }

    /**
     * get the material list under one particular construction
     * 
     * @param s
     * @return
     */
    protected ArrayList<Material> getMaterialList(String s) {
	return constructionMap.get(s);
    }

    /**
     * get the cost list for a specific construction
     * 
     * @param cons
     * @return
     */
    protected String[][] getCostListForConstruction(String cons) {
	ArrayList<Material> materialList = constructionMap.get(cons);
	String[][] costList = new String[materialList.size() + 1][rowElement];
	Double totalMaterial = 0.0;
	Double totalLabor = 0.0;
	Double totalEquipment = 0.0;
	Double totalTotal = 0.0;
	Double totalTotalOP = 0.0;
	
	String generalUnit =materialList.get(0).getMaterialUnit();
	
	for (int i = 0; i < materialList.size(); i++) {
	    Material m = materialList.get(i);
	    String[] costVector = new String[rowElement];
	    if (m.getCostInformation() != null) {
		Double[] costInfo = m.getCostInformation();
		// the first element in a vector is the material name
		costVector[0] = m.getMaterialDescription();
		costVector[1] = generalUnit;
		totalMaterial += costInfo[0];
		totalLabor += costInfo[1];
		totalEquipment += costInfo[2];
		totalTotal += costInfo[3];
		totalTotalOP += costInfo[4];
		for (int j = 0; j < costInfo.length; j++) {
		    costVector[j + 2] = df.format(costInfo[j]);
		}
		costList[i] = costVector;
	    } else {
		costList[i] = defaultCostData;
	    }
	}
	String[] totalVector = { "Total", generalUnit,df.format(totalMaterial),
		df.format(totalLabor), df.format(totalEquipment),
		df.format(totalTotal), df.format(totalTotalOP) };
	costList[costList.length - 1] = totalVector;
	return costList;
    }

    /**
     * get the entire construction list that is read from Energyplus file
     * 
     * @return
     */
    protected String[] getConstructionList() {
	Set<String> constructions = constructionMap.keySet();
	String[] consArray = new String[constructions.size()];
	Iterator<String> consIterator = constructions.iterator();

	int counter = 0;
	while (consIterator.hasNext() && counter < constructions.size()) {
	    consArray[counter] = consIterator.next();
	    counter++;
	}
	return consArray;
    }

    /**
     * set the user input which is later be used to select the cost
     * 
     * @param map
     * @param construction
     * @param index
     */
    protected void setUserInput(HashMap<String, String> map,
	    String construction, Integer index) {
	Material m = constructionMap.get(construction).get(index);
	m.setUserInputs(map);
    }

    /*
     * BELOW ARE PRE-PROCESSING CODE FOR THE CONSTRUCTION CATEGORY
     */
    private void processMaterialRawDatafromMaterials() {
	// require to do it again since the energyplus domain model may changed
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constructionList = reader
		.getObjectList(construction);
	Set<String> constructionCount = constructionList.get(construction)
		.keySet();
	Iterator<String> constructionIterator = constructionCount.iterator();
	while (constructionIterator.hasNext()) {
	    String count = constructionIterator.next();
	    ArrayList<ValueNode> materialList = constructionList.get(
		    construction).get(count);
	    String name = materialList.get(0).getAttribute();

	    // this is the unique name that we have in the construction object,
	    // so no need to check
	    constructionMap.put(name, new ArrayList<Material>());

	    for (int i = 1; i < materialList.size(); i++) {
		String materialName = materialList.get(i).getAttribute();
		Material newMaterial = new Material(materialName);
		fillInMaterialData(newMaterial);
		constructionMap.get(name).add(newMaterial);
	    }
	}
    }

    /**
     * checks material and material:nomass objects to fill up the data
     * 
     * @param m
     */
    private void fillInMaterialData(Material m) {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> materialList = reader
		.getObjectList(material);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> materialnomassList = reader
		.getObjectList(materialnomass);
	// check material objects
	Set<String> materialCount = materialList.get(material).keySet();
	Iterator<String> materialIterator = materialCount.iterator();
	while (materialIterator.hasNext()) {
	    String count = materialIterator.next();
	    ArrayList<ValueNode> materials = materialList.get(material).get(
		    count);
	    if (materials.get(0).getAttribute().equals(m.getName())) {
		for (int i = 1; i < materials.size(); i++) {
		    if (materials.get(i).getDescription().equals("Thickness")) {
			m.setThickness(materials.get(i).getAttribute());
		    } else if (materials.get(i).getDescription()
			    .equals("Conductivity")) {
			m.setConductivity(materials.get(i).getAttribute());
		    } else if (materials.get(i).getDescription()
			    .equals("Density")) {
			m.setDensity(materials.get(i).getAttribute());
		    } else if (materials.get(i).getDescription()
			    .equals("Specific Heat")) {
			m.setSpecificHeat(materials.get(i).getAttribute());
		    }
		}
	    }
	}
	// check material no mass objects
	Set<String> noMassCount = materialnomassList.get(materialnomass)
		.keySet();
	Iterator<String> noMassIterator = noMassCount.iterator();
	while (noMassIterator.hasNext()) {
	    String masscount = noMassIterator.next();
	    ArrayList<ValueNode> nomassMaterials = materialnomassList.get(
		    materialnomass).get(masscount);
	    if (nomassMaterials.get(0).getAttribute().equals(m.getName())) {
		for (int j = 1; j < nomassMaterials.size(); j++) {
		    if (nomassMaterials.get(j).getDescription()
			    .equals("Thermal Resistance")) {
			m.setResistance(nomassMaterials.get(j).getAttribute());
		    }
		}
	    }
	}
    }

    /**
     * process the raw data and convert it into HashMap<String, String[]> data
     * format, where the string specify the construction name and String[]
     * specify the data that this construction carries
     */
    private void processMaterialRawDatafromSurface() {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constructionList = reader
		.getObjectList(construction);

	Set<String> constructionCount = constructionList.get(construction)
		.keySet();
	Iterator<String> constructionIterator = constructionCount.iterator();
	while (constructionIterator.hasNext()) {
	    // find the construction
	    String count = constructionIterator.next();
	    String name = constructionList.get(construction).get(count).get(0)
		    .getAttribute();
	    // create the construction in the map
	    findSurfaceRelatedData(name, count);
	}
    }

    /**
     * find the data from zone and BuildingSurface:Detailed objects. This might
     * require duplicate the constructions since different height of space and
     * different surfacetype of the construction may yield different base cost
     * Filtering this differences and the method will duplicate the construction
     * and modify the correpondent BuildingSurface:Detail object
     * 
     * @param cons
     * @param constructionCount
     */
    private void findSurfaceRelatedData(String cons, String constructionCount) {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> surfaceMap = reader
		.getObjectList(surface);

	Set<String> surfaceList = surfaceMap.get(surface).keySet();
	Iterator<String> surfaceIterator = surfaceList.iterator();

	while (surfaceIterator.hasNext()) {
	    String surfaceCount = surfaceIterator.next();
	    ArrayList<ValueNode> nodes = surfaceMap.get(surface).get(
		    surfaceCount);

	    for (int i = 0; i < nodes.size(); i++) {
		if (nodes.get(i).getDescription().equals("Construction Name")
			&& nodes.get(i).getAttribute().equals(cons)) {
		    // the surface type is the previous node
		    String tempSurfaceType = nodes.get(i - 1).getAttribute();
		    // get the data from zone object
		    String[] zoneRelatedData = findZoneRelatedData(nodes.get(
			    i + 1).getAttribute());
		    String tempFloorArea = zoneRelatedData[0];
		    String tempCeilingHeight = zoneRelatedData[1];

		    // set the first iteration inputs and fix it
		    if (surfaceType == null) {
			surfaceType = tempSurfaceType;
		    }

		    if (floorArea == null) {
			floorArea = tempFloorArea;
		    }

		    if (ceilingHeight == null) {
			ceilingHeight = tempCeilingHeight;
		    }
		    
		    if(surfaceTypeList.contains(tempSurfaceType)){
			//condition when there is a copied surface type created
			//it only requires to change the construction name field in the surface
			String copiedName = cons+"_"+tempSurfaceType;
			reader.editExistObjectsOnOneElement(surface,
				surfaceCount, "Construction Name", copiedName);
			
			ArrayList<Material> temp = constructionMap.get(cons);
			setForAll(temp, tempFloorArea, tempCeilingHeight,
				tempSurfaceType);
			
		    }else if(ceilingHeightList.contains(tempCeilingHeight)){
			//condition when there is a copied surface type created
			//it only rquires to change the construction name field in the surface
			String copiedName = cons+" "+tempCeilingHeight;
			reader.editExistObjectsOnOneElement(surface,
				surfaceCount, "Construction Name", copiedName);
			
			ArrayList<Material> temp = constructionMap.get(cons);
			setForAll(temp, tempFloorArea, tempCeilingHeight,
				tempSurfaceType);
			
		    }else if (!tempSurfaceType.equals(surfaceType)){
			// condition when there is difference between surfacetype and tempsurfacetype
			//it will create a new copy in the domain model
			//and then change the construction name field in the surface
			String tempCount = reader.copyExistingEplusObject(
				construction, constructionCount);
			String copiedName = cons+"_"+tempSurfaceType;
			reader.editExistObjectsOnOneElementFromCount(construction, tempCount, "Name",copiedName);
			reader.editExistObjectsOnOneElementFromCount(surface,
				surfaceCount, "Construction Name", copiedName);
			surfaceTypeList.add(tempSurfaceType);
			// add this into the construction map
			ArrayList<Material> temp = cloneExistingMaterialList(constructionMap
				.get(cons));
			setForAll(temp, tempFloorArea, tempCeilingHeight,
				tempSurfaceType);
			constructionMap.put(copiedName, temp);
			
		    }else if(!tempCeilingHeight.equals(ceilingHeight)) {
			// condition when there is difference between ceiling height and ceiling height
			// it will create a new copy in the domain model
			// and then change the construction name field in the surface
			// copy the existing construction
			String tempCount = reader.copyExistingEplusObject(
				construction, constructionCount);
			String copiedName = cons + "_"+ceilingHeight;
			// change the copied construction name
			reader.editExistObjectsOnOneElementFromCount(construction,
				tempCount, "Name", copiedName);

			// change the current surface element's construction
			// name
			reader.editExistObjectsOnOneElementFromCount(surface,
				surfaceCount, "Construction Name", copiedName);
			ceilingHeightList.add(tempCeilingHeight);
			// add this into the construction map
			ArrayList<Material> temp = cloneExistingMaterialList(constructionMap
				.get(cons));
			setForAll(temp, tempFloorArea, tempCeilingHeight,
				tempSurfaceType);
			constructionMap.put(copiedName, temp);
		    } else {
			ArrayList<Material> temp = constructionMap.get(cons);
			setForAll(temp, tempFloorArea, tempCeilingHeight,
				tempSurfaceType);
		    }
		}
	    }
	}
    }

    private void setForAll(ArrayList<Material> materialList, String fa,
	    String ch, String st) {
	for (Material m : materialList) {
	    m.setFloorArea(fa);
	    m.setHeight(ch);
	    m.setSurfaceType(st);
	}
    }

    // find the data in the zone objects
    private String[] findZoneRelatedData(String zoneName) {
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> surfaceMap = reader
		.getObjectList(zone);
	String[] data = new String[2];
	Set<String> zoneList = surfaceMap.get(zone).keySet();
	Iterator<String> zoneIterator = zoneList.iterator();
	String volume = null;
	while (zoneIterator.hasNext()) {
	    String name = zoneIterator.next();
	    ArrayList<ValueNode> nodeList = surfaceMap.get(zone).get(name);
	    // name idnex
	    if (nodeList.get(0).getAttribute().equals(zoneName)) {
		for (ValueNode vn : nodeList) {
		    if (vn.getDescription().equals("Ceiling Height")) {
			data[1] = vn.getAttribute();
		    } else if (vn.getDescription().equals("Floor Area")) {
			data[0] = vn.getAttribute();
		    } else if (vn.getDescription().equals("Volume")) {
			volume = vn.getAttribute();
		    }
		}
	    }
	}
	if (data[0] == "") {
	    data[0] = "";
	}

	if (data[1] == "" || data[1].equals("autocalculate")) {
	    data[1] = "";
	}
	
	try{
	    Double v = Double.parseDouble(volume);
	    Double f = Double.parseDouble(data[0]);
	    Double c = v / f;
	    String ceiling = df.format(c);
	    data[1] = ceiling; 
	}catch(NumberFormatException e){
	    //do nothing
	}
	return data;
    }

    private ArrayList<Material> cloneExistingMaterialList(
	    ArrayList<Material> exist) {
	ArrayList<Material> temp = new ArrayList<Material>();
	for (Material m : exist) {
	    temp.add(m.clone());
	}
	return temp;
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
    public class Material {
	private final String materialName;
	private String[] properties = new String[stringArraySize];
	private MasterFormat material;

	public Material(String name) {
	    materialName = name;
	}

	public void setFloorArea(String fa) {
	    properties[floorAreaIndex] = fa;
	}

	public void setHeight(String h) {
	    properties[heightIndex] = h;
	}

	public void setSurfaceType(String surface) {
	    properties[surfaceTypeIndex] = surface;
	}

	public void setThickness(String thick) {
	    properties[thicknessIndex] = thick;
	}

	public void setConductivity(String cond) {
	    properties[conductivityIndex] = cond;
	}

	public void setDensity(String den) {
	    properties[densityIndex] = den;
	}

	public void setSpecificHeat(String spec) {
	    properties[specificHeatIndex] = spec;
	}

	public void setResistance(String resist) {
	    properties[resistanceIndex] = resist;
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
	
	public String getMaterialUnit(){
	    if(material==null){
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
	public void setMaterial(MasterFormat m) {
	    // life cycle call, properties will always be filled before this
	    // method
	    material = m;

	    properties = getProperties();
	    // make sure the resistance won't be empty if conductivity and
	    // thickness are there
	    if (properties[resistanceIndex] == ""
		    && properties[conductivityIndex] != ""
		    && properties[thicknessIndex] != "") {
		Double conductivity = Double
			.parseDouble(properties[conductivityIndex]);
		Double thickness = Double
			.parseDouble(properties[thicknessIndex]);
		Double resistance = thickness / conductivity;
		properties[resistanceIndex] = df.format(resistance);
	    }
	    material.setVariable(properties);
	}

	public ArrayList<String> getUserInputs() {
	    return material.getUserInputs();
	}

	public void setUserInputs(HashMap<String, String> map) {
	    material.setUserInputs(map);
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

	public Material clone() {
	    Material temp = new Material(this.materialName);
	    temp.setFloorArea(properties[floorAreaIndex]);
	    temp.setHeight(properties[heightIndex]);
	    temp.setSurfaceType(properties[surfaceTypeIndex]);
	    temp.setThickness(properties[thicknessIndex]);
	    temp.setConductivity(properties[conductivityIndex]);
	    temp.setDensity(properties[densityIndex]);
	    temp.setSpecificHeat(properties[specificHeatIndex]);
	    temp.setResistance(properties[resistanceIndex]);
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
