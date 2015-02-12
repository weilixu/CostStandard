package eplus.lifecyclecost;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * parse the economic XML into database
 * @author Weili
 *
 */
public class EconomicParser {

    private final SAXBuilder builder;
    private final File economics;
    private Document document;

    private HashMap<String,ArrayList<TemplateObject>> objects;

    private static final String FILE_NAME = "economic.xml";

    public EconomicParser() {
	builder = new SAXBuilder();
	economics = new File(FILE_NAME);
	
	objects = new HashMap<String,ArrayList<TemplateObject>>();

	try {
	    document = (Document) builder.build(economics);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	economicBuilder();
    }
    
    public HashMap<String,ArrayList<TemplateObject>> getObjects(){
	return objects;
    }

    private void economicBuilder() {
	Element root = document.getRootElement();
	builderHelper(root);
    }

    private void builderHelper(Element current) {
	List<Element> children = current.getChildren();
	Iterator<Element> iterator = children.iterator();
	while (iterator.hasNext()) {
	    Element child = iterator.next();
	    if (child.getName().equals("object")) {
		TemplateObject temp = new TemplateObject(
			child.getAttributeValue("description"),
			child.getAttributeValue("reference"));
		buildFields(child, temp);
		String category = child.getAttributeValue("category");
		if(!objects.containsKey(category)){
		    objects.put(category, new ArrayList<TemplateObject>());
		}
		objects.get(category).add(temp);
		
	    } else {
		builderHelper(child);
	    }
	}
    }

    private void buildFields(Element current, TemplateObject temp) {
	List<Element> children = current.getChildren();
	Iterator<Element> iterator = children.iterator();
	while (iterator.hasNext()) {
	    Element child = iterator.next();
	    FieldElement fe = new FieldElement(
		    child.getAttributeValue("description"),
		    child.getAttributeValue("type"));
	    
	    if(child.getName().equals("key")&&!child.getChildren().isEmpty()){
		buildOptions(child, fe);
	    }
	    
	    if (!child.getText().equals("")) {
		fe.setValue(child.getText());
	    }
	    
	    if (child.getAttributeValue("minimum")!=null) {
		fe.setMinimum(child.getAttributeValue("minimum"));
	    }
	    if (child.getAttributeValue("maximum")!=null) {
		fe.setMaximum(child.getAttributeValue("maximum"));
	    }
	    temp.insertFieldElement(fe);
	}
    }
    
    private void buildOptions(Element current, FieldElement field){
	List<Element> children = current.getChildren();
	Iterator<Element> iterator = children.iterator();
	while (iterator.hasNext()){
	    Element child = iterator.next();
	    field.insertOptions(child.getText());
	}
    }

//    public static void main(String[] args){
//	EconomicParser parser = new EconomicParser();
//	HashMap<String,ArrayList<TemplateObject>> objects = parser.getObjects();
//	Set<String> categories = objects.keySet();
//	Iterator<String> iterator = categories.iterator();
//	while(iterator.hasNext()){
//	    String category = iterator.next();
//	    ArrayList<TemplateObject> object = objects.get(category);
//	    System.out.print(category+": ");
//	    for(TemplateObject o: object){
//		System.out.println(o.getObject());
//		for(FieldElement e: o.getFieldList()){
//		    System.out.println(e.getOptionList());
//		}
//	    }
//	}
//    }
}
