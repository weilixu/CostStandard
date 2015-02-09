package masterformat.squarefoot;

public class SeniorHousing extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	typicalSize = 37000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(101.0,157.0,128.0));
	distParams.put(SITE,DistParametersGenerator.normalDistParameter(7.05,16.05,10.95));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(1.91,16.85,11.55));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(2.45,5.35,3.37));
	distParams.put(CEQUIP,DistParametersGenerator.normalDistParameter(2.29,4.49,3.31));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(7.50,12.10,9.60));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(3.86,8.15,5.45));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(7.55,13.10,10.25));
	unitConversion();
    }

}
