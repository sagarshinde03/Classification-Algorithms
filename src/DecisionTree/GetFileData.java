/**
 * 
 */
package DecisionTree;

/**
 * @author Sagar Shinde
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GetFileData {
	ArrayList<String[][]> getFileData(String datasetName, double trainPercentage) throws FileNotFoundException{
		Scanner scan=new Scanner(new File("src\\"+datasetName+".txt"));
		ArrayList<String[][]> result=new ArrayList<String[][]>();
		String[][] trainData;
		String[][] trainTruth;
		String[][] testData;
		String[][] testTruth;
		ArrayList<String> fileData=new ArrayList<String>();
		while(scan.hasNext()){
			String line=scan.nextLine();
			fileData.add(line);
		}
		if(trainPercentage!=0.0){
			double d=((double)trainPercentage/100)*fileData.size();
			int numTrainRecords = (int)Math.round(d);
			int numTestRecords = fileData.size()-numTrainRecords;
			ArrayList<String> fileTrainData=new ArrayList<String>();
			ArrayList<String> fileTestData=new ArrayList<String>();
			Collections.shuffle(fileData);
			for(int i=0;i<numTrainRecords;i++) {
				fileTrainData.add(fileData.get(i));
			}
			for(int i=0;i<numTestRecords;i++) {
				fileTestData.add(fileData.get(i+numTrainRecords));
			}
			String[][] matrixTrain=new String[fileTrainData.size()][];
			for(int i=0;i<matrixTrain.length;i++){
				String line=fileTrainData.get(i);
				matrixTrain[i]=line.split("\t");
			}
			String[][] matrixTest=new String[fileTestData.size()][];
			for(int i=0;i<matrixTest.length;i++){
				String line=fileTestData.get(i);
				matrixTest[i]=line.split("\t");
			}
			trainData=new String[matrixTrain.length][matrixTrain[0].length-1];
			for(int i=0;i<matrixTrain.length;i++){
				for(int j=0;j<matrixTrain[i].length-1;j++){
					trainData[i][j]=matrixTrain[i][j];
				}
			}
			trainTruth=new String[1][matrixTrain.length];
			for(int i=0;i<matrixTrain.length;i++){
				trainTruth[0][i]=matrixTrain[i][matrixTrain[i].length-1];
			}
			testData=new String[matrixTest.length][matrixTest[0].length-1];
			for(int i=0;i<matrixTest.length;i++){
				for(int j=0;j<matrixTest[i].length-1;j++){
					testData[i][j]=matrixTest[i][j];
				}
			}
			testTruth=new String[1][matrixTest.length];
			for(int i=0;i<matrixTest.length;i++){
				testTruth[0][i]=matrixTest[i][matrixTest[i].length-1];
			}
			result.add(trainData);
			result.add(trainTruth);
			result.add(testData);
			result.add(testTruth);
		}else{
			ArrayList<String> fileTrainData=new ArrayList<String>();
			fileTrainData.addAll(fileData);
			//for test
			ArrayList<String> fileTestData=new ArrayList<String>();
			int index=datasetName.indexOf("train");
			datasetName=datasetName.substring(0, index)+"test";
			scan=new Scanner(new File("src\\"+datasetName+".txt"));
			while(scan.hasNext()){
				String line=scan.nextLine();
				fileTestData.add(line);
			}
			//for test ends
			String[][] matrixTrain=new String[fileTrainData.size()][];
			for(int i=0;i<matrixTrain.length;i++){
				String line=fileTrainData.get(i);
				matrixTrain[i]=line.split("\\s");//check and give SPACE if needed
			}
			String[][] matrixTest=new String[fileTestData.size()][];
			for(int i=0;i<matrixTest.length;i++){
				String line=fileTestData.get(i);
				matrixTest[i]=line.split("\\s");
			}
			trainData=new String[matrixTrain[0].length][matrixTrain.length];
			for(int i=0;i<matrixTrain[0].length;i++){
				for(int j=0;j<matrixTrain.length;j++){
					trainData[i][j]=matrixTrain[j][i];
				}
			}
			testData=new String[matrixTest[0].length][matrixTest.length];
			for(int i=0;i<matrixTest[0].length;i++){
				for(int j=0;j<matrixTest.length;j++){
					testData[i][j]=matrixTest[j][i];
				}
			}
			//for train truth
			index=datasetName.indexOf("test");
			datasetName=datasetName.substring(0, index)+"train_truth";
			scan=new Scanner(new File("src\\"+datasetName+".txt"));
			ArrayList<String> al=new ArrayList<String>();
			while(scan.hasNext()){
				String line=scan.nextLine();
				al.add(line);
			}
			trainTruth=new String[1][al.size()];
			for(int i=0;i<al.size();i++){
				trainTruth[0][i]=al.get(i);
			}
			testTruth=new String[1][testData.length];
			//for train truth ends 
			result.add(trainData);
			result.add(trainTruth);
			result.add(testData);
			result.add(testTruth);
		}
		scan.close();
		return result;
	}
}
