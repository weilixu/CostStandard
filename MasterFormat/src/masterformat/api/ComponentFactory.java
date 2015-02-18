package masterformat.api;

import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.hvac;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public abstract class ComponentFactory {
    
    //manufacture masonry from the list
    public abstract Masonry getMasonry(String masonryType);
    
    public abstract Concrete getConcrete(String concreteType);
    
    public abstract ThermalMoistureProtection getThermalMoistureProtection(String thermalMoistureProtectionType);
    
    public abstract hvac getHVAC(String hvac);
}
