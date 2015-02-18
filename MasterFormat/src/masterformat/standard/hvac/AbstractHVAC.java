package masterformat.standard.hvac;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractHVAC implements hvac{
    //get the material price for the product
    public Double getMaterialPrice(){
	return null;
	
    }
    
    //get labor price for the product
    public Double getLaborPrice(){
	return null;
	
    }
    
    //get equipment price for the product
    public Double getEquipmentPrice(){
	return null;
	
    }
    
    //get the total price for the product
    public Double getTotalPrice(){
	return null;
	
    }
    
    //get the total price include the operation price for the product
    public Double getTotalInclOPPrice(){
	return null;
	
    }
    
    //get the standard unit for this product
    public String getUnit(){
	return null;
	
    }
    
    public Double[] getCostVector(){
	return null;
	
    }
    
    /**
     * input material properties that can be extract from energyplus,
     * the data elements includes 0. FloorArea; 1. height; 2. thickness; 3. conductivity; 4. density;
     * 5. specific heat; 6. Resistance
     */
    public void setVariable(String[] surfaceProperties){
	
    }
    /**
     * Get the object's hierarchy in the masterformat.
     * @return String 
     */
    public String getHierarchy(){
	return null;
	
    }
    
    /**
     * Get the product description
     * @return
     */
    public String getDescription(){
	return null;
	
    }
    
    /**
     * after set the variables, this method should be called to set the costs
     */
    public void selectCostVector(){
	
    }
    /**
     * Check whether there is user inputs required for defining the cost of this class
     * @return
     */
    public boolean isUserInputsRequired(){
	return false;
	
    }
    /**
     * Get the descriptions for the user inputs
     * @return
     */
    public ArrayList<String> getUserInputs(){
	return null;
	
    }
    /**
     * Get the value of user inputs from the user
     * @param userInputsMap
     */
    public void setUserInputs(HashMap<String, String> userInputsMap){
	
    }
    

}
