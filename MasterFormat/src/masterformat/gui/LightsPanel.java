package masterformat.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import masterformat.tree.TreeBuilder;
import masterformat.tree.TreeNode;
import eplus.EnergyPlusModel;

public class LightsPanel extends JPanel implements TreeSelectionListener{
    private final static String TAG = "Lights";
    
    private final TreeBuilder builder;
    private JTree tree;
    private final JScrollPane treeView;
    private final DefaultMutableTreeNode root;

    private final JPanel editorPanel;
    private final JScrollPane editorView;
    private final JPanel statuPanel;
    private final JScrollPane statuView;

    private final JSplitPane statusPane;
    private final JSplitPane splitPane;

    private final EnergyPlusModel model;
    private final String lightsName;
    
    // data
    private ArrayList<String> userInputs;
    private HashMap<String, String> userInputMap;
    
    public LightsPanel(EnergyPlusModel m, String unitary){
	super(new GridLayout(1,0));
	
	model = m;
	lightsName = unitary;
	
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

	splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	splitPane.setLeftComponent(treeView);
	splitPane.setRightComponent(editorView);

	statusPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	//statusPane.setTopComponent(splitPane);
	statuPanel = new JPanel();
	statuPanel.setBackground(Color.WHITE);
	statuPanel.setLayout(new BoxLayout(statuPanel, BoxLayout.PAGE_AXIS));
	statuView = new JScrollPane(statuPanel);
	statuView.getViewport().setBackground(Color.WHITE);

	statusPane.setTopComponent(splitPane);
	statusPane.setBottomComponent(statuView);
	add(statusPane);
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
		model.setElectricalMasterFormat(lightsName, tn.getDescription());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    userInputs = model.getElectricalUserInputs(lightsName);
	    model.getElectricalCostVector(lightsName);
	    model.setElectricalUserInput(userInputMap, lightsName);
	    disPlayOptions();
	    disPlayData(userInputs);
	}		
    }
    
    public String getLightsName(){
	return lightsName;
    }
    
    private void disPlayOptions() {
  	statuPanel.removeAll();
  	model.getElectricalOptionList(lightsName);
  	model.getElectricalOptionQuantities(lightsName);
  	String[] optionList = model.getOptionList();
  	Integer[] optionQuantities = model.getQuantityList();

  	for (int i = 0; i < optionList.length; i++) {
  	    JPanel tempPanel = new JPanel();
  	    JLabel text = new JLabel(optionList[i]);
  	    text.setFont(new Font("Helvetica", Font.BOLD, 12));
  	    // text.setEnabled();
  	    JTextField q = new JTextField(optionQuantities[i].toString());
  	    q.setFont(new Font("Helvetica", Font.BOLD, 10));
  	    q.setPreferredSize(new Dimension(100, 15));
  	    q.setEditable(false);
  	    tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.PAGE_AXIS));
  	    tempPanel.add(text);
  	    tempPanel.add(q);
  	    tempPanel.setBackground(Color.WHITE);
  	    tempPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
  	    statuPanel.add(tempPanel);
  	}
  	statuPanel.revalidate();
  	statuPanel.repaint();
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
   			model.setElectricalUserInput(userInputMap, lightsName);
   			disPlayOptions();

   		    }

   		    if (e.getStateChange() == ItemEvent.DESELECTED) {
   			userInputMap.remove(description);
   			model.setElectricalUserInput(userInputMap, lightsName);
   			disPlayOptions();

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
	    temp = removeDuplicates(temp);

   	    JComboBox<String> tempCombo = new JComboBox<String>(
   		    temp.toArray(new String[temp.size()]));
   	    tempCombo.addActionListener(new ActionListener() {

   		@Override
   		public void actionPerformed(ActionEvent evt) {
   		    String input = (String) tempCombo.getSelectedItem();
   		    userInputMap.put(option, input);
   		    model.setElectricalUserInput(userInputMap, lightsName);
   		    userInputs = model.getElectricalUserInputs(lightsName);
   		    disPlayData(userInputs);
   		    disPlayOptions();

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
   		    String data = inputField.getText();
   		    userInputMap.put(input, data);
   		    model.setElectricalUserInput(userInputMap, lightsName);
   		    disPlayOptions();

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
       
       private ArrayList<String> removeDuplicates(ArrayList<String> list){
	 	ArrayList<String> temp = new ArrayList<String>();
	 	HashSet<String> set = new HashSet<String>();
	 	for(String s: list){
	 	    if(!set.contains(s)){
	 		temp.add(s);
	 	    }
	 	    set.add(s);
	 	}
	 	return temp;
	     }

     

}
