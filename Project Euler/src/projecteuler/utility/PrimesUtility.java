package projecteuler.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

public class PrimesUtility {
    
    private static boolean keepPrimeLinks = false;
    private static Node<BigInteger> maxPrime = null;
    private static Node<BigInteger> firstPrime = null;
    private static HashSet<Node<BigInteger>> primeList;
    
    
    public static void initialize(boolean keepPrimeLinks) throws IOException {
        PrimesUtility.keepPrimeLinks = keepPrimeLinks;
        primeList = new HashSet<Node<BigInteger>>();
        
        BufferedReader br = Utilities.getReader("/projecteuler/data/PrimeList");
       
        String line = br.readLine();
        String[] primes = line.split(" ");
        for(int i = 0; i < primes.length; i++) {
            String prime = primes[i];
            if(i == 0) {
                firstPrime = new Node<BigInteger>(new BigInteger(prime), null);
                maxPrime = firstPrime;
            }
            addPrime(prime);
        }
        
        while((line = br.readLine()) != null) {
            primes = line.split(" ");
            for(String prime : primes) {
                addPrime(prime);
            }
        }
        
        System.out.println(primeList.contains(new Node<BigInteger>(new BigInteger("5"), null)));
        
        br.close();
    }
    
    private static void addPrime(String prime) {
        Node<BigInteger> nextPrime = new Node<BigInteger>(new BigInteger(prime), null);
        if(keepPrimeLinks) {
            maxPrime.setNext(nextPrime);
            maxPrime = nextPrime;
        }
        primeList.add(nextPrime);
    }
    
    
    public static BigInteger getNextPrime(BigInteger bi) {
        return null;
    }
    
    public static boolean isPrime(BigInteger bi) {
        return false;
    }
    
    public static void calculatePrimesTo(BigInteger prime) {
        Node<BigInteger> newPrime = new Node<BigInteger>(prime, null);
        if(primeList.contains(newPrime)) {
            return;
        }
        
        BigInteger searchPrime = maxPrime.getData();
        
        while(prime.compareTo(maxPrime.getData()) < 0) {
            searchPrime = searchPrime.add(new BigInteger("2"));
            Node<BigInteger> currentDivisor = firstPrime;
            while(currentDivisor.getNext() != null) {
                
            }
            BigInteger maxSearchInt = sqrtBigInteger(prime);
            Node<BigInteger> currentPrime = firstPrime;
        }

    }
    
    private static void savePrimesToFile() {
        
    }
    
    //stolen from https://ubuntuforums.org/showthread.php?t=1331949
    private static BigInteger sqrtBigInteger(BigInteger bi) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = new BigInteger(bi.shiftRight(5).add(new BigInteger("8")).toString());
        while(b.compareTo(a) >= 0) {
          BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
          if(mid.multiply(mid).compareTo(bi) > 0) b = mid.subtract(BigInteger.ONE);
          else a = mid.add(BigInteger.ONE);
        }
        return a.subtract(BigInteger.ONE);
    }
    
    public static void main(String args[]) throws Exception {
        //        PrimesUtility.initialize(true);
//        calculatePrimesTo(new BigInteger("10000"));
//        savePrimesToFile();
    }
}
