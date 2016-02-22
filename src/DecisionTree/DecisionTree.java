/**
 * 
 */
package DecisionTree;

/**
 * @author Sagar Shinde
 *
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import DecisionTree.Tree.Node;
import DecisionTree.Tree.Tree;
import MeasureCalculation.MeasureCalculation;

public class DecisionTree {
	ArrayList<MeasureCalculation> records=new ArrayList<MeasureCalculation>();
	public void decisionTreeImplementation(String datasetName, int algorithmNumber){
		try{
			double trainPercentage=0;
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			int K=1;
			if(!datasetName.equals("project3_dataset3_train")){
				System.out.println("Enter the value of K in K-fold Cross Validation.");
				K=Integer.parseInt(br.readLine());
				trainPercentage=((double)(K-1)/K)*100;
			}
			System.out.println("Which method do you want to use for constructing Decision Tree?\nEnter 1 for GINI Index\nEnter 2 for Entropy(Information Gain)\nEnter 3 for Misclassification error");
			int method=Integer.parseInt(br.readLine());
			int numDecisionTrees=0;
			/*if(algorithmNumber==3){
				System.out.println("How many Decision Trees do you want to create?");
				numDecisionTrees=Integer.parseInt(br.readLine());
			}*/
			for(int i=0;i<K;i++) {
				if(algorithmNumber==2) implement(datasetName, trainPercentage, method);
				else if(algorithmNumber==3) implementRandomForest(datasetName, trainPercentage, method, numDecisionTrees);
			}
			if(!datasetName.equals("project3_dataset3_train")) new MeasureCalculation().calculateMeasures(records);
			br.close();
		} catch(IOException e) {
			System.out.println("Please ensure that the file name is correct and file is present at the correct location.");
			System.out.println(e.toString());e.printStackTrace();
		} catch(NumberFormatException e){
			System.out.println("Number format exception occured");System.out.println(e.toString());e.printStackTrace();
		} catch(IllegalArgumentException e){
			System.out.println("Invalid input entered.");System.out.println(e.toString());e.printStackTrace();
		} catch(Exception e){
			System.out.println("Oops! Some error occured.");System.out.println(e.toString());e.printStackTrace();
		}
	}
	
	void implement(String datasetName, double trainPercentage, int method){
		try{
			ArrayList<String[][]> arraylists=new ArrayList<String[][]>();
			GetFileData gfd=new GetFileData();
			arraylists=gfd.getFileData(datasetName, trainPercentage);
			String[][] trainData=arraylists.get(0);
			String[] trainTruth=flatten(arraylists.get(1));
			String[][] testData=arraylists.get(2);
			String[] testTruth=flatten(arraylists.get(3));
			Tree tree=new Tree();
			Node root;
			if(method==1) root=tree.constructDecisionTreeUsingGINIIndex(trainData,trainTruth);
			else if(method==2) root=tree.constructDecisionTreeUsingEntropy(trainData,trainTruth);
			else if(method==3) root=tree.constructDecisionTreeUsingMisclassificationError(trainData,trainTruth);
			else throw new IllegalArgumentException();
			String[] generatedTestTruth=tree.test(testData, root, method);
			if(!datasetName.equals("project3_dataset3_train")){
				MeasureCalculation mc=new MeasureCalculation(generatedTestTruth, testTruth);
				records.add(mc);
			}else{
				display(generatedTestTruth);
			}
		}catch(FileNotFoundException e){
			System.out.println("Invalid file name!");System.out.println(e.toString());e.printStackTrace();
		}
	}
	
	void implementRandomForest(String datasetName, double trainPercentage, int method, int numDecisionTrees) {
		try{
			ArrayList<String[][]> arraylists=new ArrayList<String[][]>();
			GetFileData gfd=new GetFileData();
			arraylists=gfd.getFileData(datasetName, trainPercentage);
			String[][] trainData=arraylists.get(0);
			String[] trainTruth=flatten(arraylists.get(1));
			String[][] testData=arraylists.get(2);
			String[] testTruth=flatten(arraylists.get(3));
			numDecisionTrees=(int)Math.sqrt(trainData.length);
			if(numDecisionTrees%2==0) numDecisionTrees--;
			ArrayList<String[][]> multipleTrainData=divideTrainData(trainData, numDecisionTrees);
			ArrayList<String[]> multipleTrainTruth=divideTrainTruth(trainTruth, numDecisionTrees);
			ArrayList<String[]> tempTruth=new ArrayList<String[]>();
			for(int i=0;i<multipleTrainData.size();i++){
				String[][] trainDataa=multipleTrainData.get(i);
				String[] trainTruthh=multipleTrainTruth.get(i);
				Tree tree=new Tree();
				Node root;
				if(method==1) root=tree.constructDecisionTreeUsingGINIIndex(trainDataa,trainTruthh);
				else if(method==2) root=tree.constructDecisionTreeUsingEntropy(trainDataa,trainTruthh);
				else if(method==3) root=tree.constructDecisionTreeUsingMisclassificationError(trainDataa,trainTruthh);
				else throw new IllegalArgumentException();
				String[] generatedTestTruth=tree.test(testData, root, method);
				tempTruth.add(generatedTestTruth);
			}
			String[] generatedTestTruth=getFinalTestTruth(tempTruth);
			if(!datasetName.equals("project3_dataset3_train")){
				MeasureCalculation mc=new MeasureCalculation(generatedTestTruth, testTruth);
				records.add(mc);
			}else{
				display(generatedTestTruth);
			}
		}catch(FileNotFoundException e){
			System.out.println("Invalid file name!");System.out.println(e.toString());e.printStackTrace();
		}
	}
	
	ArrayList<String[][]> divideTrainData(String[][] trainData, int numDecisionTrees){
		int rows=trainData.length/numDecisionTrees;
		int remaining=trainData.length%numDecisionTrees;
		String[][] solidTrainData=new String[rows*numDecisionTrees][trainData[0].length];
		String[][] remainingTrainData=new String[remaining][trainData[0].length];
		for(int i=0;i<solidTrainData.length;i++){
			solidTrainData[i]=trainData[i];
		}
		for(int i=0;i<remainingTrainData.length;i++){
			remainingTrainData[i]=trainData[i+solidTrainData.length];
		}
		ArrayList<String[][]> result=new ArrayList<String[][]>();
		int countSolid=numDecisionTrees;
		int countRemaining=0;
		int rowCount=0;
		while(countSolid>0){
			String[][] matrix;
			if(countRemaining!=remaining) matrix=new String[rows+1][];
			else matrix=new String[rows][];
			for(int i=0;i<rows;i++){
				matrix[i]=solidTrainData[rowCount+i];
			}
			rowCount+=rows;
			if(countRemaining!=remaining){
				matrix[matrix.length-1]=remainingTrainData[countRemaining];
				countRemaining++;
			}
			result.add(matrix);
			countSolid--;
		}
		return result;
	}
	
	ArrayList<String[]> divideTrainTruth(String[] trainTruth, int numDecisionTrees){
		int rows=trainTruth.length/numDecisionTrees;
		int remaining=trainTruth.length%numDecisionTrees;
		String[] solidTrainTruth=new String[rows*numDecisionTrees];
		String[] remainingTrainTruth=new String[remaining];
		for(int i=0;i<solidTrainTruth.length;i++){
			solidTrainTruth[i]=trainTruth[i];
		}
		for(int i=0;i<remainingTrainTruth.length;i++){
			remainingTrainTruth[i]=trainTruth[i+solidTrainTruth.length];
		}
		ArrayList<String[]> result=new ArrayList<String[]>();
		int countSolid=numDecisionTrees;
		int countRemaining=0;
		int rowCount=0;
		while(countSolid>0){
			String[] matrix;
			if(countRemaining!=remaining) matrix=new String[rows+1];
			else matrix=new String[rows];
			for(int i=0;i<rows;i++){
				matrix[i]=solidTrainTruth[rowCount+i];
			}
			rowCount+=rows;
			if(countRemaining!=remaining){
				matrix[matrix.length-1]=remainingTrainTruth[countRemaining];
				countRemaining++;
			}
			result.add(matrix);
			countSolid--;
		}
		return result;
	}
	
	String[] getFinalTestTruth(ArrayList<String[]> tempTruth){
		String[] result=new String[tempTruth.get(0).length];
		for(int i=0;i<result.length;i++){
			int countOf1=0;
			for(int j=0;j<tempTruth.size();j++){
				if(tempTruth.get(j)[i].equals("1")) countOf1++;
			}
			if(countOf1>tempTruth.size()/2) result[i]="1";
			else result[i]="0";
		}
		return result;
	}
	
	String[] flatten(String[][] input){
		String[] result=new String[input[0].length];
		for(int i=0;i<input[0].length;i++){
			result[i]=input[0][i];
		}
		return result;
	}
	
	private void display(String[] input){
		for(int i=0;i<input.length;i++){
			System.out.println(input[i]);
		}
	}
}
