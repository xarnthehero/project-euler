package projecteuler.problems;

/**
Largest prime factor
Problem 3
The prime factors of 13195 are 5, 7, 13 and 29.
What is the largest prime factor of the number 600851475143 ?
 */
public class Problem3 extends Problem {

	private long factorableNumber;
	
	public Problem3() {
		this(600851475143L);
	}
	
	public Problem3(long num) {
		factorableNumber = num;
	}
	
	@Override
	String execute() {
		return new Long(findLargestPrime(factorableNumber)).toString();
	}
	
	public String getProblemStatement() {
		return "Finding the largest prime factor of " + factorableNumber;
	}
	
	private long findLargestPrime(long num) {
		long largestPrime = 1;
		
		long currentNumberToFind = num;
		
		while(currentNumberToFind != 1) {
			for(int i = 2; i <= currentNumberToFind; i++) {
				if(currentNumberToFind % i == 0) {
					largestPrime = i;
					currentNumberToFind /= i;
					debug("Prime: " + i);
					break;
				}
			}
		}
		
		return largestPrime;
	}
}
