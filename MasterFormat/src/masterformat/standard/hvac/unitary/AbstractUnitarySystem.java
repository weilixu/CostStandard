package masterformat.standard.hvac.unitary;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractUnitarySystem extends AbstractMasterFormatComponent implements UnitarySystem{
    
    protected final int coolingCapacityIndex = 0;
    protected final int heatingCapacityIndex = 1;
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    public AbstractUnitarySystem(){
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	unit = "Ea.";
	hierarchy = "238100 Decentralized Unitary HVAC Equipment";
	userInputs = new ArrayList<String>();
	initializeData();
    }

    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
