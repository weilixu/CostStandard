package masterformat.api;

import masterformat.standard.concrete.Concrete;
import masterformat.standard.electrical.Electrical;
import masterformat.standard.finishes.Finishes;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.convectionunits.ConvectionUnits;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.hvac.pump.Pump;
import masterformat.standard.hvac.unitary.UnitarySystem;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.openings.Openings;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;
import masterformat.standard.wood.Wood;

public abstract class ComponentFactory {
    
    //manufacture masonry from the list
    public Masonry getMasonry(String masonryType) throws Exception{
	return null;
    }
    
    public Concrete getConcrete(String concreteType){
	return null;
    }
    
    public ThermalMoistureProtection getThermalMoistureProtection(String thermalMoistureProtectionType){
	return null;
    }
    
    public Boiler getBoiler(String boiler){
	return null;
    }
    
    public Fan getFan(String fan){
	return null;
    }
    
    public CondenserUnits getCondenserUnit(String condenserunit){
	return null;
    }
    
    public Furnace getFurnace(String furnace){
	return null;
    }
    
    public UnitarySystem getUnitarySystem(String unitary){
	return null;
    }
    
    public Pump getPump(String pump){
	return null;
    }
    
    public ConvectionUnits getConvectionUnits(String unit){
	return null;
    }
    
    public Electrical getElectrical(String electrical){
	return null;
    }
    
    public Finishes getFinishes(String finish){
	return null;
    }
    
    public Openings getOpenings(String opening){
	return null;
    }
    
    public Wood getWood(String wood){
	return null;
    }
}
