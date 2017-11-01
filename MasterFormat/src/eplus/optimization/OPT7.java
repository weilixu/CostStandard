package eplus.optimization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.construction.BuildingComponent;
import eplus.construction.ComponentFactory;
import eplus.htmlparser.EnergyPlusHTMLParser;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntRealSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;
import ml.util.WekaUtil;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * For Toshiba - 3 objectives
 * 
 * @author Weili
 *
 */
public class OPT7 extends Problem {
    /*
     * Adaptive regression algorithm parameters
     */
    private int generationNumForSim;
    private int generationNumForCir;
    private int trainNumber;
    private Instances o1TrainSet;
    private Instances o2TrainSet;
    private Instances o3TrainSet;
    private FastVector fvO1Attributes;
    private FastVector fvO2Attributes;
    private FastVector fvO3Attributes;
    private Classifier o1Classifier;
    private Classifier o2Classifier;
    private Classifier o3Classifier;

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
    private int IntegerIndex;
    private int NumericIndex;

    public OPT7(EnergyPlusBuildingForHVACSystems building, IdfReader data,
	    File folder, int n, int Q, int p) {
	bldg = building;
	List<BuildingComponent> componentList = ComponentFactory
		.getScaifeHallComponentList(bldg);
	originalData = data;
	analyzeFolder = folder;
	generationNumForSim = n;
	generationNumForCir = Q;
	trainNumber = 0;
	population = p;
	int size = ComponentFactory.getNumberOfVariable(componentList);
	// System.out.println("The size of the opti: " + size);

	/*
	 * set-up optimization parameters
	 */
	numberOfVariables_ = size;
	numberOfObjectives_ = 3; // budget and llc and euis
	numberOfConstraints_ = 0;
	problemName_ = "Budget and EUI";

	fvO1Attributes = new FastVector(numberOfVariables_);
	fvO2Attributes = new FastVector(numberOfVariables_);
	fvO3Attributes = new FastVector(numberOfVariables_);
	upperLimit_ = new double[numberOfVariables_];
	lowerLimit_ = new double[numberOfVariables_];
	Iterator<BuildingComponent> componentIterator = componentList
		.iterator();

	// Integer variables
	IntegerIndex = 0;
	while (componentIterator.hasNext()) {
	    BuildingComponent comp = componentIterator.next();
	    if (comp.isIntegerTypeComponent()) {
		lowerLimit_[IntegerIndex] = 0;
		if (comp.getName().equals("lpd")
			|| comp.getName().equals("window")
			|| comp.getName().equals("Insulation")) {
		    upperLimit_[IntegerIndex] = comp
			    .getSelectedComponents().length - 1;
		} else {
		    // System.out.println(comp.getName());
		    upperLimit_[IntegerIndex] = comp
			    .getSelectedComponents().length - 1;
		}
		FastVector fvNominalVal = new FastVector(
			comp.getSelectedComponents().length);
		for (int i = 0; i < upperLimit_[IntegerIndex] + 1; i++) {
		    // System.out.println(comp.getSelectedComponentName(i));
		    fvNominalVal.addElement(comp.getSelectedComponentName(i));
		}
		// System.out.println(comp.getName());
		Attribute temp = new Attribute(comp.getName(), fvNominalVal);
		fvO1Attributes.addElement(temp);
		fvO2Attributes.addElement(temp);
		fvO3Attributes.addElement(temp);
	    }
	    IntegerIndex++;
	}

	/*
	 * Numerical variables
	 */
	NumericIndex = 0;
	componentIterator = componentList.iterator();
	while (componentIterator.hasNext()) {
	    BuildingComponent comp = componentIterator.next();
	    if (!comp.isIntegerTypeComponent()) {
		for (int i = 0; i < comp.getNumberOfVariables(); i++) {
		    String property = comp.getSelectedComponentName(i);
		    String[] propertyList = property.split(":");

		    String name = propertyList[0];
		    Double lower = Double.valueOf(propertyList[1]);
		    Double higher = Double.valueOf(propertyList[2]);
		    lowerLimit_[IntegerIndex + NumericIndex] = lower;
		    upperLimit_[IntegerIndex + NumericIndex] = higher;

		    Attribute temp = new Attribute(name);
		    fvO1Attributes.addElement(temp);
		    fvO2Attributes.addElement(temp);
		    fvO3Attributes.addElement(temp);
		}
	    }
	}

	solutionType_ = new IntSolutionType(this); // need to offset 1

	/*
	 * Add class and initiate data
	 */
	Attribute capitalcost = new Attribute("CapitalCost");
	Attribute operationcost = new Attribute("OperationCost");
	Attribute euiCost = new Attribute("Energy");
	fvO1Attributes.addElement(operationcost);
	fvO2Attributes.addElement(capitalcost);
	fvO3Attributes.addElement(euiCost);
	o1TrainSet = new Instances("Rel", fvO1Attributes, 0);
	o2TrainSet = new Instances("Rel", fvO2Attributes, 0);
	o3TrainSet = new Instances("Rel", fvO3Attributes, 0);
	o1TrainSet.setClassIndex(o1TrainSet.numAttributes() - 1);
	o2TrainSet.setClassIndex(o2TrainSet.numAttributes() - 1);
	o3TrainSet.setClassIndex(o3TrainSet.numAttributes() - 1);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
	// set up values
	List<BuildingComponent> componentList = ComponentFactory
		.getScaifeHallComponentList(bldg);
	Variable[] decisionVariables = solution.getDecisionVariables();
	IdfReader copiedData = originalData.cloneIdf();
	OptResult result = new OptResult();
	boolean realSimulation = true;
	RunEplusOptimization optimization = null;
	Double cost = 0.0;
	// add one class index
	Instance o1Ins = new Instance(decisionVariables.length + 1);
	Instance o2Ins = new Instance(decisionVariables.length + 1);
	Instance o3Ins = new Instance(decisionVariables.length + 1);
	o1Ins.setDataset(o1TrainSet);
	o2Ins.setDataset(o2TrainSet);
	o3Ins.setDataset(o3TrainSet);

	synchronized (OPT7.lock) {
	    System.out.println(simulationCount);

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
		if (o1Classifier != null && o2Classifier != null && o3Classifier != null ) {
		    o1Classifier = null;
		    o2Classifier = null;
		    o3Classifier = null;
		    // o1TrainSet = new Instances(o1TrainSet, 0);
		    // o2TrainSet = new Instances(o2TrainSet, 0);
		} // if

		// 1.2 modify the idf according to generated data
		int counter = 0;
		int componentCounter = 0;
		while (counter < decisionVariables.length) {

		    BuildingComponent comp = componentList
			    .get(componentCounter);
		    // System.out.println(comp.getName());

		    if (comp.isIntegerTypeComponent()) {
			// System.out.println("Counter " + counter + " " +
			// decisionVariables.length);
			Double value = decisionVariables[counter].getValue();

			// System.out.println(comp.getName());
			int index = value.intValue();

			String name = comp.getSelectedComponentName(index);

			result.addComponent(name);
			// add value
			o1Ins.setValue(counter, name);
			o2Ins.setValue(counter, name);
			o3Ins.setValue(counter, name);

			comp.writeInEnergyPlus(copiedData, name);
			// System.out.println("Complete writing");
			counter++;
			componentCounter++; // count the component;
		    } else {
			HashMap<String, Double> property = new HashMap<String, Double>();
			for (int i = 0; i < comp.getNumberOfVariables(); i++) {
			    Double value = decisionVariables[counter]
				    .getValue();
			    String propertyName = comp
				    .getSelectedComponentName(i).split(":")[0];

			    result.addNumericValues(value);
			    property.put(propertyName, value);
			    o1Ins.setValue(counter, value);
			    o2Ins.setValue(counter, value);
			    o3Ins.setValue(counter, value);
			    counter++;// count the variables
			}
			// System.out.println(comp.getName());
			comp.readsInProperty(property, "");
			comp.writeInEnergyPlus(copiedData, "");
			componentCounter++; // count the component
		    } // ELSE
		} // while

		// 1.3 duplicate case detection
		OptResult temp = bldg.duplicatedSimulationCase(result);
		if (temp != null) {
		    skipedSimulation++;
		    System.out.println(
			    "find a duplicate case! " + skipedSimulation);
		    result.setFirstCost(temp.getFirstCost());
		    result.setOperationCost(temp.getOperationCost());
		    result.setEUI(temp.getEUI());
		    // start training data
		    o1Ins.setClassValue(temp.getOperationCost());
		    o2Ins.setClassValue(temp.getFirstCost());
		    o3Ins.setClassValue(temp.getEUI());
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    o3TrainSet.add(o3Ins);
		    // end of training data
		} else {
		    // set up real simulation
		    optimization = new RunEplusOptimization(copiedData);
		    optimization.setFolder(analyzeFolder);
		    optimization.setSimulationTime(simulationCount);
		} // if
	    } else {
		// 2 Regression Case
		realSimulation = false;
		result.setRegressionMode();
		System.out.println("Regression Simulation");

		if (o1Classifier == null && o2Classifier == null && o3Classifier==null) {
		    System.out.println(
			    "Regression Simulation, need to train model");

		    // 2.1 build a new classifier if it is the start of
		    // simulation case
		    try {
			o1Classifier = trainModel(o1TrainSet);
			o2Classifier = trainModel(o2TrainSet);
			o3Classifier = trainModel(o3TrainSet);
			dataForplot();
		    } catch (Exception e) {
			System.err.println(
				"Exception caught when classifying the training data");
			e.printStackTrace();
		    } // if
		} // if

		// o1Ins = o1TrainSet.instance(0);
		// o2Ins = o2TrainSet.instance(0);
		// 2.2 give design values to the instance
		int counter = 0;
		int componentCounter = 0;
		while (counter < decisionVariables.length) {
		    BuildingComponent comp = componentList
			    .get(componentCounter);
		    if (comp.isIntegerTypeComponent()) {
			Double value = decisionVariables[counter].getValue();
			int index = value.intValue();

			String name = comp.getSelectedComponentName(index);
			result.addComponent(name);
			o1Ins.setValue(counter, name);
			o2Ins.setValue(counter, name);
			o3Ins.setValue(counter, name);
			counter++;
			componentCounter++;
		    } else {
			for (int i = 0; i < comp.getNumberOfVariables(); i++) {
			    Double value = decisionVariables[counter]
				    .getValue();
			    result.addNumericValues(value);
			    o1Ins.setValue(counter, value);
			    o2Ins.setValue(counter, value);
			    o3Ins.setValue(counter, value);
			    counter++;
			} // for
			componentCounter++;
		    }
		} // while
	    } // else
	} // synchronized

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
		    for (int i = 0; i < componentList.size(); i++) {
			BuildingComponent comp = componentList.get(i);
			cost = cost + comp.getComponentCost(parser.getDoc());
		    } // for
		    System.out.println("This is hvac cost: " + cost);
		    System.out.println("This is total cost: " + cost + " "
			    + parser.getBudget());
		    Double operationCost = parser.getOperationCost();
		    Double totalCost = parser.getBudget() + cost;
		    Double eui = parser.getEUI();
		    
		    result.setEUI(eui);
		    result.setFirstCost(totalCost);
		    result.setOperationCost(operationCost);
		    // training data
		    o1Ins.setClassValue(operationCost);
		    o2Ins.setClassValue(totalCost);
		    o3Ins.setClassValue(eui);
		    o1TrainSet.add(o1Ins);
		    o2TrainSet.add(o2Ins);
		    o3TrainSet.add(o3Ins);
		    // training data
		    bldg.addOptimizationResult(result);

		    // feed back to the solutions
		    solution.setObjective(0, operationCost);
		    solution.setObjective(1, totalCost);
		    solution.setObjective(2, eui);

		} catch (IOException e) {
		    e.printStackTrace();
		} // try-catch
	    } else {
		// 3.1.2 find duplicate case in real simulation

		// System.out.println("This is not hvac cost: " + cost);
		// System.out.println("This is total cost: "
		// + result.getFirstCost());

		bldg.addOptimizationResult(result);

		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
		solution.setObjective(2, result.getEUI());
	    } // if
	} else {
	    // 3.2 Regression mode
	    try {
		double operation = o1Classifier.classifyInstance(o1Ins);
		double capital = o2Classifier.classifyInstance(o2Ins);
		double eui = o3Classifier.classifyInstance(o3Ins);
		System.out.println("Classified Capital Cost: " + capital);
		System.out.println("Classified Operation Cost: " + operation);
		System.out.println("Classified EUI: " + eui);
		result.setFirstCost(capital);
		result.setOperationCost(operation);
		result.setEUI(eui);
		bldg.addOptimizationResult(result);

		solution.setObjective(0, result.getOperationCost());
		solution.setObjective(1, result.getFirstCost());
		solution.setObjective(2, result.getEUI());
	    } catch (Exception e) {
		System.err
			.print("Exception catched when classifying the costs");
		e.printStackTrace();
	    } // try-catch
	} // if

    }

    private Classifier trainModel(Instances data) throws Exception {
	String[] algorithm = { "SVM", "LR" }; // algorithms
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
		Instance insEui = o3TrainSet.instance(i);
		double operation = o1Classifier.classifyInstance(ins);
		double capital = o2Classifier.classifyInstance(insC);
		double eui = o2Classifier.classifyInstance(insEui);
		for (int j = 0; j < ins.numAttributes(); j++) {
		    sb.append(ins.value(j));
		    sb.append(",");
		}
		sb.append(operation);
		sb.append(",");
		sb.append(capital);
		sb.append(",");
		sb.append(eui);
		sb.append(",");
		sb.append(o1TrainSet.instance(i).classValue());
		sb.append(",");
		sb.append(o2TrainSet.instance(i).classValue());
		sb.append(",");
		sb.append(o3TrainSet.instance(i).classValue());
		sb.append("\n");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    try {
		File file = new File(
			"E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\OneMP\\predict"
				+ trainNumber + ".csv");
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
