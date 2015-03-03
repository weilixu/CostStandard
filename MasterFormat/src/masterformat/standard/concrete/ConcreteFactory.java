package masterformat.standard.concrete;

import masterformat.api.ComponentFactory;

public class ConcreteFactory extends ComponentFactory{

    @Override
    public Concrete getConcrete(String concreteType) {
	
	if(concreteType.equalsIgnoreCase("CAST IN PLACE WALL")){
	    return new CastInPlaceWall();
	}else if(concreteType.equalsIgnoreCase("CAST IN PLACE SLAB ON GRADE")){
	    return new CastInPlaceSlabOnGrade();
	}
	return null;
    }
}
