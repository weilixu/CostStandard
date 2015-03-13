package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import masterformat.listener.CostTableListener;
import eplus.EnergyPlusModel;
import eplus.MaterialAnalyzer.Material;

public class MappingPanel extends JPanel implements CostTableListener {

    private final JPanel EnergyPlusObjectPanel;
    private final JPanel itemPanel;
    private final JPanel constructionPanel;
    private final JPanel boilerPanel;
    private final JPanel fanPanel;
    private final JPanel condenserUnitPanel;
    private final JPanel furnacePanel;
    private final JPanel pumpPanel;
    private final JPanel unitaryPanel;
    private final JPanel convectionUnitPanel;
    private final JPanel lightsPanel;
    private final JPanel tablePanel;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JPanel buttonPanel;
    private final JButton totalButton;
    private final String TOTAL_BUTTON = "Add total Cost to EnergyPlus";
    private final JButton totalOPButton;
    private final String TOTAL_OP_BUTTON = "Add total Incl O&P Cost to EnergyPlus";
    // temp there
    private final JButton writeButton;
    private final String WRITE = "Export to EnergyPlus";

    private final EnergyPlusModel model;

    private final JComboBox<String> objectSelectionCombo;
    private JList<String> constructionList;
    private JScrollPane constructionListScrollPane;
    private DefaultListModel<String> constructionListModel;
    private JList<String> boilerList;
    private JScrollPane boilerListScrollPane;
    private DefaultListModel<String> boilerListModel;
    private JList<String> fanList;
    private JScrollPane fanListScrollPane;
    private DefaultListModel<String> fanListModel;
    private JList<String> condenserUnitList;
    private JScrollPane condenserUnitListScrollPane;
    private DefaultListModel<String> condenserUnitListModel;
    private DefaultListModel<String> furnaceModel;
    private JList<String> furnaceList;
    private JScrollPane furnaceScrollPane;
    private DefaultListModel<String> pumpListModel;
    private JList<String> pumpList;
    private JScrollPane pumpListScrollPane;
    private DefaultListModel<String> unitaryListModel;
    private JList<String> unitaryList;
    private JScrollPane unitaryListScrollPane;
    private DefaultListModel<String> convectionUnitListModel;
    private JList<String> convectionUnitList;
    private JScrollPane convectionUnitListScrollPane;
    private DefaultListModel<String> lightsListModel;
    private JList<String> lightsList;
    private JScrollPane lightsScrollPane;

    // private final String[] costColumnName =
    // {"Material Name","Material Cost ($)",
    // "Labor Cost ($)","Equipment Cost ($)","Total ($)","Total Incl O&P ($)"};
    // private String[][] costData = {{"Unknown","0","0","0","0","0"}}; //temp,
    // will move to wrapper model class later

    public MappingPanel(EnergyPlusModel m) {
	model = m;
	model.addTableListener(this);

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

	boilerPanel = new JPanel(new CardLayout());
	boilerPanel.setBackground(Color.WHITE);
	updateBoilers();

	fanPanel = new JPanel(new CardLayout());
	fanPanel.setBackground(Color.WHITE);
	updateFans();

	condenserUnitPanel = new JPanel(new CardLayout());
	condenserUnitPanel.setBackground(Color.WHITE);
	updateCondenserUnit();

	furnacePanel = new JPanel(new CardLayout());
	furnacePanel.setBackground(Color.WHITE);
	updateFurnace();

	pumpPanel = new JPanel(new CardLayout());
	pumpPanel.setBackground(Color.WHITE);
	updatePump();
	
	unitaryPanel = new JPanel(new CardLayout());
	unitaryPanel.setBackground(Color.WHITE);
	updateUnitary();
	
	convectionUnitPanel = new JPanel(new CardLayout());
	convectionUnitPanel.setBackground(Color.WHITE);
	updateConvectionUnit();
	
	lightsPanel = new JPanel(new CardLayout());
	lightsPanel.setBackground(Color.WHITE);
	updateLightsPanel();

	// initialize the selection combo list
	objectSelectionCombo = new JComboBox<String>(model.getDomainList());
	objectSelectionCombo.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent evt) {
		String category = (String) evt.getItem();

		if (category.equals("Construction")) {
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
		} else if (category.equals("Fan")) {
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
		} else if (category.equalsIgnoreCase("PUMP")) {
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
		}else if(category.equalsIgnoreCase("UNITARY SYSTEM")){
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel
			    .getLayout();
		    EnergyPlusObjectPanel.remove(layout
			    .getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();

		    itemPanel.add(unitaryPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(unitaryListScrollPane,
			    BorderLayout.CENTER);

		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if(category.equalsIgnoreCase("CONVECTION UNIT")){
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
		}
	    }
	});

	itemPanel.add(constructionPanel, BorderLayout.CENTER);
	EnergyPlusObjectPanel
		.add(objectSelectionCombo, BorderLayout.PAGE_START);
	EnergyPlusObjectPanel.add(constructionListScrollPane,
		BorderLayout.CENTER);

	add(EnergyPlusObjectPanel, BorderLayout.WEST);

	tablePanel = new JPanel(new BorderLayout());

	tableModel = new DefaultTableModel();
	tableModel.addColumn("Product Name");
	tableModel.addColumn("Unit");
	tableModel.addColumn("Material Cost");
	tableModel.addColumn("Labor Cost");
	tableModel.addColumn("Equipment Cost");
	tableModel.addColumn("Total");
	tableModel.addColumn("Total Incl O&P");

	table = new JTable(tableModel);
	table.setEnabled(false);
	table.setBackground(Color.WHITE);
	JScrollPane scrollPane = new JScrollPane(table);
	scrollPane.getViewport().setBackground(Color.WHITE);
	// scrollPane.setBackground(Color.WHITE);
	tablePanel.add(scrollPane, BorderLayout.CENTER);
	// tablePanel.setBackground(Color.WHITE);

	totalButton = new JButton(TOTAL_BUTTON);
	totalButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		String category = (String) objectSelectionCombo
			.getSelectedItem();
		if (category.equals("Construction")) {
		    model.addTotalCostToComponentCost(constructionList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Boiler")) {
		    model.addTotalCostToComponentCost(boilerList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Fan")) {
		    model.addTotalCostToComponentCost(fanList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Condenser Unit")) {
		    model.addTotalCostToComponentCost(condenserUnitList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Furnace")) {
		    model.addTotalCostToComponentCost(furnaceList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Pump")) {
		    model.addTotalCostToComponentCost(pumpList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Unitary System")){
		    model.addTotalCostToComponentCost(unitaryList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Convection Unit")){
		    model.addTotalCostToComponentCost(convectionUnitList
			    .getSelectedValue().toString(), category);
		}
	    }
	});

	totalOPButton = new JButton(TOTAL_OP_BUTTON);
	totalOPButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String category = (String) objectSelectionCombo
			.getSelectedItem();
		if (category.equals("Construction")) {
		    model.addTotalOPCostToComponentCost(constructionList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Boiler")) {
		    model.addTotalOPCostToComponentCost(boilerList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Fan")) {
		    model.addTotalOPCostToComponentCost(fanList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Condenser Unit")) {
		    model.addTotalOPCostToComponentCost(condenserUnitList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Furnace")) {
		    model.addTotalOPCostToComponentCost(furnaceList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Pump")) {
		    model.addTotalOPCostToComponentCost(pumpList
			    .getSelectedValue().toString(), category);
		} else if(category.equals("Unitary System")){
		    model.addTotalOPCostToComponentCost(unitaryList
			    .getSelectedValue().toString(), category);
		} else if (category.equals("Convection Unit")){
		    model.addTotalOPCostToComponentCost(convectionUnitList
			    .getSelectedValue().toString(), category);
		}

	    }
	});

	writeButton = new JButton(WRITE);
	writeButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		model.writeIdf();
	    }
	});

	buttonPanel = new JPanel();
	buttonPanel.add(totalButton);
	buttonPanel.add(totalOPButton);
	buttonPanel.add(writeButton);

	tablePanel.add(buttonPanel, BorderLayout.PAGE_END);

	add(tablePanel, BorderLayout.PAGE_END);
	add(itemPanel, BorderLayout.CENTER);
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

    private void updateBoilers() {
	boilerListModel = new DefaultListModel<String>();
	boilerList = new JList<String>(boilerListModel);
	boilerList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] boilers = model.getBoilerList();
	boilerListModel.clear();
	for (String s : boilers) {

	    model.setBoilerMasterFormat(s);
	    JPanel boiler = new BoilerPanel(model, s);// need to change this
	    boilerPanel.add(boiler, s);

	    boilerListModel.addElement(s);
	    boilerList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (boilerPanel
			    .getLayout());
		    String selection = boilerList.getSelectedValue().toString();
		    if (selection.equals(s)) {
			cardLayout.show(boilerPanel, selection);
			model.getBoilerCostVector(selection);
		    }
		}
	    });

	}

	boilerListScrollPane = new JScrollPane(boilerList);
    }

    private void updateFans() {
	fanListModel = new DefaultListModel<String>();
	fanList = new JList<String>(fanListModel);
	fanList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] fans = model.getFanList();
	fanListModel.clear();
	for (String f : fans) {
	    // model.setFanMasterFormat(f);
	    JPanel fan = new FanPanel(model, f);// need to change this

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

    private void updateCondenserUnit() {
	condenserUnitListModel = new DefaultListModel<String>();
	condenserUnitList = new JList<String>(condenserUnitListModel);
	condenserUnitList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] condenserUnit = model.getCondenserUnitList();
	condenserUnitListModel.clear();
	for (String c : condenserUnit) {
	    // model.setFanMasterFormat(f);
	    JPanel cu = new CondenserUnitPanel(model, c);// need to change this
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

    private void updateFurnace() {
	furnaceModel = new DefaultListModel<String>();
	furnaceList = new JList<String>(furnaceModel);
	furnaceList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] furnaces = model.getFurnaceList();
	furnaceModel.clear();

	for (String f : furnaces) {

	    model.setFurnaceMasterFormat(f);
	    JPanel furnace = new FurnacePanel(model, f);// need to change this
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

    private void updatePump() {
	pumpListModel = new DefaultListModel<String>();
	pumpList = new JList<String>(pumpListModel);
	pumpList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] pumps = model.getPumpList();
	pumpListModel.clear();
	for (String p : pumps) {
	    // model.setFanMasterFormat(f);
	    JPanel pump = new PumpPanel(model, p);// need to change this

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
    
    private void updateUnitary() {
	unitaryListModel = new DefaultListModel<String>();
	unitaryList = new JList<String>(unitaryListModel);
	unitaryList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] unitaries = model.getUnitaryList();
	unitaryListModel.clear();
	for (String u : unitaries) {
	    // model.setFanMasterFormat(f);
	    JPanel unitary = new UnitaryPanel(model, u);// need to change this

	    unitaryPanel.add(unitary,u);
	    unitaryListModel.addElement(u);
	    unitaryList.addListSelectionListener(new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout) (unitaryPanel.getLayout());
		    String selection = unitaryList.getSelectedValue().toString();
		    if (selection.equals(u)) {
			cardLayout.show(unitaryPanel, selection);
		    }
		    model.getUnitaryCostVector(selection);
		}
	    });
	}
	unitaryListScrollPane = new JScrollPane(unitaryList);
    }
    
    private void updateConvectionUnit(){
	convectionUnitListModel = new DefaultListModel<String>();
	convectionUnitList = new JList<String>(convectionUnitListModel);
	convectionUnitList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] units = model.getConvectionUnitList();
	convectionUnitListModel.clear();
	for (String c : units) {
	    // model.setFanMasterFormat(f);
	    JPanel unit = new ConvectionUnitPanel(model, c);// need to change this

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
    
    public void updateLightsPanel(){
	lightsListModel = new DefaultListModel<String>();
	lightsList = new JList<String>(lightsListModel);
	lightsList.setFont(new Font("Helvetica", Font.BOLD, 20));

	String[] lights = model.getElectricalList();
	lightsListModel.clear();
	for (String l : lights) {
	    // model.setFanMasterFormat(f);
	    JPanel unit = new LightsPanel(model, l);// need to change this

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

    private JTabbedPane makeTabbedPanel(String construction) {
	JTabbedPane tp = new JTabbedPane();
	ArrayList<Material> materialList = model.getMaterialList(construction);
	for (int i = 0; i < materialList.size(); i++) {
	    tp.addTab(materialList.get(i).getName(), new MaterialPanel(model,
		    construction, i));
	}
	return tp;
    }

    @Override
    public void onCostTableUpdated(String[][] data) {
	if (tableModel.getRowCount() > 0) {
	    for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
		tableModel.removeRow(i);
	    }
	}
	for (int j = 0; j < data.length; j++) {
	    tableModel.addRow(data[j]);
	}
	tableModel.fireTableDataChanged();
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("MasterFormatTreeDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//File file = new File(
	//	"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\FanCoil\\FanCoil.idf");
	//File file = new File(
	//	"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\ConstantAC\\SingleZoneCA.idf");
	File file = new File(
		"C:\\Users\\Weili\\Dropbox\\BCD-weili\\CostDatabase\\IBPSA-LLC\\EnergyPlus Model\\SimulationFile\\1.idf");
	//File file = new File(
	//	"C:\\Users\\Weili\\Desktop\\One_Montgomery_Plaza_appTest.idf");
	EnergyPlusModel model = new EnergyPlusModel(file);
	// Add content to the window.
	frame.add(new MappingPanel(model));

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
