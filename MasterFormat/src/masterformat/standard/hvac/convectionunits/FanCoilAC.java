package masterformat.standard.hvac.convectionunits;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class FanCoilAC extends AbstractConvectionUnits {

    private String coolCoilType;
    private Double coolingCapacity;
    private boolean hotWaterCoil;

    private final Double[] heatCoilFactor = { 1.4, 1.1, 1.0, 1.0, 1.0 };

    public FanCoilAC() {
	unit = "$/Ea";
	hierarchy = "238200 Convection Heating and Cooling Units:238219 Fan Coil Units:238219.10 Fan Coil Air Conditioning";
	coolCoilType = "Chilled Water";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("CoolCoilType")) {
		coolCoilType = userInputsMap.get(temp);
	    } else if (temp.equals("coolPower")) {
		coolingCapacity = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	// this only needs cooling capacity
	try {
	    coolingCapacity = Double
		    .parseDouble(properties[coolingCapacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:coolPower:Watt");
	}

	String heatingCapacity = properties[heatingCapacityIndex];
	if (heatingCapacity != null) {
	    hotWaterCoil = true;
	} else {
	    hotWaterCoil = false;
	}

	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from hvac.fancoil where capacity<= '"
			    + coolingCapacity + "'");

	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("description"));
	    }
	    resultSet = statement
		    .executeQuery("select * from hvac.fancoil where capacity>= '"
			    + coolingCapacity + "' order by capacity");
	    if (resultSet.next()) {
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
		    .executeQuery("select * from hvac.fancoil where description = '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    double unitPower = resultSet.getDouble("capacity");
	    double numOfEquip = Math.ceil(coolingCapacity / unitPower);
	    double cost = 0.0;
	    if (hotWaterCoil) {
		cost = numOfEquip
			* (resultSet.getDouble("materialcost") * 1.4
				+ resultSet.getDouble("laborcost") * 1.1 + resultSet
				    .getDouble("equipmentcost"));
	    } else {
		cost = resultSet.getDouble("totalcost")*numOfEquip;
	    }

	    return cost;

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

	    int numberOfFCU = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.fancoil where capacity>='"
			    + coolingCapacity
			    + "' and coolsource = '"
			    + coolCoilType + "'");

	    if (!resultSet.next()) {
		// this means there is no such fcu that can satisfy the
		// the cooling load, so we need to modularize the fcu
		double tempCapacity = coolingCapacity;
		while (!resultSet.next()) {
		    numberOfFCU *= 2;

		    tempCapacity = tempCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.fancoil where capacity>='"
				    + tempCapacity
				    + "' and coolsource = '"
				    + coolCoilType + "'");
		}
	    }

	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfFCU;
	    cost[laborIndex] = resultSet.getDouble("laborcost") * numberOfFCU;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfFCU;
	    cost[totalIndex] = resultSet.getDouble("totalCost") * numberOfFCU;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfFCU;

	    // set the description
	    description = resultSet.getString("description");

	    if (hotWaterCoil) {
		cost = multiOperation(cost, heatCoilFactor);
	    }

	    costVector = cost;

	    optionLists.add(description);
	    optionQuantities.add(numberOfFCU);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
