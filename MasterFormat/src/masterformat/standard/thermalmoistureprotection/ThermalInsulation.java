package masterformat.standard.thermalmoistureprotection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ThermalInsulation extends AbstractThermalMoistureProtection {

    // see what's the insulation types
    private String insulationType;
    // see the insulation product -e.g. rigid or foam etc.
    private String insulationProduct;
    // see the insulation for wall or ceiling etc.
    private String insulationConstruction;
    // see the insulation material
    private String material;
    // see if the insulaiton is faced or unfaced
    private String faced;
    // the R-value of the insulation
    private double rvalue;
    // the thickness of the insulation
    private double thickness;
    // the width of the insulaiton
    private double width;

    @Override
    protected void initializeData() {
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/masonry?"
			    + "user=root&password=911383");

	    statement = connect.createStatement();

	    // first, select the insulation type
	    if (insulationType == null) {
		// get the options for the type of constructions
		resultSet = statement
			.executeQuery("select * from insulation.thermalinsulation");
		while (resultSet.next()) {
		    userInputs.add("OPTION:TYPE:"
			    + resultSet.getString("insulationtype"));

		}
	    } else {
		userInputs.clear();
		// set the product of insulation
		if (insulationProduct == null) {
		    resultSet = statement
			    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
				    + insulationType + "'");
		    while (resultSet.next()) {
			userInputs.add("OPTION:PRODUCT:"
				+ resultSet.getString("product"));
		    }

		}
		// set the insulation constructions
		if (insulationConstruction == "") {
		    resultSet = statement
			    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
				    + insulationType + "'");
		    while (resultSet.next()) {
			userInputs.add("OPTION:CONSTRUCTION:"
				+ resultSet.getString("construction"));
		    }
		}
		// Set the material of insulation
		if (material == null) {
		    resultSet = statement
			    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
				    + insulationType + "'");
		    while (resultSet.next()) {
			userInputs.add("OPTION:MATERIAL:"
				+ resultSet.getString("material"));
		    }
		} else if (material.equals("Fiberglass")) {
		    resultSet = statement
			    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
				    + insulationType
				    + "' and material = '"
				    + material + "'");
		    while (resultSet.next()) {
			userInputs.add("OPTION:FACED:"
				+ resultSet.getString("faced"));
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    @Override
    public void selectCostVector() {
	Double[] cost = new Double[numOfCostElement];
	int numberOfLayer = 1;
	if (insulationType != null) {
	    // insulation type must be specified
	    try {
		connect = DriverManager
			.getConnection("jdbc:mysql://localhost/masonry?"
				+ "user=root&password=911383");
		statement = connect.createStatement();

		// insulation type must be true
		StringBuffer selectQuery = new StringBuffer(
			"select * from insulation.thermalinsulation where insulationtype = '"
				+ insulationType);

		if (insulationProduct != null) {
		    selectQuery.append("' and ");
		    selectQuery.append("product = '");
		    selectQuery.append(insulationProduct);
		}

		if (insulationConstruction != "") {
		    selectQuery.append("' and ");
		    selectQuery.append("construction = '");
		    selectQuery.append(insulationConstruction);
		}

		if (material != null) {
		    selectQuery.append("' and ");
		    selectQuery.append("material = '");
		    selectQuery.append(material);
		}

		if (faced != null) {
		    selectQuery.append("' and ");
		    selectQuery.append("faced = '");
		    selectQuery.append(faced);
		}

		if (insulationProduct == null || insulationConstruction == null
			|| material == null) {
	
		    initializeData();
		} else if (material.equals("Fiberglass") && faced == null) {
		    initializeData();
		} else {

		    resultSet = statement.executeQuery(selectQuery.toString()
			    + "' and thickness>='" + thickness
			    + "' and rvalue>='" + rvalue
			    + "' order by totalcost");

		    if (!resultSet.next()) {
			// this means the selected insulation can not satisfy
			// the
			// condition
			// we need to modularize the insulation based on r-value
			double tempRvalue = rvalue;
			while (!resultSet.next()) {
			    numberOfLayer += 1;
			    tempRvalue = rvalue / 2;
	
			    resultSet = statement.executeQuery(selectQuery
				    + "' and rvalue>='" + tempRvalue
				    + "' order by totalcost");
			}
		    }

		    cost[materialIndex] = resultSet.getDouble("materialcost")
			    * numberOfLayer;
		    cost[laborIndex] = resultSet.getDouble("laborcost")
			    * numberOfLayer;
		    cost[equipIndex] = resultSet.getDouble("equipmentcost")
			    * numberOfLayer;
		    cost[totalIndex] = resultSet.getDouble("totalCost")
			    * numberOfLayer;
		    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
			    * numberOfLayer;

		    description = resultSet.getString("description");
		    costVector = cost;

		    optionLists.add(description);
		    optionQuantities.add(numberOfLayer);
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close();
	    }
	}
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equalsIgnoreCase("TYPE")) {
		insulationType = userInputsMap.get(temp);
	    } else if (temp.equalsIgnoreCase("PRODUCT")) {
		insulationProduct = userInputsMap.get(temp);
	    } else if (temp.equalsIgnoreCase("CONSTRUCTION")) {
		insulationConstruction = userInputsMap.get(temp);
	    } else if (temp.equalsIgnoreCase("MATERIAL")) {
		material = userInputsMap.get(temp);
	    } else if (temp.equalsIgnoreCase("FACED")) {
		faced = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	} catch (NumberFormatException | NullPointerException e) {
	    userInputs.add("INPUT:Thickness:m");
	}
	try {
	    rvalue = Double.parseDouble(surfaceProperties[resistanceIndex]);
	} catch (NumberFormatException | NullPointerException e) {
	    userInputs.add("INPUT:Rvalue:m2K/W");
	}
	insulationConstruction = surfaceProperties[surfaceTypeIndex];
    }

}
