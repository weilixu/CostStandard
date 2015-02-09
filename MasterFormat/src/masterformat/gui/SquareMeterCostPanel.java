package masterformat.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import masterformat.listener.SquareMeterCostModelListener;
import masterformat.squarefoot.BuildingType;
import masterformat.squarefoot.SquareMeterModel;

public class SquareMeterCostPanel extends JPanel implements SquareMeterCostModelListener{
    
    private final SquareMeterModel model;
    private int simulationNumber;
    private Double buildingSize;
    
    
    private final JFrame frame;
    
    private final JPanel outerPanel;
    private final JPanel displayPanel;
    private final JScrollPane displayView;
    
    
    private final JButton generateButton;
    private final JComboBox selection;
    
    
    
    public SquareMeterCostPanel(SquareMeterModel m){
	model = m;
	model.addCostModelListener(this);
	//for test only
	simulationNumber = 1000;
	buildingSize = 20000.0;
	
	// build the frame
	frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setPreferredSize(new Dimension(650, 500));
	frame.setResizable(true);
	
	//set up the panels
	
	outerPanel = new JPanel(new BorderLayout());
	
	selection = new JComboBox(BuildingType.values());
	outerPanel.add(selection, BorderLayout.PAGE_START);
	
	
	generateButton = new JButton("Generate");
	generateButton.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		model.setBuildingSize(buildingSize);
		model.setSimulationNumber(simulationNumber);
		model.setBuildingType((BuildingType)selection.getSelectedItem());
		model.generateSamples();
	    }
	});
	outerPanel.add(generateButton, BorderLayout.PAGE_END);
	
	displayPanel = new JPanel(new GridLayout(0,3));
	displayView = new JScrollPane(displayPanel);
	
	outerPanel.add(displayPanel, BorderLayout.CENTER);
	
	frame.add(outerPanel);
	frame.pack();
	frame.setVisible(true);
    }


    @Override
    public void costInfoUpdated(HashMap<String, double[]> costInfo) {
	displayPanel.removeAll();
	Set<String> keys = costInfo.keySet();
	Iterator<String> iterator = keys.iterator();
	while(iterator.hasNext()){
	    String key = iterator.next();
	    double[] tempData = costInfo.get(key);
	    
	    PlotHistogram p = new PlotHistogram(key, tempData);
	    displayPanel.add(p.createPanel());
	}
	displayPanel.revalidate();
	displayPanel.repaint();
    } 
}
