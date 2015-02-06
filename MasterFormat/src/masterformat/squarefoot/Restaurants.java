package masterformat.squarefoot;

public class Restaurants extends AbstractBuildingTypes {

    @Override
    protected void initializeData() {

	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(
		149.0,249.0,192.0));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(8.80,35.50,23.50));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(11.80,18.75,14.30));
	distParams.put(HVAC, DistParametersGenerator.normalDistParameter(14.95,25.0,21.0));
	distParams.put(ELEC, DistParametersGenerator.normalDistParameter(15.70,25.0,19.40));
	unitConversion();

    }

}
