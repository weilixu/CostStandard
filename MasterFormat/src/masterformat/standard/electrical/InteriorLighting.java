package masterformat.standard.electrical;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class InteriorLighting extends AbstractElectrical{
    private Double power;
    //mount include ceiling mount and surface mount
    private String mount;
    private String dimension;
    private String type;
    
    public InteriorLighting() {
	unit = "$/Ea";
	hierarchy = "260000 Electrical:265100 Interior Lighting:265113 Interior Lighting Fixtures, Lamps, and Ballasts:265113.500100 Fluorescent";
    }
    
    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Mount")){
		//only "Surface" and "Recessed"
		mount = userInputsMap.get(temp);
		addDimension();
	    }else if(temp.equals("TYPE")){
		type = userInputsMap.get(temp);
	    }else if(temp.equals("DIMENSION")){
		dimension = userInputsMap.get(temp);
	    }
	}
    }
    

    @Override
    public void setVariable(String[] properties) {
	try {
	    power = Double.parseDouble(properties[electricPowerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
	
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from lighting.interiorlighting");

	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("description"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
    
    @Override
    public double randomDrawTotalCost() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from lighting.interiorlighting where description = '"
			    + descriptionList.get(index)+ "'");
	    resultSet.next();
	    double unitPower = resultSet.getDouble("power");
	    return resultSet.getDouble("totalcost")
		    *Math.ceil(power/unitPower);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	// hopefully we won't reach here
	return 0.0;
    }
    

    @Override
    protected void initializeData() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from lighting.interiorlighting");
	    
	    // initialize the default pump type
	    resultSet.next();
	    type = resultSet.getString("type");
	    userInputs.add("OPTION:TYPE:" + type);
	    while(resultSet.next()){
		userInputs.add("OPTION:TYPE:"+resultSet.getString("type"));
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
    
    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	int numberOfFix = 1;
	try{
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
		statement = connect.createStatement();
		//select all the available lighting fixtures
		resultSet = statement.executeQuery("select * from lighting.interiorlighting where type = '" + type +"' and mount = '" +mount + "' and dimension = '" + dimension+"'" );
		//compare one that  provide cheapest cost among all
		//first initialize the data
		resultSet.next();
		numberOfFix = getNumberOfFixtures(resultSet.getDouble("power"));
		description = resultSet.getString("description");
		cost[materialIndex] = resultSet.getDouble("materialcost")
			* numberOfFix;
		cost[laborIndex] = resultSet.getDouble("laborcost")
			* numberOfFix;
		cost[equipIndex] = resultSet.getDouble("equipmentcost")
			* numberOfFix;
		cost[totalIndex] = resultSet.getDouble("totalCost")
			* numberOfFix;
		cost[totalOPIndex] = resultSet.getDouble("totalInclop")
			* numberOfFix;
		
		while(resultSet.next()){
		    Integer tempNumber = getNumberOfFixtures(resultSet.getDouble("power"));
		    if(resultSet.getDouble("totalCost")*tempNumber < cost[totalIndex]){
			description = resultSet.getString("description");
			cost[materialIndex] = resultSet.getDouble("materialcost")
				* tempNumber;
			cost[laborIndex] = resultSet.getDouble("laborcost")
				* tempNumber;
			cost[equipIndex] = resultSet.getDouble("equipmentcost")
				* tempNumber;
			cost[totalIndex] = resultSet.getDouble("totalCost")
				* tempNumber;
			cost[totalOPIndex] = resultSet.getDouble("totalInclop")
				* tempNumber;
			numberOfFix = tempNumber;
		    }
		}
		
		costVector = cost;
		optionLists.add(description);
		optionQuantities.add(numberOfFix);
	}catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	
    }
    
    private void addDimension(){
	userInputs.clear();
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    
	    resultSet = statement
		    .executeQuery("select * from lighting.interiorlighting");
	    while(resultSet.next()){
		userInputs.add("OPTION:TYPE:"+resultSet.getString("type"));
	    }
	    
	    resultSet = statement
		    .executeQuery("select * from lighting.interiorlighting where type = '" + type +"'  and mount = '" + mount + "'");
	    // initialize the default pump type
	    resultSet.next();
	    dimension = resultSet.getString("dimension");
	    userInputs.add("OPTION:DIMENSION:" + dimension);
	    while(resultSet.next()){
		userInputs.add("OPTION:DIMENSION:"+resultSet.getString("dimension"));
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
    
    private Integer getNumberOfFixtures(Double unitPower){
	int num = 1;
	double tempPower = power;
	while(tempPower>0){
	    num+=1;
	    tempPower=tempPower-unitPower;
	}
	return num;
    }

}
