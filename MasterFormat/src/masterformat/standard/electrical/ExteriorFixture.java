package masterformat.standard.electrical;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ExteriorFixture extends AbstractElectrical {
    private Double power;
    private String mount;
    private Double requiredPower;

    public ExteriorFixture() {
	unit = "$/Ea";
	hierarchy = "265600 Exterior Lighting:265623 Area Lighting:265623.10 Exteiror Fixtures";
    }

    @Override
    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting");

	    // initialize the default exterior lighting type
	    resultSet.next();
	    mount = resultSet.getString("mount");
	    userInputs.add("OPTION:Mount:" + mount);
	    while (resultSet.next()) {
		userInputs.add("OPTION:Mount:" + resultSet.getString("mount"));
	    }

	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting where mount='"
			    + mount + "'");
	    resultSet.next();
	    power = resultSet.getDouble("power");
	    userInputs.add("OPTION:Power:" + power);
	    while (resultSet.next()) {
		userInputs.add("OPTION:Power:" + resultSet.getString("power"));
	    }
	} catch (Exception e) {
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
		    .executeQuery("select * from lighting.exteriorlighting where description = '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    double unitPower = resultSet.getDouble("power");
	    return resultSet.getDouble("totalcost")
		    * Math.ceil(requiredPower / unitPower);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	// hopefully we won't reach here
	return 0.0;
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	int numberOfFix = 1;
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting where mount = '"
			    + mount + "' and power = '" + power + "'");
	    if (!resultSet.next()) {
		resultSet = statement
			.executeQuery("select * from lighting.exteriorlighting where mount = 'wall mount' and power = '"
				+ power + "'");
		resultSet.next();
	    }

	    description = resultSet.getString("description");

	    double tempPower = requiredPower;
	    while (tempPower > 0) {
		tempPower = tempPower - power;
		numberOfFix++;
	    }

	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfFix;
	    cost[laborIndex] = resultSet.getDouble("laborcost") * numberOfFix;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfFix;
	    cost[totalIndex] = resultSet.getDouble("totalCost") * numberOfFix;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfFix;

	    costVector = cost;
	    optionLists.add(description);
	    optionQuantities.add(numberOfFix);
	} catch (Exception e) {
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
	    if (temp.equals("Mount")) {
		mount = userInputsMap.get(temp);
		reGenerateInputs();
	    } else if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    private void reGenerateInputs() {
	userInputs.clear();
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting");
	    while (resultSet.next()) {
		userInputs.add("OPTION:Mount:" + resultSet.getString("mount"));
	    }

	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting where mount='"
			    + mount + "'");
	    while (resultSet.next()) {
		userInputs.add("OPTION:Power:" + resultSet.getString("power"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    requiredPower = Double.parseDouble(properties[electricPowerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}

	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from lighting.exteriorlighting");

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
