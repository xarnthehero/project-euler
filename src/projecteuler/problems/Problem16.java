package projecteuler.problems;

import java.math.BigInteger;

/*
Power digit sum
Problem 16
2^15 = 32768 and the sum of its digits is 3 + 2 + 7 + 6 + 8 = 26.

What is the sum of the digits of the number 2^1000?
*/
public class Problem16 extends Problem {
	
	private int power;
	
	public Problem16() {
		this(1000);
	}
	
	public Problem16(int power) {
		this.power = power;
	}
	
	@Override
	String getProblemStatement() {
		return "Finding the sum of digits of 2^" + power;
	}
	
	String execute() {
		BigInteger total = BigInteger.valueOf(2);
		BigInteger multiply = BigInteger.valueOf(2);
		power = 1000;
		for(int i = 0; i < power - 1; i++) {
			total = total.multiply(multiply);
		}
		debug("Number is " + total);
		String totalString = total.toString();
		int digitTotal = 0;
		for(char c : totalString.toCharArray()) {
			int addAmount = c - '0';
			debug(digitTotal + " + " + addAmount + " = " + (digitTotal + addAmount));
			digitTotal += addAmount;
		}
		return new Integer(digitTotal).toString();
		
	}
}







