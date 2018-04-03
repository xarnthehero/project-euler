package projecteuler.problems;

/*
Counting Digits
Problem 156
Starting from zero the natural numbers are written down in base 10 like this: 
0 1 2 3 4 5 6 7 8 9 10 11 12....

Consider the digit d=1. After we write down each number n, we will update the number of ones that have occurred and call this number f(n,1). The first values for f(n,1), then, are as follows:

n       f(n,1)
0       0
1       1
2       1
3       1
4       1
5       1
6       1
7       1
8       1
9       1
10      2
11      4
12      5
Note that f(n,1) never equals 3. 
So the first two solutions of the equation f(n,1)=n are n=0 and n=1. The next solution is n=199981.

In the same manner the function f(n,d) gives the total number of digits d that have been written down after the number n has been written. 
In fact, for every digit d ≠ 0, 0 is the first solution of the equation f(n,d)=n.

Let s(d) be the sum of all the solutions for which f(n,d)=n. 
You are given that s(1)=22786974071.

Find ∑ s(d) for 1 ≤ d ≤ 9.

Note: if, for some n, f(n,d)=n for more than one value of d this value of n is counted again for every value of d for which f(n,d)=n.
*/
public class Problem156 extends Problem {


    private long[] sum;
    private long fnd;
    private long n;
    
    long[] fndJumpByDigit;
    
    

	@Override
	String getProblemStatement() {
		return "Find the sum of of numbers where f(n,d)=n from n = 1 to 9";
	}
    
    /**
     * At a certain point, our f(n,d) will be increasing faster than n and n will not catch up. This will be the end point for
     * the loop so I need to figure out how to determine when that is the case. It will be the same point for all values of d.
     * The strategy will be to compare values of f(n,d) vs d at particular points and be able to calculate it for given values
     * by knowing where the jumps are.
     * 0-9 - 1                  10 * (1/10)
     * 0-99 - 20                100 * (1/10) + 100 * (1/10)
     * 0-999 - 300              1000 * (1/10) + 1000 * (1/10) + 1000 * (1/10)
     * 0-9999 - 4000            
     * 0-99999 - 50000          
     * 0-999999 - 600000
     * 0-9999999 - 7000000
     * 0-99999999 - 80000000
     * 0-999999999 - 900000000
     * 0-9999999999 - 10000000000
     * At this point, f(n,d) is increasing faster than n so soon after we will hit a point when we no longer need to check.
     * 
     * Execution Steps:
     * 1) Loop through all numbers 1 - 9 while calculating s(d) 
     * 2) For each d, we keep running values for n and its f(n,d) along with the sum for s(d) so far
     *  Starting with f(n,d) = 0, n = 0;
     * 3) If f(n,d) and n are equal, add it to the total
     * 4) If they are within 10 (arbitrary) of each other, increment by 1 at a time
     * 5) If f(n,d) < n (which will happen early on), calculate up to f(n,d) = that value of n
     *  Ex. n = 9999, f(n,d) = 4000, we know that at least until f(n,d) = 9999 we are fine,
     *  so iterate our way up to that value
     * 6) If f(n,d) > n, check the arbitrary end point to be calculated or guessed at later
     * 7) If we are under that end point, move n up to f(n,d)
     */
    String execute() {
        
        //we will keep the sums for s(d) in sum[1] - sum[9]
        sum = new long[10];
        
        fnd = 0;
        n = 0;
        
        long endNumber = 99999;
        int jumpByDigitMax = new Long(endNumber).toString().length() + 1;
        fndJumpByDigit = new long[jumpByDigitMax];
        
        for(int i = 0; i < jumpByDigitMax; i++) {
            fndJumpByDigit[i] = (i + 1) * ((long)Math.pow(10, i));
        }
        
        char currentDigit = '1';
        int currentDigitInt = currentDigit - '0';
        
        while(n < endNumber) {
            long oldN = n;
            long difference = n - fnd;
            if(difference == 0) {
                sum[currentDigitInt] += fnd;
                n++;
                continue;
            }
            
            fnd = getNewN(154911, 184612, currentDigit);
//            fnd = getNewN(fnd, oldN, currentDigit);
        }
        return "WOW whats the answer?";
    }

    
    /*
     * This method will calculate the difference between oldN and n and will update fnc accordingly
     * in an incremental fashion
     */
    private long getNewFnd(long oldFnd, long oldN, long newN, char searchNumber) {
        long difference = n - oldN;
        //ex 24, 103, 
        
        return 0;
    }
    
    /*
     * This method will get the new value of N based on the old N and new fnd.
     * This is assuming that fnd was behind n so oldN will be the new fnd value.
     */
    private long getNewN(long oldFnd, long oldN, char searchNumber) {
        
        debug("current fnd: " + oldFnd + ", current N: " + oldN);
        Long oldFndLong = new Long(oldFnd);
        Long oldNLong = new Long(oldN);
        
        //ex oldFnd = 24, new Fnd = 103, oldN = 103
        long fndDifference = oldN - oldFnd;
        //79
        
        long currentFnd = oldFnd;
        long currentN = oldN;
        
        int roundDigits = 0;
        
        //calculate how many digits we need to even up to, in this instance it is 2 - from 24 to 100
        if(oldNLong.toString().length() > oldFndLong.toString().length()) {
            roundDigits = oldNLong.toString().length() - 1;
        }
        else {
            //march down digits until we have a different one
            //ex - oldFnd = 154911, oldN = 184612, need to even up 4 digits to 159999
            //154911   154918
            for(int i = 0; i < oldFndLong.toString().length(); i++) {
                if(!(oldFndLong.toString().charAt(i) == oldNLong.toString().charAt(i))) {
                    roundDigits = oldFndLong.toString().length() - i - 1;
                    break;
                }
            }
        }
        
        boolean searchNumberAlreadyHit = false;
        
        //ex oldFnd = 154911, oldN = 184612, roundDigits = 4
        //need to get to 159999
        for(int i = 1; i <= roundDigits; i++) {
            String currentFndString = new Long(currentFnd).toString();
            char[] currentFndCharsWithoutDigitInQuestion = currentFndString.toCharArray();

            char currentCharacter = currentFndString.charAt(currentFndString.length() - i);
            if(currentCharacter == '9') {
                //already rounded, maybe not counted?
                continue;
            }
            currentFndCharsWithoutDigitInQuestion[currentFndCharsWithoutDigitInQuestion.length - i] = '9';
            int charCount = getCharCount(currentFndCharsWithoutDigitInQuestion, searchNumber);
            searchNumberAlreadyHit = currentCharacter <= searchNumber;
            //from 154999 to 159999 -
            //we are counting 1s in top unchanging digits for 5000 
            // + 1s in 4999 to 9999
            //charCount (1) * (9 - 4) * 10^3 + fndJumpByDigit[2] * (9 - 4) + searchNumberAlreadyHit ? 0 : 10^3;
            //5000 + 1500 + 0 = 6500
            long addAmount = (long)(charCount * ('9' - currentCharacter) * Math.pow(10, i - 1)
                            + (i == 1 ? 0 : fndJumpByDigit[i - 2]) * ('9' - currentCharacter)
                            + (searchNumberAlreadyHit ? 0 : Math.pow(10, i - 1)));
            currentN = new Long(new String(currentFndCharsWithoutDigitInQuestion));;
            currentFnd += addAmount;
            debug("new fnd: " + currentFnd + ", new N: " + currentN + ", added " + addAmount + " to fnd");
                            
        }
        
        
        return 0;
    }
    /*    
     * Ex - fnd is 154911, n is 184012, search number is 1
     * We know that fnd can go up to 184012, so what value of n is at or before that?
     * Strategy is we even up digits of n to 9 - ex 184619, then 194699, then 184999
     * since we can easily calculate how big of a jump in fnd it is from n=184999 to 189999.
     * Once we hit a jump that puts fnd over the end value, we roll that value of n
     * back and calculate how many steps we can take with that digit -
     *  fnd going from 154911 -> 184012
     *  n = 184012
     *  184012 -> 184019 = 2 * (9 - 2) = 14 (change in fnd)
     *  154911 + 14 < 184012 so continue with n = 184019 and fnd = 154925
     *  184019 -> 184099 = 1 * (9 - 1) * 10^1 + (9 - 1) * 10^0 = 88
     *  154925 + 88 < 184012 so continue with n = 184099 and fnd = 155013
     *  first expression is top 3 digits, next is 4th, next is 5 and 6
     *  184099 -> 184999 = 1 * (9 - 0) * 10^2 + hitourdigityet? 0 : 10^2 + (9 - 0) * (0-99 amt) = 900 + 100 + 180 = 1180
     *  155013 + 1180 < 184012 so continue with n = 184999 and fnd = 156193
     *  184999 -> 189999 = 1 * (9 - 4) * 10^3 + 0 + (9 - 4) * 300 = 5000 + 1500 = 6500
     *  156193 + 6500 < 184012 so continue with n = 189999 and fnd = 162693
     *  189999 -> 199999 = 1 * (9 - 8) * 10^4 + 0 + (9 - 8) * 4000 = 14000
     *  162693 + 14000 < 184012 so continue with n = 199999 and fnd = 176693
     *  199999 -> 999999 = 0 + 0 + (9 - 1) * 50000 = 400000
     *  176693 + 400000 > 184012, we know that going to 999999 is too high
     *  once we are rounded out to as many 9s as possible, lets add 1 to get our new top digits -
     *          n = 200000, not sure if we add to fnd yet
     *  From 200000 to 299999..
     *  Jumps are 50000 each, if our number was still coming up (like 3) then that jump would be 10^6 + 50000
     *  Get the difference: 184012 - 176693 = 7319
     *  Make jumps in that digit until we can't make the next one (in this case that is 0 jumps since 7319 < 50000).
     *  Go down to next digit, jumps are 4000 (from 199999 to 209999)
     *  Now we have  n = 209999 and fnd = 180693
     *  
     */
//    private long getNewNewN(long fnd, long n, char searchNumber) {
//        
//    }
    
    private int getCharCount(char[] val, char digit) {
        int count = 0;
        for(char c : val) {
            if(c == digit) {
                count++;
            }
        }
        return count;
    }
    
    public static void main(String[] args) {
        new Problem156().getNewN(154911L, 184612L, '1');
    }
}

