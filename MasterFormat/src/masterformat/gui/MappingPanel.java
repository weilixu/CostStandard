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
    private final JPanel tablePanel;
    private final DefaultTableModel tableModel;
    private final JTable table;
    
    private final JPanel buttonPanel;
    private final JButton totalButton;
    private final String TOTAL_BUTTON = "Add total Cost to EnergyPlus";
    private final JButton totalOPButton;
    private final String TOTAL_OP_BUTTON = "Add total Incl O&P Cost to EnergyPlus";
    //temp there
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
	
	//set-up different category panels
	constructionPanel = new JPanel(new CardLayout());
	constructionPanel.setBackground(Color.WHITE);
	updateConstructions();
	
	boilerPanel = new JPanel(new CardLayout());
	boilerPanel.setBackground(Color.WHITE);
	updateBoilers();
	
	fanPanel = new JPanel(new CardLayout());
	fanPanel.setBackground(Color.WHITE);
	updateFans();

	
	//initialize the selection combo list
	objectSelectionCombo = new JComboBox<String>(model.getDomainList());
	objectSelectionCombo.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent evt) {
		String category = (String) evt.getItem();

		if (category.equals("Construction")) {
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel.getLayout();
		    EnergyPlusObjectPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();
		    
		    itemPanel.add(constructionPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(constructionListScrollPane, BorderLayout.CENTER);
		    
		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		    
		}else if(category.equals("Boiler")){
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel.getLayout();
		    EnergyPlusObjectPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();
		    
		    itemPanel.add(boilerPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(boilerListScrollPane, BorderLayout.CENTER);
		    
		    itemPanel.revalidate();
		    itemPanel.repaint();
		    EnergyPlusObjectPanel.revalidate();
		    EnergyPlusObjectPanel.repaint();
		}else if(category.equals("Fan")){
		    BorderLayout layout = (BorderLayout) EnergyPlusObjectPanel.getLayout();
		    EnergyPlusObjectPanel.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		    itemPanel.removeAll();
		    
		    itemPanel.add(fanPanel, BorderLayout.CENTER);
		    EnergyPlusObjectPanel.add(fanListScrollPane, BorderLayout.CENTER);
		    
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
	EnergyPlusObjectPanel.add(constructionListScrollPane, BorderLayout.CENTER);
	
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
		String category = (String) objectSelectionCombo.getSelectedItem();
		if(category.equals("Construction")){
			model.addTotalCostToComponentCost(constructionList.getSelectedValue()
				.toString(), category);  
		}else if(category.equals("Boiler")){
		    model.addTotalCostToComponentCost(boilerList.getSelectedValue().toString(), category);
		}else if(category.equals("Fan")){
		    model.addTotalCostToComponentCost(fanList.getSelectedValue().toString(), category);
		}

	    }
	});

	totalOPButton = new JButton(TOTAL_OP_BUTTON);
	totalOPButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String category = (String) objectSelectionCombo.getSelectedItem();
		if(category.equals("Construction")){
			model.addTotalOPCostToComponentCost(constructionList.getSelectedValue()
				.toString(), category);	    
		}else if(category.equals("Boiler")){
			model.addTotalOPCostToComponentCost(boilerList.getSelectedValue()
				.toString(), category);	
		}else if(category.equals("Fan")){
			model.addTotalOPCostToComponentCost(fanList.getSelectedValue()
				.toString(), category);	
		}

	    }

	});
	
	writeButton = new JButton(WRITE);
	writeButton.addActionListener(new ActionListener(){

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
	    constructionList.addListSelectionListener(new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent evt) {
		    CardLayout cardLayout = (CardLayout) (constructionPanel.getLayout());
		    String selection = constructionList.getSelectedValue().toString();
		    if (selection.equals(s)) {
			cardLayout.show(constructionPanel, selection);
		    }
		    model.getConstructionCostVector(selection);
		}
	    });
	}
	constructionListScrollPane = new JScrollPane(constructionList);
    }
    
    private void updateBoilers(){
	boilerListModel = new DefaultListModel<String>();
	boilerList = new JList<String>(boilerListModel);
	boilerList.setFont(new Font("Helvetica", Font.BOLD, 20));
	
	String[] boilers = model.getBoilerList();
	boilerListModel.clear();
	for(String s:boilers){
	    
	    model.setBoilerMasterFormat(s);
	    JPanel boiler = new BoilerPanel(model,s);// need to change this
	    boilerPanel.add(boiler, s);
	    
	    boilerListModel.addElement(s);
	    boilerList.addListSelectionListener(new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout)(boilerPanel.getLayout());
		    String selection = boilerList.getSelectedValue().toString();
		    if(selection.equals(s)){
			cardLayout.show(boilerPanel, selection);
		    }
		    model.getBoilerCostVector(selection);
		}
	    });
	    

	}
	
	boilerListScrollPane = new JScrollPane(boilerList);
    }
    
    private void updateFans(){
	fanListModel = new DefaultListModel<String>();
	fanList = new JList<String>(fanListModel);
	fanList.setFont(new Font("Helvetica", Font.BOLD, 20));
	
	String[] fans = model.getFanList();
	fanListModel.clear();
	for(String f:fans){
	    //model.setFanMasterFormat(f);
	    JPanel fan = new FanPanel(model,f);// need to change this
	    
	    fanPanel.add(fan);
	    fanListModel.addElement(f);
	    fanList.addListSelectionListener(new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
		    CardLayout cardLayout = (CardLayout)(fanPanel.getLayout());
		    String selection = fanList.getSelectedValue().toString();
		    if(selection.equals(f)){
			cardLayout.show(fanPanel, selection);
		    }
		    model.getFanCostVector(selection);
		}
		
	    });
	}
	fanListScrollPane = new JScrollPane(fanList);

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
	File file = new File(
		"C:\\Users\\Weili\\Desktop\\New folder\\CostTester.idf");

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
