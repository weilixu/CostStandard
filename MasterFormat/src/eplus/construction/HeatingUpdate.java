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

public class HeatingUpdate extends AbstractMasterFormatComponent
	implements BuildingComponent {

    private final static String HeatUpdate = "Boiler:HotWater";

    private String[] selectedComponents;
    private Double[] costArray;
    private Double[] capacityArray;

    public HeatingUpdate() {
	// initialization
	selectedComponents = new String[2];
	selectedComponents[0] = "NONE:NONE";
	selectedComponents[1] = "Boiler:Boiler";

	getListAvailableComponent();
    }

    @Override
    public String getName() {
	return "BoilerEfficiency";
    }

    @Override
    public String[] getListAvailableComponent() {

	String[] availableComponents = null;

	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement.executeQuery(
		    "select count(*) AS rowcount from hvac.heatingboilers where SOURCE = 'Gas' and MEDIA = 'Hot Water'");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    costArray = new Double[count];
	    capacityArray = new Double[count];

	    resultSet = statement.executeQuery(
		    "select * from hvac.heatingboilers where SOURCE = 'Gas' and MEDIA = 'Hot Water'");
	    int index = 0;
	    while (resultSet.next()) {
		capacityArray[index] = resultSet.getDouble("CAPACITY");
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
	    HashMap<String, ArrayList<ValueNode>> boilers = reader
		    .getObjectListCopy(HeatUpdate);
	    Iterator<String> boilerIterator = boilers.keySet().iterator();
	    while (boilerIterator.hasNext()) {
		String element = boilerIterator.next();
		ArrayList<ValueNode> nodes = boilers.get(element);
		for (ValueNode vn : nodes) {
		    if (vn.getDescription()
			    .equalsIgnoreCase("Nominal Thermal Efficiency")) {
			vn.setAttribute("0.95");
		    }
		}
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {

	Elements zoneList = doc
		.getElementsByAttributeValue("tableID",
			"HVAC Sizing Summary%Zone Heating")
		.get(0).getElementsByTag("tr");
	Double load = 0.0;
	for (int i = 1; i < zoneList.size(); i++) {
	    Element ele = zoneList.get(i);

	    load = load + Double
		    .parseDouble(ele.getElementsByTag("td").get(2).text());
	}

	int index = 0;
	Double numBoilers = Double.MAX_VALUE;
	for (int j = 0; j < capacityArray.length; j++) {
	    Double num = Math.ceil(load / capacityArray[j]);
	    if (num < numBoilers && num > 1) {
		numBoilers = num;
		index = j;
	    }
	}
	return numBoilers * costArray[index];
    }

    @Override
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	// TODO Auto-generated method stub
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
