package eplus.optimization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.Ranking;

public class ManualSort {

    public static void main(String[] args) {
	//String csvFile = "E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\CSL\\Optimization\\forSort.csv";
	String csvFile = "E:\\02_Weili\\02_ResearchTopic\\PhD Case Study\\OneMp\\forSort.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";

	ArrayList<String[]> objectives = new ArrayList<String[]>();

	try {
	    //skip the first line
	    
	    br = new BufferedReader(new FileReader(csvFile));
	    line = br.readLine();
	    while ((line = br.readLine()) != null) {

		// use comma as separator
		String[] twoObj = line.split(cvsSplitBy);

		objectives.add(twoObj);

	    }

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	
	SolutionSet testSet = new SolutionSet();
	testSet.setCapacity(objectives.size());
	for(int i=0; i<objectives.size(); i++){
	    String[] tempob = objectives.get(i);
	    Solution sol = new Solution(2);
	    sol.setObjective(0, Double.parseDouble(tempob[0]));
	    sol.setObjective(1, Double.parseDouble(tempob[1]));
	    testSet.add(sol);
	}
	
	//System.out.println(testSet.size());
	Ranking rank = new Ranking(testSet);
	
	Distance dist = new Distance();
	
	SolutionSet sortedSet = new SolutionSet();
	sortedSet.setCapacity(objectives.size());
	for(int j=0; j<rank.getNumberOfSubfronts(); j++){
	    SolutionSet temp = rank.getSubfront(j);
	    //System.out.println(temp.size());
	    dist.crowdingDistanceAssignment(temp, 2);
	    
	    for(int k=0; k<temp.size(); k++){
		sortedSet.add(temp.get(k));
	    }
	}
	
	for(int q=0; q<sortedSet.size(); q++){
	    Solution sol = sortedSet.get(q);
	    System.out.println(sol.getObjective(0)+", " + sol.getObjective(1));
	}
    }

}
