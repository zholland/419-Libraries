package algorithms.clustering;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
    public static <V, E> int count(Graph<V, E> graph) {
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
     * Counts the number of triangles in a graph using the Node-Iteration algorithm.
     *
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return The number of triangles in the graph.
     */
    public static <V, E> GraphTriangles<E> nodeIterationCount(Graph<V, E> graph) {
        return null;
    }

    /**
     * Counts the number of triangles in a graph using the Fast-Forward counting algorithm.
     *
     * @param graph The graph on which the triangles are counted.
     * @param <V>   The type of the vertices.
     * @param <E>   The type of the edges.
     * @return The number of triangles in the graph.
     */
    public static <V extends Comparable<V>, E> GraphTriangles<E> fastForwardCount(Graph<V, E> graph) {
        TreeSet<V> vertices = graph.getVertices().stream()
                .collect(Collectors.toCollection(TreeSet::new));

        HashMap<V, Set<V>> vertexMap = new HashMap<>(graph.getVertexCount());
        graph.getVertices().forEach(v -> vertexMap.put(v, new TreeSet<>()));

        GraphTriangles<E> graphTriangles = new GraphTriangles<>();

        vertices.forEach(s -> graph.getNeighbors(s)
                .forEach(t -> {
                    int sDegree = graph.degree(s);
                    int tDegree = graph.degree(t);

                    // Proceed only if the degree of s is greater than the degree of t OR if the degrees are equal
                    // and s comes before t in the natural ordering of the vertex type.
                    // This is required to maintain an absolute ordering of the vertices.
                    if (sDegree > tDegree || (sDegree == tDegree && s.compareTo(t) < 0)) {
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
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);

        graph.addEdge("4-1", 4, 1);
        graph.addEdge("4-2", 4, 2);
        graph.addEdge("1-2", 1, 2);
        graph.addEdge("2-3", 2, 3);
        graph.addEdge("3-1", 3, 1);

        GraphTriangles<String> gt = Triangles.fastForwardCount(graph);
        System.out.println(gt.getTotalTriangles());
        System.out.println(gt.getEdgeTriangleCountMap().get("3-1"));
    }
}
