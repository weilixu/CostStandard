package masterformat.standard.hvac.boiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SteamBoiler extends AbstractBoiler {

    private String sourceType;
    private Double power;

    private final Double[] powerList = { 6000.0, 60000.0, 112000.0, 2220000.0,
	    518000.0, 814000.0, 2340000.0, 24000.0, 60000.0, 117000.0,
	    224000.0, 550000.0, 1383000.0, 1788000.0, 2042000.0, 211000.0,
	    469000.0, 791000.0, 1618000.0, 1873000.0, 2043000.0, 32000.0,
	    61000.0, 318000.0, 880000.0, 1618000.0, 2043000.0 };

    public SteamBoiler() {
	unit = "$/Ea";
	hierarchy = "235200 Heating Boilers";
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
	Double[][] costsMatrix = { { 3950.0, 1075.0, 0.0, 5025.0, 5975.0 },
		{ 6650.0, 1300.0, 0.0, 7950.0, 9275.0 },
		{ 9375.0, 1725.0, 0.0, 11100.0, 12900.0 },
		{ 23800.0, 2350.0, 0.0, 26150.0, 29800.0 },
		{ 32600.0, 4925.0, 0.0, 37525.0, 43300.0 },
		{ 40600.0, 7100.0, 0.0, 47700.0, 55500.0 },
		{ 86500.0, 11100.0, 0.0, 97600.0, 111500.0 },
		{ 2450.0, 1300.0, 0.0, 3750.0, 4675.0 },
		{ 3825.0, 2025.0, 0.0, 5850.0, 7250.0 },
		{ 5900.0, 3225.0, 0.0, 9125.0, 11400.0 },
		{ 12200.0, 4225.0, 0.0, 16425.0, 19800.0 },
		{ 25200.0, 6075.0, 0.0, 31275.0, 36900.0 },
		{ 68000.0, 11800.0, 0.0, 79800.0, 93000.0 },
		{ 88000.0, 14000.0, 0.0, 102000.0, 118000.0 },
		{ 98500.0, 18200.0, 0.0, 116700.0, 136000.0 },
		{ 14700.0, 4225.0, 0.0, 18925.0, 22600.0 },
		{ 20900.0, 6100.0, 0.0, 27000.0, 32200.0 },
		{ 28100.0, 9425.0, 0.0, 37525.0, 45100.0 },
		{ 90500.0, 13400.0, 0.0, 103900.0, 119500.0 },
		{ 97000.0, 16900.0, 0.0, 113900.0, 132000.0 },
		{ 102500.0, 21200.0, 0.0, 123700.0, 145000.0 },
		{ 2325.00, 1525.0, 0.0, 3850.0, 4850.0 },
		{ 3175.0, 2025.0, 0.0, 5200.0, 6550.0 },
		{ 10900.0, 4850.0, 0.0, 15750.0, 19300.0 },
		{ 23700.0, 9675.0, 0.0, 33375.0, 40700.0 },
		{ 78500.0, 13400.0, 0.0, 91900.0, 106500.0 },
		{ 101000.0, 20700.0, 0.0, 121700.0, 142000.0 } };

	optionLists = new ArrayList<String>();
	optionQuantities = new ArrayList<Integer>();
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 6kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 60kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 112kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 222kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 518kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 814kW");
	optionQuantities.add(0);
	optionLists
		.add("Electric Boilers, ASME, Standard controls and trim, Steam, 2340kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 24 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 60 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 117 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 224 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 550 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1383 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 1788 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, 2042 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 211 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 469 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 791 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1618 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 1873 kW");
	optionQuantities.add(0);
	optionLists
		.add("Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, 2043 kW");
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
	    if (power <= 6000) {
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 6000 && power <= 60000) {
		upperindex = 1;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 60000 && power <= 112000) {
		upperindex = 2;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 112000 && power <= 222000) {
		upperindex = 3;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 222000 && power <= 518000) {
		upperindex = 4;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 518000 && power <= 814000) {
		upperindex = 5;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 814000 && power <= 2340000) {
		upperindex = 6;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else {
		upperindex = 6;
		description = "Electric Boilers, ASME, Standard controls and trim, Steam, grouped";
		fittingPower(upperindex, lowerIndex);
	    }
	} else if (sourceType.equals("NaturalGas")
		|| sourceType.equals("PropaneGas")) {
	    lowerIndex = 7;
	    if (power <= 24000) {
		upperindex = 7;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 24000 && power <= 60000) {
		upperindex = 8;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 60000 && power <= 117000) {
		upperindex = 9;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 117000 && power <= 224000) {
		upperindex = 10;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 224000 && power <= 550000) {
		upperindex = 11;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 550000 && power <= 1383000) {
		upperindex = 12;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1383000 && power <= 1788000) {
		upperindex = 13;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1788000 && power <= 2042000) {
		upperindex = 14;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else {
		upperindex = 14;
		description = "Gas-Fired Boilers, Natural or propane, standard controls, packaged, cast iron with insulated jacket, Steam, grouped";
		fittingPower(upperindex, lowerIndex);
	    }
	} else if (sourceType.equals("Gasoline")) {
	    lowerIndex = 15;
	    if (power <= 211000) {
		upperindex = 15;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 211000 && power <= 469000) {
		upperindex = 16;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 469000 && power <= 791000) {
		upperindex = 17;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 791000 && power <= 1618000) {
		upperindex = 18;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1618000 && power <= 1873000) {
		upperindex = 19;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else if (power > 1873000 && power <= 2043000) {
		upperindex = 20;
		description = optionLists.get(upperindex);
		costVector = deepCopyCost(priceData.get(description));
		Integer i = optionQuantities.get(upperindex);
		optionQuantities.set(upperindex, i + 1);
	    } else {
		upperindex = 20;
		description = "Gas/Oil Fired Boilers, Combination with burners and controls, packaged, Cast iron with insulated jacket, Steam, grouped";
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
	    } else if (power > 1618000 && power <= 2043000) {
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
