package algorithms.spanningtree;

import java.util.Collection;

import core.components.Edge;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;

public class PrimSpanningTree<V, E extends Edge> implements MinimalSpanningTree<V, E> {

	@Override
	public Forest<V,E> findMinimalSpanningTree(Graph<V,E> graph) {
		
		// Nothing to do if no vertices or edges
		if (graph.getVertexCount() == 0 || graph.getEdgeCount() == 0) {
			return null;
		}
		
		Forest<V, E> spanningTree = new DelegateForest<>();
		
		// Place all vertices in the tree
		for (V vertex : graph.getVertices()) {
			spanningTree.addVertex(vertex);
		}
		
		Collection<V> vertices = graph.getVertices();
		// Make a collection of vertices that are added, copy the original and
		// empty to allow comparisons
		Collection<V> addedVertices = vertices;
		addedVertices.clear();
		
		// Pick a vertex at random, add the the added vertices and begin
		V firstVertex = vertices.iterator().next();
		vertices.remove(firstVertex);
		addedVertices.add(firstVertex);
		
		while (vertices.size() != 0) {
			
			
		}
		
		return spanningTree;
	}

	@Override
	public void visualizeTree(Forest<V, E> forest) {
		// TODO Visualize the forest
		
	}

}
