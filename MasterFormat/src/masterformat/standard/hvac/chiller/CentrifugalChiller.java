package masterformat.standard.hvac.chiller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CentrifugalChiller extends AbstractChiller{
    
    private Double capacity;
    private final String type = "Centrifugal";
    
    public CentrifugalChiller(){
	unit = "$/Ea";
	hierarchy = "236416 Centrifugal Chiller";
    }

    @Override
    protected void initializeData() {
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
    }

    @Override
    public void selectCostVector() {
	optionLists.clear();
	optionQuantities.clear();
	Double[] cost = new Double[numOfCostElement];
	
	try{
	    super.testConnect();
	    statement = connect.createStatement();
	    resultSet = statement.executeQuery("select * from hvac.chillers where DESCRIPTION = '" + description +"'");
	    int numberOfChiller = 1;
	    if(!resultSet.next()){
		//this means there is no such chiller
		double chillerCapacity = capacity;
		double unitCap = resultSet.getDouble("CAPACITY") * 3.5168525; //convert to kW
		numberOfChiller =  Integer.valueOf((int) Math.round(chillerCapacity / unitCap));
	    }
	    
	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfChiller;
	    cost[laborIndex] = resultSet.getDouble("laborcost")
		    * numberOfChiller;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfChiller;
	    cost[totalIndex] = resultSet.getDouble("totalCost")
		    * numberOfChiller;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfChiller;
	    
	    costVector = cost;
	    
	    optionLists.add(description);
	    optionQuantities.add(numberOfChiller);
	    
	}catch(SQLException e){
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
	    if(temp.equals("Power")){
		capacity=Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("Description")){
		description = userInputsMap.get(temp);
	    }
	}	
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try{
	    super.testConnect();
	    statement = connect.createStatement();
	    
	    resultSet = statement.executeQuery("select * from hvac.chillers where TYPE = '" + type +"'");
	    while(resultSet.next()){
		descriptionList.add(resultSet.getString("description"));
	    }
	}catch(SQLException e){
	    e.printStackTrace();
	}finally{
	    close();
	}	
    }
}
