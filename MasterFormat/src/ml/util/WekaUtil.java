package ml.util;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.trees.M5P;
import weka.core.Instances;

public final class WekaUtil {
	
	//private static String fileName="";
	private static String currentAlg="";
	//private static final String SEPERATOR = ",";

	public static double classifierBuildElapsedTime = 0.0;
	public static StringBuilder highLevelReporting = new StringBuilder();
	public static StringBuilder lowLevelReporting = new StringBuilder();
	
	
	private static String[] m5pOptions = {"-U","-N"};
	private static String[] svmOptions = {"-C","0.5"};
	private static String[] nnOptions = {"-L","0.3","-M","0.2","-N","500","-H","i"};
	private static String[] lrOptions = {"-S","2","-C"};
	
	/**
	 * get classifier after selection process is complete
	 * @param classifierName
	 * @return
	 */
	public static Classifier getClassifier(String classifierName){
		Classifier classifier = null;
		if(classifierName.equals("M5P")){
			classifier = new M5P();
			try {
				
				classifier.setOptions(m5pOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("SVM")){
			classifier = new SMOreg();
			try {
				classifier.setOptions(svmOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("NN")){
			classifier = new MultilayerPerceptron();
			try {
				classifier.setOptions(nnOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("LR")){
			classifier = new LinearRegression();
			try {
				classifier.setOptions(lrOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Given classifier name "+ classifierName + " can not be found");
		}
		return classifier;
	}
	
	/**
	 * setting the options for the learning classifiers
	 * @param data
	 * @param classfierName
	 * @return
	 */
	public static Classifier buildClassifier(Instances data, String classifierName){
		Classifier classifier = null;
		if(classifierName.equals("M5P")){
			classifier = new M5P();
			try {
				classifier.setOptions(m5pOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("SVM")){
			classifier = new SMOreg();
			try {
				classifier.setOptions(svmOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("NN")){
			classifier = new MultilayerPerceptron();
			try {
				classifier.setOptions(nnOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classifierName.equals("LR")){
			classifier = new LinearRegression();
			try {
				classifier.setOptions(lrOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Given classifier name "+ classifierName + " can not be found");
			return classifier;
		}
		currentAlg = classifierName;
		System.out.println("Building "+currentAlg + " classifier...");
		long classifierBuildStartTime = System.nanoTime();
		//classifier.setOptions(options);
		try{
			classifier.buildClassifier(data);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		long classifierBuildEndTime = System.nanoTime();
		classifierBuildElapsedTime = (
				(classifierBuildEndTime - classifierBuildStartTime) /
				 1000000000.0);
		
		System.out.println(currentAlg + " classifier building finished in " +
				   classifierBuildElapsedTime + " seconds");
		return classifier;
	}
	
	/**
	 * Evaluate model performance using 10-fold cross validation.
	 * @input Instances: contains data and attributes in a instances format
	 * 		  Classifier: the classifier model
	 * @param data
	 * @param model
	 * @return
	 */
	public static double evaluateCrossValidate(Instances data, Classifier model){
		System.out.println("\n\nEvaluate with cross validation... Classifier: "+ model);
		//StringBuilder sb = new StringBuilder();
		Evaluation eval = null;
		try{
			eval = new Evaluation(data);
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Evaluation object created");
		
		double timeElapsed = 0;
		try{
			long validatorBuildStartTime = System.nanoTime();
			eval.crossValidateModel(model, data, 10, new Random(1));
			long validatorBuilderEndTime = System.nanoTime();
			timeElapsed = (validatorBuilderEndTime - validatorBuildStartTime)/1000000000.0;
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Cross validate model finished in "+timeElapsed + " seconds");
		
		int numAttributes = data.numAttributes();
		double meanAbsError = eval.meanAbsoluteError();
		double rootmeanSQ = eval.rootMeanSquaredError();
		
		System.out.println("Number of Attributes: "+ numAttributes);
		System.out.println("mean absolute Error: " + meanAbsError);
		System.out.println("root mean square error: "+ rootmeanSQ);
		
		return meanAbsError;
	}
}