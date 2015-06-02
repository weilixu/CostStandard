package masterformat.standard.finishes;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WoodFlooring extends AbstractFinishes {
    private String material;
    private String character;

    public WoodFlooring() {
	unit = "$/m2";
	hierarchy = "096400 Wood Flooring";
    }

    @Override
    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring");
	    resultSet.next();
	    material = resultSet.getString("material");
	    userInputs.add("OPTION:Material:" + material);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Material:"
			+ resultSet.getString("material"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring where material='"
			    + material + "'");

	    resultSet.next();
	    character = resultSet.getString("description");
	    userInputs.add("OPTION:Character:" + character);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Character:"
			+ resultSet.getString("description"));
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
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring where material = '"
			    + material
			    + "' and description = '"
			    + character
			    + "'");

	    resultSet.next();
	    cost[materialIndex] = resultSet.getDouble("materialcost");
	    cost[laborIndex] = resultSet.getDouble("laborcost");
	    cost[equipIndex] = resultSet.getDouble("equipmentcost");
	    cost[totalIndex] = resultSet.getDouble("totalCost");
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop");
	    
	    description = material+", "+resultSet.getString("description");
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
	    System.out.println(temp);
	    if (temp.equals("Material")) {
		material = userInputsMap.get(temp);
		reGenerateUserInputs();
	    } else if (temp.equals("Character")) {
		character = userInputsMap.get(temp);
	    }
	}
    }

    private void reGenerateUserInputs() {
	userInputs.clear();
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring");
	    while (resultSet.next()) {
		userInputs.add("OPTION:Material:"
			+ resultSet.getString("material"));
	    }

	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring where material = '"
			    + material + "'");
	    resultSet.next();
	    character = resultSet.getString("description");
		userInputs.add("OPTION:Character:"
			+ character);
	    while (resultSet.next()) {
		userInputs.add("OPTION:Character:"
			+ resultSet.getString("description"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // we need to close the connection
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
		    .executeQuery("select * from finishes.woodflooring where description= '"
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
    public void setVariable(String[] surfaceProperties) {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from finishes.woodflooring");

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
