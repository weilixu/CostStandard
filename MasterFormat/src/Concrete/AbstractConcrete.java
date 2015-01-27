package Concrete;

import java.util.HashMap;

abstract class AbstractConcrete implements Concrete{
    
    protected String unit = "SF";
    protected String hierarchy = "030000 Concrete";
    protected HashMap<String, HashMap<String, Double[]>> priceData = new HashMap<String, HashMap<String, Double[]>>();
    protected String firstKey;
    protected String secondKey;

    public AbstractConcrete(){
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
    
    /**
     * This method is used to initialize the product's cost data
     */
    abstract protected void initializeData();

}
