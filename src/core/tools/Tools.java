package core.tools;

import core.components.Edge;
import core.components.Vertex;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * A collection of methods to help access information
 * @author Mike Nowicki
 */
public class Tools<V extends Vertex,E extends Edge> {

    /**
     * Find a vertex in the graph.
     * @param graph The graph to search.
     * @param id The string ID of the vertex.
     * @param <V> The vertex type, extending the core.Vertex class.
     * @param <E> The edge type, extending the core.Edge class.
     * @return The vertex with that string ID, null if one doesn't exist.
     */
    public static<V extends Vertex,E extends Edge> V getVertex(Graph<V,E> graph, String id) {
        for (V vertex : graph.getVertices()) {
            if (vertex.getId().equals(id)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Find an edge in the graph.
     * @param graph The graph to search.
     * @param id The string ID of the edge.
     * @param <V> The vertex type, extending the core.Vertex class.
     * @param <E> The edge type, extending the core.Edge class.
     * @return The edge with that string ID, null if one doesn't exist.
     */
    public static<V extends Vertex,E extends Edge> E getEdge(Graph<V,E> graph, String id) {
        for (E edge : graph.getEdges()) {
            if (edge.getId().equals(id)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Computes the average degree for a network.
     * @param graph The graph to compute for.
     * @param <V> The vertex type, extending Vertex
     * @param <E> The edge type, extending Edge
     * @return The average degree in the network, or 0 if there are no edges.
     */
    public static<V extends Vertex,E extends Edge> Double getAverageDegree(Graph<V,E> graph) {

        if(graph.getEdgeCount() == 0) {
            return 0.0;
        }

        // Test if edges are directed, perform computation accordingly.
        if (graph.getEdgeType(graph.getEdges().iterator().next()) == EdgeType.DIRECTED) {
            return (double)((graph.getEdgeCount()) / graph.getVertexCount());
        } else {
            return (double)((2 * graph.getEdgeCount()) / graph.getVertexCount());
        }

    }

}
