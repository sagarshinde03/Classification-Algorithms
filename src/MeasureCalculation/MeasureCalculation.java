/**
 * 
 */
package MeasureCalculation;

/**
 * @author Sagar Shinde
 *
 */
import java.util.ArrayList;

public class MeasureCalculation {
	String[] predicted;
	String[] actual;
	public MeasureCalculation(String[] predicted,String[] actual){
		this.predicted=predicted;
		this.actual=actual;
	}
	public MeasureCalculation(){
		
	}
	
	public void calculateMeasures(ArrayList<MeasureCalculation> records){
		ArrayList<Double> accuracies=new ArrayList<Double>();
		ArrayList<Double> precisions=new ArrayList<Double>();
		ArrayList<Double> recalls=new ArrayList<Double>();
		ArrayList<Double> f1Measures=new ArrayList<Double>();
		for(MeasureCalculation mc:records){
			ArrayList<Integer> arr=calcDistribution(mc.predicted, mc.actual);
			int truePositive=arr.get(0);
			int falseNegative=arr.get(1);
			int falsePositive=arr.get(2);
			int trueNegative=arr.get(3);
			if(truePositive+falseNegative+falsePositive+trueNegative != mc.predicted.length){
				System.out.println("Something is wrong!");
			}
			double accuracy=calcAccuracy(truePositive, falseNegative, falsePositive, trueNegative);
			accuracies.add(accuracy);
			double precision=calcPrecision(truePositive, falseNegative, falsePositive, trueNegative);
			precisions.add(precision);
			double recall=calcRecall(truePositive, falseNegative, falsePositive, trueNegative);
			recalls.add(recall);
			double f1Measure=calcF1Measure(precision, recall);
			f1Measures.add(f1Measure);
		}
		System.out.println("Average Accuracy: "+calcAverage(accuracies, "Accuracy"));
		System.out.println();
		System.out.println("Average Precision: "+calcAverage(precisions, "Precision"));
		System.out.println();
		System.out.println("Average Recall: "+calcAverage(recalls, "Recall"));
		System.out.println();
		System.out.println("Average F1 Measure: "+calcAverage(f1Measures, "F1 Measure"));
	}
	
	private ArrayList<Integer> calcDistribution(String[] predicted, String[] actual){
		ArrayList<Integer> result=new ArrayList<Integer>();
		int truePositive=0;
		for(int i=0;i<predicted.length;i++){
			if(predicted[i].equals("1") && actual[i].equals("1")) truePositive++;
		}
		int falseNegative=0;
		for(int i=0;i<predicted.length;i++){
			if(predicted[i].equals("0") && actual[i].equals("1")) falseNegative++;
		}
		int falsePositive=0;
		for(int i=0;i<predicted.length;i++){
			if(predicted[i].equals("1") && actual[i].equals("0")) falsePositive++;
		}
		int trueNegative=0;
		for(int i=0;i<predicted.length;i++){
			if(predicted[i].equals("0") && actual[i].equals("0")) trueNegative++;
		}
		result.add(truePositive);
		result.add(falseNegative);
		result.add(falsePositive);
		result.add(trueNegative);
		return result;
	}
	
	private double calcAccuracy(int truePositive, int falseNegative, int falsePositive, int trueNegative){
		double result=((double)(truePositive+trueNegative))/(truePositive+falseNegative+falsePositive+trueNegative);
		return result*100;
	}
	
	private double calcPrecision(int truePositive, int falseNegative, int falsePositive, int trueNegative){
		if(truePositive==0 && falsePositive==0) return 0.0;
		double result=((double)(truePositive))/(truePositive+falsePositive);
		return result;
	}
	
	private double calcRecall(int truePositive, int falseNegative, int falsePositive, int trueNegative){
		if(truePositive==0 && falseNegative==0) return 0.0;
		double result=((double)(truePositive))/(truePositive+falseNegative);
		return result;
	}
	
	private double calcF1Measure(double precision, double recall){
		double d1=2*precision*recall;
		double d2=precision+recall;
		if(d1==0 && d2==0) return 0.0;
		return d1/d2;
	}
	
	private double calcAverage(ArrayList<Double> input, String parameter){
		double result=0.0;
		int count=0;
		for(double d:input){
			System.out.println(parameter+" in iteration "+(++count)+": "+d);
			result+=d;
		}
		return result/input.size();
	}
}
