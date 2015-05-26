package masterformat.standard.hvac.fan;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class BlowerCeilingFan extends AbstractFan {

    private static final String fanType = "Blower Type HVAC Fans";
    private static final String fanFunction = "Ceiling Fan";

    private Double flowRate;
    private String character;

    public BlowerCeilingFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233414.102500 Ceiling Fan";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Flow Rate")) {
		flowRate = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("CHARACTER")) {
		character = userInputsMap.get(temp);
	    }
	}
    }

    /**
     * Only flow rate can be extracted from this surface properties, the other
     * two attributes are physical design issues, and is currently not included
     * in the data array
     */
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
		    .executeQuery("select * from hvacfan.blowerceilingfan");

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
		    .executeQuery("select * from hvacfan.blowerceilingfan where description = '"
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
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.specialcharacter where fantype>='"
			    + fanType
			    + "' and function = '"
			    + fanFunction
			    + "'");
	    
	    // initialize the default character
	    resultSet.next();
	    character = resultSet.getString("description");
	    userInputs.add("OPTION:CHARACTER:"+character);

	    while (resultSet.next()) {
		userInputs.add("OPTION:CHARACTER:"
			+ resultSet.getString("description"));
	    }

	} catch (SQLException e) {
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
	Double[] factor = new Double[numOfCostElement];
	String operation = null;
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    int numberOfFan = 1;

	    resultSet = statement
		    .executeQuery("select * from hvacfan.blowerceilingfan where flowrate>='"
			    + flowRate + "'");

	    if (!resultSet.next()) {
		// this means there is no such blower ceiling fan can satisfy the
		// flow rate, we need to modularize the fan
		double fanFlowRate = flowRate;
		while (!resultSet.next()) {
		    numberOfFan *= 2;

		    fanFlowRate = fanFlowRate / 2;
		    resultSet = statement
			    .executeQuery("select * from hvacfan.blowerceilingfan where flowrate>='"
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
	    description = resultSet.getString("description");
	    
	    // gets the special character for this fan
	    if (character != null) {
		resultSet = statement
			.executeQuery("select * from hvacfan.specialcharacter where fantype = '"
				+ fanType
				+ "' and function = '"
				+ fanFunction
				+ "' and description = '" + character + "'");
		resultSet.next();
		factor[materialIndex] = resultSet.getDouble("material");
		factor[laborIndex] = resultSet.getDouble("labor");
		factor[equipIndex] = resultSet.getDouble("equipment");
		factor[totalIndex] = resultSet.getDouble("total");
		factor[totalOPIndex] = resultSet.getDouble("totalinclop");
		operation = resultSet.getString("operation");
		if (operation != null) {
		    if (operation.equals("ADD")) {
			cost = addOperation(cost, factor);
		    } else if (operation.equals("MULT")) {
			cost = multiOperation(cost, factor);
		    }
		}
	    }
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
