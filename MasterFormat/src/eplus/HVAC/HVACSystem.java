package eplus.HVAC;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;

import baseline.generator.EplusObject;

public interface HVACSystem {
    public HashMap<String, ArrayList<EplusObject>> getSystemData();
    
    public double getTotalLoad(Document doc);
    
    public String getSystemName();
    
    public int getNumberOfSupplySystem();
    
    public int getNumberOfDemandSystem();
}
