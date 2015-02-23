package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FanSizingSummary {
    private final int fanCategoryIndex =1;
    private final int fanPressureIndex=2;
    private final int fanFlowRateIndex = 3;
    private final int fanPower = 4;
    private final int fanPowerperFlowRate = 5;
    private final int fanMotorHeat = 6;
    private final int fanEndUse = 7;
    
    private final Document doc;
    private final Elements fanSummaryTable;
    
    private static final String FAN_TABLE_ID = "Equipment Summary:Fans";
    private static final String TAG = "tableID";
    
    public FanSizingSummary(Document d){
	doc = d;
	fanSummaryTable = doc.getElementsByAttributeValue(TAG,
		FAN_TABLE_ID);
    }
    
    /**
     * Find the fan category according to the fan object in the idf file;
     * @param name
     * @return
     */
    public String getFanCategory(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanCategoryIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan category according to the fan object in the idf file (W/W);
     * @param name
     * @return
     */
    public String getFanPressure(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanPressureIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan flow rate according to the fan object in the idf file (m3/s)
     * @param name
     * @return
     */
    public String getFanFlowRate(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanFlowRateIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan power according to the fan object in the idf file (W)
     * @param name
     * @return
     */
    public String getFanPower(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanPower).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan power according to the fan object in the idf file (W-s/m3)
     * @param name
     * @return
     */
    public String getFanPowerPerAirFlow(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanMotorHeat).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan motor heat in air fraction according to the fan object in the idf file.
     * @param name
     * @return
     */
    public String getFanMotorHeat(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanPowerperFlowRate).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the fan end use according to the fan object in the idf file.
     * @param name
     * @return
     */
    public String getFanEndUse(String name){
	Elements fanList = fanSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<fanList.size();i++){
	    if(fanList.get(i).text().equalsIgnoreCase(name)){
		return fanList.get(i+fanEndUse).text();
	    }
	}
	return null;
    }
}
