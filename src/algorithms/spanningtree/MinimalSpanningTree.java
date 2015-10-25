package algorithms.spanningtree;

import core.components.Edge;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;

import java.util.Comparator;

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

	/**
	 * Hacky casting but w/e
	 *
	 * @author Mike Nowicki
	 *
	 */
	class EdgeComparator<T extends Edge> implements Comparator<T> {

		@Override
		public int compare(T e1, T e2) {
			if (e1.getWeight().getClass() == Double.class) {
				if ((Double)e1.getWeight() < (Double)e2.getWeight()) {
					return -1;
				} else if ((Double)e1.getWeight() > (Double)e2.getWeight()) {
					return 1;
				}
				return 0;
			} else if (e1.getWeight().getClass() == Integer.class) {
				if ((Integer)e1.getWeight() < (Integer)e2.getWeight()) {
					return -1;
				} else if ((Integer)e1.getWeight() > (Integer)e2.getWeight()) {
					return 1;
				}
				return 0;
			} else {
				return 0;
			}
		}
	}
}
