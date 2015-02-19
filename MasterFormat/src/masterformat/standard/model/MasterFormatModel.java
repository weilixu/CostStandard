package masterformat.standard.model;

import masterformat.api.ComponentFactory;
import masterformat.api.MasterFormat;
import masterformat.standard.concrete.ConcreteFactory;
import masterformat.standard.hvac.boiler.BoilerFactory;
import masterformat.standard.masonry.MasonryFactory;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtectionFactory;

public class MasterFormatModel {
    
    private ComponentFactory factory;
    
    public MasterFormatModel(){
	
    }
    
    public MasterFormat getUserInputFromMap(String type, String description){
	if(type.equalsIgnoreCase("CONCRETE")){
	    factory = new ConcreteFactory();
	    return factory.getConcrete(description);
	}else if(type.equalsIgnoreCase("MASONRY")){
	    factory = new MasonryFactory();
	    return factory.getMasonry(description);
	}else if(type.equalsIgnoreCase("THERMAL MOISTURE PROTECTION")){
	    factory = new ThermalMoistureProtectionFactory();
	    return factory.getThermalMoistureProtection(description);
	}else if(type.equalsIgnoreCase("BOILER")){
	    factory = new BoilerFactory();
	    return factory.getBoiler(description);
	}
	return null;
    }
}
