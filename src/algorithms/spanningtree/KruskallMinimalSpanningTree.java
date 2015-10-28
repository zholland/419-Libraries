package algorithms.spanningtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import core.components.UnionFind;
import core.components.Edge;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;

/**
 * Spanning tree generator using Kruskall's algorithm.
 * 
 * @author Mike Nowicki
 *
 * @param <V> - Vertex type
 * @param <E> - Edge type
 */
public class KruskallMinimalSpanningTree<V, E extends Edge> implements MinimalSpanningTree<V, E> {

	/**
	 * @param graph - The graph to search
	 * @return A Forest object containing the minimal spanning tree or
	 *         null if one does not exist.
	 */
	@Override
	public Forest<V, E> findMinimalSpanningTree(Graph<V, E> graph) {
		
		// Nothing to do if no vertices or edges
		if (graph.getVertexCount() == 0 || graph.getEdgeCount() == 0) {
			return null;
		}
		
		Forest<V, E> spanningTree = new DelegateForest<>();
		UnionFind<V> unionFind = new UnionFind<>();
		
		// Place each vertex into its own set, also add to the forest
		for (V vertex : graph.getVertices()) {
			spanningTree.addVertex(vertex);
			unionFind.makeSet(vertex);
		}
		
		// Place list of edges into sortable list
		List<E> orderedEdgeWeights = new ArrayList<>();
		for (E edge : graph.getEdges()) {
			orderedEdgeWeights.add(edge);
		}
		orderedEdgeWeights.sort(new EdgeComparator<>());
		
		// Loop over edges
		for (int i = 0; i < orderedEdgeWeights.size(); i++) {
			
			E edge = orderedEdgeWeights.get(i);
			Collection<V> vertices = graph.getIncidentVertices(edge);
			V u = null;
			V v = null;
			
			// Lame way to get the source and dest of edge
			int ctr = 0;
			Iterator<V> itr = vertices.iterator();
			while (itr.hasNext()) {
				if (ctr == 0) {
					u = itr.next();
				} else {
					v = itr.next();
				}
				ctr++;
			}
			
			// If u and v are not in the same set, add an edge between them
			// in the tree and union their two sets
			if (!unionFind.find(u).equals(unionFind.find(v))){
				spanningTree.addEdge(edge, u, v);
				unionFind.union(u, v);
			}
			// Once we have n-1 edges on n vertices the tree is complete
			if (spanningTree.getEdgeCount() == (spanningTree.getVertexCount() - 1)) {
				break;
			}
		}
		return spanningTree;
	}

}
