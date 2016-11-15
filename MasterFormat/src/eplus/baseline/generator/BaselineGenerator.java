package eplus.baseline.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import baseline.generator.BaselineSetting;
import baseline.generator.Generator;
import baseline.idfdata.BaselineInfo;
import baseline.util.ClimateZone;
import baseline.util.parallel.BaselineParallel;
import baseline.util.parallel.MultithreadedBaselineSimulator;

public class BaselineGenerator {

    private ArrayList<String> pathList;
    // private Generator baselineGen;

    // related input data
    private File energyPlusFile;
    private File weatherFile;
    private ClimateZone zone;

    private String buildingFunction;
    private String energyPlusAuthor;

    private boolean buildingCondition;
    
    private BaselineParallel parallelEvaluator;

    public BaselineGenerator() {
	pathList = new ArrayList<String>();
	// assume the energyplusfiles is from a default route
	energyPlusFile = null;
	weatherFile = null;
	zone = ClimateZone.CLIMATEZONE5A;

	buildingFunction = "Commercial";
	energyPlusAuthor = "DesignBuilder";

	buildingCondition = false;
	parallelEvaluator = new MultithreadedBaselineSimulator(4);
    }

    /**
     * paths cannot be empty arraylsit
     * 
     * @param paths
     * @param weatherPath
     * @param zone
     */
    public BaselineGenerator(ArrayList<String> paths, String weatherPath,
	    ClimateZone zone) {
	// set to the first document
	energyPlusFile = new File(paths.get(0));
	weatherFile = new File(weatherPath);
	this.zone = zone;

	buildingFunction = "Commercial";
	energyPlusAuthor = "DesignBuilder";

	buildingCondition = false;
	parallelEvaluator = new MultithreadedBaselineSimulator(4);
    }

    public BaselineGenerator(ArrayList<String> paths, String weatherPath,
	    ClimateZone zone, String function, String author,
	    Boolean condition) {
	// set to the first document
	energyPlusFile = new File(paths.get(0));
	weatherFile = new File(weatherPath);
	this.zone = zone;

	buildingFunction = function;
	energyPlusAuthor = author;
	buildingCondition = condition;
	parallelEvaluator = new MultithreadedBaselineSimulator(4);
    }

    public void setEnergyPlusFile(String path) {
	energyPlusFile = new File(path);
    }

    public void setWeatherFile(String path) {
	weatherFile = new File(path);
    }

    public void setClimateZone(ClimateZone zone) {
	this.zone = zone;
    }

    public void setBuildingFunction(String function) {
	if (function.equals("Residential")) {
	    buildingFunction = function;
	} else {
	    buildingFunction = "Commercial";
	}
    }

    public void setAuthor(String author) {
	energyPlusAuthor = author;
    }

    public void setBuildingCondition(boolean condition) {
	buildingCondition = condition;
    }

    public void runBaselineModels() {
	if (!pathList.isEmpty() && weatherFile != null) {
	    for (String path : pathList) {
		energyPlusFile = new File(path);
		
		BaselineSetting setting = new BaselineSetting();
		setting.setBaselineInfo(new BaselineInfo());
		setting.setExisting(buildingCondition);
		setting.setIdfFile(energyPlusFile);
		setting.setTool(energyPlusAuthor);
		setting.setType(buildingFunction);
		setting.setWeatherFile(weatherFile);
		setting.setZone(zone);
		
		parallelEvaluator.addBaselineForEvaluation(setting);
	    }
	    List<BaselineSetting> solutionList = parallelEvaluator.parallelEvaluation();
	    
	    for(BaselineSetting bs : solutionList){
		Double cost = bs.getBaselineInfo().getOperationCost();
	    }
	}
	parallelEvaluator.stopEvaluator();
    }
}
