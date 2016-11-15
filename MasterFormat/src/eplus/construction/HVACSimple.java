package eplus.construction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.nodes.Document;

import baseline.idfdata.EplusObject;
import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.HVAC.DOASFactory;
import eplus.HVAC.HVACSystem;
import eplus.HVAC.PSZACFactory;
import eplus.HVAC.PTHPFactory;
import eplus.HVAC.PackagedVAVFactory;
import eplus.HVAC.SystemMerger;
import eplus.HVAC.VAVFactory;
import eplus.HVAC.VRFSystemFactory;
import masterformat.api.AbstractMasterFormatComponent;

public class HVACSimple extends AbstractMasterFormatComponent implements
	BuildingComponent {

    private String[] selectedComponents = null;

    private EnergyPlusBuildingForHVACSystems eplusBldg;

    private static final String FILE_NAME = "HVACObjects.txt";
    private String[] objectList;

    private HVACSystem system;

    private Double systemUnitCost;

    private final String reccuringCostDescription = "Name:Category:Cost:Start of Costs:Years from Start:Months from Start:Repeat Period Years:Repeat Period Months:Annual escalation rate";
    private final String recurringCostObject = "LifeCycleCost:RecurringCosts";

    public HVACSimple(EnergyPlusBuildingForHVACSystems bldg) {
	selectedComponents = getListAvailableComponent();
	eplusBldg = bldg;
    }
    
    @Override
    public String getName(){
	return "hvac";
    }

    @Override
    public String[] getListAvailableComponent() {
	String[] availableComponents = null;
	try {
	    super.testConnect();

	    statement = connect.createStatement();

	    resultSet = statement
		    .executeQuery("select count(*) AS rowcount from energyplusconstruction.hvacsimple");

	    resultSet.next();
	    int count = resultSet.getInt("rowcount");

	    availableComponents = new String[count];

	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.hvacsimple");
	    int index = 0;
	    while (resultSet.next()) {
		String des = resultSet.getString("HVACTYPE") + ":"
			+ resultSet.getString("DESCRIPTION");
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
	return selectedComponents[Index] + ":HVAC";
    }

    @Override
    public void writeInEnergyPlus(IdfReader eplusFile, String component) {
	String[] sysCharacter = component.split(":");
	String sysName = sysCharacter[0];
	String sysDes = sysCharacter[1];
	
	try {
	    super.testConnect();

	    statement = connect.createStatement();
	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.hvacsimple where HVACTYPE = '"
			    + sysName + "'");
	    resultSet.next();
	    systemUnitCost = resultSet.getDouble("Cost");

	    if (sysName.equals("Packaged VAV")) {
		PackagedVAVFactory factory = new PackagedVAVFactory(eplusBldg);
		system = factory.getSystem();
	    } else if(sysName.equals("VAV")){
		VAVFactory factory = new VAVFactory(eplusBldg);
		system = factory.getSystem();
	    }else if (sysName.equals("VRF")) {
		VRFSystemFactory factory = new VRFSystemFactory(eplusBldg);
		system = factory.getSystem();
	    } else if(sysName.equals("PTHP")){
		PTHPFactory factory = new PTHPFactory(eplusBldg);
		system = factory.getSystem();
	    }else if(sysName.equals("PSZAC")){
		PSZACFactory factory = new PSZACFactory(eplusBldg);
		system = factory.getSystem();
	    }else {
		VRFSystemFactory vrfFactory = new VRFSystemFactory(eplusBldg);
		DOASFactory doasFactory = new DOASFactory(eplusBldg);
		system = new SystemMerger(doasFactory.getSystem(),
			vrfFactory.getSystem());
	    }
	    
///		VAVFactory factory = new VAVFactory(eplusBldg);
//		system = factory.getSystem();
//	    System.out.println("get the system!!!");

	    // 4. create lca maintenance/replacement schedule
	    resultSet = statement
		    .executeQuery("select * from energyplusconstruction.recurringcost where System = '"
			    + sysDes + "'");
	    while (resultSet.next()) {
		String hvacside = resultSet.getString("HVACPART");
		if (hvacside.equals("Demand")) {
		    for (int i = 0; i < system.getNumberOfDemandSystem(); i++) {
			String recost = resultSet.getString("Cost");
			String reCategory = resultSet.getString("Category");
			Double reYear = resultSet.getDouble("Year");
			Double month = reYear * 12;// 12 months
			String reTask = resultSet.getString("Task");
			String[] reValues = {
				system.getSystemName() + "_Demand_" + i + "_"
					+ reTask, reCategory, recost,
				"ServicePeriod", "0", "0", "",
				month.toString(), "" };
			String[] reDescirption = reccuringCostDescription
				.split(":");
			eplusFile.addNewEnergyPlusObject(recurringCostObject,
				reValues, reDescirption);
		    }
		} else {
		    for (int i = 0; i < system.getNumberOfSupplySystem(); i++) {
			String recost = resultSet.getString("Cost");
			String reCategory = resultSet.getString("Category");
			Double reYear = resultSet.getDouble("Year");
			Double month = reYear * 12;// 12 months
			String reTask = resultSet.getString("Task");
			String[] reValues = {
				system.getSystemName() + "_Supply" + i + "_"
					+ reTask, reCategory, recost,
				"ServicePeriod", "0", "0", "",
				month.toString(), "" };
			String[] reDescirption = reccuringCostDescription
				.split(":");
			eplusFile.addNewEnergyPlusObject(recurringCostObject,
				reValues, reDescirption);
		    }
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	insertSystem(system, eplusFile);
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

    /**
     * Merge the system with baseline model, this should be called after
     */
    private void insertSystem(HVACSystem system, IdfReader eplusFile) {
	try {
	    removeHVAC(eplusFile);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	HashMap<String, ArrayList<EplusObject>> hvac = system.getSystemData();
	Set<String> hvacSet = hvac.keySet();
	Iterator<String> hvacIterator = hvacSet.iterator();
	while (hvacIterator.hasNext()) {
	    String partSystem = hvacIterator.next();
	    ArrayList<EplusObject> objectList = hvac.get(partSystem);
	    for (EplusObject eo : objectList) {
		// System.out.println(eo.getObjectName());
		String[] objectValues = new String[eo.getSize()];
		String[] objectDes = new String[eo.getSize()];
		// loop over the key-value pairs
		for (int i = 0; i < objectValues.length; i++) {
		    objectValues[i] = eo.getKeyValuePair(i).getValue();
		    objectDes[i] = eo.getKeyValuePair(i).getKey();
		}
		// add the object to the baseline model
		
		eplusFile.addNewEnergyPlusObject(eo.getObjectName(),
			objectValues, objectDes);
	    }
	}
    }

    public void removeHVAC(IdfReader eplusFile) throws IOException {
	processObjectLists();
	for (String s : objectList) {
	    eplusFile.removeEnergyPlusObject(s);
	}
    }

    // HVAC objects list is read from local list file
    private void processObjectLists() throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));

	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
		sb.append(line);
		sb.append("%");
		line = br.readLine();
	    }
	    objectList = sb.toString().split("%");
	} finally {
	    br.close();
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	Double load = system.getTotalLoad(doc);
	return load * systemUnitCost / 1000;
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
}
