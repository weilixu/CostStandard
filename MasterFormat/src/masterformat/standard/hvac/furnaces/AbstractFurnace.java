package masterformat.standard.hvac.furnaces;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractFurnace extends AbstractMasterFormatComponent implements Furnace{
    
    protected final int powerIndex = 0;
    protected final int efficiencyIndex=1;
    
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
   
    
    public AbstractFurnace(){
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	unit = "Ea.";
	hierarchy = "235400 Furnaces";
	userInputs = new ArrayList<String>();
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
