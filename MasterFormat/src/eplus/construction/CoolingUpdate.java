package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class CoolingUpdate extends AbstractMasterFormatComponent
	implements BuildingComponent {

    private final static String coolingUpdate = "Chiller:Electric:EIR";

    private String[] selectedComponents;
    private Double[] costArray;
    private Double[] capacityArray;

    public CoolingUpdate() {
	// initialization
	selectedComponents = new String[2];
	selectedComponents[0] = "NONE:NONE";
	selectedComponents[1] = "Chiller:Chiller";

	getListAvailableComponent();
    }

    @Override
    public String getName() {
	return "ChillerEfficiency";
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;

	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement.executeQuery(
		    "select count(*) AS rowcount from hvac.chillers where TYPE = 'Reciprocating'");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    costArray = new Double[count];
	    capacityArray = new Double[count];

	    resultSet = statement.executeQuery(
		    "select * from hvac.chillers where TYPE = 'Reciprocating'");
	    int index = 0;
	    while (resultSet.next()) {
		capacityArray[index] = resultSet.getDouble("CAPACITY")
			* 3516.8525; // ton to kW conversion
		costArray[index] = resultSet.getDouble("TOTALCOST");
		index++;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return availableComponents;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {

    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public String[] getSelectedComponentsForRetrofit() {
	return selectedComponents;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	String indicator = component.split(":")[1];
	if (!indicator.equals("NONE")) {
	    HashMap<String, ArrayList<ValueNode>> chillers = reader
		    .getObjectListCopy(coolingUpdate);
	    Iterator<String> chillerIterator = chillers.keySet().iterator();
	    while (chillerIterator.hasNext()) {
		String element = chillerIterator.next();
		ArrayList<ValueNode> nodes = chillers.get(element);
		for (ValueNode vn : nodes) {
		    if (vn.getDescription().equalsIgnoreCase("Reference COP")) {
			vn.setAttribute("8.4");
		    }
		}
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	boolean hasReplaced = false;
	Elements plantList = doc
		.getElementsByAttributeValue("tableID",
			"Equipment Summary%Central Plant")
		.get(0).getElementsByTag("tr");

	for (int j = 1; j < plantList.size(); j++) {
	    Element plant = plantList.get(j);
	    if (plant.getElementsByTag("td").get(0).text()
		    .contains("CHILLER")) {
		if (plant.getElementsByTag("td").get(3).text().equals("8.40")) {
		    hasReplaced = true;
		}
	    }
	}

	if (hasReplaced) {
	    Elements zoneList = doc
		    .getElementsByAttributeValue("tableID",
			    "HVAC Sizing Summary%Zone Cooling")
		    .get(0).getElementsByTag("tr");
	    Double load = 0.0;
	    for (int i = 1; i < zoneList.size(); i++) {
		Element ele = zoneList.get(i);

		load = load + Double
			.parseDouble(ele.getElementsByTag("td").get(2).text());
	    }

	    int index = 0;
	    Double numChillers = Double.MAX_VALUE;
	    for (int j = 0; j < capacityArray.length; j++) {
		Double num = Math.ceil(load / capacityArray[j]);
		if (num < numChillers && num >= 1) {
		    numChillers = num;
		    index = j;
		}
	    }

	    return numChillers * costArray[index];
	} else {
	    return 0.0;
	}

    }

    @Override
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// TODO Auto-generated method stub

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
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub

    }

}
