package Concrete;

import java.util.ArrayList;
import java.util.HashMap;

public class CastInPlaceSlabOnGrade extends AbstractConcrete{
    
    protected String unit = "m2";
    protected String hierarchy = "030000 Concrete|033000 Cast-In-Place Concrete|033053.40 Concrete In Place";

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
	ArrayList<String> typesTwo = new ArrayList<String>();
	
	typesOne.add("Slab on grade (3500 psi), incl. troweled finish, not incl.forms or reinforcing, over 1,000 m2");
	typesOne.add("Slab on grade(3000 psi), incl.broom finish, not incl.forms or reinforcing");
	typesTwo.add("100 mm thick");
	typesTwo.add("150 mm thick");
	typesTwo.add("200 mm thick");
	typesTwo.add("300 mm thick");
	typesTwo.add("380 mm thick");
	
	HashMap<String, Double[]> tempTable = new HashMap<String, Double[]>();
	tempTable.put(typesTwo.get(0), unitConversion(costsMatrix[0]));
	tempTable.put(typesTwo.get(1), unitConversion(costsMatrix[1]));
	tempTable.put(typesTwo.get(2), unitConversion(costsMatrix[2]));
	tempTable.put(typesTwo.get(3), unitConversion(costsMatrix[3]));
	tempTable.put(typesTwo.get(4), unitConversion(costsMatrix[4]));
	priceData.put(typesOne.get(0),tempTable);
	tempTable = new HashMap<String, Double[]>();
	tempTable.put(typesTwo.get(0), unitConversion(costsMatrix[5]));
	tempTable.put(typesTwo.get(1), unitConversion(costsMatrix[6]));
	tempTable.put(typesTwo.get(2), unitConversion(costsMatrix[7]));
	priceData.put(typesOne.get(1),tempTable);
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
