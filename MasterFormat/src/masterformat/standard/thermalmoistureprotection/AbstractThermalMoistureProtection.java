package masterformat.standard.thermalmoistureprotection;

import java.util.ArrayList;
import java.util.HashMap;
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
abstract class AbstractThermalMoistureProtection implements ThermalMoistureProtection{
    
    private final int materialIndex = 0;
    private final int laborIndex = 1;
    private final int equipIndex = 2;
    private final int totalIndex = 3;
    private final int totalOPIndex = 4;
    
    protected final int floorAreaIndex = 0;
    protected final int heightIndex = 1;
    protected final int surfaceTypeIndex=2;
    protected final int thicknessIndex=3;
    protected final int conductivityIndex=4;
    protected final int densityIndex=5;
    protected final int specificHeatIndex=6;
    protected final int resistanceIndex=7;
    
    protected String unit = "m2";
    protected String hierarchy = "070000 Thermal & Moisture Protection";
    
    
    //price data structure to save the data
    protected HashMap<String, Double[]> priceData = new HashMap<String, Double[]>();
    //cost data after selected
    protected Double[] costVector = null;
    //recorde the inputs that required outside of mapping process
    protected ArrayList<String> userInputs;
    protected String description;
    
    
    protected ArrayList<String> optionLists;
    protected ArrayList<Integer> optionQuantities;
    
    
    public AbstractThermalMoistureProtection(){
	userInputs = new ArrayList<String>();
	initializeData();
    }
    
    
    @Override
    public Double getMaterialPrice() {
	return costVector[materialIndex];
    }

    @Override
    public Double getLaborPrice() {
	return costVector[laborIndex];
    }

    @Override
    public Double getEquipmentPrice() {
	return costVector[equipIndex];
    }

    @Override
    public Double getTotalPrice() {
	return costVector[totalIndex];
    }

    @Override
    public Double getTotalInclOPPrice() {
	return costVector[totalOPIndex];
    }
    
    @Override
    public Double[] getCostVector(){
	return costVector;
    }

    @Override
    public String getUnit() {
	return unit;
    }

    @Override
    public String getHierarchy() {
	return hierarchy;
    }
    
    @Override
    public String getDescription(){
	return description;
    }
    
    @Override
    public boolean isUserInputsRequired(){
	return !userInputs.isEmpty();
    }
    
    @Override
    public ArrayList<String> getUserInputs(){
	return userInputs;
    }
    
    @Override
    public ArrayList<String> getOptionListFromObjects(){
	return optionLists;
    }
    
    @Override
    public ArrayList<Integer> getQuantitiesFromObjects(){
	return optionQuantities;
    }
    
    @Override
    abstract public void selectCostVector();
  
    @Override
    abstract public void setUserInputs(HashMap<String, String> userInputsMap);
    
    @Override
    abstract public void setVariable(String[] surfaceProperties);
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();
    
}
