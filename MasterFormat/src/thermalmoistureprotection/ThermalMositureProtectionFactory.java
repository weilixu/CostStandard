package thermalmoistureprotection;

import Concrete.Concrete;
import masonry.Masonry;
import masterformat.api.ComponentFactory;

public class ThermalMositureProtectionFactory extends ComponentFactory{

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
	    System.out.println("got here");
	    return new RigidInsulation();
	}
	return null;
    }

}
