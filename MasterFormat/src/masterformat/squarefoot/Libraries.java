package masterformat.squarefoot;

public class Libraries extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(143.0,248.0,190.0));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(9.50,32.50,19.35));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(1.91,7.75,5.15));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(5.15,10.20,7.30));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(14.40,25.50,19.0));
	unitConversion();
    }
}
