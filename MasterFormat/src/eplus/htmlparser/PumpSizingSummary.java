package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PumpSizingSummary {
    private final int pumpControl = 2;
    private final int pumpHead = 3;
    private final int pumpWaterFlow = 4;
    private final int pumpPower = 5;
    private final int pumpPowerperFlow = 6;
    private final int pumpEfficiency = 7;
    
    private final Document doc;
    private final Elements pumpSummaryTable;
    
    private static final String PUMP_TABLE_ID = "Equipment Summary%Pumps";
    private static final String TAG = "tableID";
    
    public PumpSizingSummary(Document d){
	doc = d;
	pumpSummaryTable = doc.getElementsByAttributeValue(TAG, PUMP_TABLE_ID);
    }
    
    /**
     * Find the pump control type according to the pump object in the idf file;
     * @param name
     * @return
     */
    public String getPumpControl(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpControl).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the pump head according to the pump object in the idf file;
     * @param name
     * @return
     */
    public String getPumpHead(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpHead).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the pump water flow according to the pump object in the idf file
     * @param name
     * @return
     */
    public String getPumpWaterFlow(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpWaterFlow).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the pump power according to the pump object in the idf file
     * @param name
     * @return
     */
    public String getPumpPower(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpPower).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the pump power per water flow according to the pump object in the idf file
     * @param name
     * @return
     */
    public String getPumpPowerperFlow(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpPowerperFlow).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the pump efficiency according to the pump object in the idf file
     * @param name
     * @return
     */
    public String getPumpEfficiency(String name){
	Elements pumpList = pumpSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<pumpList.size();i++){
	    if(pumpList.get(i).text().equalsIgnoreCase(name)){
		return pumpList.get(i+pumpEfficiency).text();
	    }
	}
	return null;
    }

}
