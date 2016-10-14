package eplus.optimization;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * For IBPSA paper + 1MP case study
 * @author Weili
 *
 */
public class OPT6 {
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
   

}
