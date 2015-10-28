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
public class Barabasi<V> {

    /**
     * Generates an undirected Barabasi-Albert random graph based on the given
     * parameter m and the maximum number of nodes n with the default
     * Vertex and Edge class.
     *
     * @param m The number of nodes/edges to start
     * @param n The number of vertices in the graph
     * @return A undirected Barabasi-Albert random graph based on the given
     *         parameters.
     */
    public Graph<Vertex,Edge> getGraph(int m, int n) {
        return getGraph(m, n, false);
    }



    /**
     * Generates an undirected Barabasi-Albert random graph based on the given
     * parameter m for the number of initial vertices to use and the collection
     * of vertices provided to generate the graph with.
     *
     * @param vertices The set of vertices to build the random graph from
     * @param m The initial number of nodes to append to the graph
     * @return An undirected Barabasi-Albert graph.
     */
    public Graph<V, Edge> getGraph(Collection<V> vertices, int m) {
        return getGraph(vertices, m, false);
    }

    /**
     * Generates a Barabasi-Albert random graph based on the given parameter
     * m for the number of initial vertices to use and the collection
     * of vertices provided to generate the graph with.
     *
     * @param vertices The set of vertices to build the random graph from
     * @param m The initial number of nodes to append to the graph
     * @param isDirected Indicates if edges should be directed or not.
     * @return An undirected Barabasi-Albert graph.
     */
    public Graph<V, Edge> getGraph(Collection<V> vertices, int m, boolean isDirected) {
        Graph<V, Edge> graph = new SparseGraph<>();
        Random randomGen = new Random();

        Integer edgeId = 0;

        int counter = 0;
        // Add the first m vertices
        for (V vertex : vertices) {
            if (counter == m) break;

            graph.addVertex(vertex);
            // Remove it from the collection so it is not used again later
            vertices.remove(vertex);
            counter++;
        }

        // Connect the graph
        for (V sourceVertex : graph.getVertices()) {
            for (V targetVertex : graph.getVertices()) {

                if (sourceVertex.equals(targetVertex))
                    continue;

                if (isDirected) {
                    graph.addEdge(new Edge("" + edgeId), sourceVertex, targetVertex, EdgeType.DIRECTED);
                } else {
                    graph.addEdge(new Edge("" + edgeId), sourceVertex, targetVertex, EdgeType.UNDIRECTED);
                }

                edgeId++;
            }
        }

        // Add the remaining vertices to the graph.
        for (V newVertex : vertices) {

            graph.addVertex(newVertex);

            int edgeCounter = 0;
            while (edgeCounter < m) {

                double probability = 0.0;
                double randomNumber = randomGen.nextDouble();
                // Try to append edge to a target vertex
                for (V targetVertex : graph.getVertices()) {
                    // Already neighbours, move on.
                    if (graph.isNeighbor(newVertex, targetVertex) || targetVertex.equals(newVertex)) {
                        continue;
                    }

                    // Get the right degree based on in/out degrees
                    int vertexDegree;
                    if (isDirected) {
                        vertexDegree = graph.outDegree(targetVertex);
                    } else {
                        vertexDegree = graph.inDegree(targetVertex) + graph.outDegree(targetVertex);
                    }

                    probability += (double)vertexDegree/(2 * graph.getVertices().size());

                    if (randomNumber <= probability) {
                        if (isDirected) {
                            graph.addEdge(new Edge(""+edgeId), newVertex, targetVertex, EdgeType.DIRECTED);
                        } else {
                            graph.addEdge(new Edge(""+edgeId), newVertex, targetVertex, EdgeType.UNDIRECTED);
                        }

                        edgeId++;
                        edgeCounter++;

                        break;
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Generates an undirected Barabasi-Albert random graph based on the given
     * parameter m and the maximum number of nodes n with the default
     * Vertex and Edge class.
     *
     * @param m The number of nodes/edges to start
     * @param n The number of vertices in the graph
     * @param isDirected Indicates if edges are directed or not
     * @return A Barabasi-Albert random graph based on the given
     *         parameters.
     */
    public Graph<Vertex,Edge> getGraph(int m, int n, boolean isDirected) {

        Graph<Vertex, Edge> graph = new SparseGraph<>();
        Random randomGen = new Random();

        Integer edgeId = 0;
        int[] degrees = new int[n];

        // Add the initial nodes to the graph
        for (int i = 0; i < m; i++) {
            graph.addVertex(new Vertex("" + i));
            degrees[i] = m; // Since they will all be connected set their initial degree to m
        }
        // Connect the graph
        for (Vertex sourceVertex : graph.getVertices()) {
            for (Vertex targetVertex : graph.getVertices()) {

                if (sourceVertex.equals(targetVertex))
                    continue;

                if (isDirected) {
                    graph.addEdge(new Edge("" + edgeId), sourceVertex, targetVertex, EdgeType.DIRECTED);
                } else {
                    graph.addEdge(new Edge("" + edgeId), sourceVertex, targetVertex, EdgeType.UNDIRECTED);
                }

                edgeId++;
            }
        }

        // Add the remaining vertices to the graph.
        for (int i = m; i < n; i++) {

            Vertex newVertex = new Vertex("" + i);
            graph.addVertex(newVertex);

            int edgeCounter = 0;
            while (edgeCounter < m) {

                double probability = 0.0;
                double randomNumber = randomGen.nextDouble();
                // Try to append edge to a target vertex
                for (Vertex targetVertex : graph.getVertices()) {
                    // Already neighbours, move on.
                    if (graph.isNeighbor(newVertex, targetVertex) || targetVertex.equals(newVertex)) {
                        continue;
                    }

                    int id = Integer.valueOf(targetVertex.getId());
                    probability += (double)degrees[id]/(2*i);

                    if (randomNumber <= probability) {
                        if (isDirected) {
                            graph.addEdge(new Edge(""+edgeId), newVertex, targetVertex, EdgeType.DIRECTED);
                            // If it is directed this only effects the source vertices degree
                            degrees[i]++;
                        } else {
                            graph.addEdge(new Edge(""+edgeId), newVertex, targetVertex, EdgeType.UNDIRECTED);
                            // Adjust degrees for undirected graph.
                            degrees[i]++;
                            degrees[id]++;
                        }

                        edgeId++;
                        edgeCounter++;
                        // Update list of degrees

                        break;
                    }
                }
            }
        }
        return graph;
    }

//    public static void main(String[] args) {
//        Graph<Vertex, Edge> graph = new Barabasi<>().getGraph(3, 50, true);
//        Visualizer.viewGraph(graph, new CircleLayout<>(graph));
//    }

}
