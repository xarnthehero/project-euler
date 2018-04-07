package projecteuler.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;

public class PrimesUtility {

	private static boolean isInitialized = false;
	private static Node<BigInteger> maxPrime = null;
	private static Node<BigInteger> firstPrime = null;

	private static final String PRIME_FILE_NAME = "PrimeList.data";
	
	public static void initialize() throws IOException {
		PrimesUtility.initialize("9999999999999999999999999999");
	}
	
	/**
	 * @param maxInitPrime - will stop loading values around this value, use for quicker loading 
	 * when testing or when a max prime needed is known.
	 * @throws IOException
	 */
	public static void initialize(String maxInitPrime) throws IOException {
		if(isInitialized) {
			return;
		}
		
		BigInteger maxInitPrimeBigInt = new BigInteger(maxInitPrime);

		BufferedReader br = Utilities.getReader(Utilities.DATA_FILE_PATH + PRIME_FILE_NAME);

		String line = br.readLine();
		String[] primes = line.split(" ");
		for (int i = 0; i < primes.length; i++) {
			String prime = primes[i];
			if (i == 0) {
				// Just here temporarily
				Node<BigInteger> tempFirst = maxPrime = new Node<BigInteger>(BigInteger.ZERO, null);
				firstPrime = addPrime(prime);
				maxPrime = tempFirst.getNext();
				tempFirst.setNext(null);
				firstPrime.setPrevious(null);
			} else {
				addPrime(prime);
			}
		}

		while ((line = br.readLine()) != null && maxPrime.getData().compareTo(maxInitPrimeBigInt) < 0) {
			primes = line.split(" ");
			for (String prime : primes) {
				addPrime(prime);
			}
		}

		br.close();
		
		isInitialized = true;
	}

	private static Node<BigInteger> addPrime(String prime) {
		Node<BigInteger> nextPrime = new Node<BigInteger>(new BigInteger(prime), null);
		maxPrime.setNext(nextPrime);
		maxPrime = nextPrime;
		return nextPrime;
	}

	public static boolean isPrime(BigInteger potentialPrime) {
		BigInteger sqrt = sqrtBigInteger(potentialPrime);
		//If maxPrime is less than the square root of our prime, we don't have enough
		//primes on file to check this one.
		if(maxPrime.getData().compareTo(sqrt) < 0) {
			throw new RuntimeException("search prime " + potentialPrime.toString() + " is greater than max prime " + maxPrime + ", need to increase prime list.");
		}

		Node<BigInteger> currentDivisorNode = firstPrime;
		BigInteger currentDivisor = currentDivisorNode.getData();

		// While the prime is less than the square root of searchPrime
		while (currentDivisor.compareTo(sqrt) <= 0) {
			if (potentialPrime.mod(currentDivisor).equals(BigInteger.ZERO)) {
				// potentialPrime is divisible, not a prime
				return false;
			}
			currentDivisorNode = currentDivisorNode.getNext();
			currentDivisor = currentDivisorNode.getData();
		}

		return true;
		
	}

	public static void calculatePrimesTo(BigInteger prime) {
		BigInteger searchPrime = maxPrime.getData();
		final BigInteger TWO = new BigInteger("2");
		while (searchPrime.compareTo(prime) < 0) {
			searchPrime = searchPrime.add(TWO);

			if(isPrime(searchPrime)) {
				addPrime(searchPrime.toString());
			}
		}

	}

	private static void savePrimesToSourceFile() throws IOException, URISyntaxException {

		BufferedWriter writer = Utilities.getSourceFileWriter(Utilities.DATA_FILE_PATH + PRIME_FILE_NAME);

		Node<BigInteger> node = firstPrime;

		int primeCount = 0;
		while (node != null) {
			writer.write(node.getData().toString());
			node = node.getNext();
			primeCount++;
			if (primeCount % 20 == 0) {
				writer.newLine();
			} else {
				writer.write(" ");
			}
		}

		writer.close();

	}
	
	// Potential risk giving out prime nodes as they could be changed
	public static Node<BigInteger> getFirstPrime() {
		return firstPrime;
	}

	// stolen from https://ubuntuforums.org/showthread.php?t=1331949
	private static BigInteger sqrtBigInteger(BigInteger bi) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(bi.shiftRight(5).add(new BigInteger("8")).toString());
		while (b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if (mid.multiply(mid).compareTo(bi) > 0)
				b = mid.subtract(BigInteger.ONE);
			else
				a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}

	public static void main(String args[]) throws Exception {		
		PrimesUtility.initialize();
		calculatePrimesTo(new BigInteger("10000"));
		savePrimesToSourceFile();
	}
}
