package masonry;

import java.util.ArrayList;
import java.util.HashMap;

abstract class AbstractMasonry implements Masonry{
    
    private final int materialIndex = 0;
    private final int laborIndex = 1;
    private final int equipIndex = 2;
    private final int totalIndex = 3;
    private final int totalOPIndex = 4;
    
    protected String unit = "m2";
    protected String hierarchy = "040000 Masonry";
    
    //price data structure to save the data
    protected HashMap<String, Double[]> priceData = new HashMap<String, Double[]>();
    //cost data after selected
    protected Double[] costVector;
    //recorde the inputs that required outside of mapping process
    protected ArrayList<String> userInputs;
    protected String description;
    
    public AbstractMasonry(){
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
