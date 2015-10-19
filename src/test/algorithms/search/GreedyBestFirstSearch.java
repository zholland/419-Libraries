package test.algorithms.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import algorithms.search.tools.Heuristic;
import algorithms.search.tools.State;
import edu.uci.ics.jung.graph.Graph;

/**
 * Simple GBFS search implementation. Does not perform implicit search,
 * i.e., by having a successor generator (yet), only handles explicitly defined
 * graphs at the moment. This will be extended later.
 * 
 * @author Mike-SSD
 *
 * @param <V> - Vertex type, must extend Search.Tools.State
 * @param <E> - The edge type
 */
public class GreedyBestFirstSearch<V extends State,E> implements InformedSearch<V, E> {

	private Heuristic<V> heuristic;
	private Graph<V,E> graph;

	/**
	 * 
	 * @param graph - The graph to search through
	 * @param heuristic - Heuristic function to use
	 */
	public GreedyBestFirstSearch(Graph<V,E> graph, Heuristic<V> heuristic) {
		this.graph = graph;
		this.heuristic = heuristic;
	}

	// TODO: Write a search method that uses a successor function to search
	//		 implicit graphs.

	@Override
	public List<V> search(V start, V goal) {

		List<V> solution = new ArrayList<>();

		if (start.equals(goal)) {
			solution.add(start);
			return solution;
		}

		PriorityQueue<V> openList = new PriorityQueue<>(new StateComparator());
		HashMap<V,V> path = new HashMap<>();
		HashSet<V> closedList = new HashSet<>();

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
			closedList.add(currentNode);

			// Iterate over neighbours to add to queue
			for (V neighbour : graph.getNeighbors(currentNode)) {

				if (!closedList.contains(neighbour) && !openList.contains(neighbour)) { 

					int newG = currentNode.getG() + 1;
					neighbour.setG(newG);

					// Heuristic evaluation
					int hValue = heuristic.evaluate(neighbour);
					neighbour.setH(hValue);

					// Add to path mapping
					path.put(neighbour, currentNode);

					// Add to queue
					openList.add(neighbour);
					
				} else {
					
					int hValue = heuristic.evaluate(neighbour);
					
					if (hValue < neighbour.getH()) {
						neighbour.setH(hValue);
						// Add to path mapping
						path.put(neighbour, currentNode);
						// Add to queue
						openList.add(neighbour);
					}
				
				}

			}

		}

		return null;
	}

	@Override
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

	/**
	 * Comparator based on heuristic values
	 * 
	 * @author Mike Nowicki
	 *
	 */
	private class StateComparator implements Comparator<V> {

		@Override
		public int compare(V state1, V state2) {
			if (state1.getH() > state2.getH()) {
				return 1;
			} else if (state1.getH() < state2.getH()) {
				return -1;
			}
			return 0;
		}

	}

}
