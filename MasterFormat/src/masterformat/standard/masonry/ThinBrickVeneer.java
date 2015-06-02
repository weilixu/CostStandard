package masterformat.standard.masonry;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ThinBrickVeneer extends AbstractMasonry {
    private static String TAG = "Thin Brick Veneer";
    // shows the tyep of the brick that selected
    private String brickType;
    // shows any special charactor the type of brick might have
    private String specialCharacter;

    public ThinBrickVeneer() throws Exception {
	unit = "$/m2";
	hierarchy = "042100 Clay Unit Masonry:042113 Brick Masonry:042113.14 Thin Brick Veneer";
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	Double[] factor = new Double[numOfCostElement];
	String operation = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from masonry.brickmasonry where type='"
			    + brickType + "'");
	    while (resultSet.next()) {
		cost[materialIndex] = resultSet.getDouble("materialcost");
		cost[laborIndex] = resultSet.getDouble("laborcost");
		cost[equipIndex] = resultSet.getDouble("equipmentcost");
		cost[totalIndex] = resultSet.getDouble("totalCost");
		cost[totalOPIndex] = resultSet.getDouble("totalInclop");
	    }

	    resultSet = statement
		    .executeQuery("select * from masonry.specialcharacter where masonryname = '"
			    + brickType
			    + "' and description = '"
			    + specialCharacter + "'");
	    while (resultSet.next()) {
		factor[materialIndex] = resultSet.getDouble("materialfactor");
		factor[laborIndex] = resultSet.getDouble("laborfactor");
		factor[equipIndex] = resultSet.getDouble("equipmentfactor");
		factor[totalIndex] = resultSet.getDouble("totalfactor");
		factor[totalOPIndex] = resultSet.getDouble("totalinclopfactor");
		operation = resultSet.getString("operation");
	    }

	    if (operation != null) {
		if (operation.equals("ADD")) {
		    cost = addOperation(cost, factor);
		} else if (operation.equals("MULTI")) {
		    cost = multiOperation(cost, factor);
		}
	    }
	    description = TAG + " " + brickType;
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
	    if (temp.equals("BrickType")) {
		brickType = userInputsMap.get(temp);
	    } else if (temp.equals("SpecialCharacter")) {
		specialCharacter = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from masonry.brickmasonry where masonryname='"
			    + TAG + "'");
	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("type"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
    
    @Override
    public double randomDrawTotalCost(){
	try{
	    super.testConnect();

	    statement = connect.createStatement();
	    int index = randGenerator.nextInt(descriptionList.size());
	    resultSet = statement.executeQuery("select * from masonry.brickmasonry where MASONRYNAME= '"
			    + TAG + "' and type = '"+descriptionList.get(index)+"'");
	    resultSet.next();
	    return resultSet.getDouble("totalCost");
	}catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return 0.0;
    }

    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    // get the options for the type of constructions
	    resultSet = statement
		    .executeQuery("select type from masonry.brickmasonry where MASONRYNAME= '"
			    + TAG + "'");

	    // initialize the default bricktype
	    resultSet.next();
	    brickType = resultSet.getString("type");
	    userInputs.add("OPTION:BrickType:" + brickType);

	    while (resultSet.next()) {
		userInputs.add("OPTION:BrickType:"
			+ resultSet.getString("type"));
	    }

	    // gets the special construction for this type
	    resultSet = statement
		    .executeQuery("select description from masonry.specialcharacter where MASONRYNAME= '"
			    + TAG + "'");

	    // initialize the default special character
	    resultSet.next();
	    specialCharacter = resultSet.getString("description");
	    userInputs.add("OPTION:SpecialCharacter:" + specialCharacter);

	    while (resultSet.next()) {
		userInputs.add("OPTION:SpecialCharacter:"
			+ resultSet.getString("description"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // we need to close the connection
	    close();
	}
    }
}
