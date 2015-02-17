package masterformat.standard.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CastInPlaceSlabOnGrade extends AbstractConcrete{
    
    private double area;
    private double thickness;
    
    public CastInPlaceSlabOnGrade(){
	unit = "$/m2";
	hierarchy = "030000 Concrete:033000 Cast-In-Place Concrete:033053.40 Concrete In Place";
    }


    @Override
    public void selectCostVector() {
	if(area>=1000.00){
	    if(thickness<0.1){
		description = "Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 100 mm thick";
	    }else if(thickness<0.15){
		description = "Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 150 mm thick";
	    }else if(thickness<0.2){
		description = "Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 200 mm thick";
	    }else if(thickness<0.3){
		description = "Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 300 mm thick";
	    }else {
		description = "Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 380 mm thick";
	    }
	}else{
	    if(thickness<0.1){
		description = "Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 100 mm thick";
	    }else if(thickness<0.15){
		description = "Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 150 mm thick";
	    }else {
		description = "Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 200 mm thick";
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
	    if(temp.equals("Area")){
		area = Double.parseDouble(userInputsMap.get(temp));
	    }else if(temp.equals("thickness")){
		thickness = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
	
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	try{
	    area = Double.parseDouble(surfaceProperties[floorAreaIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Area:m2");
	}
	try{
	    thickness = Double.parseDouble(surfaceProperties[thicknessIndex]);
	}catch(NumberFormatException e){
	    userInputs.add("INPUT:Thickness:m");
	}
    }
    
    @Override
    protected void initializeData() {
	//the data is in IP unit, conversion factor from S.F to m2 is 1sf = 0.092903 m3
	Double[][] costsMatrix = { { 1.35, 0.90,0.01, 2.26, 2.85},
		{ 1.98, 0.92, 0.01, 2.91,3.57 },
		{ 2.71, 0.97, 0.01, 3.69,4.44 },
		{ 4.06, 1.13, 0.01, 5.20, 6.15 },
		{ 5.10, 1.23, 0.01, 6.34, 7.45 },
		{ 1.32, 0.82, 0.01, 2.15, 2.69 },
		{ 2.07, 0.91, 0.01, 2.99, 3.65 },
		{ 2.70, 1.02, 0.01, 3.73, 4.50 }};
	
	ArrayList<String> typesOne = new ArrayList<String>();
	typesOne.add("Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 100 mm thick");
	typesOne.add("Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 150 mm thick");
	typesOne.add("Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 200 mm thick");
	typesOne.add("Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 300 mm thick");
	typesOne.add("Slab on grade(3500 psi), incl.troweled finish, not incl.forms or reinforcing, over 1,000 m2, 380 mm thick");
	typesOne.add("Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 100 mm thick");
	typesOne.add("Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 150 mm thick");
	typesOne.add("Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing, 200 mm thick");

	
	for(int i=0; i<typesOne.size(); i++){
	    priceData.put(typesOne.get(i), unitConversion(costsMatrix[i]));
	}
    }
    
    private Double[] unitConversion(Double[] data){
	//the data is in IP unit, conversion factor from S.F to m2 is 1sq = 0.092903 m2
	Double[] temp = new Double[data.length];
	for(int i = 0; i<temp.length;i++){
	    temp[i]=data[i]/0.092903;
	}
	return temp;
    }

}
