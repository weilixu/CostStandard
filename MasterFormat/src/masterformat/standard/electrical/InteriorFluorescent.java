package masterformat.standard.electrical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class InteriorFluorescent extends AbstractElectrical {
    private Double power;
    //mount include ceiling mount and surface mount
    private String mount;

    private Double[] powerList = { 80.0, 120.0, 80.0, 80.0, 120.0, 160.0,
	    160.0, 240.0, 320.0, 64.0, 64.0, 64.0, 96.0, 128.0, 80.0, 120.0,
	    80.0, 80.0, 120.0, 160.0, 160.0, 240.0, 320.0, 160.0, 320.0 };
    
    private final Integer ceilingMountLower=0;
    private final Integer ceilingMountHigher=13;
    private final Integer surfaceMountLower=14;
    private final Integer surfaceMountHigher=24;

    public InteriorFluorescent() {
	unit = "$/Ea";
	hierarchy = "260000 Electrical:265100 Interior Lighting:265113 Interior Lighting Fixtures, Lamps, and Ballasts:265113.500100 Fluorescent";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Mount")){
		//only "Surface" and "Recessed"
		mount = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] properties) {
	try {
	    power = Double.parseDouble(properties[electricPowerIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = { { 47.50, 77.0, 0.0, 124.50, 167.0 },
		{ 54.0, 81.0, 0.0, 135.0, 180.0 },
		{ 51.0, 77.0, 0.0, 128.0, 172.0 },
		{ 50.0, 82.5, 0.0, 132.50, 179.0 },
		{ 55.0, 87.50, 0.0, 142.50, 192.0 },
		{ 57.5, 93.0, 0.0, 150.5, 203.0 },
		{ 293.0, 137.0, 0.0, 430.0, 525.0 },
		{ 305.0, 141.0, 0.0, 446.0, 545.0 },
		{ 315.0, 151.0, 0.0, 466.0, 570.0 },
		{ 59.50, 77.0, 0.0, 136.50, 181.1 },
		{ 81.0, 77.0, 0.0, 158.0, 204.0 },
		{ 67.0, 82.50, 0.0, 149.50, 198.0 },
		{ 68.0, 87.50, 0.0, 155.50, 206.0 },
		{ 71.0, 93.0, 0.0, 164.0, 217.0 },
		{ 64.0, 62.50, 0.0, 126.50, 164.0 },
		{ 66.0, 65.50, 0.0, 131.50, 171.0 },
		{ 68.50, 62.50, 0.0, 131.0, 169.0 },
		{ 78.0, 70.50, 0.0, 148.50, 192.0 },
		{ 79.0, 77.0, 0.0, 156.0, 202.0 },
		{ 81.0, 82.50, 0.0, 163.50, 213.0 },
		{ 400.0, 122.0, 0.0, 522.0, 620.0 },
		{ 435.0, 133.0, 0.0, 568.0, 675.0 },
		{ 450.0, 141.0, 0.0, 591.0, 705.0 },
		{ 159.0, 137.0, 0.0, 296.0, 705.0 },
		{ 159.0, 137.0, 0.0, 296.0, 380.0 },
		{ 171.0, 141.0, 0.0, 312.0, 400.0 }, };
	

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	
	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, three 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");
//	optionLists.add("Fluorescent, C.W. lamps, troffer, recess mounted in grid, RS Gride ceiling mount, Acrylic lens, 0.3m W x 1.2m L, two 40 watt");

    }
    
    @Override
    public void selectCostVector() {
	
    }

}
