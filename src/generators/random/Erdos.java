package generators.random;

import core.components.Edge;
import core.components.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.Collection;
import java.util.Random;

/**
 * @author Mike Nowicki
 */
public class Erdos<V extends Vertex> {

    /**
     * Generates an undirected Erdos-Renyi random graph based on the given
     * probability and the maximum number of nodes n. Nodes are
     * general Vertex objects with ID's equal to the vertices number
     * when created ("1", "2", ... etc). Creates new core.Vertex objects
     * and core.Edges between vertices
     *
     * @param probability The probability of two vertices having an edge
     * @param n The number of vertices in the graph
     * @return A Erdos-Renyi random graph based on the given
     *         parameters.
     */
    public Graph<Vertex, Edge> getGraph(double probability, int n) {
        return getGraph(probability, n, false);
    }

    /**
     * Generate an undirected Erdo-Renyi random graph based on the given probability
     * and the vertex set provided. All nodes in the set are added to
     * the graph before random edges are added. Uses the core.Edges
     * to connect the V vertices.
     *
     * @param vertexSet The set of vertices to make the graph from
     * @param probability The probability of an edge existing [0,1]
     * @return A Erdos-Renyi random graph based on the given
     *         parameters.
     */
    public Graph<V, Edge> getGraph(Collection<V> vertexSet, double probability) {
        return getGraph(vertexSet, probability, false);
    }

    /**
     * Generates an Erdos-Renyi random graph based on the given
     * probability and the maximum number of nodes n. Nodes are
     * general Vertex objects with ID's equal to the vertices number
     * when created ("1", "2", ... etc). Creates new core.Vertex objects
     * and core.Edges between vertices
     *
     * @param probability The probability of two vertices having an edge
     * @param n The number of vertices in the graph
     * @param isDirected Indicates whether or not the graph has directed edges
     * @return A Erdos-Renyi random graph based on the given
     *         parameters.
     */
    public Graph<Vertex, Edge> getGraph(double probability, int n, boolean isDirected) {

        Random randomGen = new Random();
        Graph<Vertex, Edge> graph = new SparseGraph<>();

        // Add the nodes to the graph.
        for (int i = 0; i < n; i++) {
            graph.addVertex(new Vertex(""+i));
        }

        Integer edgeId = 0;

        // Test each pair of vertices to see if we add an edge
        for (Vertex sourceVertex : graph.getVertices()) {
            for (Vertex testVertex : graph.getVertices()) {
                // Don't add them if they are already connected or are the same vertex
                if (sourceVertex.equals(testVertex) || graph.isNeighbor(sourceVertex,testVertex)) {
                    continue;
                }

                double randomNumber = randomGen.nextDouble();

                if (randomNumber <= probability) {
                    if (isDirected) {
                        graph.addEdge(new Edge(""+edgeId), sourceVertex, testVertex, EdgeType.DIRECTED);
                    } else {
                        graph.addEdge(new Edge("" + edgeId), sourceVertex, testVertex, EdgeType.UNDIRECTED);
                    }
                    edgeId++;
                }
            }
        }
        return graph;
    }

    /**
     * Generate an Erdo-Renyi random graph based on the given probability
     * and the vertex set provided. All nodes in the set are added to
     * the graph before random edges are added. Uses the core.Edges
     * to connect the V vertices.
     *
     * @param vertexSet The set of vertices to make the graph from
     * @param probability The probability of an edge existing [0,1]
     * @param isDirected Indicates whether or not the graph has directed edges
     * @return A Erdos-Renyi random graph based on the given
     *         parameters.
     */
    public Graph<V, Edge> getGraph(Collection<V> vertexSet, double probability, boolean isDirected) {

        Random randomGen = new Random();
        Graph<V, Edge> graph = new SparseGraph<>();

        // Add the nodes to the graph.
        for (V vertex : vertexSet) {
            graph.addVertex(vertex);
        }

        Integer edgeId = 0;

        // Test each pair of vertices to see if we add an edge
        for (V sourceVertex : vertexSet) {
            for (V testVertex : vertexSet) {
                // Don't add them if they are already connected or are the same vertex
                if (sourceVertex.equals(testVertex) || graph.isNeighbor(sourceVertex,testVertex)) {
                    continue;
                }

                double randomNumber = randomGen.nextDouble();
                if (randomNumber <= probability) {
                    if (isDirected) {
                        graph.addEdge(new Edge(""+edgeId), sourceVertex, testVertex, EdgeType.DIRECTED);
                    } else {
                        graph.addEdge(new Edge("" + edgeId), sourceVertex, testVertex, EdgeType.UNDIRECTED);
                    }
                    edgeId++;
                }
            }
        }
        return graph;
    }

//    public static void main(String[] args) {
//        Graph<Vertex, Edge> graph = new Erdos<>().getGraph(0.1, 50);
//        Visualizer.viewGraph(graph);
//    }

}
