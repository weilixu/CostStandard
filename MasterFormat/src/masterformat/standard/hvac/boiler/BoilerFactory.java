package masterformat.standard.hvac.boiler;

import masterformat.api.ComponentFactory;

public class BoilerFactory extends ComponentFactory{

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
