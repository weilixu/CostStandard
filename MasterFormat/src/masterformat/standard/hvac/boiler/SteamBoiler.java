package masterformat.standard.hvac.boiler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SteamBoiler extends AbstractBoiler {

    private String sourceType;
    private Double power;

    public SteamBoiler() {
	unit = "$/Ea";
	hierarchy = "235200 Heating Boilers";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Source")) {
		sourceType = userInputsMap.get(temp);
	    } else if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	if (sourceType != "") {
	    sourceType = surfaceProperties[sourceTypeIndex];
	    if (sourceType.equals("Electricity")) {
		sourceType = "Electric";
	    } else if (sourceType.equals("NaturalGas")||sourceType.equals("PropaneGas")){
		sourceType = "Gas";
	    }else if(sourceType.equals("Diesel")||sourceType.equals("Gasoline")){
		sourceType = "Oil";
	    }else{
		sourceType = "Gas//Oil";
	    }
	}
	
	try {
	    power = Double.parseDouble(surfaceProperties[capacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	// there is no need to initialize data because all
	// the data can be mapped from EnergyPlus
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	Double[] factor = new Double[numOfCostElement];
	try {
	    connect = DriverManager
		    .getConnection("jdbc:mysql://localhost/hvac?"
			    + "user=root&password=911383");
	    statement = connect.createStatement();

	    int numberOfBoiler = 1;
	    // select a qualified boiler and cheapest boiler
	    resultSet = statement
		    .executeQuery("select * from hvac.heatingboilers where media = steam and source ='"
			    + sourceType
			    + "' and capacity >= '"
			    + power
			    + "' order by totalcost");
	    if (!resultSet.next()) {
		// this means there is no such boiler that can satisfy the
		// capacity. We need to modularize the boiler
		double boilerCapacity = power;
		while (!resultSet.next()) {
		    numberOfBoiler *= 2;
		    boilerCapacity = boilerCapacity / 2; // modularze the
							 // capacity.
		    resultSet = statement
			    .executeQuery("select * from hvac.heatingboilers where media = steam and source = '"
				    + sourceType
				    + "' and capacity >= '"
				    + boilerCapacity + "' order by totalcost");
		}
	    }
	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfBoiler;
	    cost[laborIndex] = resultSet.getDouble("laborcost")
		    * numberOfBoiler;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfBoiler;
	    cost[totalIndex] = resultSet.getDouble("totalCost")
		    * numberOfBoiler;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfBoiler;

	    description = resultSet.getString("description");
	    costVector = cost;
	    
	    optionLists.add(description);
	    optionQuantities.add(numberOfBoiler);

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
    }
}
