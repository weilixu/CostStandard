package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.ZoneHTMLParser;
import masterformat.api.AbstractMasterFormatComponent;

public class WindowWallRatio extends AbstractMasterFormatComponent
	implements BuildingComponent {

    private final static String fenestration = "FenestrationSurface:Detailed";
    private final static String field = "Multiplier";
    private String orientation;

    private String[] selectedComponents; // daylight sensor has only on/off

    public WindowWallRatio(String orientation) {
	this.orientation = orientation;
	selectedComponents = getListAvailableComponent();
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement.executeQuery(
		    "select count(*) AS rowcount from energyplusconstruction.wwr");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.wwr");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("RATIO");
		availableComponents[index] = des;
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
	selectedComponents = componentList;
    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	Integer ratio = Integer.parseInt(component);
	HashMap<String, ArrayList<ValueNode>> fene = reader
		.getObjectListCopy(fenestration);
	Set<String> feneSet = fene.keySet();
	Iterator<String> feneIterator = feneSet.iterator();
	while (feneIterator.hasNext()) {
	    String element = feneIterator.next();
	    ArrayList<ValueNode> nodes = fene.get(element);
	    String name = nodes.get(0).getAttribute();
	    if (ZoneHTMLParser.getFenestrationOrientation(name)
		    .equals(orientation)) {
		for (ValueNode vn : nodes) {
		    if (vn.getDescription().equalsIgnoreCase(field)) {
			vn.setAttribute(ratio.toString());
		    }
		}
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	return 0;
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
