package algorithms.search.tools;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Generic Union-Find data structure
 * 
 * @author Michael
 *
 * @param <V> - Type of object being used
 */
public class UnionFind<V> {

	private HashMap<V, HashSet<V> > mapping;

	public UnionFind() {	
		mapping = new HashMap<>();
	}

	/**
	 * Initialize the set of nodes in the neighbourhood of v,
	 * namely v itself to begin.
	 * 
	 * @param v - The node to store the key for and create a hashset for
	 */
	public void makeSet(V v) {
		HashSet<V> set = new HashSet<V>();
		set.add(v);
		mapping.put(v, set);
	}
	
	/**
	 * Get the hashset for the vertex v
	 * @param v - Current vertex who's component we want to examine.
	 * @return - The set of vetices in the neighbourhood of v.
	 */
	public HashSet<V> find(V v) {
		return mapping.get(v);
	}

	/**
	 * Joins the two sets, representing the components that
	 * u and v belong to initially.
	 * 
	 * @param u - First vertex to union with.
	 * @param v - Second vertex to union with.
	 */
	public void union(V u, V v) {
		HashSet<V> union = new HashSet<>();
		HashSet<V> setOne = mapping.get(u);
		HashSet<V> setTwo = mapping.get(v);
		// Combine all vertices into a new set
		for (V neighbour : setOne) {
			union.add(neighbour);
		}
		for (V neighbour : setTwo) {
			union.add(neighbour);
		}
		// Place the new set into the hashmap for each key
		for (V neighbour : union) {
			mapping.put(neighbour, union);
		}
	}	
}
