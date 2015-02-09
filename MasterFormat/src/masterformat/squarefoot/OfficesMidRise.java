package masterformat.squarefoot;

public class OfficesMidRise extends AbstractBuildingTypes{
    
    @Override
    protected void initializeData(){
	typicalSize = 120000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(109.0,180.0,132.0));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(3.30,7.35,5.10));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(8.30,18.95,11.85));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(8.10,15.70,10.40));
	unitConversion();
    }

}
