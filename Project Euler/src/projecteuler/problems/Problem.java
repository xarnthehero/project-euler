package projecteuler.problems;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public abstract class Problem {

    Integer problemNumber = null;
    String answer = null;
    boolean debug = false;
    List<String> errors;


    public Problem() {
        errors = new LinkedList<String>();
        String className = this.getClass().getSimpleName();
        if (className.length() > 7) {
            problemNumber = Integer.parseInt(className.substring(7));
        }
    }

    public final void runTest() throws Exception {
        Calendar startTime = Calendar.getInstance();
        preTest();
        System.out.println(getProblemStatement());
        answer = execute();
        printResults();
        postTest();
        Calendar endTime = Calendar.getInstance();
        System.out.println("Run Time: " + (endTime.getTime().getTime() - startTime.getTime().getTime()) + "ms");
        System.out.println();
    }

    final void preTest() {
        System.out.println("----- Starting Problem " + problemNumber + " -----");
    }
    
    abstract String getProblemStatement();

    // Solution here
    abstract String execute() throws Exception;

    final void postTest() {
        System.out.println("----- Ending Problem " + problemNumber + " -----");
    }

    final void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    final void debug(Object o) {
        debug(o.toString());
    }
    
    final void error(String s) {
        System.out.println("[Error " + errors.size() + 1 + "] " + s);
        errors.add(s);
    }
    
    final void printResults() {
        if(errors.isEmpty()) {
        	System.out.println("Answer: " + answer);
        }
        else {
	        System.out.println();
	        System.out.println("Errors:");
	        int errorNumber = 1;
	        for(String s : errors) {
	            System.out.println("[" + errorNumber + "] " + s);
	            errorNumber++;
	        }
        }
    }
}
