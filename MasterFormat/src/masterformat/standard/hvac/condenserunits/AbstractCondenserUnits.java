package masterformat.standard.hvac.condenserunits;

import java.util.ArrayList;

import masterformat.api.AbstractMasterFormatComponent;

/**
 * condenser unit needs to map following items to MasterFormat:
 * String[] = {MaximumCapacity, COP, ratedAirFlowRate}
 * @author Weili
 *
 */
public abstract class AbstractCondenserUnits extends AbstractMasterFormatComponent implements CondenserUnits{
    
    protected final int capacityIndex = 0;
    protected final int copIndex = 1;
    protected final int airFlowIndex=2;
    
    public AbstractCondenserUnits(){
	userInputs = new ArrayList<String>();
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	unit = "Ea.";
	hierarchy = "236200 Packaged Compressor and Condenser Units";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
