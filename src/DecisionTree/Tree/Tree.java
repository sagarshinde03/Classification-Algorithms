/**
 * 
 */
package DecisionTree.Tree;

/**
 * @author Sagar Shinde
 *
 */
import DecisionTree.Tree.Entropy.Entropy;
import DecisionTree.Tree.Entropy.modifyTrainData;
import DecisionTree.Tree.GINIIndex.GINIIndex;
import DecisionTree.Tree.MisclassificationError.MisclassificationError;

public class Tree {
	Node root;
	GINIIndex giniIndex;
	Entropy entropy;
	MisclassificationError misclassificationError;
	public Node constructDecisionTreeUsingGINIIndex(String[][] trainData, String[] trainTruth){
		giniIndex=new GINIIndex();
		return giniIndex.constructDecisionTree(trainData, trainTruth);
	}
	
	public Node constructDecisionTreeUsingEntropy(String[][] trainData, String[] trainTruth){
		entropy=new Entropy();
		return entropy.constructDecisionTree(trainData, trainTruth);
	}
	
	public Node constructDecisionTreeUsingMisclassificationError(String[][] trainData, String[] trainTruth){
		misclassificationError=new MisclassificationError();
		return misclassificationError.constructDecisionTree(trainData, trainTruth);
	}
	
	public String[] test(String[][] testData, Node root, int method){
		if(method==1){
			int[][] testingData=new modifyTrainData().modifyTheTrainData(testData, giniIndex.boundaryHashMap);
			int[] outputArray=generateTestResultEntropy(testingData, root);
			return ConvertIntArrayToString(outputArray);
		}
		else if(method==2){
			int[][] testingData=new modifyTrainData().modifyTheTrainData(testData, entropy.boundaryHashMap);
			int[] outputArray=generateTestResultEntropy(testingData, root);
			return ConvertIntArrayToString(outputArray);
		}else if(method==3){
			int[][] testingData=new modifyTrainData().modifyTheTrainData(testData, misclassificationError.boundaryHashMap);
			int[] outputArray=generateTestResultEntropy(testingData, root);
			return ConvertIntArrayToString(outputArray);
		}else{
			return null;
		}
	}
	
	private int[] generateTestResultEntropy(int[][] testingData, Node root){
		int[] result=new int[testingData.length];
		for(int i=0;i<testingData.length;i++){
			int[] sample=testingData[i];
			result[i]=predictClassLabel(sample, root);
		}
		return result;
	}
	
	private int predictClassLabel(int[] sample, Node root){
		if(sample[root.attributeNumber]==0){
			if(root.left==null) return root.leftValue;
			return predictClassLabel(sample, root.left);
		}else if(sample[root.attributeNumber]==1){
			if(root.right==null) return root.rightValue;
			return predictClassLabel(sample, root.right);
		}else{
			System.out.println("Something wrong happened while predicting class label!");
			return -1;
		}
	}
	
	private String[] ConvertIntArrayToString(int[] input){
		String[] result=new String[input.length];
		for(int i=0;i<input.length;i++){
			result[i]=String.valueOf(input[i]);
		}
		return result;
	}
}