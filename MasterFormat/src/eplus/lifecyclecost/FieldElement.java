package eplus.lifecyclecost;

import java.util.ArrayList;

public class FieldElement {

    // used when this field element is a key and has options for client to
    // choose
    private ArrayList<String> optionList;
    private String description;
    private String type;
    
    private String minimum;
    private String maximum;
    
    private String value;
    
    public FieldElement(String description, String type){
	this.description = description;
	this.type = type;
	optionList = new ArrayList<String>();
    }
    
    /**
     * if this is a key element, and requires client to select multiple choices,
     * insert the choice into this field
     * @param option
     */
    public void insertOptions(String option){
	optionList.add(option);
    }
    
    /**
     * set the minimum limitation
     * @param minimum
     */
    public void setMinimum(String minimum){
	this.minimum = minimum;
    }
    
    /**
     * set the maximum limitation
     * @param maximum
     */
    public void setMaximum(String maximum){
	this.maximum = maximum;
    }
    
    /**
     * set a value for this field
     * @param value
     */
    public void setValue(String value){
	this.value = value;
    }
    
    /**
     * get the option list for display
     * @return
     */
    public ArrayList<String> getOptionList(){
	for(String s: optionList){
	    System.out.println("This is the options: "+s);
	}
	return optionList;
    }
    
    /**
     * get the input type
     * @return
     */
    public String getType(){
	return type;
    }
    
    /**
     * get the description of the object
     * @return
     */
    public String getDescription(){
	return description;
    }
    
    public String getValue(){
	if(value!=null){
	    return value;
	}
	return "";
    }
}
