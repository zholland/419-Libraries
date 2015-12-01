package algorithms.triangles;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of algorithms used to compute the number of triangles in a graph.
 *
 * @author Zach Holland
 */
public class Triangles {

    /**
     * Counts the number of triangles in a graph. Uses the Node-Iteration algorithm.
     *
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return The number of triangles in the graph.
     */
    public static <V extends Comparable<V>, E> int count(Graph<V, E> graph) {
        return nodeIterationCount(graph).getTotalTriangles();
    }

    /**
     * Counts the number of triangles for each edge as well as the total number of edges
     * in a graph using the specified counting algorithm.
     *
     * Use the CountAlgorithm enum to specify the algorithm.
     *
     * @param graph     The graph on which the triangles are counted.
     * @param algorithm The algorithm to use to count the triangles.
     * @param <V>       The type of the vertices.
     * @param <E>       The type of the edges.
     * @return A special object containing the total number of triangles in the graph as well as the
     * number of triangles for each edge.
     * @see GraphTriangles
     */
    public static <V extends Comparable<V>, E> GraphTriangles<E> count(Graph<V, E> graph, CountAlgorithm algorithm) {
        switch (algorithm) {
            case NODE_ITERATOR:
                return nodeIterationCount(graph);
            case FAST_FORWARD:
                return fastForwardCount(graph);
            default:
                return nodeIterationCount(graph);
        }
    }

    /**
     * Counts the number of triangles for each edge as well as the total number of edges
     * in a graph using the Node-Iteration algorithm.
     *
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return A special object containing the total number of triangles in the graph as well as the
     * number of triangles for each edge.
     * @see GraphTriangles
     */
    public static <V extends Comparable<V>, E> GraphTriangles<E> nodeIterationCount(Graph<V, E> graph) {
        // Since the algorithm works by removing vertices from the graph, we need to operate on
        // a copy of the original graph to prevent the original graph from changing which might
        // be unexpected for the user.
        Graph<V, E> graphCopy = copyGraph(graph);

        // Create a comparator to sort the vertices by ascending degree.
        Comparator<V> byAscendingDegree = (v1, v2) -> {
            int diff = graphCopy.degree(v1) - graphCopy.degree(v2);
            if (diff == 0) {
                return v1.compareTo(v2);
            }
            return diff;
        };

        // Create a sorted set of the vertices.
        TreeSet<V> vertices = graphCopy.getVertices().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(byAscendingDegree)));

        GraphTriangles<E> graphTriangles = new GraphTriangles<>();

        // While there are still vertices to process
        while (graphCopy.getVertexCount() > 0) {

            // Get the lowest degree vertex and get its neighbors
            V v = vertices.pollFirst();
            Collection<V> neighborCollection = graphCopy.getNeighbors(v);

            if (neighborCollection != null) {
                ArrayList<V> neighbors = neighborCollection.stream().collect(Collectors.toCollection(ArrayList::new));

                // For each pair of neighbors, if they are neighbors of each other, increment the triangle count.
                for (int i = 0; i < neighbors.size() - 1; i++) {
                    for (int j = i + 1; j < neighbors.size(); j++) {
                        if (graphCopy.isNeighbor(neighbors.get(i), neighbors.get(j))) {
                            graphTriangles.incrementTriangleCount(graphCopy.findEdge(v, neighbors.get(i)));
                            graphTriangles.incrementTriangleCount(graphCopy.findEdge(v, neighbors.get(j)));
                            graphTriangles.incrementTriangleCount(graphCopy.findEdge(neighbors.get(i), neighbors.get(j)));
                        }
                    }
                }
            }
            graphCopy.removeVertex(v);
        }
        return graphTriangles;
    }

    /**
     * Creates a new copy of the given graph
     *
     * @param graph The graph to copy.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return A copy of the given graph.
     */
    private static <V, E> Graph<V, E> copyGraph(Graph<V, E> graph) {
        Graph<V, E> newGraph = new SparseGraph<>();
        graph.getVertices().forEach(newGraph::addVertex);
        graph.getEdges().forEach(e -> {
            Pair<V> endpoints = graph.getEndpoints(e);
            newGraph.addEdge(e, endpoints.getFirst(), endpoints.getSecond());
        });
        return newGraph;
    }

    /**
     * Counts the number of triangles for each edge as well as the total number of edges
     * in a graph using the Fast-Forward counting algorithm.
     *
     * See T. Schank's dissertation "Algorithmic Aspects of Triangle-Based Network Analysis"
     * URL: digbib.ubka.uni-karlsruhe.de/volltexte/documents/4541
     *
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return A special object containing the total number of triangles in the graph as well as the
     * number of triangles for each edge.
     * @see GraphTriangles
     */
    public static <V extends Comparable<V>, E> GraphTriangles<E> fastForwardCount(Graph<V, E> graph) {
        // Create a comparator to sort the vertices by descending degree
        Comparator<V> byDescendingDegree = (v1, v2) -> {
            int diff = graph.degree(v2) - graph.degree(v1);
            if (diff == 0) {
                return v1.compareTo(v2);
            }
            return diff;
        };

        // Get the set of vertices sorted by descending degree.
        TreeSet<V> vertices = graph.getVertices().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(byDescendingDegree)));

        // Create a map to store a set of vertices associated with each vertex.
        // This is A(v_i) in the psudocode.
        HashMap<V, Set<V>> vertexMap = new HashMap<>(graph.getVertexCount());
        graph.getVertices().forEach(v -> vertexMap.put(v, new TreeSet<>()));

        GraphTriangles<E> graphTriangles = new GraphTriangles<>();

        vertices.forEach(s -> graph.getNeighbors(s)
                .forEach(t -> {
                    // Proceed only if the degree of s is greater than the degree of t OR if the degrees are equal
                    // and s comes before t in the natural ordering of the vertex type.
                    // This is required to maintain an absolute ordering of the vertices.
                    if (byDescendingDegree.compare(s, t) < 0) {
                        Set<V> sSet = vertexMap.get(s);
                        Set<V> tSet = vertexMap.get(t);
                        Set<V> intersection = new TreeSet<>(sSet);

                        // The intersection is the common neighbors
                        intersection.retainAll(tSet);
                        intersection.forEach(v -> {
                            E edgeST = graph.findEdge(s, t);
                            graphTriangles.incrementTriangleCount(edgeST);
                            E edgeSV = graph.findEdge(s, v);
                            graphTriangles.incrementTriangleCount(edgeSV);
                            E edgeVT = graph.findEdge(v, t);
                            graphTriangles.incrementTriangleCount(edgeVT);
                        });
                        vertexMap.get(t).add(s);
                    }
                }));
        return graphTriangles;
    }

    /**
     * Constants that are used to indicate which counting algorithm to run.
     */
    public enum CountAlgorithm {
        NODE_ITERATOR, FAST_FORWARD
    }

    /**
     * GraphTriangles represents the triangles in a graph. It contains methods for storing and retrieving the
     * number of triangles on each edge as well as the number of triangles in the graph as a whole.
     * @param <E> The type of the edges.
     */
    public static class GraphTriangles<E> {
        // The total number of triangles in the graph.
        private int totalTriangles;

        // Maps the edge to its triangle count.
        private Map<E, Integer> edgeTriangleCountMap;

        /**
         * Creates an empty GraphTriangles instance.
         */
        private GraphTriangles() {
            totalTriangles = 0;
            edgeTriangleCountMap = new HashMap<>();
        }

        /**
         * Increments the number of triangles associated with the given edge.
         * @param edge The edge whose triangle count needs to be incremented.
         */
        private void incrementTriangleCount(E edge) {
            Integer numTriangles = edgeTriangleCountMap.get(edge);
            if (numTriangles == null) {
                numTriangles = 0;
            }
            edgeTriangleCountMap.put(edge, numTriangles + 1);
            totalTriangles += 1;
        }

        /**
         * Returns the total number of triangles in the graph.
         * @return The total number of triangles in the graph.
         */
        public int getTotalTriangles() {
            // Each triangle will be counted 3 times.
            return totalTriangles / 3;
        }

        /**
         * Returns the map containing the number of triangles associated with each edge.
         * @return The map containing the number of triangles associated with each edge.
         */
        private Map<E, Integer> getEdgeTriangleCountMap() {
            return edgeTriangleCountMap;
        }

        /**
         * Returns the number of triangles that the given edge is involved in.
         *
         * @param edge The edge for which to get the number of triangles.
         * @return The number of triangles that the given edge is involved in
         */
        public int getNumberOfTrianglesForEdge(E edge) {
            Integer numTriangles = edgeTriangleCountMap.get(edge);

            return numTriangles == null ? 0 : numTriangles;
        }
    }
}
