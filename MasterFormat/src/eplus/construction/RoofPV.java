package eplus.construction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class RoofPV extends AbstractMasterFormatComponent
	implements BuildingComponent {

    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
    private final String componentCostObject = "ComponentCost:LineItem";

    private String[] selectedComponents = null;
    private double totalPower;

    public RoofPV() {
	selectedComponents = new String[2];
	selectedComponents[0] = "Yes";
	selectedComponents[1] = "No";
    }

    @Override
    public String getName() {
	return "Roof PV";
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = { "No", "Yes" };
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
    public String[] getSelectedComponentsForRetrofit() {
	return null;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	System.out.println("Roof top PV " + component);
	if (component.equals("Yes")) {
	    IdfReader pvReader = new IdfReader();
	    pvReader.setFilePath(
		    "E:\\02_Weili\\01_Projects\\07_Toshiba\\Year 3\\Optimization\\ROOFPV.idf");
	    try {
		pvReader.readEplusFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    // HashMap<String, ArrayList<ValueNode>> schedule =
	    // pvReader.getObjectListCopy("Schedule:Compact");
	    HashMap<String, ArrayList<ValueNode>> shade = pvReader
		    .getObjectListCopy("Shading:Building:Detailed");
	    //System.out.println("This is the shading size:" + shade.size());
	    HashMap<String, ArrayList<ValueNode>> photovoltaic = pvReader
		    .getObjectListCopy("Generator:Photovoltaic");
	    HashMap<String, ArrayList<ValueNode>> pvOneDiode = pvReader
		    .getObjectListCopy(
			    "PhotovoltaicPerformance:EquivalentOne-Diode");
	    HashMap<String, ArrayList<ValueNode>> electricLoad = pvReader
		    .getObjectListCopy("ElectricLoadCenter:Generators");
	    HashMap<String, ArrayList<ValueNode>> inverter = pvReader
		    .getObjectListCopy("ElectricLoadCenter:Inverter:Simple");
	    HashMap<String, ArrayList<ValueNode>> distribution = pvReader
		    .getObjectListCopy("ElectricLoadCenter:Distribution");

	    reader.writeInObjects("Shading:Building:Detailed", shade);

	    reader.writeInObjects("Generator:Photovoltaic", photovoltaic);
	    reader.writeInObjects("PhotovoltaicPerformance:EquivalentOne-Diode",
		    pvOneDiode);
	    reader.writeInObjects("ElectricLoadCenter:Generators",
		    electricLoad);
	    reader.writeInObjects("ElectricLoadCenter:Inverter:Simple",
		    inverter);
	    reader.writeInObjects("ElectricLoadCenter:Distribution",
		    distribution);
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	return 37925 * 4500 / 1000;
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
