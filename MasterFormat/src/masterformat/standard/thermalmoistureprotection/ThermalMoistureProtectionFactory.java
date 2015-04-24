package masterformat.standard.thermalmoistureprotection;

import masterformat.api.ComponentFactory;

public class ThermalMoistureProtectionFactory extends ComponentFactory{

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	if(thermalMoistureProtectionType.equalsIgnoreCase("THERMAL INSULATION")){
	    return new ThermalInsulation();
	}else if(thermalMoistureProtectionType.equalsIgnoreCase("CLAY TILES")){
	    return new ClayRoofTile();
	}else if(thermalMoistureProtectionType.equalsIgnoreCase("ASPHALT SHINGLES")){
	    return new AsphaltShingles();
	}
	return null;
    }
}
