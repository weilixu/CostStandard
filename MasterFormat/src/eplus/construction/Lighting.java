package eplus.construction;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.ZoneHTMLParser;
import masterformat.api.AbstractMasterFormatComponent;

public class Lighting extends AbstractMasterFormatComponent implements
	BuildingComponent {

    private final static String lights = "Lights";

    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";

    private String[] selectedComponents = null;

    public Lighting() {
	selectedComponents = getListAvailableComponent();
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.lighting");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.lighting");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("TYPE") + ":"
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
    public void writeInEnergyPlus(IdfReader eplusFile, String component) {	
	String lightType = component.split(":")[1];

	try {
	    // 1. setup connections
	    super.testConnect();

	    statement = connect.createStatement();
	    
	    //take out the useful data
	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.lighting where description = '"
			    + lightType + "'");
	    resultSet.next();
	    String costTable = resultSet.getString("CostTable");
	    String power = resultSet.getString("Power");
	    //re request from the costtable
//	    System.out.println("select * from '" + costTable
//		    + "' where description = '" + lightType + "'");
	    resultSet = statement.executeQuery("select * from " + costTable
		    + " where description = '" + lightType + "'");
	    resultSet.next();

	    // 2. modify lighting related data
	    HashMap<String, ArrayList<ValueNode>> lightMap = eplusFile
		    .getObjectListCopy(lights);
	    Set<String> lightList = lightMap.keySet();
	    Iterator<String> lightIterator = lightList.iterator();
	    while (lightIterator.hasNext()) {
		// get the light object
		String lightName = null;
		String count = lightIterator.next();
		String zoneName = null;
		ArrayList<ValueNode> lightNodes = lightMap.get(count);
		for (ValueNode vn : lightNodes) {
		    if (vn.getDescription().equalsIgnoreCase(
			    "Design Level Calculation Method")) {
			vn.setAttribute("Watts/Area");
		    } else if (vn.getDescription().equalsIgnoreCase(
			    "Watts per Zone Floor Area")) {
			vn.setAttribute(power);
		    } else if (vn.getDescription().equalsIgnoreCase("Name")) {
			lightName = vn.getAttribute();
		    } else if(vn.getDescription().equalsIgnoreCase("Zone or ZoneList Name")){
			zoneName = vn.getAttribute();
		    }
		}
		//W/m2 * m2 / W * $ = $
		Double cost = Double.parseDouble(power) * ZoneHTMLParser.getZoneArea(zoneName)
			/ resultSet.getDouble("power")
			* resultSet.getDouble("materialcost");
		//3. insert cost object to eplus
		String[] values = { lightName + " Cost", "", "Lights",
			lightName, "", cost + "", "","", "", "", "", "", "" };
		String[] description = componentCostDescription.split(":");

		eplusFile.addNewEnergyPlusObject(componentCostObject, values,
			description);
	    }
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

}
