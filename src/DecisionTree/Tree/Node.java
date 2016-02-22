/**
 * 
 */
package DecisionTree.Tree;

/**
 * @author Sagar Shinde
 *
 */
public class Node {
	public int attributeNumber;
	public int leftValue;
	public int rightValue;
	public Node left;
	public Node right;
	
	public Node(int attributeNumber){
		this.attributeNumber=attributeNumber;
	}
}
