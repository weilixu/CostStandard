package eplus.construction;

import java.util.HashMap;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import masterformat.api.MasterFormat;

public interface BuildingComponent extends MasterFormat{
    
    public String getName();
    
    /**
     * provide the available building component list for
     * user to select
     * @return
     */
    public String[] getListAvailableComponent();
    
    /**
     * set a range of components to overwrite energyplus components
     * @param componentList
     */
    public void setRangeOfComponent(String[] componentList);
    
    /**
     * get the selected component list
     * @return
     */
    public String[] getSelectedComponents();
    
    
    public String getSelectedComponentName(int Index);
    
    /**
     * write the component into a copy of energyplus file.
     * The energyPlus file is a copied file sent from main controller
     * component is the component selected for a specific simulation
     * @param eplusFile
     * @param component
     */
    public void writeInEnergyPlus(IdfReader reader, String component);
    
    /**
     * this method is used for those components whose costs are determined
     * from simulation results. Such as HVAC.
     * If this is not applicable for components whose costs can be determined
     * by quantity, then it will return 0.0
     * @return
     */
    public double getComponentCost(Document doc);
    
    /**
     * Test if the component type is integer or numerical
     * @return
     */
    public boolean isIntegerTypeComponent();
    
    /**
     * Retrieve the number of variables inside this component (Currently only used for numerical variables)
     * @return
     */
    public int getNumberOfVariables();
    
    /**
     * this method is designed for component that has numerical types.
     * @param shelfProperty
     * @param component
     */
    public void readsInProperty(HashMap<String, Double> shelfProperty, String component);
}
