package eplus.construction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.geometry.Coordinate3D;
import eplus.geometry.EplusWindow;
import eplus.htmlparser.ZoneHTMLParser;

public class LightShelf implements BuildingComponent{
    // light shelf design factors
    private boolean insideShelf = false;
    private boolean outsideShelf = false;
    private double insideShelfDepth = 0.0;
    private double outsideShelfDepth = 0.0;
    private double shelfHeight = 0.0;
    private double shelfHeightPercentage = 0.0;
    private String orientation;

    private static final int SurfaceNumVerticeLoc = 9;
    private static final int SurfaceCoordsOffset = 10;
    private static final int ShadingSurfaceCoordsOffset = 4;
    
    
    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";

    // private String shelfConstruction;
    
    public LightShelf(){
	//initialization
    }

    public LightShelf(HashMap<String, Double> shelfProperty,
	    String orientation) {
	if (shelfProperty != null && shelfProperty.size() != 0) {
	    Set<String> shelfPropSet = shelfProperty.keySet();
	    Iterator<String> shelfIterator = shelfPropSet.iterator();
	    while (shelfIterator.hasNext()) {
		String propertyName = shelfIterator.next();
		if (propertyName.equals("InsideShelfDepth")) {
		    insideShelfDepth = shelfProperty.get(propertyName);
		    insideShelf = true;
		}
		if (propertyName.equals("OutsideShelfDepth")) {
		    outsideShelfDepth = shelfProperty.get(propertyName);
		    outsideShelf = true;
		}
		if (propertyName.equals("ShelfHeight")) {
		    shelfHeightPercentage = shelfProperty.get(propertyName);
		}
	    }
	}
	this.orientation = orientation;
    }
    
    
    public void readsInProperty(HashMap<String, Double> shelfProperty, String component){
	if (shelfProperty != null && shelfProperty.size() != 0) {
	    Set<String> shelfPropSet = shelfProperty.keySet();
	    Iterator<String> shelfIterator = shelfPropSet.iterator();
	    while (shelfIterator.hasNext()) {
		String propertyName = shelfIterator.next();
		if (propertyName.equals("InsideShelfDepth")) {
		    insideShelfDepth = shelfProperty.get(propertyName);
		}
		if (propertyName.equals("OutsideShelfDepth")) {
		    outsideShelfDepth = shelfProperty.get(propertyName);
		}
		if (propertyName.equals("ShelfHeight")) {
		    shelfHeightPercentage = shelfProperty.get(propertyName);
		}
	    }
	}
	
	orientation = "S";
    }
    
    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	//get building orientation
	HashMap<String, ArrayList<ValueNode>> bldg = reader.getObjectListCopy("Building");
	Double northAxis = 0.0;
	Iterator<String> bldgIterator = bldg.keySet().iterator();
	while(bldgIterator.hasNext()){
	    ArrayList<ValueNode> value = bldg.get(bldgIterator.next());
	    northAxis = Double.parseDouble(value.get(1).getAttribute());//1 is the locaito nof north axis
	}
	
	//readsInProperty(shelfProperty, component);
	
	// 1. insert shelf construction
	HashMap<String, EplusWindow> idfWindow = initWindows(reader);
	Set<String> windowSet = idfWindow.keySet();
	Iterator<String> windowIterator = windowSet.iterator();
	
	//check the inputs to make sure whether we should generate the inside shelf
	if(insideShelfDepth < 0.5){
	    insideShelf = false;
	}else{
	    insideShelf = true;
	}
	
	//check the inputs to make sure whether we should generate the inside shelf
	if(outsideShelfDepth < 0.5){
	    outsideShelf = false;
	}else{
	    outsideShelf = true;
	}
	//check the inputs to make sure whether we should generate the daylight shelf
	if(shelfHeightPercentage < 0.1){
	    insideShelf = false;
	    outsideShelf = false;
	}
	
	//System.out.println(insideShelfDepth + " " + outsideShelfDepth + " "+ shelfHeightPercentage + " " + insideShelf + " " + outsideShelf);
	
	int counter = 0;
	//System.out.println(shelfHeightPercentage + "" + insideShelf + " " + outsideShelf);
	while ((insideShelf!=false || outsideShelf!=false) && windowIterator.hasNext()) {
	    //System.out.println("working on it...");
	    String windowName = windowIterator.next();
	    EplusWindow win = idfWindow.get(windowName);
	    shelfHeight = win.getZDiff()*shelfHeightPercentage;
	    //System.out.println(shelfHeight);
	    Double azimuth = Double.valueOf(
		    ZoneHTMLParser.getFenestrationAzimuth(win.getName()));
	    azimuth = azimuth - northAxis;
	    //System.out.println(windowName + ": " + azimuth);

	    // 1. build inside shelf
	    Coordinate3D[] insideShelfCoord = null;
	    if (insideShelf) {
		insideShelfCoord = buildInsideShelf(win, azimuth);
	    }

	    // 2. build outside shelf
	    Coordinate3D[] outsideShelfCoord = null;
	    if (outsideShelf) {
		outsideShelfCoord = buildOutsideShelf(win, azimuth,
			insideShelfCoord);
	    }

	    // 3. separate the window
	    EplusWindow[] windows = separateWindow(win);

	    // export to EnergyPlus
	    // 4. deal with windows
	    HashMap<String, ArrayList<ValueNode>> feneSurfaces = reader
		    .getObjectListCopy("FenestrationSurface:Detailed");
	    ArrayList<ValueNode> fene = feneSurfaces.get(windows[0].getId());

	    // upper window - new addition
	    String[] objectValues = new String[fene.size()];
	    String[] objectDes = new String[fene.size()];
	    objectValues[0] = windows[0].getName();
	    objectDes[0] = "Name";
	    for (int j = 1; j < fene.size(); j++) {
		if (fene.get(j).getDescription()
			.equalsIgnoreCase("Frame and Divider Name")) {
		    objectValues[j] = "";
		} else {
		    objectValues[j] = fene.get(j).getAttribute();
		}
		objectDes[j] = fene.get(j).getDescription();
	    }

	    List<Coordinate3D> coord = windows[0].getCoords();
	    for (int k = 0; k < coord.size(); k++) {
		Coordinate3D point = coord.get(k);
		objectValues[k * 3 + SurfaceCoordsOffset] = String
			.valueOf(point.getX());
		objectValues[k * 3 + SurfaceCoordsOffset + 1] = String
			.valueOf(point.getY());
		objectValues[k * 3 + SurfaceCoordsOffset + 2] = String
			.valueOf(point.getZ());
	    }
	    // System.out.println(Arrays.toString(objectValues));
	    // System.out.println(Arrays.toString(objectDes));
	    // write-in
	    reader.addNewEnergyPlusObject("FenestrationSurface:Detailed",
		    objectValues, objectDes);

	    // lower window - revise
	    List<Coordinate3D> lowerCoord = windows[1].getCoords();
	    for (int j = 0; j < lowerCoord.size(); j++) {
		Coordinate3D point = lowerCoord.get(j);
		fene.get(j * 3 + SurfaceCoordsOffset)
			.setAttribute(String.valueOf(point.getX()));
		fene.get(j * 3 + SurfaceCoordsOffset + 1)
			.setAttribute(String.valueOf(point.getY()));
		fene.get(j * 3 + SurfaceCoordsOffset + 2)
			.setAttribute(String.valueOf(point.getZ()));
	    }

	    String insideShelfName = "";
	    String outsideShelfName = "";
	    // 5. set-up inside shelf
	    if (insideShelfCoord != null) {
		// insert inside shelf, which is a wall object
		setupInsideShelf(reader, fene, insideShelfCoord);
		insideShelfName = windowName + " Inside Shelf";
	    }

	    // 6. set-up outside shelf
	    if (outsideShelfCoord != null) {
		// insert outside shelf, which is a shading device
		setupOutsideShelf(reader, fene, outsideShelfCoord);
		outsideShelfName = windowName + " Outside Shelf";
	    }

	    if (insideShelfCoord != null || outsideShelfCoord != null) {
		String[] objectDaylit = { windowName + " Shelf",
			windowName + "_ShelfHost", insideShelfName,
			outsideShelfName, "SHELF" };
		String[] objectDaylitDes = { "Name", "Window Name",
			"Inside Shelf Name", "Outside Shelf Name",
			"Outside Shelf Construction Name"};

		reader.addNewEnergyPlusObject("DaylightingDevice:Shelf",
			objectDaylit, objectDaylitDes);

	    }
	    
	    counter ++;
	    
	    //insert cost
	    
	    // 3. insert cost object to eplus
	    String[] values = {windowName + " Shelf Cost", "",
		    "DaylightingDevice:Shelf", windowName + " Shelf", "", "100.0", "", "",
		    "", "", "", "", Integer.toString(counter) };
	    String[] description = componentCostDescription.split(":");
	    reader.addNewEnergyPlusObject(componentCostObject, values,
		    description);
	    
	}
	
	insertShelfConstruction(reader);	
	
    }

    private void insertShelfConstruction(IdfReader reader) {
	String[] objectConstruction = { "SHELF",
		"HW Concrete - Painted White" };
	String[] objectConsDes = { "Name", "Outside Layer" };

	reader.addNewEnergyPlusObject("Construction", objectConstruction,
		objectConsDes);

	String[] objectMaterial = { "HW Concrete - Painted White",
		"MediumRough", "5.0901599E-02", "1.729577", "2242.585", "836.8",
		"0.90", "0.30", "0.30" };
	String[] objectMatDes = { "Name", "Roughness", "Thickness {m}",
		"Conductivity {W/m-K}", "Density {kg/m3}",
		"Specific Heat {J/kg-K}", "Thermal Absorptance",
		"Solar Absorptance", "Visible Absorptance" };
	reader.addNewEnergyPlusObject("Material", objectMaterial, objectMatDes);
    }

    private void setupInsideShelf(IdfReader reader, ArrayList<ValueNode> fene,
	    Coordinate3D[] insideShelfCoord) {
	// 1. find out the host wall
	String hostWall = null;
	for (int i = 0; i < fene.size(); i++) {
	    ValueNode node = fene.get(i);
	    if (node.getDescription()
		    .equalsIgnoreCase("Building Surface Name")) {
		hostWall = node.getAttribute();
	    }
	}

	// 2. get the zone
	String zoneName = null;
	if (hostWall != null) {
	    HashMap<String, ArrayList<ValueNode>> buildingSurfaces = reader
		    .getObjectListCopy("BuildingSurface:Detailed");
	    // System.out.println(hostWall);

	    ArrayList<ValueNode> wall = null;
	    Iterator<String> bldgSurface = buildingSurfaces.keySet().iterator();
	    while (bldgSurface.hasNext()) {
		String counter = bldgSurface.next();
		ArrayList<ValueNode> temp = buildingSurfaces.get(counter);
		if (temp.get(0).getAttribute().equalsIgnoreCase(hostWall)) {
		    wall = temp;
		}
	    }
	    for (int i = 0; i < wall.size(); i++) {
		ValueNode node = wall.get(i);
		if (node.getDescription().equalsIgnoreCase("Zone Name")) {
		    zoneName = node.getAttribute();
		}
	    }
	} else {
	    System.err.println("No host wall found for this window");
	}

	// 3. insert the wall
	if (zoneName != null) {
	    String[] objectDes = { "Name", "Surface Type", "Construction Name",
		    "Zone Name", "Outside Boundary Condition",
		    "Outside Boundary Condition Object", "Sun Exposure",
		    "Wind Exposure", "View Factor to Ground",
		    "Number of Vertices", "Vertex 1 X-coordinate",
		    "Vertex 1 Y-coordinate", "Vertex 1 Z-coordinate",
		    "Vertex 2 X-coordinate", "Vertex 2 Y-coordinate",
		    "Vertex 2 Z-coordinate", "Vertex 3 X-coordinate",
		    "Vertex 3 Y-coordinate", "Vertex 3 Z-coordinate",
		    "Vertex 4 X-coordinate", "Vertex 4 Y-coordinate",
		    "Vertex 4 Z-coordinate" };
	    String[] objectValues = new String[objectDes.length];
	    objectValues[0] = fene.get(0).getAttribute() + " Inside Shelf";
	    objectValues[1] = "WALL";
	    objectValues[2] = "Shelf";
	    objectValues[3] = zoneName;
	    objectValues[4] = "Surface";
	    objectValues[5] = fene.get(0).getAttribute() + " Inside Shelf";
	    objectValues[6] = "NoSun";
	    objectValues[7] = "NoWind";
	    objectValues[8] = "0";
	    objectValues[9] = "4";

	    for (int j = 0; j < insideShelfCoord.length; j++) {
		Coordinate3D point = insideShelfCoord[j];
		objectValues[j * 3 + SurfaceCoordsOffset] = String
			.valueOf(point.getX());
		objectValues[j * 3 + SurfaceCoordsOffset + 1] = String
			.valueOf(point.getY());
		objectValues[j * 3 + SurfaceCoordsOffset + 2] = String
			.valueOf(point.getZ());
	    }

	    reader.addNewEnergyPlusObject("BuildingSurface:Detailed",
		    objectValues, objectDes);
	} else {
	    System.err.println("No host zone found for this wall");
	}
    }

    private void setupOutsideShelf(IdfReader reader, ArrayList<ValueNode> fene,
	    Coordinate3D[] outsideShelfCoord) {
	// 1. find out the host wall
	String hostWall = null;
	for (int i = 0; i < fene.size(); i++) {
	    ValueNode node = fene.get(i);
	    if (node.getDescription()
		    .equalsIgnoreCase("Building Surface Name")) {
		hostWall = node.getAttribute();
	    }
	}

	if (hostWall != null) {
	    String[] objectDes = { "Name", "Base Surface Name",
		    "Transmittance Schedule Name", "Number of Vertices",
		    "Vertex 1 X-coordinate", "Vertex 1 Y-coordinate",
		    "Vertex 1 Z-coordinate", "Vertex 2 X-coordinate",
		    "Vertex 2 Y-coordinate", "Vertex 2 Z-coordinate",
		    "Vertex 3 X-coordinate", "Vertex 3 Y-coordinate",
		    "Vertex 3 Z-coordinate", "Vertex 4 X-coordinate",
		    "Vertex 4 Y-coordinate", "Vertex 4 Z-coordinate" };
	    String[] objectValues = new String[objectDes.length];
	    objectValues[0] = fene.get(0).getAttribute() + " Outside Shelf";
	    objectValues[1] = hostWall;
	    objectValues[2] = "";
	    objectValues[3] = "4";

	    for (int j = 0; j < outsideShelfCoord.length; j++) {
		Coordinate3D point = outsideShelfCoord[j];
		objectValues[j * 3 + ShadingSurfaceCoordsOffset] = String
			.valueOf(point.getX());
		objectValues[j * 3 + ShadingSurfaceCoordsOffset + 1] = String
			.valueOf(point.getY());
		objectValues[j * 3 + ShadingSurfaceCoordsOffset + 2] = String
			.valueOf(point.getZ());
	    }

	    reader.addNewEnergyPlusObject("Shading:Zone:Detailed", objectValues,
		    objectDes);
	} else {
	    System.err.println("No host wall found for this window");
	}
    }

    /**
     * This function separate one window into TWO WINDOWS. the separation point
     * is based on the height
     * 
     * @return Two EplusWindows
     */
    private EplusWindow[] separateWindow(EplusWindow win) {
	// B---------A
	// | 	     |
	// B*        A*
	// |         |
	// C---------D
	List<Coordinate3D> coord = win.getCoords();
	ArrayList<Coordinate3D> coordArray = new ArrayList<Coordinate3D>();
	Iterator<Coordinate3D> iter = coord.iterator();
	int pointAIndex = 0;
	int pointBIndex = 0;
	int pointCIndex = 0;
	int pointDIndex = 0;

	// re-structuring data structure.
	while (iter.hasNext()) {
	    coordArray.add(iter.next());
	}

	double highA = 0;
	double highB = 0;
	// search and fill out the data - only need to exam maximum 2 points
	for (int i = 0; i < 2; i++) {
	    Coordinate3D c3d = coordArray.get(i);
	    if (i == 0) {
		highA = c3d.getZ();
	    } else if (i == 1) {
		highB = c3d.getZ();
		if (Math.abs(highA - highB) > 0.001) {
		    if (highA > highB) {
			// scenario B-C-D-A
			pointAIndex = coordArray.size() - 1;
			pointBIndex = 0;
		    } else if (highA < highB) {
			pointAIndex = i;
			pointBIndex = i + 1;
		    }
		} else if (Math.abs(highA - highB) <= 0.001) {
		    double highC = coordArray.get(i + 1).getZ();
		    if (highC > highA) {
			pointAIndex = i + 1;
			pointBIndex = i + 2;
		    } else {
			pointAIndex = 0;
			pointBIndex = 1;
		    }
		}
	    }
	}
	pointCIndex = (pointBIndex + 1) % coordArray.size();
	pointDIndex = (pointBIndex + 2) % coordArray.size();

	Coordinate3D aHash = coordArray.get(pointAIndex).duplicate();
	aHash.setZ(aHash.getZ() - shelfHeight);

	Coordinate3D bHash = coordArray.get(pointBIndex).duplicate();
	bHash.setZ(bHash.getZ() - shelfHeight);

	List<Coordinate3D> upperWindowCoord = new LinkedList<Coordinate3D>();
	//follow: b* - a* - a - b (start from left corner)
	upperWindowCoord.add(bHash);
	upperWindowCoord.add(aHash);
	upperWindowCoord.add(coordArray.get(pointAIndex).duplicate());
	upperWindowCoord.add(coordArray.get(pointBIndex).duplicate());
	

	List<Coordinate3D> lowerWindowCoord = new LinkedList<Coordinate3D>();
	//follow: c - d - a* - b* (start from left corner)
	lowerWindowCoord.add(coordArray.get(pointCIndex).duplicate());
	lowerWindowCoord.add(coordArray.get(pointDIndex).duplicate());
	lowerWindowCoord.add(aHash.duplicate());
	lowerWindowCoord.add(bHash.duplicate());
	

	EplusWindow[] windows = new EplusWindow[2];
	windows[0] = new EplusWindow(upperWindowCoord,
		win.getName() + "_ShelfHost", win.getId());
	windows[1] = new EplusWindow(lowerWindowCoord, win.getName(),
		win.getId());

	return windows;
    }

    private Coordinate3D[] buildOutsideShelf(EplusWindow win, double azimuth,
	    Coordinate3D[] insideShelfCoord) {
	// 1. judging the direction of the outside shelf
	int x_dir = 1;
	int y_dir = 1;
	if (azimuth >= 0 && azimuth < 90) {
	    x_dir = 1;
	    y_dir = 1;
	} else if (azimuth >= 90 && azimuth < 180) {
	    x_dir = -1;
	    y_dir = 1;
	} else if (azimuth >= 180 && azimuth < 270) {
	    x_dir = -1;
	    y_dir = -1;
	} else {
	    x_dir = 1;
	    y_dir = -1;
	}

	// find the two coordinates
	Coordinate3D pointA = null;// starting point
	int pointAIndex = 0;
	Coordinate3D pointB = null;// second point
	int pointBIndex = 0;
	if (insideShelfCoord != null) {
	    pointA = insideShelfCoord[0];
	    pointB = insideShelfCoord[1];
	} else {
	    List<Coordinate3D> coord = win.getCoords();
	    ArrayList<Coordinate3D> coordArray = new ArrayList<Coordinate3D>();
	    Iterator<Coordinate3D> iter = coord.iterator();
	    double highA = 0;
	    double highB = 0;
	    // re-structuring data structure.
	    while (iter.hasNext()) {
		coordArray.add(iter.next());
	    }
	    // search and fill out the data - only need to exam maximum 2 points
	    for (int i = 0; i < 2; i++) {
		Coordinate3D c3d = coordArray.get(i);
		if (i == 0) {
		    highA = c3d.getZ();
		} else if (i == 1) {
		    highB = c3d.getZ();
		    if (Math.abs(highA - highB) > 0.001) {
			if (highA > highB) {
			    // scenario B-C-D-A
			    pointAIndex = coordArray.size() - 1;
			    pointBIndex = 0;
			} else {
			    pointAIndex = i;
			    pointBIndex = i + 1;
			}
		    } else if (Math.abs(highA - highB) <= 0.001) {
			double highC = coordArray.get(i + 1).getZ();
			if (highC > highA) {
			    pointAIndex = i + 1;
			    pointBIndex = i + 2;
			} else {
			    pointAIndex = 0;
			    pointBIndex = 1;
			}
		    }
		}
	    }
	    pointA = coordArray.get(pointAIndex).duplicate();
	    pointA.setZ(pointA.getZ() - shelfHeight);
	    pointB = coordArray.get(pointBIndex).duplicate();
	    pointB.setZ(pointB.getZ() - shelfHeight);
	}
	
	// 3. calculate differences for the other two coordinates
	double radians = Math.toRadians(azimuth);
	//double deltaX = Math.abs(format(Math.sin(radians))) * outsideShelfDepth* x_dir;//round to 2 decimal points
	double deltaY = Math.abs(format(Math.cos(radians))) * outsideShelfDepth* y_dir;//round to 2 decimal points
	
	
	// 4. calculate the rest two points
	Coordinate3D smallPointA = pointA.duplicate();
	Coordinate3D smallPointB = pointB.duplicate();
	smallPointA.setX(smallPointA.getX());
	smallPointA.setY(smallPointA.getY() + deltaY);
	smallPointB.setX(smallPointB.getX());
	smallPointB.setY(smallPointB.getY() + deltaY);

	// 5. return
	Coordinate3D[] result = {smallPointA, pointA, pointB, smallPointB};
	return result;
    }

    private Coordinate3D[] buildInsideShelf(EplusWindow win, double azimuth) {
	// 1. judging the direction of the inside shelf
	int x_dir = 1;
	int y_dir = 1;
	if (azimuth >= 0 && azimuth < 90) {
	    x_dir = -1;
	    y_dir = -1;
	} else if (azimuth >= 90 && azimuth < 180) {
	    x_dir = 1;
	    y_dir = -1;
	} else if (azimuth >= 180 && azimuth < 270) {
	    x_dir = 1;
	    y_dir = 1;
	} else {
	    x_dir = -1;
	    y_dir = 1;
	}

	// 2. find the two coordinates on the window

	// B---------A
	// | |
	// | |
	// | |
	// C---------D

	List<Coordinate3D> coord = win.getCoords();
	ArrayList<Coordinate3D> coordArray = new ArrayList<Coordinate3D>();
	Iterator<Coordinate3D> iter = coord.iterator();
	Coordinate3D pointA = null;// starting point
	int pointAIndex = 0;
	Coordinate3D pointB = null;// second point
	int pointBIndex = 0;

	double highA = 0;
	double highB = 0;
	// re-structuring data structure.
	while (iter.hasNext()) {
	    coordArray.add(iter.next());
	}
	// search and fill out the data - only need to exam maximum 2 points
	for (int i = 0; i < 2; i++) {
	    Coordinate3D c3d = coordArray.get(i);
	    if (i == 0) {
		highA = c3d.getZ();
	    } else if (i == 1) {
		highB = c3d.getZ();
		if (Math.abs(highA - highB) > 0.001) {
		    if (highA > highB) {
			// scenario B-C-D-A
			pointAIndex = coordArray.size() - 1;
			pointBIndex = 0;
		    } else {
			pointAIndex = i;
			pointBIndex = i + 1;
		    }
		} else if (Math.abs(highA - highB) <= 0.001) {
		    double highC = coordArray.get(i + 1).getZ();
		    if (highC > highA) {
			pointAIndex = i + 1;
			pointBIndex = i + 2;
		    } else {
			pointAIndex = 0;
			pointBIndex = 1;
		    }
		}
	    }
	}
	pointA = coordArray.get(pointAIndex).duplicate();
	pointA.setZ(pointA.getZ() - shelfHeight);
	pointB = coordArray.get(pointBIndex).duplicate();
	pointB.setZ(pointB.getZ() - shelfHeight);

	// 3. calculate differences for the other two coordinates
	double radians = Math.toRadians(azimuth);
	double deltaX = Math.abs(format(Math.sin(radians))) * x_dir * insideShelfDepth;
	double deltaY = Math.abs(format(Math.cos(radians))) * y_dir * insideShelfDepth;

	// 4. calculate the rest two points
	Coordinate3D smallPointA = pointA.duplicate();
	Coordinate3D smallPointB = pointB.duplicate();
	smallPointA.setX(smallPointA.getX() + deltaX);
	smallPointA.setY(smallPointA.getY() + deltaY);
	smallPointB.setX(smallPointB.getX() + deltaX);
	smallPointB.setY(smallPointB.getY() + deltaY);

	// 5. return
	Coordinate3D[] result = {pointA, pointB, smallPointB, smallPointA };
	return result;

    }

    private HashMap<String, EplusWindow> initWindows(IdfReader reader) {
	HashMap<String, EplusWindow> idfWindow = new HashMap<String, EplusWindow>();
	// caution, this is actual model data, not a copy, carefully revise
	HashMap<String, ArrayList<ValueNode>> feneSurfaces = reader
		.getObjectListCopy("FenestrationSurface:Detailed");

	Set<String> names = feneSurfaces.keySet();

	for (String name : names) {
	    ArrayList<ValueNode> info = feneSurfaces.get(name);
	    ValueNode type = info.get(1);
	    if (type.getAttribute().equalsIgnoreCase("Window")) {
		if (ZoneHTMLParser
			.getFenestrationOrientation(info.get(0).getAttribute())
			.equals(orientation)) {
		    List<Coordinate3D> coords = this.readSurfaceCoords(info);
		    String feneSurfaceName = info.get(0).getAttribute();
		    //System.out.println(feneSurfaceName);
		    // no need to record id in this case
		    idfWindow.put(feneSurfaceName,
			    new EplusWindow(coords, feneSurfaceName, name));
		}
	    }
	}
	// System.out.println(idfWindow.size());
	return idfWindow;
    }

    /**
     * Same for BuildingSurface:Detailed and FenestrationSurface:Detailed
     * 
     * @param attrs
     * @return
     */
    private List<Coordinate3D> readSurfaceCoords(ArrayList<ValueNode> attrs) {
	List<Coordinate3D> coords = new LinkedList<Coordinate3D>();
	int numVertices = Integer
		.valueOf(attrs.get(SurfaceNumVerticeLoc).getAttribute());
	for (int i = 0; i < numVertices; i++) {
	    double x = Double.parseDouble(
		    attrs.get(i * 3 + SurfaceCoordsOffset).getAttribute());
	    double y = Double.parseDouble(
		    attrs.get(i * 3 + SurfaceCoordsOffset + 1).getAttribute());
	    double z = Double.parseDouble(
		    attrs.get(i * 3 + SurfaceCoordsOffset + 2).getAttribute());
	    Coordinate3D coord = new Coordinate3D(x, y, z);
	    coords.add(coord);
	}
	return coords;
    }
    
    private double format(double value){
	return (double)Math.round(value * 1E10) / 1E10;
    }

    @Override
    public Double getMaterialPrice() {
	return 100.0;
    }

    @Override
    public Double getLaborPrice() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Double getEquipmentPrice() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Double getTotalPrice() {
	return 100.0;
    }

    @Override
    public Double getTotalInclOPPrice() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getUnit() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Double[] getCostVector() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public String getHierarchy() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getDescription() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub
	
    }

    @Override
    public boolean isUserInputsRequired() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public ArrayList<String> getUserInputs() {
	
	return null;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public ArrayList<String> getOptionListFromObjects() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ArrayList<Integer> getQuantitiesFromObjects() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public double randomDrawTotalCost() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return "LightShelf";
    }

    @Override
    public String[] getListAvailableComponent() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public String[] getSelectedComponents() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	if(Index == 0){
	    return "ShelfHeight:0:0.3";
	}else if(Index == 1){
	    return "InsideShelfDepth:0:1.5";
	}else if(Index == 2){
	    return "OutsideShelfDepth:0:1.5";
	}
	return null;
    }

    @Override
    public double getComponentCost(Document doc) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean isIntegerTypeComponent() {
	/*
	 *This object is a numerical type object
	 */
	return false;
    }

    @Override
    public int getNumberOfVariables() {
	/*
	 * 3 variables: depth, inside shelf length, outside shelf length
	 */
	return 3;
    }

    @Override
    public String[] getSelectedComponentsForRetrofit() {
	// TODO Auto-generated method stub
	return null;
    }
    
    //test
    public static void main(String[] args) throws IOException{
	IdfReader reader = new IdfReader();
	reader.setFilePath("E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\1MP lightshelf\\Both\\Both.idf");
	reader.readEplusFile();
	
	ZoneHTMLParser.processOutputs(new File("E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\1MP lightshelf\\Both\\BothTable.html"));
	LightShelf shelf = new LightShelf();
	HashMap<String,Double> property = new HashMap<String, Double>();
	property.put("ShelfHeight", 0.3);
	property.put("InsideShelfDepth", 1.25);
	property.put("OutsideShelfDepth", 0.0);
	
	shelf.readsInProperty(property, "");
	
	shelf.writeInEnergyPlus(reader, "");
	
	reader.WriteIdf("E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\1MP lightshelf\\InteriorLightShelf\\1.25", "Design");
	
    }

}
