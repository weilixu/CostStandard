package masterformat.standard.openings;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractOpenings extends AbstractMasterFormatComponent implements Openings{
    
    // the standard data format for mapping Energyplus model and masterformat
    protected final int glazingSizeIndex = 0;
    protected final int numLayerIndex = 1;
    protected final int thicknessIndex = 2;
    protected final int uvalueIndex = 3;
    protected final int shgcIndex = 4;
    protected final int vtIndex = 5;
    protected final int blindIndex = 6;
    protected final int screenIndex = 7;
    
    public AbstractOpenings(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	unit = "m2";
	hierarchy = "080000 Openings";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
    

}
