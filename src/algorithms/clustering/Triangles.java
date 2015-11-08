package algorithms.clustering;

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
        Graph<V, E> graphClone = cloneGraph(graph);

        Comparator<V> byAscendingDegree = (v1, v2) -> {
            int diff = graphClone.degree(v1) - graphClone.degree(v2);
            if (diff == 0) {
                return v1.compareTo(v2);
            }
            return diff;
        };

        TreeSet<V> vertices = graphClone.getVertices().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(byAscendingDegree)));

        GraphTriangles<E> graphTriangles = new GraphTriangles<>();


        while (graphClone.getVertexCount() > 0) {
            V v = vertices.pollFirst();
            Collection<V> neighborCollection = graphClone.getNeighbors(v);
            if (neighborCollection != null) {
                ArrayList<V> neighbors = neighborCollection.stream().collect(Collectors.toCollection(ArrayList::new));

                for (int i = 0; i < neighbors.size() - 1; i++) {
                    for (int j = i + 1; j < neighbors.size(); j++) {
                        if (graphClone.isNeighbor(neighbors.get(i), neighbors.get(j))) {
                            graphTriangles.incrementTriangleCount(graphClone.findEdge(v, neighbors.get(i)));
                            graphTriangles.incrementTriangleCount(graphClone.findEdge(v, neighbors.get(j)));
                            graphTriangles.incrementTriangleCount(graphClone.findEdge(neighbors.get(i), neighbors.get(j)));
                        }
                    }
                }
            }
            graphClone.removeVertex(v);
        }
        return graphTriangles;
    }

    private static <V, E> Graph<V, E> cloneGraph(Graph<V, E> graph) {
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
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return A special object containing the total number of triangles in the graph as well as the
     * number of triangles for each edge.
     * @see GraphTriangles
     */
    public static <V extends Comparable<V>, E> GraphTriangles<E> fastForwardCount(Graph<V, E> graph) {
        Comparator<V> byDescendingDegree = (v1, v2) -> {
            int diff = graph.degree(v2) - graph.degree(v1);
            if (diff == 0) {
                return v1.compareTo(v2);
            }
            return diff;
        };

        TreeSet<V> vertices = graph.getVertices().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(byDescendingDegree)));

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

    public enum CountAlgorithm {
        NODE_ITERATOR, FAST_FORWARD
    }

    public static class GraphTriangles<E> {
        private int totalTriangles;
        private Map<E, Integer> edgeTriangleCountMap;

        private GraphTriangles() {
            totalTriangles = 0;
            edgeTriangleCountMap = new HashMap<>();
        }

        private void incrementTriangleCount(E edge) {
            Integer numTriangles = edgeTriangleCountMap.get(edge);
            if (numTriangles == null) {
                numTriangles = 0;
            }
            edgeTriangleCountMap.put(edge, numTriangles + 1);
            totalTriangles += 1;
        }

        public int getTotalTriangles() {
            // Each triangle will be counted 3 times.
            return totalTriangles / 3;
        }

        public Map<E, Integer> getEdgeTriangleCountMap() {
            return edgeTriangleCountMap;
        }
    }

    public static void main(String[] args) {
        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 1; i <= 10000; i++) {
            graph.addVertex(i);
        }

        Random random = new Random();

        for (int i = 1; i <= 99; i++) {
            for (int j = i + 1; j <= 99; j++) {
                if (random.nextDouble() < 0.5) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        GraphTriangles<String> nodeIterationCount;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            nodeIterationCount = Triangles.nodeIterationCount(graph);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        GraphTriangles<String> fastForwardCount;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            fastForwardCount = Triangles.fastForwardCount(graph);
        }
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
