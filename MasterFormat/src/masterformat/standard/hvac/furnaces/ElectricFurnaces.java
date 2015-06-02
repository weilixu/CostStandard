package masterformat.standard.hvac.furnaces;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ElectricFurnaces extends AbstractFurnace {

    private Double power;

    public ElectricFurnaces() {
	unit = "$/Ea.";
	hierarchy = "235400 Furnaces:235413 Electric-Resistance Furnaces:235413.10 Electric Furnaces";
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

	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from hvac.furnaces where source = 'Electric'");

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
	    super.testConnect();

	    statement = connect.createStatement();

	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from hvac.furnaces where source = 'Electric' and description = '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    double unitPower = resultSet.getDouble("power");
	    if (unitPower > power) {
		return resultSet.getDouble("totalcost");
	    } else {
		return resultSet.getDouble("totalcost")
			* Math.ceil(power / unitPower);
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
	// nothing to initialize
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    int numberOfFurnace = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.furnaces where source = 'Electric' and power>='"
			    + power + "'");

	    if (!resultSet.next()) {
		// this means there is no such furnace that can satisfy the
		// capacity. We need to modularize the furnace
		double furnaceCapacity = power;
		while (!resultSet.next()) {
		    numberOfFurnace *= 2;
		    furnaceCapacity = furnaceCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.furnaces where source = 'Electric' and power>='"
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
