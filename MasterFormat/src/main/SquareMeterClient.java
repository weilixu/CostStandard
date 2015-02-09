package main;
import javax.swing.SwingUtilities;

import masterformat.gui.SquareMeterCostPanel;
import masterformat.squarefoot.SquareMeterModel;


public class SquareMeterClient {
    
    public static void main(String[] args){
	SwingUtilities.invokeLater(new Runnable(){
	    @Override
	    public void run(){
		SquareMeterModel m = new SquareMeterModel();
		SquareMeterCostPanel gui = new SquareMeterCostPanel(m);
	    }
	});
    }
}
