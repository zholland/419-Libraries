package algorithms.sorting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import algorithms.graphloader.GraphMLReader;
import core.components.Edge;
import core.components.Vertex;
import core.visualizer.Visualizer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Implementation of a topological sorting algorithm.
 * 
 * @author Michael Nowicki
 *
 * @param <V> The vertex class type
 * @param <E> The edge class type
 */
public class TopologicalSort<V, E> {
	
	public static void main(String[] args) {
		GraphMLReader reader = new GraphMLReader();
		Graph<Vertex, Edge> graph = reader.loadGraph(0);
		TopologicalSort<Vertex, Edge> sort = new TopologicalSort<>();

		if (sort != null) {
			for (Vertex v : sort.sort(graph)) {
				System.out.println(v);
			}
			Visualizer.viewGraph(graph);
		}

	}
	
	/**
	 * Sorts the vertices in the given graph if the graph is directed.
	 * 
	 * @param graph The graph to perform the sort on
	 * @return An topological ordering of the vertices
	 * 			from an arbitrarily picked vertex in 
	 * 			the graph that has no ancestors. Returns
	 * 			null if the graph does not have directed
	 * 			edges or if the graph contains cycles.
	 */
	public ArrayList<V> sort(Graph<V, E> graph) {
		
		// Ensure edges are directed to perform search.
		if (graph.getDefaultEdgeType() != EdgeType.DIRECTED) {
			System.out.println("Directed graph needed for topological " +
				"sorting of vertices.");
			return null;
		}
		
		// List of sorted vertices
		ArrayList<V> topSort = new ArrayList<>();
		// Get collection of vertices to initialize structures
		Collection<V> vertexSet = graph.getVertices();
		
		// Set stores all vertices with no incident edges
		HashSet<V> set = new HashSet<>();
		HashMap<V,Integer> edgeCounter = new HashMap<>();
		
		// Iterate over all vertices, if the vertex does not
		// have a predecessor add it to the set of vertices
		// to explore
		for (V vertex : vertexSet) {
			if (graph.getPredecessorCount(vertex) == 0) {
				set.add(vertex);
			}
			// Keep count of incident edges, need to know
			// when all have been "removed" so that the vertex
			// can be added to the set if it was not already
			edgeCounter.put(vertex, graph.getPredecessorCount(vertex));
		}
		
		while (!set.isEmpty()) {
		
			// Take one arbitrary vertex out
			V vertex = set.iterator().next();
			set.remove(vertex);
			// Add it to the sorted list
			topSort.add(vertex);
			
			// Cycle through all descendants, 			
			Collection<V> neighbours = new HashSet<>();
			for (E edge : graph.getOutEdges(vertex)) {
				neighbours.add(graph.getDest(edge));
			}
			for (V descendant : neighbours) {			
				// Decrease edge count by one
				edgeCounter.put(descendant, edgeCounter.get(descendant) - 1);
				// Add the descendant to the set if it has not inward edges
				// anymore, ie the edgeCounter value is 0.
				if (edgeCounter.get(descendant) == 0) {
					set.add(descendant);
				}
			}
		}
		
		// Case where failed to remove all edges from the graph
		// then the graph has at least one cycle and the vertices
		// cannot be sorted.
		if (topSort.size() != graph.getVertexCount()) {
			System.out.println("Graph contains cycles, cannot sort vertices");
			return null;
		}
		return topSort;	
	}
}
