package eplus;

import java.util.Arrays;

/**
 * This class describes the thermal zones described from the DesignBuidler
 * exported idf file. The thermal zone naming has to be exactly follows the
 * naming schema:
 * 
 * FLOOR:FUNCTION:IDENTIFIER:HVACZONE
 * 
 * <value> floor <value> indicates which floor this thermal zone resides <value>
 * function <value> defines the zone functions e.g. office <value> hvac <value>
 * defines the HVAC zone this thermal zone belongs to. <value> identifier<value>
 * defines the zone number, e.g. 101
 * 
 * @author Weili
 *
 */
public class ThermalZones {

    private String zoneName;
    private String floor;
    private String function;
    private String hvac;
    private String identifier;

    public ThermalZones(String zone) {
	zoneName = zone;

	// according to DesignBuilder export block and zone is seperated by :
	String[] firstSplit = zoneName.split(":");
	if (firstSplit.length <= 1) {
	    floor = null;
	    // condition when there is no floor name exist
	    initializeZone(firstSplit[0]);
	} else {
	    floor = firstSplit[0];
	    initializeZone(firstSplit[1]);
	}
    }
    
    /*
     * check whether it has floor
     */
    public boolean hasFloor() {
	return floor != null;
    }
    
    /*
     * check whether it has function
     */
    public boolean hasFunction() {
	return function != null;
    }
    
    /*
     * check whether has HVAC
     */
    public boolean hasHVAC() {
	return hvac != null;
    }
    
    /*
     * check whether it has identifier
     */
    public boolean hasIdentifier() {
	return identifier != null;
    }
    
    /*
     * retrieve floor
     */
    public String getFloor() {
	if (hasFloor()) {
	    return floor;
	}
	return "";
    }
    
    /*
     * retrieve zone function
     */
    public String getFunction() {
	if (hasFunction()) {
	    return function;
	}
	return "";
    }
    
    /*
     * retrieve hvac zone
     */
    public String getHVAC() {
	if (hasHVAC()) {
	    return hvac;
	}
	return "";
    }
    
    /*
     * retrieve zone identifier
     */
    public String getIdentifier() {
	if (hasIdentifier()) {
	    return identifier;
	}
	return "";
    }

    
    private void initializeZone(String nameList) {
	String[] secondSplit = nameList.split("%");
	if (secondSplit.length <= 1) {
	    function = secondSplit[0];
	    hvac = null;
	    identifier = null;
	} else if (secondSplit.length == 2) {
	    function = secondSplit[0];
	    hvac = secondSplit[1];
	    identifier = null;
	} else {
	    function = secondSplit[0];
	    hvac = secondSplit[secondSplit.length - 1];
	    identifier = concatString(Arrays.copyOfRange(secondSplit, 1,
		    secondSplit.length - 2));
	}
    }

    private String concatString(String[] stringList) {
	StringBuffer sb = new StringBuffer();
	for (String s : stringList) {
	    sb.append(s);
	}
	return sb.toString();
    }
}
