package projecteuler.utility;

import java.util.Collection;
import java.util.LinkedList;

public class MyQueue <T> {

    private LinkedList<T> list;
    
    public MyQueue() {
        list = new LinkedList<T>();
    }
    
    public void push(T t) {
        list.addLast(t);
    }
    
    public void pushAll(Collection<T> c) {
        list.addAll(c);
    }
    
    public T pop() {
        if(list.isEmpty()) {return null;}
        return list.removeFirst();
    }
    
    public T peek() {
        return list.getFirst();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
