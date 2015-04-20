package masterformat.standard.thermalmoistureprotection;

import java.util.ArrayList;
import java.util.HashMap;

import masterformat.api.AbstractMasterFormatComponent;
/**
 * This is an abstract class of all the materials relate to thermal adn moisture protection products.
 * The key values includes whether this material can be applied to vertical surfaces or horizontal surfaces or both.
 * <value>horizontal<value> and <value>vertical<value> indicates this properties.
 * unit is default to m2 and this class owns <value>firstkey<value> and <value>secondkey<value> to access the cost information
 * 
 * The Double[] in this class contains the R-Value information as well. This value will be added as the 6th element
 * Therefore, the first 5 are still material, labor, equipment, total and total with profit costs.
 * 
 * 
 * @author Weili
 *
 */
abstract class AbstractThermalMoistureProtection extends AbstractMasterFormatComponent implements ThermalMoistureProtection{
    
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex=2;
    protected final int thicknessIndex=3;
    protected final int conductivityIndex=4;
    protected final int densityIndex=5;
    protected final int specificHeatIndex=6;
    protected final int resistanceIndex=7;
    
    
    public AbstractThermalMoistureProtection(){
	userInputs = new ArrayList<String>();
	unit = "m2";
	hierarchy = "070000 Thermal & Moisture Protection";
	initializeData();
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
    
}
