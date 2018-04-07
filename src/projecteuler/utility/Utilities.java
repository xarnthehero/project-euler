package projecteuler.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import projecteuler.problems.Problem;

public class Utilities {
	
	public static final String DATA_FILE_PATH = "/projecteuler/data/";
	
	public static BufferedReader getReaderForProblem(Class<? extends Problem> clazz) {
		return getReader(DATA_FILE_PATH + clazz.getSimpleName() + ".data");
	}
	
	/**
	 * To edit the original file in the src directory instead of the file built file
	 */
	public static BufferedWriter getSourceFileWriter(String fileName) throws IOException, URISyntaxException {
		URL resourceUrl = Utilities.class.getResource(fileName);
    	File file = new File(resourceUrl.toURI());
    	String fullPath = file.getCanonicalPath();
    	fullPath = fullPath.replaceAll("\\\\bin\\\\", "\\\\src\\\\");
    	file = new File(fullPath);
    	return new BufferedWriter(new FileWriter(file));
	}
	
	public static BufferedWriter getWriter(String fileName) throws IOException, URISyntaxException {
    	URL resourceUrl = Utilities.class.getResource(fileName);
    	File file = new File(resourceUrl.toURI());
    	return new BufferedWriter(new FileWriter(file));
	}
	
	public static BufferedReader getReader(String filePath) {
		InputStream is = Utilities.class.getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	}
	
	public static <T> List<T> makeShallowCopy(List<T> l) {
		List<T> other = new ArrayList<T>();
		other.addAll(l);
		return other;
	}
	
	public static <T> Set<T> makeShallowCopy(Set<T> s) {
		Set<T> other = new HashSet<T>();
		other.addAll(s);
		return other;
	}
}
