package masterformat.standard.masonry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ThinBrickVeneer extends AbstractMasonry {
    
    protected String unit = "$/m2";
    protected String hierarchy = "042100 Clay Unit Masonry:042113 Brick Masonry:042113.14 Thin Brick Veneer";

    
    //shows the tyep of the brick that selected
    private String brickType;
    //shows any special charactor the type of brick might have
    private String specialCharacter;

    @Override
    public void selectCostVector() {
	Double[] cost = priceData.get(brickType);
	
	if(specialCharacter.equals("For embedment into pre-cast concrete panels, add (155/m2)")){
	    cost = addToTotal(cost,155.00);
	}
	description = brickType;
	costVector = cost;
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while(iterator.hasNext()){
	    String temp = iterator.next();
	    if(temp.equals("BrickType")){
		brickType = userInputsMap.get(temp);
	    }else if(temp.equals("SpecialCharacter")){
		specialCharacter = userInputsMap.get(temp);
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	//there is nothing to map it from EnergyPlus to this class
    }
    
    protected void initializeData(){
	Double[][] costsMatrix = { { 9.00, 6.65, 0.0, 15.65, 19.70 },
		{ 8.80, 5.55, 0.0, 14.35, 17.85 },
		{ 8.85, 5.55, 0.0, 14.40, 17.90 },
		{ 8.55, 4.89, 0.0, 13.44, 16.65 },
		{ 9.65, 3.49, 0.0, 13.14, 15.80 },
		{ 9.95, 3.13, 0.0, 13.08, 15.60 },
		{ 4.21, 4.46, 0.0, 8.67, 11.25 },
		{ 3.98, 3.70, 0.0, 7.68, 9.85 },
		{ 4.02, 3.70, 0.0, 7.72, 9.85 },
		{ 3.75, 3.30, 0.0, 7.05, 9.00 },
		{ 4.85, 2.35, 0.0, 7.20, 8.80 },
		{ 5.15, 2.14, 0.0, 7.29, 8.80 }};
	
	
	ArrayList<String> typesOne = new ArrayList<String>();
	
	typesOne.add("On & incl. metal panel support sys, modular, 68 x 16 x 200 (mm), red");
	typesOne.add("On & incl. metal panel support sys, Closure, 100 x 16 x 200 (mm)");
	typesOne.add("On & incl. metal panel support sys, Norman, 68 x 16 x 300 (mm)");
	typesOne.add("On & incl. metal panel support sys, Utility, 100 x 16 x 300 (mm)");
	typesOne.add("On & incl. metal panel support sys, Emperor, 100 x 20 x 400 (mm)");
	typesOne.add("On & incl. metal panel support sys, Super emperor, 200 x 20 x 400 (mm)");
	typesOne.add("On masonry/plaster back-up, modular, 68 x 16 x 200 (mm), red");
	typesOne.add("On masonry/plaster back-up, Closure, 100 x 16 x 200 (mm)");
	typesOne.add("On masonry/plaster back-up, Norman, 68 x 16 x 300 (mm)");
	typesOne.add("On masonry/plaster back-up, Utility, 100 x 16 x 300 (mm)");
	typesOne.add("On masonry/plaster back-up, Emperor, 100 x 20 x 400 (mm)");
	typesOne.add("On masonry/plaster back-up, Super emperor, 200 x 20 x 400 (mm)");
	
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, modular, 68 x 16 x 200 (mm), red");
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, Closure, 100 x 16 x 200 (mm)");
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, Norman, 68 x 16 x 300 (mm)");
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, Utility, 100 x 16 x 300 (mm)");
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, Emperor, 100 x 20 x 400 (mm)");
	userInputs.add("OPTION:BrickType:On & incl. metal panel support sys, Super emperor, 200 x 20 x 400 (mm)");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, modular, 68 x 16 x 200 (mm), red");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, Closure, 100 x 16 x 200 (mm)");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, Norman, 68 x 16 x 300 (mm)");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, Utility, 100 x 16 x 300 (mm)");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, Emperor, 100 x 20 x 400 (mm)");
	userInputs.add("OPTION:BrickType:On masonry/plaster back-up, Super emperor, 200 x 20 x 400 (mm)");
	
	userInputs.add("OPTION:SpecialCharacter:For embedment into pre-cast concrete panels, add (155/m2)");
	
	for(int i = 0; i<typesOne.size();i++){
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

    // this method allows the last two elements in the double array to be added
    // by the addition
    /*
     * PreCondition: the double array has a length of 5 and the last two element represents
     * the total cost and total cost with profit
     */
    private Double[] addToTotal(Double[] data, Double addition) {
	Double[] tempDouble = new Double[5];
	for(int i=0; i<data.length; i++){
	    tempDouble[i] = data[i];
	}
	tempDouble[tempDouble.length-1] = tempDouble[tempDouble.length-1]+addition;
	tempDouble[tempDouble.length-2] = tempDouble[tempDouble.length-2]+addition;

	return tempDouble;
    }

}
