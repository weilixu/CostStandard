package masonry;

import java.util.ArrayList;
import java.util.HashMap;

public class BrickVennerMasonry extends AbstractMasonry {

    public BrickVennerMasonry() {
	hierarchy = "042100 Clay Unit Masonry|042113 Brick Masonry|042113.13 Brick Veneer Masonry";
	initializeData();
    }
    
    /**
     * Initialize the data for this particular object
     * Data includes a 2-dimension cost matrix with double type values.
     * The first element indicates material cost
     * The second element indicates labor cost
     * The third element indicates equipment cost
     * The fourth element indicates total cost
     * The fifth element indicates total include operation cost
     * 
     * Two ArrayList<String> are built. One contains the first level filters
     * the other one contains the second level filters.
     * 
     * As last, all the information is built in a 2-dimension hashmap where the first
     * level filter point to the second level filter and then point to the array of 
     * calculated cost
     */
    protected void initializeData() {
	Double[][] costsMatrix = { { 4.16, 7.45, 0.0, 11.61, 16.0 },
		{ 3.72, 7.80, 0.0, 11.52, 16.0 },
		{ 4.34, 9.30, 0.0, 13.64, 18.90 },
		{ 5.55, 12.25, 0.0, 17.80, 25.0 },
		{ 4.94, 11.45, 0.0, 16.39, 23.0 },
		{ 3.93, 8.35, 0.0, 12.28, 17.10 },
		{ 7.40, 16.35, 0.0, 23.75, 33.0 },
		{ 7.40, 17.15, 0.0, 24.55, 34.0 },
		{ 2.51, 5.55, 0.0, 8.06, 11.20 },
		{ 3.72, 8.60, 0.0, 12.32, 17.20 },
		{ 2.51, 5.90, 0.0, 8.41, 11.80 },
		{ 3.93, 7.80, 0.0, 11.73, 16.20 },
		{ 11.75, 8.15, 0.0, 19.90, 25.50 },
		{ 13.70, 10.10, 0.0, 23.80, 30.50 },
		{ 4.67, 3.95, 0.0, 8.62, 11.15 },
		{ 5.95, 5.35, 0.0, 11.30, 14.70 },
		{ 5.15, 4.58, 0.0, 9.73, 12.65 },
		{ 4.08, 5.55, 0.0, 9.63, 12.95 },
		{ 3.61, 6.60, 0.0, 10.21, 14.05 },
		{ 6.95, 6.85, 0.0, 13.80, 18.10 },
		{ 6.0, 5.55, 0.0, 11.55, 15.05 },
		{ 4.65, 4.77, 0.0, 9.42, 12.40 } };
	
	
	ArrayList<String> typesOne = new ArrayList<String>();
	ArrayList<String> typesTwo = new ArrayList<String>();
	typesOne.add("Standard, sel. comon, 100 x 68 x 200 (mm), (72.55/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Running bond");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Full header every 6th course(85/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), English, full header every 2nd course(109/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Flemish, alternate header every course(96.87/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Flemish, alt, header every 6th course(76.75/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Full headers throughout(145.31/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Rowlock course(145.31/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Rowlock stretcher(48.44/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Soldier course(72.65/m2)");
	typesOne.add("Standard, Red, 100 x 68 x 200 (mm), Sailor course(48.44/m2)");
	typesOne.add("Standard, Buff or gray face, Running bond(72.65/m2)");
	typesOne.add("Standard, Buff or gray face, glazed face brick, running bond(72.65/m2)");
	typesOne.add("Standard, Buff or gray face, glazed face brick, running bond, fl header every 6th course(84.8/m2)");
	typesOne.add("Jumbo, 152 x 100 x 300 (mm) running bond (32.3/m2)");
	typesOne.add("Norman, 100 x 68 x 300 (mm) running bond (48.44/m2)");
	typesOne.add("Norwegian, 100 x 80 x 300 (mm) (40.36/m2)");
	typesOne.add("Economy, 100 x 100 x 200 (mm) (48.44/m2)");
	typesOne.add("Engineer, 100 x 80 x 200 (mm) (48.44/m2)");
	typesOne.add("Roman, 100 x 50 x 300 (mm) (64.58/m2)");
	typesOne.add("SCR, 152 x 68 x 300 (mm) (48.44/m2)");
	typesOne.add("Utility, 100 x 100 x 300 (mm) (32.3/m2)");

	typesTwo.add("NONE");
	typesTwo.add("For less than truck load lots (add 15%)");
	typesTwo.add("For battered walls, (add 30%)");
	typesTwo.add("For corbels, (add 75%)");
	typesTwo.add("For cavity wall construction, (add 15%)");
	typesTwo.add("For stacked bond, (add 10%)");
	typesTwo.add("For interior veneer construction, (add 15%)");
	typesTwo.add("For pits and trenches, (deduct 20%)");
	typesTwo.add("For curved walls, (add 30%)");

	for (int i = 0; i < typesOne.size(); i++) {
	    HashMap<String, Double[]> tempTable = new HashMap<String, Double[]>();
	    tempTable.put(typesTwo.get(0),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.0));
	    tempTable.put(typesTwo.get(1),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.15));
	    tempTable.put(typesTwo.get(2),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.3));
	    tempTable.put(typesTwo.get(3),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.75));
	    tempTable.put(typesTwo.get(4),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.15));
	    tempTable.put(typesTwo.get(5),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.10));
	    tempTable.put(typesTwo.get(6),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.15));
	    tempTable.put(typesTwo.get(7),
		    MultiplyElements(unitConversion(costsMatrix[i]), 0.80));
	    tempTable.put(typesTwo.get(8),
		    MultiplyElements(unitConversion(costsMatrix[i]), 1.30));
	    priceData.put(typesOne.get(i), tempTable);
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
    
    /**
     * used when it requires a multiplication or deduction of an array.
     * @param data
     * @param multiplier
     * @return
     */
    private Double[] MultiplyElements(Double[] data, double multiplier) {
	for (int i = 0; i < data.length; i++) {
	    data[i] = data[i] * multiplier;
	}
	return data;
    }

}
