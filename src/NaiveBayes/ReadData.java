/**
 * 
 */
package NaiveBayes;

/**
 * @author Rahul Derashri
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadData {
	public static String[][] scanFile(String fileName){
		Scanner scan=null;
		try {
			scan=new Scanner(new File("src\\"+fileName+".txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file");
			e.printStackTrace();
		}
		String[][] table = getDataFromFile(scan);
		return table;
	}
	
	static String[][] getDataFromFile(Scanner x){
		ArrayList<String[]> t=new ArrayList<String[]>();
		int columnSize=0;
		while(x.hasNext()){
			String line=x.nextLine();
			String[] split=line.split("\t");
			columnSize=split.length;
			//if(split[1]!="-1") 
			t.add(split);
		}
		String[][] table=new String[t.size()][columnSize];
		int count=0;
		for(String[] arr:t){
			table[count]=arr;
			count++;
		}
		return table;
	}
}
