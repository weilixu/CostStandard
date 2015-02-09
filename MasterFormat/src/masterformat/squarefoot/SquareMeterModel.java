package masterformat.squarefoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import masterformat.distribution.TruncatedNormalDistribution;
import masterformat.listener.SquareMeterCostModelListener;

public class SquareMeterModel {

    // size multiplier that deviates the typical size of the building
    private Double buildingSize;
    private Double sizeMultiplier;
    // a database contains info includes: String: division name; Double[]:
    // generated samples
    private HashMap<String, double[]> costDataMap;

    private final SquareCostEstimationFactory factory;
    private AbstractBuildingTypes building;
    private BuildingType type;

    private int simulationNumber;
    // this indicates the factor truncate the distribution of the cost by 2
    // variance
    private final double cutOffFactor = 1.5;
    
    private List<SquareMeterCostModelListener> costModelListeners;

    public SquareMeterModel() {
	sizeMultiplier = 1.0;
	costDataMap = new HashMap<String, double[]>();
	factory = new SquareCostEstimationFactory();
	costModelListeners = new ArrayList<SquareMeterCostModelListener>();
    }

    public void setBuildingSize(Double size) {
	buildingSize = size;
    }

    public void setSimulationNumber(int number) {
	simulationNumber = number;
    }
    
    public void addCostModelListener(SquareMeterCostModelListener ml){
	costModelListeners.add(ml);
    }

    // once the user set a new building size, the costInfoMap will require to be
    // cleared
    public void setBuildingType(BuildingType type) {
	this.type = type;
	//clean the data structure
	costDataMap.clear();
	factory.setBuildingType(this.type);
	building = factory.getBuilding();
    }

    public void generateSamples() {
	// retrieve data from database
	HashMap<String, Double[]> costInfo = building.getDistParams();
	sizeMultiplier = building.getCostMultiplier(buildingSize);

	// create new data by drawing samples from the normal distribution space
	Set<String> divisions = costInfo.keySet();
	Iterator<String> iterator = divisions.iterator();
	while (iterator.hasNext()) {
	    // set the key
	    String division = iterator.next();
	    // get mean, variance and set the cut off criterias
	    Double[] params = costInfo.get(division);
	    TruncatedNormalDistribution generator = new TruncatedNormalDistribution(
		    params[0], params[1], params[0] - cutOffFactor * params[1],
		    params[0] + cutOffFactor * params[1]);

	    // set the value
	    double[] costData = new double[simulationNumber];
	    for (int i = 0; i < simulationNumber; i++) {
		costData[i] = generator.truncatedSample() * sizeMultiplier;
	    }
	    // put the value in the datastructure
	    costDataMap.put(division, costData);
	}
	onUpdatedCostDataMap();
    }
    
    private void onUpdatedCostDataMap(){
	for(SquareMeterCostModelListener ml: costModelListeners){
	    ml.costInfoUpdated(costDataMap);
	}
    }
}
