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
	componentList.add(lt);
	componentList.add(hvac);
	componentList.add(new DaylightSensor());
	return componentList;
    }
}
