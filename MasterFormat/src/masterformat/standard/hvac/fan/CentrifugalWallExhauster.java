package masterformat.standard.hvac.fan;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalWallExhauster extends AbstractFan {
    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    public CentrifugalWallExhauster() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233416 Centrifugal HVAC Fans:233416.108500 Wall exhausters";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Flow Rate")) {
		flowRate = Double.parseDouble(userInputsMap.get(temp));
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
	
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalwallexhauster");

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
    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalwallexhauster");
	    // initialize the default driver
	    resultSet.next();
	    drives = resultSet.getString("drive");
	    userInputs.add("OPTION:Drive:"+drives);

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
    public double randomDrawTotalCost() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalwallexhauster where description = '"
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
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    int numberOfFan = 1;

	    resultSet = statement
		    .executeQuery("select * from hvacfan.centrifugalwallexhauster where flowrate>='"
			    + flowRate + "' and drive = '" + drives+"'");

	    if (!resultSet.next()) {
		// this means there is no such blower utility set can satisfy the
		// flow rate, we need to modularize the fan
		double fanFlowRate = flowRate;
		while (!resultSet.next()) {
		    numberOfFan *= 2;

		    fanFlowRate = fanFlowRate / 2;
		    resultSet = statement
			    .executeQuery("select * from hvacfan.centrifugalwallexhauster where flowrate>='"
				    + fanFlowRate + "' and drive = '" + drives+"'");
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

	    // set the description
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
