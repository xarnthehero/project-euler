package projecteuler.utility;

import java.math.BigDecimal;

public class TreeNode<T> {

	protected TreeNode<T> parent;
	protected TreeNode<T> childLeft;
	protected TreeNode<T> childRight;
	protected T value;
	
	
	public void setValue(T v) {
		value = v;
	}
	
	public T getValue() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public void setChildLeft(TreeNode<T> l) {
		childLeft = l;
		l.parent = this;
	}
	
	public TreeNode<T> getChildLeft() {
		return childLeft;
	}
	
	public void setChildRight(TreeNode<T> r) {
		childRight = r;
		r.parent = this;
	}
	
	public TreeNode<T> getChildRight() {
		return childRight;
	}
	
	public TreeNode<T> getParent() {
		return parent;
	}
	
	/**
	 * Don't call this if value isn't numeric.
	 * @return the value of left + right + this
	 */
	public BigDecimal getTreeValue() {
		BigDecimal sum = BigDecimal.ZERO;
		if(childLeft != null) {
			sum = sum.add(new BigDecimal(childLeft.getTreeValue().toString()));
		}
		if(childRight != null) {
			sum = sum.add(new BigDecimal(childRight.getTreeValue().toString()));
		}
		sum = sum.add(new BigDecimal(value.toString()));
		
		return sum;
	}
	
	public int getLevel() {
		
		int level = 1;
		TreeNode<T> currentNode = this;
		while(currentNode.getParent() != null) {
			level += 1;
			currentNode = currentNode.parent;
		}
		
		return level;
	}
}
