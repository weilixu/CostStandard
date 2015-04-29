package uncertainty.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import masterformat.gui.OpeningsPanel;
import eplus.EnergyPlusModel;
import eplus.MaterialAnalyzer.Material;

public class UncertaintyPanel extends JPanel{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JPanel EnergyPlusObjectPanel;
    private final JPanel itemPanel;
    private final JPanel constructionPanel;
    private final JPanel lightsPanel;
    private final JPanel fanPanel;
    private final JPanel boilerPanel;
    private final JPanel condenserUnitPanel;
    private final JPanel pumpPanel;
    private final JPanel furnacePanel;
    private final JPanel convectionUnitPanel;
    private final JPanel openingsPanel;
    private final JPanel controllPanel;
    
    private final JComboBox<String> objectSelectionCombo;
    private JList<String> constructionList;
    private JScrollPane constructionListScrollPane;
    private DefaultListModel<String> constructionListModel;
    private DefaultListModel<String> lightsListModel;
    private JList<String> lightsList;
    private JScrollPane lightsScrollPane;
    private JList<String> fanList;
    private JScrollPane fanListScrollPane;
    private DefaultListModel<String> fanListModel;
    private JList<String> boilerList;
    private JScrollPane boilerListScrollPane;
    private DefaultListModel<String> boilerListModel;
    private DefaultListModel<String> pumpListModel;
    private JList<String> pumpList;
    private JScrollPane pumpListScrollPane;
    private DefaultListModel<String> furnaceModel;
    private JList<String> furnaceList;
    private JScrollPane furnaceScrollPane;
    private JList<String> condenserUnitList;
    private JScrollPane condenserUnitListScrollPane;
    private DefaultListModel<String> condenserUnitListModel;
    private DefaultListModel<String> convectionUnitListModel;
    private JList<String> convectionUnitList;
    private JScrollPane convectionUnitListScrollPane;
    private DefaultListModel<String> openingsListModel;
    private JList<String> openingsList;
    private JScrollPane openingsScrollPane;
    
    private final JButton budgetButton;
    private final String BUDGET="Calculate Budget";
    
    private final EnergyPlusModel model;
    
    public UncertaintyPanel(EnergyPlusModel m) throws Exception{
	model = m;
	setLayout(new BorderLayout());

	itemPanel = new JPanel(new BorderLayout());
	itemPanel.setBackground(Color.WHITE);
	EnergyPlusObjectPanel = new JPanel(new BorderLayout());
	Border raisedetched = BorderFactory
		.createEtchedBorder(EtchedBorder.RAISED);
	EnergyPlusObjectPanel.setBorder(raisedetched);
	
	// set-up different category panels
	constructionPanel = new JPanel(new CardLayout());
	constructionPanel.setBackground(Color.WHITE);
	updateConstructions();
	
	fanPanel = new JPanel(new CardLayout());
	fanPanel.setBackground(Color.WHITE);
	updateFans();
	
	lightsPanel = new JPanel(new CardLayout());
	lightsPanel.setBackground(Color.WHITE);
	updateLightsPanel();
	
	boilerPanel = new JPanel(new CardLayout());
	boilerPanel.setBackground(Color.WHITE);
	updateBoilers();
	
	pumpPanel = new JPanel(new CardLayout());
	pumpPanel.setBackground(Color.WHITE);
	updatePump();
	
	furnacePanel = new JPanel(new CardLayout());
	furnacePanel.setBackground(Color.WHITE);
	updateFurnace();
	
	condenserUnitPanel = new JPanel(new CardLayout());
	condenserUnitPanel.setBackground(Color.WHITE);
	updateCondenserUnit();
	
	convectionUnitPanel = new JPanel(new CardLayout());
	convectionUnitPanel.setBackground(Color.WHITE);
	updateConvectionUnit();
	
	openingsPanel = new JPanel(new CardLayout());
	openingsPanel.setBackground(Color.WHITE);
	updateOpeningsPanel();
	
	// initialize the selection combo list
	objectSelectionCombo = new JComboBox<String>(model.getDomainList());
	objectSelectionCombo.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent evt) {
		String category = (String) evt.getItem();

		if (category.equals("Opaque Construction")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(constructionPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(constructionListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if(category.equalsIgnoreCase("LIGHTS")){
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(lightsPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(lightsScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if (category.equals("Fan")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(fanPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(fanListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		} else if (category.equals("Boiler")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(boilerPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(boilerListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if (category.equalsIgnoreCase("PUMP")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(pumpPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(pumpListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		} else if (category.equalsIgnoreCase("Furnace")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(furnacePanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(furnaceScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if (category.equalsIgnoreCase("Convection Unit")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(convectionUnitPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(convectionUnitListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		} else if (category.equalsIgnoreCase("Condenser Unit")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(condenserUnitPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(condenserUnitListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if(category.equalsIgnoreCase("Transparent Construction")){
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(openingsPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(openingsScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}
	    }
	}); 
	
	itemPanel.add(constructionPanel, BorderLayout.CENTER);
	EnergyPlusObjectPanel
		.add(objectSelectionCombo, BorderLayout.PAGE_START);
	EnergyPlusObjectPanel.add(constructionListScrollPane,
		BorderLayout.CENTER);
	
	controllPanel = new JPanel();
	budgetButton = new JButton(BUDGET);
	budgetButton.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e){
		try{
		    model.calculateBudget();
		}catch(NullPointerException ne){
		    showErrorDialog(new JFrame(),"Warning","There is objects that are not categorized!");
		}
	    }
	});
	controllPanel.add(budgetButton);

	add(EnergyPlusObjectPanel, BorderLayout.WEST);
	add(itemPanel, BorderLayout.CENTER);
	add(controllPanel,BorderLayout.PAGE_END);
    }
    
    private void updateConstructions() {

 	constructionListModel = new DefaultListModel<String>();
 	constructionList = new JList<String>(constructionListModel);
 	constructionList.setFont(new Font("Helvetica", Font.BOLD, 20));

 	String[] cons = model.getConstructionList();
 	constructionListModel.clear();
 	for (String s : cons) {
 	    JTabbedPane tp = makeTabbedPanel(s);
 	    constructionPanel.add(tp, s);

 	    constructionListModel.addElement(s);
 	    constructionList
 		    .addListSelectionListener(new ListSelectionListener() {
 			@Override
 			public void valueChanged(ListSelectionEvent evt) {
 			    CardLayout cardLayout = (CardLayout) (constructionPanel
 				    .getLayout());
 			    String selection = constructionList
 				    .getSelectedValue().toString();
 			    if (selection.equals(s)) {
 				cardLayout.show(constructionPanel, selection);
 			    }
 			    model.getConstructionCostVector(selection);
 			}
 		    });
 	}
 	constructionListScrollPane = new JScrollPane(constructionList);
     }
    
    private void updateFans() {
	fanListModel = new DefaultListModel<String>();
	fanList = new JList<String>(fanListModel);
	fanList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] fans = model.getFanList();
	fanListModel.clear();
	for (String f : fans) {
	    // model.setFanMasterFormat(f);
	    JPanel fan = new UncertaintyFanPanel(model, f);// need to change this

	    fanPanel.add(fan,f);
	    fanListModel.addElement(f);
	    fanList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (fanPanel.getLayout());
		    String selection = fanList.getSelectedValue().toString();
		    if (selection.equals(f)) {
			cardLayout.show(fanPanel, selection);
		    }
		    model.getFanCostVector(selection);
		}
	    });
	}
	fanListScrollPane = new JScrollPane(fanList);
    }
    
    private void updateBoilers() throws Exception {
	boilerListModel = new DefaultListModel<String>();
	boilerList = new JList<String>(boilerListModel);
	boilerList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] boilers = model.getBoilerList();
	boilerListModel.clear();
	for (String s : boilers) {

	    model.setBoilerMasterFormat(s);
	    JPanel boiler = new UncertaintyBoilerPanel(model, s);// need to change this
	    boilerPanel.add(boiler, s);

	    boilerListModel.addElement(s);
	    boilerList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (boilerPanel
			    .getLayout());
		    String selection = boilerList.getSelectedValue().toString();
		    if (selection.equals(s)&& e.getValueIsAdjusting()==true) {
			cardLayout.show(boilerPanel, selection);
			model.getBoilerCostVector(selection);
		    }
		}
	    });

	}

	boilerListScrollPane = new JScrollPane(boilerList);
    }
    
    private void updatePump() {
 	pumpListModel = new DefaultListModel<String>();
 	pumpList = new JList<String>(pumpListModel);
 	pumpList.setFont(new Font("Helvetica", Font.BOLD, 20));

 	String[] pumps = model.getPumpList();
 	pumpListModel.clear();
 	for (String p : pumps) {
 	    // model.setFanMasterFormat(f);
 	    JPanel pump = new UncertaintyPumpPanel(model, p);// need to change this

 	    pumpPanel.add(pump,p);
 	    pumpListModel.addElement(p);
 	    pumpList.addListSelectionListener(new ListSelectionListener() {

 		@Override
 		public void valueChanged(ListSelectionEvent e) {
 		    CardLayout cardLayout = (CardLayout) (pumpPanel.getLayout());
 		    String selection = pumpList.getSelectedValue().toString();
 		    if (selection.equals(p)) {
 			cardLayout.show(pumpPanel, selection);
 		    }
 		    model.getPumpCostVector(selection);
 		}
 	    });
 	}
 	pumpListScrollPane = new JScrollPane(pumpList);
     }
    
    private void updateFurnace() throws Exception {
	furnaceModel = new DefaultListModel<String>();
	furnaceList = new JList<String>(furnaceModel);
	furnaceList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] furnaces = model.getFurnaceList();
	furnaceModel.clear();

	for (String f : furnaces) {

	    model.setFurnaceMasterFormat(f);
	    JPanel furnace = new UncertaintyFurnacePanel(model, f);// need to change this
	    furnacePanel.add(furnace, f);

	    furnaceModel.addElement(f);
	    furnaceList.addListSelectionListener(new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (furnacePanel
			    .getLayout());
		    String selection = furnaceList.getSelectedValue()
			    .toString();
		    if (selection.equals(f)) {
			cardLayout.show(furnacePanel, selection);
			model.getFurnaceCostVector(selection);
		    }
		}
	    });

	}

	furnaceScrollPane = new JScrollPane(furnaceList);

    }
    
    
    public void updateLightsPanel(){
	lightsListModel = new DefaultListModel<String>();
	lightsList = new JList<String>(lightsListModel);
	lightsList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] lights = model.getElectricalList();
	lightsListModel.clear();
	for (String l : lights) {
	    // model.setFanMasterFormat(f);
	    JPanel unit = new UncertaintyLightsPanel(model, l);// need to change this

	    lightsPanel.add(unit,l);
	    lightsListModel.addElement(l);
	    lightsList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (lightsPanel.getLayout());
		    String selection = lightsList.getSelectedValue().toString();
		    if (selection.equals(l)) {
			cardLayout.show(lightsPanel, selection);
		    }
		    model.getElectricalCostVector(selection);
		}
	    });
	}
	lightsScrollPane = new JScrollPane(lightsList);
    }
    
    private void updateCondenserUnit() {
	condenserUnitListModel = new DefaultListModel<String>();
	condenserUnitList = new JList<String>(condenserUnitListModel);
	condenserUnitList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] condenserUnit = model.getCondenserUnitList();
	condenserUnitListModel.clear();
	for (String c : condenserUnit) {
	    // model.setFanMasterFormat(f);
	    JPanel cu = new UncertaintyCUPanel(model, c);// need to change this
	    condenserUnitPanel.add(cu,c);
	    condenserUnitListModel.addElement(c);
	    condenserUnitList
		    .addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
			    CardLayout cardLayout = (CardLayout) (condenserUnitPanel
				    .getLayout());
			    String selection = condenserUnitList
				    .getSelectedValue().toString();
			    if (selection.equals(c)) {
				cardLayout.show(condenserUnitPanel, selection);
			    }
			    model.getCondenserUnitCostVector(selection);
			}
		    });
	}
	condenserUnitListScrollPane = new JScrollPane(condenserUnitList);
    }
    
    private void updateConvectionUnit(){
	convectionUnitListModel = new DefaultListModel<String>();
	convectionUnitList = new JList<String>(convectionUnitListModel);
	convectionUnitList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] units = model.getConvectionUnitList();
	convectionUnitListModel.clear();
	for (String c : units) {
	    // model.setFanMasterFormat(f);
	    JPanel unit = new UncertaintyConvectionUnitPanel(model, c);// need to change this

	    convectionUnitPanel.add(unit,c);
	    convectionUnitListModel.addElement(c);
	    convectionUnitList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (convectionUnitPanel.getLayout());
		    String selection = convectionUnitList.getSelectedValue().toString();
		    if (selection.equals(c)) {
			cardLayout.show(convectionUnitPanel, selection);
		    }
		    model.getConvectionUnitCostVector(selection);
		}
	    });
	}
	convectionUnitListScrollPane = new JScrollPane(convectionUnitList);
    }
    
    private void updateOpeningsPanel(){
	openingsListModel = new DefaultListModel<String>();
	openingsList = new JList<String>(openingsListModel);
	openingsList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] openings = model.getTransparentEnvelopeList();
	openingsListModel.clear();
	for (String o : openings) {
	    // model.setFanMasterFormat(f);
	    JPanel unit = new UncertaintyOpeningsPanel(model, o);// need to change this

	    openingsPanel.add(unit,o);
	    openingsListModel.addElement(o);
	    openingsList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (openingsPanel.getLayout());
		    String selection = openingsList.getSelectedValue().toString();
		    if (selection.equals(o)) {
			cardLayout.show(openingsPanel, selection);
		    }
		    model.getOpeningsCostVector(selection);
		}
	    });
	}
	openingsScrollPane = new JScrollPane(openingsList);
    }
    
    
    private JTabbedPane makeTabbedPanel(String construction) {
	JTabbedPane tp = new JTabbedPane();
	ArrayList<Material> materialList = model.getMaterialList(construction);
	for (int i = 0; i < materialList.size(); i++) {
	    tp.addTab(materialList.get(i).getName(), new UncertaintyMaterialPanel(model,
		    construction, i));
	}
	return tp;
    }
    
    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     * @throws Exception 
     */
    private static void createAndShowGUI() throws Exception {
	// Create and set up the window.
	JFrame frame = new JFrame("BEM-QTO Application V1");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//File file = new File(
	//	"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\FanCoil\\FanCoil.idf");
	//File file = new File(
	//	"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\ConstantAC\\SingleZoneCA.idf");
	File file = new File(
		"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\SimulationFile\\6.idf");
	//File file = new File(
	//	"C:\\Users\\Weili\\Desktop\\One_Montgomery_Plaza_appTest.idf");
	EnergyPlusModel model = new EnergyPlusModel(file);
	// Add content to the window.
	frame.add(new UncertaintyPanel(model));

	// Display the window.
	frame.pack();
	frame.setVisible(true);
    }
    
    public static void main(String[] args) {
	// Schedule a job for the event dispatch thread:
	// creating and showing this application's GUI.
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		try {
		    createAndShowGUI();
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	});
    }
    
    // for error info
    private static void showErrorDialog(Component c, String title, String msg) {
	JOptionPane.showMessageDialog(c, msg, title, JOptionPane.ERROR_MESSAGE);
    }

}
