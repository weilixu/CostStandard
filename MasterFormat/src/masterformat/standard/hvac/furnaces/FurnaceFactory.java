package masterformat.standard.hvac.furnaces;

import masterformat.api.ComponentFactory;


public class FurnaceFactory extends ComponentFactory{

    @Override
    public Furnace getFurnace(String furnace) {
	if(furnace.equalsIgnoreCase("Coil:Heating:Electric")){
	    return new ElectricFurnaces();
	}else if(furnace.equalsIgnoreCase("Coil:Heating:Gas")){
	    return new FuelFiredFurnaces();
	}
	return null;
    }
}
