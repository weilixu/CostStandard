package masterformat.squarefoot;

public class FireStations extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(113.0,213.0,156.0));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(15.40,38.0,30.0));
	distParams.put(ROOF,DistParametersGenerator.normalDistParameter(3.68,11.35,9.95));
	distParams.put(PAINT,DistParametersGenerator.normalDistParameter(2.86,4.37,4.27));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(1.40,4.97,2.69));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(6.30,14.25,10.05));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(8.15,19.35,14.35));
	unitConversion();
    }

}
