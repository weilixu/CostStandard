package masterformat.standard.hvac.boiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HotWaterBoiler extends AbstractBoiler {

    private String sourceType;
    private Double power;

    private final Double[] powerList = { 7500.0, 90000.0, 296000.0, 1036000.0,
	    2400000.0, 3600000.0, 24000.0, 94000.0, 319000.0, 837000.0,
	    957000.0, 1788000.0, 2043000.0, 59000.0, 88000.0, 117000.0,
	    147000.0, 171000.0, 428000.0, 1198000.0, 3956000.0, 32000.0,
	    61000.0, 318000.0, 880000.0, 1618000.0, 2043000.0 };

    public HotWaterBoiler() {
	unit = "$/Ea";
	hierarchy = "235200 Heating Boiler";
    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	Set<String> inputs = userInputsMap.keySet();
	Iterator<String> iterator = inputs.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (temp.equals("Source")) {
		sourceType = userInputsMap.get(temp);
	    } else if (temp.equals("Power")) {
		power = Double.parseDouble(userInputsMap.get(temp));
	    }
	}
    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	if (sourceType != "") {
	    sourceType = surfaceProperties[sourceTypeIndex];
	} else {
	    userInputs.add("INPUT:Source: ");
	}

	try {
	    power = Double.parseDouble(surfaceProperties[capacityIndex]);
	} catch (NumberFormatException e) {
	    userInputs.add("INPUT:Power:Watt");
	}
    }

    @Override
    protected void initializeData() {
	Double[][] costsMatrix = { { 4975.0, 1000.0, 0.0, 5975.0, 6975.0 },
		{ 6000.0, 1175.0, 0.0, 7175.0, 8375.0 },
		{ 16100.0, 2350.0, 0.0, 18450.0, 21300.0 },
		{ 35500.0, 5225.0, 0.0, 40725.0, 47000.0 },
		{ 68000.0, 7100.0, 0.0, 75100.0, 85000.0 },
		{ 94000.0, 11100.0, 0.0, 105100.0, 120000.0 },
		{ 1975.0, 1250.0, 0.0, 3225.0, 4050.0 },
		{ 4675.0, 2275.0, 0.0, 6950.0, 8575.0 },
		{ 13500.0, 4550.0, 0.0, 18050.0, 21800.0 },
		{ 30800.0, 9100.0, 0.0, 39900.0, 47600.0 },
		{ 32700.0, 10200.0, 0.0, 42900.0, 51500.0 },
		{ 115500.0, 14200.0, 0.0, 129700.0, 148500.0 },
		{ 118500.0, 20500.0, 0.0, 139000.0, 161500.0 },
		{ 10500.0, 2175.0, 0.0, 12675.0, 14900.0 },
		{ 10500.0, 2725.0, 0.0, 13225.0, 15700.0 },
		{ 12300.0, 3225.0, 0.0, 15525.0, 18400.0 },
		{ 13300.0, 3750.0, 0.0, 17050.0, 20300.0 },
		{ 14700.0, 4100.0, 0.0, 18800.0, 22300.0 },
		{ 36400.0, 6425.0, 0.0, 42825.0, 49700.0 },
		{ 61500.0, 11100.0, 0.0, 72600.0, 84500.0 },
		{ 190500.0, 41400.0, 0.0, 231900.0, 272000.0 },
		{ 2325.00, 1525.0, 0.0, 3850.0, 4850.0 },
		{ 3175.0, 2025.0, 0.0, 5200.0, 6550.0 },
		{ 10900.0, 4850.0, 0.0, 15750.0, 19300.0 },
		{ 23700.0, 9675.0, 0.0, 33375.0, 40700.0 },
		{ 78500.0, 13400.0, 0.0, 91900.0, 106500.0 },
		{ 101000.0, 20700.0, 0.0, 121700.0, 142000.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 7.5kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 90kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 296kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 1036kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 2400kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Hot water, 3600kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 24 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 94 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 319 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 837 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 957 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 1788 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Hot water, 2043 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 59 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 88 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 117 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 147 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 171 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 428 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 1198 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, 3956 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 32 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 61 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 318 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 880 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 1618 kW");
	optionQuantities.add(0);
	optionLists
		.add("Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, 2043 kW");
	optionQuantities.add(0);
	for (int i = 0; i < optionLists.size(); i++) {
	    priceData.put(optionLists.get(i), costsMatrix[i]);
	}
    }

    @Override
    public void selectCostVector() {
	setToZero();
	Integer upperindex = 0;
	Integer lowerIndex = 0;
	if (sourceType.equals("Electricity")) {
	    if (power <= 7500) {
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 7500 && power <= 90000) {
		upperindex = 1;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 90000 && power <= 296000) {
		upperindex = 2;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 296000 && power <= 1036000) {
		upperindex = 3;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1036000 && power <= 2400000) {
		upperindex = 4;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 2400000 && power <= 3600000) {
		upperindex = 5;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else {
		upperindex = 5;
		description = "Electric Boilers, ASME, Standard controls and trim, Hot water, grouped";
		fittingPower(upperindex, lowerIndex);
	    }
	} else if (sourceType.equals("NaturalGas")
		|| sourceType.equals("PropaneGas")) {
	    lowerIndex = 6;
	    if (power <= 24000) {
		upperindex = 6;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 24000 && power <= 94000) {
		upperindex = 7;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 94000 && power <= 319000) {
		upperindex = 8;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 319000 && power <= 837000) {
		upperindex = 9;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 837000 && power <= 957000) {
		upperindex = 10;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 957000 && power <= 1788000) {
		upperindex = 11;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1788000 && power<=2043000) {
		upperindex = 12;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    }else{
		upperindex = 12;
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, grouped";
		fittingPower(upperindex,lowerIndex);
	    }
	} else if (sourceType.equals("Gasoline")) {
	    lowerIndex = 13;
	    if (power <= 59000) {
		upperindex = 13;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 59000 && power <= 88000) {
		upperindex = 14;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 88000 && power <= 117000) {
		upperindex = 15;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 117000 && power <= 147000) {
		upperindex = 16;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 147000 && power <= 171000) {
		upperindex = 17;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 171000 && power <= 428000) {
		upperindex = 18;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 428000 && power <= 1198000) {
		upperindex = 19;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1198000 && power<=3956000) {
		upperindex = 20;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else{
		upperindex = 20;
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Hotwater, grouped";
		fittingPower(upperindex, lowerIndex);
	    }
	} else if (sourceType.equals("Diesel")) {
	    lowerIndex = 21;
	    if (power <= 32000) {
		upperindex = 21;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 32000 && power <= 61000) {
		upperindex = 22;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 61000 && power <= 318000) {
		upperindex = 23;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 318000 && power <= 880000) {
		upperindex = 24;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 880000 && power <= 1618000) {
		upperindex = 25;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1618000 && power<=2043000.0) {
		upperindex = 26;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else {
		upperindex = 26;
		description = "Oil-Fired Fired Boilers, Standard controls, flame retention burner, packaged, Cast iron with insulated flush jacket, grouped";
		fittingPower(upperindex, lowerIndex);
	    }
	}
    }

    private void fittingPower(Integer upper, Integer lower) {
	// shows the best fit capacity
	Double fittedPower = 0.0;
	// shows the total capacity added
	Double totalPower = 0.0;
	costVector = deepCopyCost(Default_Cost_Vector);

	while (totalPower < power) {
	    fittedPower = findFittedPower(totalPower, upper, lower);
	    totalPower += fittedPower;
	}
    }

    private Double findFittedPower(Double total, Integer upper, Integer lower) {
	// the difference between capacity and total capacity
	Double temp = power;
	// index shows the current best fit capacity
	int criticalIndex = 0;

	for (int i = lower; i <= upper; i++) {
	    Double residual = Math.abs(power - total - powerList[i]);
	    if (residual < temp) {
		temp = residual;
		criticalIndex = i;
	    }
	}
	// add to the cost vector
	Double[] itemCost = priceData.get(optionLists.get(criticalIndex));
	for (int j = 0; j < costVector.length; j++) {
	    costVector[j] += itemCost[j];
	}
	Integer q = optionQuantities.get(criticalIndex) + 1;
	optionQuantities.set(criticalIndex, q);
	return powerList[criticalIndex];
    }

    private void setToZero() {
	for (int i = 0; i < optionQuantities.size(); i++) {
	    optionQuantities.set(i, 0);
	}
    }

    private Double[] deepCopyCost(Double[] costVector) {
	Double[] temp = new Double[costVector.length];
	for (int i = 0; i < costVector.length; i++) {
	    temp[i] = costVector[i];
	}
	return temp;
    }

}
