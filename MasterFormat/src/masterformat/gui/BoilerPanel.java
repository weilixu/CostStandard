package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import eplus.EnergyPlusModel;

public class BoilerPanel extends JPanel{
    private final static String TAG = "Boiler";
    
    private final JPanel editorPanel;
    private final JScrollPane editorView;
    
    private final EnergyPlusModel model;
    
    //data
    private final String boiler;
    private ArrayList<String> userInputs;
    private HashMap<String, String> userInputMap;
    
    public BoilerPanel(EnergyPlusModel m, String boilerName){
	super(new BorderLayout());
	
	model = m;
	boiler = boilerName;
	userInputs = new ArrayList<String>();
	userInputMap = new HashMap<String, String>();
	
	editorPanel = new JPanel();
	editorPanel.setLayout(new GridLayout(2,0));
	editorPanel.setBackground(Color.WHITE);
	processEditorPanel();

	editorView = new JScrollPane(editorPanel);
	editorView.getViewport().setBackground(Color.WHITE);
	
	add(editorView, BorderLayout.CENTER);
    }
    
    private void processEditorPanel(){
	userInputs = model.getBoilerUserInputs(boiler);
	
	HashMap<String, HashMap<String, ArrayList<String>>> mapData = stringToMap();
	
	// currently only have these two options;
	String option = "OPTION";
	String input = "INPUT";
	HashMap<String, ArrayList<String>> optionMap = mapData.get(option);
	HashMap<String, ArrayList<String>> inputMap = mapData.get(input);

	if (optionMap != null) {
	    JPanel optionPanel = createOptions(optionMap);
	    optionPanel.setBackground(Color.WHITE);
	    editorPanel.add(optionPanel);
	}

	if (inputMap != null) {
	    JPanel inputPanel = createInputs(inputMap);
	    inputPanel.setBackground(Color.WHITE);
	    editorPanel.add(inputPanel);
	}

	editorPanel.revalidate();
	editorPanel.repaint();
    
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
 	    tempCombo.addActionListener(new ActionListener(){

 		@Override
 		public void actionPerformed(ActionEvent evt) {
 		    String input = (String)tempCombo.getSelectedItem();
 		    userInputMap.put(option, input);
 		    model.setBoilerUserInput(userInputMap,boiler);
 		}
 	    });
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
 	    inputField.addActionListener(new ActionListener(){

 		@Override
 		public void actionPerformed(ActionEvent e) {
 		    String data = (String)inputField.getText();
 		    userInputMap.put(input, data);  
 		    model.setBoilerUserInput(userInputMap,boiler);
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
    


