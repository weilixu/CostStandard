package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CentralPlantSummary {
    private final int equipmentCapacity = 2;
    private final int equipmentEfficiency = 3;
    
    private final Document doc;
    private final Elements centralPlantTable;
    
    private static final String PLANT_TABLE_ID = "Equipment Summary%Central Plant";
    private static final String TAG = "tableID";
    
    public CentralPlantSummary(Document d){
	doc = d;
	centralPlantTable = doc.getElementsByAttributeValue(TAG, PLANT_TABLE_ID);
    }
    
    public String getEquipmentCapacity(String name){
	Elements equipmentList = centralPlantTable.get(0).getElementsByTag("td");
	for(int i=0; i<equipmentList.size(); i++){
	    if(equipmentList.get(i).text().equalsIgnoreCase(name)){
		return equipmentList.get(i+equipmentCapacity).text();
	    }
	}
	return null;
    }
    
    public String getEquipmentEfficiency(String name){
	Elements equipmentList = centralPlantTable.get(0).getElementsByTag("td");
	for(int i=0; i<equipmentList.size(); i++){
	    if(equipmentList.get(i).text().equalsIgnoreCase(name)){
		return equipmentList.get(i+equipmentEfficiency).text();
	    }
	}
	return null;
    }
    

}
