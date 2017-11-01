package eplus.construction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.nodes.Document;

import eplus.IdfReader;
import eplus.IdfReader.ValueNode;
import masterformat.api.AbstractMasterFormatComponent;

public class ShadingControl extends AbstractMasterFormatComponent
	implements BuildingComponent {
    
    private static final String shadecontrol = "WindowProperty:ShadingControl";
    private static final String construction = "Construction";
    private static final String windowmaterial="WindowMaterial:Blind";
    
    private String[] selectedComponents = null;
    
    public ShadingControl(){
	selectedComponents = new String[2];
	selectedComponents[0] = "Yes";
	selectedComponents[1] = "No";
	
    }

    @Override
    public void selectCostVector() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setUserInputs(HashMap<String, String> userInputsMap) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setVariable(String[] surfaceProperties) {
	// TODO Auto-generated method stub

    }

    @Override
    public String getName() {
	return "ShadingControls";
    }

    @Override
    public String[] getListAvailableComponent() {
	return null;
    }

    @Override
    public void setRangeOfComponent(String[] componentList) {
	
    }

    @Override
    public String[] getSelectedComponents() {
	return selectedComponents;
    }

    @Override
    public String[] getSelectedComponentsForRetrofit() {
	return null;
    }

    @Override
    public String getSelectedComponentName(int Index) {
	
	return selectedComponents[Index];
    }

    @Override
    public void writeInEnergyPlus(IdfReader reader, String component) {
	// create constructions and put into eplusFile
	//String shadingControlName = component.split(":")[1];
	if(!component.equals("No")){
	    //get materials
	    String[] materialDes={"Name","Slat Orientation", "Slat Width","Slat Separation", "Slat Thickness",
		    "Slat Angle","Slat Conductivity","Slat Beam Solar Transmittance","Front Side Slat Beam Solar Reflectance",
		    "Back Side Slat Beam Solar Reflectance","Slat Diffuse Solar Transmittance","Front Side Slat Diffuse Solar Reflectance",
		    "Back Side Slat Diffuse Solar Reflectance","Slat Beam Visible Transmittance","Front Side Slat Beam Visible Reflectance","Back Side Slat Beam Visible Reflectance",
		    "Slat Diffuse Visible Transmittance","Front Side Slat Diffuse Visible Reflectance","Back Side Slat Diffuse Visible Reflectance","Slat Infrared Hemispherical Transmittance","Front Side Slat Infrared Hemispherical EMissivity",
		    "Back Side Slat Infrared Hemispherical Emissivity","Blind to Glass Distance","Blind Top Opening Multiplier",
		    "Blind Bottom Opening Multiplier","Blind Left Side Opening Multiplier","Blind Right Side Opeining Multiplier","Minimum Slat Angle","Maximum Slat Angle"};
	    String[] materialValue = {"Typical Blind","Horizontal","0.25","0.25","0.01","15","0.9","0","0.5","0.5","0","0.5","0.5","0","0.5","0.5","0","0.5","0.5","0","0.9","0.9","1","0.5","0.5","0.5","0.5","0","180"};
	    reader.addNewEnergyPlusObject(windowmaterial, materialValue, materialDes);

	    String[] shadingControl={"Name","Shading Type","Construction with Shading Name","Shading Control Type","Schedule Name","Setpoint","Setpoint Control Is Scheduled","Glare Control Is Active",
		"Shading Device Material Name","Type of Slat Angle Control for Blinds","Slat Angle Schedule Name"};
	    String[] shadingValue={"Blind Control","ExteriorBlind","","OnIfHighSolarOnWindow","","130","No","No","Typical Blind","FixedSlatAngle",""};
	    reader.addNewEnergyPlusObject(shadecontrol, shadingValue,shadingControl);
	   // System.out.println(Arrays.toString(shadingValue));
	    //now search through the fenestrations and change each of them
	    HashMap<String, ArrayList<ValueNode>> fene = reader.getObjectListCopy("FenestrationSurface:Detailed");
	    Iterator<String> feneList = fene.keySet().iterator();
	    while(feneList.hasNext()){
		String feneId = feneList.next();
		ArrayList<ValueNode> feneArray = fene.get(feneId);
		
		for(ValueNode vn: feneArray){
		    if(vn.getDescription().equals("Shading Control Name")){
			//System.out.println(vn.getAttribute());
			vn.setAttribute("Blind Control");
		    }
		}
	    }
	}else{
	    String[] materialDes={"Name","Slat Orientation", "Slat Width","Slat Separation", "Slat Thickness",
		    "Slat Angle","Slat Conductivity","Slat Beam Solar Transmittance","Front Side Slat Beam Solar Reflectance",
		    "Back Side Slat Beam Solar Reflectance","Slat Diffuse Solar Transmittance","Front Side Slat Diffuse Solar Reflectance",
		    "Back Side Slat Diffuse Solar Reflectance","Slat Beam Visible Transmittance","Front Side Slat Beam Visible Reflectance","Back Side Slat Beam Visible Reflectance",
		    "Slat Diffuse Visible Transmittance","Front Side Slat Diffuse Visible Reflectance","Back Side Slat Diffuse Visible Reflectance","Slat Infrared Hemispherical Transmittance","Front Side Slat Infrared Hemispherical EMissivity",
		    "Back Side Slat Infrared Hemispherical Emissivity","Blind to Glass Distance","Blind Top Opening Multiplier",
		    "Blind Bottom Opening Multiplier","Blind Left Side Opening Multiplier","Blind Right Side Opeining Multiplier","Minimum Slat Angle","Maximum Slat Angle"};
	    String[] materialValue = {"Typical Blind","Horizontal","0.25","0.25","0.01","15","0.9","0","0.5","0.5","0","0.5","0.5","0","0.5","0.5","0","0.5","0.5","0","0.9","0.9","1","0.5","0.5","0.5","0.5","0","180"};
	    reader.addNewEnergyPlusObject(windowmaterial, materialValue, materialDes);
	    
	    HashMap<String, ArrayList<ValueNode>> fene = reader.getObjectListCopy("FenestrationSurface:Detailed");
	    Iterator<String> feneList = fene.keySet().iterator();
	    while(feneList.hasNext()){
		String feneId = feneList.next();
		ArrayList<ValueNode> feneArray = fene.get(feneId);
		
		for(ValueNode vn: feneArray){
		    if(vn.getDescription().equals("Shading Control Name")){
			//System.out.println(vn.getAttribute());
			vn.setAttribute("");
		    }
		}
	    }
	}
    }

    @Override
    public double getComponentCost(Document doc) {
	return 97.0 * 75 * 120;//75 motorized, 185 blind cost
    }
    

    @Override
    public boolean isIntegerTypeComponent() {
	return true;
    }

    @Override
    public int getNumberOfVariables() {
	return 1;
    }

    @Override
    public void readsInProperty(HashMap<String, Double> shelfProperty,
	    String component) {
	// TODO Auto-generated method stub
	
    }
}
