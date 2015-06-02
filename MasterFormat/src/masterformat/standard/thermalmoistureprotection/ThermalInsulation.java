package masterformat.standard.thermalmoistureprotection;

import java.sql.SQLException;
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
    // see if the insulation is faced or unfaced
    private String faced;
    // the R-value of the insulation
    private double rvalue;
    // the thickness of the insulation
    private double thickness;

    @Override
    protected void initializeData() {
	userInputs.clear();

	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    // first, select the insulation type

	    // get the options for the type of constructions
	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation");

	    // initialize the default insulation type
	    resultSet.next();
	    insulationType = resultSet.getString("insulationtype");
	    userInputs.add("OPTION:TYPE:" + insulationType);

	    while (resultSet.next()) {
		userInputs.add("OPTION:TYPE:"
			+ resultSet.getString("insulationtype"));

	    }

	    // set the product of insulation

	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
			    + insulationType + "'");

	    // initialize the default insulation product
	    resultSet.next();
	    insulationProduct = resultSet.getString("product");
	    userInputs.add("OPTION:PRODUCT:" + insulationProduct);

	    while (resultSet.next()) {
		userInputs.add("OPTION:PRODUCT:"
			+ resultSet.getString("product"));
	    }

	    // set the insulation constructions
	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
			    + insulationType + "'");

	    // initialize the default special character
	    resultSet.next();
	    insulationConstruction = resultSet.getString("construction");
	    userInputs.add("OPTION:CONSTRUCTION:" + insulationConstruction);

	    while (resultSet.next()) {
		userInputs.add("OPTION:CONSTRUCTION:"
			+ resultSet.getString("construction"));
	    }

	    // Set the material of insulation

	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
			    + insulationType + "'");

	    // initialize the default insulation material
	    resultSet.next();
	    material = resultSet.getString("material");
	    userInputs.add("OPTION:MATERIAL:" + material);

	    while (resultSet.next()) {
		userInputs.add("OPTION:MATERIAL:"
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
	if (insulationType != null) {
	    // insulation type must be specified
	    try {
		    super.testConnect();

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

		resultSet = statement.executeQuery(selectQuery.toString()
			+ "' and thickness>='" + thickness + "' and rvalue>='"
			+ rvalue + "' order by totalcost");

		if (!resultSet.next()) {
		    // this means the selected insulation can not satisfy
		    // the
		    // condition
		    // we need to modularize the insulation based on r-value
		    double tempRvalue = rvalue;
		    while (!resultSet.next()) {
			numberOfLayer *= 2;
			tempRvalue = tempRvalue / 2;

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
		if (material.equals("Fiberglass")) {
		    addFacedValue();
		}
	    } else if (temp.equalsIgnoreCase("FACED")) {
		faced = userInputsMap.get(temp);
		// no need to initialize because we already know
		// most of the needed inputs at this step
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
	
	try{
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation where rvalue <= '"
			    + rvalue +"' and construction = '" + insulationConstruction+"'");
	    
	    while(resultSet.next()){
		descriptionList.add(resultSet.getString("description"));
	    }
	    
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }

    private void addFacedValue() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from insulation.thermalinsulation where insulationtype = '"
			    + insulationType
			    + "' and material = '"
			    + material
			    + "'");
	    // initialize the default faced option
	    resultSet.next();
	    faced = resultSet.getString("faced");
	    userInputs.add("OPTION:FACED:" + faced);

	    while (resultSet.next()) {
		userInputs.add("OPTION:FACED:" + resultSet.getString("faced"));
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
	    super.testConnect();

	    statement = connect.createStatement();
	    
	    if(!descriptionList.isEmpty()){
		int index = randGenerator.nextInt(descriptionList.size());
		    resultSet = statement
			    .executeQuery("select * from insulation.thermalinsulation where description = '"
				    +descriptionList.get(index)+"'");
		    resultSet.next();
		numMaterial = Math.round(rvalue/resultSet.getDouble("rvalue"));
	    }else{
		resultSet = statement
			.executeQuery("select * from insulation.thermalinsulation where totalcost = (select min(totalcost) from insulation.thermalinsulation)");
		    resultSet.next();
		numMaterial = Math.ceil(thickness/resultSet.getDouble("thickness"));
	    }
	    return resultSet.getDouble("totalcost") * numMaterial;
	}catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	//hopefully we won't reach here
	return 0.0;
    }
}
