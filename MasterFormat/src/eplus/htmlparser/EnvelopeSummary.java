package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EnvelopeSummary {
    private final int areaIndex =4;
    private final int orientationIndex = 9;
    
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
    
    public double getSurfaceArea(String surface){
	double area = 0.0;
	Elements surfaceList = envelopeTable.get(0).getElementsByTag("td");
	for(int i=0; i<surfaceList.size(); i++){
	    if(surfaceList.get(i).text().equalsIgnoreCase(surface)){
		area = Double.parseDouble(surfaceList.get(i+1+areaIndex).text());
		break;
	    }
	}
	return area;
    }
    
    public String getSurfaceOrientation(String cons){
	String orientation = "";
	Elements surfaceList = envelopeTable.get(0).getElementsByTag("td");
	for(int i=0; i<surfaceList.size(); i++){
	    if(surfaceList.get(i).text().equalsIgnoreCase(cons)){
		orientation = surfaceList.get(i+orientationIndex).text();
		break;
	    }
	}
	return orientation;
    }
}
