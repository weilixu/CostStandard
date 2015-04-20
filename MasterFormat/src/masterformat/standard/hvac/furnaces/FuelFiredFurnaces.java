package masterformat.standard.hvac.furnaces;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FuelFiredFurnaces extends AbstractFurnace {

    private Double power;


    public FuelFiredFurnaces() {
	unit = "$/Ea.";
	hierarchy = "235400 Furnaces:235416 Fuel-Fired Furnaces:235416.13 Gas-Fired Furnaces";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    power = Double.parseDouble(surfaceProperties[powerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	//nothing to initialize
    }

    @Override
    public void selectCostVector() {
	Double[] cost = new Double[numOfCostElement];
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    int numberOfFurnace = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.furnaces where source = gas and power>='"
			    + power + "'");

	    if (!resultSet.next()) {
		// this means there is no such furnace that can satisfy the
		// capacity. We need to modularize the furnace
		double furnaceCapacity = power;
		while (!resultSet.next()) {
		    numberOfFurnace += 1;
		    furnaceCapacity = furnaceCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.furnaces where source = gas and power>='"
				    + furnaceCapacity + "'");
		}
		cost[materialIndex] = resultSet.getDouble("materialcost")
			* numberOfFurnace;
		cost[laborIndex] = resultSet.getDouble("laborcost")
			* numberOfFurnace;
		cost[equipIndex] = resultSet.getDouble("equipmentcost")
			* numberOfFurnace;
		cost[totalIndex] = resultSet.getDouble("totalCost")
			* numberOfFurnace;
		cost[totalOPIndex] = resultSet.getDouble("totalInclop")
			* numberOfFurnace;

		description = resultSet.getString("description");
		costVector = cost;

		optionLists.add(description);
		optionQuantities.add(numberOfFurnace);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
