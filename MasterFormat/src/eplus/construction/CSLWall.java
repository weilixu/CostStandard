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

public class CSLWall extends AbstractMasterFormatComponent implements
BuildingComponent {
    
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

    public CSLWall() {
	String[] temp = {"NONE:NONE","Precast and CIP Walls:300 mm HW concrete",
		"Precast and CIP Walls:100 mm LW concrete, board insulation, gyp board",
		"Precast and CIP Walls:EIFS finish, insulation baord, 200 mm HW concrete, gyp board",
		"Precast and CIP Walls:200 mm LW concrete, batt insulation, gyp board",
		"Precast and CIP Walls:300 mm HW concrete, batt insulation, gyp board"};
	selectedComponents = temp;
	// System.out.println(selectedComponents.length);
    }
    
    @Override
    public String getName(){
	return "CSLWall";
    }

    @Override
    public String[] getListAvailableComponent() {
	return null;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {
	selectedComponents = componentList;
    }

    @Override
    public void writeInEnergyPlus(IdfReader eplusFile, String component) {
	// create constructions and put into eplusFile
	String walldescription = component.split(":")[1];
	if(!walldescription.equals("NONE")){
		try {
		    // 1. setup connections
		    super.testConnect();

		    statement = connect.createStatement();

		    resultSet = statement
			    .executeQuery("select * from energyplusconstruction.wallconstruction where description = '"
				    + walldescription + "'");
		    resultSet.next();
		    // 2. setup construction object data, and extract the material
		    // list
		    // for this data.
		    String[] materialList = resultSet.getString("CONSTRUCTIONLIST")
			    .split(",");

		    // System.out.println(Arrays.toString(materialList));

		    // initialize material cost related data
		    String[] materialCostTable = new String[materialList.length];
		    String[] materialDescription = new String[materialList.length];

		    String constructionName = resultSet.getString("WALLTYPE");
		    String[] constructionDes = new String[materialList.length + 1];
		    String[] constructionValue = new String[materialList.length + 1];
		    constructionDes[0] = "Name";
		    constructionValue[0] = constructionName;

		    // 3. loop through material list and create
		    // material/material:nomass
		    // objects in energyplus
		    for (int i = 0; i < materialList.length; i++) {
			resultSet = statement
				.executeQuery("select * from energyplusconstruction.materials where layerid = '"
					+ materialList[i] + "'");
			resultSet.next();
			if (duplicateMaterial(constructionValue,
				resultSet.getString("MATERIALNAME"))) {
			    constructionDes[i + 1] = "Layer";
			    constructionValue[i + 1] = resultSet
				    .getString("MATERIALNAME") + "_Wall";
			} else if (resultSet.getDouble("RESISTANCE") <= 0.0) {
			    // if detail thermal properties are available
			    String[] materialDes = { "Name", "Roughness",
				    "Thickness {m}", "Conductivity {W/m-K}",
				    "Density {kg/m3}", "Specific Heat {J/kg-K}",
				    "Thermal Absorptance", "Solar Absorptance",
				    "Visible Absorptance" };
			    String[] materialValue = {
				    resultSet.getString("MATERIALNAME") + "_Wall",
				    "MediumRough", resultSet.getString("THICKNESS"),
				    resultSet.getString("CONDUCTIVITY"),
				    resultSet.getString("DENSITY"),
				    resultSet.getString("SPECIFICHEAT"), "0.9", "0.7",
				    "0.7" };
			    eplusFile.addNewEnergyPlusObject(material, materialValue,
				    materialDes);// add object to
						 // eplus

			    // complete the construction object data
			    constructionDes[i + 1] = "Layer";
			    constructionValue[i + 1] = resultSet
				    .getString("MATERIALNAME") + "_Wall";

			} else {
			    // if there is only thermal resistance data available
			    String[] materialDes = { "Name", "Roughness",
				    "Thermal Resistance {m2-K/W}",
				    "Thermal Absorptance", "Solar Absorptance",
				    "Visible Absorptance" };
			    String[] materialValue = {
				    resultSet.getString("MATERIALNAME") + "_Wall",
				    "MediumRough", resultSet.getString("RESISTANCE"),
				    "0.9", "0.7", "0.7" };
			    // System.out.println(Arrays.toString(materialValue));
			    eplusFile.addNewEnergyPlusObject(materialnomass,
				    materialValue, materialDes);// add object to
								// eplus

			    // complete the construction object data
			    constructionDes[i + 1] = "Layer";
			    constructionValue[i + 1] = resultSet
				    .getString("MATERIALNAME") + "_Wall";
			}

			materialCostTable[i] = resultSet.getString("COSTTABLE");
			materialDescription[i] = resultSet.getString("DESCRIPTION");

		    }
		    // System.out.println(Arrays.toString(constructionValue) + " " +
		    // Arrays.toString(constructionDes));
		    eplusFile.addNewEnergyPlusObject(construction, constructionValue,
			    constructionDes);// add construction
					     // data

		    // 4. replace all the exterior wall surfaces with the newly
		    // created
		    // external wall construction
		    HashMap<String, ArrayList<ValueNode>> surfaceMap = eplusFile
			    .getObjectListCopy(surface);

		    Set<String> surfaceList = surfaceMap.keySet();
		    Iterator<String> surfaceIterator = surfaceList.iterator();
		    while (surfaceIterator.hasNext()) {
			// get one surface
			String surfaceCount = surfaceIterator.next();
			ArrayList<ValueNode> nodes = surfaceMap.get(surfaceCount);
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
			if (surfaceType.equals("Wall") && boundary.equals("Outdoors")) {
			    // if surface is external wall, replace the construction
			    // System.out.print(nodes.get(0).getAttribute());
			    for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).getDescription()
					.equals("Construction Name")) {
				    nodes.get(i).setAttribute(constructionName);
				    // System.out.println(nodes.get(i).getDescription()
				    // + " " + nodes.get(i).getAttribute());
				    break;
				}
			    }
			}
		    }

		    // 5. write cost data into energyplus
		    Double materialCost = 0.0;

		    if(walldescription.equals("300 mm HW concrete")){
			materialCost = 189.65;
		    }else if(walldescription.equals("100 mm LW concrete, board insulation, gyp board")){
			materialCost = 222.65;
		    }else if(walldescription.equals("EIFS finish, insulation baord, 200 mm HW concrete, gyp board")){
			materialCost = 242.89;
		    }else if(walldescription.equals("200 mm LW concrete, batt insulation, gyp board")){
			materialCost = 265.34;
		    }else if(walldescription.equals("300 mm HW concrete, batt insulation, gyp board")){
			materialCost = 302.44;
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
	}else{
	    // prepare data for the component cost
	    String[] values = { "CSL_WALLS_SEW_PLENUM_BELOW_GRADE", "",
		    "Construction", "CSL_WALLS_SEW_PLENUM_BELOW_GRADE", "", "",
		    "368.73", "", "", "", "", "", "" };
	    String[] description = componentCostDescription.split(":");
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values,
		    description);
	    
	    // prepare data for the component cost
	    String[] values2 = { "CSL_WALLS_NORTH_PLENUM", "",
		    "Construction", "CSL_WALLS_NORTH_PLENUM", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values2,
		    description);
	    
	    // prepare data for the component cost
	    String[] values3 = { "CSL_WALLS_NORTH_CIP_A601_BELOW_GRADE", "",
		    "Construction", "CSL_WALLS_NORTH_CIP_A601_BELOW_GRADE", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values3,
		    description);
	    
	    String[] values4 = { "CSL_WALLS_NORTH_CIP_A301_BELOW_GRADE", "",
		    "Construction", "CSL_WALLS_NORTH_CIP_A301_BELOW_GRADE", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values4,
		    description);
	    
	    String[] values5 = { "CSL_WALLS_GFRC_GWB", "",
		    "Construction", "CSL_WALLS_GFRC_GWB", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values5,
		    description);
	    
	    String[] values6 = { "PROJECT WALL", "",
		    "Construction", "PROJECT WALL", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values6,
		    description);
	    
	    String[] values7 = { "CSL_WALLS_WOODCLADDING_GWB", "",
		    "Construction", "CSL_WALLS_WOODCLADDING_GWB", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values7,
		    description);
	    
	    String[] values8 = { "CSL_WALLS_WOODCLADDING_PLYWOOD", "",
		    "Construction", "CSL_WALLS_WOODCLADDING_PLYWOOD", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values8,
		    description);
	    
	    String[] values9 = { "CSL_WALLS_W_BELOW_CURTAIN_WALL", "",
		    "Construction", "CSL_WALLS_W_BELOW_CURTAIN_WALL", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values9,
		    description);
	    
	    String[] values10 = { "CSL_WALLS_NORTH_CIP_A301", "",
		    "Construction", "CSL_WALLS_NORTH_CIP_A301", "", "",
		    "368.73", "", "", "", "", "", "" };
	    // add to eplus
	    eplusFile.addNewEnergyPlusObject(componentCostObject, values10,
		    description);
	}
    }

    private boolean duplicateMaterial(String[] constructionLayers,
	    String material) {
	for (int i = 0; i < constructionLayers.length; i++) {
	    String layer = constructionLayers[i];
	    String materialName = material + "_Wall";
	    // System.out.println(layer +" " + material);
	    if (layer != null && layer.equalsIgnoreCase(materialName)) {
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

    @Override
    public double getComponentCost(Document doc) {
	return 0.0;
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
	String[] retrofitSelect = new String[selectedComponents.length];
	retrofitSelect[0] = "NONE:NONE";
	for(int i=0; i<selectedComponents.length; i++){
	    retrofitSelect[i+1] = selectedComponents[i];
	}
	
	return retrofitSelect;
    }

}
