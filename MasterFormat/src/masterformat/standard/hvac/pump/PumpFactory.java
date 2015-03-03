package masterformat.standard.hvac.pump;

import masterformat.api.ComponentFactory;

public class PumpFactory extends ComponentFactory{
    
    @Override
    public Pump getPump(String pump) {
	if(pump.equalsIgnoreCase("IN-LINE CENTRIFUGAL HYDRONIC PUMPS")){
	    return new CentrifugalPumps();
	}
	return null;
    }
}
