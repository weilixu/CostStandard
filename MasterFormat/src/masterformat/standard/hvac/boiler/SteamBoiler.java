package masterformat.standard.hvac.boiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SteamBoiler extends AbstractBoiler{
    
    private String sourceType;
    private Double power;
    
    public SteamBoiler(){
	unit = "$/Ea";
	hierarchy = "235200 Heating Boilers";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Source")){
		sourceType = userInputsMap.get(temp);
	    }else if(temp.equals("Power")){
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	if(sourceType!=""){
		sourceType = surfaceProperties[sourceTypeIndex];	    
	}else{
	    userInputs.add("INPUT:Source: ");
	}
	
	try{
	    power = Double.parseDouble(surfaceProperties[capacityIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = {{3950.0,1075.0,0.0,5025.0,5975.0},
		{6650.0,1300.0,0.0,7950.0,9275.0},
		{9375.0,1725.0,0.0,11100.0,12900.0},
		{23800.0,2350.0,0.0,26150.0,29800.0},
		{32600.0,4925.0,0.0,37525.0,43300.0},
		{40600.0,7100.0,0.0,47700.0,55500.0},
		{86500.0,11100.0,0.0,97600.0,111500.0},
		{2450.0,1300.0,0.0,3750.0,4675.0},
		{3825.0,2025.0,0.0,5850.0,7250.0},
		{5900.0,3225.0,0.0,9125.0,11400.0},
		{12200.0,4225.0,0.0,16425.0,19800.0},
		{25200.0,6075.0,0.0,31275.0,36900.0},
		{68000.0,11800.0,0.0,79800.0,93000.0},
		{88000.0,14000.0,0.0,102000.0,118000.0},
		{98500.0,18200.0,0.0,116700.0,136000.0},
		{14700.0,4225.0,0.0,18925.0,22600.0},
		{20900.0,6100.0,0.0,27000.0,32200.0},
		{28100.0,9425.0,0.0,37525.0,45100.0},
		{90500.0,13400.0,0.0,103900.0,119500.0},
		{97000.0,16900.0,0.0,113900.0,132000.0},
		{102500.0,21200.0,0.0,123700.0,145000.0},
		{2325.00,1525.0,0.0,3850.0,4850.0},
		{3175.0,2025.0,0.0,5200.0,6550.0},
		{10900.0,4850.0,0.0,15750.0,19300.0},
		{23700.0,9675.0,0.0,33375.0,40700.0},
		{78500.0,13400.0,0.0,91900.0,106500.0},
		{101000.0,20700.0,0.0,121700.0,142000.0}
	};
	
	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 6kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 60kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 112kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 222kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 518kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 814kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Steam, 2340kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 24 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 60 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 117 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 224 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 550 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1383 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1788 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 2042 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 211 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 469 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 791 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1618 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1873 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 2043 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 32 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 61 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 318 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 880 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 1618 kW");
	typesOne.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 2043 kW");

	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), costsMatrix[i]);
	}
    }
    
    @Override
    public void selectCostVector() {
	
	if(sourceType.equals("Electricity")){
	    if(power<=6000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 6kW";
	    }else if(power>6000 && power<=60000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 60kW";
	    }else if(power>60000 && power <=112000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 112kW";
	    }else if(power>112000 && power<=222000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 222kW";
	    }else if(power>222000 && power<=518000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 518kW";
	    }else if(power>518000 && power<=814000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 814kW";
	    }else if(power>814000){
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, 2340kW";
	    }
	}else if(sourceType.equals("NaturalGas")||sourceType.equals("PropaneGas")){
	    if(power<=24000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 24 kW";
	    }else if(power>24000 && power<=60000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 60 kW";
	    }else if(power>60000 && power<=117000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 117 kW";
	    }else if(power>117000 && power<=224000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 224 kW";
	    }else if(power>224000 && power<=550000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 550 kW";
	    }else if(power>550000 && power<=1383000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1383 kW";
	    }else if(power>1383000 && power<=1788000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1788 kW";
	    }else if(power>1788000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 2042 kW";
	    }	    
	}else if(sourceType.equals("Gasoline")){
	    if(power<=211000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 211 kW";
	    }else if(power>211000 && power<=469000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 469 kW";
	    }else if(power>469000 && power<=791000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 791 kW";
	    }else if(power>791000 && power<=1618000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1618 kW";
	    }else if(power>1618000 && power<=1873000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1873 kW";
	    }else if(power>1873000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 2043 kW";
	    }
	}else if(sourceType.equals("Diesel")){
	    if(power<=32000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 32 kW";
	    }else if(power>32000 && power<=61000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 61 kW";
	    }else if(power>61000 && power<=318000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 318 kW";
	    }else if(power>318000 && power<=880000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 880 kW";
	    }else if(power>880000 && power<=1618000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 1618 kW";
	    }else if(power>1618000){
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 2043 kW";
	    }
	}
	costVector = priceData.get(description);
    }
}
