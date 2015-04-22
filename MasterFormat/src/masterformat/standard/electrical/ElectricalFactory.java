package masterformat.standard.electrical;

import masterformat.api.ComponentFactory;

public class ElectricalFactory extends ComponentFactory{
    
    @Override
    public Electrical getElectrical(String electrical){
	if(electrical.equalsIgnoreCase("Interior Lighting Fixture")){
	    return new InteriorLighting();
	}
	return null;
    }

}
