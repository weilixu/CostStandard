package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class Insulation extends AbstractMasterFormatComponent implements BuildingComponent{
    
    
    /*
     * write in energyplus related data
     */
    private final static String material = "Material";

    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";
    
    private final static String Insulation = "Insulation";
    private Double criteria;
    
    private String[] selectedComponents = null;
    
    public Insulation(Double criteria){
	this.criteria = criteria;
	selectedComponents = getListAvailableComponent();
    }

    @Override
    public Double getMaterialPrice() {
	// TODO Auto-generated method stub
	return null;
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
	// TODO Auto-generated method stub
	return null;
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
	// TODO Auto-generated method stub
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
	return "Insulation";
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try{
	    super.testConnect();
	    
	    statement = connect.createStatement();
	    resultSet = statement.executeQuery("select count(*) AS rowcount from energyplusconstruction.materials where RVALUE>" + criteria.toString());
	    resultSet.next();
	    int count = resultSet.getInt("rowcount");
	    
	    availableComponents = new String[count];
	    
	    resultSet = statement.executeQuery("select * from energyplusconstruction.materials where RVALUE > " + criteria.toString());
	    int index = 0;
	    while(resultSet.next()){
		String des = resultSet.getString("MATERIALNAME") + ";" +
			resultSet.getString("Description");
		//System.out.println(des);
		availableComponents[index] = des;
		index++;
	    }
	}catch(SQLException e){
	    e.printStackTrace();
	}finally{
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
    public String[] getSelectedComponentsForRetrofit() {
	String[] retrofitSelect = new String[selectedComponents.length+1];
	retrofitSelect[0] = "NONE;NONE";
	for(int i=0; i<selectedComponents.length; i++){
	    retrofitSelect[i+1] = selectedComponents[i];
	}
	selectedComponents = retrofitSelect;
	return selectedComponents;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	String materialType = component.split(";")[0];
	System.out.println(materialType);
	if(!materialType.equals("NONE")){
	    try{
		//1. setup connections
		super.testConnect();
		
		statement = connect.createStatement();
		
		resultSet = statement.executeQuery("select * from energyplusconstruction.materials where materialname = '" + materialType + "'");
		resultSet.next();
		//2. insert the material
		String materialName = resultSet.getString("MATERIALNAME");
		String[] materialDes = { "Name", "Roughness",
			"Thickness {m}", "Conductivity {W/m-K}",
			"Density {kg/m3}", "Specific Heat {J/kg-K}",
			"Thermal Absorptance", "Solar Absorptance",
			"Visible Absorptance" };
		String[] materialValue = {
			materialName,
			"MediumRough", resultSet.getString("THICKNESS"),
			resultSet.getString("CONDUCTIVITY"),
			resultSet.getString("DENSITY"),
			resultSet.getString("SPECIFICHEAT"), "0.9", "0.7",
			"0.7" };
		reader.addNewEnergyPlusObject(material, materialValue, materialDes);
		
		String materialCostTable = resultSet.getString("COSTTABLE");
		String materialDescription = resultSet.getString("DESCRIPTION");
		
		//a dead code - want to quick finish this work
		HashMap<String, ArrayList<ValueNode>> tempList = reader
			.getObjectListCopy("Construction");
		Set<String> names = tempList.keySet();
		for(String name: names){
		    ArrayList<ValueNode> info = tempList.get(name);
		    if(info.get(0).getAttribute().equals("Project wall")){
			info.get(2).setAttribute(materialName);
		    }
		}
		
		resultSet = statement
			    .executeQuery("select materialcost from "
				    + materialCostTable
				    + " where description = '"
				    + materialDescription + "'");
		resultSet.next();
		Double materialCost = resultSet.getDouble("MATERIALCOST");
		    
		    // prepare data for the component cost
		String[] values = { "PROJECT WALL", "",
			    "Construction", "Project wall", "", "",
			    materialCost.toString(), "", "", "", "", "", "" };
		String[] description = componentCostDescription.split(":");
		
		reader.addNewEnergyPlusObject(componentCostObject, values,
			    description);
	    }catch(SQLException e){
		e.printStackTrace();
	    }finally{
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
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	// TODO Auto-generated method stub
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// TODO Auto-generated method stub
	
    }

}
