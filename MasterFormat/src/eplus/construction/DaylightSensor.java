package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class DaylightSensor extends AbstractMasterFormatComponent implements
	BuildingComponent {

    private final static String daylight = "Daylighting:Controls";

    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";

    private String[] selectedComponents; // daylight sensor has only on/off

    public DaylightSensor() {
	selectedComponents = getListAvailableComponent();
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.sensor");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.sensor");
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
    public void writeInEnergyPlus(IdfReader reader, String component) {
	String sensor = component.split(":")[1];
	if (sensor.contains("on")) {
	    try {
		// 1. setup connections
		super.testConnect();

		statement = connect.createStatement();

		// take out the useful data
		resultSet = statement
			.executeQuery("select * from energyplusconstruction.sensor where description = '"
				+ sensor + "'");
		resultSet.next();
		String cost = resultSet.getString("Cost");

		// modify the idf daylight sensors
		HashMap<String, ArrayList<ValueNode>> daylightSensors = reader
			.getObjectListCopy(daylight);
		Set<String> daylightSet = daylightSensors.keySet();
		Iterator<String> daylightIterator = daylightSet.iterator();
		String zoneName = null;
		while (daylightIterator.hasNext()) {
		    String element = daylightIterator.next();
		    ArrayList<ValueNode> nodes = daylightSensors.get(element);
		    for (ValueNode vn : nodes) {
			if (vn.getDescription()
				.equalsIgnoreCase(
					"Fraction of Zone Controlled by First Reference Point")) {
			    vn.setAttribute("1");
			} else if (vn.getDescription().equalsIgnoreCase(
				"Zone Name")) {
			    zoneName = vn.getAttribute();
			}
		    }
		    // write in component costs for daylight sensors
		    // 3. insert cost object to eplus
		    String[] values = { zoneName + " Daylit Cost", "",
			    "Daylighting:Controls", zoneName, "", cost, "", "",
			    "", "", "", "", "" };
		    String[] description = componentCostDescription.split(":");
		    reader.addNewEnergyPlusObject(componentCostObject, values, description);
		}

	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close();
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	// TODO Auto-generated method stub
	return 0;
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
