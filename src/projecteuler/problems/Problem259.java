package projecteuler.problems;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import projecteuler.utility.MyQueue;
import projecteuler.utility.TreeNode;


/**
Reachable Numbers
Problem 259
A positive integer will be called reachable if it can result from an arithmetic expression obeying the following rules:
•Uses the digits 1 through 9, in that order and exactly once each.
•Any successive digits can be concatenated (for example, using the digits 2, 3 and 4 we obtain the number 234).
•Only the four usual binary arithmetic operations (addition, subtraction, multiplication and division) are allowed.
•Each operation can be used any number of times, or not at all.
•Unary minus is not allowed.
•Any number of (possibly nested) parentheses may be used to define the order of operations.

For example, 42 is reachable, since (1/23) * ((4*5)-6) * (78-9) = 42.
1 / 23 * 4 * 5 - 6 * 78 - 9
What is the sum of all positive reachable integers?
 */
public class Problem259 extends Problem {

	private int startNumber;
	private int endNumber;
	
	//The list of reused operators. These will rotate to different values (from + to *) and are referenced in all trees.
	private List<Operator> operators;
	
	//List of the head of all trees. Each tree will have 1 reference to each of the values in operators[].
	private List<OperatorTreeNode> headOfTrees;
	
	public Problem259() {
		this(1, 9);
	}
	
	public Problem259(int start, int end) {
		startNumber = start;
		endNumber = end;
	}
	
	@Override
	String getProblemStatement() {
		return "Finding sum of reachable numbers using {  + - * / combine ()  } from " + startNumber + " to " + endNumber;
	}
	
	/**
	 * Strategy -
	 * I should be able to brute force this problem without pruning down the possibilities as the number of trees to check
	 * should be 32768 * 1400 = 45,875,200. One invalid case that I didn't see initially is (1 / 2) # 3   where # is combine.
	 * Depending on the parenthesis, 1 / (2 # 3) would be valid. Before calculating the value for each equation, I will make
	 * sure it is a valid tree by skipping equations where the combine is not done first.
	 * 
	 * 1) Create each of the 1400 possible tree structures using the same 8 operator nodes. This way, changing the value
	 * for one node from + to - will change it in all trees. Add the 9 digit leaf nodes to each tree.
	 * 2) Calculate the answer for each tree. If the answer isn't an integer, ignore it. After finding an answer, it needs
	 * to be stored in a hash table to we can quickly see if it has been seen before as we only want to count it once.
	 * 3) Rotate the value of one of the operator nodes in the tree to a tree we haven't seen before. There are 8^5 possible
	 * trees.
	 */
	String execute() {
		return sumOfReachableNumbers(startNumber, endNumber).toString();
	}

	

	private BigInteger sumOfReachableNumbers(int startDigit, int endDigit) {
		
		int numberOfOperators = endDigit - startDigit;
		
		//Initialize the 8 operators used
		operators = new LinkedList<Operator>();
		for(int i = 0; i < numberOfOperators; i++) {
			operators.add(new Operator(OperatorEnum.ADD));
		}
		
		buildTrees();

		
		
		return BigInteger.ZERO;
	}
	
	/**
	 * Loop through all operators, creating the head of a tree for each. For index i as the tree head,
	 * 0 through i-1 have to go in the left branch and i+1 - size()-1 have to go in the right branch.
	 * With these two new lists [0, i-1] and [i+1, size()-1], the same process can be repeated, copying
	 * the tree when adding each value.
	 */
	private void buildTrees() {
		
		headOfTrees = new ArrayList<OperatorTreeNode>();
		
		for(int i = 0; i < operators.size(); i++) {
			OperatorTreeNode head = new OperatorTreeNode(operators.get(i));
			headOfTrees.add(head);
			
			List<Operator> left = null;
			List<Operator> right = null;
			if(0 <= i - 1) {
				left = operators.subList(0, i - 1);
			}
			if(i + 1 <= operators.size() - 1) {
				right = operators.subList(i + 1, operators.size() - 1);
			}
			
			addChildNodes(head, left, true);
			addChildNodes(head, right, false);
		}
	}
	
	private void addChildNodes(OperatorTreeNode node, List<Operator> opList, boolean leftChild) {
		if(opList == null || opList.isEmpty()) {
			return;
		}
		
		for(int i = 0; i < opList.size(); i++) {
			
			//Make a new copy of the tree for each added value after the first, the first has already been added.
			if(i > 0) {
				node = node.deepCopyTree();
				headOfTrees.add((OperatorTreeNode)node.getParent());
			}
			
			List<Operator> left = null;
			List<Operator> right = null;
			if(0 <= i - 1) {
				left = operators.subList(0, i - 1);
			}
			if(i + 1 <= operators.size() - 1) {
				right = operators.subList(i + 1, operators.size() - 1);
			}
			
			
		}
	}
	
	private enum OperatorEnum {
		ADD("+") {	public BigDecimal operate(BigDecimal v1, BigDecimal v2) { return v1.add(v2); } },
		SUBTRACT("-") {	public BigDecimal operate(BigDecimal v1, BigDecimal v2) { return v1.subtract(v2); } },
		MULTIPLY("*") {	public BigDecimal operate(BigDecimal v1, BigDecimal v2) { return v1.multiply(v2); } },
		DIVIDE("/") {	public BigDecimal operate(BigDecimal v1, BigDecimal v2) { return v1.divide(v2); } },
		COMBINE("#") {	public BigDecimal operate(BigDecimal v1, BigDecimal v2) { return new BigDecimal(v1.toBigInteger().toString() + v2.toBigInteger().toString()); } };
		
		private String display;
		
		private OperatorEnum(String s) {
			display = " " + s + " ";
		}
		
		public String toString() {
			return display;
		}

		public BigDecimal operate(BigDecimal v1, BigDecimal v2) {
			throw new IllegalStateException("operate() must be overwritten");
		}
		
	}
	
	/**
	 * Will only have 8 total Operators that will rotate through the different OperatorEnums. Each Operator will be in each
	 * equation tree once.
	 */
	private class Operator {
		
		OperatorEnum op;
		
		public Operator(OperatorEnum op) {
			this.op = op;
		}
		
		/**
		 * Pass through method
		 */
		public BigDecimal operate(BigDecimal v1, BigDecimal v2) {
			return op.operate(v1, v2);
		}
		
		public OperatorEnum getOperatorEnum() {
			return op;
		}
		
	}
	
	/**
	 * A node in the tree that represents an operator.
	 * For the equation 1+(2*4), the tree will look like:
	 * 
	 * 					+						O				O = OperatorTreeNode
	 * 				/		\				/		\			T = TreeNode
	 * 				1		*				T		O
	 * 					/		\				/		\
	 * 					2		4				T		T
	 *
	 */
	private class OperatorTreeNode extends TreeNode {
		
		Operator operator;
		
		public OperatorTreeNode(Operator o) {
			operator = o;
		}
		
		//To create a new copy
		public OperatorTreeNode(OperatorTreeNode t) {
			operator = t.getOperator();
		}
		

		public Operator getOperator() {
			return operator;
		}
		
		public BigDecimal getTreeValue() {
			return operator.operate(childLeft.getTreeValue(), childRight.getTreeValue());
		}
		
		/**
		 * If this node is a combine node and one of it's children isn't a combine, this combine will get
		 * executed after another operation which isn't valid.
		 */
		public boolean isValidTree() {

			boolean iAmCombine = operator.getOperatorEnum().equals(OperatorEnum.COMBINE);

			if (childLeft instanceof OperatorTreeNode) {
				OperatorTreeNode childLeftOperator = (OperatorTreeNode) childLeft;
				if (iAmCombine && !childLeftOperator.getOperator().getOperatorEnum().equals(OperatorEnum.COMBINE)) {
					return false;
				}
				if (!childLeftOperator.isValidTree()) {
					return false;
				}
			}

			if (childRight instanceof OperatorTreeNode) {
				OperatorTreeNode childRightOperator = (OperatorTreeNode) childRight;
				if (iAmCombine && !childRightOperator.getOperator().getOperatorEnum().equals(OperatorEnum.COMBINE)) {
					return false;
				}
				if (!childRightOperator.isValidTree()) {
					return false;
				}
			}

			return true;
		}
		
		/**
		 * Makes a new node for every node in the original tree with a reference to the same operator enum object.
		 * This method is assuming all nodes in the tree are OperatorTreeNode objects
		 * @return the corresponding copied node in the new tree.
		 */
		public OperatorTreeNode deepCopyTree() {
			
			OperatorTreeNode copiedCorrespondingNode = null;
			OperatorTreeNode currentOldNode = null;
			OperatorTreeNode currentNewNode = null;
			MyQueue<OperatorTreeNode> nodesToProcess = new MyQueue<OperatorTreeNode>();
			
			//Get the head of the tree
			OperatorTreeNode head = (OperatorTreeNode)super.getHead();
			
			//Make the corresponding head to the new tree
			OperatorTreeNode newHead = new OperatorTreeNode(head);
			
			//Add both to the queue, push the old tree value first followed by the new value.
			nodesToProcess.push(head);
			nodesToProcess.push(newHead);
			
			while(!nodesToProcess.isEmpty()) {
				currentOldNode = nodesToProcess.pop();
				currentNewNode = nodesToProcess.pop();
				
				if(this == currentOldNode) {
					//So we can return the corresponding new node.
					copiedCorrespondingNode = currentNewNode;
				}
				
				if(currentOldNode.getChildLeft() != null) {
					OperatorTreeNode newLeft = new OperatorTreeNode((OperatorTreeNode)currentOldNode.getChildLeft());
					nodesToProcess.push((OperatorTreeNode)currentOldNode.getChildLeft());
					nodesToProcess.push((OperatorTreeNode)newLeft);
				}
				
				if(currentOldNode.getChildRight() != null) {
					OperatorTreeNode newRight = new OperatorTreeNode((OperatorTreeNode)currentOldNode.getChildRight());
					nodesToProcess.push((OperatorTreeNode)currentOldNode.getChildRight());
					nodesToProcess.push((OperatorTreeNode)newRight);
				}
			}
			
			return copiedCorrespondingNode;
		}
	}
}
