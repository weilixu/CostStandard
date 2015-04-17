package masterformat.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractMasterFormatComponent implements MasterFormat{
    
    protected Connection connect = null;
    protected Statement statement = null;
    protected PreparedStatement preparedStatement = null;
    protected ResultSet resultSet = null;
    
    protected final int materialIndex = 0;
    protected final int laborIndex = 1;
    protected final int equipIndex = 2;
    protected final int totalIndex = 3;
    protected final int totalOPIndex = 4;
    
    protected final int numOfCostElement = 5;
    
    protected String unit = "m2";
    protected String hierarchy = "000000 MasterFormat";
    //price data structure to save the data
    protected HashMap<String, Double[]> priceData = new HashMap<String, Double[]>();
    //cost data after selected
    protected Double[] costVector = null;
    //recorde the inputs that required outside of mapping process
    protected ArrayList<String> userInputs;
    protected String description;
    
    protected ArrayList<String> optionLists;
    protected ArrayList<Integer> optionQuantities;

    @Override
    public Double getMaterialPrice() {
	return costVector[materialIndex];
    }

    @Override
    public Double getLaborPrice() {
	return costVector[laborIndex];
    }

    @Override
    public Double getEquipmentPrice() {
	return costVector[equipIndex];
    }

    @Override
    public Double getTotalPrice() {
	return costVector[totalIndex];
    }

    @Override
    public Double getTotalInclOPPrice() {
	return costVector[totalOPIndex];
    }
    
    @Override
    public Double[] getCostVector(){
	return costVector;
    }

    @Override
    public String getUnit(){
	return unit;
    }

    @Override
    public String getHierarchy(){
	return hierarchy;
    }
    
    @Override
    public String getDescription(){
	return description;
    }
    
    @Override
    public boolean isUserInputsRequired(){
	return !userInputs.isEmpty();
    }
    
    @Override
    public ArrayList<String> getUserInputs(){
	return userInputs;
    }
    
    @Override
    abstract public void selectCostVector();
  
    @Override
    abstract public void setUserInputs(HashMap<String, String> userInputsMap);
    
    @Override
    abstract public void setVariable(String[] surfaceProperties);
    
    @Override
    public ArrayList<String> getOptionListFromObjects(){
	return optionLists;
    }
    
    @Override
    public ArrayList<Integer> getQuantitiesFromObjects(){
	return optionQuantities;
    }
    
    // this method allows the last two elements in the double array to be added
    // by the addition
    /*
     * PreCondition: the double array has a length of 5 and the last two element
     * represents the total cost and total cost with profit
     */
    protected Double[] addOperation(Double[] data, Double[] factor) {
	Double[] tempDouble = new Double[numOfCostElement];
	for (int i = 0; i < data.length; i++) {
	    tempDouble[i] = data[i] + factor[i];
	}
	return tempDouble;
    } 
    
    protected Double[] multiOperation(Double[] data, Double[] factor) {
	Double[] tempDouble = new Double[numOfCostElement];

	tempDouble[materialIndex] = data[materialIndex] * factor[materialIndex];
	tempDouble[laborIndex] = data[laborIndex] * factor[laborIndex];
	tempDouble[equipIndex] = data[equipIndex] * factor[equipIndex];
	tempDouble[totalIndex] = tempDouble[materialIndex]
		+ tempDouble[laborIndex] + tempDouble[equipIndex];
	tempDouble[totalOPIndex] = tempDouble[totalIndex]
		* (data[totalOPIndex] / data[totalIndex]);
	return tempDouble;
    }
    
    // You need to close the sql connection after each query operation
    protected void close() {
	try {
	    if (resultSet != null) {
		resultSet.close();
	    }

	    if (statement != null) {
		statement.close();
	    }

	    if (connect != null) {
		connect.close();
	    }
	} catch (Exception e) {

	}
    }

}
