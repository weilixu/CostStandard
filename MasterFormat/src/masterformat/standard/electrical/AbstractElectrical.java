package masterformat.standard.electrical;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractElectrical extends AbstractMasterFormatComponent implements Electrical{
    
    protected final int electricPowerIndex = 0;
    protected final int capacitorIndex = 1;
    protected final int voltageIndex = 2;
    protected final int ampsIndex = 3;
    protected static final Double[] Default_Cost_Vector = {0.0,0.0,0.0,0.0,0.0};
    
    public AbstractElectrical(){
	optionQuantities = new ArrayList<Integer>();
	optionLists = new ArrayList<String>();
	userInputs = new ArrayList<String>();
	descriptionList = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "260000 Electrical";
	initializeData();
    }

    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
}
