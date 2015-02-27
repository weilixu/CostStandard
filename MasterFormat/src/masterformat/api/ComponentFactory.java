package masterformat.api;

import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.decentralized.heatpump.HeatPump;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public abstract class ComponentFactory {
    
    //manufacture masonry from the list
    public abstract Masonry getMasonry(String masonryType);
    
    public abstract Concrete getConcrete(String concreteType);
    
    public abstract ThermalMoistureProtection getThermalMoistureProtection(String thermalMoistureProtectionType);
    
    public abstract Boiler getBoiler(String boiler);
    
    public abstract Fan getFan(String fan);
    
    public abstract CondenserUnits getCondenserUnit(String condenserunit);
    
    public abstract Furnace getFurnace(String furnace);
    
    public abstract HeatPump getHeatPump(String heatpump);
    
    public abstract Pump getPump(String pump);
}
