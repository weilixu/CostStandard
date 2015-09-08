package eplus.optimization;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import eplus.IdfReader;
import eplus.construction.BuildingComponent;
import eplus.htmlparser.EnergyPlusHTMLParser;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

public class OPT1 extends Problem {
    private static Integer simulationCount = 0;
    private IdfReader originalData;
    private File analyzeFolder;
    private List<BuildingComponent> componentList;

    public OPT1(List<BuildingComponent> list, IdfReader data, File folder) {
	componentList = list;
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
	    upperLimit_[index] = comp.getSelectedComponents().length;
	    index++;
	}

	solutionType_ = new IntSolutionType(this);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
	Variable[] decisionVariables = solution.getDecisionVariables();
	IdfReader copiedData = originalData.cloneIdf();
	// modify the idf according to generated data
	for (int i = 0; i < decisionVariables.length; i++) {
	    Double value = (Double) decisionVariables[i].getValue();
	    BuildingComponent comp = componentList.get(i);
	    String name = comp.getSelectedComponentName(value.intValue());
	    comp.writeInEnergyPlus(copiedData, name);
	}
	// initialize simulation for optimization
	RunEplusOptimization optimization = new RunEplusOptimization(copiedData);
	optimization.setFolder(analyzeFolder);
	synchronized (this) {
	    simulationCount++;
	    optimization.setSimulationTime(simulationCount);
	}
	EnergyPlusHTMLParser parser = null;
	try {
	    parser = optimization.runSimulation();

	    solution.setObjective(0, parser.getEUI());
	    solution.setObjective(1, parser.getBudget());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
