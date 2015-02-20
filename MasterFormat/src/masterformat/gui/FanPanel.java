package masterformat.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import eplus.EnergyPlusModel;
import masterformat.tree.TreeBuilder;
import masterformat.tree.TreeNode;

public class FanPanel extends JPanel implements TreeSelectionListener {

    private final static String TAG = "Fan";

    private final TreeBuilder builder;
    private JTree tree;
    private final JScrollPane treeView;
    private final DefaultMutableTreeNode root;

    private final JPanel editorPanel;
    private final JScrollPane editorView;

    private final JSplitPane splitPane;

    private final EnergyPlusModel model;
    private final String fanName;

    // data
    private ArrayList<String> userInputs;
    private HashMap<String, String> userInputMap;

    public FanPanel(EnergyPlusModel m, String fan) {
	super(new GridLayout(1, 0));

	model = m;
	fanName = fan;

	userInputMap = new HashMap<String, String>();

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
	editorView.getViewport().setBackground(Color.WHITE);

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
	    TreeNode tn = (TreeNode) nodeInfo;
	    model.setFanMasterFormat(fanName, tn.getDescription());
	    userInputs = model.getFanUserInputs(fanName);
	    model.getFanCostVector(fanName);
	    disPlayData(userInputs);
	}
    }

    public String getFanName() {
	return fanName;
    }

    private void disPlayData(ArrayList<String> inputs) {
	editorPanel.removeAll();
	HashMap<String, HashMap<String, ArrayList<String>>> mapData = stringToMap();

	// currently only have these three options;
	String option = "OPTION";
	String input = "INPUT";
	String bool = "BOOL";

	HashMap<String, ArrayList<String>> optionMap = mapData.get(option);
	HashMap<String, ArrayList<String>> inputMap = mapData.get(input);
	HashMap<String, ArrayList<String>> boolMap = mapData.get(bool);

	if (optionMap != null) {
	    JPanel optionPanel = createOptions(optionMap);
	    editorPanel.add(optionPanel);
	}

	if (inputMap != null) {
	    JPanel inputPanel = createInputs(inputMap);
	    editorPanel.add(inputPanel);
	}

	if (boolMap != null) {
	    JPanel boolPanel = createBool(boolMap);
	    editorPanel.add(boolPanel);
	}

	editorPanel.revalidate();
	editorPanel.repaint();

    }

    private JPanel createBool(HashMap<String, ArrayList<String>> boolMap) {
	JPanel boolPanel = new JPanel(new GridLayout(3, 0));
	boolPanel.setBackground(Color.WHITE);

	Set<String> boolList = boolMap.keySet();
	Iterator<String> boolIterator = boolList.iterator();
	while (boolIterator.hasNext()) {
	    String bool = boolIterator.next();
	    String description = boolMap.get(bool).get(0);

	    JCheckBox tempCheckedBox = new JCheckBox(description);
	    tempCheckedBox.addItemListener(new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
		    Object source = e.getItemSelectable();
		    if (source == tempCheckedBox) {
			userInputMap.put(description, "true");
		    }
		    
		    if(e.getStateChange()==ItemEvent.DESELECTED){
			userInputMap.remove(description);
		    }
		}
	    });
	    boolPanel.add(tempCheckedBox);
	}
	return boolPanel;
    }

    private JPanel createOptions(HashMap<String, ArrayList<String>> optionMap) {
	JPanel optionPanel = new JPanel(new GridLayout(3, 0));
	optionPanel.setBackground(Color.WHITE);

	Set<String> optionList = optionMap.keySet();
	Iterator<String> optionIterator = optionList.iterator();
	while (optionIterator.hasNext()) {
	    String option = optionIterator.next();
	    ArrayList<String> temp = optionMap.get(option);

	    JComboBox<String> tempCombo = new JComboBox<String>(
		    temp.toArray(new String[temp.size()]));
	    tempCombo.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt) {
		    String input = (String) tempCombo.getSelectedItem();
		    userInputMap.put(option, input);
		    model.setFanUserInput(userInputMap, fanName);
		}
	    });
	    optionPanel.add(tempCombo);
	}
	return optionPanel;
    }

    private JPanel createInputs(HashMap<String, ArrayList<String>> inputMap) {
	JPanel inputPanel = new JPanel(new GridLayout(0, 3));
	inputPanel.setBackground(Color.WHITE);
	Set<String> inputList = inputMap.keySet();
	Iterator<String> inputIterator = inputList.iterator();
	while (inputIterator.hasNext()) {
	    String input = inputIterator.next();
	    String inputUnit = inputMap.get(input).get(0);

	    JTextField inputField = new JTextField(input + " " + inputUnit);
	    inputField.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    String data = (String) inputField.getText();
		    userInputMap.put(input, data);
		    model.setFanUserInput(userInputMap, fanName);
		}

	    });
	    inputPanel.add(inputField);
	}
	return inputPanel;
    }

    // convert the fixed userinput arraylist data into a map
    private HashMap<String, HashMap<String, ArrayList<String>>> stringToMap() {
	HashMap<String, HashMap<String, ArrayList<String>>> dataMap = new HashMap<String, HashMap<String, ArrayList<String>>>();

	for (String s : userInputs) {
	    String[] sList = s.split(":");
	    String sType = sList[0];
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
