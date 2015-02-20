package masterformat.api;

import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public abstract class ComponentFactory {
    
    //manufacture masonry from the list
    public abstract Masonry getMasonry(String masonryType);
    
    public abstract Concrete getConcrete(String concreteType);
    
    public abstract ThermalMoistureProtection getThermalMoistureProtection(String thermalMoistureProtectionType);
    
    public abstract Boiler getBoiler(String boiler);
    
    public abstract Fan getFan(String fan);
}
