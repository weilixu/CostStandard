package masterformat.standard.hvac.cooltower;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ForcedDraftCrossFlowCoolingTower extends AbstractCoolingTower implements CoolingTower{
    
    private final String flowType = "Crossflow";
    private Double capacity;
    
    public ForcedDraftCrossFlowCoolingTower(){
	unit = "$/TonAC";
	hierarchy = "236500 Cooling Tower";
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
	    resultSet = statement.executeQuery("select * from hvac.coolingtower where DESCRIPTION = '" + description +"'");
	    int numberOfCoolingTower = 1;
	    if(!resultSet.next()){
		//this means there is no such chiller
		double towerCapacity = capacity;
		double unitCap = resultSet.getDouble("CAPACITY") * 3.5168525; //convert to kW
		numberOfCoolingTower =  Integer.valueOf((int) Math.round(towerCapacity / unitCap));
	    }
	    
	    cost[materialIndex] = resultSet.getDouble("materialcost")
		    * numberOfCoolingTower * capacity;
	    cost[laborIndex] = resultSet.getDouble("laborcost")
		    * numberOfCoolingTower * capacity;
	    cost[equipIndex] = resultSet.getDouble("equipmentcost")
		    * numberOfCoolingTower * capacity;
	    cost[totalIndex] = resultSet.getDouble("totalCost")
		    * numberOfCoolingTower * capacity;
	    cost[totalOPIndex] = resultSet.getDouble("totalInclop")
		    * numberOfCoolingTower * capacity;
	    
	    costVector = cost;
	    
	    optionLists.add(description);
	    optionQuantities.add(numberOfCoolingTower);
	    
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
	    
	    resultSet = statement.executeQuery("select * from hvac.coolingtower where FLOW = '" + flowType +"'");
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
