package eplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import baseline.idfdata.thermalzone.DesignBuilderThermalZone;
import baseline.idfdata.thermalzone.ThermalZone;
import eplus.optimization.OptResult;
import eplus.optimization.OptResultSet;

public class EnergyPlusBuildingForHVACSystems {
    /**
     * the building thermal zone lists
     */
    private List<ThermalZone> thermalZoneList;
    private HashMap<String, ArrayList<ThermalZone>> ventilationMap;
    private HashMap<String, ArrayList<ThermalZone>> zoneSystemMap;

    private OptResultSet optResults;
    /**
     * EnergyPlus data
     */
    private IdfReader energyplusModel;

    /**
     * process related data
     */
    private Document doc;
    private static final String FILE_NAME = "HVACObjects.txt";
    private String[] objectList;
    private double totalArea = 0.0;


    public EnergyPlusBuildingForHVACSystems(IdfReader energyModel) {
        energyplusModel = energyModel;
        thermalZoneList = new ArrayList<ThermalZone>();
        ventilationMap = new HashMap<String, ArrayList<ThermalZone>>();
        zoneSystemMap = new HashMap<String, ArrayList<ThermalZone>>();
        optResults = new OptResultSet();
    }

    /**
     * This method must be called prior to get floorMap, get total cooling load
     * and get total heating load and then check return fans
     * 
     * @return
     */
    public void processModelInfo() {
	
	// building the thermal zones
	for (ThermalZone zone : thermalZoneList) {
	    // String block = zone.getBlock();
	    String hvac = zone.getZoneCoolHeat();
	    String vent = zone.getZoneVent();
	    
	    //totalArea += zone.getZoneArea();
	    if (!vent.equalsIgnoreCase("NONE") && !vent.equalsIgnoreCase("EXT")) {
		if (!ventilationMap.containsKey(vent)) {
		    ventilationMap.put(vent, new ArrayList<ThermalZone>());
		}
		ventilationMap.get(vent).add(zone);
	    }

	    if (!hvac.equalsIgnoreCase("NONE")) {
		if (!zoneSystemMap.containsKey(hvac)) {
		    zoneSystemMap.put(hvac, new ArrayList<ThermalZone>());
		}
		zoneSystemMap.get(hvac).add(zone);
	    }
	}
    }
    
    public HashMap<String, ArrayList<ThermalZone>> getVentilationMap() {
	return ventilationMap;
    }

    public HashMap<String, ArrayList<ThermalZone>> getZoneSystemMap() {
	return zoneSystemMap;
    }

    public int getNumberOfZone() {
	return thermalZoneList.size();
    }

    public String getZoneNamebyIndex(int index) {
	return thermalZoneList.get(index).getFullName();
    }

    public IdfReader getIdfData() {
	return energyplusModel;
    }

    public void generateEnergyPlusModel(String filePath, String fileName) {
	energyplusModel.WriteIdf(filePath, fileName);
    }

    public void removeHVAC() throws IOException {
	processObjectLists();
	for (String s : objectList) {
	    energyplusModel.removeEnergyPlusObject(s);
	}
    }

    public void processOutputs(File htmloutput) {
	try {
	    doc = Jsoup.parse(htmloutput, "UTF-8");
	    preprocessTable();
	} catch (IOException e) {
	    // do nothing
	}
	totalArea = getBuildingArea();
	System.out.println(totalArea);
	extractThermalZones();
    }

    public void addOptimizationResult(OptResult opt) {
	optResults.addResultSet(opt);
    }

    public OptResultSet getOptimizationResults() {
	return optResults;
    }
    
    public OptResult duplicatedSimulationCase(OptResult result){
	for(int i=0; i<optResults.getSize(); i++){
	    if(optResults.getResult(i).getRegressionMode()==false &&
		    result.equals(optResults.getResult(i))){
		return optResults.getResult(i);
	    }
	}
	return null;
    }
    
    public double getTotalBuildingArea(){
	return totalArea;
    }

    public void writeOutResults() {
	int row = optResults.getResultSet().size();

	try {
	    //FileWriter writer = new FileWriter(
	    //	    "E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\CSL\\Optimization\\output.txt");
	    FileWriter writer = new FileWriter(
		    "E:\\02_Weili\\01_Projects\\07_Toshiba\\Year 3\\Optimization\\output.txt");
	    for (int i = 0; i < row; i++) {
		OptResult r = optResults.getResult(i);
		writer.append(i + "@");
		writer.append(r.getOperationCost() + "@");
		writer.append(r.getFirstCost() + "@");
		writer.append(r.getEUI() + "@");
		for (int j = 0; j < r.getComponentLength(); j++) {
		    writer.append(r.getComponent(j));
		    writer.append("@");
		}
		for(int k=0; k<r.getNumericLength(); k++){
		    writer.append(r.getNumericValue(k).toString());
		    writer.append("@");
		}
		writer.append("\n");
	    }
	    writer.flush();
	    writer.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void extractThermalZones() {
	Elements thermalZoneSummary = doc.getElementsByAttributeValue(
		"tableID",
		"Input Verification and Results Summary:Zone Summary");
	Elements zoneList = thermalZoneSummary.get(0).getElementsByTag("tr");
	int conditionIndex = 2;
	for (int i = 1; i < zoneList.size(); i++) {
	    Elements info = zoneList.get(i).getElementsByTag("td");
	    if (info.get(conditionIndex).text().equalsIgnoreCase("YES") && info.get(conditionIndex+1).text().equalsIgnoreCase("YES")) {
		String zoneName = info.get(0).text();
		ThermalZone temp = null;
		temp = new DesignBuilderThermalZone(zoneName);
		thermalZoneList.add(temp);
	    }
	}
	processModelInfo();
    }

    private void preprocessTable() {
	String report = null;
	Elements htmlNodes = doc.getAllElements();
	for (int i = 0; i < htmlNodes.size(); i++) {
	    if (htmlNodes.get(i).text().contains("Report:")) {
		report = htmlNodes.get(i + 1).text();
	    }
	    if (htmlNodes.get(i).hasAttr("cellpadding")) {
		String tableName = htmlNodes.get(i - 3).text();
		htmlNodes.get(i).attr("tableID", report + ":" + tableName);
	    }
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
    
    /**
     * gets the building area
     * 
     * @return
     */
    private double getBuildingArea() {
	double area;
	Elements tables = doc.getElementsByTag("table");
	for (Element table : tables) {
	    Elements texts = table.getAllElements();
	    for (int i = 0; i < texts.size(); i++) {
		if (texts.get(i).getElementsByTag("td").text()
			.equals("Total Building Area")) {
		    area = Double.parseDouble(texts.get(i + 1).text());
		    return area;
		}
	    }
	}
	return -1;
    }
}
