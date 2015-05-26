package masterformat.standard.hvac.fan;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class CentrifugalBooster extends AbstractFan {

    private Double flowRate;

    public CentrifugalBooster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233416.100200 In-Line centrifugal, supply or exhaust booster";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Flow Rate")) {
		flowRate = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    flowRate = Double.parseDouble(surfaceProperties[flowRateIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Flow Rate:m3/s");
	}
	
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalbooster");

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
		    .executeQuery("select * from hvacfan.centrifugalbooster where description = '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    double unitFlowRate = resultSet.getDouble("flowrate");
	    if (unitFlowRate > flowRate) {
		return resultSet.getDouble("totalcost");
	    } else {
		return resultSet.getDouble("totalcost")
			* Math.ceil(flowRate / unitFlowRate);
	    }
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

    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    int numberOfFan = 1;

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalbooster where flowrate>='"
			    + flowRate + "'");

	    if (!resultSet.next()) {
		// this means there is no such centrifugal booster can satisfy the
		// flow rate, we need to modularize the fan
		double fanFlowRate = flowRate;
		while (!resultSet.next()) {
		    numberOfFan *= 2;

		    fanFlowRate = fanFlowRate / 2;
		    resultSet = statement
			    .executeQuery("select * from hvacfan.centrifugalbooster where flowrate>='"
				    + fanFlowRate + "'");
		}
	    }

	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfFan;
	    cost[laborIndex] = resultSet.getDouble("laborcost") * numberOfFan;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfFan;
	    cost[totalIndex] = resultSet.getDouble("totalCost") * numberOfFan;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfFan;
	    
	    //set the description
	    description = resultSet.getString("description");

	    costVector = cost;
	    
	    optionLists.add(description);
	    optionQuantities.add(numberOfFan);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
