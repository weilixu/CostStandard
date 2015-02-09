package masterformat.squarefoot;

public class ApartmentsMidRise extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	typicalSize = 50000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(99.0,147.0,119.0));
	distParams.put(SITE, DistParametersGenerator.normalDistParameter(3.95,15.45,8.0));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(4.13,12.40,9.05));
	distParams.put(FINISH,DistParametersGenerator.normalDistParameter(12.95,20.50,17.35));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(2.77,5.65,4.30));
	distParams.put(CEQUIP,DistParametersGenerator.normalDistParameter(2.24,3.33,2.76));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(5.80,9.85,9.30));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(6.50,10.75,8.85));
	unitConversion();
    }
    
    

}
