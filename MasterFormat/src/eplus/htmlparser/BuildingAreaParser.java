package eplus.htmlparser;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTML parser that searches for the building area in the result HTML file
 * 
 * @author Weili
 *
 */
public class BuildingAreaParser {

    private File htmlFile;
    private Document doc;

    /**
     * constructor that builds the HTML file
     * 
     * @param f
     */
    public BuildingAreaParser(File f) {
	htmlFile = f;

	try {
	    doc = Jsoup.parse(htmlFile, "UTF-8");
	} catch (IOException e) {
	    // do nothing
	}
	preprocessTable();
    }

    private void preprocessTable() {
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

    /**
     * gets the building area
     * 
     * @return
     */
    public double getBuildingArea() {
	double area;
	Elements tables = doc.getElementsByTag("table");
	for (Element table : tables) {
	    Elements texts = table.getAllElements();
	    for (int i = 0; i < texts.size(); i++) {
		if (texts.get(i).getElementsByTag("td").text()
			.equals("Total Building Area")) {
		    area = Double.parseDouble(texts.get(i + 1).text());
		    return area;
		}
	    }
	}
	return -1;
    }

    /**
     * gets the fan summary
     */
    public void getFanSummary(String fanName) {
	Double efficiency = null;
	Double delta = null;
	Double flow = null;
	Elements nodeList = doc.getElementsByAttributeValue("tableID",
		"Equipment Summary%Fans");
	for (int i = 0; i < nodeList.size(); i++) {
	    Elements fanList = nodeList.get(i).getElementsByTag("td");
		for(int j=0; j<fanList.size(); j++){
		    System.out.println(fanList.get(j).text());
		    if(fanList.get(j).text().equals(fanName)){
			efficiency = Double.parseDouble(fanList.get(j + 2).text());
			delta = Double.parseDouble(fanList.get(j + 3).text());
			flow = Double.parseDouble(fanList.get(j + 4).text());
			break;
		}
	    }
	}

	System.out.println(efficiency + " " + delta + " " + flow);
    }

    public static void main(String[] args) {
	File newFile = new File(
		"C:\\Users\\Weili\\Desktop\\New folder\\CostTestSimulation\\testHTMLTable.html");
	BuildingAreaParser parser = new BuildingAreaParser(newFile);
	parser.getFanSummary("VAV_1_FAN");
    }
}
