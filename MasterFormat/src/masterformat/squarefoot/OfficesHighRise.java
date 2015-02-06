package masterformat.squarefoot;

public class OfficesHighRise extends AbstractBuildingTypes{
    
    @Override
    protected void initializeData(){
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(134.00,208.0,169.0));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(8.15,14.80,9.95));
	unitConversion();
    }

}
