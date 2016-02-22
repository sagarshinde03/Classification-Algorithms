/**
 * 
 */
package DecisionTree.Tree.Entropy;

/**
 * @author Sagar Shinde
 *
 */
import java.util.ArrayList;
import java.util.HashMap;

public class BinarySplit {
	ArrayList<Double> attributeValue;
	ArrayList<Integer> classValue;
	int countt=0;
	HashMap<Integer, Double> createBoundaryHashMap(String[][] trainData, String[] trainTruth){
		HashMap<Integer, Double> result=new HashMap<Integer, Double>();
		for(int i=0;i<trainData[0].length;i++){
			attributeValue=new ArrayList<Double>();
			classValue=new ArrayList<Integer>();
			// these arraylists store attribute values and corresponding class label.
			if(!trainData[0][i].equals("Present") && !trainData[0][i].equals("Absent")){
				for(int j=0;j<trainData.length;j++){
					attributeValue.add(Double.parseDouble(trainData[j][i]));
					classValue.add(Integer.parseInt(trainTruth[j]));
				}
				sort();	// sorts attributeValue and classValue combinely
				double boundary=getBoundaryUsingBinarySplit();
				result.put(i, boundary);
			}
		}
		return result;
	}
	
	private void sort(){
		for(int i=0;i<attributeValue.size();i++){
			for(int j=i+1;j<attributeValue.size();j++){
				if(attributeValue.get(i)>attributeValue.get(j)){
					double d=attributeValue.get(j);
					attributeValue.remove(j);
					attributeValue.add(i, d);
					int x=classValue.get(j);
					classValue.remove(j);
					classValue.add(i, x);
				}
			}
		}
	}
	
	private double getBoundaryUsingBinarySplit(){
		double boundary=0.0;
		double informationGain=Double.MAX_VALUE;
		int totalOnes=calculateOnes();
		int totalZeros=classValue.size()-totalOnes;
		int count1SoFar=0;
		for(int i=0;i<attributeValue.size();i++){
			if(classValue.get(i)==1) count1SoFar++;
			if(i!=attributeValue.size()-1 && attributeValue.get(i)!=attributeValue.get(i+1) && classValue.get(i)!=classValue.get(i+1)){
				double averageVal=(attributeValue.get(i)+attributeValue.get(i+1))/2;
				int count0SoFar=i+1-count1SoFar;
				double tempInformationGain=calculateInformationGain(count1SoFar, count0SoFar, totalOnes-count1SoFar, totalZeros-count0SoFar);
				if(tempInformationGain<informationGain){
					informationGain=tempInformationGain;
					boundary=averageVal;
				}
			}
		}
		return boundary;
	}
	
	private int calculateOnes(){
		int count=0;
		for(int i:classValue){
			if(i==1) count++;
		}
		return count;
	}
	
	private double calculateInformationGain(int count1Previous,int count0Previous, int count1Rest, int count0Rest){
		double entropy1=0.0;
		if(count1Previous!=0 && count0Previous!=0) entropy1=calculateEntropy(count1Previous, count0Previous);
		double entropy2=0.0;
		if(count1Rest!=0 && count0Rest!=0) entropy2=calculateEntropy(count1Rest, count0Rest);
		double val1=((double)(count1Previous+count0Previous))/(count1Previous+count0Previous+count1Rest+count0Rest);
		double val2=((double)(count1Rest+count0Rest))/(count1Previous+count0Previous+count1Rest+count0Rest);
		return val1*entropy1+val2*entropy2;
	}
	
	private double calculateEntropy(int count1, int count0){
		double p1=(double)count1/(count1+count0);
		double log1=Math.log(p1)/Math.log(2);
		double product1=p1*log1;
		double p2=(double)count0/(count1+count0);
		double log2=Math.log(p2)/Math.log(2);
		double product2=p2*log2;
		double result=product1+product2;
		result*=-1;
		return result;
	}
}
