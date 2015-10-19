package algorithms.spanningtree;

import core.components.Edge;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;

/**
 * Interface for classes that will find minimal spanning trees
 * in a given graph.
 * 
 * @author Mike Nowicki
 *
 * @param <V> - Vertex type
 * @param <E> - Edge type
 */
public interface MinimalSpanningTree<V, E extends Edge> {
	

	/**
	 * Finds a the minimal spanning tree in the given graph
	 * 
	 * @param graph - The graph to search
	 * @return - A minimal spanning tree
	 */
	public Forest<V, E> findMinimalSpanningTree(Graph<V, E> graph);
	
	public void visualizeTree(Forest<V,E> forest);
	
}
