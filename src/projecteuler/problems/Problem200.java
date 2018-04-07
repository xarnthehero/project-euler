package projecteuler.problems;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import projecteuler.utility.Node;
import projecteuler.utility.PrimesUtility;

/*
Find the 200th prime-proof sqube containing the contiguous sub-string "200"
Problem 200
We shall define a sqube to be a number of the form, p^2*q^3, where p and q are distinct primes.
For example, 200 = 5223 or 120072949 = 232613.

The first five squbes are 72, 108, 200, 392, and 500.

Interestingly, 200 is also the first number for which you cannot change any single digit to make a prime; we shall call such numbers, prime-proof. The next prime-proof sqube which contains the contiguous sub-string "200" is 1992008.

Find the 200th prime-proof sqube containing the contiguous sub-string "200".
*/
public class Problem200 extends Problem {

	private String searchString;
	private int numberOfSearchSqubes;
	
	private PriorityQueue<Sqube> queue;
	private List<Long> solutions;
	
	public Problem200() {
		this("200", 200);
	}
	
	public Problem200(String searchString, int numSqubes) {
		this.searchString = searchString;
		numberOfSearchSqubes = numSqubes;
	}
	
	@Override
	String getProblemStatement() {
		return "Find the " + numberOfSearchSqubes + "th prime-proof sqube containing the contiguous sub-string \"" + searchString + "\".";
	}
	
    @Override
    /**
     * Execution Steps:
     * 1) Load a big list of primes
     * 2) A min heap will be used to keep track of the pending solutions to the equation, starting
     * with the values [p=2, q=3, solution=72] and [p=3, q=2, solution=108]. 
     * I need to iterate through the solutions to the equation p^2*q^3 to avoid doing extra work
     * so there isn't a way to increment and loop through individual values of p or q. This is
     * the algorithm I have come up with -
     * 	a) Remove min value solution from the heap, follow steps 3-5 to see if it is prime proof.
     * 	b) minValue = min[p, q]
     * 	c) if minValue is 2 (lets say it was p), add the pair [p=2, q=next prime after previous q, solution]
     * 		For example, if we just checked [2, 7, solution=392], add [2, 11, 968] to the heap.
     *  d) if the next prime after minValue is less than the other value in the pair, add [p=next prime after p, q, solution]
     *  	For example, if we just checked [2, 7, solution=392], add [3, 7, 1323] to the heap.
     * 3) Check if this product has 200 in it, if so continue to 4, otherwise move on the the next solution in step 2.
     * 4) Check if it is prime-proof
     * 
     * Continue this loop until 200 values are found.
     */
    String execute() throws Exception {
        
    	queue = new PriorityQueue<Sqube>();
    	solutions = new ArrayList<Long>();
    	
    	PrimesUtility.initialize();
    	
    	//2
    	Node<BigInteger> firstPrime = PrimesUtility.getFirstPrime();
    	//3
    	Node<BigInteger> secondPrime = PrimesUtility.getFirstPrime().getNext();
    	
    	//2^2*3^3
    	Sqube init1 = new Sqube(firstPrime, secondPrime);
    	//3^2*2^3
    	Sqube init2 = new Sqube(secondPrime, firstPrime);
    	queue.add(init1);
    	queue.add(init2);
    	
    	while(solutions.size() < numberOfSearchSqubes) {
    		Sqube sqube = queue.poll();
    		debug("Processing " + sqube.toString());
    		if(isPrimeProofSqubeWithString(sqube.getSolution())) {
    			debug("Adding solution " + sqube.toString());
    			solutions.add(sqube.getSolution());
    		}
    		
    		addNewSqubes(sqube);
    	}
    	
    	
    	return solutions.get(numberOfSearchSqubes - 1).toString();

    }
    
    private boolean isPrimeProofSqubeWithString(long number) {
    	String numberString = new Long(number).toString();
    	if(!numberString.contains(searchString)) {
    		//Doesn't contain "200"
    		return false;
    	}
    	
    	//If numberString ends in an even digit or is 5, only need to change the last value, loop iterator is set to length - 1.
    	//Otherwise check every digit, i = 0.
    	int lastDigit = new Integer(numberString.substring(numberString.length() - 1)).intValue();
    	int i = lastDigit % 2 == 0 || lastDigit == 5 ? numberString.length() - 1 : 0;
    	
    	for(; i < numberString.length(); i++) {
    		for(int j = 0; j <= 9; j++) {
        		String primeCheck = replaceDigit(numberString, j, i);
        		if(PrimesUtility.isPrime(new BigInteger(primeCheck))) {
        			return false;
        		}
    		}   		
    	}
    	
    	return true;
    }
    
    private String replaceDigit(String input, int digit, int index) {
		
    	String newString;
    	if(index == 0) {
			newString = digit + input.substring(index + 1);
		}
		else if(index == input.length() - 1) {
			newString = input.substring(0, index) + digit;
		}
		else {
			newString = input.substring(0, index) + digit + input.substring(index + 1);
		}
    	
    	return newString;
    }
    
    private void addNewSqubes(Sqube sqube) {
    	   	
    	boolean isPLower = sqube.getP().getData().compareTo(sqube.getQ().getData()) < 0;
    	Node<BigInteger> lowerValue = isPLower ? sqube.getP() : sqube.getQ();
    	Node<BigInteger> higherValue = isPLower ? sqube.getQ() : sqube.getP();
    	
    	if(lowerValue.equals(PrimesUtility.getFirstPrime())) {
    		//This is a case like [p=2 q=11], need to add [p=2 q=13]
    		Sqube newSqube;
    		if(isPLower) {
    			newSqube = new Sqube(lowerValue, higherValue.getNext());
    		}
    		else {
    			newSqube = new Sqube(higherValue.getNext(), lowerValue);
    		}
    		queue.add(newSqube);
    	}
    	
    	if( ! lowerValue.getNext().equals(higherValue)) {
    		//This is a case like [p=5 q=11], need to add [p=7 q=11]
    		Sqube newSqube;
    		if(isPLower) {
    			newSqube = new Sqube(lowerValue.getNext(), higherValue);
    		}
    		else {
    			newSqube = new Sqube(higherValue, lowerValue.getNext());
    		}
    		queue.add(newSqube);
    	}
    }
    
    private class Sqube implements Comparable<Sqube> {
    	private Node<BigInteger> p;
    	private Node<BigInteger> q;
    	private long solution;
    	
    	private Sqube(Node<BigInteger> p, Node<BigInteger> q) {
    		this.p = p;
    		this.q = q;
    		long pLong = p.getData().longValue();
    		long qLong = q.getData().longValue();
    		solution = pLong * pLong * qLong * qLong * qLong;
    		debug("Sqube[" + pLong + " " + qLong + " " + solution + "]");
    	}
    	
    	public int compareTo(Sqube other) {
    		return solution > other.solution ? 1 : -1;
    	}
    	
    	public Node<BigInteger> getP() {
    		return p;
    	}
    	
    	public Node<BigInteger> getQ() {
    		return q;
    	}
    	
    	public long getSolution() { 
    		return solution;
    	}
    	
    	public String toString() {
    		return "[p=" + p.toString() + " q=" + q.toString() + " " + solution + "]";
    	}
    }

}
