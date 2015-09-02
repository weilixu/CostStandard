package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class ExteriorWall extends AbstractMasterFormatComponent implements
	BuildingComponent {
    /*
     * write in energyplus related data
     */
    private final static String construction = "Construction";
    private final static String material = "Material";
    private final static String materialnomass = "Material:NoMass";
    private final static String zone = "Zone";
    private final static String surface = "BuildingSurface:Detailed";

    private String[] selectedComponents = null;

    public ExteriorWall() {

    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.wallconstruction");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.wallconstruction");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("WALLTYPE") + ": "
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
    public void writeInEnergyPlus(IdfReader eplusFile, String component) {
	// create constructions and put into eplusFile
	String walldescription = component.split(":")[1];
	try {
	    // 1. setup connections
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.wallconstruction where description = '"
			    + walldescription + "'");
	    ;
	    resultSet.next();
	    // 2. setup construction object data, and extract the material list
	    // for this data.
	    String[] materialList = resultSet.getString("CONSTRUCTIONLIST")
		    .split(",");

	    String constructionName = resultSet.getString("WALLTYPE");
	    String[] constructionDes = new String[materialList.length + 1];
	    String[] constructionValue = new String[materialList.length + 1];
	    constructionDes[0] = "Name";
	    constructionValue[0] = constructionName;

	    // 3. loop through material list and create material/material:nomass
	    // objects in energyplus
	    for (int i = 0; i < materialList.length; i++) {
		resultSet = statement
			.executeQuery("select * from energyplusconstruction.materials where layerid = '"
				+ materialList[i] + "'");
		resultSet.next();
		if (resultSet.getDouble("RESISTANCE") > 0) {
		    // if detail thermal properties are available
		    String[] materialDes = { "Name", "Roughness",
			    "Thickness {m}", "Conductivity {W/m-K}",
			    "Density {kg/m3}", "Specific Heat {J/kg-K}",
			    "Thermal Absorptance", "Solar Absorptance",
			    "Visible Absorptance" };
		    String[] materialValue = {
			    resultSet.getString("MATERIALNAME"), "MediumRough",
			    resultSet.getString("THICKNESS"),
			    resultSet.getString("CONDUCTIVITY"),
			    resultSet.getString("DENSITY"),
			    resultSet.getString("SPECIFICHEAT"), "0.9", "0.7",
			    "0.7" };
		    eplusFile.addNewEnergyPlusObject(material, materialValue,
			    materialDes);// add object to eplus

		    // complete the construction object data
		    constructionDes[i + 1] = "Layer";
		    constructionValue[i + 1] = resultSet
			    .getString("MATERIALNAME");

		} else {
		    // if there is only thermal resistance data available
		    String[] materialDes = { "Name", "Roughness",
			    "Thermal Resistance {m2-K/W}",
			    "Thermal Absorptance", "Solar Absorptance",
			    "Visible Absorptance" };
		    String[] materialValue = {
			    resultSet.getString("MATERIALNAME"), "MediumRough",
			    resultSet.getString("RESISTANCE"), "0.9", "0.7",
			    "0.7" };
		    eplusFile.addNewEnergyPlusObject(materialnomass,
			    materialValue, materialDes);// add object to eplus

		    // complete the construction object data
		    constructionDes[i + 1] = "Layer";
		    constructionValue[i + 1] = resultSet
			    .getString("MATERIALNAME");
		}

		eplusFile.addNewEnergyPlusObject(construction,
			constructionValue, constructionDes);// add construction
							    // data
	    }

	    // 4. replace all the exterior wall surfaces with the newly created
	    // external wall construction
	    HashMap<String, HashMap<String, ArrayList<ValueNode>>> surfaceMap = eplusFile
		    .getObjectList(surface);

	    Set<String> surfaceList = surfaceMap.get(surface).keySet();
	    Iterator<String> surfaceIterator = surfaceList.iterator();
	    while (surfaceIterator.hasNext()) {
		//get one surface
		String surfaceCount = surfaceIterator.next();
		ArrayList<ValueNode> nodes = surfaceMap.get(surface).get(
			surfaceCount);
		String surfaceType = null;
		String boundary = null;
		for (int i = 0; i < nodes.size(); i++) {
		    //check surface condition, type and outside boundary
		    if (nodes.get(i).getDescription().equals("Surface Type")) {
			surfaceType = nodes.get(i).getAttribute();
		    }
		    if (nodes.get(i).getDescription()
			    .equals("Outside Boundary Condition")) {
			boundary = nodes.get(i).getAttribute();
		    }
		}
		if (surfaceType.equals("Wall") && boundary.equals("Outdoors")) {
		    //if surface is external wall, replace the construction
		    for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getDescription()
				.equals("Construction Name")) {
			    nodes.get(i).setAttribute(constructionName);
			    break;
			}
		    }
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}

    }

    @Override
    public String[] getSelectedComponent() {
	return selectedComponents;
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
