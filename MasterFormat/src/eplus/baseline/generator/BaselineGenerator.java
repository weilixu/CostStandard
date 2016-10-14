package eplus.baseline.generator;

import java.io.File;
import java.util.ArrayList;

import baseline.generator.Generator;
import baseline.idfdata.BaselineInfo;
import baseline.util.ClimateZone;

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

    public BaselineGenerator() {
	pathList = new ArrayList<String>();
	// assume the energyplusfiles is from a default route
	energyPlusFile = null;
	weatherFile = null;
	zone = ClimateZone.CLIMATEZONE5A;

	buildingFunction = "Commercial";
	energyPlusAuthor = "DesignBuilder";

	buildingCondition = false;
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
		Generator gen = new Generator(energyPlusFile, weatherFile, zone,
			buildingFunction, buildingCondition, energyPlusAuthor);
		BaselineInfo info = gen.getBaselineInfo();
		Double baselineCost = info.getOperationCost();
		
	    }
	}
    }
}
