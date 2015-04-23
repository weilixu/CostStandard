package masterformat.standard.hvac.convectionunits;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractConvectionUnits extends AbstractMasterFormatComponent implements ConvectionUnits{
    
    
    protected final int coolingCapacityIndex = 0;
    protected final int heatingCapacityIndex = 1;
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    public AbstractConvectionUnits(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "238200 Convection Heating and Cooling Units";
	initializeData();
    }

    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
}
