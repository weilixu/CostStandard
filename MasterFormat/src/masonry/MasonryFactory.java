package masonry;

import Concrete.Concrete;
import masterformat.api.ComponentFactory;

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
    
    

}
