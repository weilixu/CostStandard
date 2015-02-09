package eplus.htmlparser;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BuildingAreaParser {
    
    private File htmlFile;
    private Document doc;
    
    
    public BuildingAreaParser(File f){
	htmlFile = f;
	
	try{
	    doc = Jsoup.parse(htmlFile, "UTF-8");
	}catch(IOException e){
	    // do nothing
	}
    }
    
    public double getBuildingArea(){
	double area;
	Elements tables = doc.getElementsByTag("table");
	for(Element table: tables){
	    Elements texts = table.getAllElements();
	    for(int i=0; i<texts.size(); i++){
		if(texts.get(i).getElementsByTag("td").text().equals("Total Building Area")){
		    area = Double.parseDouble(texts.get(i+1).text());
		    return area;
		}
	    }
	}
	return -1;
    }

}
