package masterformat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import jmetal.util.JMException;
import eplus.EnergyPlusModel;

public class OptimizationPanel extends JPanel {
    /*
     * Set model and files;
     */
    private final EnergyPlusModel model;

    /*
     * set the execuation buttons
     */
    private final JButton startOptimization;

    // panel to set-up
    //private final JPanel objectivePanel;
    // panel to choose algorithm
    private final JPanel algorithmPanel;
    private final JPanel algorithmPropPanel;
    // panel that includes objective and algorithm
    private final JPanel inputPanel;
    
    //mother panel holds all the related information
    private final JPanel analysisPanel;
    //carries all the design variable
    private final OptimizationVariablePanel optVarPanel;
    private final JButton targetSwitch;

    // updates the optimization process
    //private final JPanel graphPanel;
    // updates the simulation status
    private final JPanel statusPanel;

    // algorithmPanel;
    private final JComboBox<String> algorithms;
    
    //algorithm input set
    //genetic algorithm
    private JLabel concurrentLabel = new JLabel("Number of Concurrent:");
    private JLabel populationLabel = new JLabel("Population:");
    private JLabel maxEvalLabel = new JLabel("Max Evaluation:");
    private JLabel mutationProbLabel = new JLabel("Mutation probability:");
    private JLabel crossOverProbLabel = new JLabel("Crossover probability:");
    
    private JTextField concurrentText = new JTextField("4");
    private JTextField populationText = new JTextField("30");
    private JTextField maxEvalText = new JTextField("300");
    private JTextField mutationProbText = new JTextField("0.9");
    private JTextField crossOverProbText = new JTextField("0.5");
    
    //swarm algorithm
    private JLabel swarmSizeLabel = new JLabel("Swarm size:");
    private JLabel archiveSizeLabel = new JLabel("Archive size:");
    
    private JTextField swarmSizeText = new JTextField("100");
    private JTextField archiveSizeText = new JTextField("100");
    
    //Adaptive NSGAII algorithm additional input
    private JLabel actualSimLabel = new JLabel("Number of iterations use simulation:");
    private JLabel cycleSimLabel = new JLabel("Number of iterations in a Cycle:");
    
    private JTextField actualSimText = new JTextField("3");
    private JTextField cycleSimText = new JTextField("20");
    
    // status
    private final JTextField statusTextField;

    // alldata related to inputs
    //private final String[] optimizationTypeList = { "Single-Objective",
    //	"Multi-Objectives" };
    //private final String[] objectiveList = { "EUI" };
    private final String[] algorithmList = {"NSGAII","Adaptive NSGAII","Particle Swarm"};
    private String targetLabel = "New Construction";

    public OptimizationPanel(EnergyPlusModel m){
	setLayout(new BorderLayout());
	model = m;
	
	inputPanel = new JPanel();
	inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.Y_AXIS));
	inputPanel.setBorder( BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	//inputPanel.setBackground(Color.WHITE);
	
	optVarPanel = new OptimizationVariablePanel(model);
	optVarPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Variable"));

	//set-up objectivePanel

	//objectivePanel.add(objectives);
	//inputPanel.add(objectivePanel);

	//set-up algorithm panel
	algorithmPanel = new JPanel();
	algorithmPanel.setLayout(new BorderLayout());
	
	algorithmPropPanel = new JPanel();
	algorithmPropPanel.setLayout(new BoxLayout(algorithmPropPanel, BoxLayout.Y_AXIS));
	algorithmPropPanel.add(concurrentLabel);
	algorithmPropPanel.add(concurrentText);
	algorithmPropPanel.add(populationLabel);
	algorithmPropPanel.add(populationText);
	algorithmPropPanel.add(maxEvalLabel);
	algorithmPropPanel.add(maxEvalText);
	algorithmPropPanel.add(mutationProbLabel);
	algorithmPropPanel.add(mutationProbText);
	algorithmPropPanel.add(crossOverProbLabel);
	algorithmPropPanel.add(crossOverProbText);	
	
	algorithms = new JComboBox<String>(algorithmList);
	algorithms.addItemListener(new ItemListener(){
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		// set-up algorithms panel
		String algName = (String) e.getItem();
		
		if(algName.equals("Particle Swarm")){
		    algorithmPropPanel.removeAll();
		    //re-add components;
		    algorithmPropPanel.add(concurrentLabel);
		    algorithmPropPanel.add(concurrentText);
		    
		    algorithmPropPanel.add(swarmSizeLabel);
		    algorithmPropPanel.add(swarmSizeText);
		    
		    algorithmPropPanel.add(archiveSizeLabel);
		    algorithmPropPanel.add(archiveSizeText);
		    
		    algorithmPropPanel.add(maxEvalLabel);
		    algorithmPropPanel.add(maxEvalText);
		    
		    algorithmPropPanel.add(mutationProbLabel);
		    algorithmPropPanel.add(mutationProbText);
		    
		    algorithmPropPanel.revalidate();
		    algorithmPropPanel.repaint();
		}else if(algName.equals("Adaptive NSGAII")){
		    //adaptive nsga ii algorithm
		    algorithmPropPanel.removeAll();
		    
		    algorithmPropPanel.add(concurrentLabel);
		    algorithmPropPanel.add(concurrentText);
		    algorithmPropPanel.add(populationLabel);
		    algorithmPropPanel.add(populationText);
		    algorithmPropPanel.add(maxEvalLabel);
		    algorithmPropPanel.add(maxEvalText);
		    algorithmPropPanel.add(mutationProbLabel);
		    algorithmPropPanel.add(mutationProbText);
		    algorithmPropPanel.add(crossOverProbLabel);
		    algorithmPropPanel.add(crossOverProbText);
		    
		    algorithmPropPanel.add(actualSimLabel);
		    algorithmPropPanel.add(actualSimText);
		    algorithmPropPanel.add(cycleSimLabel);
		    algorithmPropPanel.add(cycleSimText);
		    algorithmPropPanel.revalidate();
		    algorithmPropPanel.repaint();
		}else if(algName.equals("NSGAII")){
		    //nsga ii algorithm
		    algorithmPropPanel.removeAll();
		    
		    algorithmPropPanel.add(concurrentLabel);
		    algorithmPropPanel.add(concurrentText);
		    algorithmPropPanel.add(populationLabel);
		    algorithmPropPanel.add(populationText);
		    algorithmPropPanel.add(maxEvalLabel);
		    algorithmPropPanel.add(maxEvalText);
		    algorithmPropPanel.add(mutationProbLabel);
		    algorithmPropPanel.add(mutationProbText);
		    algorithmPropPanel.add(crossOverProbLabel);
		    algorithmPropPanel.add(crossOverProbText);
		    
		    algorithmPropPanel.revalidate();
		    algorithmPropPanel.repaint();
		}
	    }
	});
	
	algorithmPanel.add(algorithms, BorderLayout.PAGE_START);
	algorithmPanel.add(algorithmPropPanel, BorderLayout.CENTER);
	inputPanel.add(algorithmPanel);
	
	startOptimization = new JButton("Start");
	startOptimization.addActionListener(new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    model.BudgetEUIOptimization();
		} catch (ClassNotFoundException | JMException e1) {
		    e1.printStackTrace();
		}
	    }
	});
	
	inputPanel.add(startOptimization);
	add(inputPanel,BorderLayout.WEST);
	
	//done with algorithm set-up
	analysisPanel = new JPanel();
	analysisPanel.setLayout(new BorderLayout());
	
	//A fixed text shown the objectives of this study
	JLabel objectiveLabel = new JLabel("Objective 1: Capital cost, Objective 2: Operation Cost,  " + targetLabel);
	objectiveLabel.setOpaque(true);
	objectiveLabel.setBackground(Color.lightGray);
	objectiveLabel.setFont(new Font("Verdana", Font.BOLD,12));
	analysisPanel.add(objectiveLabel, BorderLayout.PAGE_START);
	
	targetSwitch = new JButton("Switch Study Goal");
	targetSwitch.setToolTipText("Switch the study goal between new construction design optimization and retrofit design optimization");
	targetSwitch.addActionListener(new ActionListener(){
	    @Override
	    public void actionPerformed(ActionEvent e){
		optVarPanel.switchTarget();
		if(targetLabel.equals("New Construction")){
		    targetLabel = "Retrofit";
		}else{
		    targetLabel = "New Construction";
		}
		objectiveLabel.setText("Objective 1: Capital cost, Objective 2: Operation Cost  " + targetLabel);
	    }
	});

//	graphPanel = new JPanel();
//	add(graphPanel,BorderLayout.CENTER);
	analysisPanel.add(optVarPanel, BorderLayout.CENTER);
	analysisPanel.add(targetSwitch, BorderLayout.PAGE_END);
	add(analysisPanel);
	//status
	statusPanel = new JPanel();
	statusPanel.setLayout(new BorderLayout());
	
	statusTextField = new JTextField("STATUS:");
	statusPanel.add(statusTextField, BorderLayout.CENTER);
	add(statusPanel,BorderLayout.PAGE_END);
    }
    
}
