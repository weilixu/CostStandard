package masterformat.standard.hvac.chiller;

import masterformat.api.ComponentFactory;

public class ChillerFactory extends ComponentFactory{
    
    @Override
    public Chiller getChiller(String chiller){
	if(chiller.equalsIgnoreCase("Absorption")){
	    return new AbsorptionChiller();
	}else if(chiller.equalsIgnoreCase("Centrifugal")){
	    return new CentrifugalChiller();
	}else if(chiller.equalsIgnoreCase("Reciprocating")){
	    return new ReciprocatingChiller();
	}else if(chiller.equalsIgnoreCase("Scroll")){
	    return new ScrollChiller();
	}else if(chiller.equalsIgnoreCase("Rotary Screw")){
	    
	}
	return null;
    }
}
