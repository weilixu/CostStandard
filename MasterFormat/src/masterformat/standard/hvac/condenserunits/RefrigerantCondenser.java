package masterformat.standard.hvac.condenserunits;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RefrigerantCondenser extends AbstractCondenserUnits {
    
    /**
     * belt drive or direct drive
     */
    private String drives;
    private Double capacity;

    public RefrigerantCondenser() {
	unit = "$/Ea";
	hierarchy = "236200 Packaged Compressor and Condenser Units:236313 Air-Cooled Refrigerant Condensers";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Capacity")) {
		capacity = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("Drive")){
		drives = userInputsMap.get(temp);
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

	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from hvac.refrigerantcondenser where capacity<='"
			    + capacity + "'");
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

	    if (!descriptionList.isEmpty()) {
		int index = randGenerator.nextInt(descriptionList.size());
		resultSet = statement
			.executeQuery("select * from hvac.refrigerantcondenser where description = '"
				+ descriptionList.get(index) + "'");
		resultSet.next();
		double unitPower = resultSet.getDouble("capacity");
		return resultSet.getDouble("totalcost")
			* Math.ceil(capacity / unitPower);
	    } else {
		resultSet = statement.executeQuery("select * from hvac.refrigerantcondenser where capacity >= '"
				+"' order by totalcost");
		resultSet.next();
		return resultSet.getDouble("totalcost");
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
		    .executeQuery("select * from hvac.refrigerantcondenser");
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

	    int numberOfCondenser = 1;

	    resultSet = statement
		    .executeQuery("select * from hvac.refrigerantcondenser where capacity>='"
			    + capacity + "' and drive = '" + drives+"'");

	    if (!resultSet.next()) {
		// this means there is no such blower utility set can satisfy the
		// flow rate, we need to modularize the fan
		double tempCapacity = capacity;
		while (!resultSet.next()) {
		    numberOfCondenser *= 2;

		    tempCapacity = tempCapacity / 2;
		    resultSet = statement
			    .executeQuery("select * from hvac.refrigerantcondenser where flowrate>='"
				    + tempCapacity + "' and drive = '" + drives+"'");
		}
	    }
	    
	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfCondenser;
	    cost[laborIndex] = resultSet.getDouble("laborcost") * numberOfCondenser;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfCondenser;
	    cost[totalIndex] = resultSet.getDouble("totalCost") * numberOfCondenser;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfCondenser;

	    // set the description
	    description = resultSet.getString("description");

	    costVector = cost;

	    optionLists.add(description);
	    optionQuantities.add(numberOfCondenser);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
