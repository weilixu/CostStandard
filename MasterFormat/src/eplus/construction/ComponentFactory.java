package eplus.construction;

import java.util.ArrayList;
import java.util.List;

import eplus.EnergyPlusBuildingForHVACSystems;

public final class ComponentFactory {
    
    public static List<BuildingComponent> getFullComponentList(
	    EnergyPlusBuildingForHVACSystems bldg) {
	ArrayList<BuildingComponent> componentList = new ArrayList<BuildingComponent>();
	ExteriorWall ew = new ExteriorWall();
	Roof rf = new Roof();
	Lighting lt = new Lighting();
	Window wd = new Window();
	HVACSimple hvac = new HVACSimple(bldg);
	componentList.add(ew);
	componentList.add(wd);
	componentList.add(rf);
	componentList.add(new DaylightSensor());
	componentList.add(hvac);
	componentList.add(new WindowWallRatio("N"));
	//componentList.add(new WindowWallRatio("S"));
	componentList.add(new WindowWallRatio("E"));
	componentList.add(new WindowWallRatio("W"));
	componentList.add(lt);
	return componentList;
    }
    
    public static List<BuildingComponent> getPartialComponentList(EnergyPlusBuildingForHVACSystems bldg){
	ArrayList<BuildingComponent> componentList = new ArrayList<BuildingComponent>();
	ExteriorWall ew = new ExteriorWall();
	String[] EWAvailComponents = ew.getListAvailableComponent();
	ArrayList<String> temp = new ArrayList<String>();
	for(int i=0; i<EWAvailComponents.length; i++){
	    String ews = EWAvailComponents[i];
	    if(ews.startsWith("Precast and CIP Walls")){
		temp.add(ews);
	    }
	}
	String[] selectedComponents = new String[temp.size()];
	ew.setRangeOfComponent(temp.toArray(selectedComponents));
	
	Roof rf = new Roof();
	String[] RFAvailComponents = rf.getListAvailableComponent();
	ArrayList<String> temp2 = new ArrayList<String>();
	for(int i=0; i<RFAvailComponents.length; i++){
	    String rfs = RFAvailComponents[i];
	    if(rfs.startsWith("METAL DECK ROOFS")){
		temp2.add(rfs);
	    }
	}
	String[] selectedComponents2 = new String[temp2.size()];
	rf.setRangeOfComponent(temp2.toArray(selectedComponents2));
	
	Lighting lt = new Lighting();
	Window wd = new Window();
	HVACSimple hvac = new HVACSimple(bldg);
	
	componentList.add(ew);
	componentList.add(wd);
	componentList.add(rf);
	componentList.add(new DaylightSensor());
	componentList.add(hvac);
	componentList.add(new WindowWallRatio("N"));
	//componentList.add(new WindowWallRatio("S"));
	componentList.add(new WindowWallRatio("E"));
	componentList.add(new WindowWallRatio("W"));
	componentList.add(lt);
	
	return componentList;
    }
}
