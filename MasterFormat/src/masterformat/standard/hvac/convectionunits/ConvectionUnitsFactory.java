package masterformat.standard.hvac.convectionunits;

import masterformat.api.ComponentFactory;

public class ConvectionUnitsFactory extends ComponentFactory{

    @Override
    public ConvectionUnits getConvectionUnits(String unit) {
	if(unit.equalsIgnoreCase("Fan Coil Air Conditioning")){
	    return new FanCoilAC();
	}
	return null;
    }
}
