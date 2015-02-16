package main;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MappingPanel extends JPanel{
    
    private final MaterialPanel materialTree;
    private final JPanel mapPanel;    
    
    public MappingPanel(){
	mapPanel = new JPanel(new BorderLayout());
	
	materialTree = new MaterialPanel();

    }
    
    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
	// Create and set up the window.
	JFrame frame = new JFrame("MasterFormatTreeDemo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	// Add content to the window.
	frame.add(new MaterialPanel());

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
