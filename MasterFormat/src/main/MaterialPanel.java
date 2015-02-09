package main;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import masterformat.api.ComponentFactory;
import masterformat.standard.concrete.Concrete;
import masterformat.standard.concrete.ConcreteFactory;
import masterformat.standard.masonry.Masonry;
import masterformat.standard.masonry.MasonryFactory;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtection;
import masterformat.standard.thermalmoistureprotection.ThermalMoistureProtectionFactory;
import masterformat.tree.TreeBuilder;
import masterformat.tree.TreeNode;

public class MaterialPanel extends JPanel implements TreeSelectionListener {

    private final static String TAG = "Material";

    private final TreeBuilder builder;
    private JTree tree;
    private final JScrollPane treeView;
    private final DefaultMutableTreeNode root;

    private final JPanel editorPanel;
    private final JScrollPane editorView;

    private final JSplitPane splitPane;

    public MaterialPanel() {
	super(new GridLayout(1, 0));

	builder = new TreeBuilder();

	root = builder.getPartialTree(TAG);

	tree = new JTree(root);
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
	treeView = new JScrollPane(tree);

	editorPanel = new JPanel();
	editorPanel.setLayout(new GridLayout(0, 6));
	editorPanel.setBackground(Color.WHITE);
	editorView = new JScrollPane(editorPanel);

	splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	splitPane.setTopComponent(treeView);
	splitPane.setBottomComponent(editorView);

	add(splitPane);

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
	    ComponentFactory factory;
	    TreeNode tn = (TreeNode) nodeInfo;
	    ArrayList<String> userInput = null;
	    String[] surfaceProperties = { "", "", "", "", "", "", "" };
	    if (tn.getType().equalsIgnoreCase("CONCRETE")) {
		factory = new ConcreteFactory();
		Concrete concrete = factory.getConcrete(tn.getDescription());
		concrete.setVariable(surfaceProperties);
		userInput = concrete.getUserInputs();
	    } else if (tn.getType().equalsIgnoreCase("MASONRY")) {
		factory = new MasonryFactory();
		Masonry masonry = factory.getMasonry(tn.getDescription());
		masonry.setVariable(surfaceProperties);
		userInput = masonry.getUserInputs();
	    } else if (tn.getType().equalsIgnoreCase(
		    "THERMAL MOISTURE PROTECTION")) {
		factory = new ThermalMoistureProtectionFactory();
		ThermalMoistureProtection thermal = factory
			.getThermalMoistureProtection(tn.getDescription());
		thermal.setVariable(surfaceProperties);
		userInput = thermal.getUserInputs();
	    }

	    disPlayData(userInput);
	}
    }

    private void disPlayData(ArrayList<String> inputs) {
	editorPanel.removeAll();
	StringBuffer sb = new StringBuffer();
	for (String s : inputs) {
	    sb.append(s);
	    sb.append("\n");
	}
	JTextArea tempArea = new JTextArea(sb.toString());
	editorPanel.add(tempArea);
	editorPanel.revalidate();
	editorPanel.repaint();
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("MasterFormatTreeDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// Add content to the window.
	frame.add(new MaterialPanel());

	// Display the window.
	frame.pack();
	frame.setVisible(true);
    }

    public static void main(String[] args) {
	// Schedule a job for the event dispatch thread:
	// creating and showing this application's GUI.
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		createAndShowGUI();
	    }
	});
    }

}
