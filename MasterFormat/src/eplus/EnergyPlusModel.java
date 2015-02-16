package eplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import eplus.MaterialAnalyzer.Material;

public class EnergyPlusModel {

    private final IdfReader idfDomain;

    private MaterialAnalyzer materialModule;
    private HashMap<String, ArrayList<Material>> materialData;

    private final File eplusFile;
    
    private final String[] domainList = {"None","Construction"};
    
    public EnergyPlusModel(File file) {
	eplusFile = file;
	idfDomain = new IdfReader();
	idfDomain.setFilePath(eplusFile.getAbsolutePath());
	try {
	    idfDomain.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	setUpMaterialAnalyzer();
    }
    
    public String[] getDomainList(){
	return domainList;
    }
    
    public ArrayList<Material> getMaterialList(String construction){
	return materialData.get(construction);
    }
    
    public String[] getConstructionList(){
	Set<String> constructions = materialData.keySet();
	String[] consArray = new String[constructions.size()];
	Iterator<String> consIterator = constructions.iterator();
	
	int counter = 0;
	while(consIterator.hasNext()&& counter<constructions.size()){
	    consArray[counter] = consIterator.next();
	    counter++;
	}
	return consArray;
    }
    
    private void setUpMaterialAnalyzer() {
	materialModule = new MaterialAnalyzer(idfDomain);
	materialData = materialModule.getMaterialData();
    }
       
    public void testPrinter(){
	materialData = materialModule.getMaterialData();
	
	Set<String> construction = materialData.keySet();
	Iterator<String> constructionIterator = construction.iterator();
	while(constructionIterator.hasNext()){
	    String name = constructionIterator.next();
	    System.out.print(name);
	    System.out.println();
	    ArrayList<Material> materialList = materialData.get(name);
	    for(Material m: materialList){
		System.out.print(" "+m.getName());
		System.out.print(" "+Arrays.toString(m.getProperties()));
		System.out.println();
	    }
	}
    }
}
