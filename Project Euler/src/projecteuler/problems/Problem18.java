package projecteuler.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projecteuler.utility.NodePath;
import projecteuler.utility.MultiParentTreeNode;

/*
Maximum path sum I
Problem 18
By starting at the top of the triangle below and moving to adjacent numbers on the row below, the maximum total from top to bottom is 23.

   3
  7 4
 2 4 6
8 5 9 3

That is, 3 + 7 + 4 + 9 = 23.

Find the maximum total from top to bottom of the triangle below:

                      75
                     95 64
                   17 47 82
                 18 35 87 10
                20 04 82 47 65
               19 01 23 75 03 34
              88 02 77 73 07 63 67
            99 65 04 28 06 16 70 92
          41 41 26 56 83 40 80 70 33
         41 48 72 33 47 32 37 16 94 29
       53 71 44 65 25 43 91 52 97 51 14
     70 11 33 28 77 73 17 78 39 68 17 57
   91 71 52 38 17 14 91 43 58 50 27 29 48
 63 66 04 68 89 53 67 30 73 16 69 87 40 31
04 62 98 27 23 09 70 98 73 93 38 53 60 04 23

NOTE: As there are only 16384 routes, it is possible to solve this problem by trying every route. However, Problem 67, is the same challenge with a triangle containing one-hundred rows; it cannot be solved by brute force, and requires a clever method! ;o)

*/
public class Problem18 extends Problem {	
	
	private Map<MultiParentTreeNode<Integer>, NodePath> nodePathMap = new HashMap<MultiParentTreeNode<Integer>, NodePath>();
	private Map<MultiParentTreeNode<Integer>, NodePath> finalNodePathMap = new HashMap<MultiParentTreeNode<Integer>, NodePath>();
	
	private String inputFileName;
	
	public Problem18() {
		this("Problem18.data");
	}
	
	public Problem18(String name) {
		inputFileName = name;
	}
	

	@Override
	String getProblemStatement() {
		return "Finding the maximum total from top to bottom in " + inputFileName;
	}
	
	String execute() {

		MultiParentTreeNode<Integer> top = null;
		NodePath np = new NodePath();
		
		try {
			InputStream inputStream = this.getClass().getClassLoader()
					.getResourceAsStream("projecteuler/data/" + inputFileName);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			List<MultiParentTreeNode<Integer>> lastNodeLine = new ArrayList<MultiParentTreeNode<Integer>>();
			List<MultiParentTreeNode<Integer>> currentNodeLine = new ArrayList<MultiParentTreeNode<Integer>>();
			

			
			String currentLine;
			while((currentLine = br.readLine()) != null) {
				String[] values = currentLine.trim().split(" ");
				if(!lastNodeLine.isEmpty()) {
					for(int i = 0; i < values.length; i++) {
						Integer value = Integer.parseInt(values[i]);
						MultiParentTreeNode<Integer> node = new MultiParentTreeNode<Integer>();
						node.setValue(value);
						currentNodeLine.add(node);
						
						if(i != 0 && i != values.length - 1) {
							node.setParentLeft(lastNodeLine.get(i - 1));
							node.setParentRight(lastNodeLine.get(i));
							node.setSameLevelLeft(currentNodeLine.get(i-1));
						}
						else if(i == 0) {
							node.setParentRight(lastNodeLine.get(0));
							np.addNode(node);
							
						}
						else {
							node.setParentLeft(lastNodeLine.get(lastNodeLine.size() - 1));
							node.setSameLevelLeft(currentNodeLine.get(i-1));
						}
					}
					
					lastNodeLine = currentNodeLine;
					currentNodeLine = new ArrayList<MultiParentTreeNode<Integer>>();
				}
				else {
					//first iteration
					MultiParentTreeNode<Integer> node = new MultiParentTreeNode<Integer>();
					node.setValue(Integer.parseInt(values[0]));
					lastNodeLine.add(node);
					top = node;
				}
			}
			
		}
		catch(IOException e) {
			debug(e.getMessage());
		}

		nodePathMap.put(top, new NodePath());
		
		MultiParentTreeNode<Integer> currentNode = top;
		while(currentNode != null) {
			processNode(currentNode);
			NodePath currentNodePath = nodePathMap.get(currentNode);
			int currentSum = currentNodePath.getSum();
			debug("\t(" + currentSum + ") " + currentNodePath.getPath());
			if(currentNode.getSameLevelRight() != null) {
				currentNode = currentNode.getSameLevelRight();
			}
			else {
				while(currentNode.getSameLevelLeft() != null) {
					currentNode = currentNode.getSameLevelLeft();
				}
				currentNode = currentNode.getChildLeft();
			}
		}
		

		
		int max = 0;
		MultiParentTreeNode<Integer> maxNode = null;
		for(MultiParentTreeNode<Integer> node : finalNodePathMap.keySet()) {
			int sum = node.getValueAsInt() + nodePathMap.get(node).getSum();
			debug("Path ending " + node + " = " + sum);
			if(sum > max) {
				max = sum;
				maxNode = node;
			}
		}
		
		NodePath maxSumPath = new NodePath(nodePathMap.get(maxNode));
		maxSumPath.addNode(maxNode);
		debug(maxSumPath.getPath());
		
		return new Integer(maxSumPath.getSum()).toString();
	}
	
	private void processNode(MultiParentTreeNode<Integer> node) {
		
		if(node == null) {return;}
		
		debug("L" + node.getLevel() + "  " + node.toString());
		
		NodePath parentLeftPath = null;
		NodePath parentRightPath = null;
		
		if(node.getParentLeft() != null) {
			parentLeftPath = new NodePath(nodePathMap.get(node.getParentLeft()));
			parentLeftPath.addNode(node.getParentLeft());
		}
		if(node.getParentRight() != null) {
			parentRightPath = new NodePath(nodePathMap.get(node.getParentRight()));
			parentRightPath.addNode(node.getParentRight());
		}
		
		if(parentLeftPath != null && parentRightPath != null) {
			NodePath bestPath = parentLeftPath.getSum() > parentRightPath.getSum() ? parentLeftPath : parentRightPath;
			nodePathMap.put(node, bestPath);
		}
		else if(parentLeftPath != null) {
			nodePathMap.put(node, parentLeftPath);
		}
		else if(parentRightPath != null) {
			nodePathMap.put(node, parentRightPath);
		}
		else {
			nodePathMap.put(node, new NodePath());
		}
		
		if(node.getChildLeft() == null && node.getChildRight() == null) {
			finalNodePathMap.put(node, nodePathMap.get(node));
		}
		
	}
}

