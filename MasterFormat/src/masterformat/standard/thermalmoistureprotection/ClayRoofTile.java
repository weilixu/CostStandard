package masterformat.standard.thermalmoistureprotection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ClayRoofTile extends AbstractThermalMoistureProtection{
    private String type;
    private String color;
    
    public ClayRoofTile(){
	unit = "$/m2";
	hierarchy = "073200 Roof Tiles:073213 Clay Roof Tiles:073213.10 Clay Tiles";
    }

    @Override
    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement.executeQuery("select * from insulation.claytiles");
	    resultSet.next();
	    type = resultSet.getString("type");
	    color = resultSet.getString("color");
	    userInputs.add("OPTION:Type:" + type);	 
	    userInputs.add("OPTION:Color:"+color);
	    
	    while(resultSet.next()){
		userInputs.add("OPTION:Type:"+resultSet.getString("type"));
		userInputs.add("OPTION:Color:"+resultSet.getString("color"));
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // we need to close the connection
	    close();
	}
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	try{
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.claytiles where type ='"
			    + type
			    + "' and color ='"
			    + color
			    + "'");
	    resultSet.next();
	    cost[materialIndex] = resultSet.getDouble("materialcost");
	    cost[laborIndex] = resultSet.getDouble("laborcost");
	    cost[equipIndex] = resultSet.getDouble("equipmentcost");
	    cost[totalIndex] = resultSet.getDouble("totalCost");
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop");
	    
	    description = resultSet.getString("description");
	    costVector = cost;
	}catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Type")){
		type = userInputsMap.get(temp);
	    }else if(temp.equals("Color")){
		color = userInputsMap.get(temp);
	    }
	}
    }
    

    @Override
    public double randomDrawTotalCost() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from insulation.claytiles where description= '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    return resultSet.getDouble("totalCost");
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return 0.0;
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.claytiles");
	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("description"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

}
