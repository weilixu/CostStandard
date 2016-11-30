package masterformat.standard.hvac.chiller;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractChiller extends AbstractMasterFormatComponent implements Chiller{
    
    public AbstractChiller(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "236410 HVAC Chillers";
	initializeData();
	
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
