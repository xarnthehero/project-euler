package projecteuler.utility;

import java.util.ArrayList;
import java.util.List;

/*
 * A NodePath is a list of TreeNode<Integer>s
 */
public class NodePath {

	private List<TreeNode<Integer>> nodes;
	
	public NodePath() {
		nodes = new ArrayList<TreeNode<Integer>>();
	}
	
	public NodePath(NodePath n) {
		nodes = new ArrayList<TreeNode<Integer>>();
		for(TreeNode<Integer> node : n.getNodes()) {
			nodes.add(node);
		}
	}
	
	public List<TreeNode<Integer>> getNodes() {
		return nodes;
	}
	
	public int getSum() {
		int sum = 0;
		for(TreeNode<Integer> node : nodes) {
			sum += node.getValue();
		}
		return sum;
	}
	
	public String getPath() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < nodes.size(); i++) {
			sb.append(nodes.get(i));
			if(i != nodes.size() - 1) {
				sb.append(" -> ");
			}
		}
		return sb.toString();
	}
	
	public void addNode(TreeNode<Integer> node) {
		nodes.add(node);
	}
	
	public String toString() {
		return getPath();
	}
}
