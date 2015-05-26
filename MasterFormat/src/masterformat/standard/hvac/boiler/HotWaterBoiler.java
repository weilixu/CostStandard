package masterformat.standard.hvac.boiler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class HotWaterBoiler extends AbstractBoiler {

    private String sourceType;
    private Double power;

    public HotWaterBoiler() {
	unit = "$/Ea";
	hierarchy = "235200 Heating Boiler";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Source")) {
		sourceType = userInputsMap.get(temp);
	    } else if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	if (sourceType != "") {
	    sourceType = surfaceProperties[sourceTypeIndex];
	    if (sourceType.equals("Electricity")) {
		sourceType = "Electric";
	    } else if (sourceType.equals("NaturalGas")
		    || sourceType.equals("PropaneGas")) {
		sourceType = "Gas";
	    } else if (sourceType.equals("Diesel")
		    || sourceType.equals("Gasoline")) {
		sourceType = "Oil";
	    } else {
		sourceType = "Gas//Oil";
	    }
	}

	try {
	    power = Double.parseDouble(surfaceProperties[capacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}

	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvac.heatingboilers where media ='hot water' and source = '"
			    + sourceType + "' and capacity <= '" + power + "'");
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

	    if (!descriptionList.isEmpty()) {
		int index = randGenerator.nextInt(descriptionList.size());
		resultSet = statement
			.executeQuery("select * from hvac.heatingboilers where description = '"
				+ descriptionList.get(index) + "'");
		resultSet.next();
		double unitPower = resultSet.getDouble("capacity");
		return resultSet.getDouble("totalcost")
			* Math.ceil(power / unitPower);
	    } else {
		resultSet = statement
			.executeQuery("select * from hvac.heatingboilers where media ='hot water' and source = '"
				+ sourceType + "' order by totalcost");
		resultSet.next();
		return resultSet.getDouble("totalCost");
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
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	// Double[] factor = new Double[numOfCostElement];
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    int numberOfBoiler = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.heatingboilers where media ='hot water' and source = '"
			    + sourceType
			    + "' and capacity >= '"
			    + power
			    + "' order by totalcost");
	    if (!resultSet.next()) {
		// this means there is no such boiler that can satisfy the
		// capacity. We need to modularize the boiler
		double boilerCapacity = power;
		while (!resultSet.next()) {
		    numberOfBoiler *= 2;

		    boilerCapacity = boilerCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.heatingboilers where media = 'hot water' and source = '"
				    + sourceType
				    + "' and capacity >= '"
				    + boilerCapacity + "' order by totalcost");
		}
	    }
	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfBoiler;
	    cost[laborIndex] = resultSet.getDouble("laborcost")
		    * numberOfBoiler;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfBoiler;
	    cost[totalIndex] = resultSet.getDouble("totalCost")
		    * numberOfBoiler;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfBoiler;

	    description = resultSet.getString("description");
	    costVector = cost;

	    optionLists.add(description);
	    optionQuantities.add(numberOfBoiler);

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
