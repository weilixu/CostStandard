package uncertainty.gui;

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
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    
    private final JComboBox<String> objectSelectionCombo;
    private JList<String> constructionList;
    private JScrollPane constructionListScrollPane;
    private DefaultListModel<String> constructionListModel;
    
    private final EnergyPlusModel model;
    
    public UncertaintyPanel(EnergyPlusModel m){
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
		}
	    }
	}); 
	
	itemPanel.add(constructionPanel, BorderLayout.CENTER);
	EnergyPlusObjectPanel
		.add(objectSelectionCombo, BorderLayout.PAGE_START);
	EnergyPlusObjectPanel.add(constructionListScrollPane,
		BorderLayout.CENTER);

	add(EnergyPlusObjectPanel, BorderLayout.WEST);
	
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
}
