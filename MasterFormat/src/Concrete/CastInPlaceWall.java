package Concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CastInPlaceWall extends AbstractConcrete {

    protected String unit = "m3";
    protected String hierarchy = "030000 Concrete|033000 Cast-In-Place Concrete|033053.40 Concrete In Place";

    private double thickness;
    private double height;

    /**
     * For cast in place wall, the required attributes are thickness and height
     * of the concrete
     * 
     * @param t
     * @param h
     */
    @Override
    public void setVariable(String[] surfaceProperties) {
	try {
	    height = Double.parseDouble(surfaceProperties[1]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT|Height|m");
	}
	try {
	    thickness = Double.parseDouble(surfaceProperties[2]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT|Thickness|m");
	}
    }
    
    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap){
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Height")){
		height = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("Thickness")){
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }
    
    @Override
    public void selectCostVector() {
	if (thickness <= 0.3) {
	    if (height <= 2.4) {
		description = "Wall, free-standing (20 mPa), 200 mm thick, 2400 mm high";

	    } else {
		description = "Wall, free-standing (20 mPa), 200 mm thick, 4200 mm high";
	    }
	} else if (thickness <= 0.38) {
	    if (height <= 2.4) {
		description = "Wall, free-standing (20 mPa), 300 mm thick, 2400 mm high";
	    } else {
		description = "Wall, free-standing (20 mPa), 300 mm thick, 4200 mm high";
	    }
	} else {
	    if (height <= 2.4) {
		description = "Wall, free-standing (20 mPa), 380 mm thick, 2400 mm high";
	    } else if (height <= 3.6) {
		description = "Wall, free-standing (20 mPa), 380 mm thick, 3600 mm high";
	    } else {
		description = "Wall, free-standing (20 mPa), 380 mm thick, 5500 mm high";
	    }
	}
	costVector = priceData.get(description);
    }

    @Override
    protected void initializeData() {
	// the data is in IP unit, conversion factor from C.Y to m3 is 1yd =
	// 0.76455 m3
	Double[][] costsMatrix = { { 160.00, 204.44, 16.40, 380.40, 510.00 },
		{ 190.00, 345.00, 27.50, 562.50, 770.00 },
		{ 145.00, 146.00, 11.70, 302.70, 395.00 },
		{ 154.00, 234.00, 18.80, 406.80, 550.00 },
		{ 139.00, 117.00, 9.40, 265.40, 345.00 },
		{ 139.00, 183.00, 14.65, 336.65, 450.00 },
		{ 157.00, 192.00, 15.40, 364.00, 485.00 } };

	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Wall, free-standing (20 mPa), 200 mm thick, 2400 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 200 mm thick, 4200 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 300 mm thick, 2400 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 300 mm thick, 4200 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 380 mm thick, 2400 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 380 mm thick, 3600 mm high");
	typesOne.add("Wall, free-standing (20 mPa), 380 mm thick, 5500 mm high");

	for (int i = 0; i < typesOne.size(); i++) {
	    priceData.put(typesOne.get(i), unitConversion(costsMatrix[i]));
	}
    }

    private Double[] unitConversion(Double[] data) {
	// the data is in IP unit, conversion factor from C.Y to m3 is 1yd =
	// 0.76455 m3
	Double[] temp = new Double[data.length];
	for (int i = 0; i < temp.length; i++) {
	    temp[i] = data[i] / 0.76455;
	}
	return temp;
    }

}
