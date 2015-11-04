package core.stocks;

import core.components.Edge;
import core.components.Vertex;
import core.tools.Tools;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.ArrayList;

/**
 * Create the visibility graph of a stock vertex
 * @author Mike Nowicki
 */
public class VisibilityGraph {

    /**
     * Create an adjacency matrix representing the visibility graph.
     * @param vertex The vertex to construct the graph for.
     * @return A 2D ArrayList of integers linking visible vertices.
     */
    public ArrayList<ArrayList<Integer> > createAdjacencyMatrix(StockVertex vertex) {
        return createAdjacencyMatrix(vertex, 60);
    }

    /**
     * Create an adjacency matrix representing the visibility graph of a given size.
     * @param vertex The vertex to construct the graph for.
     * @param dataSize The size of the graph to create
     * @return A 2D ArrayList of integers linking visible vertices.
     */
    public ArrayList<ArrayList<Integer> > createAdjacencyMatrix(StockVertex vertex, int dataSize) {
        ArrayList<ArrayList<Integer> > visibilityGraph = new ArrayList<>();
        for (int i = 0; i <= dataSize; i++) {
            visibilityGraph.add(new ArrayList<>());
        }
        return constructMatrix(visibilityGraph, vertex);
    }

    /**
     * Builds the adjacency matrix for the given vertex.
     * @param visibilityGraph The ArrayList to populate.
     * @param vertex The vertex to create the graph for.
     * @return A 2D ArrayList of integers linking visible vertices.
     */
    private ArrayList<ArrayList<Integer> > constructMatrix(ArrayList<ArrayList<Integer> > visibilityGraph,
                                                          StockVertex vertex) {

        double[] dataPoints = vertex.getDataPoints();

        // Make graph for last 60 days
        int startPoint = dataPoints.length-61;

//        for (int i = startPoint; i < dataPoints.length-1; i++) {
//
//            boolean endOfNeighbourhood = false;
//            int iAdjustedIndex = i - startPoint;
//
//            for (int j = i+1; j < dataPoints.length; j++) {
//
//                int jAdjustedIndex = j - startPoint;
//
//                // Edge already exists between the two
//                if (visibilityGraph.get(iAdjustedIndex).contains(jAdjustedIndex)) {
//                    continue;
//                }
//
//                // Neighbours are visible by default
//                if (j == i+1) {
//                    visibilityGraph.get(iAdjustedIndex).add(jAdjustedIndex);
//                    visibilityGraph.get(jAdjustedIndex).add(iAdjustedIndex);
//                    continue;
//                }
//
//                // Continue until an obstruction is reached, move to next index when end of neighbourhood reached.
//                for (int k = i+1; k < j; k++) {
//
//                    double yA = dataPoints[i];
//                    double yB = dataPoints[j];
//                    double yC = dataPoints[k];
//
//                    double tA = i;
//                    double tB = j;
//                    double tC = k;
//
//                    double meanValue = yB + (yA - yB)*((tB - tC)/(tB - tA));
//
//                    // If a point is greater than or equal to the mean value then the points {ta,tb} are not visible to each other
//                    if (yC >= meanValue) {
//                        endOfNeighbourhood = true;
//                        break;
//                    }
//                }
//                // At the end of the neighbourhood increase the index for ta
//                if (endOfNeighbourhood) {
//                    break;
//                } else {
//                    // Otherwise no obstructions, add edge between them
//                    visibilityGraph.get(iAdjustedIndex).add(jAdjustedIndex);
//                    visibilityGraph.get(jAdjustedIndex).add(iAdjustedIndex);
//                }
//            }
//        }
        return visibilityGraph;
    }

    /**
     * Fixed implementation to create a visibility graph as specified by the
     * financial history of a company.
     *
     * @param stockVertex The stock to create the visibility graph for
     * @return A graph representing the visibility graph of a companies
     *         stock history.
     */
    public Graph<Vertex, Edge> createGraph(StockVertex stockVertex) {
        Graph<Vertex, Edge> graph = new SparseGraph<>();

        double[] dataPoints = stockVertex.getDataPoints();

        // Make graph for last 60 days
        int startPoint = dataPoints.length-61;

        for (int i = startPoint; i < dataPoints.length-1; i++) {

            boolean endOfNeighbourhood = false;
            int iAdjustedIndex = i - startPoint;

            Vertex vertex = Tools.getVertex(graph, ""+iAdjustedIndex);

            if (vertex == null) {
                vertex = new Vertex(""+iAdjustedIndex);
                graph.addVertex(vertex);
            }

            for (int j = i+1; j < dataPoints.length; j++) {

                int jAdjustedIndex = j - startPoint;
                // See if the vertex is in the graph already, use it if it is,
                // create a new one otherwise.
                Vertex nextVertex = Tools.getVertex(graph, ""+jAdjustedIndex);

                if (nextVertex == null) {
                    nextVertex = new Vertex(""+jAdjustedIndex);
                    graph.addVertex(nextVertex);
                }

                // Edge already exists between the two
                if (graph.isNeighbor(vertex, nextVertex)) {
                    continue;
                }

                // Neighbours are visible by default
                if (j == i+1) {
                    graph.addEdge(new Edge(), vertex, nextVertex);
                }

                // Continue until an obstruction is reached, move to next index when end of neighbourhood reached.
                for (int k = i+1; k < j; k++) {

                    double yA = dataPoints[i];
                    double yB = dataPoints[j];
                    double yC = dataPoints[k];

                    double tA = i;
                    double tB = j;
                    double tC = k;

                    double meanValue = yB + (yA - yB)*((tB - tC)/(tB - tA));

                    // If a point is greater than or equal to the mean value then the points {ta,tb} are not visible to each other
                    if (yC >= meanValue) {
                        endOfNeighbourhood = true;
                        break;
                    }
                }
                // At the end of the neighbourhood increase the index for ta
                if (endOfNeighbourhood) {
                    break;
                } else {
                    // Otherwise no obstructions, add edge between them
                    graph.addEdge(new Edge(), vertex, nextVertex);
                }
            }
        }

        return graph;
    }

    /**
     * Create a JUNG graph of the visibility graph.
     * @param adjacencyMatrix A 2D ArrayList storing the adjacency matrix of the graph.
     * @return The graph defined by the matrix, or null if the matrix is empty.
     */
    public Graph<Vertex, Edge> createGraph(ArrayList<ArrayList<Integer> > adjacencyMatrix) {
        Graph<Vertex, Edge> graph = new SparseGraph<>();

        // Nothing in the matrix, cannot create a graph
        if (adjacencyMatrix.size() == 0) {
            return null;
        }

        // Place the initial nodes into the graph
        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            graph.addVertex(new Vertex("" + i));
        }

        // Cycle through adjacency matrix and connect all neighbours
        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            // Cycle through list of neighbours and attach them.
            for (Integer neighbour : adjacencyMatrix.get(i)) {

                // Find the two vertices in the graph to create the edge between
                Vertex source = Tools.getVertex(graph, ""+i);
                Vertex destination = Tools.getVertex(graph, ""+neighbour);

                // Add the edge if they are not already connected
                if (!graph.isNeighbor(source, destination)) {
                    graph.addEdge(new Edge(), source, destination, EdgeType.UNDIRECTED);
                }

            }
        }
        return graph;
    }
}
