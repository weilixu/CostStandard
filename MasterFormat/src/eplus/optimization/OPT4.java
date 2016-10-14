package eplus.optimization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
 * This algorithm collects all the simulated data for metal-model training at each cycle.
 * @author Weili
 * 09/20/2016 changed the verification mode so that the verification step can be simulated.
 */
public class OPT4 extends Problem {
    /*
     * Adaptive regression algorithm parameters
     */
    private int generationNumForSim;
    private int generationNumForCir;
    private int trainNumber;
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
    private int population;
    
   // private List<BuildingComponent> componentList;

    public OPT4(EnergyPlusBuildingForHVACSystems building, IdfReader data,
	    File folder, int n, int Q, int p) {
	bldg = building;
	List<BuildingComponent> componentList = ComponentFactory
		.getPartialComponentList(bldg);
	originalData = data;
	analyzeFolder = folder;
	generationNumForSim = n;
	generationNumForCir = Q;
	trainNumber = 0;
	population = p;
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
	    FastVector fvNominalVal = new FastVector(comp.getSelectedComponents().length);
	    for(int i=0; i<upperLimit_[index]+1; i++){
		//System.out.println(comp.getSelectedComponentName(i));
		fvNominalVal.addElement(comp.getSelectedComponentName(i));
	    }
	    Attribute temp = new Attribute(comp.getName(),fvNominalVal);
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
		.getPartialComponentList(bldg);
	Variable[] decisionVariables = solution.getDecisionVariables();
	IdfReader copiedData = originalData.cloneIdf();
	OptResult result = new OptResult();
	boolean realSimulation = true;
	RunEplusOptimization optimization = null;
	Double cost = 0.0;
	// add one class index
	Instance o1Ins = new Instance(decisionVariables.length + 1);
	Instance o2Ins = new Instance(decisionVariables.length + 1);
	o1Ins.setDataset(o1TrainSet);
	o2Ins.setDataset(o2TrainSet);

	synchronized (OPT4.lock) {

	    // create flag that determine whether we can run on regression or
	    // real simulation
	    
	    Double generation = Math.floor(simulationCount / population);
	    int newGenCounter = (generation.intValue()) % generationNumForCir;
	    simulationCount++;
	    System.out.println(generation + " " + newGenCounter);
	    /*
	     * 1. Case run real simulation
	     */
	    if (newGenCounter < generationNumForSim) {
		realSimulation = true;
		System.out.println("Real Simulation");
		// 1.1 rebuild training data or continuously build up
		if (o1Classifier!=null & o2Classifier!=null) {
		    o1Classifier=null;
		    o2Classifier=null;
		    //o1TrainSet = new Instances(o1TrainSet, 0);
		    //o2TrainSet = new Instances(o2TrainSet, 0);
		}// if

		// 1.2 modify the idf according to generated data
		for (int i = 0; i < decisionVariables.length; i++) {
		    Double value = (Double) decisionVariables[i].getValue();
		    BuildingComponent comp = componentList.get(i);
		    //System.out.println(comp.getName());
		    int index = value.intValue();
		    String name = comp.getSelectedComponentName(index);
		    result.addComponent(name);
		    // add value
		    o1Ins.setValue(i, name);
		    o2Ins.setValue(i, name);
		    
		    comp.writeInEnergyPlus(copiedData, name);
		}// for

		// 1.3 duplicate case detection
		OptResult temp = bldg.duplicatedSimulationCase(result);
		if (temp != null) {
		    skipedSimulation++;
		    System.out.println("find a duplicate case! "
			    + skipedSimulation);
		    result.setFirstCost(temp.getFirstCost());
		    result.setOperationCost(temp.getOperationCost());
		    // start training data
		    o1Ins.setClassValue(temp.getOperationCost());
		    o2Ins.setClassValue(temp.getFirstCost());
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    // end of training data
		} else {
		    // set up real simulation
		    optimization = new RunEplusOptimization(copiedData);
		    optimization.setFolder(analyzeFolder);
		    optimization.setSimulationTime(simulationCount);
		}// if
	    } else {
		// 2 Regression Case
		realSimulation = false;
		result.setRegressionMode();
		System.out.println("Regression Simulation");

		if (o1Classifier==null&&o2Classifier==null) {
		    System.out
			    .println("Regression Simulation, need to train model");

		    // 2.1 build a new classifier if it is the start of
		    // simulation case
		    try {
			o1Classifier = trainModel(o1TrainSet);
			o2Classifier = trainModel(o2TrainSet);
			dataForplot();
		    } catch (Exception e) {
			System.err
				.println("Exception caught when classifying the training data");
			e.printStackTrace();
		    }// if
		}// if

		// o1Ins = o1TrainSet.instance(0);
		// o2Ins = o2TrainSet.instance(0);
		// 2.2 give design values to the instance
		for (int i = 0; i < decisionVariables.length; i++) {
		    Double value = (Double) decisionVariables[i].getValue();
		    BuildingComponent comp = componentList.get(i);

		    int index = value.intValue();

		    String name = comp.getSelectedComponentName(index);
		    result.addComponent(name);
		    o1Ins.setValue(i, name);
		    o2Ins.setValue(i, name);
		}// for
	    }// else
	}// synchronized

	/*
	 * 3. Done with settings, test results
	 */
	if (realSimulation) {
	    // 3.1 Require real simulation cases
	    if (optimization != null) {
		// 3.1.1 non duplicate case
		EnergyPlusHTMLParser parser = null;
		try {
		    parser = optimization.runSimulation();
		    // this step is just to get HVAC system cost
		    for (int i = 0; i < decisionVariables.length; i++) {
			BuildingComponent comp = componentList.get(i);
			cost = cost + comp.getComponentCost(parser.getDoc());
		    }// for
		    System.out.println("This is hvac cost: " + cost);
		    System.out.println("This is total cost: " + cost + " "
			    + parser.getBudget());
		    Double operationCost = parser.getOperationCost();
		    Double totalCost = parser.getBudget() + cost;
		    result.setFirstCost(totalCost);
		    result.setOperationCost(operationCost);
		    // training data
		    o1Ins.setClassValue(operationCost);
		    o2Ins.setClassValue(totalCost);
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    // training data
		    bldg.addOptimizationResult(result);

		    // feed back to the solutions
		    solution.setObjective(0, operationCost);
		    solution.setObjective(1, totalCost);

		} catch (IOException e) {
		    e.printStackTrace();
		}// try-catch
	    } else {
		// 3.1.2 find duplicate case in real simulation

		//System.out.println("This is not hvac cost: " + cost);
		//System.out.println("This is total cost: "
		//	+ result.getFirstCost());

		bldg.addOptimizationResult(result);

		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
	    }// if
	} else {
	    // 3.2 Regression mode
	    try {
		double operation = o1Classifier.classifyInstance(o1Ins);
		double capital = o2Classifier.classifyInstance(o2Ins);
		System.out.println("Classified Capital Cost: " + capital);
		System.out.println("Classified Operation Cost: " + operation);
		result.setFirstCost(capital);
		result.setOperationCost(operation);
		bldg.addOptimizationResult(result);

		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
	    } catch (Exception e) {
		System.err
			.print("Exception catched when classifying the costs");
		e.printStackTrace();
	    }// try-catch
	}// if
    }// end of method

    private Classifier trainModel(Instances data) throws Exception {
	String[] algorithm = {"SVM","LR"}; // algorithms
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

    private void dataForplot() {
	trainNumber++;
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < o1TrainSet.numInstances(); i++) {
	    try {
		Instance ins = o1TrainSet.instance(i);
		Instance insC = o2TrainSet.instance(i);
		double operation = o1Classifier.classifyInstance(ins);
		double capital = o2Classifier.classifyInstance(insC);
		for(int j=0; j<ins.numAttributes(); j++){
		    sb.append(ins.value(j));
		    sb.append(",");
		}
		sb.append(operation);
		sb.append(",");
		sb.append(capital);
		sb.append(",");
		sb.append(o1TrainSet.instance(i).classValue());
		sb.append(",");
		sb.append(o2TrainSet.instance(i).classValue());
		sb.append("\n");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    try {
		File file = new File(
			"E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\OneMP\\predict" + trainNumber + ".csv");
		if (!file.exists()) {
		    file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(sb.toString());
		bw.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
