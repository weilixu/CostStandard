package eplus.lifecyclecost;

import java.util.ArrayList;

/**
 * This class groups the objects to represents one particular set of data in EnergyPlus
 * @author Weili
 *
 */
public class DataObjects {
    
    private final ArrayList<TemplateObject> objects;
    private final String setName;
    
    public DataObjects(String set){
	objects = new ArrayList<TemplateObject>();
	this.setName = set;
    }
    
    public String getSetName(){
	return setName;
    }
    
    public void addObject(TemplateObject object){
	objects.add(object);
    }
    
    public ArrayList<TemplateObject> getObjects(){
	return objects;
    }
    
    @Override
    public String toString(){
	return setName;
    }

}
