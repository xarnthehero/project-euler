package projecteuler.problems;

import java.util.Arrays;

/**
 * Multiples of 3 and 5 Problem 1 If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5,
 * 6 and 9. The sum of these multiples is 23. Find the sum of all the multiples of 3 or 5 below 1000.
 */
public class Problem1 extends Problem {
	
	private int[] factors;
	private int maxNumber;
	
	public Problem1() {
		this(new int[] {3,5}, 1000);
	}
	
	public Problem1(int[] factors, int maxNumber) {
		this.factors = factors;
		this.maxNumber = maxNumber;
	}
	
	public String getProblemStatement() {
		return "Finding the sum of all multiples of " + Arrays.toString(factors) + " below " + maxNumber;
	}
	
    @Override
    String execute() {
        return new Integer(sumOfMultiples(factors, maxNumber)).toString();
    }

    private int sumOfMultiples(int[] factors, int upTo) {
        int total = 0;

        for (int i = 1; i < upTo; i++) {
            outerLoop: for (int j = 0; j < factors.length; j++) {
                if (i < factors[j]) {
                    continue;
                }
                if (i % factors[j] == 0) {
                    debug("Adding " + i);
                    total += i;
                    break outerLoop;
                }
            }
        }

        return total;
    }
}
