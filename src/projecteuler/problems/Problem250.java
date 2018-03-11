package projecteuler.problems;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 
 * 250250 Problem 250 Find the number of non-empty subsets of {1^1, 2^2, 3^3,
 * ..., 250250^250250}, the sum of whose elements is divisible by 250. Enter the
 * rightmost 16 digits as your answer.
 * 
 *         Strategy: Since I am looking for numbers divisible by 250, I can use
 *         the set of numbers % 250 instead for the set of numbers itself and
 *         will receive the same final answer. As an example, lets say 8646 and
 *         1354 were in the set, adding up to 10000 which is divisible by 250.
 *         Instead, I can use 8646 % 250 and 1354 % 250 which is 146 and 104.
 *         These sum to 250. I still need to check that the sum % 250 = 0 but
 *         this means I can treat the numbers 146 and 8646 the same. As an
 *         example, if my initial set contains the numbers {146, 1354, 8646},
 *         there are 2 sets whose sum % 250 = 0 - {146, 1354} and {1354, 8646}.
 *         If I reduce the initial set to {104, 146(2)} with the 2 meaning there
 *         are 2 numbers with that remainder, I don't have to check as many set
 *         combinations. I can see that {104, 146} is a match and there are 1 *
 *         2 = 2 possible ways to combine those.
 * 
 *         1) Create an array that contains the count of numbers in the set %
 *         250. Loop through each number 1 - 250250 (call it x), find (x^x %
 *         250), add one to that slot in the array. For example, 9^9 % 250 =
 *         239, so arr[239]=1. Since calculating 250250^250250 is not feasible
 *         to calculate, I need a shortcut. I can run a for loop multiplying
 *         (previous answer * x) % 250 = n. If this n has been seen before, we
 *         are in a loop and can calculate what the final remainder will be
 *         without doing all the power multiplications. For example, (15 * 15) %
 *         250 = 225, (225 * 15) % 250 = 125, (125 * 15) % 250 = 125, so 125 is
 *         repeating and we can say the final % 250 will be 125. Some numbers
 *         will repeat in cycles.
 * 
 *         For future explanation I will use the simplified example maxSetNumber
 *         = 120, modNumber = 8, modCounts results in [59, 15, 0, 15, 1, 15, 0,
 *         15].
 * 
 *         2) Starting with sets size 1 and going to size maxSetNumber, keep a
 *         working list of sets that have a sum % modNumber = 0 and a set that
 *         is still actively being searched. Each time I increase the size of
 *         the set (ex from 1 to 2), go through the actively searched list and
 *         add every possible new set, removing the sets whose sum % modNumber =
 *         0 and adding them to the other list. The initial set {0} can be
 *         ignored and dealt with as a special case. For example, after set size
 *         1, the active list of sets will be {1} {3} {4} {5} {7}. For each of
 *         those sets, add the same number and bigger numbers - {1} -> {1 1} {1
 *         3} {1 4} {1 5} {1 7}. Since {1 7} is a complete set, remove that from
 *         the active list and put it in the complete set list. After doing sets
 *         of 2, we have Active List - {1 1} {1 3} {1 4} {1 5} {3 3} {3 4} {3 6}
 *         {3 7} {4 5} {4 7} {5 5} {5 7} Complete List - {1 7} {3 5} {4 4}
 * 
 *         Every time a new set is going to be added to the active list, need to
 *         check that the sequence hasn't been seen before. This may be very
 *         time consuming but it will also prevent getting duplicate sets. For
 *         example, if the set {1 3 4} has been found, when we run across {1 3 3
 *         4} it should be excluded, preventing any future searching with that
 *         set. {1 3 3 4} would get to {1 3 3 4 5}, which is just a combination
 *         of the two complete sets {1 3 4} and {3 5}. It is easier to find all
 *         the most simplified sets and later combine all possibilities. To do
 *         this, I need to store all complete sets in a tree format. For
 *         example, given the completed sets found so far {{1 7} {1 1 1 5} {1 1
 *         3 3} {1 3 4}} we have the following tree -
 * 
 *         									1
 *         								/	|	\
 *         								1	3	7
 *									/	|	|	
 *									1	3	4
 *								 	|	|
 *									5	3
 *
 *         When checking if we should continue with {1 3 3 4}, all of these sets
 *         need to be checked to make sure they aren't a previously completed
 *         set - {1 3 3 4} {1 3 4} {1 4} {3 3 4} {3 4}. The set must contain the
 *         latest number added or it would have been removed earlier. Instead of
 *         searching all completed sets, I only need to search subtrees of 1 and
 *         3. The algorithm would be to take the node for tree 1 and search all
 *         subtrees for other elements, 3 and 4, passing the remaining elements
 *         to search. In tree 1 subtree 3, I still have a 3 and 4 remaining so
 *         pass those elements. In tree 1 subtree 4, there are no larger
 *         elements so don't pass anything additional. If that node has no
 *         children then it is a leaf node and therefore a complete set. In the
 *         above example, there is no subtree 4, meaning no complete sets have
 *         been found that start {1 4 ..}. One consideration is that we must
 *         have checked the set {1 3 4} before checking the set {1 3 3 4} for
 *         this algorithm to work so this scheme depends on a breadth first
 *         search.
 *
 *         3) After all active sets have been checked (stopping at maxSetSize),
 *         I need to remove the completed sets that aren't possible due to using
 *         too many numbers in that set. For example, for the set {4 4}, there
 *         is only one 4 in modCounts so this set is not possible. For large
 *         numbers of maxSetNumber there shouldn't be many of these sets.
 * 
 *         4) Find all possible combinations of all sets. Starting with 1 set at
 *         a time, find the combinations. For example, given the set {1 3 4},
 *         there are fifteen 1s that could be used, fifteen 3s, and only one 4.
 *         The number of combinations would be: (15 choose 1) * (15 choose 1) *
 *         (1 choose 1) = (15! / (14! * 1!)) * (15! / (14! * 1!)) * (1! / (1! *
 *         0!)) = 15 * 15 * 1 = 225
 * 
 *         I'm not sure how to get all the different combinations of sets yet
 *         without enumerating them yet, I'll check that when I get there. Given
 *         the complete sets {1 3 4}, {1 7}, {3 5} I need to find how many
 *         possibilities there are for {1 1 3 4 7}, {1 3 3 4 5}, {1 3 5 7}, and
 *         {1 1 3 3 4 5 7} keeping in mind that some of these will go over the
 *         allotted numbers in modCounts. Going through all the possibilities
 *         seems too slow.. we will see.
 * 
 *  @author Spyder Spann
 * 
 */
public class Problem250 {

	// This is the array shown above - [0]=20, [1]=14
	// each index has a count of the number of original set elements % 250 that
	// equal that index
	private int[] modCounts;

	// The array of top level nodes in the tree
	// For example, completedSets[1] contains all the sets that start with
	// {1 ...}
	private SetNode[] completedSets;

	// The number of items in the original set, from {1^1 to
	// maxSetNumber^maxSetNumber}
	private final long maxSetNumber;

	// The number that (set sum) % modNumber = 0 for the set to be counted
	private final int modNumber;

	// The queue of active sets that are incomplete
	private Queue<NcpSet> activeSets;

	public Problem250() {
		this(250250L, 250);
	}

	public Problem250(long maxSetNumber, int modNumber) {
		modCounts = new int[modNumber];
		completedSets = new SetNode[modNumber];
		this.maxSetNumber = maxSetNumber;
		this.modNumber = modNumber;
		activeSets = new LinkedList<NcpSet>();
	}

	public void executeTest() {

		// Fills modCounts[]
		calculateModCounts();

		// Keeps the number of sets whose sum % modNumber = 0
		BigInteger validSetSum = BigInteger.ZERO;

		// Ignoring 0 since it is a special case
		// Add size 1 sets to the initial uncompleted list
		for(int i = 1; i < modNumber; i++) {
			if(modCounts[i] > 0) {
				NcpSet initialSet = new NcpSet();
				initialSet.addNumber(i);
				activeSets.add(initialSet);
			}
		}
		
		int runXMore = -1;
		long setsFound = 0;
		long runs = 1;
		int modBreakpointNumber = 10000000;
//		runXMore=5000000
		// while the uncompleted list is not empty, process the first one
		while(!activeSets.isEmpty()) {
			NcpSet currentSet = activeSets.remove();
			for(int i = currentSet.getLastNumber(); i < modNumber; i++) {
				if(modCounts[i] > 0) {
					NcpSet nextSet = new NcpSet(currentSet, i);
					
					runXMore--; //for debugging
					runs++; //for debugging
					if(runXMore == 0) {
						System.out.println(nextSet);
					}
					if(runs % modBreakpointNumber == 0) {
 						System.out.println(nextSet + "\n" + runs + " runs, " + activeSets.size() + " active sets, " + setsFound + " sets found.");
					}
					
					boolean isCompleteSet = nextSet.getSum() % modNumber == 0;
					boolean hasCompleteSubset = hasCompleteSubset(nextSet);
					// checkAndAddCompleteSet(nextSet);
					if(!hasCompleteSubset) {
						if(isCompleteSet) {
							// add complete set
//							System.out.println("Set Found - " + nextSet);
							completedSets[nextSet.removeFirst()].addCompleteSet(nextSet);
							setsFound++;
						} else {
							activeSets.add(nextSet);
						}
					}
				}
			}
		}
		
		System.out.println("Mod number: " + modNumber + ", sets found: " + setsFound);

		// To get the right 16 digits
		BigInteger gimmeThose16Digits = new BigInteger("10000000000000000");
//		System.out.println("Total Sets: " + validSetSum.mod(gimmeThose16Digits));
	}

	/**
	 * For the example {1 3 3 4}, the following sets need to be checked -
	 * {1 3 3 4} {1 3 4} {1 4} {3 3 4} {3 4}. The set {1 3 4} should
	 * already be a complete set so {1 3 3 4} will not be added.
	 */
	private boolean hasCompleteSubset(NcpSet ncpSet) {

		NcpSet masterCopy = new NcpSet(ncpSet, null);

		while(!masterCopy.isEmpty()) {
			Integer firstNumber = masterCopy.removeFirst();
			NcpSet lastCopy = new NcpSet(masterCopy, null);
			SetNode topNode = completedSets[firstNumber];

			while(!lastCopy.isEmpty()) {
				if(topNode.checkForCompleteSet(new NcpSet(lastCopy))) {
					return true;
				}
				lastCopy.removeFirst();
			}
		}

		return false;
	}

	/**
	 * If all sets of the current size have been found, will return false.
	 * Otherwise, will modify ncpList so the next set can be searched for and
	 * return true. For example, given the set {0, 0, 0, 1, 1, 248} and
	 * modCounts has the values [0] = 5 [1] = 10 [247] = 4 [248] = 1 ncpList
	 * would be modified to contain the set {0, 0, 1, 1, 1, 247}
	 */
	public boolean setNextNcpList(List<NumberCountPair> ncpList, int setSize) {

		int elementsToRemove = 1;
		while(elementsToRemove < setSize) {
			List<NumberCountPair> ncpListClone = NumberCountPair.cloneList(ncpList);

			for(int i = ncpListClone.size() - 1; i >= 0; i--) {
				NumberCountPair ncp = ncpListClone.get(i);
			}
		}
		return true;
	}

	/**
	 * Returns the sum of all elements in the set.
	 */
	public BigInteger getSetSum(List<NumberCountPair> ncpList) {
		BigInteger sum = BigInteger.ZERO;
		for(NumberCountPair ncp : ncpList) {
			sum = sum.add(BigInteger.valueOf(ncp.count).multiply(BigInteger.valueOf(ncp.number)));
		}
		return sum;
	}

	/**
	 * Returns the total number of possible set combinations. For example, given
	 * the set {0, 0, 0, 1, 1, 248} and modCounts has the values [0] = 5 [1] =
	 * 10 [248] = 1 There would be (5 choose 3) * (10 choose 2) * (1 choose 1)
	 * possibilities = 900
	 * 
	 */
	public BigInteger getSetPossibilities(List<NumberCountPair> ncpList) {
		BigInteger sum = BigInteger.ONE;
		for(NumberCountPair ncp : ncpList) {
			sum = sum.multiply(xChooseY(modCounts[ncp.number], ncp.count));
		}

		return sum;
	}

	/**
	 * Fills the modCounts array with the numbers 1 to maxSetNumber with that
	 * number % modNumber After running for maxSetNumber=250250 and
	 * modNumber=250, we have [25025, 1001, 0, 1001, ...] meaning 25025 of the
	 * numbers % 250 = 0, 1001 of the 250250 numbers % 250 = 1, etc.
	 */
	private void calculateModCounts() {

		List<Integer> modLoop = new ArrayList<Integer>();

		int lastMod;

		for(int i = 1; i <= maxSetNumber; i++) {
			modLoop.clear();
			lastMod = 1;

			for(int j = 1; j <= i; j++) {
				lastMod = (lastMod * i) % modNumber;
				int index = modLoop.indexOf(lastMod);

				if(index >= 0) {
					// Loop found

					int numModsInLoop = modLoop.size() - index;
					// If there is only 1 item in the loop, use current mod
					// value
					if(numModsInLoop != 1) {
						int multiplicationsLeft = (i - j) % numModsInLoop;
						lastMod = modLoop.get(index + multiplicationsLeft);
					}
					break;
				}

				modLoop.add(lastMod);
			}

			modCounts[lastMod]++;
		}

		// Set the top level tree nodes in completedSets
		for(int i = 0; i < completedSets.length; i++) {
			completedSets[i] = new SetNode(i, true);
		}

	}

	/**
	 * Ex 20 choose 5 = 20!/(15! * 5!) = 15504 
	 * for(i = 20; i > 15; i--) { total = total * i / (i - 15); }
	 */
	private BigInteger xChooseY(int total, int numSelecting) {
		if(total < numSelecting) {
			return BigInteger.ZERO;
		}

		BigInteger answer = BigInteger.ONE;
		long i = numSelecting > total / 2 ? numSelecting : total - numSelecting;
		for(; total > i; total--) {
			answer = answer.multiply(BigInteger.valueOf(total)).divide(BigInteger.valueOf(total - i));
		}

		return answer;
	}

	private void addCompleteSet(NcpSet set) {
		int firstNumber = set.removeFirst();
	}

	/**
	 * Represents numbers in a set. new NumberCountPair(4, 2) would mean this
	 * set contains two 4s {4 4}
	 */
	private static class NumberCountPair {

		private int number;
		private int count;

		public NumberCountPair(int number, int count) {
			this.number = number;
			this.count = count;
		}

		public String toString() {
			String countString = count > 1 ? "(" + count + ")" : "";
			return number + countString;
		}

		public static List<NumberCountPair> cloneList(List<NumberCountPair> ncpList) {
			List<NumberCountPair> newList = new ArrayList<NumberCountPair>(ncpList.size());
			for(NumberCountPair ncp : ncpList) {
				newList.add(new NumberCountPair(ncp.number, ncp.count));
			}
			return newList;
		}

		public NumberCountPair getCopy() {
			return new NumberCountPair(number, count);
		}
	}

	/**
	 * Represents a set comprised of NumberCountPairs
	 */
	private static class NcpSet {
		private List<NumberCountPair> ncpList;

		public NcpSet() {
			ncpList = new ArrayList<NumberCountPair>();
		}

		public NcpSet(NcpSet previousSet) {
			this(previousSet, null);
		}

		public NcpSet(NcpSet previousSet, Integer addElement) {
			ncpList = new ArrayList<NumberCountPair>(previousSet.ncpList.size());
			for(NumberCountPair ncp : previousSet.ncpList) {
				ncpList.add(ncp.getCopy());
			}

			if(addElement != null) {
				if(ncpList.get(ncpList.size() - 1).number == addElement) {
					ncpList.get(ncpList.size() - 1).count++;
				} else {
					ncpList.add(new NumberCountPair(addElement, 1));
				}
			}
		}

		public int getSum() {
			int sum = 0;
			for(NumberCountPair ncp : ncpList) {
				sum = sum + (ncp.count * ncp.number);
			}
			return sum;
		}

		public void addNumber(Integer number) {
			if(ncpList.isEmpty()) {
				ncpList.add(new NumberCountPair(number, 1));
			} else {
				// implement later if I need it
				throw new RuntimeException("Adding a number to NcpSet but it isn't implemented");
			}
		}

		public Integer removeFirst() {
			if(ncpList.isEmpty()) {
				return null;
			}

			NumberCountPair firstElement = ncpList.get(0);
			if(firstElement.count == 1) {
				ncpList.remove(0);
			} else {
				firstElement.count = firstElement.count - 1;
			}
			return firstElement.number;
		}

		public Integer getFirstNumber() {
			return ncpList.get(0).number;
		}

		public Integer getLastNumber() {
			return ncpList.get(ncpList.size() - 1).number;
		}

		public boolean isEmpty() {
			return ncpList.isEmpty();
		}

		public String toString() {
			return ncpList.toString();
		}
	}

	/**
	 * This class represents a node in a tree for all the compelete sets that
	 * have been found. One of these objects is made for every node in the tree
	 *         									1
	 *         								/	|	\
	 *         								1	3	7
	 *									/	|	|	
	 *									1	3	4
	 *								 	|	|
	 *									5	3
	 */
	private static class SetNode {

		// A leaf node is denoted as children == null
		private HashMap<Integer, SetNode> children;
		private Integer number;

		public SetNode(Integer number) {
			this.number = number;
		}

		public SetNode(Integer number, boolean isTopLevel) {
			// Special case for the top level nodes, initialize
			// the children hashmap here.
			this(number);
			children = isTopLevel ? new HashMap<Integer, SetNode>() : null;
		}

		public SetNode getChild(Integer searchValue) {
			SetNode retValue = null;
			if(children != null) {
				retValue = children.get(searchValue);
			}
			return retValue;
		}

		/**
		 * This method will recursively add the set to the tree, creating tree
		 * nodes as it goes as needed.
		 * 
		 * @param set
		 */
		public void addCompleteSet(NcpSet set) {
			Integer valueToAdd = set.removeFirst();
			if(valueToAdd != null) {
				if(children == null) {
					children = new HashMap<Integer, SetNode>();
				}
				SetNode child = children.get(valueToAdd);
				if(child == null) {
					child = new SetNode(valueToAdd);
					children.put(child.number, child);
				}
				child.addCompleteSet(set);
			}
		}

		/**
		 * This method will recursively check if the exact set passed in has
		 * already been added to the tree.
		 */
		public boolean checkForCompleteSet(NcpSet set) {

			// This is a complete set
			if(children == null) {
				return true;
			}

			Integer firstNumber = set.removeFirst();
			if(firstNumber == null) {
				return false;
			}

			SetNode nextNode = children.get(firstNumber);
			if(nextNode == null) {
				return false;
			}

			return nextNode.checkForCompleteSet(set);
		}

		public String toString() {
			if(children == null) {
				return number + " - complete";
			}
			return number + " - " + children.keySet().size() + " children";
		}
	}

	public static void main(String[] args) {

//		Problem250 prob = new Problem250(50, 10);
//		prob.executeTest();
		
//		for(int i = 4; i < 40; i++) {
//			new Problem250(50, i).executeTest();
//		}
	}

}
