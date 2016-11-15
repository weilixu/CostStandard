package eplus.htmlparser;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class ZoneHTMLParser {
    
    private static Document doc;
    
    //All Summary tables
    private static ZoneSummaryParser zoneSummary;
    private static FenestrationParser feneSummary;
    private static EnvelopeSummary surfaceSummary;
    /**
     * process the sizing results
     * @param html
     */
    public static void processOutputs(File html){
	try {
	    doc = Jsoup.parse(html, "UTF-8");
	    preprocessTable();
	    zoneSummary = new ZoneSummaryParser(doc);
	    feneSummary = new FenestrationParser(doc);
	    surfaceSummary = new EnvelopeSummary(doc);
	} catch (IOException e) {
	    // do nothing
	}
    }
    
    public static Double getZoneArea(String zone){
	Double area = zoneSummary.getZoneArea(zone);
	return area;
    }
    
    public static String getSurfaceOrientation(String surface){
	String orientation = surfaceSummary.getSurfaceOrientation(surface);
	return orientation;
    }
    
    public static double getSurfaceArea(String surface){
	double area = surfaceSummary.getSurfaceArea(surface);
	return area;
    }
    
    public static String getFenestrationOrientation(String fene){
	return feneSummary.getFenestrationOrientation(fene);
    }
    
    public static String getFenestrationAzimuth(String fene){
	return feneSummary.getFenestrationAzimuth(fene);
    }
    
    private static void preprocessTable() {
	String report = null;
	Elements htmlNodes = doc.getAllElements();
	for (int i = 0; i < htmlNodes.size(); i++) {
	    if (htmlNodes.get(i).text().contains("Report:")) {
		report = htmlNodes.get(i + 1).text();
	    }
	    if (htmlNodes.get(i).hasAttr("cellpadding")) {
		String tableName = htmlNodes.get(i - 3).text();
		htmlNodes.get(i).attr("tableID", report + "%" + tableName);
	    }
	}
    }
    
}
