package eplus.optimization;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;
import eplus.EnergyPlusBuildingForHVACSystems;
import eplus.IdfReader;
import eplus.construction.BuildingComponent;
import eplus.construction.ComponentFactory;
import eplus.htmlparser.EnergyPlusHTMLParser;

/**
 * Same as OPT1, it implements a database which records all the simulated
 * objects.
 * 
 * @author Weili
 *
 */
public class OPT2 extends Problem {
    private static final Object lock = new Object();
    private static Integer simulationCount = 0;
    private IdfReader originalData;
    private File analyzeFolder;
    private EnergyPlusBuildingForHVACSystems bldg;
    private int skipedSimulation = 0;

    // private List<BuildingComponent> componentList;

    public OPT2(EnergyPlusBuildingForHVACSystems building, IdfReader data,
	    File folder) {
	// componentList = list;
	bldg = building;
	List<BuildingComponent> componentList = ComponentFactory
		.getFullComponentList(bldg);
	numberOfVariables_ = componentList.size();
	numberOfObjectives_ = 2; // budget and eui
	numberOfConstraints_ = 0;
	problemName_ = "Budget and EUI";
	originalData = data;
	analyzeFolder = folder;

	upperLimit_ = new double[numberOfVariables_];
	lowerLimit_ = new double[numberOfVariables_];

	Iterator<BuildingComponent> componentIterator = componentList
		.iterator();
	int index = 0;
	while (componentIterator.hasNext()) {
	    BuildingComponent comp = componentIterator.next();
	    lowerLimit_[index] = 0;
	    upperLimit_[index] = comp.getSelectedComponents().length - 1;
	    index++;
	}

	solutionType_ = new IntSolutionType(this);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
	List<BuildingComponent> componentList = ComponentFactory
		.getFullComponentList(bldg);
	Variable[] decisionVariables = solution.getDecisionVariables();
	IdfReader copiedData = originalData.cloneIdf();
	OptResult result = new OptResult();

	// initialize simulation for optimization
	// RunEplusOptimization optimization = new
	// RunEplusOptimization(copiedData);
	RunEplusOptimization optimization = null;
	Double cost = 0.0;
	// HVACSystem system = null;
	synchronized (OPT2.lock) {
	    // modify the idf according to generated data
	    for (int i = 0; i < decisionVariables.length; i++) {
		Double value = (Double) decisionVariables[i].getValue();
		BuildingComponent comp = componentList.get(i);
		String name = comp.getSelectedComponentName(value.intValue());
		result.addComponent(name);
		comp.writeInEnergyPlus(copiedData, name);
		// if(name.contains("HVAC")){
		// name = name.split(":")[0];
		// comp.writeInEnergyPlus(copiedData, name);
		// system = comp.getSystem();
		// }else{
		// comp.writeInEnergyPlus(copiedData, name);
		// }
	    }
	    simulationCount++;
	    // if there is any duplicate solutions
	    OptResult temp = bldg.duplicatedSimulationCase(result);
	    if (temp != null) {
		skipedSimulation++;
		System.out.println("find a duplicate case! " + skipedSimulation);
		result.setFirstCost(temp.getFirstCost());
		result.setOperationCost(temp.getOperationCost());
	    } else {
		optimization = new RunEplusOptimization(copiedData);
		optimization.setFolder(analyzeFolder);
		optimization.setSimulationTime(simulationCount);
		// html_dir = analyzeFolder.getAbsolutePath() + "\\" +
		// simulationCount + "\\"+simulationCount+"Table.html";
	    }
	}
	if (optimization != null) {
	    // non duplicate case
	    EnergyPlusHTMLParser parser = null;
	    try {
		parser = optimization.runSimulation();
		//this step is just to get HVAC system cost
		for (int i = 0; i < decisionVariables.length; i++) {
		    BuildingComponent comp = componentList.get(i);
		    cost = cost + comp.getComponentCost(parser.getDoc());
		}
		// EnergyPlusHTMLParser parser = new EnergyPlusHTMLParser(new
		// File(html_dir));
		// Double load = system.getTotalLoad(parser.getDoc());
		// Double unitCost = system.getUnitCost();
		// cost = load * unitCost/1000;
		// System.out.println(html_dir);
		// System.out.println();
		System.out.println("This is hvac cost: " + cost);
		System.out.println("This is total cost: " + cost + " "
			+ parser.getBudget());
		Double operationCost = parser.getOperationCost();
		Double totalCost = parser.getBudget() + cost;
		result.setFirstCost(totalCost);
		result.setOperationCost(operationCost);
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
	    System.out.println("This is total cost: " + result.getFirstCost());
	    solution.setObjective(0, result.getOperationCost());
	    solution.setObjective(1, result.getFirstCost());
	}

    }
}
