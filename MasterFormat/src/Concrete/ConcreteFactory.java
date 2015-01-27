package Concrete;

import thermalmoistureprotection.ThermalMoistureProtection;
import masonry.Masonry;
import masterformat.api.ComponentFactory;

public class ConcreteFactory extends ComponentFactory{

    @Override
    public Masonry getMasonry(String masonryType) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Concrete getConcrete(String concreteType) {
	if(concreteType.equalsIgnoreCase("CAST IN PLACE WALL")){
	    return new CastInPlaceWall();
	}else if(concreteType.equalsIgnoreCase("CAST IN PLACE SLAB ON GRADE")){
	    return new CastInPlaceSlabOnGrade();
	}
	return null;
    }

    @Override
    public ThermalMoistureProtection getThermalMoistureProtection(
	    String thermalMoistureProtectionType) {
	// TODO Auto-generated method stub
	return null;
    }

}
