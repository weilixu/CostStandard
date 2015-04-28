package masterformat.standard.model;

import masterformat.api.ComponentFactory;
import masterformat.api.MasterFormat;
import masterformat.standard.concrete.ConcreteFactory;
import masterformat.standard.electrical.ElectricalFactory;
import masterformat.standard.finishes.FinishesFactory;
import masterformat.standard.hvac.boiler.BoilerFactory;
import masterformat.standard.hvac.condenserunits.CondenserUnitsFactory;
import masterformat.standard.hvac.convectionunits.ConvectionUnitsFactory;
import masterformat.standard.hvac.fan.FanFactory;
import masterformat.standard.hvac.furnaces.FurnaceFactory;
import masterformat.standard.hvac.pump.PumpFactory;
import masterformat.standard.hvac.unitary.UnitarySystemFactory;
import masterformat.standard.masonry.MasonryFactory;
import masterformat.standard.openings.OpeningsFactory;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtectionFactory;

public class MasterFormatModel {
    
    private ComponentFactory factory;
        
    public MasterFormatModel() throws Exception{

    }
    
    public MasterFormat getUserInputFromMap(String type, String description) throws Exception{
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
	}else if(type.equals("FAN")){
	    factory = new FanFactory();
	    return factory.getFan(description);
	}else if(type.equalsIgnoreCase("CONDENSERUNIT")){
	    factory = new CondenserUnitsFactory();
	    return factory.getCondenserUnit(description);
	}else if(type.equalsIgnoreCase("FURNACE")){
	    factory = new FurnaceFactory();
	    return factory.getFurnace(description);
	}else if(type.equalsIgnoreCase("UnitaryHVAC")){
	    factory = new UnitarySystemFactory();
	    return factory.getUnitarySystem(description);
	}else if(type.equalsIgnoreCase("PUMP")){
	    factory = new PumpFactory();
	    return factory.getPump(description);
	}else if(type.equalsIgnoreCase("ConvectionUnit")){
	    factory = new ConvectionUnitsFactory();
	    return factory.getConvectionUnits(description);
	}else if(type.equalsIgnoreCase("Electrical")){
	    factory = new ElectricalFactory();
	    return factory.getElectrical(description);
	}else if(type.equalsIgnoreCase("Finishes")){
	    factory = new FinishesFactory();
	    return factory.getFinishes(description);
	}else if(type.equalsIgnoreCase("Openings")){
	    factory = new OpeningsFactory();
	    return factory.getOpenings(description);
	}
	return null;
    }
}
