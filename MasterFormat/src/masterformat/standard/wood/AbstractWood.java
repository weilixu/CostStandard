package masterformat.standard.wood;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

public abstract class AbstractWood extends AbstractMasterFormatComponent implements Wood{
    
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex=2;
    protected final int thicknessIndex=3;
    protected final int conductivityIndex=4;
    protected final int densityIndex=5;
    protected final int specificHeatIndex=6;
    protected final int resistanceIndex=7;
    
    public AbstractWood(){
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	descriptionList = new ArrayList<String>();
	userInputs = new ArrayList<String>();
	unit = "m2";
	hierarchy = "060000 Wood";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
