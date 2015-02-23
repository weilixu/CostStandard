package masterformat.standard.hvac.boiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HotWaterBoiler extends AbstractBoiler{
    
    private String sourceType;
    private Double power;
    
    public HotWaterBoiler(){
	unit = "$/Ea";
	hierarchy = "235200 Heating Boiler";
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
	Double[][] costsMatrix = {{4975.0,1000.0,0.0,5975.0,6975.0},
		{6000.0,1175.0,0.0,7175.0,8375.0},
		{16100.0,2350.0,0.0,18450.0,21300.0},
		{35500.0,5225.0,0.0,40725.0,47000.0},
		{68000.0,7100.0,0.0,75100.0,85000.0},
		{94000.0,11100.0,0.0,105100.0,120000.0},
		{1975.0,1250.0,0.0,3225.0,4050.0},
		{4675.0,2275.0,0.0,6950.0,8575.0},
		{13500.0,4550.0,0.0,18050.0,21800.0},
		{30800.0,9100.0,0.0,39900.0,47600.0},
		{32700.0,10200.0,0.0,42900.0,51500.0},
		{115500.0,14200.0,0.0,129700.0,148500.0},
		{118500.0,20500.0,0.0,139000.0,161500.0},
		{10500.0,2175.0,0.0,12675.0,14900.0},
		{10500.0,2725.0,0.0,13225.0,15700.0},
		{12300.0,3225.0,0.0,15525.0,18400.0},
		{13300.0,3750.0,0.0,17050.0,20300.0},
		{14700.0,4100.0,0.0,18800.0,22300.0},
		{36400.0,6425.0,0.0,42825.0,49700.0},
		{61500.0,11100.0,0.0,72600.0,84500.0},
		{190500.0,41400.0,0.0,231900.0,272000.0},
		{2325.00,1525.0,0.0,3850.0,4850.0},
		{3175.0,2025.0,0.0,5200.0,6550.0},
		{10900.0,4850.0,0.0,15750.0,19300.0},
		{23700.0,9675.0,0.0,33375.0,40700.0},
		{78500.0,13400.0,0.0,91900.0,106500.0},
		{101000.0,20700.0,0.0,121700.0,142000.0}
	};
	
	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 7.5kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 90kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 296kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 1036kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 2400kW");
	typesOne.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 3600kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 24 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 94 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 319 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 837 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 957 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 1788 kW");
	typesOne.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 2043 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 59 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 88 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 117 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 147 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 171 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 428 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 1198 kW");
	typesOne.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 3956 kW");
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
	    if(power<=7500){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 7.5kW";
	    }else if(power>7500 && power<=90000){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 90kW";
	    }else if(power>90000 && power <=296000){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 296kW";
	    }else if(power>296000 && power<=1036000){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 1036kW";
	    }else if(power>1036000 && power<=2400000){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 2400kW";
	    }else if(power>2400000){
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, 3600kW";
	    }
	}else if(sourceType.equals("NaturalGas")||sourceType.equals("PropaneGas")){
	    if(power<=24000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 24 kW";
	    }else if(power>24000 && power<=94000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 94 kW";
	    }else if(power>94000 && power<=319000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 319 kW";
	    }else if(power>319000 && power<=837000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 837 kW";
	    }else if(power>837000 && power<=957000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 957 kW";
	    }else if(power>957000 && power<=1788000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 1788 kW";
	    }else if(power>1788000){
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 2043 kW";
	    }
	}else if(sourceType.equals("Gasoline")){
	    if(power<=59000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 59 kW";
	    }else if(power>59000 && power<=88000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 88 kW";
	    }else if(power>88000 && power<=117000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 117 kW";
	    }else if(power>117000 && power<=147000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 147 kW";
	    }else if(power>147000 && power<=171000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 171 kW";
	    }else if(power>171000 && power<=428000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 428 kW";
	    }else if(power>428000 && power<=1198000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 1198 kW";
	    }else if(power>1198000){
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 3956 kW";
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
	costVector = deepCopyCost(priceData.get(description));
    }
    
    private Double[] deepCopyCost(Double[] costVector){
	Double[] temp = new Double[costVector.length];
	for(int i=0; i<costVector.length; i++){
	    temp[i]= costVector[i];
	}
	return temp;
    }

}
