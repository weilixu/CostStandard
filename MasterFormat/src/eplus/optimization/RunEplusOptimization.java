package eplus.optimization;

import java.io.File;

import masterformat.api.DatabaseUtils;
import eplus.IdfReader;

public class RunEplusOptimization {
    private static final String EPLUSBAT = "RunEplus.bat";
    
    private File folder;
    private File eplusFolder;
    private Integer simulationCount;
    
    private IdfReader idfData;
    
    private String energyplus_dir;
    private String weather_dir;
    
    public RunEplusOptimization(IdfReader reader){
	idfData = reader;
	
	String[] config = DatabaseUtils.getEplusConfig();
	energyplus_dir = config[0];
	weather_dir = config[1];
    }
    
    public void setSimulationTime(Integer simulationTime) {
	simulationCount = simulationTime;
    }
    
}
