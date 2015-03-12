package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import eplus.IdfReader;
import eplus.IdfReader.ElementList;
import eplus.IdfReader.ValueNode;

public class EplusDomainPanel extends JPanel implements TreeSelectionListener {

    private static final String FILE_PATH = "C:\\Users\\Weili\\Desktop\\CSL.idf";

    private final IdfReader eplusData;
    private JTree tree;

    private final JPanel editorPanel;

    public EplusDomainPanel() {
	super(new GridLayout(1, 0));

	// initialize the data
	eplusData = new IdfReader();
	eplusData.setFilePath(FILE_PATH);

	try {
	    eplusData.readEplusFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	DefaultMutableTreeNode root = eplusData.createTree();

	tree = new JTree(root);
	tree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	tree.addTreeSelectionListener(this);

	JScrollPane treeView = new JScrollPane(tree);

	editorPanel = new JPanel();
	editorPanel.setLayout(new GridLayout(0, 6));
	editorPanel.setBackground(Color.WHITE);

	JScrollPane editorView = new JScrollPane(editorPanel);

	// Add the scroll panes to a split pane.
	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	splitPane.setTopComponent(treeView);
	splitPane.setBottomComponent(editorView);

	Dimension minimumSize = new Dimension(100, 50);
	editorView.setMinimumSize(minimumSize);
	treeView.setMinimumSize(minimumSize);
	splitPane.setDividerLocation(100);
	splitPane.setPreferredSize(new Dimension(500, 300));

	// Add the split pane to this panel.
	add(splitPane);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();

	if (node == null)
	    return;

	Object nodeInfo = node.getUserObject();
	if (node.isLeaf()) {
	    ElementList eList = (ElementList) nodeInfo;
	    displayData(eList.getInfo());
	}
    }

    private void displayData(ArrayList<ValueNode> data) {
	editorPanel.removeAll();
	for (int i = 0; i < data.size(); i++) {

	    JTextField tempField = new JTextField(data.get(i).getAttribute());
	    Border blackline = BorderFactory.createLineBorder(Color.black);
	    TitledBorder title;
	    title = BorderFactory.createTitledBorder(blackline, data.get(i).getDescription());
	    title.setTitleJustification(TitledBorder.LEFT);
	    tempField.setBorder(title);
	    tempField.setPreferredSize(new Dimension(150, 50));
	    tempField.setAlignmentX(Component.LEFT_ALIGNMENT);
	    editorPanel.add(tempField);
	}
	editorPanel.revalidate();
	editorPanel.repaint();
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("EplusTreeDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// Add content to the window.
	frame.add(new EplusDomainPanel());

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
