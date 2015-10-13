package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class Roof extends AbstractMasterFormatComponent implements
BuildingComponent{
    /*
     * write in energyplus related data
     */
    private final static String construction = "Construction";
    private final static String material = "Material";
    private final static String materialnomass = "Material:NoMass";
    private final static String zone = "Zone";
    private final static String surface = "BuildingSurface:Detailed";

    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";

    private String[] selectedComponents = null;

    public Roof() {
	selectedComponents = getListAvailableComponent();
	//System.out.println(selectedComponents.length);
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.roofconstruction");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.roofconstruction");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("ROOFTYPE") + ":"
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
//	synchronized(this){
	try {
	    // 1. setup connections
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.roofconstruction where description = '"
			    + walldescription + "'");
	    resultSet.next();
	    // 2. setup construction object data, and extract the material list
	    // for this data.
	    String[] materialList = resultSet.getString("CONSTRUCTIONLIST")
		    .split(",");
	    
	    //System.out.println(Arrays.toString(materialList));

	    // initialize material cost related data
	    String[] materialCostTable = new String[materialList.length];
	    String[] materialDescription = new String[materialList.length];

	    String constructionName = resultSet.getString("ROOFTYPE");
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
		if(duplicateMaterial(constructionValue, resultSet
			    .getString("MATERIALNAME"))){
		    constructionDes[i + 1] = "Layer";
		    constructionValue[i + 1] = resultSet
			    .getString("MATERIALNAME") + "_Roof";
		}else if (resultSet.getDouble("RESISTANCE") <= 0.0) {
		    // if detail thermal properties are available
		    String[] materialDes = { "Name", "Roughness",
			    "Thickness {m}", "Conductivity {W/m-K}",
			    "Density {kg/m3}", "Specific Heat {J/kg-K}",
			    "Thermal Absorptance", "Solar Absorptance",
			    "Visible Absorptance" };
		    String[] materialValue = {
			    resultSet.getString("MATERIALNAME")+"_Roof", "MediumRough",
			    resultSet.getString("THICKNESS"),
			    resultSet.getString("CONDUCTIVITY"),
			    resultSet.getString("DENSITY"),
			    resultSet.getString("SPECIFICHEAT"), "0.9", "0.7",
			    "0.7" };
		    eplusFile.addNewEnergyPlusObject(material, materialValue,
			    materialDes);// add object to eplus

		    // complete the construction object data
		    constructionDes[i + 1] = "Layer";
		    constructionValue[i + 1] = resultSet.getString("MATERIALNAME")+"_Roof";

		} else {
		    // if there is only thermal resistance data available
		    String[] materialDes = { "Name", "Roughness",
			    "Thermal Resistance {m2-K/W}",
			    "Thermal Absorptance", "Solar Absorptance",
			    "Visible Absorptance" };
		    String[] materialValue = {
			    resultSet.getString("MATERIALNAME")+"_Roof", "MediumRough",
			    resultSet.getString("RESISTANCE"), "0.9", "0.7",
			    "0.7" };
		    //System.out.println(Arrays.toString(materialValue));
		    eplusFile.addNewEnergyPlusObject(materialnomass,
			    materialValue, materialDes);// add object to eplus

		    // complete the construction object data
		    constructionDes[i + 1] = "Layer";
		    constructionValue[i + 1] = resultSet.getString("MATERIALNAME")+"_Roof";
		}

		materialCostTable[i] = resultSet.getString("COSTTABLE");
		materialDescription[i] = resultSet.getString("DESCRIPTION");

	    }
		//System.out.println(Arrays.toString(constructionValue) + " " + Arrays.toString(constructionDes));
		eplusFile.addNewEnergyPlusObject(construction,
			constructionValue, constructionDes);// add construction
							    // data

	    // 4. replace all the exterior wall surfaces with the newly created
	    // external wall construction
	    HashMap<String, ArrayList<ValueNode>> surfaceMap = eplusFile
		    .getObjectListCopy(surface);

	    Set<String> surfaceList = surfaceMap.keySet();
	    Iterator<String> surfaceIterator = surfaceList.iterator();
	    while (surfaceIterator.hasNext()) {
		// get one surface
		String surfaceCount = surfaceIterator.next();
		ArrayList<ValueNode> nodes = surfaceMap.get(
			surfaceCount);
		String surfaceType = null;
		String boundary = null;
		for (int i = 0; i < nodes.size(); i++) {
		    // check surface condition, type and outside boundary
		    if (nodes.get(i).getDescription().equals("Surface Type")) {
			surfaceType = nodes.get(i).getAttribute();
		    }
		    if (nodes.get(i).getDescription()
			    .equals("Outside Boundary Condition")) {
			boundary = nodes.get(i).getAttribute();
		    }
		}
		if (surfaceType.equals("Roof") && boundary.equals("Outdoors")) {
		    // if surface is external wall, replace the construction
		    //System.out.print(nodes.get(0).getAttribute());
		    for (int i = 0; i < nodes.size(); i++) {			
			if (nodes.get(i).getDescription()
				.equals("Construction Name")) {
			    nodes.get(i).setAttribute(constructionName);
			    //System.out.println(nodes.get(i).getDescription() + " " + nodes.get(i).getAttribute());
			    break;
			}
		    }
		}
	    }

	    // 5. write cost data into energyplus
	    Double materialCost = 0.0;
	    for (int c = 0; c < materialCostTable.length; c++) {// retrieve the
								// cost data
								// from database
		if (materialCostTable[c] != null) {
		    //System.out.println(materialCostTable[c]);
		    resultSet = statement
			    .executeQuery("select materialcost from "
				    + materialCostTable[c]
				    + " where description = '"
				    + materialDescription[c] + "'");
		    resultSet.next();
		    materialCost = materialCost
			    + resultSet.getDouble("MATERIALCOST");
		}
	    }
	    // prepare data for the component cost
	    String[] values = { constructionName.toUpperCase(), "",
		    "Construction", constructionName, "", "",
		    materialCost.toString(), "", "", "", "", "", "" };
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
//	}
    }
    
    private boolean duplicateMaterial(String[] constructionLayers, String material){
	for(int i=0; i<constructionLayers.length; i++){
	    String layer = constructionLayers[i];
	    String materialName = material + "_Roof";
	    //System.out.println(layer +" " + material);
	    if(layer!=null && layer.equalsIgnoreCase(materialName)){
		return true;
	    }
	}
	return false;
    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// No Need to implement this method because we are overwriting
	// the construction

    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub

    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }
}
