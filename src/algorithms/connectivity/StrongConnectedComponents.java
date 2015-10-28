package algorithms.connectivity;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

/**
 * Interface used as the supertype for all component algorithms
 * 
 * @author Mike Nowicki
 *
 * @param <V> - The vertex type
 * @param <E> - The edge type
 */
public interface StrongConnectedComponents<V,E> {

	/**
	 * Search the graph to find the connected components
	 *
	 * @param graph The graph to saerch
	 * @return A 2D collection of lists representing each component
	 */
	List<List<V>> findComponents(Graph<V,E> graph);

	/**
	 * Creates a new frame and displays the initial graph and the
	 * connected components coded by colour.
	 */
	void visualizeSearch(Graph<V,E> graph);
}
