package masterformat.standard.hvac.unitary;

import masterformat.api.ComponentFactory;

public class UnitarySystemFactory extends ComponentFactory{

    @Override
    public UnitarySystem getUnitarySystem(String unitary) {
	if(unitary.equalsIgnoreCase("Air-Source Heat Pumps")){
	    return new AirSourceHeatPump();
	}else if(unitary.equalsIgnoreCase("PACKAGED CABINET TYPE AIR-CONDITIONERS")){
	    return new CabinetAC();
	}
	return null;
    }
}
