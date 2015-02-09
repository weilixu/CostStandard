package masterformat.squarefoot;

public class WarehousesStorage extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	typicalSize = 25000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(44.50,95.0,63.50));
	distParams.put(SITE,DistParametersGenerator.normalDistParameter(4.62,13.80,9.15));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(2.54,13.70,6.35));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(0.68,8.70,1.55));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(1.49,5.0,2.68));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(1.70,6.45,4.80));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(2.65,8.20,4.98));
	unitConversion();
	
    }

}
