package masterformat.standard.hvac.boiler;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class BoilerFactory extends ComponentFactory{

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
    public Boiler getBoiler(String boilerType) {
	if(boilerType.equalsIgnoreCase("BOILER:STEAM")){
	    return new SteamBoiler();
	}else if(boilerType.equalsIgnoreCase("BOILER:HOTWATER")){
	    return new HotWaterBoiler();
	}
	return null;
    }

}
