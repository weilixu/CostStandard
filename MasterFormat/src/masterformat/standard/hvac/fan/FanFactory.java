package masterformat.standard.hvac.fan;

import masterformat.api.ComponentFactory;


public class FanFactory extends ComponentFactory{

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
}
