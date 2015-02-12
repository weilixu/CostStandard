package masterformat.squarefoot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractBuildingTypes {
    protected HashMap<String, Double[]> distParams;
    // convert from square feet to square meter
    protected final double conversionFactor = 0.092903;
    // default typical size
    protected double typicalSize = 1000.00;

    /*
     * typical divisions appears in the square foot estimation method
     */
    protected final String TOTAL = "Total project cost";
    protected final String SITE = "Site work";
    protected final String MASONRY = "Mansory";
    protected final String ROOF = "Roofing";
    protected final String PAINT = "Painting";
    protected final String FINISH = "Finishes";
    protected final String EQUIP = "Equipment";
    protected final String CEQUIP = "Conveying Equipment";
    protected final String PLUMB = "Plumbing";
    protected final String HVAC = "Heating, ventilationg, air conditioning";
    protected final String ELEC = "Electrical";

    public AbstractBuildingTypes() {
	distParams = new HashMap<String, Double[]>();
	initializeData();
    }

    protected abstract void initializeData();

    public HashMap<String, Double[]> getDistParams() {
	return distParams;
    }

    protected void unitConversion() {
	Set<String> keys = distParams.keySet();
	Iterator<String> iterator = keys.iterator();
	while (iterator.hasNext()) {
	    Double[] params = distParams.get(iterator.next());
	    for (int i = 0; i < params.length; i++) {
		params[i] = params[i] / conversionFactor;
	    }
	}
    }

    /**
     * get the cost multiplier. A typical size is defined for each type of
     * buildings. The multiplier applies to the building that has different size
     * than the typical size
     * 
     * @param size
     * @return
     */
    public double getCostMultiplier(double size) {
	// 1.0 is default size factor when the size of building is exactly
	// same as the typical estimation range
	double sizeFactor = 1.0;
	double ratio = size / typicalSize;
	if (ratio > 1 && ratio < 3.5) {
	    sizeFactor = higherRegression(ratio);
	} else if (ratio < 1 && ratio > 0.5) {
	    sizeFactor = lowerRegression(ratio);
	} else if (ratio <= 0.5) {
	    sizeFactor = 1.1;
	} else if (ratio >= 3.5) {
	    sizeFactor = 0.90;
	}
	return sizeFactor;
    }

    /**
     * The lower regression part is derived from the graph on Page.870 RSMeans
     * 2015
     * 
     * @param ratio
     * @return
     */
    private double lowerRegression(double ratio) {
	return 1.331387 + (-0.60706) * ratio + 0.291375 * Math.pow(ratio, 2);
    }

    /**
     * The higher regression part is derived from the graph on Page.870 RSMeans
     * 2015
     * 
     * @param ratio
     * @return
     */
    private double higherRegression(double ratio) {
	System.out.println(ratio);
	return 1.0785 + (-0.09189) * ratio + 0.011786 * Math.pow(ratio, 2);
    }
}
