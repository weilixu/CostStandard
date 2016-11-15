package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import eplus.IdfReader;
import eplus.construction.LightShelf;
import eplus.htmlparser.ZoneHTMLParser;

public class LightShelfTest {
    
    public static void main(String[] args){
	String path = "E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\LightShelfTest\\2.idf";
	IdfReader reader = new IdfReader();
	reader.setFilePath(path);
	ZoneHTMLParser.processOutputs(new File("E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\LightShelfTest\\2Table.html"));
	
	try {
	    reader.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	HashMap<String, Double> shelfProf = new HashMap<String, Double>();
	shelfProf.put("InsideShelfDepth",0.2);
	shelfProf.put("InsideShelfHeight", 0.3);
	shelfProf.put("OutsideShelfDepth", 0.4);
	shelfProf.put("OutsideShelfHeight", 0.3);
	LightShelf ls = new LightShelf(shelfProf,"S");
	
	//ls.writeInEnergyPlus(reader, "LightShelf");
	//reader.WriteIdf("E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\LightShelfTest", "output");
    }
}
