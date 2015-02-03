package Concrete;

import java.util.ArrayList;
import java.util.HashMap;

import masterformat.api.MasterFormat;

public interface Concrete extends MasterFormat{
    /**
     * input material properties that can be extract from energyplus,
     * the data elements includes 0. FloorArea; 1. height; 2. thickness; 3. conductivity; 4. density;
     * 5. specific heat; 6. Resistance
     */
    public void setVariable(String[] surfaceProperties);
    /**
     * Get the object's hierarchy in the masterformat.
     * @return String 
     */
    public String getHierarchy();
    
    /**
     * Get the product description
     * @return
     */
    public String getDescription();
    
    /**
     * after set the variables, this method should be called to set the costs
     */
    public void selectCostVector();
    /**
     * Check whether there is user inputs required for defining the cost of this class
     * @return
     */
    public boolean isUserInputsRequired();
    /**
     * Get the descriptions for the user inputs
     * @return
     */
    public ArrayList<String> getUserInputs();
    /**
     * Get the value of user inputs from the user
     * @param userInputsMap
     */
    public void setUserInputs(HashMap<String, String> userInputsMap);
    
}
