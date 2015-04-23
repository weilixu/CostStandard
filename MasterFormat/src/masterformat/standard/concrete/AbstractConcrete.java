package masterformat.standard.concrete;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

abstract class AbstractConcrete extends AbstractMasterFormatComponent implements Concrete{
    
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex=2;
    protected final int thicknessIndex=3;
    protected final int conductivityIndex=4;
    protected final int densityIndex=5;
    protected final int specificHeatIndex=6;
    protected final int resistanceIndex=7;
    
    public AbstractConcrete(){
	userInputs = new ArrayList<String>();
	descriptionList = new ArrayList<String>();
	unit = "m2";
	hierarchy = "030000 Concrete";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
}
