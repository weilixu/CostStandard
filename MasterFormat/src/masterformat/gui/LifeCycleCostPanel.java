package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import eplus.lifecyclecost.DataObjects;
import eplus.lifecyclecost.FieldElement;
import eplus.lifecyclecost.LifeCycleCostModel;
import eplus.lifecyclecost.TemplateObject;

public class LifeCycleCostPanel extends JPanel implements TreeSelectionListener {

    private final LifeCycleCostModel model;
    private JTree tree;
    private final JScrollPane treeView;

    private final JPanel editorPanel;
    private final JPanel tablePanel;
    private final JScrollPane editorView;

    private final JSplitPane splitPane;

    private final JFrame frame;

    public LifeCycleCostPanel(LifeCycleCostModel m) {
	super(new GridLayout(0, 1));
	model = m;

	frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setPreferredSize(new Dimension(650, 500));
	frame.setResizable(true);

	tree = new JTree(model.getCompleteTreeNode());
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);
	treeView = new JScrollPane(tree);

	editorPanel = new JPanel();
	editorPanel.setLayout(new BorderLayout());
	editorPanel.setBackground(Color.WHITE);

	tablePanel = new JPanel();
	tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
	tablePanel.setBackground(Color.WHITE);

	editorView = new JScrollPane(editorPanel);

	splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	splitPane.setTopComponent(treeView);
	splitPane.setBottomComponent(editorView);

	add(splitPane);

	frame.add(this);
	frame.pack();
	frame.setVisible(true);
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
	    DataObjects object = (DataObjects) nodeInfo;
	    DataObjects copiedObject = model.makeCopyOfObject(object);
	    displayObject(copiedObject);
	}
    }

    private void displayObject(DataObjects dataSet) {
	editorPanel.removeAll();
	tablePanel.removeAll();
	JTextField dataSetName = new JTextField(dataSet.getSetName());
	dataSetName.setBackground(Color.darkGray);
	dataSetName.setFont(new Font("Impact", Font.BOLD, 22));
	dataSetName.setForeground(Color.WHITE);
	dataSetName.setEditable(false);
	editorPanel.add(dataSetName, BorderLayout.PAGE_START);

	ArrayList<TemplateObject> objects = dataSet.getObjects();
	for (TemplateObject object : objects) {
	    JPanel tempPanel = new JPanel(new BorderLayout());
	    JTextField objectName = new JTextField(object.getObject());
	    objectName.setBackground(Color.BLACK);
	    objectName.setFont(new Font("Impact", Font.BOLD, 14));
	    objectName.setForeground(Color.WHITE);
	    objectName.setEditable(false);
	    
	    tempPanel.add(objectName,BorderLayout.PAGE_START);
	    ArrayList<FieldElement> fields = object.getFieldList();
	    String[] columnNames = { "Field", "Inputs", "Minimum", "Maximum" };
	    Object[][] data = new Object[fields.size()][4];
	    if (object.getReference().equals("Template")) {
		for (int i = 0; i < fields.size(); i++) {
		    data[i][0] = fields.get(i).getDescription();
		    data[i][2] = fields.get(i).getMinimum();
		    data[i][3] = fields.get(i).getMaximum();
		    if (fields.get(i).isKeyElement()) {
			data[i][1] = new JComboBox<String>(fields.get(i)
				.getOptionList()) {
			    @Override
			    public String toString() {
				return "Options";
			    }
			};
		    } else {
			data[i][1] = fields.get(i).getType();
		    }
		}
	    } else {
		for (int i = 0; i < fields.size(); i++) {
		    data[i][0] = fields.get(i).getDescription();
		    data[i][1] = fields.get(i).getValue();
		}
	    }

	    JTable table = new JTable(data, columnNames) {
		@Override
		public TableCellEditor getCellEditor(int row, int column) {
		    Object value = super.getValueAt(row, column);
		    if (value != null) {
			if (value instanceof JComboBox) {
			    return new DefaultCellEditor((JComboBox) value);
			}
			return getDefaultEditor(value.getClass());
		    }
		    return super.getCellEditor(row, column);
		}
	    };

	    table.getColumnModel().getColumn(1)
		    .setCellRenderer(new DefaultTableCellRenderer());
	    table.setAlignmentY(Component.TOP_ALIGNMENT);
	    JScrollPane scrollPane = new JScrollPane(table);

	    tempPanel.add(scrollPane, BorderLayout.CENTER);
	    tablePanel.add(tempPanel);
	    tablePanel.setBackground(Color.WHITE);

	}

	editorPanel.add(tablePanel, BorderLayout.CENTER);


	editorPanel.revalidate();
	editorPanel.repaint();
    }
}
