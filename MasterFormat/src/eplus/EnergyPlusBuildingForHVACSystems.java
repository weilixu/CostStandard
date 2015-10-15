package eplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import baseline.idfdata.DesignBuilderThermalZone;
import baseline.idfdata.ThermalZone;

public class EnergyPlusBuildingForHVACSystems {
    /**
     * the building thermal zone lists
     */
    private List<ThermalZone> thermalZoneList;
    private HashMap<String, ArrayList<ThermalZone>> ventilationMap;
    private HashMap<String, ArrayList<ThermalZone>> zoneSystemMap;
    
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
    
    public EnergyPlusBuildingForHVACSystems(IdfReader energyModel){
	energyplusModel = energyModel;
	thermalZoneList = new ArrayList<ThermalZone>();
	ventilationMap = new HashMap<String, ArrayList<ThermalZone>>();
	zoneSystemMap = new HashMap<String, ArrayList<ThermalZone>>();
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

	    if (!vent.equalsIgnoreCase("NONE")&&!vent.equalsIgnoreCase("EXT")) {
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
    
    public IdfReader getIdfData(){
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
	extractThermalZones();
    }
    

    private void extractThermalZones() {
	Elements thermalZoneSummary = doc.getElementsByAttributeValue(
		"tableID",
		"Input Verification and Results Summary:Zone Summary");
	Elements zoneList = thermalZoneSummary.get(0).getElementsByTag("tr");
	int conditionIndex = 2;
	for (int i = 1; i < zoneList.size(); i++) {
	    Elements info = zoneList.get(i).getElementsByTag("td");
	    if (info.get(conditionIndex).text().equalsIgnoreCase("YES")) {
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
}
