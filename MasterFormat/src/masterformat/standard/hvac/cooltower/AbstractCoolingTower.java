package masterformat.standard.hvac.cooltower;


import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractCoolingTower extends AbstractMasterFormatComponent implements CoolingTower {
    
    public AbstractCoolingTower(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "236513 Forced Draft Type Cooling Towers";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
