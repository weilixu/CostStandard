package eplus.construction;

import eplus.IdfReader;
import masterformat.api.MasterFormat;

public interface BuildingComponent extends MasterFormat{
    
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
    public void writeInEnergyPlus(IdfReader eplusFile, String component);
    
}
