package ml.util;

import java.io.Serializable;

import weka.classifiers.Classifier;

public class WekaClassifier implements Serializable{
	/**
	 * Serialized value
	 */
	private static final long serialVersionUID = 1L;
	private String classifierName;
	private Classifier classifier;
	private double rootMeanSquareError;
	
	public WekaClassifier(String dir, Classifier cls){
		classifierName = dir;
		classifier = cls;
	}
	
	public void setError(double error){
		rootMeanSquareError = error;
	}
	
	public String getClassifierName(){
		return classifierName;
	}
	
	public Classifier getClassifier(){
		return classifier;
	}
	
	public double getError(){
		return rootMeanSquareError;
	}
}
