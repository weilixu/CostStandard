package uncertainty.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import eplus.EnergyPlusModel;
import masterformat.tree.TreeBuilder;
import masterformat.tree.TreeNode;

public class UncertaintyMaterialPanel extends JPanel implements TreeSelectionListener{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static String TAG = "Material";

    private final TreeBuilder builder;
    private JTree tree;
    private final JScrollPane treeView;
    private final DefaultMutableTreeNode root;
    
    private final EnergyPlusModel model;
    private final String constructionName;
    private final Integer index;
    
    public UncertaintyMaterialPanel(EnergyPlusModel m, String construction, Integer i){
	super(new GridLayout(1,0));
	
	model = m;
	constructionName = construction;
	index = i;
	
	builder = new TreeBuilder();

	root = builder.getPartialTree(TAG);

	tree = new JTree(root);
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
	treeView = new JScrollPane(tree);
	
	add(treeView);
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

	if (node == null) {
	    return;
	}
	Object nodeInfo = node.getUserObject();
	if (node.isLeaf()) {
	    TreeNode tn = (TreeNode) nodeInfo;
	    try {
		model.setConstructionMasterFormat(tn.getType(), tn.getDescription(),
		    constructionName, index);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    tree.setEnabled(false);
	    //model.getConstructionCostVector(constructionName);
	}
    }
    
    public String getConstruction(){
	return constructionName;
    }

}
