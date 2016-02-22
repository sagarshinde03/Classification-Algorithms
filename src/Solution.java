/**
 * 
 */

/**
 * @author Sagar Shinde
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import DecisionTree.DecisionTree;
import NaiveBayes.NaiveBayesClassification;
import NaiveBayes.ReadData;

public class Solution {
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Which Classification Algorithm do you want to use?");
		System.out.println("Enter 1 for Naive Bayes\nEnter 2 for Decision Tree\nEnter 3 for Random Forest");
		int algorithmNumber=Integer.parseInt(br.readLine());
		if(algorithmNumber==1){
			System.out.println("Enter the name of the file you want to process:");
			String datasetName=br.readLine();
			datasetName=getProperDatasetName(datasetName, algorithmNumber);
			String[][] fileData = ReadData.scanFile(datasetName);
			System.out.println("Enter the value of K in K-fold Cross Validation.");
			int kFold = Integer.parseInt(br.readLine());
			NaiveBayesClassification bayesClassification = new NaiveBayesClassification(fileData , kFold);
			bayesClassification.startClasssication();
		}else if(algorithmNumber==2){
			System.out.println("Enter the dataset you want to process");
			String datasetName=br.readLine();
			datasetName=getProperDatasetName(datasetName, algorithmNumber);
			new DecisionTree().decisionTreeImplementation(datasetName, algorithmNumber);
		}else if(algorithmNumber==3){
			System.out.println("Enter the dataset you want to process");
			String datasetName=br.readLine();
			datasetName=getProperDatasetName(datasetName, algorithmNumber);
			new DecisionTree().decisionTreeImplementation(datasetName, algorithmNumber);
		}else{System.out.println("Please enter a proper number");}
		br.close();
	}
	static String getProperDatasetName(String datasetName, int algorithmNumber){
		datasetName="project3_"+datasetName;
		if(datasetName.endsWith("dataset3") && algorithmNumber!=1) datasetName+="_train";
		return datasetName;
	}
}
