/**
 * 
 */
package DecisionTree.Tree.Entropy;

/**
 * @author Sagar Shinde
 *
 */
import java.util.HashMap;

public class modifyTrainData {
	public int[][] modifyTheTrainData(String[][] trainData, HashMap<Integer, Double> boundaryHashMap){
		int[][] result=new int[trainData.length][trainData[0].length];
		for(int i=0;i<result[0].length;i++){
			for(int j=0;j<result.length;j++){
				if(trainData[j][i].equals("Present")){
					result[j][i]=1;
				}else if(trainData[j][i].equals("Absent")){
					result[j][i]=0;
				}else{
					double boundary=boundaryHashMap.get(i);
					double value=Double.parseDouble(trainData[j][i]);
					if(value<=boundary) result[j][i]=0;
					else result[j][i]=1;
				}
			}
		}
		return result;
	}
	
	public int[] modifyTheTrainTruth(String[] trainTruth){
		int[] result=new int[trainTruth.length];
		for(int i=0;i<result.length;i++){
			result[i]=Integer.parseInt(trainTruth[i]);
		}
		return result;
	}
}
