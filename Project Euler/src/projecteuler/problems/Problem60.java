package projecteuler.problems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import projecteuler.utility.ItemGrouping;

/*
Prime pair sets
Problem 60
The primes 3, 7, 109, and 673, are quite remarkable. By taking any two primes and concatenating them in any order the result will always be prime. For example, taking 7 and 109, both 7109 and 1097 are prime. The sum of these four primes, 792, represents the lowest sum for a set of four primes with this property.

Find the lowest sum for a set of five primes for which any two primes concatenate to produce another prime.
*/
public class Problem60 extends Problem {
	
	// 1)
	private List<ItemGrouping<Long>> setsOfTwo = new ArrayList<ItemGrouping<Long>>();
	private List<ItemGrouping<Long>> setsOfThree = new ArrayList<ItemGrouping<Long>>();
	private List<ItemGrouping<Long>> setsOfFour = new ArrayList<ItemGrouping<Long>>();
	private List<ItemGrouping<Long>> setsOfFive = new ArrayList<ItemGrouping<Long>>();
	private List<Long> primes = new ArrayList<Long>();
	
	@Override
	String getProblemStatement() {
		return "Find the lowest sum for a set of five primes for which any two primes concatenate to produce another prime.";
	}
	
	/**
	 * Execution Steps -
	 * 1) Will keep a list of 2, 3, and 4 combination sets of primes that match, along with
	 * 		a list of the primes
	 * 2) Search for the next unfound prime
	 * 3) Check all sets and add it to the next group if it is a match with that set,
	 * 		leaving that set in it's current group as well
	 * 4) Once a set of 5 is found, we need to keep searching up to the sum in case
	 * 		another set of 5 is found with a lesser sum
	 * 
	 * This solution isn't complete as all of #4 wasn't implemented but
	 * the first solution found is correct. It is not guaranteed for other
	 * start / end conditions.
	 */
	String execute() throws Exception {
		
		primes.add((long)2);
		primes.add((long)3);
		primes.add((long)5);
		
		long lastPrime = 5;
		long setOfFiveSum = 0;
		
		while(setsOfFive.isEmpty()) {
			
			// 2)
			long currentNumber = lastPrime + 2;

			while(true) {
				if(isPrime(currentNumber, false)) {
					break;
				}
				currentNumber += 2;
			}
			debug("Next Prime: " + currentNumber);
			primes.add(currentNumber);
			lastPrime = currentNumber;
			
			
			for(ItemGrouping<Long> ig : setsOfFour) {
				compareNumberToSets(currentNumber, ig.getList(), setsOfFive);
			}
			
			for(ItemGrouping<Long> ig : setsOfThree) {
				compareNumberToSets(currentNumber, ig.getList(), setsOfFour);
			}
			
			for(ItemGrouping<Long> ig : setsOfTwo) {
				compareNumberToSets(currentNumber, ig.getList(), setsOfThree);
			}
			
			for(Long prime : primes) {
				if(areConcatinationsPrime(prime, currentNumber))
				{
					ItemGrouping<Long> ig = new ItemGrouping<Long>(prime, currentNumber);
					setsOfTwo.add(ig);
				}
			}
			
			// 4)
			if(!setsOfFive.isEmpty() && setOfFiveSum == 0) {
				List<Long> setOfFive = setsOfFive.get(0).getList();
				debug("Set of five found - " + setOfFive);
				for(Long l : setOfFive) {
					setOfFiveSum += l;
				}
			}
		}
		
		debug("Total Primes Searched: " + primes.size());
		debug("Pairs of Two: " + setsOfTwo.size());
		debug("Pairs of Three: " + setsOfThree.size());
		debug("Pairs of Four: " + setsOfFour.size());
		
		
		for(ItemGrouping<Long> ig : setsOfFive) {
			int sum = 0;
			for(Long l : ig.getList()) {
				sum += l;
			}
			debug(ig.getList().toString());
			debug("\tSum: " + sum);
			return new Integer(sum).toString();
		}
		
		return "No answer found.";
	}
	
	private boolean areConcatinationsPrime(long l1, long l2) {
		return isPrime(Long.parseLong(l1 + "" + l2), true) 
		&& isPrime(Long.parseLong(l2 + "" + l1), true);
	}
	
	private void compareNumberToSets(long number, List<Long> primeList, List<ItemGrouping<Long>> igListToAddTo) {
		
		for(Long prime : primeList) {
			if(!areConcatinationsPrime(number, prime)) {
				return;
			}
		}
		
		debug("Adding " + number + " to " + primeList.toString());
		ItemGrouping<Long> ig = new ItemGrouping<Long>(primeList, number);
		igListToAddTo.add(ig);
	}
	
	private boolean isPrime(long testNumber, boolean checkAfterPrimeList) {
		for(Long prime : primes) {
			if(testNumber % prime == 0) {
				return false;
			}
		}
		
		if(checkAfterPrimeList) {
			long lastPrime = primes.get(primes.size() - 1);
			while(true) {
				if(Math.pow(lastPrime, 2) > testNumber) {
					break;
				}
				lastPrime += 2;
				if(testNumber % lastPrime == 0) {
					return false;
				}
			}
		}
		
		return true;
	}

}











