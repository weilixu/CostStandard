package masterformat.standard.thermalmoistureprotection;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.hvac.unitary.UnitarySystem;
import masterformat.standard.masonry.Masonry;

public class ThermalMoistureProtectionFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	//this factory would not produce any masonry products
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	//this factory would not produce any concrete products
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	if(thermalMoistureProtectionType.equalsIgnoreCase("RIGID INSULATION")){
	    return new RigidInsulation();
	}
	return null;
    }

    @Override
    public Boiler getBoiler(String hvac) {
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
    public Pump getPump(String pump) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public UnitarySystem getUnitarySystem(String unitary) {
	// TODO Auto-generated method stub
	return null;
    }
}
