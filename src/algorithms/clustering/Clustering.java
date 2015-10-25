package algorithms.clustering;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An implementation of an algorithm that finds
 * the clustering measure of a vertex in the graph.
 * 
 * @author Michael Nowicki
 *
 * @param <V> The vertex class type
 * @param <E> The edge class type
 */
public class Clustering<V, E> {

    /**
     *
     * Find the clustering coefficient for a vertex in the graph.
     *
     * @param graph The graph to search.
     * @param <V> The vertex type
     * @param <E> The edge type
     * @return A double with the clustering coefficient, or null
     *         if the vertex does not exist.
     */
    public static<V,E> Double coefficient(Graph<V,E> graph, V vertex) {

        // Vertex not in the graph, return null.
        if (!graph.containsVertex(vertex)) {
            System.out.println("Vertex is not in the graph.");
            return null;
        }

        if (graph.getEdgeType(graph.getEdges().iterator().next()) == EdgeType.DIRECTED) {

            // If it has one or fewer neighbours it can only have a clustering
            // coefficient of 0.
            if (graph.outDegree(vertex) <= 2) {
                System.out.println("Vertex has less than two neighbours, therefore it has " +
                                   "a clustering coefficient of 0.");
                return 0.0;
            }

            ArrayList<V> neighbourList = new ArrayList<>();

            for (E edge : graph.getOutEdges(vertex)) {
                neighbourList.add(graph.getDest(edge));
            }

            int edgesBetweenNeighbours = 0;
            // Cycle through neighbours, test if their neighbours are
            // also connected to the neigbhours of the given vertex.
            for (V neighbour : neighbourList) {

                // Get the directed neighbours of the neighbour of vertex
                ArrayList<V> nextNeighbours = new ArrayList<>();
                for (E edge : graph.getOutEdges(neighbour)) {
                    nextNeighbours.add(graph.getDest(edge));
                }

                for (V nextNeighbour : nextNeighbours) {

                    if (neighbourList.contains(nextNeighbour)) {
                        edgesBetweenNeighbours++;
                    }
                }
            }

            int outDegree = graph.outDegree(vertex);

            // Coefficient is equal to the number of edges between neighbours divided by
            // the degree of the vertex multiplied by one less than the degree of the vertex
            return ( edgesBetweenNeighbours / (double)(outDegree * (outDegree - 1)) );

        } else {

            Collection<V> neighbours = graph.getNeighbors(vertex);

            int edgesBetweenNeighbours = 0;
            // Cycle through neighbours, test if their neighbours are
            // also connected to the neigbhours of the given vertex.
            for (V neighbour : neighbours) {
                for (V nextNeighbour : graph.getNeighbors(neighbour)) {
                    if (neighbours.contains(nextNeighbour)) {
                        edgesBetweenNeighbours++;
                    }
                }
            }

            int degree = graph.degree(vertex);

            // Coefficient is equal to twice the number of edges between neighbours divided by
            // the degree of the vertex multiplied by one less than the degree of the vertex
            return ((2 * edgesBetweenNeighbours) / (double)(degree * (degree - 1)) );
        }
    }

    /**
     * Find the average clustering coefficient for the graph.
     * @param graph The graph to compute the average clustering coefficient for.
     * @param <V> The vertex type.
     * @param <E> The edge type.
     * @return A double value for the average clustering coefficient.
     */
    public static<V,E> Double average(Graph<V,E> graph) {

        Double coefficientSum = 0.0;

        for (V vertex : graph.getVertices()) {
            coefficientSum += coefficient(graph, vertex);
        }

        return (coefficientSum/graph.getVertices().size());
    }

//    public static void main(String[] args) {
//        Graph<Vertex, Edge> graph = new Erdos<>().getGraph(0.1, 50);
//        Vertex v = Tools.getVertex(graph, "2");
//        System.out.println(Clustering.clusterCoefficient(graph, v));
//    }

}
