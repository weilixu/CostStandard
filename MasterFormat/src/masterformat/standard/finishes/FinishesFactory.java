package masterformat.standard.finishes;

import masterformat.api.ComponentFactory;

public class FinishesFactory extends ComponentFactory{
    
    @Override
    public Finishes getFinishes(String finish) {
	if(finish.equalsIgnoreCase("CARPETING")){
	    return new Carpeting();
	}else if(finish.equalsIgnoreCase("GYPSUM BOARD ASSEMBLIES")){
	    return new Gypsumboard();
	}else if(finish.equalsIgnoreCase("Wood Flooring")){
	    return new WoodFlooring();
	}
	return null;
    }

}
