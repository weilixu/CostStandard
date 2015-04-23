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
	
	try{
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
			    + thickness+"'");
	    
	    while(resultSet.next()){
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
	double numMaterial = 1.0;
	try{
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/concrete?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();
	    
	    if(!descriptionList.isEmpty()){
		int index = randGenerator.nextInt(descriptionList.size());
		    resultSet = statement
			    .executeQuery("select * from concrete.castinplace where construction = '"
				    + TAG
				    + "' and type = '"+descriptionList.get(index)+"'");
		    resultSet.next();
	    }else{
		resultSet = statement
			.executeQuery("select * from concrete.castinplace where construction = '"
				+ TAG
				+ "' and totalcost = (select max(totalcost) from concrete.castinplace where construction = '"
				+ TAG + "');");
		    resultSet.next();
		numMaterial = Math.ceil(thickness/resultSet.getDouble("thickness"));
	    }
	    return resultSet.getDouble("totalcost") * resultSet.getDouble("thickness")*numMaterial;
	}catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	//hopefully we won't reach here
	return 0.0;
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
	Double num = 1.0;
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
		num = Math.ceil(thickness/resultSet.getDouble("thickness"));
	    }
	    cost[materialIndex] = resultSet.getDouble("materialcost") * resultSet.getDouble("thickness") * num;
	    cost[laborIndex] = resultSet.getDouble("laborcost")* resultSet.getDouble("thickness")*num;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")* resultSet.getDouble("thickness")* num;
	    cost[totalIndex] = resultSet.getDouble("totalCost")* resultSet.getDouble("thickness")* num;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")* resultSet.getDouble("thickness")* num;

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
