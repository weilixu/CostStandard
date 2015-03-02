package masterformat.standard.hvac.unitary;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class UnitarySystemFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Boiler getBoiler(String boiler) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Fan getFan(String fan) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public CondenserUnits getCondenserUnit(String condenserunit) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Furnace getFurnace(String furnace) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public UnitarySystem getUnitarySystem(String unitary) {
	if(unitary.equalsIgnoreCase("Air-Source Heat Pumps")){
	    return new AirSourceHeatPump();
	}else if(unitary.equalsIgnoreCase("PACKAGED CABINET TYPE AIR-CONDITIONERS")){
	    return new CabinetAC();
	}
	return null;
    }

    @Override
    public Pump getPump(String pump) {
	// TODO Auto-generated method stub
	return null;
    }

}
