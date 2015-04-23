package masterformat.standard.hvac.pump;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractPump extends AbstractMasterFormatComponent implements Pump{
    
    protected final int powerIndex = 0;
    protected final int pumpHeadIndex=1;
    protected final int pumpFlowRateIndex=2;
    
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    public AbstractPump(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();	
	unit = "Ea.";
	hierarchy = "232100 Hydronic Piping and Pumps";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
