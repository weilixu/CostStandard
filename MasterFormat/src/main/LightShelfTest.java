package main;

import java.io.IOException;
import java.util.HashMap;

import eplus.IdfReader;
import eplus.construction.LightShelf;

public class LightShelfTest {
    
    public static void main(String[] args){
	String path = "";
	IdfReader reader = new IdfReader();
	reader.setFilePath(path);
	
	try {
	    reader.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	HashMap<String, Double> shelfProf = new HashMap<String, Double>();
	shelfProf.put("InsideShelfDepth",0.2);
	shelfProf.put("InsideShelfHeight", 1.0);
	LightShelf ls = new LightShelf(shelfProf,"South");
	
	ls.writeInEnergyPlus(reader, "");
	reader.WriteIdf("", "output");
    }
}
