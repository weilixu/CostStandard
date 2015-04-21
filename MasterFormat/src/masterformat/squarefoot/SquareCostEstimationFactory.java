package masterformat.squarefoot;

public class SquareCostEstimationFactory {
    //private final static String DIST_TYPE = "Normal";
    private BuildingType buildingType;

    public SquareCostEstimationFactory() {
	// this class allows to instantiate this object without specify the
	// building type
    }

    public SquareCostEstimationFactory(BuildingType type) {
	this.buildingType = type;
    }
    
    public void setBuildingType(BuildingType type){
	buildingType = type;
    }
    
    public BuildingType getBuildingType(){
	return buildingType;
    }

    public AbstractBuildingTypes getBuilding() {
	switch (buildingType) {
	case APARTMENTSLOWRISE:
	    return new ApartmentsLowRise();

	case APARTMENTSMIDRISE:
	    return new ApartmentsMidRise();

	case APARTMENTSHIGHRISE:
	    return new ApartmentsHighRise();

	case BANKS:
	    return new Banks();

	case FIRESTATIONS:
	    return new FireStations();

	case HOSPITALS:
	    return new Hospitals();

	case SENIORHOUSING:
	    return new SeniorHousing();

	case LIBRARIES:
	    return new Libraries();

	case OFFICELOWRISE:
	    return new OfficesLowRise();

	case OFFICEMIDRISE:
	    return new OfficesMidRise();

	case OFFICEHIGHRISE:
	    return new OfficesHighRise();

	case RESTAURANTS:
	    return new Restaurants();

	case ELEMENTARYSCHOOLS:
	    return new ElementarySchools();

	case MIDDLESCHOOLS:
	    return new MiddleSchools();

	case HIGHSCHOOLS:
	    return new HighSchools();

	case WAREHOUSESSTORAGE:
	    return new WarehousesStorage();
	}
	return null;
    }
}
