package projecteuler.problems;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import projecteuler.utility.MyQueue;
import projecteuler.utility.Node;
import projecteuler.utility.NodeConnector;
import projecteuler.utility.Utilities;

/*
Minimal network
Problem 107
The following undirected network consists of seven vertices and twelve edges with a total weight of 243.


The same network can be represented by the matrix below.

    A	B	C	D	E	F	G
A	-	16	12	21	-	-	-
B	16	-	-	17	20	-	-
C	12	-	-	28	-	31	-
D	21	17	28	-	18	19	23
E	-	20	-	18	-	-	11
F	-	-	31	19	-	-	27
G	-	-	-	23	11	27	-
However, it is possible to optimise the network by removing some edges and still ensure that all points on the network remain connected. The network which achieves the maximum saving is shown below. It has a weight of 93, representing a saving of 243 − 93 = 150 from the original network.

Using network.txt (right click and 'Save Link/Target As...'), a 6K text file containing a network with forty vertices, and given in matrix form, find the maximum saving which can be achieved by removing redundant edges whilst ensuring that the network remains connected.
*/
public class Problem107 extends Problem {

    private Map<Node<String>, List<NodeConnector>> nodeMap;
    private List<Node<String>> completedNodes;
    private List<Node<String>> incompleteNodes;

	@Override
	String getProblemStatement() {
		return "Finding maximum removeable weight";
	}

    /*
     * Execution steps - 1) Create the map of nodes and links, calculate the total weight in the process 2) Loop through
     * all nodes. On each node, search for a circle looping back to that node. a) If no circle is found, that node is
     * complete and need not be checked again 3) Upon finding a circle, remove the link with the highest weight, search
     * again for a circle 4) When all nodes are complete, add up all weights and subtract from the initial total weight
     */
    String execute() throws Exception {

        BufferedReader reader = Utilities.getReaderForProblem(this.getClass());
        // BufferedReader reader = Utilities.getReader("/projecteuler/data/Problem107Test.data");

        String[] currentWeights = reader.readLine().split(",");
        int numNodes = currentWeights.length;
        int totalWeight = 0;
        int weightShed = 0;

        
        nodeMap = new HashMap<Node<String>, List<NodeConnector>>();
        completedNodes = new ArrayList<Node<String>>(numNodes);
        incompleteNodes = new ArrayList<Node<String>>(numNodes);
        
        

        
        // 1)
        for (int i = 0; i < numNodes; i++) {
            Node<String> node = new Node<String>("N" + i, i);
            incompleteNodes.add(node);
            nodeMap.put(node, new ArrayList<NodeConnector>());
            debug("Node Created - " + node);
        }
        
        //TEMP CODE !!
        Node<String> n0 = incompleteNodes.get(0), n2 = incompleteNodes.get(2);
        
        for (int i = 0; i < numNodes; i++) {
            Node<String> currentNode = incompleteNodes.get(i);
            for (int j = i + 1; j < numNodes; j++) {
                if (!"-".equals(currentWeights[j])) {
                    Integer nodeWeight = Integer.parseInt(currentWeights[j]);
                    Node<String> otherNode = incompleteNodes.get(j);
                    NodeConnector nc = new NodeConnector(currentNode, otherNode, nodeWeight);
                    totalWeight += nodeWeight;
                    nodeMap.get(currentNode).add(nc);
                    nodeMap.get(otherNode).add(nc);
                    debug("Creating Link: " + currentNode + " --" + nodeWeight + "-- " + otherNode);
                }
            }
            String currentLine = reader.readLine();
            if (currentLine != null) {
                currentWeights = currentLine.split(",");
            }
        }
        
        while (!incompleteNodes.isEmpty()) {
            Node<String> currentNode = incompleteNodes.get(0);
            // 2)
            List<NodeConnector> circle = findCircle(currentNode);
            // 3)
            if (circle != null) {
                NodeConnector maxWeight = null;
                for (NodeConnector nc : circle) {
                    if (maxWeight == null || nc.getWeight() > maxWeight.getWeight()) {
                        maxWeight = nc;
                    }
                }
                debug("Removing Connector: " + maxWeight + "     " + getCircleString(circle));
                nodeMap.get(maxWeight.getNode1()).remove(maxWeight);
                nodeMap.get(maxWeight.getNode2()).remove(maxWeight);
                weightShed += maxWeight.getWeight();
            } else {
                debug("Completed With Node " + currentNode);
                completedNodes.add(currentNode);
                incompleteNodes.remove(currentNode);
            }
        }

        // 4)
        debug("--- After Removing All Edges ---");
        Set<Node<String>> keySet = nodeMap.keySet();
        List<Node<String>> sortedKeys = new ArrayList<Node<String>>(keySet);
        Collections.sort(sortedKeys);
        HashSet<NodeConnector> alreadyDisplayed = new HashSet<NodeConnector>();
        int totalAfterRemoval = 0;
        for (Node<String> node : sortedKeys) {
            for (NodeConnector nc : nodeMap.get(node)) {
                if (!alreadyDisplayed.contains(nc)) {
                    alreadyDisplayed.add(nc);
                    debug(nc.toString());
                    totalAfterRemoval += nc.getWeight();
                }
            }
        }

        debug("Printing all reachable nodes:");
        debug("\t" + getAllReachableNodes(completedNodes.get(0)).toString());
        
        debug("Starting weight (" + totalWeight + ") - end weight (" + totalAfterRemoval + ") = removed weight ("
                        + weightShed + ")");
        
        return new Integer(weightShed).toString();
        
        
        
    }

    private List<NodeConnector> findCircle(Node<String> originalNode) {
        List<NodeConnector> originalConnections = (List<NodeConnector>) Utilities
                        .makeShallowCopy(nodeMap.get(originalNode));
        if (originalConnections.size() <= 1) {
            return null;
        }

        for (NodeConnector nc : originalConnections) {
            List<NodeConnector> path = new ArrayList<NodeConnector>();
            path.add(nc);
            Set<Node<String>> visitedNodes = new HashSet<Node<String>>();
            visitedNodes.add(originalNode);
            List<NodeConnector> fullPath = findCircle(originalNode, nc.getOtherNode(originalNode), originalNode, path,
                            visitedNodes);
            if (fullPath != null) {
                return fullPath;
            }
        }

        return null;
    }

    /*
     * This method will check the currentNode's connections. If we haven't used that connection yet and we haven't been
     * to that node before (excluding the originalNode), then it is a candidate for continuation. Of the remaining
     * connections, if one connects to the originalNode, the circle is complete. Otherwise, update the path,
     * visitedNodes, and continue searching recursively.
     */
    private List<NodeConnector> findCircle(Node<String> originalNode, Node<String> currentNode, Node<String> lastNode,
                    List<NodeConnector> path, Set<Node<String>> visitedNodes) {
        List<NodeConnector> possibleConnections = (List<NodeConnector>) Utilities
                        .makeShallowCopy(nodeMap.get(currentNode));
        List<NodeConnector> toBeRemoved = new ArrayList<NodeConnector>();

        for (NodeConnector nc : possibleConnections) {
            Node<String> otherNode = (Node<String>) nc.getOtherNode(currentNode);
            if (visitedNodes.contains(otherNode) && otherNode != lastNode) {
                // Circle found starting with currentNode going to lastNode
                LinkedList<NodeConnector> circle = new LinkedList();
                circle.addAll(path);
                while(!circle.getFirst().getNode1().equals(otherNode) && !circle.getFirst().getNode2().equals(otherNode)) {
                    circle.removeFirst();
                }
                
                //Without this, we are including a connection to the circle node
                //ex N0 -> N37 -> .... -> N37, including the 0->37 link
                if(!otherNode.equals(originalNode)) {
                    circle.removeFirst();
                }
                circle.add(nc);
                return circle;
            } else if (path.contains(nc)) {
                toBeRemoved.add(nc);
            }
        }

        possibleConnections.removeAll(toBeRemoved);

        // This is a dead end
        if (possibleConnections.isEmpty()) {
            return null;
        }

        visitedNodes.add(currentNode);

        for (NodeConnector nc : possibleConnections) {
            List<NodeConnector> newPath = (List<NodeConnector>) Utilities.makeShallowCopy(path);
            newPath.add(nc);
            Set<Node<String>> newVisitedNodes = (Set<Node<String>>) Utilities.makeShallowCopy(visitedNodes);
            List<NodeConnector> circle = findCircle(originalNode, (Node<String>) nc.getOtherNode(currentNode),
                            currentNode, newPath, newVisitedNodes);
            if (circle != null) {
                return circle;
            }
        }

        return null;
    }
    
    private String getCircleString(List<NodeConnector> circle) {
        if(circle == null || circle.size() <= 2) {
            error("Bad circle - " + circle);
            return "";
        }
        
        StringBuilder circleString = new StringBuilder();
        NodeConnector first = circle.get(0);
        NodeConnector second = circle.get(1);
        Node<String> currentNode = (Node<String>)first.getNode1();
        if(currentNode.equals(second.getNode1()) || currentNode.equals(second.getNode2())) {
            currentNode = first.getOtherNode(currentNode);
        }
        
        circleString.append(currentNode);
        for(NodeConnector nc : circle) {
            circleString.append(" --" + nc.getWeight() + "-- " + nc.getOtherNode(currentNode));
            currentNode = nc.getOtherNode(currentNode);
        }
        
        return circleString.toString();
    }
    
    
    //To test if we can still reach all nodes, we cant :(
    private List<Node<String>> getAllReachableNodes(Node<String> n) {
        Set<Node<String>> nodesReached = new HashSet<Node<String>>();
        Set<NodeConnector> connectorsReached = new HashSet<NodeConnector>();
        nodesReached.add(n);
        
        List<NodeConnector> connectors = new LinkedList<NodeConnector>();
        connectors.addAll(nodeMap.get(n));
        connectorsReached.addAll(nodeMap.get(n));
        while(!connectors.isEmpty()) {
            NodeConnector nc = connectors.remove(0);
            if(!nodesReached.contains(nc.getNode1())) {
                nodesReached.add((Node<String>)nc.getNode1());
                for(NodeConnector nc2 : nodeMap.get(nc.getNode1())) {
                    if(!connectorsReached.contains(nc2)) {
                        connectors.add(nc2);
                        connectorsReached.add(nc2);

                    }
                }
            }
            if(!nodesReached.contains(nc.getNode2())) {
                nodesReached.add((Node<String>)nc.getNode2());
                for(NodeConnector nc2 : nodeMap.get(nc.getNode2())) {
                    if(!connectorsReached.contains(nc2)) {
                        connectors.add(nc2);
                        connectorsReached.add(nc2);

                    }
                }
            }
        }
        List<Node<String>> nodeList = new ArrayList<Node<String>>(nodesReached);
        Collections.sort(nodeList);
        return nodeList;
    }
    
    private boolean isReachable(Node<String> n1, Node<String> n2) {
        MyQueue<NodeConnector> q = new MyQueue<NodeConnector>();
        Set<NodeConnector> visitedConnectors = new HashSet<NodeConnector>();
        
        List<NodeConnector> ncList = nodeMap.get(n1);
        q.pushAll(ncList);
        
        NodeConnector currentConnector;
        while((currentConnector = q.pop()) != null) {
            if(!visitedConnectors.contains(currentConnector)) {
                if(currentConnector.contains(n2)) {
                    return true;
                }
                q.pushAll(nodeMap.get(currentConnector.getNode1()));
                q.pushAll(nodeMap.get(currentConnector.getNode2()));
                visitedConnectors.add(currentConnector);
            }
        }
        return false;
    }
}







