package projecteuler.problems;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import projecteuler.utility.Fraction;
import projecteuler.utility.MyQueue;
import projecteuler.utility.PrintTree;
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
	
	private HashSet<Integer> foundAnswers = new HashSet<Integer>();
	
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
	 * should be 32768 * 1094 = 35,848,192. One invalid case that I didn't see initially is (1 / 2) # 3   where # is combine.
	 * Depending on the parenthesis, 1 / (2 # 3) would be valid. Before calculating the value for each equation, I will make
	 * sure it is a valid tree by skipping equations where the combine is not done first.
	 * 
	 * Since I am performing mathematical operations and need to know if the answer is exactly an integer, I need to be careful
	 * when doing division. Using BigDecimals, if I do (1 / 3) * 3 I get something like 1.00000000000002 which won't work. To solve
	 * this, I am going to treat the numbers as fractions and only do the division at the end. The above equation would result in
	 * the fraction 3/3 which will evaluate to 1. This is done by keeping track of the numerator and denominators separately and not
	 * doing the division until all the operations are complete.
	 * 
	 * 1) Create each of the 1094 possible tree structures using the same 8 operator nodes. This way, changing the value
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
		BigInteger sum = BigInteger.ZERO;
		
		//Initialize the 8 operators used to PLUS
		operators = new LinkedList<Operator>();
		OperatorEnum initialValue = OperatorEnum.values()[0];
		for(int i = 0; i < numberOfOperators; i++) {
			operators.add(new Operator(initialValue));
		}

//		operators.add(new Operator(OperatorEnum.ADD));
//		operators.add(new Operator(OperatorEnum.MULTIPLY));
//		operators.add(new Operator(OperatorEnum.DIVIDE));
//		operators.add(new Operator(OperatorEnum.SUBTRACT));
//		operators.add(new Operator(OperatorEnum.COMBINE));
//		operators.add(new Operator(OperatorEnum.ADD));
		
		debug("Operators: " + operators);
		
		buildTrees();
		addNumberLeaves();
		
		boolean keepRunning = true;
		
		while(keepRunning) {
			for(int i = 0; i < headOfTrees.size(); i++) {
				OperatorTreeNode node = headOfTrees.get(i);
				if(node.isValidTree()) {
					debug("Evaluating:");
					debug(PrintTree.print(node.getHead()));
					Fraction equationAnswer = calculateTreeAnswer(node);
					double answer = equationAnswer.evaluate();
					int intAnswer = (int)answer;
					if(answer == intAnswer && !foundAnswers.contains(new Integer(intAnswer))) {
						debug("Answer Found: " + intAnswer);
						foundAnswers.add(new Integer(intAnswer));
						sum = sum.add(new BigInteger(new Long((long)answer).toString()));
					}
				}
			}
			
			//Increment the operators and run the tree equations again
			keepRunning = updateOperators();
		}
		

//		PrintTree.print(node);
		
		return sum;
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
			if(0 <= i) {
				left = operators.subList(0, i);
			}
			if(i + 1 <= operators.size()) {
				right = operators.subList(i + 1, operators.size());
			}
			
			addAllNodeCombinations(head, left, right);
		}
	}
	
	private void addChildNodes(OperatorTreeNode node, List<Operator> opList, int nextNodeIndex, boolean leftChild) {
		if(opList == null || opList.isEmpty()) {
			return;
		}
		
		//Add the next node to the correct branch
		OperatorTreeNode newNode = new OperatorTreeNode(opList.get(nextNodeIndex));
		

		debug("Adding child node " + newNode + " to " + node + " from index " + nextNodeIndex);
		debug("opList=" + opList + ", adding to " + (leftChild ? "left" : "right") + " child");
		debug(PrintTree.print(node.getHead()));
		
		
		if(leftChild) {
			node.setChildLeft(newNode);
		}
		else {
			node.setChildRight(newNode);
		}
		
		List<Operator> left = null;
		List<Operator> right = null;
		if(0 <= nextNodeIndex - 1) {
			left = opList.subList(0, nextNodeIndex);
		}
		if(nextNodeIndex + 1 <= opList.size()) {
			right = opList.subList(nextNodeIndex + 1, opList.size());
		}

		
		addAllNodeCombinations(newNode, left, right);
		


		
		debug(PrintTree.print(node.getHead()));
		debug("\n");
		
			
	}
	
	private void addAllNodeCombinations(OperatorTreeNode node, List<Operator> left, List<Operator> right) {
		//Loop through at least once
		boolean noLeftNodesToAdd = left == null || left.isEmpty();
		boolean noRightNodesToAdd = right == null || right.isEmpty();
		
		if(noLeftNodesToAdd && noRightNodesToAdd) {
			//No new nodes to add
			return;
		}
		
		//If we are adding some nodes, loop through at least once since this is a nested loop
		int leftLoopSize = noLeftNodesToAdd ? 1 : left.size();
		int rightLoopSize = noRightNodesToAdd ? 1 : right.size();
		
		
		for(int leftListIndex = 0; leftListIndex < leftLoopSize; leftListIndex++) {
			for(int rightListIndex = 0; rightListIndex < rightLoopSize; rightListIndex++) {
				//If we are not on the first iteration, copy the tree.
				if(leftListIndex > 0 || rightListIndex > 0) {
					node = node.deepCopyTree();
					headOfTrees.add((OperatorTreeNode)node.getHead());
					//The node being copied from could already have set child values.
					node.setChildLeft(null);
					node.setChildRight(null);
				}
				addChildNodes(node, left, leftListIndex, true);
				addChildNodes(node, right, rightListIndex, false);
			}
		}
	}
	
	/**
	 * All trees should be built into headOfTrees[]. This method will add all leaf nodes
	 * by doing an in order traverse of each tree
	 */
	private void addNumberLeaves() {
		
		Stack<OperatorTreeNode> stack = new Stack<OperatorTreeNode>();
		
		
		for(OperatorTreeNode node : headOfTrees) {
			
			debug("Adding leaves to tree:");
			debug(PrintTree.print(node.getHead()));
			
			int currentNumber = startNumber;
			while(node != null) {
				stack.push(node);
				node = (OperatorTreeNode)node.getChildLeft();
			}
			
			while(!stack.isEmpty()) {
				node = stack.pop();
				if(node.getChildLeft() == null) {
					node.setChildLeft(new TreeNode<Fraction>(new Fraction(currentNumber++)));
				}
				
				if(node.getChildRight() == null) {
					node.setChildRight(new TreeNode<Fraction>(new Fraction(currentNumber++)));
				}
				else {
					//Right child is an operator node
					node = (OperatorTreeNode)node.getChildRight();
					while(node != null) {
						stack.push(node);
						node = (OperatorTreeNode)node.getChildLeft();
					}
				}
			}
			
			debug("After adding leaves:");
			debug(PrintTree.print(node.getHead()));
		}
	}
	
	public Fraction calculateTreeAnswer(TreeNode<Fraction> node) {
		if(node instanceof OperatorTreeNode) {
			return ((OperatorTreeNode)node).getOperator().operate(calculateTreeAnswer(node.getChildLeft()), calculateTreeAnswer(node.getChildRight()));
		}
		else {
			return node.getValue();
		}
	}
	
	/**
	 * This method will update the operators being used by the trees. This method will
	 * eventually rotate through all possible combinations.
	 * @return true if the operators have been updated, false if there are no new values.
	 */
	public boolean updateOperators() {
		OperatorEnum[] opValues = OperatorEnum.values();
		OperatorEnum lastOperator = opValues[opValues.length - 1];
		int indexToChange = 0;
		while(indexToChange < operators.size() && operators.get(indexToChange).getOperatorEnum() == lastOperator) {
			indexToChange++;
		}
		
		//End condition hit, we are at the last iteration as all operators are COMBINE.
		if(indexToChange == operators.size()) {
			return false;
		}
		
		//Reset all operators indexes 0 - indexToChange-1 to ADD
		for(int i = 0; i < indexToChange; i++) {
			operators.get(i).setOperatorEnum(opValues[0]);
		}
		
		//Set operators[indexToChange] to the next value
		for(int i = 0; i < opValues.length; i++) {
			if(opValues[i] == operators.get(indexToChange).getOperatorEnum()) {
				operators.get(indexToChange).setOperatorEnum(opValues[i+1]);
				break;
			}
		}
		
		return true;
	}
	
	private enum OperatorEnum {
		ADD("+") {	public Fraction operate(Fraction v1, Fraction v2) { return v1.add(v2); } },
		SUBTRACT("-") {	public Fraction operate(Fraction v1, Fraction v2) { return v1.subtract(v2); } }, 
		MULTIPLY("*") {	public Fraction operate(Fraction v1, Fraction v2) { return v1.multiply(v2); } },
		DIVIDE("/") {	public Fraction operate(Fraction v1, Fraction v2) { return v1.divide(v2); } },
		COMBINE("#") {	public Fraction operate(Fraction v1, Fraction v2) { return new Fraction(new Long(v1.getNumerator() + "" + v2.getNumerator())); } };
		
		private String display;
		
		private OperatorEnum(String s) {
			display = s;
		}
		
		public String toString() {
			return display;
		}

		public Fraction operate(Fraction v1, Fraction v2) {
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
		public Fraction operate(Fraction v1, Fraction v2) {
			return op.operate(v1, v2);
		}
		
		public void setOperatorEnum(OperatorEnum op) {
			this.op = op;
		}
		
		public OperatorEnum getOperatorEnum() {
			return op;
		}
		
		public String toString() {
			return op.toString();
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
		
		@Override
		public String getText() {
			return operator.toString();
		}
		
		public String toString() {
			return operator.toString();
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
					currentNewNode.setChildLeft(newLeft);
					nodesToProcess.push((OperatorTreeNode)currentOldNode.getChildLeft());
					nodesToProcess.push((OperatorTreeNode)newLeft);
				}
				
				if(currentOldNode.getChildRight() != null) {
					OperatorTreeNode newRight = new OperatorTreeNode((OperatorTreeNode)currentOldNode.getChildRight());
					currentNewNode.setChildRight(newRight);
					nodesToProcess.push((OperatorTreeNode)currentOldNode.getChildRight());
					nodesToProcess.push((OperatorTreeNode)newRight);
				}
			}
			
			return copiedCorrespondingNode;
		}
	}
	
}
