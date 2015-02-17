package masterformat.standard.thermalmoistureprotection;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.masonry.Masonry;

public class ThermalMoistureProtectionFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	//this factory would not produce any masonry products
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	//this factory would not produce any concrete products
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	if(thermalMoistureProtectionType.equalsIgnoreCase("RIGID INSULATION")){
	    return new RigidInsulation();
	}
	return null;
    }
}
