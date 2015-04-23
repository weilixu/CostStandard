package masterformat.standard.hvac.fan;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractFan extends AbstractMasterFormatComponent implements Fan{

    protected final int flowRateIndex = 0;
    protected final int staticPressureIndex = 1;
    protected final int powerIndex=2;
    protected final int diameterIndex=3;
    
    public AbstractFan(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	unit = "Ea.";
	hierarchy = "233400 HVAC Fans";
	initializeData();
    }

    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
