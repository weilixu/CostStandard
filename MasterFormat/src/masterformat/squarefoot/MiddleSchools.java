package masterformat.squarefoot;

public class MiddleSchools extends AbstractBuildingTypes{
    
    @Override
    protected void initializeData() {
	typicalSize = 92000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(117.0,177.0,145.0));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(12.55,23.0,18.80));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(3.20,8.80,6.0));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(6.80,10.40,8.40));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(13.60,29.0,16.55));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(11.90,18.60,14.70));
	unitConversion();	
    }

}
