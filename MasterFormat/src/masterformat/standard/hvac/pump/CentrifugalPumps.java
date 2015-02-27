package masterformat.standard.hvac.pump;

import java.util.ArrayList;
import java.util.HashMap;

public class CentrifugalPumps extends AbstractPump{
    /**
     * types: bronze sweat connection, flange connection, cast iron flange connection, pumps circulating, varied by size";
     */
    private String pumpType;
    private boolean nonferrous;
    private Double power;
    
    private final Double[] powerList = {18.6,62.1,93.2,248.6,124.3,186.4,62.2,248.6,248.6,124.3,186.4,186.4};
    
    public CentrifugalPumps(){
	unit = "$/Ea.";
	hierarchy = "232100 Hydronic Piping and Pump:232123 Hydronic Pumps:232123.13 In-Line Centrifugal Hydronic Pumps";
    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void setVariable(String[] properties) {
	try{
	    power = Double.parseDouble(properties[powerIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Power:Watt");
	}	
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = {{218.0,53.0,0.0,271.0,320.0},
		{565.0,141.0,0.0,706.0,835.0},
		{970.0,141.0,0.0,1111.0,1300.0},
		{1075.0,141.0,0.0,1216.0,1425.0},
		{1400.0,169.0,0.0,1569.0,1775.0},
		{1775.0,169.0,0.0,1944.0,2200.0},
		{365.0,141.0,0.0,506.0,615.0},
		{680.0,141.0,0.0,821.0,960.0},
		{790.0,141.0,0.0,931.0,1075.0},
		{745.0,169.0,0.0,914.0,1075.0},
		{960.0,169.0,0.0,1129.0,1300.0},
		{975.0,169.0,0.0,1186.0,1400.0}
	};
	
	powerList = {18.6,62.1,93.2,248.6,124.3,186.4,62.2,248.6,248.6,124.3,186.4,186.4};
	
	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists.add("Bronze, sweat connections, 18.6Watts, in line, 0.02m size");
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 62.1Watts");
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 93.2Watts");
	optionLists.add("Bronze, flange connection, 0.02m-0.04m size, 248.6Watts");
	optionLists.add("Bronze, flange connection, 0.05m size, 124.3Watts");
	optionLists.add("Bronze, flange connection, 0.06m size, 186.4Watts");
	optionLists.add("Cast iron, flange connection, 0.02m-0.04m size, 62.2Watts");
	optionLists.add("Cast iron, flange connection, 0.02m-0.04m size, 248.6Watts");
	optionLists.add("Pumps, circulating, 0.02m-0.04m size, 248.6Watts");
	optionLists.add("Pumps, circulating, 0.05m size, 124.3Watts");
	optionLists.add("Pumps, circulating, 0.06m size, 186.4Watts");
	optionLists.add("Pumps, circulating, 0.08m size, 186.4Watts");
    }
    

}
