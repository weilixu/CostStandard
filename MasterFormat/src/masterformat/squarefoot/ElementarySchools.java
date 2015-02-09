package masterformat.squarefoot;

public class ElementarySchools extends AbstractBuildingTypes{
    @Override
    protected void initializeData() {
	typicalSize = 41000*conversionFactor;
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(114.0,172.0,141.0));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(8.0,25.50,17.30));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(2.63,9.40,4.96));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(6.50,12.30,9.20));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(9.80,15.55,22.50));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(10.70,18.20,14.25));
	unitConversion();	
    }
}
