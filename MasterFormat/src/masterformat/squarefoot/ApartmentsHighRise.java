package masterformat.squarefoot;

public class ApartmentsHighRise extends AbstractBuildingTypes{

    @Override
    protected void initializeData() {
	distParams.put(TOTAL, DistParametersGenerator.normalDistParameter(112.0,155.0,129.0));
	distParams.put(SITE, DistParametersGenerator.normalDistParameter(4.07,9.20,6.60));
	distParams.put(MASONRY,DistParametersGenerator.normalDistParameter(6.50,14.65,11.80));
	distParams.put(FINISH,DistParametersGenerator.normalDistParameter(12.45,18.35,15.55));
	distParams.put(EQUIP,DistParametersGenerator.normalDistParameter(3.61,5.85,4.44));
	distParams.put(CEQUIP,DistParametersGenerator.normalDistParameter(2.55,5.25,3.87));
	distParams.put(PLUMB,DistParametersGenerator.normalDistParameter(7.15,11.95,9.75));
	distParams.put(ELEC,DistParametersGenerator.normalDistParameter(4.70,13.15,9.75));
	unitConversion();	
    }

}
