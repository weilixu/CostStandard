package masterformat.squarefoot;

public class SquareCostEstimationFactory {
    private final static String DIST_TYPE = "Normal";
    private BuildingType buildingType;
    
    
    public SquareCostEstimationFactory(BuildingType type){
	this.buildingType = type;
    }

}
