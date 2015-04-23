package masterformat.standard.concrete;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CastInPlaceSlabOnGrade extends AbstractConcrete{
    private static String TAG = "Slab on grade";

    private double area;
    private double thickness;
    
    public CastInPlaceSlabOnGrade(){
	unit = "$/m2";
	hierarchy = "030000 Concrete:033000 Cast-In-Place Concrete:033053.40 Concrete In Place";
    }


    @Override
    public void selectCostVector() {
	Double[] cost = new Double[numOfCostElement];
	//indicate the number of slab on grade floor
	Double num = 1.0;

	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/concrete?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();
	    
	    resultSet = statement
		    .executeQuery("select * from concrete.castinplace where construction = '"
			    + TAG
			    + "' and area <= '"
			    + area
			    + "' and thickness >= '"
			    + thickness
			    + "' order by totalcost");
	    //if there is no matching, select the most expensive product in the database
	    if (!resultSet.next()) {		
		resultSet = statement
			.executeQuery("select * from concrete.castinplace where totalcost = (select max(totalcost) from concrete.castinplace where construction = '"
				+ TAG + "' and area <= '" + area+"')");
		resultSet.next();
		num = Math.ceil(thickness/resultSet.getDouble("thickness"));
	    }
	    cost[materialIndex] = resultSet.getDouble("materialcost")*num* resultSet.getDouble("thickness");
	    cost[laborIndex] = resultSet.getDouble("laborcost")*num* resultSet.getDouble("thickness");
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")*num* resultSet.getDouble("thickness");
	    cost[totalIndex] = resultSet.getDouble("totalCost")*num* resultSet.getDouble("thickness");
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")*num* resultSet.getDouble("thickness");

	    description = TAG + " " + resultSet.getString("type");
	    costVector = cost;
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
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Area")){
		area = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("thickness")){
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
	
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try{
	    area = Double.parseDouble(surfaceProperties[floorAreaIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Area:m2");
	}
	try{
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	}catch(NumberFormatException e){
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
			    + "' and area <= '"
			    + area
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
    protected void initializeData() {
	//there is no data to initialize
    }
}
