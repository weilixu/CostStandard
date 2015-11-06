package eplus.optimization;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ml.util.WekaUtil;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.construction.BuildingComponent;
import eplus.construction.ComponentFactory;
import eplus.htmlparser.EnergyPlusHTMLParser;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

/**
 * Optimization problem with dynamic updates on regression model This is adapted
 * from Kalyanmoy Deb and Pawan K.S. Nain "An Evolutionary Multi-Objective
 * Adaptive Meta-modeling Procedure Using Artificial Neural Networks"
 * 
 * @author Weili
 *
 */
public class OPT3 extends Problem {
    /*
     * Adaptive regression algorithm parameters
     */
    private int generationNumForSim;
    private int generationNumForCir;
    private Instances o1TrainSet;
    private Instances o2TrainSet;
    private FastVector fvO1Attributes;
    private FastVector fvO2Attributes;
    private Classifier o1Classifier;
    private Classifier o2Classifier;
    /*
     * Energy Simulation Parameters
     */
    private static final Object lock = new Object();
    private static Integer simulationCount = 0;
    private IdfReader originalData;
    private File analyzeFolder;
    private EnergyPlusBuildingForHVACSystems bldg;
    private int skipedSimulation = 0;

    // private List<BuildingComponent> componentList;

    public OPT3(EnergyPlusBuildingForHVACSystems building, IdfReader data,
	    File folder, int n, int Q) {
	bldg = building;
	List<BuildingComponent> componentList = ComponentFactory
		.getFullComponentList(bldg);
	originalData = data;
	analyzeFolder = folder;
	generationNumForSim = n;
	generationNumForCir = Q;
	fvO1Attributes = new FastVector(componentList.size());
	fvO2Attributes = new FastVector(componentList.size());

	/*
	 * set-up optimization parameters
	 */
	numberOfVariables_ = componentList.size();
	numberOfObjectives_ = 2; // budget and eui
	numberOfConstraints_ = 0;
	problemName_ = "Budget and EUI";

	upperLimit_ = new double[numberOfVariables_];
	lowerLimit_ = new double[numberOfVariables_];
	Iterator<BuildingComponent> componentIterator = componentList
		.iterator();
	int index = 0;
	while (componentIterator.hasNext()) {
	    BuildingComponent comp = componentIterator.next();
	    lowerLimit_[index] = 0;
	    upperLimit_[index] = comp.getSelectedComponents().length - 1;
	    Attribute temp = new Attribute(comp.getClass().getName());
	    fvO1Attributes.addElement(temp);
	    fvO2Attributes.addElement(temp);

	    index++;
	}
	solutionType_ = new IntSolutionType(this);

	Attribute capitalcost = new Attribute("CapitalCost");
	Attribute operationcost = new Attribute("OperationCost");
	fvO1Attributes.addElement(operationcost);
	fvO2Attributes.addElement(capitalcost);
	o1TrainSet = new Instances("Rel", fvO1Attributes, 0);
	o2TrainSet = new Instances("Rel", fvO2Attributes, 0);
	o1TrainSet.setClassIndex(o1TrainSet.numAttributes() - 1);
	o2TrainSet.setClassIndex(o2TrainSet.numAttributes() - 1);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
	// set up values
	List<BuildingComponent> componentList = ComponentFactory
		.getFullComponentList(bldg);
	Variable[] decisionVariables = solution.getDecisionVariables();
	IdfReader copiedData = originalData.cloneIdf();
	OptResult result = new OptResult();
	boolean realSimulation = true;
	RunEplusOptimization optimization = null;
	Double cost = 0.0;
	//add one class index
	Instance o1Ins = new Instance(decisionVariables.length + 1);
	Instance o2Ins = new Instance(decisionVariables.length + 1);

	synchronized (OPT3.lock) {
	    simulationCount++;
	    // check if it needs to add training data or create regression model
	    int newGenCounter = simulationCount % generationNumForCir;
	    if (newGenCounter <= generationNumForSim) {
		// rebuild training data or continuesly build up
		if (newGenCounter == 0) {
		    o1TrainSet = new Instances(o1TrainSet, 0);
		    o2TrainSet = new Instances(o2TrainSet, 0);
		}
		// case when performing real simulation
		// modify the idf according to generated data
		for (int i = 0; i < decisionVariables.length; i++) {
		    Double value = (Double) decisionVariables[i].getValue();
		    BuildingComponent comp = componentList.get(i);
		    int index = value.intValue();
		    // add value
		    o1Ins.setValue(i, index);
		    o2Ins.setValue(i, index);
		    //
		    String name = comp.getSelectedComponentName(index);
		    result.addComponent(name);
		    comp.writeInEnergyPlus(copiedData, name);
		}

		// if there is any duplicate solutions
		OptResult temp = bldg.duplicatedSimulationCase(result);
		if (temp != null) {
		    skipedSimulation++;
		    System.out.println("find a duplicate case! "
			    + skipedSimulation);
		    result.setFirstCost(temp.getFirstCost());
		    result.setOperationCost(temp.getOperationCost());
		    // training data
		    o1Ins.setClassValue(temp.getOperationCost()
			    / bldg.getTotalBuildingArea());
		    o2Ins.setClassValue(temp.getFirstCost()
			    / bldg.getTotalBuildingArea());
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    // training data
		} else {
		    optimization = new RunEplusOptimization(copiedData);
		    optimization.setFolder(analyzeFolder);
		    optimization.setSimulationTime(simulationCount);
		}
	    } else {
		realSimulation = false;
		if (newGenCounter == generationNumForSim + 1) {
		    // train data case
		    try {
			o1Classifier = trainModel(o1TrainSet);
			o2Classifier = trainModel(o2TrainSet);
		    } catch (Exception e) {
			System.err
				.println("Exception caught when classifying the training data");
		    }
		}
		o1Ins = o1TrainSet.instance(0);
		o2Ins = o2TrainSet.instance(0);
		for (int i = 0; i < decisionVariables.length; i++) {
		    Double value = (Double) decisionVariables[i].getValue();
		    BuildingComponent comp = componentList.get(i);

		    int index = value.intValue();
		    o1Ins.setValue(i, index);
		    o2Ins.setValue(i, index);

		    String name = comp.getSelectedComponentName(index);
		    result.addComponent(name);
		}
	    }
	}
	
	/*
	 * Done with settings, test whether simulation / regression
	 */
	if (realSimulation) {
	    if (optimization != null) {
		// non duplicate case
		EnergyPlusHTMLParser parser = null;
		try {
		    parser = optimization.runSimulation();
		    // this step is just to get HVAC system cost
		    for (int i = 0; i < decisionVariables.length; i++) {
			BuildingComponent comp = componentList.get(i);
			cost = cost + comp.getComponentCost(parser.getDoc());
		    }
		    System.out.println("This is hvac cost: " + cost);
		    System.out.println("This is total cost: " + cost + " "
			    + parser.getBudget());
		    Double operationCost = parser.getOperationCost();
		    Double totalCost = parser.getBudget() + cost;
		    result.setFirstCost(totalCost);
		    result.setOperationCost(operationCost);
		    // training data
		    o1Ins.setClassValue(operationCost
			    / bldg.getTotalBuildingArea());
		    o2Ins.setClassValue(totalCost / bldg.getTotalBuildingArea());
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    // training data
		    bldg.addOptimizationResult(result);

		    solution.setObjective(0, operationCost);
		    solution.setObjective(1, totalCost);

		} catch (IOException e) {
		    e.printStackTrace();
		}
	    } else {
		// duplicate case
		bldg.addOptimizationResult(result);
		System.out.println("This is not hvac cost: " + cost);
		System.out.println("This is total cost: "
			+ result.getFirstCost());
		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
	    }
	} else {
	    try {
		double operation = o1Classifier.classifyInstance(o1Ins)
			* bldg.getTotalBuildingArea();
		double capital = o2Classifier.classifyInstance(o2Ins)
			* bldg.getTotalBuildingArea();
		System.out.println("Classified Capital Cost: " + capital);
		System.out.println("Classified Operation Cost: " + operation);
		result.setFirstCost(capital);
		result.setOperationCost(operation);
		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
	    } catch (Exception e) {
		System.err
			.print("Exception catched when classifying the costs");
	    }
	}
    }

    private Classifier trainModel(Instances data) throws Exception {
	String[] algorithm = { "M5P", "SVM", "NN", "LR" }; // algorithms
	String bestSelected = null;
	double minRootMeanError = Double.MAX_VALUE;
	for (int i = 0; i < algorithm.length; i++) {
	    String currentAlg = algorithm[i];
	    Classifier temp = WekaUtil.getClassifier(currentAlg);
	    double tempRME = WekaUtil.evaluateCrossValidate(data, temp);
	    if (minRootMeanError >= tempRME) {
		minRootMeanError = tempRME;
		bestSelected = currentAlg;
	    }
	}
	return WekaUtil.buildClassifier(data, bestSelected);
    }
}
