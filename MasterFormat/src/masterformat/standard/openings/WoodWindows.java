package masterformat.standard.openings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WoodWindows extends AbstractOpenings{
    private static final String FRAME = "Wood";
    // in energy model, height of the window will not change
    // however, for the sake of convenience, models may assume
    // continous fenestration area which means the width of a
    // fenestration may varies. Therefore, the mapping is
    // logic in this class is map the height of the openings
    // and their area
    private double area;
    private Integer numOfLayer = 1;
    private String windowType;
    private String insulated;
    private String lowe;
    
    public WoodWindows(){
	unit = "$/Ea";
	hierarchy = "085210 Wood Windows";
    }

    @Override
    protected void initializeData() {
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    // add special character
	    resultSet = statement
		    .executeQuery("select * from openings.windows where frame='"
			    + FRAME + "'");

	    // initialize the default character
	    resultSet.next();
	    windowType = resultSet.getString("windowtype");
	    userInputs.add("OPTION:TYPE:" + windowType);

	    insulated = resultSet.getString("insulating");
	    userInputs.add("OPTION:INSULATION:" + insulated);

	    lowe = resultSet.getString("lowe");
	    userInputs.add("OPTION:LOWE:" + lowe);

	    while (resultSet.next()) {
		userInputs.add("OPTION:TYPE:"
			+ resultSet.getString("windowtype"));
		userInputs.add("OPTION:INSULATION:"
			+ resultSet.getString("insulating"));
		userInputs.add("OPTION:LOWE:" + resultSet.getString("lowe"));

	    }
	} catch (SQLException e) {
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
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    int numberOfWindow = 0;

	    resultSet = statement
		    .executeQuery("select * from openings.windows where frame = '"
			    + FRAME
			    + "' and windowtype = '"+windowType+"'and insulating ='"
			    + insulated
			    + "' and lowe ='" + lowe + "'");
	    
		resultSet.next();
		double unitArea = resultSet.getDouble("height")*resultSet.getDouble("width");
		
		double tempArea = area;
		while(tempArea>0){
		    numberOfWindow++;
		    tempArea-= unitArea;
		}
		
		if(numberOfWindow<1){
		    numberOfWindow = 1;
		}
		
		cost[materialIndex] = resultSet.getDouble("materialcost")
			* numberOfWindow;
		cost[laborIndex] = resultSet.getDouble("laborcost")
			* numberOfWindow;
		cost[equipIndex] = resultSet.getDouble("equipmentcost")
			* numberOfWindow;
		cost[totalIndex] = resultSet.getDouble("totalCost")
			* numberOfWindow;
		cost[totalOPIndex] = resultSet.getDouble("totalInclop")
			* numberOfWindow;

		description = resultSet.getString("description");
		costVector = cost;

		optionLists.add(description);
		optionQuantities.add(numberOfWindow);

	} catch (SQLException e) {
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
		    .executeQuery("select * from openings.windows where description = '"
			    + descriptionList.get(index) + "'");
	    resultSet.next();
	    double unitArea = resultSet.getDouble("height")
		    * resultSet.getDouble("width");
	    if (unitArea > area) {
		return resultSet.getDouble("totalcost");
	    } else {
		return resultSet.getDouble("totalcost")
			* Math.ceil(area / unitArea);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	// hopefully we won't reach here
	return 0.0;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("TYPE")) {
		windowType = userInputsMap.get(temp);
	    } else if (temp.equals("INSULATION")) {
		insulated = userInputsMap.get(temp);
	    } else if (temp.equals("LOWE")) {
		lowe = userInputsMap.get(temp);
	    } else if (temp.equals("AREA")) {
		area = Double.parseDouble(userInputsMap.get(temp));
	    } else if (temp.equals("LAYER")) {
		numOfLayer = Integer.parseInt(userInputsMap.get(temp));
	    }
	}	
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    area = Double.parseDouble(surfaceProperties[glazingSizeIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:AREA:m2");
	}

	try {
	    numOfLayer = Integer.parseInt(surfaceProperties[numLayerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:LAYER:");
	}

	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select * from openings.windows where frame = '"
			    + FRAME + "'");

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
