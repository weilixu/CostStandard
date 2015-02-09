package eplus.htmlparser;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ResultsParser {

    public static void main(String[] args) {
	File input = new File(
		"C://Users//Weili//Desktop//New folder//Results//0Table.html");
	Document doc = null;

	try {
	    doc = Jsoup.parse(input, "UTF-8");

	} catch (IOException e) {
	    // do nothing
	}

	Elements links = doc.getElementsByTag("table");
	for (Element link : links) {
	    Elements children = link.getAllElements();
	    for (int i=0; i<children.size(); i++) {
		if (children.get(i).getElementsByTag("td").text().equals("Total Building Area")) {
		    System.out.println(children.get(i+1).text());
		}
	    }
	}
    }

}
