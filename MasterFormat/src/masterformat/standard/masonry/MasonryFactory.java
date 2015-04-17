package masterformat.standard.masonry;

import masterformat.api.ComponentFactory;

public class MasonryFactory extends ComponentFactory{
    

    @Override
    public Masonry getMasonry(String masonryType) throws Exception {
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
}
