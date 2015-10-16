package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class InteriorLightingSummary {
    
    private final int lightTotalPower=4;
    
    private final Document doc;
    private final Elements lightSummaryTable;
    
    private static final String LIGHT_TABLE_ID = "Lighting Summary%Interior Lighting";
    private static final String TAG = "tableID";
    
    public InteriorLightingSummary(Document d){
	doc = d;
	lightSummaryTable = doc.getElementsByAttributeValue(TAG,LIGHT_TABLE_ID);
    }
    
    public String getLightPower(String name){
	Elements lightList = lightSummaryTable.get(0).getElementsByTag("td");
	for(int i=0; i<lightList.size();i++){
	    if(lightList.get(i).text().equalsIgnoreCase(name)){
		return lightList.get(i+lightTotalPower).text();
	    }
	}
	return null;
    }
}
