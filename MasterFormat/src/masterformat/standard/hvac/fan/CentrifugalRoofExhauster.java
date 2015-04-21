package masterformat.standard.hvac.fan;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalRoofExhauster extends AbstractFan {
    private static final String fanType = "Centrifugal Type HVAC Fans";
    private static final String fanFunction = "Roof Exhauster";

    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    private String character;

    private final Double speedWindPercent = 1.15;
    private final Double[] explosionproofAddition = { 600.0, 0.0, 0.0, 600.0,
	    660.0 };
    private final Double topdischargePercent = 1.15;

    private Double[] flowRateVector = { 0.15, 0.28, 0.38, 0.68, 0.97, 0.78,
	    1.30, 1.65, 2.32, 4.02, 6.50, 9.70 };

    private static final Double[] Default_Cost_Vector = { 0.0, 0.0, 0.0, 0.0,
	    0.0 };

    public CentrifugalRoofExhauster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.107000 Roof exhauster";
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
	    } else if (temp.equals("Drive")) {
		drives = userInputsMap.get(temp);
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
    }

    @Override
    protected void initializeData() {
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    // add special character
	    resultSet = statement
		    .executeQuery("select * from hvacfan.specialcharacter where fantype>='"
			    + fanType
			    + "' and function = '"
			    + fanFunction
			    + "'");
	    while (resultSet.next()) {
		userInputs.add("OPTION:CHARACTER:"
			+ resultSet.getString("description"));
	    }

	    // extract the driver option
	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalroofexhauster");
	    // initialize the default driver
	    resultSet.next();
	    drives = resultSet.getString("drive");
	    userInputs.add("OPTION:Drive:" + drives);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Drive:" + resultSet.getString("drive"));
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
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    int numberOfFan = 1;

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalroofexhauster where flowrate>='"
			    + flowRate + "' and drive = '" + drives + "'");

	    if (!resultSet.next()) {
		// this means there is no such centrifugalroofexhauster can satisfy
		// the
		// flow rate, we need to modularize the fan
		double fanFlowRate = flowRate;
		while (!resultSet.next()) {
		    numberOfFan *= 2;

		    fanFlowRate = fanFlowRate / 2;
		    resultSet = statement
			    .executeQuery("select * from hvacfan.centrifugalroofexhauster where flowrate>='"
				    + fanFlowRate
				    + "' and drive = '"
				    + drives
				    + "'");
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
