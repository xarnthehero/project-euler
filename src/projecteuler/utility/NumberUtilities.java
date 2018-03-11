package projecteuler.utility;

public class NumberUtilities {

    public static boolean isPalindrome(long number) {
        String numberString = new Long(number).toString();
        
        for(int i = 0; i <= numberString.length() / 2; i++)
        {
                if(!(numberString.charAt(i) == numberString.charAt(numberString.length() - i - 1))) {
                        return false;
                }
        }
        return true;
    }
}
