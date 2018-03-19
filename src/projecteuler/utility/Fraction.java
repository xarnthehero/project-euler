package projecteuler.utility;

public class Fraction {

	private long numerator;
	private long denominator;
	
	public Fraction(long number) {
		this(number, 1);
	}
	
	public Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public long getNumerator() {
		return numerator;
	}
	
	public long getDenominator() {
		return denominator;
	}
	
	public Fraction add(Fraction other) {
		long newDenominator = denominator;
		long numerator1 = numerator;
		long numerator2 = other.numerator;
		if(denominator != other.denominator) {
			newDenominator = denominator * other.denominator;
			numerator1 = numerator1 * other.denominator;
			numerator2 = numerator2 * denominator;
		}
		
		return new Fraction(numerator1 + numerator2, newDenominator);
	}
	
	public Fraction subtract(Fraction other){
		long newDenominator = denominator;
		long numerator1 = numerator;
		long numerator2 = other.numerator;
		if(denominator != other.denominator) {
			newDenominator = denominator * other.denominator;
			numerator1 = numerator1 * other.denominator;
			numerator2 = numerator2 * denominator;
		}
		
		return new Fraction(numerator1 - numerator2, newDenominator);
	}
	
	public Fraction multiply(Fraction other) {
		return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
	}
	
	public Fraction divide(Fraction other) {
		return new Fraction(this.numerator * other.denominator, this.denominator * other.numerator);
	}
	
	public double evaluate() {
		if(denominator == 0) {
			return 0;
		}
		return ((double)numerator) / denominator;
	}
	
	public String toString() {
		if(denominator == 1) {
			return numerator + "";
		}
		return numerator + "/" + denominator;
	}
}
