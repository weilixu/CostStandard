package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.geometry.Coordinate3D;
import eplus.geometry.EplusWindow;
import eplus.geometry.Wall;
import eplus.htmlparser.ZoneHTMLParser;
import masterformat.api.AbstractMasterFormatComponent;

public class WindowWallRatio extends AbstractMasterFormatComponent
	implements BuildingComponent {

    public static final int INVALID_POLYGON = -1;
    public static final int ZERO_AREA_WALL = -2;
    private static final int SurfaceNumVerticeLoc = 9;
    private static final int SurfaceCoordsOffset = 10;
    // private static final double maximumRatio = 90; //assume the model has 90%
    // WWR

    // private final static String fenestration =
    // "FenestrationSurface:Detailed";
    // private final static String field = "Multiplier";
    private String orientation;

    private LinkedList<Wall> walls;
    private HashMap<String, ArrayList<ValueNode>> feneSurfaces;

    private String[] selectedComponents; // daylight sensor has only on/off

    public WindowWallRatio(String orientation) {
	this.orientation = orientation;
	selectedComponents = getListAvailableComponent();
    }

    @Override
    public String getName() {
	return "wwr" + orientation;
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement.executeQuery(
		    "select count(*) AS rowcount from energyplusconstruction.wwr");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.wwr");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("RATIO");
		availableComponents[index] = des;
		index++;
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}

	return availableComponents;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {
	selectedComponents = componentList;
    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	
	initWWR(reader);
	double currentRatio = this.getWindowWallRatio();
	//System.out.println(currentRatio);
	Double ratio = Double.parseDouble(component);
	//System.out.println(ratio);
	if (ratio >= 10 && ratio <= 90) {
	    ratio = ratio / 100;
	    //System.out.println(ratio);
	    double scaleRatio = ratio / currentRatio;
	    //System.out.println("The scale ratio is: " + scaleRatio);
	    //System.out.println(scaleRatio);
	    for (Wall wall : walls) {
		wall.scaleWindows(scaleRatio);
		this.saveWindowCoordsToReader(wall);
	    }
	}

	// HashMap<String, ArrayList<ValueNode>> fene = reader
	// .getObjectListCopy(fenestration);
	// Set<String> feneSet = fene.keySet();
	// Iterator<String> feneIterator = feneSet.iterator();
	// while (feneIterator.hasNext()) {
	// String element = feneIterator.next();
	// ArrayList<ValueNode> nodes = fene.get(element);
	// String name = nodes.get(0).getAttribute();
	// if (ZoneHTMLParser.getFenestrationOrientation(name)
	// .equals(orientation)) {
	// for (ValueNode vn : nodes) {
	// if (vn.getDescription().equalsIgnoreCase(field)) {
	// vn.setAttribute(ratio.toString());
	// }
	// }
	// }
	// }
    }

    @Override
    public double getComponentCost(Document doc) {
	return 0;
    }

    @Override
    public void selectCostVector() {
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {

    }

    @Override
    public void setVariable(String[] surfaceProperties) {

    }

    private void initWWR(IdfReader reader) {
	this.walls = new LinkedList<Wall>();
	int buildSurfaceNameLoc = 3;

	HashMap<String, Wall> idfWalls = new HashMap<String, Wall>();

	HashMap<String, ArrayList<ValueNode>> buildSurfaces = reader
		.getObjectListCopy("BuildingSurface:Detailed");

	Set<String> names = buildSurfaces.keySet();
	//System.out.println("Test........................");

	for (String name : names) {
	    ArrayList<ValueNode> info = buildSurfaces.get(name);
	    ValueNode type = info.get(1);
	    if (type.getAttribute().equals("Wall")) {
		List<Coordinate3D> coords = this.readSurfaceCoords(info);
		idfWalls.put(info.get(0).getAttribute(), new Wall(coords));
	    }
	}

	this.feneSurfaces = reader
		.getObjectListCopy("FenestrationSurface:Detailed");
	names = feneSurfaces.keySet();
	for (String name : names) {
	    ArrayList<ValueNode> info = feneSurfaces.get(name);
	    ValueNode type = info.get(1);
	    if (type.getAttribute().equals("Window")) {
		//System.out.println(info.get(0).getAttribute());
		if (ZoneHTMLParser.getFenestrationOrientation(info.get(0).getAttribute())
			.equals(orientation)) {
		    //System.out.println("I am here!");
		    List<Coordinate3D> coords = this.readSurfaceCoords(info);
		    String buildSurfaceName = info.get(buildSurfaceNameLoc)
			    .getAttribute();
		    if (idfWalls.containsKey(buildSurfaceName)) {
			idfWalls.get(buildSurfaceName).addWindow(coords,
				info.get(0).getAttribute(), name);
		    } else {
			System.err.println("Window has no wall: " + name
				+ ", missing wall name:" + buildSurfaceName);
		    }
		}
	    }
	}

	// remove walls has no window
	names = idfWalls.keySet();
	//System.out.println(names);
	for (String name : names) {
	    Wall wall = idfWalls.get(name);
	    if (wall.hasWindow()) {
		this.walls.add(wall);
	    }
	}
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

    private double getWindowWallRatio() {
	double wallArea = 0, winArea = 0;
	for (Wall wall : walls) {
	    wallArea += wall.getWallArea();
	    winArea += wall.getWindowArea();
	}

	return winArea / wallArea;
    }

    private void saveWindowCoordsToReader(Wall wall) {
	List<EplusWindow> wins = wall.getWindows();
	for (EplusWindow win : wins) {
	    String id = win.getId();
	    
	    ArrayList<ValueNode> winInfo = this.feneSurfaces.get(id);
	    List<Coordinate3D> points = win.getCoords();
	    for (int i = 0; i < points.size(); i++) {
		Coordinate3D point = points.get(i);
		//System.out.print("Before: " + winInfo.get(i * 3 + SurfaceCoordsOffset).getAttribute());
		//System.out.println(point.getX());
		winInfo.get(i * 3 + SurfaceCoordsOffset)
			.setAttribute(String.valueOf(point.getX()));
		//System.out.println("After: " + winInfo.get(i * 3 + SurfaceCoordsOffset).getAttribute());
		//System.out.print("Before: " + winInfo.get(i * 3 + SurfaceCoordsOffset+1).getAttribute());
		//System.out.println(point.getY());
		winInfo.get(i * 3 + SurfaceCoordsOffset + 1).setAttribute(String.valueOf(point.getY()));
		//System.out.println("After: " + winInfo.get(i * 3 + SurfaceCoordsOffset+1).getAttribute());
		//System.out.print("Before: " + winInfo.get(i * 3 + SurfaceCoordsOffset+2).getAttribute());
		//System.out.println(point.getZ());		
		winInfo.get(i * 3 + SurfaceCoordsOffset + 2)
			.setAttribute(String.valueOf(point.getZ()));
		//System.out.println("After: " + winInfo.get(i * 3 + SurfaceCoordsOffset+2).getAttribute());
	    }
	}
    }

    @Override
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public String[] getSelectedComponentsForRetrofit() {
	// TODO Auto-generated method stub
	return null;
    }
}
