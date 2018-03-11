package projecteuler.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import projecteuler.utility.Dictionary;
import projecteuler.utility.Utilities;

/*
XOR decryption
Problem 59
Each character on a computer is assigned a unique code and the preferred standard is ASCII (American Standard Code for Information Interchange). For example, uppercase A = 65, asterisk (*) = 42, and lowercase k = 107.

A modern encryption method is to take a text file, convert the bytes to ASCII, then XOR each byte with a given value, taken from a secret key. The advantage with the XOR function is that using the same encryption key on the cipher text, restores the plain text; for example, 65 XOR 42 = 107, then 107 XOR 42 = 65.

For unbreakable encryption, the key is the same length as the plain text message, and the key is made up of random bytes. The user would keep the encrypted message and the encryption key in different locations, and without both "halves", it is impossible to decrypt the message.

Unfortunately, this method is impractical for most users, so the modified method is to use a password as a key. If the password is shorter than the message, which is likely, the key is repeated cyclically throughout the message. The balance for this method is using a sufficiently long password key for security, but short enough to be memorable.

Your task has been made easy, as the encryption key consists of three lower case characters. Using cipher.txt (right click and 'Save Link/Target As...'), a file containing the encrypted ASCII codes, and the knowledge that the plain text must contain common English words, decrypt the message and find the sum of the ASCII values in the original text.
*/
public class Problem59 extends Problem {

	private final int AVERAGE_WORD_LENGTH_MAX;
	private final double PERCENT_REAL_WORDS_NEEDED;
	private final int PASSWORD_LENGTH;
	
	private String lastPassword;
	
	public Problem59() {
		this(10, .2, 3);
	}
	
	public Problem59(int wordLength, double percentWords, int pwLength) {
		AVERAGE_WORD_LENGTH_MAX = wordLength;
		PERCENT_REAL_WORDS_NEEDED = percentWords;
		PASSWORD_LENGTH = pwLength;
	}
	
	@Override
	String getProblemStatement() {
		return "Find the sum of the characters in the decrypted message";
	}
	
	/**
	 * Execution Steps -
	 * 1) Load a dictionary of words
	 * 2) Read the encrypted input
	 * 3) Loop through possible 3 character keys, decrypting the input
	 * 4) For each iteration, split the decrypted text on the space character ' '
	 * 5) Check if it meets criteria for average word length, enough actual words as compared
	 * 		with the dictionary
	 * 6) Upon finding the solution, get the sum of the characters
	 * 
	 */
	String execute() throws Exception {
		
		Map<String, String> potentialAnswers = new HashMap<String, String>();
		lastPassword = "";
		for(int i = 0; i < PASSWORD_LENGTH; i++) {
			lastPassword += 'z';
		}

		// 1)
		Dictionary dictionary = Dictionary.getInstance();
		
		// 2)
		BufferedReader br = Utilities.getReaderForProblem(this.getClass());
		String currentLine;
		StringBuilder encryptedString = new StringBuilder();
		StringBuilder totalFile = new StringBuilder();
		
		while((currentLine = br.readLine()) != null) {
			totalFile.append(currentLine);
		}
		
		String[] encryptedCharacters = totalFile.toString().split(",");
		for(String chars : encryptedCharacters) {
			encryptedString.append((char)Integer.parseInt(chars));
		}
		
		final int WORDS_NEEDED = encryptedString.length() / AVERAGE_WORD_LENGTH_MAX;
		
		StringBuilder decryptedString;
		int passwordIndex = 0;
		String password = null;
		
		// 3)
		while((password = getNextPassword(password)) != null) {
			decryptedString = new StringBuilder();
			for(int i = 0; i < encryptedString.length(); i++) {
				decryptedString.append(xOrCharacters(encryptedString.charAt(i), password.charAt(passwordIndex)));
				passwordIndex = (passwordIndex + 1) % PASSWORD_LENGTH;
			}
			
			debug("Checking Password " + password);
			
			// 4)
			String[] words = decryptedString.toString().split(" ");
			
			// 5)
			if(words.length >= WORDS_NEEDED) {
				int totalWords = 0;
				int realWords = 0;
				
				for(String word : words) {
					totalWords++;
					if(dictionary.isWord(word)) {
						realWords++;
					}
				}
				double wordPercentage = realWords / (double) totalWords;
				if(wordPercentage > PERCENT_REAL_WORDS_NEEDED) {
					debug("Potential Answer -");
					debug("\t(" + password + ") " + decryptedString);
					potentialAnswers.put(password, decryptedString.toString());
				}
			}
		}
		
		String answer = null;
		
		// 6)
		if(potentialAnswers.isEmpty()) {
			answer = "No Answers found :^(";
		}
		else {
			for(String currentPassword : potentialAnswers.keySet()) {
				//If there are multiple answers, print them and return the last one.. why not.
				answer = potentialAnswers.get(currentPassword);
				int sum = 0;
				char[] answerChars = answer.toCharArray();
				for(char c : answerChars) {
					sum += c;
				}
				debug("(" + currentPassword + ")  " + answer);
				answer = new Integer(sum).toString();
			}
		}
		
		return answer;
	}
	
	private char xOrCharacters(char c1, char c2) {
		return (char)(c1 ^ c2);
	}
	
	private String getNextPassword(String currentPassword) {
		
		//first time
		if(currentPassword == null) {
			String password = "";
			for(int i = 0; i < PASSWORD_LENGTH; i++) {
				password += "a";
			}
			return password;
		}
		else if(currentPassword.equals(lastPassword)) {
			return null;
		}
		
		int indexToChange = PASSWORD_LENGTH - 1;
		while(currentPassword.charAt(indexToChange) == 'z') {
			indexToChange--;
		}
		
		StringBuilder newPassword = new StringBuilder();
		newPassword.append(currentPassword.substring(0, indexToChange));
		newPassword.append((char)(currentPassword.charAt(indexToChange) + 1));
		for(int i = indexToChange + 1; i < PASSWORD_LENGTH; i++) {
			newPassword.append('a');
		}
		
		return newPassword.toString();
	}
}










