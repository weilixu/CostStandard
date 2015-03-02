package masterformat.standard.concrete;

import masterformat.api.ComponentFactory;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.hvac.unitary.UnitarySystem;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class ConcreteFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	
	if(concreteType.equalsIgnoreCase("CAST IN PLACE WALL")){
	    return new CastInPlaceWall();
	}else if(concreteType.equalsIgnoreCase("CAST IN PLACE SLAB ON GRADE")){
	    return new CastInPlaceSlabOnGrade();
	}
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	// TODO Auto-generated method stub
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
