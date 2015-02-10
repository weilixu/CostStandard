package eplus.lifecyclecost;

import java.util.ArrayList;

public class TemplateObject {
    
    private ArrayList<FieldElement> fieldLists;
    
    private final String object;
    private final String reference;
    
    public TemplateObject(String object, String reference){
	this.object = object;
	this.reference = reference;
	fieldLists = new ArrayList<FieldElement>();
    }
    
    public void insertFieldElement(FieldElement fe){
	fieldLists.add(fe);
    }
    
    public ArrayList<FieldElement> getFieldList(){
	return fieldLists;
    }
    
    public String getObject(){
	return object;
    }
}
