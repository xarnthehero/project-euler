package projecteuler.problems;

/*
Find the 200th prime-proof sqube containing the contiguous sub-string "200"
Problem 200
We shall define a sqube to be a number of the form, p2q3, where p and q are distinct primes.
For example, 200 = 5223 or 120072949 = 232613.

The first five squbes are 72, 108, 200, 392, and 500.

Interestingly, 200 is also the first number for which you cannot change any single digit to make a prime; we shall call such numbers, prime-proof. The next prime-proof sqube which contains the contiguous sub-string "200" is 1992008.

Find the 200th prime-proof sqube containing the contiguous sub-string "200".
*/
public class Problem200 extends Problem {

	@Override
	String getProblemStatement() {
		return "Find the 200th prime-proof sqube containing the contiguous sub-string \"200\".";
	}
	
    @Override
    /**
     * Execution Steps:
     * 1) Load a big list of primes
     * 2) Starting with the set [2, 3], keep a running list of each prime for when it is used in the squared position and the cubed position with the result
     *          ex - after we have found (3^2 * 2^3 = 72), (2^2 * 3^3 = 108), (5^2 * 2^3 = 200), our set contains the numbers [2, 3, 5]
     *          squared list in the form (n, next prime that hasn't been used, product) - (2, 5, 500), (3, 5, 1125), (5, 3, 675)
     *          qubed list - (7, 2, 392), (5, 3, 675), (2, 5, 500)
     * 3) These lists need to be kept in an ordered format so we can sort on the lowest product
     *  Not sure if 2 lists are needed
     * 4) Check if this product has 200 in it
     * 5) If it does, check if it is prime-proof
     */
    String execute() throws Exception {
        return null;

    }

}
