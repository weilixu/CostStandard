package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EnvelopeSummary {
    private final int areaIndex =4;
    
    private final Document doc;
    private final Elements envelopeTable;
    
    private static final String PLANT_TABLE_ID = "Envelope Summary%Opaque Exterior";
    private static final String TAG = "tableID";
    
    
    public EnvelopeSummary(Document d){
	doc = d;
	envelopeTable = doc.getElementsByAttributeValue(TAG, PLANT_TABLE_ID);
    }
    
    public double getConstructionArea(String cons){
	Elements constructionList = envelopeTable.get(0).getElementsByTag("td");
	double area = 0.0;
	for(int i=0; i<constructionList.size(); i++){
	    if(constructionList.get(i).text().equalsIgnoreCase(cons)){
		area+=Double.parseDouble(constructionList.get(i+areaIndex).text());
	    }
	}
	return area;
    }
}
