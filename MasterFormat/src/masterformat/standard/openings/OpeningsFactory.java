package masterformat.standard.openings;

import masterformat.api.ComponentFactory;

public class OpeningsFactory extends ComponentFactory{
    
    @Override
    public Openings getOpenings(String openings){
	if(openings.equalsIgnoreCase("Aluminum Windows")){
	    return new AluminumWindows();
	}else if(openings.equalsIgnoreCase("Plain Wood Windows")){
	    return new WoodWindows();
	}else if(openings.equalsIgnoreCase("Vinyl Windows")){
	    return new PlasticWindows();
	}
	return  null;
    }
}
