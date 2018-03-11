package projecteuler.utility;

public class NodeConnector {

    Node<?> n1, n2;
    Integer weight;

    public NodeConnector(Node<?> n1, Node<?> n2, Integer weight) {
        this.n1 = n1;
        this.n2 = n2;
        this.weight = weight;
    }

    public Node<?> getNode1() {
        return n1;
    }

    public Node<?> getNode2() {
        return n2;
    }

    public <T extends Comparable<T>> Node<T> getOtherNode(Node<T> node) {
        return n1.equals(node) ? (Node<T>) n2 : (Node<T>) n1;
    }

    public Integer getWeight() {
        return weight;
    }

    public String toString() {
        return n1 + " --" + weight + "-- " + n2;
    }

    public boolean contains(Node<?> n) {
        return n1.equals(n) || n2.equals(n);
    }
}
