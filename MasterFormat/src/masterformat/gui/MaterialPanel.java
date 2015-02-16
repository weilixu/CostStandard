package masterformat.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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

    // data
    private ArrayList<String> userInputs;
    
    private final String[] surfaceProperties;
    
    public MaterialPanel(String[] properties) {
	super(new GridLayout(1, 0));
	
	surfaceProperties = properties;

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
	    if (tn.getType().equalsIgnoreCase("CONCRETE")) {
		factory = new ConcreteFactory();
		Concrete concrete = factory.getConcrete(tn.getDescription());
		concrete.setVariable(surfaceProperties);
		userInputs = concrete.getUserInputs();
	    } else if (tn.getType().equalsIgnoreCase("MASONRY")) {
		factory = new MasonryFactory();
		Masonry masonry = factory.getMasonry(tn.getDescription());
		masonry.setVariable(surfaceProperties);
		userInputs = masonry.getUserInputs();
	    } else if (tn.getType().equalsIgnoreCase(
		    "THERMAL MOISTURE PROTECTION")) {
		factory = new ThermalMoistureProtectionFactory();
		ThermalMoistureProtection thermal = factory
			.getThermalMoistureProtection(tn.getDescription());
		thermal.setVariable(surfaceProperties);
		userInputs = thermal.getUserInputs();
	    }
	    disPlayData(userInputs);
	}
    }

    private void disPlayData(ArrayList<String> inputs) {
	editorPanel.removeAll();
	StringBuffer sb = new StringBuffer();
	HashMap<String, HashMap<String, ArrayList<String>>> mapData = stringToMap();

	// currently only have these two options;
	String option = "OPTION";
	String input = "INPUT";
	HashMap<String, ArrayList<String>> optionMap = mapData.get(option);
	HashMap<String, ArrayList<String>> inputMap = mapData.get(input);

	if (optionMap != null) {
	    System.out.println("hello");
	    JPanel optionPanel = createOptions(optionMap);
	    editorPanel.add(optionPanel);

	}

	if (inputMap != null) {
	    JPanel inputPanel = createInputs(inputMap);
	    editorPanel.add(inputPanel);
	}

	editorPanel.revalidate();
	editorPanel.repaint();
    }

    private JPanel createOptions(HashMap<String, ArrayList<String>> optionMap) {
	JPanel optionPanel = new JPanel(new GridLayout(3, 0));

	Set<String> optionList = optionMap.keySet();
	Iterator<String> optionIterator = optionList.iterator();
	while (optionIterator.hasNext()) {
	    String option = optionIterator.next();
	    ArrayList<String> temp = optionMap.get(option);

	    JComboBox<String> tempCombo = new JComboBox<String>(
		    temp.toArray(new String[temp.size()]));
	    optionPanel.add(tempCombo);
	}
	return optionPanel;
    }

    private JPanel createInputs(HashMap<String, ArrayList<String>> inputMap) {
	JPanel inputPanel = new JPanel(new GridLayout(0, 3));
	Set<String> inputList = inputMap.keySet();
	Iterator<String> inputIterator = inputList.iterator();
	while (inputIterator.hasNext()) {
	    String input = inputIterator.next();
	    String inputUnit = inputMap.get(input).get(0);

	    JTextField inputField = new JTextField(input + " " + inputUnit);
	    inputPanel.add(inputField);
	}
	return inputPanel;
    }

    // convert the fixed userinput arraylist data into a map
    private HashMap<String, HashMap<String, ArrayList<String>>> stringToMap() {
	HashMap<String, HashMap<String, ArrayList<String>>> dataMap = new HashMap<String, HashMap<String, ArrayList<String>>>();

	for (String s : userInputs) {
	    System.out.println(s);
	    String[] sList = s.split(":");
	    System.out.println(Arrays.toString(sList));
	    String sType = sList[0];
	    System.out.println(sType);
	    String sName = sList[1];
	    String sInput = sList[2];

	    if (!dataMap.containsKey(sType)) {
		dataMap.put(sType, new HashMap<String, ArrayList<String>>());
		dataMap.get(sType).put(sName, new ArrayList<String>());
	    } else if (dataMap.containsKey(sType)
		    && !dataMap.get(sType).containsKey(sName)) {
		dataMap.get(sType).put(sName, new ArrayList<String>());
	    }
	    dataMap.get(sType).get(sName).add(sInput);
	}

	return dataMap;
    }
}
