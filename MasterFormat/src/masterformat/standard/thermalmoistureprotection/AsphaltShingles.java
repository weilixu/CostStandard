package masterformat.standard.thermalmoistureprotection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AsphaltShingles extends AbstractThermalMoistureProtection {
    private String type;
    private String installation;

    public AsphaltShingles() {
	unit = "$/m2";
	hierarchy = "073100 Shingles and Shakes:073113 Asphalt Shingles:073113.10 Asphalt Roof Shingles";
    }

    @Override
    protected void initializeData() {
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/masonry?"
			    + "user=root&password=911383");

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.asphaltshingles");
	    resultSet.next();
	    type = resultSet.getString("type");
	    installation = resultSet.getString("installation");
	    userInputs.add("OPTION:Type:" + type);
	    userInputs.add("OPTION:Install:" + installation);
	    while (resultSet.next()) {
		userInputs.add("OPTION:Type:" + resultSet.getString("type"));
		userInputs.add("OPTION:Install:"
			+ resultSet.getString("installation"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // we need to close the connection
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
		    .getConnection("jdbc:mysql://localhost/masonry?"
			    + "user=root&password=911383");

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.asphaltshingles where type ='"
			    + type
			    + "' and installation ='"
			    + installation
			    + "'");
	    resultSet.next();
	    cost[materialIndex] = resultSet.getDouble("materialcost");
	    cost[laborIndex] = resultSet.getDouble("laborcost");
	    cost[equipIndex] = resultSet.getDouble("equipmentcost");
	    cost[totalIndex] = resultSet.getDouble("totalCost");
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop");

	    description = resultSet.getString("description");
	    costVector = cost;
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
		    .executeQuery("select * from insulation.asphaltshingles where description= '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    return resultSet.getDouble("totalCost");
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return 0.0;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Type")) {
		type = userInputsMap.get(temp);
	    } else if (temp.equals("Install")) {
		installation = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/concrete?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.asphaltshingles");
	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("description"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

}
