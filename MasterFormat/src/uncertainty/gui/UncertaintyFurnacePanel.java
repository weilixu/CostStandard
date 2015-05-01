package uncertainty.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eplus.EnergyPlusModel;
import masterformat.listener.FurnaceListener;


public class UncertaintyFurnacePanel extends JPanel implements FurnaceListener{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final JPanel editorPanel;
    private final JScrollPane editorView;

    private final EnergyPlusModel model;
    private final String furnace;

    public UncertaintyFurnacePanel(EnergyPlusModel m, String furnaceName){
	super(new BorderLayout());

	model = m;
	furnace = furnaceName;
	
	editorPanel = new JPanel();
	editorPanel.setLayout(new GridLayout(2,0));
	editorPanel.setBackground(Color.WHITE);
	editorView = new JScrollPane(editorPanel);
	editorView.getViewport().setBackground(Color.WHITE);
	
	add(editorView, BorderLayout.CENTER);
    }
    
    public String getName(){
	return furnace;
    }

    @Override
    public void onQuanatitiesUpdates() {
	// TODO Auto-generated method stub
	
    }
}
