package masterformat.squarefoot;

public class Hospitals extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(206.0,355.0,258.0));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(5.20,17.30,10.05));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(17.70,32.0,25.0));
	distParams.put(HVAC,DistParametersGenerator.normalDistParameter(26.0,46.5,33.5));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(22.50,45.50,30.50));
	unitConversion();
    }

}
