package eplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eplus.MaterialAnalyzer.Material;
import masterformat.api.MasterFormat;
import masterformat.listener.CostTableListener;
import masterformat.standard.model.MasterFormatModel;

public class EnergyPlusModel {

    private final IdfReader idfDomain;
    private final MasterFormatModel masterformat;

    private MaterialAnalyzer materialModule;

    private final File eplusFile;
    private final File parentFolder;

    private final String[] domainList = { "None", "Construction" };
    private String[][] costData;
    private final String componentCostDescription = "Name:Type:Line Item Type:Item Name:Object End-Use Key:Cost per Each:Cost per Area:"
	    + "Cost per Unit of Output Capacity:Cost per Unit of Output Capacity per COP:Cost per Volume:Cost per Volume Rate:Cost per Energy per Temperature Difference"
	    + ":Quantity";
    private final String componentCostObject="ComponentCost:LineItem";
    //count the generated idf file
    private Integer generatedCounter = 0;

    private List<CostTableListener> tableListeners;

    public EnergyPlusModel(File file) {
	eplusFile = file;
	parentFolder = eplusFile.getParentFile();
	masterformat = new MasterFormatModel();
	tableListeners = new ArrayList<CostTableListener>();

	idfDomain = new IdfReader();
	idfDomain.setFilePath(eplusFile.getAbsolutePath());
	try {
	    idfDomain.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	setUpMaterialAnalyzer();
    }

    public void addTableListener(CostTableListener ct) {
	tableListeners.add(ct);
    }

    public String[] getDomainList() {
	return domainList;
    }

    public ArrayList<Material> getMaterialList(String construction) {
	return materialModule.getMaterialList(construction);
    }

    public String[] getConstructionList() {
	return materialModule.getConstructionList();
    }

    public String[][] getMaterialTableData(String cons) {
	return materialModule.getCostListForConstruction(cons);
    }

    public void setMasterFormat(String type, String description,
	    String construction, Integer index) {
	MasterFormat mf = masterformat.getUserInputFromMap(type, description);
	materialModule.getMaterialList(construction).get(index).setMaterial(mf);
    }

    public ArrayList<String> getUserInputs(String construction, Integer index) {
	return materialModule.getMaterialList(construction).get(index)
		.getUserInputs();
    }

    // temp, only has materials
    public void getCostVector(String item) {
	costData = materialModule.getCostListForConstruction(item);
	updateCostVectorInformation();
    }

    public void setUserInput(HashMap<String, String> map, String construction,
	    Integer index) {
	materialModule.setUserInput(map, construction, index);
	getCostVector(construction);
    }

    public void addTotalCostToComponentCost(String item, String category){
	Integer totalCostIndex = 4;
	Double cost = Double.parseDouble(costData[costData.length-1][totalCostIndex]);
	
	String[] description = componentCostDescription.split(":");
	if(category.equalsIgnoreCase("CONSTRUCTION")){
	    String[]value = {item.toUpperCase(),"",category,item,"","",cost.toString(),"","","","","",""};
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value, description);
	}
    }
    
    public void addTotalOPCostToComponentCost(String item, String category){
	Integer totalCostIndex = 5;
	Double cost = Double.parseDouble(costData[costData.length-1][totalCostIndex]);
	
	String[] description = componentCostDescription.split(":");
	if(category.equalsIgnoreCase("CONSTRUCTION")){
	    String[]value = {item.toUpperCase(),"",category,item,"","",cost.toString(),"","","","","",""};
	    idfDomain.addNewEnergyPlusObject(componentCostObject, value, description);
	}
    }
    
    public void writeIdf(){
	idfDomain.WriteIdf(parentFolder.getAbsolutePath(), generatedCounter.toString());
	generatedCounter++;
    }

    private void setUpMaterialAnalyzer() {
	materialModule = new MaterialAnalyzer(idfDomain);
    }

    private void updateCostVectorInformation() {
	for (CostTableListener ct : tableListeners) {
	    ct.onCostTableUpdated(costData);
	}
    }
}
