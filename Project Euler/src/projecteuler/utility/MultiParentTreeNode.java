package projecteuler.utility;

public class MultiParentTreeNode<T> extends TreeNode<T> {

	private MultiParentTreeNode<T> parentLeft;
	private MultiParentTreeNode<T> parentRight;

	private MultiParentTreeNode<T> sameLevelLeft;
	private MultiParentTreeNode<T> sameLevelRight;
	
	

	public void setParentLeft(MultiParentTreeNode<T> p) {
		parentLeft = p;
		p.childRight = this;
	}
	
	public MultiParentTreeNode<T> getParentLeft() {
		return parentLeft;
	}
	
	public void setParentRight(MultiParentTreeNode<T> p) {
		parentRight = p;
		p.childLeft = this;
	}
	
	public MultiParentTreeNode<T> getParentRight() {
		return parentRight;
	}
	
	public void setChildLeft(MultiParentTreeNode<T> l) {
		childLeft = l;
		l.parentRight = this;
	}
	
	public MultiParentTreeNode<T> getChildLeft() {
		return (MultiParentTreeNode<T>)childLeft;
	}
	
	public void setChildRight(MultiParentTreeNode<T> r) {
		childRight = r;
		r.parentLeft = this;
	}
	
	public MultiParentTreeNode<T> getChildRight() {
		return (MultiParentTreeNode<T>)childRight;
	}
	
	public void setSameLevelLeft(MultiParentTreeNode<T> l) {
		sameLevelLeft = l;
		l.sameLevelRight = this;
	}
	
	public MultiParentTreeNode<T> getSameLevelLeft() {
		return sameLevelLeft;
	}
	
	public void setSameLevelRight(MultiParentTreeNode<T> r) {
		sameLevelRight = r;
		r.sameLevelLeft = this;
	}
	
	public MultiParentTreeNode<T> getSameLevelRight() {
		return sameLevelRight;
	}
	
	public int getValueAsInt() {
		return (Integer)value;
	}
	
	public int getLevel() {
		
		int level = 1;
		MultiParentTreeNode<T> currentNode = this;
		while(currentNode.getParentLeft() != null || currentNode.getParentRight() != null) {
			level += 1;
			currentNode = currentNode.getParentLeft() != null ? currentNode.getParentLeft() : currentNode.getParentRight();
		}
		
		return level;
	}

}
