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
	//componentList.add(new DaylightSensor());
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
	componentList.add(new WindowWallRatio("S"));
	componentList.add(new WindowWallRatio("E"));
	componentList.add(new WindowWallRatio("W"));
	componentList.add(new PVEnergy("N"));
	componentList.add(new PVEnergy("S"));
	componentList.add(new PVEnergy("E"));
	componentList.add(new PVEnergy("W"));
	
	componentList.add(lt);
	
	//numerical values
	componentList.add(new LightShelf());
	
	return componentList;
    }
    
    public static List<BuildingComponent> getPartialComponentListForRetrofit(EnergyPlusBuildingForHVACSystems bldg){
	ArrayList<BuildingComponent> componentList = new ArrayList<BuildingComponent>();
//	ExteriorWall ew = new ExteriorWall();
//	String[] EWAvailComponents = ew.getListAvailableComponent();
//	ArrayList<String> temp = new ArrayList<String>();
//	for(int i=0; i<EWAvailComponents.length; i++){
//	    String ews = EWAvailComponents[i];
//	    if(ews.startsWith("Precast and CIP Walls")){
//		temp.add(ews);
//	    }
//	}
	
//	String[] selectedComponents = new String[temp.size()];
//	ew.setRangeOfComponent(temp.toArray(selectedComponents));
	
//	Roof rf = new Roof();
//	String[] RFAvailComponents = rf.getListAvailableComponent();
//	ArrayList<String> temp2 = new ArrayList<String>();
//	for(int i=0; i<RFAvailComponents.length; i++){
//	    String rfs = RFAvailComponents[i];
//	    if(rfs.startsWith("METAL DECK ROOFS")){
//		temp2.add(rfs);
//	    }
//	}
//	String[] selectedComponents2 = new String[temp2.size()];
//	rf.setRangeOfComponent(temp2.toArray(selectedComponents2));
	
	Insulation ins = new Insulation(3.0);
	ins.getSelectedComponentsForRetrofit();
	Lighting lt = new Lighting();
	lt.getSelectedComponentsForRetrofit();
	Window wd = new Window();
	wd.getSelectedComponentsForRetrofit();
	//HVACSimple hvac = new HVACSimple(bldg);
	
	HeatingUpdate heat = new HeatingUpdate();
	CoolingUpdate cool = new CoolingUpdate();
	
	//componentList.add(ew);
	componentList.add(ins);
	componentList.add(wd);
	//componentList.add(rf);
	componentList.add(new DaylightSensor());
	//componentList.add(hvac);
	componentList.add(heat);
	componentList.add(cool);
	//omponentList.add(new WindowWallRatio("N"));
	//componentList.add(new WindowWallRatio("S"));
	//componentList.add(new WindowWallRatio("E"));
	//componentList.add(new WindowWallRatio("W"));
	//componentList.add(new PVEnergy("N"));
	componentList.add(new PVEnergy("S"));
	componentList.add(new PVEnergy("E"));
	componentList.add(new PVEnergy("W"));
	
	componentList.add(lt);
	
	//numerical values
	componentList.add(new LightShelf());
	
	return componentList;
    }
    
    public static int getNumberOfVariable(List<BuildingComponent> componentList){
	int IntegerVariables = 0;
	int NumericalVariables = 0;
	
	for(int i=0; i<componentList.size(); i++){
	    if(componentList.get(i).isIntegerTypeComponent()){
		IntegerVariables += componentList.get(i).getNumberOfVariables();
	    }else{
		NumericalVariables += componentList.get(i).getNumberOfVariables();
	    }
	}
	return IntegerVariables + NumericalVariables;
    }
}
