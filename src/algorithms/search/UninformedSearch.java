package algorithms.search;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import core.components.Edge;
import edu.uci.ics.jung.graph.Graph;

/**
 * Generic interface for uninformed search algorithms
 * 
 * @author Mike Nowicki
 *
 * @param <V> - Vertex type
 * @param <E> - Edge type must extend edge class
 */
public interface UninformedSearch<V, E extends Edge> {
	
	/**
	 * Main search method.
	 * 
	 * @param root - The root node to search from.
	 * @return - A list indicating the order nodes were traversed, or a
	 * 			null list if the root is not in the graph.
	 */
	List<V> search(Graph<V,E> graph, V root);
	
	/**
	 * Performs the same search functionality except
	 * it displays the graph and highlights the order
	 * nodes were traversed.
	 * 
	 * @param root - The node to start from.
	 */
	void visualizeSearch(Graph<V,E> graph, V root);
	
	/**
	 * Scans all outward edges from the current node and adds the destination
	 * to the collection of neighbours.
	 * 
	 * @param userGraph The graph being searched.
	 * @param currentNode The node who's neighbours we are looking for
	 * @return A collection of nodes 
	 */
	Collection<V> getDirectedNeighbours(Graph<V, E> userGraph, V currentNode);
	
	/**
	 * Vertex painting class for the original graph, colours
	 * the root node differently than the others.
	 * 
	 * @author Mike Nowicki
	 *
	 * @param <V> The vertex type
	 */
	class OriginalVertexPaint<V> implements Transformer<V, Paint> {

		V root;
		
		public OriginalVertexPaint(V root) {
			this.root = root;
		}
		
		@Override
		public Paint transform(V v) {
			if (v == root) {
				return Color.GREEN;
			}
			return Color.RED;
		}
		
	}
	
	/**
	 * Simple vertex paint class, colours vertices based
	 * on their layed in the search tree
	 * 
	 * @author Mike Nowicki
	 *
	 * @param <V> The vertex type
	 */
	class SearchVertexPaint<V> implements Transformer<ExtendedVertex<V>, Paint> {

		@Override
		public Paint transform(ExtendedVertex<V> v) {
			int layer = v.getLayer();
			switch(layer) {
				case 0: return Color.YELLOW;
				case 1: return Color.GREEN;
				case 2: return Color.BLUE;
				case 3: return Color.RED;
				case 4: return Color.BLACK;
				case 5: return Color.WHITE;
				case 6: return Color.CYAN;
				default: return Color.MAGENTA;
			}
		}
	}
	
	/**
	 * Private wrapper class to store user vertices with some additional info
	 * 
	 * @author Mike Nowicki
	 *
	 * @param <T> - Vertex type of user graph
	 */
	class ExtendedVertex<T> {
		
		T vertex;
		int layer;
		
		public ExtendedVertex(T vertex, int layer) {
			this.vertex = vertex;
			this.layer = layer;
		}
		
		public T getVertex() {
			return vertex;
		}
		
		public int getLayer() {
			return layer;
		}	
		
		public String toString() {
			return  vertex.toString() + " .. " + "Layer: " + String.valueOf(layer);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof ExtendedVertex) {
				ExtendedVertex<T> otherV = (ExtendedVertex<T>)other;
				return this.vertex.equals(otherV.getVertex());
			}
			return false;
		}
	}
}
