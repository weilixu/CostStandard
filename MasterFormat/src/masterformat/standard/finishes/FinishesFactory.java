package masterformat.standard.finishes;

import masterformat.api.ComponentFactory;

public class FinishesFactory extends ComponentFactory{
    
    @Override
    public Finishes getFinishes(String finish) {
	if(finish.equalsIgnoreCase("CARPETING")){
	    return new Carpeting();
	}
	return null;
    }

}
