package eplus;

import java.text.DecimalFormat;
import java.util.HashMap;

import eplus.htmlparser.EnergyPlusHTMLParser;

public class UnitaryHVACAnalyzer {
    private final IdfReader reader;
    private final EnergyPlusHTMLParser parser;
    //private HashMap<String, Unitary> unitaryMap;
    
    private final int stringArraySize = 2;
    private final DecimalFormat df = new DecimalFormat("###.##");
    

}
