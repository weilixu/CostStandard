package masterformat.standard.hvac.furnaces;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.decentralized.heatpump.HeatPump;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class FurnaceFactory extends ComponentFactory{

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
	if(furnace.equalsIgnoreCase("Coil:Heating:Electric")){
	    return new ElectricFurnaces();
	}else if(furnace.equalsIgnoreCase("Coil:Heating:Gas")){
	    return new FuelFiredFurnaces();
	}
	return null;
    }

    @Override
    public HeatPump getHeatPump(String heatpump) {
	// TODO Auto-generated method stub
	return null;
    }

}
