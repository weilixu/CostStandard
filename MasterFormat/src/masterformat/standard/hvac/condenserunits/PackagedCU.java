package masterformat.standard.hvac.condenserunits;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PackagedCU extends AbstractCondenserUnits {

    private Double capacity;
    
    public PackagedCU() {
	unit = "$/Ea";
	hierarchy = "236200 Packaged Compressor and Condenser Units:236213 Packaged Air-Cooled Refrigerant Compressor and Condenser Units";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Capacity")) {
		capacity = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    capacity = Double.parseDouble(properties[capacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Capacity:Watt");
	}

    }

    @Override
    protected void initializeData() {
	// no  need to initize data
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

	    int numberOfCU = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.packagedcompressor where capacity>='"
			    + capacity + "'");

	    if (!resultSet.next()) {
		// this means there is no such compressor that can satisfy the
		// the cooling load, so we need to modularize the cu
		double tempCapacity = capacity;
		while (!resultSet.next()) {
		    numberOfCU *= 2;

		    tempCapacity = tempCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.packagedcompressor where capacity>='"
				    + tempCapacity + "'");
		}
	    }

	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfCU;
	    cost[laborIndex] = resultSet.getDouble("laborcost") * numberOfCU;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfCU;
	    cost[totalIndex] = resultSet.getDouble("totalCost") * numberOfCU;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfCU;
	    
	    //set the description
	    description = resultSet.getString("description");

	    costVector = cost;
	    
	    optionLists.add(description);
	    optionQuantities.add(numberOfCU);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
