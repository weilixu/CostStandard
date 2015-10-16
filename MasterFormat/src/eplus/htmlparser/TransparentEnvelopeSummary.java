package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TransparentEnvelopeSummary {
    private final int areaIndexForConstruction =1;
    private final int uvalueIndexForConstruction =6;
    private final int shgcIndexForConstruction = 7;
    private final int vtIndexForConstruction = 8;

    
    private final Document doc;
    private final Elements envelopeTable;
    
    private static final String PLANT_TABLE_ID = "Envelope Summary%Exterior Fenestration";
    private static final String TAG = "tableID";
    
    public TransparentEnvelopeSummary(Document d){
	doc = d;
	envelopeTable = doc.getElementsByAttributeValue(TAG, PLANT_TABLE_ID);
    }
    
    public String getContructionArea(String cons){
	Elements constructionList = envelopeTable.get(0).getElementsByTag("td");
	double area = 0.0;
	
	for(int i=0; i<constructionList.size(); i++){
	    if(constructionList.get(i).text().equalsIgnoreCase(cons)){
		area+= Double.parseDouble(constructionList.get(i+areaIndexForConstruction).text());
	    }
	}
	return ""+area;
    }
    
    public String getConstructionUValue(String cons){
	Elements constructionList = envelopeTable.get(0).getElementsByTag("td");
	for(int i=0; i<constructionList.size(); i++){
	    if(constructionList.get(i).text().equalsIgnoreCase(cons)){
		return constructionList.get(i+uvalueIndexForConstruction).text();
	    }
	}
	return "";
    }
    
    public String getConstructionVisibleTransmittance(String cons){
	Elements constructionList = envelopeTable.get(0).getElementsByTag("td");
	for(int i=0; i<constructionList.size(); i++){
	    if(constructionList.get(i).text().equalsIgnoreCase(cons)){
		return constructionList.get(i+vtIndexForConstruction).text();
	    }
	}
	return "";
    }
    
    public String getConstructionSHGC(String cons){
	Elements constructionList = envelopeTable.get(0).getElementsByTag("td");
	for(int i=0; i<constructionList.size(); i++){
	    if(constructionList.get(i).text().equalsIgnoreCase(cons)){
		return constructionList.get(i+shgcIndexForConstruction).text();
	    }
	}
	return "";
    }
}
