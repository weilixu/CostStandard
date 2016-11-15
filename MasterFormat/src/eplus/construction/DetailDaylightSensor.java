package eplus.construction;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;

import eplus.IdfReader;

public class DetailDaylightSensor implements BuildingComponent{
    
    private static final int SurfaceNumVerticeLoc = 9;
    private static final int SurfaceCoordsOffset = 10;
    private static final int ShadingSurfaceCoordsOffset = 4;
    
    private String orientation;
    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
			   // inputs
    private final String componentCostObject = "ComponentCost:LineItem";
    
    public DetailDaylightSensor(){
	//initialization
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
	return null;
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
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public double getComponentCost(Document doc) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean isIntegerTypeComponent() {
	return false;
    }

    @Override
    public int getNumberOfVariables() {
	return 2;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String orientation) {
	orientation = orientation;
	
    }

}
