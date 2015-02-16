package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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

import eplus.EnergyPlusModel;
import eplus.MaterialAnalyzer.Material;

public class MappingPanel extends JPanel {

    private final JPanel EnergyPlusObjectPanel;
    private final JPanel itemPanel;
    private final JPanel tablePanel;
    private final JTable table;

    private final EnergyPlusModel model;

    private final JComboBox<String> objectSelectionCombo;
    private final JList<String> itemLists;
    private final DefaultListModel<String> listModel;
    
    private final String[] costColumnName = {"Material Name","Material Cost ($)", "Labor Cost ($)","Equipment Cost ($)","Total ($)","Total Incl O&P ($)"};
    private final String[][] costData = {{"Unknown","0","0","0","0","0"}}; //temp, will move to wrapper model class later

    public MappingPanel(EnergyPlusModel m) {
	model = m;
	setLayout(new BorderLayout());
	
	itemPanel = new JPanel(new CardLayout());
	itemPanel.setBackground(Color.WHITE);
	EnergyPlusObjectPanel = new JPanel(new BorderLayout());
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	EnergyPlusObjectPanel.setBorder(raisedetched);
	

	listModel = new DefaultListModel<String>();
	itemLists = new JList<String>(listModel);
	itemLists.setFont(new Font("Helvetica", Font.BOLD, 20));

	objectSelectionCombo = new JComboBox<String>(model.getDomainList());
	objectSelectionCombo.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent evt) {
		String category = (String) evt.getItem();

		if (category.equals("Construction")) {
		    updateConstructions();
		}
	    }
	});
	
	EnergyPlusObjectPanel.add(objectSelectionCombo, BorderLayout.PAGE_START);
	EnergyPlusObjectPanel.add(itemLists, BorderLayout.CENTER);
	add(EnergyPlusObjectPanel, BorderLayout.WEST);
	
	tablePanel = new JPanel(new BorderLayout());
	table = new JTable(costData,costColumnName);
	table.setEnabled(false);
	table.setBackground(Color.WHITE);
	JScrollPane scrollPane = new JScrollPane(table);
	scrollPane.getViewport().setBackground(Color.WHITE);
	//scrollPane.setBackground(Color.WHITE);
	tablePanel.add(scrollPane, BorderLayout.CENTER);
	//tablePanel.setBackground(Color.WHITE);
	
	
	add(tablePanel,BorderLayout.PAGE_END);
	add(itemPanel, BorderLayout.CENTER);
    }

    private void updateConstructions() {
	String[] cons = model.getConstructionList();
	listModel.clear();
	itemPanel.removeAll();
	for (String s : cons) {
	    JTabbedPane tp = makeTabbedPanel(model.getMaterialList(s));
	    itemPanel.add(tp,s);
	    
	    listModel.addElement(s);
	    itemLists.addListSelectionListener(new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent evt) {
		    CardLayout cardLayout = (CardLayout)(itemPanel.getLayout());
		    String selection = itemLists.getSelectedValue().toString();
		    if(selection.equals(s)){
			cardLayout.show(itemPanel, selection);
		    }
		}
	    });
	}
	itemPanel.revalidate();
	itemPanel.repaint();
    }
    
    private JTabbedPane makeTabbedPanel(ArrayList<Material> materialList){
	JTabbedPane tp = new JTabbedPane();
	for(int i=0; i<materialList.size(); i++){
	    tp.addTab(materialList.get(i).getName(), new MaterialPanel(model,materialList.get(i).getProperties()));
	}
	return tp;
    }
    
    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("MasterFormatTreeDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	File file = new File("C:\\Users\\Weili\\Desktop\\New folder\\ScaifeHall.idf");
	
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
