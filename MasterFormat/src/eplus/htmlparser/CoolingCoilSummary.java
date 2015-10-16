package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CoolingCoilSummary {
    
    private final int coilTypeIndex = 1;
    private final int coilDesignLoadIndex = 2;
    private final int coilTotalCapacityIndex = 3;
    private final int coilSensibleLoadIndex = 4;
    private final int coilLatentLoadIndex = 5;
    private final int coilEfficiencyIndex = 6;
    private final int coilUAValue = 7;
    private final int coilSurfaceArea = 8;
    
    private final Document doc;
    private final Elements coolingCoilSummaryTable;
    
    private static final String COOL_COIL_TABLE_ID = "Equipment Summary%Cooling Coils";
    private static final String TAG = "tableID";
    
    public CoolingCoilSummary(Document d){
	doc = d;
	coolingCoilSummaryTable = doc.getElementsByAttributeValue(TAG,COOL_COIL_TABLE_ID);
    }
    
    public String getCoolingCoilType(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilTypeIndex).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilDesignLoad(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilDesignLoadIndex).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilTotalCapacity(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilTotalCapacityIndex).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilSensibleLoad(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilSensibleLoadIndex).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilLatentLoad(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilLatentLoadIndex).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilEfficiency(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilEfficiencyIndex).text();
	    }
	}
	return null;
    }
    
    public String getCooilingCoilUAValue(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilUAValue).text();
	    }
	}
	return null;
    }
    
    public String getCoolingCoilSurfaceArea(String name){
	Elements coilList = coolingCoilSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<coilList.size(); i++){
	    if(coilList.get(i).text().equalsIgnoreCase(name)){
		return coilList.get(i+coilSurfaceArea).text();
	    }
	}
	return null;
    }
}
