package masterformat.standard.hvac.condenserunits;

import masterformat.api.ComponentFactory;

public class CondenserUnitsFactory extends ComponentFactory{


    @Override
    public CondenserUnits getCondenserUnit(String condenserunit) {
	if(condenserunit.equalsIgnoreCase("Packaged Air-Cooled Refrigerant Condensing Units")){
	    return new PackagedCU();
	}else if(condenserunit.equalsIgnoreCase("Air-Cooled Refrigerant Condensers")){
	    return new RefrigerantCondenser();
	}
	return null;
    }
}
