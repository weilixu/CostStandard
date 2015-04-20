package masterformat.standard.thermalmoistureprotection;

import masterformat.api.ComponentFactory;

public class ThermalMoistureProtectionFactory extends ComponentFactory{

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	if(thermalMoistureProtectionType.equalsIgnoreCase("THERMAL INSULATION")){
	    return new ThermalInsulation();
	}
	return null;
    }
}
