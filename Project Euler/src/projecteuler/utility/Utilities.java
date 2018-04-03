package projecteuler.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import projecteuler.problems.Problem;

public class Utilities {

	
	public static BufferedReader getReaderForProblem(Class<? extends Problem> clazz) {
		return getReader("/projecteuler/data/" + clazz.getSimpleName() + ".data");
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
