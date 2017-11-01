package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import eplus.EnergyPlusModel;
import eplus.construction.ComponentFactory;

public class OptimizationVariablePanel extends JPanel {

    /*
     * set model and files
     */
    private EnergyPlusModel model;

    /*
     * set the panels for jtabbed panel
     */
    private final JPanel inputPanel;
    private final JPanel graphPanel;

    /*
     * set the variables
     */
    private JPanel coolPanel;
    private final JLabel coolUpdate = new JLabel("Chiller");
    private JList coolList;

    private JPanel wallPanel;
    private final JLabel Wall = new JLabel("Exterior Wall");
    private JList wallList;

    private JPanel roofPanel;
    private final JLabel Roof = new JLabel("Roof");
    private JList roofList;

    private JPanel heatPanel;
    private final JLabel heatUpdate = new JLabel("Boiler");
    private JList heatList;

    private JPanel hvacPanel;
    private final JLabel hvac = new JLabel("HVAC System");
    private JList hvacList;

    private JPanel insulPanel;
    private final JLabel insulation = new JLabel("Wall Insulation Level (R)");
    private JTextField insulationR;

    private JPanel lightPanel;
    private final JLabel light = new JLabel("Lighting Fixture");
    private JList lightList;

    private JPanel lightSensorPanel;
    private final JLabel lightSensor = new JLabel("Daylight Sensor");
    private JList lightSensorList;

    private JPanel lightShelfPanel;
    private final JLabel lightShelf = new JLabel("Light Shelf System");
    private JList lightShelfList;

    private JPanel pvPanel;
    private final JLabel pv = new JLabel("BiPV System");
    private JList pvList;

    private JPanel wwrPanel;
    private final JLabel wwr = new JLabel("Window to Wall Ratio");
    private JList wwrList;

    private JPanel windowPanel;
    private final JLabel window = new JLabel("Windows");
    private JList winList;

    private boolean isNewConstruction;

    public OptimizationVariablePanel(EnergyPlusModel m) {
	model = m;
	setLayout(new BorderLayout());

	inputPanel = new JPanel();
	setJListForNewConstruction();
	add(inputPanel, BorderLayout.CENTER);

	graphPanel = new JPanel(new BorderLayout());

    }

    public void switchTarget() {
	inputPanel.removeAll();
	if (isNewConstruction) {
	    setJListForRetrofit();
	} else {
	    setJListForNewConstruction();
	}

	inputPanel.revalidate();
	inputPanel.repaint();
    }

    private void setJListForRetrofit() {
	inputPanel.setLayout(new GridLayout(4, 2));
	// initialize the variable panels and lists;
	// insulation
	insulPanel = new JPanel(new BorderLayout());
	insulPanel.add(insulation, BorderLayout.PAGE_START);
	insulationR = new JTextField("2.0");
	insulPanel.add(insulationR, BorderLayout.CENTER);
	inputPanel.add(insulPanel);
	// light
	lightPanel = new JPanel(new BorderLayout());
	lightPanel.add(light, BorderLayout.PAGE_START);
	lightList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("Lighting"));
	listSetUp(lightList);
	JScrollPane lightScroller = new JScrollPane(lightList);
	lightPanel.add(lightScroller, BorderLayout.CENTER);
	inputPanel.add(lightPanel);
	// light sensor
	lightSensorPanel = new JPanel(new BorderLayout());
	lightSensorPanel.add(lightSensor, BorderLayout.PAGE_START);
	lightSensorList = new JList(ComponentFactory
		.getListOfComponentsForRetrofit("Daylight Sensor"));
	listSetUp(lightSensorList);
	JScrollPane lightSensorScroller = new JScrollPane(lightSensorList);
	lightSensorPanel.add(lightSensorScroller, BorderLayout.CENTER);
	inputPanel.add(lightSensorPanel);
	// window
	windowPanel = new JPanel(new BorderLayout());
	windowPanel.add(window, BorderLayout.PAGE_START);
	winList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("Window"));
	listSetUp(winList);
	JScrollPane winScroller = new JScrollPane(winList);
	windowPanel.add(winScroller, BorderLayout.CENTER);
	inputPanel.add(windowPanel);
	// pv
	pvPanel = new JPanel(new BorderLayout());
	pvPanel.add(pv, BorderLayout.PAGE_START);
	pvList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("PV"));
	listSetUp(pvList);
	JScrollPane pvScroller = new JScrollPane(pvList);
	pvPanel.add(pvScroller, BorderLayout.CENTER);
	inputPanel.add(pvPanel);
	// lightshelf
	lightShelfPanel = new JPanel(new BorderLayout());
	lightShelfPanel.add(lightShelf, BorderLayout.PAGE_START);
	lightShelfList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("LightShelf"));
	listSetUp(lightShelfList);
	JScrollPane lightShelfScroller = new JScrollPane(lightShelfList);
	lightShelfPanel.add(lightShelfScroller, BorderLayout.CENTER);
	inputPanel.add(lightShelfPanel);
	// heating
	heatPanel = new JPanel(new BorderLayout());
	heatPanel.add(heatUpdate, BorderLayout.PAGE_START);
	heatList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("Boiler"));
	listSetUp(heatList);
	JScrollPane boilerScroller = new JScrollPane(heatList);
	heatPanel.add(boilerScroller, BorderLayout.CENTER);
	inputPanel.add(heatPanel);
	// cooling
	coolPanel = new JPanel(new BorderLayout());
	coolPanel.add(coolUpdate, BorderLayout.PAGE_START);
	coolList = new JList(
		ComponentFactory.getListOfComponentsForRetrofit("Chiller"));
	listSetUp(coolList);
	JScrollPane chillerScroller = new JScrollPane(coolList);
	coolPanel.add(chillerScroller, BorderLayout.CENTER);
	inputPanel.add(coolPanel);
	isNewConstruction = false;
    }

    @SuppressWarnings("unchecked")
    private void setJListForNewConstruction() {

	inputPanel.setLayout(new GridLayout(5, 2));
	// initialize the variable panels and lists;
	// wall
	wallPanel = new JPanel(new BorderLayout());
	wallPanel.add(Wall, BorderLayout.PAGE_START);
	wallList = new JList(
		ComponentFactory.getListOfComponentsForNewConstruction("Wall"));
	listSetUp(wallList);
	JScrollPane wallScroller = new JScrollPane(wallList);
	wallPanel.add(wallScroller, BorderLayout.CENTER);
	inputPanel.add(wallPanel);
	// roof
	roofPanel = new JPanel(new BorderLayout());
	roofPanel.add(Roof, BorderLayout.PAGE_START);
	roofList = new JList(
		ComponentFactory.getListOfComponentsForNewConstruction("Roof"));
	listSetUp(roofList);
	JScrollPane roolScroller = new JScrollPane(roofList);
	roofPanel.add(roolScroller, BorderLayout.CENTER);
	inputPanel.add(roofPanel);
	// light
	lightPanel = new JPanel(new BorderLayout());
	lightPanel.add(light, BorderLayout.PAGE_START);
	lightList = new JList(ComponentFactory
		.getListOfComponentsForNewConstruction("Lighting"));
	listSetUp(lightList);
	JScrollPane lightScroller = new JScrollPane(lightList);
	lightPanel.add(lightScroller, BorderLayout.CENTER);
	inputPanel.add(lightPanel);
	// light sensor
	lightSensorPanel = new JPanel(new BorderLayout());
	lightSensorPanel.add(lightSensor, BorderLayout.PAGE_START);
	lightSensorList = new JList(ComponentFactory
		.getListOfComponentsForNewConstruction("Daylight Sensor"));
	listSetUp(lightSensorList);
	JScrollPane lightSensorScroller = new JScrollPane(lightSensorList);
	lightSensorPanel.add(lightSensorScroller, BorderLayout.CENTER);
	inputPanel.add(lightSensorPanel);
	// window
	windowPanel = new JPanel(new BorderLayout());
	windowPanel.add(window, BorderLayout.PAGE_START);
	winList = new JList(ComponentFactory
		.getListOfComponentsForNewConstruction("Window"));
	listSetUp(winList);
	JScrollPane winScroller = new JScrollPane(winList);
	windowPanel.add(winScroller, BorderLayout.CENTER);
	inputPanel.add(windowPanel);
	// winToWallRatio
	wwrPanel = new JPanel(new BorderLayout());
	wwrPanel.add(wwr, BorderLayout.PAGE_START);
	wwrList = new JList(ComponentFactory
		.getListOfComponentsForNewConstruction("WindowWallRatio"));
	listSetUp(wwrList);
	JScrollPane wwrScroller = new JScrollPane(wwrList);
	wwrPanel.add(wwrScroller, BorderLayout.CENTER);
	inputPanel.add(wwrPanel);
	// pv
	pvPanel = new JPanel(new BorderLayout());
	pvPanel.add(pv, BorderLayout.PAGE_START);
	pvList = new JList(
		ComponentFactory.getListOfComponentsForNewConstruction("PV"));
	listSetUp(pvList);
	JScrollPane pvScroller = new JScrollPane(pvList);
	pvPanel.add(pvScroller, BorderLayout.CENTER);
	inputPanel.add(pvPanel);
	// lightshelf
	lightShelfPanel = new JPanel(new BorderLayout());
	lightShelfPanel.add(lightShelf, BorderLayout.PAGE_START);
	lightShelfList = new JList(ComponentFactory
		.getListOfComponentsForNewConstruction("LightShelf"));
	listSetUp(lightShelfList);
	JScrollPane lightShelfScroller = new JScrollPane(lightShelfList);
	lightShelfPanel.add(lightShelfScroller, BorderLayout.CENTER);
	inputPanel.add(lightShelfPanel);
	// hvac
	hvacPanel = new JPanel(new BorderLayout());
	hvacPanel.add(hvac, BorderLayout.PAGE_START);
	hvacList = new JList(
		ComponentFactory.getListOfComponentsForNewConstruction("HVAC"));
	listSetUp(hvacList);
	JScrollPane hvacScroller = new JScrollPane(hvacList);
	hvacPanel.add(hvacScroller, BorderLayout.CENTER);
	inputPanel.add(hvacPanel);

	isNewConstruction = true;
    }

    private void listSetUp(JList list) {
	list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	list.setLayoutOrientation(JList.VERTICAL);
	/*
	 * selection model
	 */
	list.setSelectionModel(new DefaultListSelectionModel(){
	    private static final long serialVersionUID = 1L;
	    
	    boolean gestureStarted = false;
	    
	    @Override
	    public void setSelectionInterval(int index0, int index1){
		if(!gestureStarted){
		    if(isSelectedIndex(index0)){
			    super.removeSelectionInterval(index0, index1);
			}else{
			    super.addSelectionInterval(index0, index1);
			}			
		    }
		gestureStarted = true;
	    }
	    
	    @Override
	    public void setValueIsAdjusting(boolean isAdjusting){
		if(isAdjusting == false){
		    gestureStarted = false;
		}
	    }
	});
	
	int start = 0;
	int end = list.getModel().getSize() - 1;
	if (end >= 0) {
	    list.setSelectionInterval(start, end);
	}
    }
    
    public HashMap<String, String[]> extractVariables(){
	HashMap<String, String[]> variableMap = new HashMap<String, String[]>();
	if(isNewConstruction){
	    String[] wallOptions = (String[]) wallList.getSelectedValuesList().toArray();
	    String[] roofOptions = (String[]) roofList.getSelectedValuesList().toArray();
	    String[] lightOptions = (String[]) lightList.getSelectedValuesList().toArray();
	    String[] lightSensorOptions = (String[]) lightSensorList.getSelectedValuesList().toArray();
	    String[] lightShelfOptions = (String[])lightShelfList.getSelectedValuesList().toArray();
	    String[] pvOptions = (String[]) pvList.getSelectedValuesList().toArray();
	    String[] wwrOptions = (String[]) wwrList.getSelectedValuesList().toArray();
	    String[] windowOptions = (String[]) winList.getSelectedValuesList().toArray();
	    String[] hvacOptions = (String[]) hvacList.getSelectedValuesList().toArray();
	    
	    variableMap.put("exteriorwall", wallOptions);
	    variableMap.put("roof", roofOptions);
	    variableMap.put("lpd", lightOptions);
	    variableMap.put("daylightsensor", lightSensorOptions);
	    variableMap.put("LightShelf", lightShelfOptions);
	    variableMap.put("PV", pvOptions);
	    variableMap.put("wwr", wwrOptions);
	    variableMap.put("window", windowOptions);
	    variableMap.put("hvac", hvacOptions);
	}else{
	    String[] insulationOptions = new String[1];
	    insulationOptions[0] = insulationR.getText();
	    String[] lightOptions = (String[]) lightList.getSelectedValuesList().toArray();
	    String[] lightSensorOptions = (String[]) lightSensorList.getSelectedValuesList().toArray();
	    String[] windowOptions = (String[]) winList.getSelectedValuesList().toArray();
	    String[] pvOptions = (String[]) pvList.getSelectedValuesList().toArray();
	    String[] lightShelfOptions = (String[])lightShelfList.getSelectedValuesList().toArray();
	    String[] boilerOptions = (String[]) heatList.getSelectedValuesList().toArray();
	    String[] chillerOptions = (String[]) coolList.getSelectedValuesList().toArray();
	    
	    variableMap.put("Insulation",insulationOptions);
	    variableMap.put("lpd", lightOptions);
	    variableMap.put("daylightsensor", lightSensorOptions);
	    variableMap.put("window", windowOptions);
	    variableMap.put("PV", pvOptions);
	    variableMap.put("LightShelf", lightShelfOptions);
	    variableMap.put("BoilerEfficiency", boilerOptions);
	    variableMap.put("ChillerEfficiency", chillerOptions);
	}
	return variableMap;
    }

}
