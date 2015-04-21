package masterformat.standard.masonry;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class BrickVennerMasonry extends AbstractMasonry {
    private static String TAG = "Brick Veneer Masonry";

    //shows the tyep of the brick that selected
    private String brickType;
    //shows any special charactor the type of brick might have
    private String specialCharacter;
    
    public BrickVennerMasonry(){
	unit = "$/m2";
	hierarchy = "042100 Clay Unit Masonry:042113 Brick Masonry:042113.13 Brick Veneer Masonry";
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	Double[] factor = new Double[numOfCostElement];
	String operation = null;
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/masonry?"
			    + "user=root&password=911383");

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
		    .executeQuery("select * from masonry.specialcharacter where description = '"
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
	    description = TAG + " "+brickType;
	    costVector = cost;
	} catch (SQLException e) {
	    e.printStackTrace();
	}finally{
	    close();
	}
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("BrickType")){
		brickType = userInputsMap.get(temp);
	    }else if(temp.equals("SpecialCharacter")){
		specialCharacter = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	//there is nothing can be mapped from energyplus to this module
    }

    /**
     * 
     * As last, all the information is built in a 2-dimension hashmap where the
     * first level filter point to the second level filter and then point to the
     * array of calculated cost
     */
    protected void initializeData() {
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/masonry?"
			    + "user=root&password=911383");

	    statement = connect.createStatement();

	    // get the options for the type of constructions
	    resultSet = statement
		    .executeQuery("select type from masonry.brickmasonry where MASONRYNAME= '"
			    + TAG + "'");
	    while (resultSet.next()) {
		userInputs.add("OPTION:BrickType:"
			+ resultSet.getString("type"));
	    }

	    // gets the special construction for this type
	    resultSet = statement
		    .executeQuery("select description from masonry.specialcharacter where MASONRYNAME= '"
			    + TAG + "'");
	    while (resultSet.next()) {
		userInputs.add("OPTION:SpecialCharacter:"
			+ resultSet.getString("description"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	} finally{
	    //we need to close the connection
	    close();
	}
    }
}
