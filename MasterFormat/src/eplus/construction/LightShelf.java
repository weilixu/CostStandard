package eplus.construction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.geometry.Coordinate3D;
import eplus.geometry.EplusWindow;
import eplus.htmlparser.ZoneHTMLParser;

public class LightShelf {
    // light shelf design factors
    private boolean insideShelf = false;
    private boolean outsideShelf = false;
    private double insideShelfDepth = 0.0;
    private double outsideShelfDepth = 0.0;
    private double insideShelfHeight = 0.0;
    private double outsideShelfHeight = 0.0;
    private String orientation;

    private static final int SurfaceNumVerticeLoc = 9;
    private static final int SurfaceCoordsOffset = 10;
    private static final int ShadingSurfaceCoordsOffset = 4;

    // private String shelfConstruction;

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
		if (propertyName.equals("InsideShelfHeight")) {
		    insideShelfHeight = shelfProperty.get(propertyName);
		    insideShelf = true;
		}
		if (propertyName.equals("OutsideShelfHeight")) {
		    outsideShelfHeight = shelfProperty.get(propertyName);
		    outsideShelf = true;
		}
	    }
	}

	// check whether inside and outside light shelves are in the same level.
	if (insideShelfHeight != outsideShelfHeight) {
	    System.err.println(
		    "The inside and outside light shelf should be designed at a same level");
	}

	this.orientation = orientation;
    }

    public void writeInEnergyPlus(IdfReader reader, String component) {
	// 1. insert shelf construction
	HashMap<String, EplusWindow> idfWindow = initWindows(reader);
	Set<String> windowSet = idfWindow.keySet();
	Iterator<String> windowIterator = windowSet.iterator();

	while (windowIterator.hasNext()) {
	    String windowName = windowIterator.next();
	    EplusWindow win = idfWindow.get(windowName);
	    Double azimuth = Double.valueOf(
		    ZoneHTMLParser.getFenestrationAzimuth(win.getName()));
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
		objectValues[j] = fene.get(j).getAttribute();
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
	    // 5. set-up inside shelf
	    if (insideShelfCoord != null) {
		// insert inside shelf, which is a wall object
		setupInsideShelf(reader, fene, insideShelfCoord);
	    }

	    // 6. set-up outside shelf
	    if (outsideShelfCoord != null) {
		// insert outside shelf, which is a shading device
		setupOutsideShelf(reader, fene, outsideShelfCoord);
	    }
	}
    }

    private void setupInsideShelf(IdfReader reader, ArrayList<ValueNode> fene, Coordinate3D[] insideShelfCoord) {
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
	    ArrayList<ValueNode> wall = buildingSurfaces.get(hostWall);
	    for(int i=0; i<wall.size(); i++){
		ValueNode node = wall.get(i);
		if(node.getDescription().equalsIgnoreCase("Zone Name")){
		    zoneName = node.getAttribute();
		}
	    }
	}else{
	    System.err.println("No host wall found for this window");
	}
	
	//3. insert the wall
	if(zoneName!=null){
	    String[] objectDes = {"Name","Surface Type","Construction Name","Zone Name","Outside Boundary Condition",
		    "Outside Boundary Condition Object","Sun Exposure","Wind Exposure","View Factor to Ground","Number of Vertices",
		    "Vertex 1 X-coordinate","Vertex 1 Y-coordinate","Vertex 1 Z-coordinate",
		    "Vertex 2 X-coordinate","Vertex 2 Y-coordinate","Vertex 2 Z-coordinate",
		    "Vertex 3 X-coordinate","Vertex 3 Y-coordinate","Vertex 3 Z-coordinate",
		    "Vertex 4 X-coordinate","Vertex 4 Y-coordinate","Vertex 4 Z-coordinate"};
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
		objectValues[j*3+SurfaceCoordsOffset] = String.valueOf(point.getX());
		objectValues[j*3+SurfaceCoordsOffset + 1] = String.valueOf(point.getY());
		objectValues[j*3+SurfaceCoordsOffset + 2] = String.valueOf(point.getZ());
	    }
	    
	    reader.addNewEnergyPlusObject("BuildingSurface:Detailed", objectValues, objectDes);
	}else{
	    System.err.println("No host zone found for this wall");
	}
	
	
    }

    private void setupOutsideShelf(IdfReader reader, ArrayList<ValueNode> fene, Coordinate3D[] outsideShelfCoord) {
	// 1. find out the host wall
	String hostWall = null;
	for (int i = 0; i < fene.size(); i++) {
	    ValueNode node = fene.get(i);
	    if (node.getDescription()
		    .equalsIgnoreCase("Building Surface Name")) {
		hostWall = node.getAttribute();
	    }
	}
	
	if(hostWall!=null){
	    String[] objectDes = {"Name","Base Surface Name","Transmittance Schedule Name","Number of Vertices",
		    "Vertex 1 X-coordinate","Vertex 1 Y-coordinate","Vertex 1 Z-coordinate",
		    "Vertex 2 X-coordinate","Vertex 2 Y-coordinate","Vertex 2 Z-coordinate",
		    "Vertex 3 X-coordinate","Vertex 3 Y-coordinate","Vertex 3 Z-coordinate",
		    "Vertex 4 X-coordinate","Vertex 4 Y-coordinate","Vertex 4 Z-coordinate"};
	    String[] objectValues = new String[objectDes.length];
	    objectValues[0] = fene.get(0).getAttribute() + " Outside Shelf";
	    objectValues[1] = hostWall;
	    objectValues[2] = "";
	    objectValues[3] = "4";
	    
	    for (int j = 0; j < outsideShelfCoord.length; j++) {
		Coordinate3D point = outsideShelfCoord[j];
		objectValues[j*3+ShadingSurfaceCoordsOffset] = String.valueOf(point.getX());
		objectValues[j*3+ShadingSurfaceCoordsOffset + 1] = String.valueOf(point.getY());
		objectValues[j*3+ShadingSurfaceCoordsOffset + 2] = String.valueOf(point.getZ());
	    }
	    
	    reader.addNewEnergyPlusObject("Shading:Zone:Detailed", objectValues, objectDes);
	}else{
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
	// | |
	// B* A*
	// | |
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
	aHash.setZ(aHash.getZ() - insideShelfHeight);

	Coordinate3D bHash = coordArray.get(pointBIndex).duplicate();
	bHash.setZ(bHash.getZ() - insideShelfHeight);

	List<Coordinate3D> upperWindowCoord = new LinkedList<Coordinate3D>();
	upperWindowCoord.add(coordArray.get(pointAIndex).duplicate());
	upperWindowCoord.add(coordArray.get(pointBIndex).duplicate());
	upperWindowCoord.add(bHash);
	upperWindowCoord.add(aHash);

	List<Coordinate3D> lowerWindowCoord = new LinkedList<Coordinate3D>();
	lowerWindowCoord.add(aHash.duplicate());
	lowerWindowCoord.add(bHash.duplicate());
	lowerWindowCoord.add(coordArray.get(pointCIndex).duplicate());
	lowerWindowCoord.add(coordArray.get(pointDIndex).duplicate());

	EplusWindow[] windows = new EplusWindow[2];
	windows[0] = new EplusWindow(upperWindowCoord,
		win.getName() + "_ShelfHost", win.getId());
	windows[1] = new EplusWindow(lowerWindowCoord, win.getName(),
		win.getId());

	return windows;
    }

    private Coordinate3D[] buildOutsideShelf(EplusWindow win, double azimuth,
	    Coordinate3D[] insideShelfCoord) {
	// 1. judging the direction of the inside shelf
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
	    pointA = coordArray.get(pointAIndex);
	    pointA.setZ(pointA.getZ() - outsideShelfHeight);
	    pointB = coordArray.get(pointBIndex);
	    pointB.setZ(pointB.getZ() - outsideShelfHeight);
	}
	// 3. calculate differences for the other two coordinates
	double radians = Math.toRadians(azimuth);
	double deltaX = Math.sin(radians) * x_dir * outsideShelfDepth;
	double deltaY = Math.cos(radians) * y_dir * outsideShelfDepth;

	// 4. calculate the rest two points
	Coordinate3D smallPointA = pointA.duplicate();
	Coordinate3D smallPointB = pointB.duplicate();
	smallPointA.setX(smallPointA.getX() + deltaX);
	smallPointA.setY(smallPointA.getY() + deltaY);
	smallPointB.setX(smallPointB.getX() + deltaX);
	smallPointB.setY(smallPointB.getY() + deltaY);

	// 5. return
	Coordinate3D[] result = { pointA, smallPointA, smallPointB, pointB };
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
	pointA = coordArray.get(pointAIndex);
	pointA.setZ(pointA.getZ() - insideShelfHeight);
	pointB = coordArray.get(pointBIndex);
	pointB.setZ(pointB.getZ() - insideShelfHeight);

	// 3. calculate differences for the other two coordinates
	double radians = Math.toRadians(azimuth);
	double deltaX = Math.sin(radians) * x_dir * insideShelfDepth;
	double deltaY = Math.cos(radians) * y_dir * insideShelfDepth;

	// 4. calculate the rest two points
	Coordinate3D smallPointA = pointA.duplicate();
	Coordinate3D smallPointB = pointB.duplicate();
	smallPointA.setX(smallPointA.getX() + deltaX);
	smallPointA.setY(smallPointA.getY() + deltaY);
	smallPointB.setX(smallPointB.getX() + deltaX);
	smallPointB.setY(smallPointB.getY() + deltaY);

	// 5. return
	Coordinate3D[] result = { pointA, pointB, smallPointB, smallPointA };
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
	    if (type.getAttribute().equals("Window")) {
		if (ZoneHTMLParser
			.getFenestrationOrientation(info.get(0).getAttribute())
			.equals(orientation)) {
		    List<Coordinate3D> coords = this.readSurfaceCoords(info);
		    String feneSurfaceName = info.get(0).getAttribute();
		    // no need to record id in this case
		    idfWindow.put(feneSurfaceName,
			    new EplusWindow(coords, feneSurfaceName, name));
		}
	    }
	}
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

}
