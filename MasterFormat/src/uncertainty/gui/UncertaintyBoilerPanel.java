package uncertainty.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import masterformat.listener.BoilerListener;
import eplus.EnergyPlusModel;

public class UncertaintyBoilerPanel extends JPanel implements BoilerListener{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    private final JPanel editorPanel;
    private final JScrollPane editorView;
    
    private final EnergyPlusModel model;
    private final String boilerName;

    public UncertaintyBoilerPanel(EnergyPlusModel m, String boiler){
	super(new BorderLayout());
	
	model = m;
	boilerName = boiler;
	
	editorPanel = new JPanel();
	editorPanel.setLayout(new GridLayout(2,0));
	editorPanel.setBackground(Color.WHITE);

	editorView = new JScrollPane(editorPanel);
	editorView.getViewport().setBackground(Color.WHITE);
	
	add(editorView, BorderLayout.CENTER);
    }
    
    @Override
    public String getName(){
	return boilerName;
    }

    @Override
    public void onQuanatitiesUpdates() {
	// TODO Auto-generated method stub	
    }
    
}
