package test.algorithms.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import core.components.Heuristic;
import core.components.State;
import core.components.WeightedEdge;
import edu.uci.ics.jung.graph.Graph;

// TODO: Change to have a successor function instead of using
// a fully defined graph to begin.

/**
 * Implementation of the informed search algorithm A*. The graph must be fully
 * defined as a JUNG Graph, handling implicit graphs with successor generators
 * may be too far out of scope for this project as it is aiming to allow
 * users quick scripts to 
 * 
 * @author Mike Nowicki
 *
 * @param <V> - Vertex type, must extend the State class
 * @param <E> - Edge type
 */
public class AStarSearch<V extends State, E extends WeightedEdge> implements InformedSearch<V, E>{

	private Heuristic<V> heuristic;
	private Graph<V,E> graph;
	
	/**
	 * 
	 * @param graph - The graph to search through
	 * @param heuristic - Heuristic function to use
	 */
	public AStarSearch(Graph<V,E> graph, Heuristic<V> heuristic) {
		this.graph = graph;
		this.heuristic = heuristic;
	}
	
	/**
	 * Search method to perform A* search
	 * 
	 * @param start - The initial state
	 * @param goal - The goal state
	 * @return - An ordered list of the nodes on the solution path, 
	 * 			 null if no solution exists.
	 */
	public List<V> search(V start, V goal) {

		List<V> solution = new ArrayList<>();
		
		if (start.equals(goal)) {
			solution.add(start);
			return solution;
		}
		
		PriorityQueue<V> openList = new PriorityQueue<>(new StateComparator());
		HashMap<V,V> path = new HashMap<>();
		Set<V> closedList = new HashSet<>();
	
		// Initialize source node
		start.setG(0);
		start.setH(heuristic.evaluate(start));

		// Mark start and goal
		start.setStart(true);
		goal.setGoal(true);
		
		openList.add(start);
	
		while (!openList.isEmpty()) {
			
			V currentNode = openList.poll();
			
			// Goal test
			if (currentNode.equals(goal)) {
				solution = tracePath(path, currentNode);
				System.out.println("Solution found. Path is of length " + (solution.size()-1));
				return solution;
			}
			
			// Mark node for visualization
			currentNode.setSearched(true);
			// Add to closed list to prevent re-expansion
			closedList.add(currentNode);
			
			// Iterate over neighbours to add to queue
			for (V neighbour : graph.getNeighbors(currentNode)) {
				
				if (closedList.contains(neighbour))
					continue;
				
				// Compute new path cost value to neighbour
				int pathCost = graph.findEdge(currentNode, neighbour).getEdgeWeight();
				int newG = currentNode.getG() + pathCost;
				
				// In case it is in the queue but with a lower G value, in which
				// case we skip recomputing and adding to the queue
				if (newG < neighbour.getG()) {
					neighbour.setG(newG);
					
					// Heuristic evaluation
					int hValue = heuristic.evaluate(neighbour);
					neighbour.setH(hValue);
					
					// Add to path mapping
					path.put(neighbour, currentNode);
					
					// Add to queue
					openList.add(neighbour);
					
				}
				
			}
			
		}
		
		// Evaluated every reachable node and could not reach the goal, return
		// null to indicate a failed search.
		return null;
	}
	
	public List<V> tracePath(HashMap<V, V> path, V node) {
		
		List<V> solution = new ArrayList<>();
		
		solution.add(node);
		
		while(path.containsKey(node)) {
			node = path.get(node);
			node.setOnPath(true);	// Mark for visualization
			solution.add(node);
		}
		
		// Since list is built by tracing back from the goal we must reverse it
		Collections.reverse(solution);
		
		return solution;
	
	}

	private class StateComparator implements Comparator<V> {

		@Override
		public int compare(V state1, V state2) {
			if (state1.getF() > state2.getF()) {
				return 1;
			} else if (state1.getF() < state2.getF()) {
				return -1;
			}
			return 0;
		}
		
	}
	
}
