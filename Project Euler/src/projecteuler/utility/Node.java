package projecteuler.utility;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

	private T data;
	private int sortOrder;
	
	//Can be used in a linked list format
	private Node<T> next;
	private Node<T> previous;
	
	public Node(T t, int sortOrder) {
		this.data = t;
		this.sortOrder = sortOrder;
	}
	
	public Node(T t, Node<T> next) {
	    this.data = t;
	    this.next = next;
	}
	
	public void setNext(Node<T> next) {
	    this.next = next;
	    if(this != next.getPrevious()) {
	        next.setPrevious(this);
	    }
	}
	
	public Node<T> getNext() {
	    return next;
	}
	
	public void setPrevious(Node<T> previous) {
	    this.previous = previous;
	    if(this != previous.getNext()) {
	        previous.setNext(this);
	    }
	}
	
	public Node<T> getPrevious() {
	    return previous;
	}
	
	public T getData() {
		return data;
	}
	
	public int getSortOrder() {
	    return sortOrder;
	}
	
	public String toString() {
		return data.toString();
	}
	
	public int hashCode() {
		return data.hashCode();
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Node)) {
			return false;
		}
		return data.equals(((Node)o).getData());
	}
	
	public int compareTo(Node<T> other) {
	    return sortOrder - other.getSortOrder();
	}
	
}
