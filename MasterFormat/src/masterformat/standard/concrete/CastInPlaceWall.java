package masterformat.standard.concrete;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CastInPlaceWall extends AbstractConcrete {
    private static String TAG = "Wall";

    private double thickness;
    private double height;

    public CastInPlaceWall() {
	unit = "$/m2";
	hierarchy = "030000 Concrete:033000 Cast-In-Place Concrete:033053.40 Concrete In Place";
    }

    /**
     * For cast in place wall, the required attributes are thickness and height
     * of the concrete
     * 
     * @param t
     * @param h
     */
    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    height = Double.parseDouble(surfaceProperties[heightIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Height:m");
	}
	try {
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Thickness:m");
	}
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Height")) {
		height = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("Thickness")) {
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void selectCostVector() {
	Double[] cost = new Double[numOfCostElement];

	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/concrete?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from concrete.castinplace where construction = '"
			    + TAG
			    + "' and height >= '"
			    + height
			    + "' and thickness >= '"
			    + thickness
			    + "' order by totalcost;");
	    //if there is no matching, select the most expensive product in the database
	    if (!resultSet.next()) {

		resultSet = statement
			.executeQuery("select * from concrete.castinplace where construction = '"
				+ TAG
				+ "' and totalcost = (select max(totalcost) from concrete.castinplace where construction = '"
				+ TAG + "');");
		resultSet.next();
	    }
	    cost[materialIndex] = resultSet.getDouble("materialcost");
	    cost[laborIndex] = resultSet.getDouble("laborcost");
	    cost[equipIndex] = resultSet.getDouble("equipmentcost");
	    cost[totalIndex] = resultSet.getDouble("totalCost");
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop");

	    description = TAG + " " + resultSet.getString("type");
	    costVector = cost;
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    @Override
    protected void initializeData() {
	// no need to initialize data Becasue there is no further requirement
	// needed for mapping
    }
}
