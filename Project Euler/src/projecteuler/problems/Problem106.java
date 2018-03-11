package projecteuler.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import projecteuler.utility.Utilities;

/*
Special subset sums: meta-testing
Problem 106
Let S(A) represent the sum of elements in set A of size n. We shall call it a special sum set if for any two non-empty disjoint subsets, B and C, the following properties are true:

S(B) ≠ S(C); that is, sums of subsets cannot be equal.
If B contains more elements than C then S(B) > S(C).
For this problem we shall assume that a given set contains n strictly increasing elements and it already satisfies the second rule.

Surprisingly, out of the 25 possible subset pairs that can be obtained from a set for which n = 4, only 1 of these pairs need to be tested for equality (first rule). Similarly, when n = 7, only 70 out of the 966 subset pairs need to be tested.

For n = 12, how many of the 261625 subset pairs that can be obtained need to be tested for equality?

NOTE: This problem is related to Problem 103 and Problem 105.
*/
public class Problem106 extends Problem {

    private int setSize;
    private int maxSetSize;
    
    //Map of size (2 - maxSetSize) -> Map of numbers (from allNumber) -> the set of lists of integers
    //maybe I made this too complicated :(
    //This map will contain duplicates, ex [2, 3] will be in the map for 2 and 3
    private Map<Integer, Map<Integer, Set<List<Integer>>>> masterMap;
    private List<List<Integer>> allSetsInOrder;
    private List<Integer> allNumbers;
    
    public Problem106(int n) {
        setSize = n;
        maxSetSize = n / 2;
    }
    
    public Problem106() {
        this(12);
    }
    
	@Override
	String getProblemStatement() {
		return "For n = " + setSize + ", how many of the subset pairs that can be obtained need to be tested for equality?";
	}
    
    /*
     * For the solution, we are only looking for sets with equal sizes whose sum is equal.
     * I can't think of a smarter solution so brute force it is.
     * Execution Steps -
     * 1) Loop through from size 2 to n / 2 (2, 3, 4, 5, 6 for this problem)
     * 2) For each size, make a list of all possible sets grouped by number (set containing "1", set containing "2")
     * 3) Make a master set that contains all possible sets
     * 4) For each individual set (ex [2,3]), copy the master set and remove all sets containing those elements
     *  (remove all sets containing 2, all sets containing 3)
     * 5) For each ascending number in the set, check that set B has at least one higher and one lower number than set C
     *  for a given position
     */
    String execute() {
        
        masterMap = new HashMap<Integer, Map<Integer, Set<List<Integer>>>>();
        allNumbers = new ArrayList<Integer>();
        allSetsInOrder = new LinkedList<List<Integer>>();
        for(int i = 2; i <= maxSetSize; i++) {
            masterMap.put(i, new HashMap<Integer, Set<List<Integer>>>());
            for(int j = 1; j <= setSize; j++) {
                //2)
                allNumbers.add(new Integer(j));
                masterMap.get(i).put(j, new HashSet<List<Integer>>());
            }
        }
        
        
        int debugCount = 0;
        List<Integer> currentSet = null;
        
        //1)
        for(int i = 2; i <= maxSetSize; i++) {
            //3)
            Map<Integer, Set<List<Integer>>> setForSize = masterMap.get(i);
            while((currentSet = getNextSetInSequence(currentSet, i)) != null) {
                for(Integer numInSet : currentSet) {
                    setForSize.get(numInSet).add(currentSet);
                }
                allSetsInOrder.add(currentSet);
                
                if(debugCount % 100 == 0) {
                    debug("(" + debugCount + ") Adding " + currentSet);
                }
                debugCount++;
            }
        }
        
        int solutionsFound = 0;
        
        //4)
        for(List<Integer> numberSet : allSetsInOrder) {
            debug("Starting " + numberSet);
            List<List<Integer>> setsToCompare = getSetsExcludingNumbers(numberSet);
            for(List<Integer> setToCompare : setsToCompare) {
                //5)
                if(containsHigherAndLowerNumber(numberSet, setToCompare)) {
                    debug("Needs Checking - ");
                    debug("\t" + numberSet);
                    debug("\t" + setToCompare);
                    solutionsFound++;
                }
            }
        }
        
        debug("Answer will have duplicates, ie [2,3] and [1,4] found, [1,4] and [2,3] found");
        debug("Equality Tests Needed: " + (solutionsFound / 2));
        return "" + solutionsFound / 2;
    }
    
    
    private List<Integer> getNextSetInSequence(List<Integer> currentList, int numInSet) {
        
        List<Integer> nextSet;
        
        //first time with this set size
        if(currentList == null) {
            nextSet = new ArrayList<Integer>();
            for(int i = 0; i < numInSet; i++) {
                nextSet.add(allNumbers.get(i));
            }
        }
        else {
            nextSet = Utilities.makeShallowCopy(currentList);
            Integer maxNumber = allNumbers.get(allNumbers.size() - 1);
            boolean maxSetForN = true;                                          //ex for n = 12, numInSet = 3, [10, 11, 12]
            for(int i = nextSet.size() - 1; i >= 0; i--) {
                if(!nextSet.get(i).equals(maxNumber)) {                         //true if any set other than max
                    maxSetForN = false;                                         //ex [5, 7, 12], will enter if when i = 1
                    maxNumber = nextSet.get(i) + 1;
                    //Add 1 to every number from i to nextSet.size() - 1
                    for(int j = i; j < nextSet.size(); j++) {
                        nextSet.set(j, maxNumber);
                        maxNumber++;
                    }
                    break;
                }
                else {
                    maxNumber--;
                }
            }
            
            if(maxSetForN) {
                nextSet =  null;
            }
        }
        
        return nextSet;
    }
    
    private Set<List<Integer>> getAllSetsOfSize(int size) {
        Map<Integer, Set<List<Integer>>> sizeMap = masterMap.get(size);
        Set<List<Integer>> allSetsOfSize = new HashSet<List<Integer>>();
        for(Integer i : allNumbers) {
            allSetsOfSize.addAll(sizeMap.get(i));
        }
        
        return allSetsOfSize;
    }
    
    private Set<List<Integer>> getAllSetsBySizeAndNumber(int size, Integer number) {
        return Utilities.makeShallowCopy(masterMap.get(size).get(number));
    }
    
    private List<List<Integer>> getSetsExcludingNumbers(List<Integer> inputList) {
        Set<List<Integer>> allSets = getAllSetsOfSize(inputList.size());
        
        for(Integer i : inputList) {
            allSets.removeAll(getAllSetsBySizeAndNumber(inputList.size(), i));
        }
        
        return new ArrayList<List<Integer>>(allSets);
    }
    
    private boolean containsHigherAndLowerNumber(List<Integer> set1, List<Integer> set2) {
        boolean containsHigher = false;
        boolean containsLower = false;
        
        for(int i = 0; i < set1.size(); i++) {
            if(set1.get(i) > set2.get(i)) {
                containsHigher = true;
            }
            else {
                containsLower = true;
            }
        }
        return containsHigher && containsLower;
    }
}
