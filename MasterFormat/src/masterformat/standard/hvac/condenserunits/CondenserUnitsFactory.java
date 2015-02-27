package masterformat.standard.hvac.condenserunits;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.decentralized.heatpump.HeatPump;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class CondenserUnitsFactory extends ComponentFactory{

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
	if(condenserunit.equalsIgnoreCase("Packaged Air-Cooled Refrigerant Condensing Units")){
	    return new PackagedCU();
	}else if(condenserunit.equalsIgnoreCase("Air-Cooled Refrigerant Condensers")){
	    return new RefrigerantCondenser();
	}
	return null;
    }

    @Override
    public Furnace getFurnace(String furnace) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public HeatPump getHeatPump(String heatpump) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Pump getPump(String pump) {
	// TODO Auto-generated method stub
	return null;
    }

}
