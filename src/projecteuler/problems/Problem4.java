package projecteuler.problems;

import projecteuler.utility.NumberUtilies;

/**
Largest palindrome product
Problem 4
A palindromic number reads the same both ways. The largest palindrome made from the product of two 2-digit numbers is 9009 = 91 × 99.
Find the largest palindrome made from the product of two 3-digit numbers.
 */
public class Problem4 extends Problem {

	int digits;
	
	public Problem4() {
		this(3);
	}
	
	public Problem4(int digits) {
		this.digits = digits;
	}
	
	@Override
	String getProblemStatement() {
		return "Finding the largest palindrome made from the product of two " + digits + " digit numbers";
	}
	
	@Override
	String execute() {
		return new Long(findLargesPalindromeProduct(digits)).toString();
	}
	
	private long findLargesPalindromeProduct(int maxProductDigit) {
		long maxPalindrome = 0;
		int num1 = 0;
		int num2 = 0;
		
		int startNumber = (int)Math.pow(10, maxProductDigit - 1);
		int endNumber = (int)Math.pow(10, maxProductDigit) - 1;
		
		for(int i = startNumber; i <= endNumber; i++) {
			for(int j = startNumber; j <= endNumber; j++) {
				long product = i * j;
				if(product > maxPalindrome && NumberUtilies.isPalindrome(product)) {
					maxPalindrome = product;
					num1 = i;
					num2 = j;
					debug(num1 + " x " + num2 + " = " + maxPalindrome);
				}
			}
		}
		
		return maxPalindrome;
	}

}
