/**
 * 
 */
package DecisionTree.Tree.GINIIndex;

/**
 * @author Sagar Shinde
 *
 */
import java.util.HashMap;
import java.util.HashSet;

import DecisionTree.Tree.Node;
import DecisionTree.Tree.Entropy.modifyTrainData;

public class GINIIndex {
	public HashMap<Integer, Double> boundaryHashMap;
	public Node constructDecisionTree(String[][] trainData, String[] trainTruth){
		boundaryHashMap=new BinarySplitGiniIndex().createBoundaryHashMap(trainData, trainTruth);
		//In boundaryHashMap, key is an integer value assigned to each attribute and value is the double boundary that is used for binary split.
		int[][] trainingData=new modifyTrainData().modifyTheTrainData(trainData, boundaryHashMap);
		int[] trainingTruth=new modifyTrainData().modifyTheTrainTruth(trainTruth);
		HashSet<Integer> attributesToBeVisited=setAttributesToBeVisited(trainingData);
		HashSet<Integer> samplesToBeVisited=setSamplesToBeVisited(trainingData);
		return createDecisionTree(trainingData, trainingTruth, attributesToBeVisited, samplesToBeVisited);
	}
	
	private HashSet<Integer> setAttributesToBeVisited(int[][] trainingData){
		HashSet<Integer> result=new HashSet<Integer>();
		for(int i=0;i<trainingData[0].length;i++){
			if(!allSameValues(trainingData, i)){
				result.add(i);
			}
		}
		if(trainingData[0].length-result.size()>0){
			System.out.println("Number of attributes reduced: "+(trainingData[0].length-result.size()));
		}
		return result;
	}
	
	private boolean allSameValues(int[][] trainingData, int i){
		int first=trainingData[0][i];
		for(int x=1;x<trainingData.length;x++){
			if(first!=trainingData[x][i]) return false;
		}
		return true;
	}
	
	private HashSet<Integer> setSamplesToBeVisited(int[][] trainingData){
		HashSet<Integer> result=new HashSet<Integer>();
		for(int i=0;i<trainingData.length;i++){
			result.add(i);
		}
		return result;
	}
	
	private Node createDecisionTree(int[][] trainingData, int[] trainingTruth, HashSet<Integer> attributesToBeVisited, HashSet<Integer> samplesToBeVisited){
		int attributeWithMaxInfoGain=getAttributeWithMinGini(trainingData, trainingTruth, attributesToBeVisited, samplesToBeVisited);
		if(attributeWithMaxInfoGain!=-1){
			Node n=new Node(attributeWithMaxInfoGain);
			boolean b=checkAllHaveSameClassLabel(trainingData, trainingTruth, attributeWithMaxInfoGain, samplesToBeVisited, 0);
			if(b){
				n.leftValue=getClassLabel(trainingData, trainingTruth, attributeWithMaxInfoGain, samplesToBeVisited, 0);
				if(n.leftValue==-1) {
					HashSet<Integer> tempAttributesToBeVisited=new HashSet<Integer>();
					tempAttributesToBeVisited.addAll(attributesToBeVisited);
					tempAttributesToBeVisited.remove(n.attributeNumber);
					HashSet<Integer> tempSamplesToBeVisited=setSamplesToBeVisited(trainingData);
					n.left=createDecisionTree(trainingData, trainingTruth, tempAttributesToBeVisited, tempSamplesToBeVisited);
					if(n.left==null) n.leftValue=0;
				}
			}else{
				HashSet<Integer> tempAttributesToBeVisited=new HashSet<Integer>();
				tempAttributesToBeVisited.addAll(attributesToBeVisited);
				tempAttributesToBeVisited.remove(n.attributeNumber);
				HashSet<Integer> newSamplesToBeVisited=new HashSet<Integer>();
				newSamplesToBeVisited=selectParticularSamples(trainingData, attributeWithMaxInfoGain, samplesToBeVisited, 1);
				n.left=createDecisionTree(trainingData, trainingTruth, tempAttributesToBeVisited, newSamplesToBeVisited);
			}
			b=checkAllHaveSameClassLabel(trainingData, trainingTruth, attributeWithMaxInfoGain, samplesToBeVisited, 1);
			if(b){
				n.rightValue=getClassLabel(trainingData, trainingTruth, attributeWithMaxInfoGain, samplesToBeVisited, 1);
				if(n.rightValue==-1) {
					HashSet<Integer> tempAttributesToBeVisited=new HashSet<Integer>();
					tempAttributesToBeVisited.addAll(attributesToBeVisited);
					tempAttributesToBeVisited.remove(n.attributeNumber);
					HashSet<Integer> tempSamplesToBeVisited=setSamplesToBeVisited(trainingData);
					n.right=createDecisionTree(trainingData, trainingTruth, tempAttributesToBeVisited, tempSamplesToBeVisited);
					if(n.right==null) n.rightValue=1;
				}
			}else{
				HashSet<Integer> tempAttributesToBeVisited=new HashSet<Integer>();
				tempAttributesToBeVisited.addAll(attributesToBeVisited);
				tempAttributesToBeVisited.remove(n.attributeNumber);
				HashSet<Integer> newSamplesToBeVisited=new HashSet<Integer>();
				newSamplesToBeVisited=selectParticularSamples(trainingData, attributeWithMaxInfoGain, samplesToBeVisited, 0);
				n.right=createDecisionTree(trainingData, trainingTruth, tempAttributesToBeVisited, newSamplesToBeVisited);
			}
			return n;
		}else{
			return null;
		}
	}
	
	private boolean checkAllHaveSameClassLabel(int[][] trainingData,int[] trainingTruth,int attribute, HashSet<Integer> samplesToBeVisited, int val){
		int classLabel=-1;
		boolean result=true;
		for(int i:samplesToBeVisited){
			if(trainingData[i][attribute]==val){
				if(classLabel==-1){
					classLabel=trainingTruth[i];
				}else{
					if(classLabel!=trainingTruth[i]){
						result=false;
						break;
					}
				}
			}
		}
		//if(classLabel==-1) System.out.println("CheckClassLabel: Something is wrong!");
		return result;
	}
	
	private int getClassLabel(int[][] trainingData,int[] trainingTruth,int attribute, HashSet<Integer> samplesToBeVisited, int val){
		for(int i:samplesToBeVisited){
			if(trainingData[i][attribute]==val){
				return trainingTruth[i];
			}
		}
		return -1;
	}
	
	private HashSet<Integer> selectParticularSamples(int[][] trainingData, int attribute, HashSet<Integer> oldSamples, int valToRemove){
		HashSet<Integer> result=new HashSet<Integer>();
		for(int i:oldSamples){
			if(trainingData[i][attribute]!=valToRemove){
				result.add(i);
			}
		}
		return result;
	}
	
	private int getAttributeWithMinGini(int[][] trainingData, int[] trainingTruth, HashSet<Integer> attributesToBeVisited, HashSet<Integer> samplesToBeVisited){
		if(attributesToBeVisited.size()==0) return -1;
		else{
			double gini=Double.MAX_VALUE;
			int attributeWithLeastGini=-1;
			for(int i:attributesToBeVisited){
				double ig=getGini(trainingData, trainingTruth, i, samplesToBeVisited);
				if(Double.isNaN(ig)) System.out.println("NaN value found");
				if(ig<gini){
					gini=ig;
					attributeWithLeastGini=i;
				}
			}
			if(attributeWithLeastGini==-1) System.out.println("Something is Wrong!");
			return attributeWithLeastGini;
		}
	}
	
	private double getGini(int[][] trainingData, int[] trainingTruth, int attribute, HashSet<Integer> samplesToBeVisited){
		int count0Previous=0, count1Previous=0, count0Rest=0, count1Rest=0;
		for(int i:samplesToBeVisited){
			if(trainingData[i][attribute]==0){
				if(trainingTruth[i]==0) count0Previous++;
				else count1Previous++;
			}else{
				if(trainingTruth[i]==0) count0Rest++;
				else count1Rest++;
			}
		}
		double d= calculateGini(count1Previous, count0Previous, count1Rest, count0Rest);
		if(Double.isNaN(d)) System.out.println("NaN value found!");
		return d;
	}
	
	private double calculateGini(int count1Previous,int count0Previous, int count1Rest, int count0Rest){
		double gini1=0.0;
		if(count1Previous!=0 || count0Previous!=0) gini1=calculateGini(count1Previous, count0Previous);
		double gini2=0.0;
		if(count1Rest!=0 || count0Rest!=0) gini2=calculateGini(count1Rest, count0Rest);
		int val1=count1Previous+count0Previous;
		int val2=count1Rest+count0Rest;
		return val1*gini1+val2*gini2;
	}
	
	private double calculateGini(int count1, int count0){
		//check
		double p1=(double)count1/(count1+count0);
		double p2=(double)count0/(count1+count0);
		p1*=p1;
		p2*=p2;
		double result=p1+p2;
		result=1-result;
		return result;
	}
}
