package eplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader.ValueNode;

/**
 * Currently material Analyzer could extra the following information: Floor
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

    private final int stringArraySize = 8;

    private final static String construction = "Construction";
    private final static String material = "Material";
    private final static String materialnomass = "Material:NoMass";
    private final static String zone = "Zone";
    private final static String surface = "BuildingSurface:Detailed";
    
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex=2;
    protected final int thicknessIndex=3;
    protected final int conductivityIndex=4;
    protected final int densityIndex=5;
    protected final int specificHeatIndex=6;
    protected final int resistanceIndex=7;

    public MaterialAnalyzer(IdfReader reader) {
	this.reader = reader;
	constructionMap = new HashMap<String, ArrayList<Material>>();
	processMaterialRawDatafromMaterials();
	processMaterialRawDatafromSurface();
    }
    
    public HashMap<String, ArrayList<Material>> getMaterialData(){
	return constructionMap;
    }
    
    private void processMaterialRawDatafromMaterials(){
	//require to do it again since the energyplus domain model may changed
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> constructionList = reader
		.getObjectList(construction);
	Set<String> constructionCount = constructionList.get(construction).keySet();
	Iterator<String> constructionIterator = constructionCount.iterator();
	while(constructionIterator.hasNext()){
	    String count = constructionIterator.next();
	    ArrayList<ValueNode> materialList=constructionList.get(construction).get(count);
	    String name = materialList.get(0).getAttribute();

	    //this is the unique name that we have in the construction object, so no need to check
	    constructionMap.put(name, new ArrayList<Material>());
	    
	    for(int i=1; i<materialList.size(); i++){
		String materialName = materialList.get(i).getAttribute();
		Material newMaterial = new Material(materialName);
		fillInMaterialData(newMaterial);
		constructionMap.get(name).add(newMaterial);
	    }
	    
	}
    }
    
    /**
     * checks material and material:nomass objects to fill up the data
     * @param m
     */
    private void fillInMaterialData(Material m){
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> materialList = reader
		.getObjectList(material);
	HashMap<String, HashMap<String, ArrayList<ValueNode>>> materialnomassList = reader
		.getObjectList(materialnomass);
	//check material objects
	Set<String> materialCount = materialList.get(material).keySet();
	Iterator<String> materialIterator = materialCount.iterator();
	while(materialIterator.hasNext()){
	    String count = materialIterator.next();
	    ArrayList<ValueNode> materials = materialList.get(material).get(count);
	    if(materials.get(0).getAttribute().equals(m.getName())){
		for(int i=1; i<materials.size(); i++){
		    if(materials.get(i).getDescription().equals("Thickness")){
			m.setThickness(materials.get(i).getAttribute());
		    }else if(materials.get(i).getDescription().equals("Conductivity")){
			m.setConductivity(materials.get(i).getAttribute());
		    }else if(materials.get(i).getDescription().equals("Density")){
			m.setDensity(materials.get(i).getAttribute());
		    }else if(materials.get(i).getDescription().equals("Specific Heat")){
			m.setSpecificHeat(materials.get(i).getAttribute());
		    }
		}
	    }
	}
	//check material no mass objects
	Set<String> noMassCount = materialnomassList.get(materialnomass).keySet();
	Iterator<String> noMassIterator = noMassCount.iterator();
	while(noMassIterator.hasNext()){
	    String masscount = noMassIterator.next();
	    ArrayList<ValueNode> nomassMaterials = materialnomassList.get(materialnomass).get(masscount);
	    if(nomassMaterials.get(0).getAttribute().equals(m.getName())){
		for(int j=1; j<nomassMaterials.size(); j++){
		    if(nomassMaterials.get(j).getDescription().equals("Thermal Resistance")){
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

	Set<String> constructionCount = constructionList.get(construction).keySet();
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
     * requrie duplicate the constructions since different height of space and
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

	// only extract these three values
	String floorArea = null; // index 0
	String ceilingHeight = null; // index 1
	String surfaceType = null; // index 2

	Integer copiedCounter = 0;// this indicates how many copes require to
				  // make for this type of construction

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

		    // if there is anything different
		    if (!tempSurfaceType.equals(surfaceType)
			    || !tempCeilingHeight.equals(ceilingHeight)) {
			// the modification is at idf domain model level, not on
			// the copied model level
			// copy the existing construction
			String tempCount = reader.copyExistingEplusObject(
				construction, constructionCount);
			String copiedName = cons + copiedCounter.toString();
			// change the copied construction name
			reader.editExistObjectsOnOneElement(construction,
				tempCount, "Name", copiedName);
			copiedCounter++;
			// change the current surface element's construction
			// name
			reader.editExistObjectsOnOneElement(surface,
				surfaceCount, "Construction Name", copiedName);
			// add this into the construction map
			ArrayList<Material> temp = cloneExistingMaterialList(constructionMap.get(cons));
			setForAll(temp, tempFloorArea, tempCeilingHeight, tempSurfaceType);
			constructionMap.put(copiedName,temp);
		    } else {
			ArrayList<Material> temp = constructionMap.get(cons);
			setForAll(temp, tempFloorArea, tempCeilingHeight, tempSurfaceType);
		    }
		}
	    }
	}
    }
    
    private void setForAll(ArrayList<Material> materialList, String fa, String ch, String st){
	for(Material m: materialList){
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
		    } else if(vn.getDescription().equals("Volume")){
			volume = vn.getAttribute();
		    }
		}
	    }
	}
	if (data[0] == null) {
	    data[0] = "";
	}
	
	if (data[1] == null||data[1].equals("autocalculate")) {
	    data[1] = "";
	}else if(volume!=null && data[0]!=null){
	    Double v = Double.parseDouble(volume);
	    Double f = Double.parseDouble(data[0]);
	    Double c = v/f;
	    DecimalFormat df = new DecimalFormat("###.##");
	    String ceiling = df.format(c);
	    data[1]=ceiling;
	}
	
	return data;
    }
    
    private ArrayList<Material> cloneExistingMaterialList(ArrayList<Material> exist){
	ArrayList<Material> temp = new ArrayList<Material>();
	for(Material m: exist){
	    temp.add(m.clone());
	}
	return temp;
    }
    
    public class Material{
	private final String materialName;
	private String[] properties = new String[stringArraySize];
	
	public Material(String name){
	    materialName = name;
	}
	
	public void setFloorArea(String fa){
	    properties[floorAreaIndex] =fa; 
	}
	
	public void setHeight(String h){
	    properties[heightIndex] = h;
	}
	
	public void setSurfaceType(String surface){
	    properties[surfaceTypeIndex] = surface;
	}
	
	public void setThickness(String thick){
	    properties[thicknessIndex]=thick;
	}
	
	public void setConductivity(String cond){
	    properties[conductivityIndex] = cond;
	}
	
	public void setDensity(String den){
	    properties[densityIndex] = den;
	}
	
	public void setSpecificHeat(String spec){
	    properties[specificHeatIndex] = spec;
	}
	
	public void setResistance(String resist){
	    properties[resistanceIndex] = resist;
	}
	
	public String[] getProperties(){
	    for(int i=0; i<properties.length; i++){
		if(properties[i]==null){
		    properties[i]="";
		}
	    }
	    return properties;
	}
	
	public String getName(){
	    return materialName;
	}
	
	public Material clone(){
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
    }
    
}
