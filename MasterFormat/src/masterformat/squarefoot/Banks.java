package masterformat.squarefoot;

public class Banks extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(170.0,268.0,211.0));
	distParams.put(SITE, DistParametersGenerator.normalDistParameter(19.40,43.0,30.50));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(7.70,29.50,16.50));
	distParams.put(FINISH,DistParametersGenerator.normalDistParameter(15.15,28.50,23.0));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(6.35,28.50,14.0));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(5.30,11.05,7.55));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(16.05,28.0,21.50));
	unitConversion();	
    }

}
