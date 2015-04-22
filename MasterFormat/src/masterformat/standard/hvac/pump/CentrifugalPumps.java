package masterformat.standard.hvac.pump;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalPumps extends AbstractPump {
    /**
     * types: bronze sweat connection, flange connection, cast iron flange
     * connection, pumps circulating, varied by size";
     */
    private String pumpType;
    private boolean nonferrous;
    private Double power;
    private Double size;

    public CentrifugalPumps() {
	unit = "$/Ea.";
	hierarchy = "232100 Hydronic Piping and Pump:232123 Hydronic Pumps:232123.13 In-Line Centrifugal Hydronic Pumps";
	nonferrous = false;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("PUMPTYPE")) {
		pumpType = userInputsMap.get(temp);
	    } else if (temp.equals("Ferrous")) {
		String ferrou = userInputsMap.get(temp);
		if (ferrou.equalsIgnoreCase("true")) {
		    nonferrous = true;
		}
	    } else if (temp.equals("SIZE")) {
		size = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    power = Double.parseDouble(properties[powerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {

	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();
	    if (pumpType == null) {
		resultSet = statement
			.executeQuery("select pumptype from hvac.pump");

		// initialize the default pump type
		resultSet.next();
		pumpType = resultSet.getString("pumptype");
		userInputs.add("OPTION:PUMPTYPE:" + pumpType);

		while (resultSet.next()) {
		    userInputs.add("OPTION:PUMPTYPE:"
			    + resultSet.getString("pumptype"));
		}
	    } else {
		resultSet = statement
			.executeQuery("select pumpsize from hvac.pump where pumptype = '"
				+ pumpType + "'");

		// initialize the default pump size
		resultSet.next();
		size = resultSet.getDouble("pumpsize");
		userInputs.add("OPTION:SIZE:" + size);
		
		while (resultSet.next()) {
		    userInputs.add("OPTION:SIZE:"
			    + resultSet.getDouble("pumpsize"));
		}
	    }

	} catch (Exception e) {
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
	int numberOfPump = 1;
	if (pumpType != null) {
	    try {
		connect = DriverManager
			.getConnection("jdbc:mysql://localhost/hvac?"
				+ "user=root&password=911383");
		statement = connect.createStatement();

		if (size == null) {
		    initializeData();
		} else {
		    resultSet = statement
			    .executeQuery("select * from hvac.pump where pumpType = '"
				    + pumpType
				    + "' and pumpsize='"
				    + size
				    + "' and pumppower>='"
				    + power
				    + "' order by totalcost");

		    if (!resultSet.next()) {
			// this means the selected pump can not satisfy the
			// power condition
			// we need to modularize the pump based on power
			double tempPower = power;
			while (!resultSet.next()) {
			    numberOfPump *= 2;
			    tempPower = tempPower / 2;

			    resultSet = statement
				    .executeQuery("select * from hvac.pump where pumpType = '"
					    + pumpType
					    + "' and pumpsize='"
					    + size
					    + "' and pumppower>='"
					    + tempPower
					    + "' order by totalcost");
			}
		    }
		    cost[materialIndex] = resultSet.getDouble("materialcost")
			    * numberOfPump;
		    cost[laborIndex] = resultSet.getDouble("laborcost")
			    * numberOfPump;
		    cost[equipIndex] = resultSet.getDouble("equipmentcost")
			    * numberOfPump;
		    cost[totalIndex] = resultSet.getDouble("totalCost")
			    * numberOfPump;
		    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
			    * numberOfPump;

		    description = resultSet.getString("description");
		    costVector = cost;

		    optionLists.add(description);
		    optionQuantities.add(numberOfPump);
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close();
	    }
	}
    }
}
