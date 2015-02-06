package masterformat.squarefoot;

public class OfficesLowRise extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(103.0,135.0,175.0));
	distParams.put(SITE,DistParametersGenerator.normalDistParameter(8.30,21.50,14.45));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(4.0,14.70,8.05));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(0.93,5.85,2.15));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(3.69,8.35,5.70));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(8.15,16.65,11.40));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(8.45,17.20,12.10));
	unitConversion();	
    }

}
