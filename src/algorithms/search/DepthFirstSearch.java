package algorithms.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import core.components.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class DepthFirstSearch<V, E extends Edge> implements UninformedSearch<V, E>{

	/**
	 * Non-recursive DFS implementation
	 */
	public List<V> search(Graph<V,E> graph, V root) {

		List<V> nodeOrder = new ArrayList<>();
		Stack<V> openList = new Stack<>();
		boolean directed = false;

		if (graph.getDefaultEdgeType() == EdgeType.DIRECTED) {
			directed = true;
		}
		
		openList.push(root);
		
		while (!openList.isEmpty()) {
	
			V currentNode = openList.pop();
			nodeOrder.add(currentNode);
			
			Collection<V> neighbours = null;
			
			if (directed) {
				neighbours = getDirectedNeighbours(graph, currentNode);
			} else {
				neighbours = graph.getNeighbors(currentNode);
			}
			
			for (V neighbour : neighbours) {
				if (nodeOrder.contains(neighbour) || openList.contains(neighbour)){
					continue;
				}
				openList.push(neighbour);
			}

		}		
		return nodeOrder;
	}
	
	/**
	 * Scans all outward edges from the current node and adds the destination
	 * to the collection of neighbours.
	 * 
	 * @param userGraph The graph being searched.
	 * @param currentNode The node who's neighbours we are looking for
	 * @return A collection of nodes 
	 */
	public Collection<V> getDirectedNeighbours(Graph<V, E> userGraph, V currentNode) {
		Collection<V> neighbours = new HashSet<>();
		for (E edge : userGraph.getOutEdges(currentNode)) {
			neighbours.add(userGraph.getDest(edge));
		}
		return neighbours;
	}
	
	// TODO: Implement this!
	@Override
	public void visualizeSearch(Graph<V,E> graph, V root) {
		
	}
}
