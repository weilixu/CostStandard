package masterformat.standard.thermalmoistureprotection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RigidInsulation extends AbstractThermalMoistureProtection {

    protected String hierarchy = "070000 Thermal & Moisture Protection:072100 Thermal Insulation:072113 Board Insulation:072113.10 Rigid Insulation";
    
    protected String unit = "$/m2";
    //see what's the insulation types
    private String insulationType;
    //the R-value of the insulation
    private double rvalue;
    //the thickness of the insulation
    private double thickness;


    @Override
    protected void initializeData() {
	// the cost matrix indicates: material, labor, equipment, total, total
	// incl O&P, R-value
	Double[][] costsMatrix = { { 0.27, 0.38, 0.00, 0.65, 0.88},
		{ 0.40, 0.38, 0.00, 0.78, 1.02},
		{ 0.45, 0.38, 0.00, 0.83, 1.08},
		{ 0.56, 0.47, 0.00, 1.03, 1.34},
		{ 0.52, 0.38, 0.00, 0.90, 1.15},
		{ 0.78, 0.38, 0.00, 1.16, 1.44},
		{ 1.05, 0.42, 0.00, 1.47, 1.81},
		{ 1.10, 0.47, 0.00, 1.57, 1.93},
		{ 1.59, 0.47, 0.00, 2.06, 2.47},
		{ 0.90, 0.38, 0.00, 1.28, 1.57},
		{ 1.35, 0.38, 0.00, 1.73, 2.07},
		{ 1.69, 0.42, 0.00, 2.11, 2.51},
		{ 1.98, 0.47, 0.00, 2.45, 2.90},
		{ 2.18, 0.47, 0.00, 2.65, 3.12},
		{ 0.42, 0.47, 0.00, 0.89, 1.18},
		{ 0.75, 0.51, 0.00, 1.26, 1.62},
		{ 0.53, 0.47, 0.00, 1.00, 1.30},
		{ 1.04, 0.51, 0.00, 1.55, 1.93},
		{ 1.50, 0.51, 0.00, 2.01, 2.44},
		{ 0.25, 0.47, 0.00, 0.72, 1.00},
		{ 0.50, 0.51, 0.00, 1.01, 1.34},
		{ 0.75, 0.51, 0.00, 1.25, 1.62} };

	ArrayList<String> typesOne = new ArrayList<String>();	
	userInputs.add("OPTION:InsulationType:Fiberglass, 0.04#/m3, unfaced");
	userInputs.add("OPTION:InsulationType:Fiberglass, 0.085#/m3, unfaced");
	userInputs.add("OPTION:InsulationType:Fiberglass, 0.085#/m3, Foil faced");
	userInputs.add("OPTION:InsulationType:Perlite");
	userInputs.add("OPTION:InsulationType:Extruded polystyrene, 25 PSI compressive strength");
	userInputs.add("OPTION:InsulationType:Expanded polystyrene");
	
	typesOne.add("Fiberglass, 0.04#/m3, unfaced, 25.4 mm, R-Value: 1.2");
	typesOne.add("Fiberglass, 0.04#/m3, unfaced, 38.1 mm, R-Value: 1.82");
	typesOne.add("Fiberglass, 0.04#/m3, unfaced, 50.8 mm, R-Value: 2.43");
	typesOne.add("Fiberglass, 0.04#/m3, unfaced, 76.2 mm, R-Value: 3.63");
	typesOne.add("Fiberglass, 0.085#/m3, unfaced, 25.4 mm, R-Value: 1.26");
	typesOne.add("Fiberglass, 0.085#/m3, unfaced, 38.1 mm, R-Value: 1.90");
	typesOne.add("Fiberglass, 0.085#/m3, unfaced, 50.8 mm, R-Value: 2.55");
	typesOne.add("Fiberglass, 0.085#/m3, unfaced, 63.5 mm, R-Value: 3.19");
	typesOne.add("Fiberglass, 0.085#/m3, unfaced, 76.2 mm, R-Value: 3.81");
	typesOne.add("Fiberglass, 0.085#/m3, Foil faced, 25.4 mm, R-Value: 1.26");
	typesOne.add("Fiberglass, 0.085#/m3, Foil faced, 38.1 mm, R-Value: 1.90");
	typesOne.add("Fiberglass, 0.085#/m3, Foil faced, 50.8 mm, R-Value: 2.55");
	typesOne.add("Fiberglass, 0.085#/m3, Foil faced, 63.5 mm, R-Value: 3.19");
	typesOne.add("Fiberglass, 0.085#/m3, Foil faced, 76.2 mm, R-Value: 3.81");
	typesOne.add("Perlite, 25.4 mm, R-Value: 0.81");
	typesOne.add("Perlite, 50.8 mm, R-Value: 1.63");
	typesOne.add("Extruded polystyrene, 25 PSI compressive strength, 25.4 mm, R-Value: 1.47");
	typesOne.add("Extruded polystyrene, 25 PSI compressive strength, 50.8 mm, R-Value: 2.93");
	typesOne.add("Extruded polystyrene, 25 PSI compressive strength, 76.2 mm, R-Value: 4.40");
	typesOne.add("Expanded polystyrene, 25.4 mm, R-Value: 1.13");
	typesOne.add("Expanded polystyrene, 50.8 mm, R-Value: 2.25");
	typesOne.add("Expanded polystyrene, 76.2 mm, R-Value: 3.37");
	
	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), unitConversion(costsMatrix[i]));
	}
    }

    @Override
    public void selectCostVector() {
	if(insulationType.equals("Fiberglass, 0.04#/m3, unfaced")){
	    if(thickness<=0.0254||rvalue<=1.2){
		description = "Fiberglass, 0.04#/m3, unfaced, 25.4 mm, R-Value: 1.2";
	    }else if(thickness<=0.0381||rvalue<=1.82){
		description = "Fiberglass, 0.04#/m3, unfaced, 38.1 mm, R-Value: 1.82";
	    }else if(thickness<=0.0508||rvalue<=2.43){
		description = "Fiberglass, 0.04#/m3, unfaced, 50.8 mm, R-Value: 2.43";
	    }else if(thickness<=0.0762||rvalue<=3.63){
		description = "Fiberglass, 0.04#/m3, unfaced, 76.2 mm, R-Value: 3.63";
	    }
	}else if(insulationType.equals("Fiberglass, 0.085#/m3, unfaced")){
	    if(thickness<=0.254||rvalue<=1.26){
		description = "Fiberglass, 0.085#/m3, unfaced, 25.4 mm, R-Value: 1.26";
	    }else if(thickness<=0.381||rvalue<=1.90){
		description = "Fiberglass, 0.085#/m3, unfaced, 38.1 mm, R-Value: 1.90";
	    }else if(thickness<=0.508||rvalue<=2.55){
		description = "Fiberglass, 0.085#/m3, unfaced, 50.8 mm, R-Value: 2.55";
	    }else if(thickness<=0.635||rvalue<=3.19){
		description = "Fiberglass, 0.085#/m3, unfaced, 63.5 mm, R-Value: 3.19";
	    }else if(thickness<=0.762||rvalue<=3.81){
		description = "Fiberglass, 0.085#/m3, unfaced, 76.2 mm, R-Value: 3.81";
	    }
	}else if(insulationType.equals("Fiberglass, 0.085#/m3, Foil faced")){
	    if(thickness<=0.0254||rvalue<=1.26){
		description = "Fiberglass, 0.085#/m3, Foil faced, 25.4 mm, R-Value: 1.26";
	    }else if(thickness<=0.0381||rvalue<=1.90){
		description = "Fiberglass, 0.085#/m3, Foil faced, 38.1 mm, R-Value: 1.90";
	    }else if(thickness<=0.0508||rvalue<=2.55){
		description = "Fiberglass, 0.085#/m3, Foil faced, 50.8 mm, R-Value: 2.55";
	    }else if(thickness<=0.0635||rvalue<=3.19){
		description = "Fiberglass, 0.085#/m3, Foil faced, 63.5 mm, R-Value: 3.19";
	    }else if(thickness<=0.0762||rvalue<=3.81){
		description = "Fiberglass, 0.085#/m3, Foil faced, 76.2 mm, R-Value: 3.81";
	    }
	}else if(insulationType.equals("Perlite")){
	    if(thickness<=0.0254||rvalue<=0.81){
		description = "Perlite, 25.4 mm, R-Value: 0.81";
	    }else if(thickness<=0.0508||rvalue<=1.63){
		description = "Perlite, 50.8 mm, R-Value: 1.63";
	    }
	}else if(insulationType.equals("Extruded polystyrene, 25 PSI compressive strength")){
	    if(thickness<=0.0254||rvalue<=1.47){
		description = "Extruded polystyrene, 25 PSI compressive strength, 25.4 mm, R-Value: 1.47";
	    }else if(thickness<=0.0508||rvalue<=2.93){
		description = "Extruded polystyrene, 25 PSI compressive strength, 50.8 mm, R-Value: 2.93";
	    }else if(thickness<=0.0762||rvalue<=4.40){
		description = "Extruded polystyrene, 25 PSI compressive strength, 76.2 mm, R-Value: 4.40";
	    }
	}else if(insulationType.equals("Expanded polystyrene")){
	    if(thickness<=0.0254||rvalue<=1.13){
		description = "Expanded polystyrene, 25.4 mm, R-Value: 1.13";
	    }else if(thickness<=0.0508||rvalue<=2.25){
		description = "Expanded polystyrene, 50.8 mm, R-Value: 2.25";
	    }else if(thickness<=0.0762||rvalue<=3.37){
		description = "Expanded polystyrene, 76.2 mm, R-Value: 3.37";
	    }
	}
	costVector = priceData.get(description);
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("Thickness")){
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("Rvalue")){
		rvalue = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("InsulationType")){
		insulationType = userInputsMap.get(temp);
	    }
	}
    }
    
    @Override
    public void setVariable(String[] surfaceProperties) {
	System.out.println(Arrays.toString(surfaceProperties));
	
	try{
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	}catch(NumberFormatException | NullPointerException e){
	    userInputs.add("INPUT:Thickness:m");
	}
	try{
	    rvalue = Double.parseDouble(surfaceProperties[resistanceIndex]);
	}catch(NumberFormatException | NullPointerException e){
	    userInputs.add("INPUT:Rvalue:m2K/W");
	}
    }
    
    private Double[] unitConversion(Double[] data) {
	// the data is in IP unit, conversion factor from S.F to m2 is 1sf =
	// 0.092903 m2
	Double[] temp = new Double[data.length];
	for (int i = 0; i < temp.length; i++) {
	    temp[i] = data[i] / 0.092903;
	}
	return temp;
    }

}
