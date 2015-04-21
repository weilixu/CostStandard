package masterformat.standard.hvac.boiler;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractBoiler extends AbstractMasterFormatComponent implements Boiler{
    
    protected final int sourceTypeIndex = 0;
    protected final int capacityIndex = 1;
    protected final int efficiencyIndex=2;
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    public AbstractBoiler(){
	userInputs = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "235200 Heating Boiler";
	initializeData();
    }

    abstract protected void initializeData();

}
