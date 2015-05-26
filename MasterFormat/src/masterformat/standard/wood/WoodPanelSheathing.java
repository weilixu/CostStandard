package masterformat.standard.wood;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class WoodPanelSheathing extends AbstractWood {
    private double thickness;
    private String material;

    private String construction;

    public WoodPanelSheathing() {
	unit = "$/m2";
	hierarchy = "060000 Wood:061600 Sheathing:061636 Wood Panel Product Sheathing:061636.10 Sheathing";
    }

    @Override
    public double randomDrawTotalCost() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement
		    .executeQuery("select * from wood.woodpanelsheathing where description='"
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
    protected void initializeData() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    // get the options for the type of constructions
	    resultSet = statement
		    .executeQuery("select * from wood.woodpanelsheathing");

	    // initialize the default
	    resultSet.next();
	    material = resultSet.getString("material");
	    userInputs.add("OPTION:Material:" + material);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Material:"
			+ resultSet.getString("material"));
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
	int numberOfLayer = 1;

	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from wood.woodpanelsheathing where construction = '"
			    + construction
			    + "' and material = '"
			    + material
			    + "' and thickness >='" + thickness + "'");

	    if (!resultSet.next()) {
		double tempThickness = thickness;
		while (!resultSet.next()) {
		    numberOfLayer *= 2;
		    tempThickness = tempThickness / 2.0;
		    resultSet = statement
			    .executeQuery("select * from wood.woodpanelsheathing where construction = '"
				    + construction
				    + "' and material = '"
				    + material
				    + "' and thickness >='" + tempThickness + "'");
		}
	    }

	    cost[materialIndex] = resultSet.getDouble("materialcost")* numberOfLayer;
	    cost[laborIndex] = resultSet.getDouble("laborcost")* numberOfLayer;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")* numberOfLayer;
	    cost[totalIndex] = resultSet.getDouble("totalCost")* numberOfLayer;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")* numberOfLayer;
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
	    if (temp.equals("Material")) {
		material = userInputsMap.get(temp);
	    } else if (temp.equals("Thickness")) {
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	construction = surfaceProperties[surfaceTypeIndex];
	//temperary assume to be wall
	if(!construction.equals("Wall")&&!construction.equals("Roof")){
	    construction = "Wall";
	}

	try {
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Thickness:m");
	}

	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from wood.woodpanelsheathing where construction = '"
			    + construction
			    + "' and thickness >='"
			    + thickness
			    + "'");
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
