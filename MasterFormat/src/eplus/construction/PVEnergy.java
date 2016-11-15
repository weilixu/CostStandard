package eplus.construction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import eplus.htmlparser.ZoneHTMLParser;
import masterformat.api.AbstractMasterFormatComponent;

public class PVEnergy extends AbstractMasterFormatComponent
	implements BuildingComponent {
    // component cost items
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity"; // indicates the component cost line item object
    private final String componentCostObject = "ComponentCost:LineItem";

    private final String PVPerformanceObject = "PhotovoltaicPerformance:EquivalentOne-Diode";
    private final String pvPerformanceDataMap = "Name:Cell type:Number of Cells in Series:Active Area:Transmittance Absorptance Product:Semiconductor Bandgap:Shunt Resistance:"
	    + "Short Circuit Current:Open Circuit Voltage:Reference Temperature:Reference Insolation:Module Current at Maximum Power:Module Voltage at Maximum Power:"
	    + "Temperature Coefficient of Short Circuit Current:Temperature Coefficient of Open Circuit Voltage:Nominal Operating Cell Temperature Test Ambiet Temperature:"
	    + "Nominal Operating Cell Temperature Test Cell Temperature:Nominal Operating Cell Temperature Test Insolation:Module Heat Loss Coefficient:Total Heat Capacity";

    private final String generatorPhotovoltaic = "Name:Surface Name:Photovoltaic Performance Object Type:Module Performance Name:Heat Transfer Integration Model:"
	    + "Number of Series Strings in Parallel:Number of Modules in Series";
    private final String generatorObject = "Generator:Photovoltaic";

    private final String inverter = "Name:Availabitlity Schedule Name:Zone Name:Radiative Fraction:Inverter Efficiency";
    private final String inverterObject = "ElectricLoadCenter:Inverter:Simple";

    private final String distribution = "Name:Generator List Name:Generator Operation Scheme Type:Demand Limit Scheme Purchased Electric Demand Limit:Track Schedule Name Scheme Schedule Name:"
	    + "Track Meter Scheme Meter Name:Electrical Buss Type:Inverter Object Name:Electrical Storage Object Name:Transformer object Name";
    private final String distributionObject = "ElectricLoadCenter:Distribution";

    private String[] selectedComponents = null;
    private final String orientation;
    
    private double totalPower;

    // surface data
    private ArrayList<String> surfaces;

    public PVEnergy(String o) {
	selectedComponents = getListAvailableComponent();
	orientation = o;
	surfaces = new ArrayList<String>();
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return "PV" + orientation;
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();
	    statement = connect.createStatement();

	    resultSet = statement.executeQuery(
		    "select count(*) AS rowcount from energyplusconstruction.pvproducts");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement.executeQuery(
		    "select * from energyplusconstruction.pvproducts");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("ProductNAME");
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
	// this component is supposedly the pv product name
	if (!component.equals("NONE")) {
	    totalPower = 0;
	    try {
		// 1. setup connections
		super.testConnect();
		statement = connect.createStatement();
		resultSet = statement.executeQuery(
			"select * from energyplusconstruction.pvproducts where ProductNAME = '"
				+ component + "'");
		resultSet.next();
		// 2. setUp PhotovoltaicPerformance:EquivalentOne-Diode data
		Double activeArea = Double
			.parseDouble(resultSet.getString("ActiveArea"));
		String productName = resultSet.getString("ProductNAME") + "_"
			+ orientation;

		String[] pvPerformanceDes = pvPerformanceDataMap.split(":");
		String[] pvPerformanceValue = { productName,
			resultSet.getString("CellType"),
			resultSet.getString("NumberOfCells"),
			activeArea.toString(), "0.9", "1.12", "1000000",
			resultSet.getString("SCC"), resultSet.getString("OCV"),
			"25", "1000", resultSet.getString("CurrentAtMaxPower"),
			resultSet.getString("VoltageAtMaxPower"),
			resultSet.getString("TempCoefSCC"),
			resultSet.getString("TempCoefOCV"), "20", "46", "800",
			"26.66", "50000" };
		reader.addNewEnergyPlusObject(PVPerformanceObject,
			pvPerformanceValue, pvPerformanceDes);

		// Generates generator:photovoltaic data
		// 1 take out all the exterior wall surfaces face to one
		// orientation.
		HashMap<String, ArrayList<ValueNode>> tempList = reader
			.getObjectListCopy("BuildingSurface:Detailed");
		Set<String> names = tempList.keySet();
		for (String name : names) {
		    ArrayList<ValueNode> info = tempList.get(name);
		    String surfaceName = info.get(0).getAttribute();
		    String sunExpo = info.get(6).getAttribute();
		    if (sunExpo.equalsIgnoreCase("SunExposed")) {
			if (ZoneHTMLParser.getSurfaceOrientation(surfaceName)
				.equals(orientation)) {
			    surfaces.add(surfaceName);
			}
		    }
		}
		// 2. start creating generator:photovoltaic object for each
		// surface in surfaces list
		for (int i = 0; i < surfaces.size(); i++) {
		    Double numModule = ZoneHTMLParser
			    .getSurfaceArea(surfaces.get(i)) / activeArea;
		    if (numModule < 1) {
			numModule = 1.0;
		    }

		    String[] pvGeneratorDes = generatorPhotovoltaic.split(":");
		    String[] pvGeneratorValues = { surfaces.get(i) + "_PV",
			    surfaces.get(i),
			    "PhotovoltaicPerformance:EquivalentOne-Diode",
			    productName, "IntegratedSurfaceOutsideFace", "1",
			    numModule.intValue() + "" };
		    reader.addNewEnergyPlusObject(generatorObject,
			    pvGeneratorValues, pvGeneratorDes);
		    
		    Double power = resultSet.getDouble("PeakPower");
		    totalPower += power * numModule.intValue();
		}

		// Next, develope the ElectricLoadCenter:Generators
		String[] electricGeneratorsDes = new String[1
			+ surfaces.size() * 5];// each surface generator has 5
					       // values
		String[] electricGeneratorsValue = new String[electricGeneratorsDes.length];
		electricGeneratorsDes[0] = "Name";
		electricGeneratorsValue[0] = "BIPV_Generator_" + orientation;
		int genNameOffset = 1;
		int objTypeOffset = 2;
		int powerOutputOffset = 3;
		int availScheOffset = 4;
		int powerRatioOffset = 5;

		int numPV = 0;
		for (int i = 0; i < surfaces.size(); i++) {
		    numPV++;
		    electricGeneratorsDes[i * 5 + genNameOffset] = "Generator "
			    + numPV + " Name";
		    electricGeneratorsValue[i * 5 + genNameOffset] = surfaces
			    .get(i) + "_PV";

		    electricGeneratorsDes[i * 5 + objTypeOffset] = "Generator "
			    + numPV + " Object Type";
		    electricGeneratorsValue[i * 5
			    + objTypeOffset] = "Generator:Photovoltaic";

		    electricGeneratorsDes[i * 5
			    + powerOutputOffset] = "Generator " + numPV
				    + " Rated Electric Power Output";
		    electricGeneratorsValue[i * 5
			    + powerOutputOffset] = "1000000";

		    electricGeneratorsDes[i * 5
			    + availScheOffset] = "Generator " + numPV
				    + " Availability Schedule Name";
		    electricGeneratorsValue[i * 5 + availScheOffset] = "On";

		    electricGeneratorsDes[i * 5
			    + powerRatioOffset] = "Generator " + numPV
				    + " Rated Thermal to Electrical Power Ratio";
		    electricGeneratorsValue[i * 5 + powerRatioOffset] = "";
		}
		reader.addNewEnergyPlusObject("ElectricLoadCenter:Generators",
			electricGeneratorsValue, electricGeneratorsDes);

		// build simple inverter
		String[] inverterDes = inverter.split(":");
		String[] inverterValues = { "Inverter_" + orientation, "On", "",
			"0.3", "0.94" };
		reader.addNewEnergyPlusObject(inverterObject, inverterValues,
			inverterDes);

		// build load distribution
		String[] distributionDes = distribution.split(":");
		String[] distributionValues = { orientation + " BIPV",
			"BIPV_Generator_" + orientation, "Baseload", "0", "",
			"", "DirectCurrentWithInverter",
			"Inverter_" + orientation, "", "" };
		reader.addNewEnergyPlusObject(distributionObject,
			distributionValues, distributionDes);

	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close();
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	// TODO Auto-generated method stub
	return totalPower * 4500;
    }

    @Override
    public boolean isIntegerTypeComponent() {
	// yes this is an integer type component (yes/no)
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// No need because it is a integer type variable

    }

    @Override
    public void selectCostVector() {
	// currently no need

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
