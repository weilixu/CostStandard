package thermalmoistureprotection;

import java.util.HashMap;
/**
 * This is an abstract class of all the materials relate to thermal adn moisture protection products.
 * The key values includes whether this material can be applied to vertical surfaces or horizontal surfaces or both.
 * <value>horizontal<value> and <value>vertical<value> indicates this properties.
 * unit is default to m2 and this class owns <value>firstkey<value> and <value>secondkey<value> to access the cost information
 * 
 * The Double[] in this class contains the R-Value information as well. This value will be added as the 6th element
 * Therefore, the first 5 are still material, labor, equipment, total and total with profit costs.
 * @author Weili
 *
 */
abstract class AbstractThermalMoistureProtection implements ThermalMoistureProtection{
    
    protected boolean horizontal = false;
    protected boolean vertical = false;
    protected String unit = "m2";
    protected String hierarchy = "070000 Thermal & Moisture Protection";
    protected HashMap<String, HashMap<String, Double[]>> priceData = new HashMap<String, HashMap<String, Double[]>>();
    protected String firstKey;
    protected String secondKey;
    
    public AbstractThermalMoistureProtection(){
	initializeData();
    }
    
    
    @Override
    public Double getMaterialPrice() {
	if(firstKey!=null && secondKey!=null){
	    return priceData.get(firstKey).get(secondKey)[0];
	}
	return 0.0;
    }

    @Override
    public Double getLaborPrice() {
	if(firstKey!=null && secondKey!=null){
	    return priceData.get(firstKey).get(secondKey)[1];
	}
	return 0.0;
    }

    @Override
    public Double getEquipmentPrice() {
	if(firstKey!=null && secondKey!=null){
	    return priceData.get(firstKey).get(secondKey)[2];
	}
	return 0.0;
    }

    @Override
    public Double getTotalPrice() {
	if(firstKey!=null && secondKey!=null){
	    return priceData.get(firstKey).get(secondKey)[3];
	}
	return 0.0;
    }

    @Override
    public Double getTotalInclOPPrice() {
	if(firstKey!=null && secondKey!=null){
	    return priceData.get(firstKey).get(secondKey)[4];
	}
	return 0.0;
    }

    @Override
    public String getUnit() {
	return unit;
    }

    @Override
    public String[] getFirstFilter() {
	if (priceData != null) {
	    return (String[]) priceData.keySet().toArray();
	}
	return null;
    }

    public void setFirstKey(String fk) {
	String[] keys = getFirstFilter();
	for (String s : keys) {
	    if (fk.equals(s)) {
		firstKey = fk;
	    }
	}
    }

    @Override
    public String[] getSecondFilter() {
	if (priceData != null) {
	    HashMap<String, Double[]> temp = priceData.get(priceData.keySet()
		    .iterator().next());
	    return (String[]) temp.keySet().toArray();
	}
	return null;
    }
    
    @Override
    public void setSecondKey(String sk) {
	String[] keys = getSecondFilter();
	for (String s : keys) {
	    if (sk.equals(s)) {
		secondKey = sk;
	    }
	}
    }

    @Override
    public String getHierarchy() {
	return hierarchy;
    }
    
    @Override
    public boolean isHorizontal() {
	return horizontal;
    }

    @Override
    public boolean isVertical() {
	// TODO Auto-generated method stub
	return vertical;
    }
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

    @Override
    abstract public double getInsulationRValue();
    
    

}
