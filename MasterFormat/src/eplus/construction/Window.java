package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.HVAC.HVACSystem;
import masterformat.api.AbstractMasterFormatComponent;

public class Window extends AbstractMasterFormatComponent implements
	BuildingComponent {
    /*
     * write in energyplus related data
     */
    private final static String construction = "Construction";
    private final static String simplewindow = "WindowMaterial:SimpleGlazingSystem";
    private final static String surface = "FenestrationSurface:Detailed";

    private final String simpleWindowDescription = "Name:U-Factor:Solar Heat Gain Coefficient:Visible Transmittance";
    private final String constructionDes = "Name:Outside Layer";

    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";

    private String[] selectedComponents = null;

    public Window() {
	selectedComponents = getListAvailableComponent();
	// System.out.println(selectedComponents.length);
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.window");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.window");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("WindowType") + ":"
			+ resultSet.getString("DESCRIPTION");
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
    public void writeInEnergyPlus(IdfReader eplusFile,
	    String component) {
	// create constructions and put into eplusFile
	String windowDescription = component.split(":")[1];
	try {
	    // 1. setup connections
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.window where description = '"
			    + windowDescription + "'");
	    resultSet.next();
	    // 2. Extract the information and insert object to idf file
	    String name = resultSet.getString("WindowType");
	    String uvalue = resultSet.getString("UValue");
	    String shgc = resultSet.getString("SHGC");
	    String vt = resultSet.getString("VT");
	    String[] windowproperty = new String[4];
	    String[] constructionList = new String[2];

	    windowproperty[0] = name;
	    windowproperty[1] = uvalue;
	    windowproperty[2] = shgc;
	    windowproperty[3] = vt;

	    String constructionName = name + " System";
	    constructionList[0] = constructionName;
	    constructionList[1] = name;

	    eplusFile.addNewEnergyPlusObject(simplewindow, windowproperty,
		    simpleWindowDescription.split(":"));
	    eplusFile.addNewEnergyPlusObject(construction, constructionList,
		    constructionDes.split(":"));

	    // 3. replace the fenestration material to created material
	    HashMap<String, ArrayList<ValueNode>> surfaceMap = eplusFile
		    .getObjectListCopy(surface);
	    Set<String> surfaceList = surfaceMap.keySet();
	    Iterator<String> surfaceIterator = surfaceList.iterator();
	    while (surfaceIterator.hasNext()) {
		// get one surface
		String surfaceCount = surfaceIterator.next();
		ArrayList<ValueNode> nodes = surfaceMap.get(surfaceCount);
		String surfaceType = null;
		for (int i = 0; i < nodes.size(); i++) {
		    if (nodes.get(i).getDescription().equals("Surface Type")) {
			surfaceType = nodes.get(i).getAttribute();
		    }
		}
		if (surfaceType.equals("Window")) {
		    // find window
		    for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getDescription()
				.equals("Construction Name")) {
			    nodes.get(i).setAttribute(constructionName);
			    break;
			}
		    }
		}
	    }

	    // 5. write cost data into energyplus
	    String cost = resultSet.getString("COST");

	    // prepare data for the component cost
	    String[] values = { constructionName.toUpperCase(), "",
		    "Construction", constructionName, "", "", cost, "", "", "",
		    "", "", "" };
	    String[] description = componentCostDescription.split(":");
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values,
		    description);
	    // DONE!!!

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub

    }

    @Override
    public double getComponentCost(Document doc) {
	return 0.0;
    }
}
