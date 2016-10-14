package eplus.htmlparser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FenestrationParser {
    
    private final int azimuthDir = 14;
    private final int cardinalDir = 16;
    private final Document doc;
    private final Elements feneSummary;
    
    private static final String FENE_SUMMARY = "Envelope Summary%Exterior Fenestration";
    private static final String TAG = "tableID";
    
    public FenestrationParser(Document d){
	doc = d;
	feneSummary = doc.getElementsByAttributeValue(TAG, FENE_SUMMARY);
    }
    
    public String getFenestrationOrientation(String fene){
	Elements feneList = feneSummary.get(0).getElementsByTag("td");
	for(int i=0;  i<feneList.size(); i++){
	    if(feneList.get(i).text().equalsIgnoreCase(fene)){
		return (feneList.get(i+cardinalDir).text());
	    }
	}
	return null;
    }
    
    public String getFenestrationAzimuth(String fene){
	Elements feneList = feneSummary.get(0).getElementsByTag("td");
	for(int i=0;  i<feneList.size(); i++){
	    if(feneList.get(i).text().equalsIgnoreCase(fene)){
		return (feneList.get(i+azimuthDir).text());
	    }
	}
	return null;
    }
}
