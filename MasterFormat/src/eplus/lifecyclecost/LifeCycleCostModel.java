package eplus.lifecyclecost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

public class LifeCycleCostModel {
    private final EconomicParser parser;
    private final HashMap<String, ArrayList<DataObjects>> dataMap;

    private DefaultMutableTreeNode root;

    public LifeCycleCostModel() {
	parser = new EconomicParser();
	dataMap = parser.getObjects();
    }

    public DefaultMutableTreeNode getCompleteTreeNode() {
	buildTreeFromMap();
	return root;
    }

    public void addCostToIdfFile() {

    }

    public DataObjects makeCopyOfObject(DataObjects dataset) {
	DataObjects tempSet = new DataObjects(dataset.getSetName());
	ArrayList<TemplateObject> objectList = dataset.getObjects();
	for (TemplateObject object : objectList) {
	    TemplateObject temp = new TemplateObject(object.getObject(),
		    object.getReference());
	    ArrayList<FieldElement> fieldList = object.getFieldList();
	    for (FieldElement fe : fieldList) {
		temp.insertFieldElement(fe.clone());
	    }
	    tempSet.addObject(object);
	}
	return tempSet;
    }

    private void buildTreeFromMap() {
	root = new DefaultMutableTreeNode("Database");
	Set<String> categories = dataMap.keySet();
	Iterator<String> iterator = categories.iterator();
	while (iterator.hasNext()) {
	    String category = iterator.next();
	    DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(
		    category);
	    root.add(categoryNode);

	    ArrayList<DataObjects> objects = dataMap.get(category);
	    for (DataObjects object : objects) {
		DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode(
			object);
		categoryNode.add(objectNode);
	    }
	}
    }

}
