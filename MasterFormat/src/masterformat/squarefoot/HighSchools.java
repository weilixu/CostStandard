package masterformat.squarefoot;

public class HighSchools extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	typicalSize = 101000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(123.0,188.0,150.0));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(3.24,10.45,7.55));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(6.80,18.70,10.25));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(11.75,24.50,15.95));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(12.30,24.0,16.40));
	unitConversion();
    }

}
