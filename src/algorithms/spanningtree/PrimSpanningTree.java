package algorithms.spanningtree;

import java.util.*;

import core.components.Edge;
import core.components.Vertex;
import core.visualizer.Visualizer;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @param <V> The vertex type
 * @param <E> The edge type, extending core.Edge
 *
 * @author Mike Nowicki
 */
public class PrimSpanningTree<V, E extends Edge> implements MinimalSpanningTree<V, E> {

	/**
	 * Generate a minimum spanning tree based on Prim's algorithm. Not quite complete yet...
	 *
	 * @param graph - The graph to search
	 * @return A tree representing a minimal spanning tree using
	 *         Prim's algorithm, or null if one doesn't exist.
	 */
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
		
		Set<V> vertices = new HashSet<>();
		vertices.addAll(graph.getVertices());

		Queue<E> edges = new PriorityQueue<>(new EdgeComparator<>());

		// Pick a vertex at random, add the the added vertices and begin
		V vertex = vertices.iterator().next();
		vertices.remove(vertex);
		
		while (!vertices.isEmpty()) {
			// Find all edges from this node and add them to the queue
			for (E edge : graph.getIncidentEdges(vertex)) {
				if (!edges.contains(edge)) {
					edges.add(edge);
				}
			}

			// Iterate over minimum edges until one is found with an
			// endpoint still in the list of vertices.
			E minimumEdge = edges.poll();
			while (vertices.contains(graph.getDest(minimumEdge))) {
				minimumEdge = edges.poll();
			}

			// Add the edge to the tree
			if (graph.getEdgeType(minimumEdge) == EdgeType.DIRECTED) {
				// Get the source and target for directed edges
				V source = graph.getSource(minimumEdge);
				V target = graph.getDest(minimumEdge);
				spanningTree.addEdge(minimumEdge, source, target);

				// Update to the next vertex to check from and remove it from the list
				// of available nodes.
				vertex = graph.getDest(minimumEdge);
				vertices.remove(vertex);

			} else {
				V source;
				V target;
				// Get endpoints of undirected edge and add to the graph
				Iterator<V> itr = graph.getEndpoints(minimumEdge).iterator();
				source = itr.next();
				target = itr.next();

				spanningTree.addEdge(minimumEdge, source, target);

				// Update to the next vertex to check from and remove it from the list
				// of available nodes.
				if (!vertex.equals(source)) {
					vertex = source;
				} else {
					vertex = target;
				}
				vertices.remove(vertex);
			}

		}
		return spanningTree;
	}

	public static void main(String[] args) {

		Vertex v1 = new Vertex("A");
		Vertex v2 = new Vertex("B");
		Vertex v3 = new Vertex("C");
		Vertex v4 = new Vertex("D");
		Vertex v5 = new Vertex("E");

		Graph<Vertex,Edge> graph = new SparseGraph<>();

		graph.addVertex(v1);
		graph.addVertex(v2);
		graph.addVertex(v3);
		graph.addVertex(v4);
		graph.addVertex(v5);

		graph.addEdge(new Edge("A", 10), v1, v2);
		graph.addEdge(new Edge("B", 15), v1, v3);
		graph.addEdge(new Edge("C", 50), v3, v2);
		graph.addEdge(new Edge("D", 20), v3, v4);
		graph.addEdge(new Edge("E", 80), v2, v4);
		graph.addEdge(new Edge("F", 80), v2, v5);

		PrimSpanningTree<Vertex, Edge> pst = new PrimSpanningTree<>();
		Forest<Vertex, Edge> forest = pst.findMinimalSpanningTree(graph);

		Visualizer.viewTree(forest);

	}

}
