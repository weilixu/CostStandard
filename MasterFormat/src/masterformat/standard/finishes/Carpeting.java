package masterformat.standard.finishes;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.api.DatabaseUtils;

public class Carpeting extends AbstractFinishes {

    private String carpetType;
    // face weight indicates the amount of fiber in a square meter carpet.
    private String carpetMaterial;
    private double faceweight;

    public Carpeting() {
	unit = "$/m2";
	hierarchy = "096800 Carpeting";
    }

    @Override
    protected void initializeData() {
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select type from finishes.carpeting");
	    resultSet.next();
	    carpetType = resultSet.getString("type");
	    userInputs.add("OPTION:CarpetType:" + carpetType);

	    while (resultSet.next()) {
		userInputs.add("OPTION:CarpetType:"
			+ resultSet.getString("type"));
	    }
	    
	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting where type = '"+carpetType+"'");
	    resultSet.next();
	    carpetMaterial = resultSet.getString("material");
	    userInputs.add("OPTION:Material:" + carpetMaterial);

	    while (resultSet.next()) {
		userInputs.add("OPTION:Material:"
			+ resultSet.getString("material"));
	    }
	    
	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting where type = '"
			    + carpetType + "' and material='"+carpetMaterial+"'");
	    resultSet.next();
	    faceweight = resultSet.getDouble("faceweight");
	    userInputs.add("OPTION:FaceWeight:" + faceweight);
	    while (resultSet.next()) {
		userInputs.add("OPTION:FaceWeight:"
			+ resultSet.getDouble("FaceWeight"));
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

	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting where type ='"
			    + carpetType
			    + "' and faceweight ='"
			    + faceweight
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
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("CarpetType")) {
		carpetType = userInputsMap.get(temp);
		addFaceWeight();
	    } else if (temp.equals("FaceWeight")) {
		faceweight = Double.parseDouble(userInputsMap.get(temp));
	    } else if(temp.equals("Material")){
		carpetMaterial = userInputsMap.get(temp);
		addFaceWeight();
	    }
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
		    .executeQuery("select * from finishes.carpeting where description= '"
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
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting");
	    while (resultSet.next()) {
		descriptionList.add(resultSet.getString("description"));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    private void addFaceWeight() {
	userInputs.clear();
	try {
	    connect = DriverManager.getConnection(DatabaseUtils.getUrl(),
		    DatabaseUtils.getUser(), DatabaseUtils.getPassword());
	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting where type = '"
			    + carpetType + "' and material = '"+carpetMaterial+"'");

	    while (resultSet.next()) {
		userInputs.add("OPTION:FaceWeight:"
			+ resultSet.getDouble("faceweight"));
	    }
	    
	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting where type = '"
			    + carpetType + "'");
	    while(resultSet.next()){
		userInputs.add("OPTION:Material:"+resultSet.getString("material"));
	    }
	    
	    
	    resultSet = statement
		    .executeQuery("select * from finishes.carpeting");
	    while (resultSet.next()) {
		userInputs.add("OPTION:CarpetType:"
			+ resultSet.getString("type"));
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

}
