package Concrete;

import masterformat.api.MasterFormat;

public interface Concrete extends MasterFormat{
    /**
     * Get the first level of filters. the return value includes all the
     * descriptions of the first level filters The selection of the filter at
     * this level will affect the second level filters.
     * 
     * @return String[]
     */
    public String[] getFirstFilter();

    /**
     * Set the first key to the data structure. It used when user types in the
     * key value according to the first level filter values
     * 
     * @param fk
     */
    public void setFirstKey(String fk);

    /**
     * Get the second level of filters.
     * 
     * @return String []
     */
    public String[] getSecondFilter();

    /**
     * set the second level of filter. After this filter is being set, the
     * object should be able to extract the correspondent cost for the component
     * 
     * @param sk
     */
    public void setSecondKey(String sk);
    
    /**
     * Get the object's hierarchy in the masterformat.
     * @return String 
     */
    public String getHierarchy();
    

}
