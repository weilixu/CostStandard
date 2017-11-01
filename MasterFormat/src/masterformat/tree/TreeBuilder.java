package masterformat.tree;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class TreeBuilder {

    private final SAXBuilder builder;
    private final File masterformat;
    private Document document;
    private DefaultMutableTreeNode node;

    private final String FILE_NAME = "masterformat.xml";

    public TreeBuilder() {
	builder = new SAXBuilder();
	masterformat = new File(FILE_NAME);
	node = new DefaultMutableTreeNode("MasterFormat");

	try {
	    document = builder.build(masterformat);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	masterformatBuilder();
    }

    private void masterformatBuilder() {
	Element root = document.getRootElement();
	builderHelper(root, node);
    }

    private void builderHelper(Element current, DefaultMutableTreeNode parent) {
	List<Element> children = current.getChildren();
	Iterator<Element> iterator = children.iterator();
	while (iterator.hasNext()) {
	    Element child = iterator.next();
	    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
	    if (child.hasAttributes()) {
		TreeNode tempNode = new TreeNode(
			child.getAttributeValue("type"),
			child.getAttributeValue("tag"),
			child.getAttributeValue("description"));
		childNode.setUserObject(tempNode);
	    } else {
		childNode.setUserObject(child.getName());
	    }
	    parent.add(childNode);
	    builderHelper(child, childNode);
	}
    }

    public DefaultMutableTreeNode getTree() {
	return node;
    }

    /**
     * retrieve part of the tree
     * 
     * @param category
     * @return
     */
    public DefaultMutableTreeNode getPartialTree(String category) {
	int count = node.getChildCount();
	for (int i = 0; i < count; i++) {
	    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
		    .getChildAt(i);
	    String childName = (String) child.getUserObject();
	    if (childName.equalsIgnoreCase(category)) {
		return child;
	    }
	}
	return null;
    }

}
