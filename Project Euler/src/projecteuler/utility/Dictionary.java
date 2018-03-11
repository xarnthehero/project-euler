package projecteuler.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Dictionary {

	private static Dictionary instance = new Dictionary();
	
	private Set<String> words;
	
	private Dictionary() {
		load();
	}
	
	private void load() {
		System.out.println("Loading Dictionary...");
		words = new HashSet<String>();
		
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("projecteuler/data/EnglishDictionary.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String currentLine;
			while((currentLine = br.readLine()) != null) {
				words.add(currentLine);
			}
		}
		catch(IOException e) {
			System.out.println("Error loading dictionary: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		System.out.println("Dictionary Load Complete");
	}
	
	public static Dictionary getInstance() {
		return instance;
	}
	
	public boolean isWord(String word) {
		return words.contains(word);
	}
}
