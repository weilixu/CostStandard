package main;

import javax.swing.SwingUtilities;

import masterformat.gui.LifeCycleCostPanel;
import eplus.lifecyclecost.LifeCycleCostModel;

public class LifeCycleCostClient {
    
    public static void main(String[] args){
	SwingUtilities.invokeLater(new Runnable(){
	    @Override
	    public void run(){
		LifeCycleCostModel m = new LifeCycleCostModel();
		LifeCycleCostPanel gui = new LifeCycleCostPanel(m);
	    }
	});
    }
}
