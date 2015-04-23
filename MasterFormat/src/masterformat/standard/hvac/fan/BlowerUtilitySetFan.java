package masterformat.standard.hvac.fan;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility ventilating set. This type of fans are self-contained units
 * consisting of a complete fan, motor and drive package. The arrangements offer
 * a compact and economical design.
 * 
 * This type of fan has two drivers option. V-belt drive: In a belt driven
 * configuration, the motor exists independently of the fan blades and at least
 * one belt-sometimes more- connects the motor to the fan's moving parts. This
 * offers greater flexibility in terms of RPM speed and such fans are cheaper
 * than direct drive fans of comparable size. However there is more friction
 * between moving parts which leads to higher maintenance and energy costs
 * 
 * Direct Drive: The fan motor that controls the movement of the fan blades is
 * connected either to a shaft or fan axle. Thus the fan blades will rotate at
 * the same speed as the motor rotates. Unlike belt-drive fans, it has greater
 * efficiency and also less energy loss from friction. However, it has lesser
 * flexibility compared to belt driven fans,and also more expensive.
 * 
 * @author Weili
 *
 */
public class BlowerUtilitySetFan extends AbstractFan {

    /**
     * have options V-belt drive or Direct drive
     */
    private String drives;
    private Double flowRate;

    public BlowerUtilitySetFan() {
	unit = "$/Ea";
	hierarchy = "230000 HVAC:233400 HVAC Fans:233414 Blower HVAC Fans:233414.107500 Utility Set";
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
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.blowerutilityset");

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
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/concrete?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from hvacfan.blowerutilityset where description = '"
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
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvacfan.blowerutilityset");
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
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    int numberOfFan = 1;

	    resultSet = statement
		    .executeQuery("select * from hvacfan.blowerutilityset where flowrate>='"
			    + flowRate + "' and drive = '" + drives+"'");

	    if (!resultSet.next()) {
		// this means there is no such blower utility set can satisfy the
		// flow rate, we need to modularize the fan
		double fanFlowRate = flowRate;
		while (!resultSet.next()) {
		    numberOfFan *= 2;

		    fanFlowRate = fanFlowRate / 2;
		    resultSet = statement
			    .executeQuery("select * from hvacfan.blowerutilityset where flowrate>='"
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
