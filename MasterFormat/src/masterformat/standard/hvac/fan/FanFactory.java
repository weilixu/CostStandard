package masterformat.standard.hvac.fan;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.condenserunits.CondenserUnits;
import masterformat.standard.hvac.furnaces.Furnace;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;


public class FanFactory extends ComponentFactory{

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
	if(fan.equalsIgnoreCase("AXIAL FLOW HVAC FANS")){
	    return new AxialFlowFan();
	}else if(fan.equalsIgnoreCase("BLOWER TYPE HVAC CEILING FANS")){
	    return new BlowerCeilingFan();
	}else if(fan.equalsIgnoreCase("BLOWER TYPE HVAC UTILITY SET")){
	    return new BlowerUtilitySetFan();
	}else if(fan.equalsIgnoreCase("CENTRIFUGAL TYPE HVAC SUPPLY OR EXHAUST BOOSTER")){
	    return new CentrifugalBooster();
	}else if(fan.equalsIgnoreCase("CENTRIFUGAL TYPE HVAC UTILITY SET")){
	    return new CentrifugalUtilitySetFan();
	}else if(fan.equalsIgnoreCase("CENTRIFUGAL TYPE HVAC ROOF EXHAUST")){
	    return new CentrifugalRoofExhauster();
	}else if(fan.equalsIgnoreCase("CENTRIFUGAL TYPE HVAC WALL EXHAUST")){
	    return new CentrifugalWallExhauster();
	}
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
}
