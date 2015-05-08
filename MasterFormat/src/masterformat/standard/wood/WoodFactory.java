package masterformat.standard.wood;

import masterformat.api.ComponentFactory;

public class WoodFactory extends ComponentFactory{
    
    @Override
    public Wood getWood(String wood){
	if(wood.equalsIgnoreCase("Wood Panel Product Sheathing")){
	    return new WoodPanelSheathing();
	}
	return null;
    }
}
