package masterformat.squarefoot;

public class ApartmentsLowRise extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	typicalSize = 21000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(74.50,125.0,94.0));
	distParams.put(SITE, DistParametersGenerator.normalDistParameter(5.45,15.30,8.70));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(1.47,5.95,3.62));
	distParams.put(FINISH,DistParametersGenerator.normalDistParameter(7.90,13.45,10.85));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(2.44,5.50,3.70));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(5.80,9.50,7.45));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(3.70,6.70,4.56));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(4.33,7.80,5.75));
	unitConversion();
    }
}
