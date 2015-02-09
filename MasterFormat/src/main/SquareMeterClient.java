package main;
import javax.swing.SwingUtilities;

import masterformat.gui.SquareMeterCostPanel;
import masterformat.squarefoot.SquareMeterModel;


public class SquareMeterClient {
    
    public static void main(String[] args){
	SwingUtilities.invokeLater(new Runnable(){
	    @Override
	    public void run(){
		SquareMeterModel m = new SquareMeterModel();
		SquareMeterCostPanel gui = new SquareMeterCostPanel(m);
		
	    }
	});
    }
    
    
    
    
    
    
    

//    public static void main(String[] args) {
//	SquareCostEstimationFactory factory = new SquareCostEstimationFactory(
//		BuildingType.APARTMENTSHIGHRISE);
//
//	AbstractBuildingTypes costInfo = factory.getBuilding();
//
//	Double multiplier = costInfo.getCostMultiplier(1000);
//	HashMap<String, Double[]> temp = costInfo.getDistParams();
//
//	Set<String> keys = temp.keySet();
//	Iterator<String> iterator = keys.iterator();
//	while (iterator.hasNext()) {
//	    String name = iterator.next();
//	    Double[] cost = temp.get(name);
//	    System.out.print(name + " ");
//	    System.out.print(cost[0] + " ");
//	    System.out.print(cost[1]);
//	    TruncatedNormalDistribution tempNorm = new TruncatedNormalDistribution(
//		    cost[0], cost[1], cost[0] - 2*cost[1], cost[0] + 2*cost[1]);
//	    System.out.println("Sampled value: " + tempNorm.sample());
//	}
//	System.out.println("This is the multiplier: " + multiplier);
//    }
}
