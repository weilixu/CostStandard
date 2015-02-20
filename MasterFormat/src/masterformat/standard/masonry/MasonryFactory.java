package masterformat.standard.masonry;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.hvac.boiler.Boiler;
import masterformat.standard.hvac.fan.Fan;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;

public class MasonryFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	if(masonryType==null){
	    return null;
	}
	
	if(masonryType.equalsIgnoreCase("BRICK VENEER MASONRY")){
		return new BrickVennerMasonry();
	}
	
	if(masonryType.equalsIgnoreCase("THIN BRICK VENEER")){
	    return new ThinBrickVeneer();
	}
	
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	//this factory would not produce concrete
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	//this factory would not produce thermal and moisture protection
	return null;
    }

    @Override
    public Boiler getBoiler(String hvac) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Fan getFan(String fan) {
	// TODO Auto-generated method stub
	return null;
    }
}
