package algorithms.centrality;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Tony Culos
 */
public class Transitivity {

    /**
     * Approximates the transitivity of a given graph by edge sampling.
     *
     * @param graph The graph for which to approximate the transitivity.
     * @param numSamples The number of samples to take.
     * @return The approximate transitivity of the graph.
     */
    public static <V extends Comparable<V>, E> double approximate(Graph<V, E> graph, int numSamples) {
        double transitivity = 0.0;
        ArrayList<E> edges = new ArrayList<>(graph.getEdges());
        ArrayList<E> sample = new ArrayList<>();

        //collect M sample vertices
        Random rnd = new Random();
        while (sample.size() != numSamples) {
            int choose = rnd.nextInt(edges.size());

            E edge = edges.get(choose);
            if (!sample.contains(edge)) {
                sample.add(edge);
            }
        }

        //calculate transitivity of each edge
        for (E edge : sample) {
            transitivity += edgeClustering(graph, edge);
        }

        return (transitivity / (numSamples));
    }

    /**
     * Computes the edge clustering coefficient of the given edge.
     * i.e. C(e) = lambda(e) / tau(e) = embededness / triplets.
     *
     * @param graph The to which the edge belongs.
     * @param edge The edge for which to compute the clustering.
     * @return The edge clustering.
     */
    private static <V extends Comparable<V>, E> double edgeClustering(Graph<V, E> graph, E edge) {
        int embeddedness = embeddedness(graph, edge);
        int triplets = triplets(graph, edge);

        if (triplets != 0) {
            return ((double) embeddedness) / ((double) triplets);
        } else {
            return 0.0;
        }
    }

    /**
     * Computes the edge embeddedness of the given edge.
     *
     * @param g The graph to which the edge belongs.
     * @param edge The edge for calculating embeddedness.
     * @return The embededness of the given edge.
     */
    private static <V extends Comparable<V>, E> int embeddedness(Graph<V, E> g, E edge) {
        int embeddedness = 0;

        Pair<V> ends = g.getEndpoints(edge);
        ArrayList<V> n1 = new ArrayList<>(g.getNeighbors(ends.getFirst()));
        ArrayList<V> n2 = new ArrayList<>(g.getNeighbors(ends.getSecond()));

        for (V e : n1) {
            if (n2.contains(e)) {
                embeddedness++;
            }
        }

        return embeddedness;
    }

    /**
     * Computes the number of triplets an edge is involved in
     * using (deg(v)+deg(u)-2)/2 as the formula.
     *
     * @param graph The graph to which the edge belongs.
     * @param edge The edge to count the triplets for.
     * @return The number of triplets that the edge is involved in.
     */
    private static <V extends Comparable<V>, E> int triplets(Graph<V, E> graph, E edge) {
        Pair<V> ends = graph.getEndpoints(edge);
        V v1 = ends.getFirst();
        V v2 = ends.getSecond();

        return (graph.getNeighborCount(v1) + graph.getNeighborCount(v2) - 2) / 2;
    }
}