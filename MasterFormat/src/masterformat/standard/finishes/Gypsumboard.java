package masterformat.standard.finishes;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class Gypsumboard extends AbstractFinishes {
    private double thickness;
    private double spacing;
    private double width;
    private String stud;
    private String function;

    public Gypsumboard() {
	unit = "$/m2";
	hierarchy = "092116 Gypsum Board Assemblies";
    }

    @Override
    protected void initializeData() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard");
	    resultSet.next();
	    stud = resultSet.getString("stud");
	    userInputs.add("OPTION:StudType:" + stud);

	    while (resultSet.next()) {
		userInputs
			.add("OPTION:StudType:" + resultSet.getString("stud"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where stud='"
			    + stud + "'");
	    resultSet.next();
	    spacing = resultSet.getDouble("studspacing");
	    userInputs.add("OPTION:StudSpacing:" + spacing);

	    while (resultSet.next()) {
		userInputs.add("OPTION:StudSpacing:"
			+ resultSet.getDouble("studspacing"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where stud='"
			    + stud + "' and studspacing = '" + spacing + "'");
	    resultSet.next();
	    function = resultSet.getString("function");
	    userInputs.add("OPTION:Function:" + function);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Function:"
			+ resultSet.getString("function"));
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
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    if (stud.equals("Wood Studs")) {
		resultSet = statement
			.executeQuery("select * from finishes.gypsumboard where stud='"
				+ stud
				+ "' and studspacing = '"
				+ spacing
				+ "' and function = '" + function + "'");
	    } else {
		resultSet = statement
			.executeQuery("select * from finishes.gypsumboard where stud='"
				+ stud
				+ "' and studspacing = '"
				+ spacing
				+ "' and function = '"
				+ function
				+ "' and studwidth = '" + width + "'");
	    }

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
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("StudType")) {
		stud = userInputsMap.get(temp);
		reGenerateUserInputs();
	    } else if (temp.equals("StudSpacing")) {
		spacing = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("Function")) {
		function = userInputsMap.get(temp);
	    } else if (temp.equals("Width")) {
		width = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    private void reGenerateUserInputs() {
	userInputs.clear();
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard");

	    while (resultSet.next()) {
		userInputs
			.add("OPTION:StudType:" + resultSet.getString("stud"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where stud='"
			    + stud + "'");
	    resultSet.next();
	    spacing = resultSet.getDouble("studspacing");
	    userInputs.add("OPTION:StudSpacing:" + spacing);

	    while (resultSet.next()) {
		userInputs.add("OPTION:StudSpacing:"
			+ resultSet.getDouble("studspacing"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where stud='"
			    + stud + "' and studspacing = '" + spacing + "'");
	    resultSet.next();
	    function = resultSet.getString("function");
	    userInputs.add("OPTION:Function:" + function);
	    while (resultSet.next()) {
		userInputs.add("OPTION:Function:"
			+ resultSet.getString("function"));
	    }

	    if (stud.equals("Metal Studs")) {
		resultSet = statement
			.executeQuery("select * from finishes.gypsumboard where stud='"
				+ stud
				+ "' and studspacing = '"
				+ spacing
				+ "' and function = '" + function + "'");
		while (resultSet.next()) {
		    userInputs.add("OPTION:Width:"
			    + resultSet.getDouble("studwidth"));
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // we need to close the connection
	    close();
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where thickness >='"
			    + thickness + "'");
	    if (!resultSet.next()) {
		resultSet = statement
			.executeQuery("select * from finishes.gypsumboard where thickness <='"
				+ thickness + "'");
	    } else {
		descriptionList.add(resultSet.getString("description"));
	    }

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
	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from finishes.gypsumboard where description= '"
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

}
