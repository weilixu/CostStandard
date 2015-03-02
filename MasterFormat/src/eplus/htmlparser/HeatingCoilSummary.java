package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HeatingCoilSummary {
    
    private final int coilTypeIndex = 1;
    private final int coilDesignLoadIndex = 2;
    private final int coilTotalCapacityIndex = 3;
    private final int coilEfficiencyIndex = 4;
    
    private final Document doc;
    private final Elements heatingCoilSummaryTable;
    
    private static final String HEAT_COIL_TABLE_ID = "Equipment Summary:Heating Coils";
    private static final String TAG = "tableID";
    
    public HeatingCoilSummary(Document d){
	doc = d;
	heatingCoilSummaryTable = doc.getElementsByAttributeValue(TAG,HEAT_COIL_TABLE_ID);
    }
    
    public String getHeatingCoilType(String name){
	Elements coilList = heatingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilTypeIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the heating coil design load according to the heating coil object in the idf file;
     */
    public String getHeatingCoilDesignLoad(String name){
	Elements coilList = heatingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilDesignLoadIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the heating coil total capacity according to the heating coil object in the idf file;
     */
    public String getHeatingCoilCapacity(String name){
	Elements coilList = heatingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilTotalCapacityIndex).text();
	    }
	}
	return null;
    }
    
    /**
     * Find the heating coil efficiency according to the heating coil object in the idf file;
     */
    public String getHeatingCoilEfficiency(String name){
	Elements coilList = heatingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilEfficiencyIndex).text();
	    }
	}
	return null;
    }
}
