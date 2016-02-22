/**
 * 
 */
package NaiveBayes;

/**
 * @author Rahul Derashri
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MeasureCalculation.MeasureCalculation;


public class NaiveBayesClassification {
	ArrayList<MeasureCalculation> records=new ArrayList<MeasureCalculation>();
	private String[][] data;
	private String[][] train;
	private String[][] test;
	private int trainPercentage;
	private int kFold;
	private Map<String, ArrayList<String[]>> classMap = null;
	private Map<String, ArrayList<double[]>> meanVarianceMap = null;
	private Map<String, HashMap<Integer, HashMap<String, Double>>> descPostProb = null;
	
	public NaiveBayesClassification(String[][] data , int kFold) {
		this.data = data;
		this.kFold = kFold;
	}
	
	public void startClasssication(){
		int counter = kFold;
		while( counter > 0 ){
			splitData(generateIndexList());
			prepareClassMap();
			calculateMeanVariance();
			
			String[] testPred = predictAll(test);
			MeasureCalculation mc=new MeasureCalculation(testPred, getLables(test));
			records.add(mc);
			counter--;
		}
		new MeasureCalculation().calculateMeasures(records);
	}
	
	
	private List<Integer> generateIndexList(){
		List<Integer> list = new ArrayList<Integer>();
		
		for( int i = 0; i < data.length; i++  ){
			list.add(i);
		}
		
		return list;
	}
	
	private void splitData(List<Integer> list){
		Collections.shuffle(list);
		
		trainPercentage = (data.length * (kFold-1))/kFold;
		
		int start = 0;
		int end = trainPercentage;
		train = new String[end][];
		
		for( int counter = start; counter < end; counter++  ){
			train[counter] = data[list.get(counter)];
		}
		
		start = end;
		end = (int)(data.length * 1.0);
		
		test = new String[end-start][];
		for( int counter = start; counter < end; counter++  ){
			test[counter-start] = data[list.get(counter)];
		}
	}
	
	public void prepareClassMap(){
		classMap = new HashMap<String, ArrayList<String[]>>();
		
		int attrLen = train[0].length;
		
		for( int counter = 0; counter < train.length; counter++ ){
			ArrayList<String[]> list = null;
			String label = train[counter][attrLen-1];
			
			if( !classMap.containsKey(label)){
				list = new ArrayList<String[]>();
			}
			else{
				list = classMap.get(label);
			}
			
			list.add(train[counter]);
			classMap.put(label, list);
		}
	}
	
	
	public void normalizeData(){
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for( int counter = 0; counter < data.length; counter++ ){
			for( int innerCounter = 0; innerCounter < data[0].length-1; innerCounter++ ){
				try{
					if( Double.parseDouble(data[counter][innerCounter]) > max ){
						max = Double.parseDouble(data[counter][innerCounter]);
					}
					
					if( Double.parseDouble(data[counter][innerCounter]) < min ){
						min = Double.parseDouble(data[counter][innerCounter]);
					}
				}
				catch(NumberFormatException e){
					
				}
			}
		}
		
		
		for( int counter = 0; counter < data.length; counter++ ){
			for( int innerCounter = 0; innerCounter < data[0].length-1; innerCounter++ ){
				try{
					data[counter][innerCounter] = String.valueOf((Double.parseDouble(data[counter][innerCounter]) - min)/(max-min));
				}
				catch(NumberFormatException e){
					
				}
			}
		}
		
	}
	
	public void calculateMeanVariance(){
		meanVarianceMap = new HashMap<String, ArrayList<double[]>>();
		descPostProb = new HashMap<String, HashMap<Integer, HashMap<String, Double>>>();
		
		for( String label : classMap.keySet() ){
			ArrayList<String[]> dataList = classMap.get(label);
			
			ArrayList<double[]> list = new ArrayList<double[]>();
			
			for(int counter = 0; counter < classMap.get(label).get(0).length-1; counter++ ){
				
				try {
					double sum = 0.0;
					
					for(int innerCounter = 0; innerCounter < classMap.get(label).size(); innerCounter++ ){
						sum += Double.parseDouble(dataList.get(innerCounter)[counter]);
					}
					
					double mean = sum / dataList.size();
					
					sum = 0.0;
					for(int innerCounter = 0; innerCounter < classMap.get(label).size(); innerCounter++ ){
						sum += Math.pow( Double.parseDouble(dataList.get(innerCounter)[counter]) - mean, 2);
					}
					
					double stdv = Math.sqrt(sum / (dataList.size()-1));
					
					double[] val = {mean , stdv};
					list.add(val);
					
				} catch (NumberFormatException e) {
					list.add(null);
					HashMap<String, Double> attrValMap = new HashMap<String, Double>();
					for(int innerCounter = 0; innerCounter < classMap.get(label).size(); innerCounter++ ){
						if(!attrValMap.containsKey(dataList.get(innerCounter)[counter])){
							attrValMap.put(dataList.get(innerCounter)[counter], 0.0);
						}
						
						attrValMap.put(dataList.get(innerCounter)[counter], attrValMap.get(dataList.get(innerCounter)[counter])+1);
					}
					
					for( String key : attrValMap.keySet() ){
						attrValMap.put(key, attrValMap.get(key)/dataList.size());
					}
					
					HashMap<Integer, HashMap<String, Double>> attrMap = descPostProb.get(label);
					if( attrMap == null ){
						attrMap = new HashMap<Integer, HashMap<String, Double>>();
					}
					
					attrMap.put(counter, attrValMap);
					descPostProb.put(label, attrMap);
				}
			}
			
			meanVarianceMap.put(label, list);
		}
	}
	
	
	public String[] predictAll(String[][] samples){
		String[] predictedLabels = new String[samples.length];
		
		for(int counter = 0; counter < samples.length; counter++ ){
			predictedLabels[counter] = predict(samples[counter]);
		}
		
		return predictedLabels;
	}
	
	
	public String predict(String[] sample){
		String label = "";
		double classProb = 0.0;
		
		for( String key :  classMap.keySet() ){
			double prob = calculateDPP( key , sample );
			
			if( prob > classProb ){
				classProb = prob;
				label = key;
			}
		}
		
		return label;
	}
	
	
	public double calculateDPP(String label , String[] sample){
		double prob = 1.0;
		ArrayList<double[]> list = meanVarianceMap.get(label);
		for(int counter = 0; counter < sample.length-1; counter++ ){
			double temp = 0.0;
			if( list.get(counter) != null ){
				temp = calculateProb(Double.parseDouble(sample[counter]) , list.get(counter)[0], list.get(counter)[1] );
				prob *= temp;
			}
			else{
				prob *= descPostProb.get(label).get(counter).get(sample[counter]);
			}
		}
		return prob * classMap.get(label).size()/train.length;
	}
	
	
	public double calculateProb(double attr, double mean, double stdv){
		double num1 = Math.exp(-(Math.pow(attr - mean, 2)/(2* Math.pow(stdv, 2))));
		double deno2 = stdv * Math.sqrt(2*Math.PI);
		return num1/deno2;
	}
	
	
	public double calculateAccuracy(String[] predicted, String[] gTruth){
		int count = 0;
		
		for(int counter = 0; counter < predicted.length; counter++ ){
			if( predicted[counter].equalsIgnoreCase(gTruth[counter]) ){
				count++;
			}
		}
		
		double acc = (count*100.00)/predicted.length;
		
		return acc;
	}
	
	
	public String[] getLables(String[][] samples){
		String[] labels = new String[samples.length];
		int attrLen = samples[0].length;
		for(int counter = 0; counter < samples.length; counter++ ){
			labels[counter] = samples[counter][attrLen-1];
		}
		
		return labels;
	}
}
