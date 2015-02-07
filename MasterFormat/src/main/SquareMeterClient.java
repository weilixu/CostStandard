package main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import masterformat.squarefoot.AbstractBuildingTypes;
import masterformat.squarefoot.BuildingType;
import masterformat.squarefoot.SquareCostEstimationFactory;

public class SquareMeterClient {
    
    public static void main(String[] args){
	SquareCostEstimationFactory factory = new SquareCostEstimationFactory(BuildingType.HOSPITALS);
	
	AbstractBuildingTypes costInfo = factory.getBuildingType();
	
	Double multiplier = costInfo.getCostMultiplier(2000);
	HashMap<String, Double[]> temp = costInfo.getDistParams();
	
	Set<String> keys = temp.keySet();
	Iterator<String> iterator = keys.iterator();
	while(iterator.hasNext()){
	    String name = iterator.next();
	    Double[] cost = temp.get(name);
	    System.out.print(name + " ");
	    System.out.print(cost[0]+ " ");
	    System.out.print(cost[1]);
	    System.out.println();
	    
	}
	System.out.println("This is the multiplier: "+multiplier);
	
	
	
	
    }

}
