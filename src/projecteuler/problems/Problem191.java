package projecteuler.problems;

import java.util.HashMap;
import java.util.Map;

import projecteuler.utility.MyQueue;
import projecteuler.utility.NumberUtilies;

/*
Prize Strings
Problem 191;
A particular school offers cash rewards to children with good attendance and punctuality. If they are absent for three consecutive days or late on more than one occasion then they forfeit their prize.

During an n-day period a trinary string is formed for each child consisting of L's (late), O's (on time), and A's (absent).

Although there are eighty-one trinary strings for a 4-day period that can be formed, exactly forty-three strings would lead to a prize:

OOOO OOOA OOOL OOAO OOAA OOAL OOLO OOLA OAOO OAOA
OAOL OAAO OAAL OALO OALA OLOO OLOA OLAO OLAA AOOO
AOOA AOOL AOAO AOAA AOAL AOLO AOLA AAOO AAOA AAOL
AALO AALA ALOO ALOA ALAO ALAA LOOO LOOA LOAO LOAA
LAOO LAOA LAAO

How many "prize" strings exist over a 30-day period?
*/
public class Problem191 extends Problem {
	
	private int days;
	private Map<String, DayState> seenStates;
	
	private static final int MAX_CONSECUTIVE_LATE_COUNT = 2;
	private static final int MAX_ABSENT_COUNT = 1;
	
	public Problem191() {
		this(30);
	}
	
	public Problem191(int days) {
		this.days = days;
	}
	
	@Override
	String getProblemStatement() {
		return "How many \"prize\" strings exist over a " + days + "-day period?";
	}
	
	/**
	 * Can do a dynamic programming strategy - if X means this day hasn't been assigned a value yet, the strings
	 * OOOOXX and OLLOXX are equivalent as far as future possibilities so the possible values of X only need to be
	 * calculated once and and can multiply by the number of possibilities that can get to that state.
	 * 
	 * For the purpose of this problem, I'll use the notation -
	 * #[A][L][L] to represent a state -
	 * 4L		Have seen 5 values with the last one being an L
	 * 2A		Have seen 3 values with one of them being an A, the last value not having been an L
	 * 5ALL		Have seen 8 values including one A and the last two being Ls
	 * 10		Have seen 10 values, no As, the last value being an O
	 * 
	 * Colon indicates how many ways there are to arrive at that state -
	 * 4L:2		Two ways to arrive at 4L
	 * 
	 * In this way, I'll start with the sequences O, L, A and keep a running total for how many times this sequence has been
	 * seen. Each value will be put in a queue. When getting a value from the queue, derive each new possible state and see if
	 * it has been seen before. If so, add to that state, otherwise add that new state to the queue.
	 * 
	 * Here is an example through the first 2 days -
	 * Queue={O:1, L:1, A:1}
	 * O:1 -> OO, OL, OA		represented as 2:1, 1L:1, 1A:1
	 * Queue={L:1, A:1, 2:1, 1L:1, 1A:1}
	 * L:1 -> 2:1, LL:1, 1A:1
	 * Since 2 and 1A have already been seen, add to those states.
	 * Queue={A:1, 2:2, 1L:1, 1A:2, LL:1}
	 * A:1 -> 1A:1, AL:1, AA.. no good so throw it out
	 * Queue={2:2, 1L:1, 1A:3, LL:1, AL:1}
	 * 
	 * Adding up the counts of the states found in the queue, we have 2+1+3+1+1=8,
	 * showing there are 8 valid combinations out of 9 since AA was thrown out.
	 */
    String execute() throws Exception {

    	MyQueue<DayState> queue = new MyQueue<DayState>();
    	seenStates = new HashMap<String, DayState>();
    	DayState initialState = new DayState();
    	queue.push(initialState);
    	
    	debug("Running for " + days + " days.");
    	int answer = 0;
    	DayValue[] dayValues = DayValue.values();
    	
    	while(!queue.isEmpty()) {
    		DayState currentState = queue.pop();
    		
    		for(DayValue day : dayValues) {
    			DayState newState = new DayState(currentState);
    			if(newState.addDay(day)) {
    				//This state is in the running to win the prize!
    				debug(currentState.toString() + " -> " + newState.toString());
    				DayState seenState = seenStates.get(newState.getStateString());
    				
    				if(newState.getDayCount() == days) {
    					//This string of days is a winner! Add it to the total.
    					answer += newState.getViewCount();
    				}
    				else if(seenState != null) {
    					//This state has been seen before, add count and don't put new state in the queue
    					seenState.addViews(currentState.getViewCount());
    				}
    				else {
    					//This is a new state
    					seenStates.put(newState.getStateString(), newState);
    					queue.push(newState);
    				}
    			}
    		}
    	}
    	
    	return NumberUtilies.longToString(answer);
    }
    
    
    enum DayValue {
    	
    	ON_TIME("O"),
    	LATE("L"),
    	ABSENT("A");
    	
    	private String value;
    	
    	DayValue(String s) {
    		value = s;
    	}
    	
    	public String toString() {
    		return value;
    	}
    }
    
    private class DayState {
    	
    	private int dayCount;
    	private int lateCount;
    	private int absentCount;
    	
    	//How many ways there are to get to this state.
    	private int viewCount;
    	
    	public DayState() {
    		dayCount = 0;
    		lateCount = 0;
    		absentCount = 0;
    		viewCount = 1;
    	}
    	
    	public DayState(DayState other) {
    		this.dayCount = other.dayCount;
    		this.lateCount = other.lateCount;
    		this.absentCount = other.absentCount;
    		this.viewCount = other.viewCount;
    	}
    	
    	public boolean addDay(DayValue day) {
    		switch (day) {
    		case ON_TIME: 
    			dayCount = dayCount + lateCount + 1;
    			lateCount = 0;
    			return true;
    		case LATE: 
    			lateCount = lateCount + 1; 
    			return lateCount <= MAX_CONSECUTIVE_LATE_COUNT;
    		case ABSENT: 
    			absentCount = absentCount + 1;
    			dayCount = dayCount + lateCount;
    			lateCount = 0;
    			return absentCount <= MAX_ABSENT_COUNT; 
    		}
    		
    		throw new IllegalStateException("Uh Oh");
    	}
    	
    	public int getDayCount() {
    		return dayCount + lateCount + absentCount;
    	}
    	
    	public int getViewCount() {
    		return viewCount;
    	}
    	
    	public void addViews(int views) {
    		viewCount += views;
    	}
    	
    	public String getStateString() {
    		StringBuilder sb = new StringBuilder();
    		sb.append(dayCount);
    		for(int i = 0; i < absentCount; i++) {
    			sb.append(DayValue.ABSENT.toString());
    		}
    		for(int i = 0; i < lateCount; i++) {
    			sb.append(DayValue.LATE.toString());
    		}
    		return sb.toString();
    	}
    	
    	public String toString() {
    		return getStateString() + ":" + viewCount;
    	}
    }
}











