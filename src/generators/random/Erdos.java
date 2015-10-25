package generators.random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

/**
 * @author Mike Nowicki
 */
public class Erdos<V,E> {

    /**
     * Generates a Barabasi-Albert random graph based on the given
     * parameter m and the maximum number of nodes n.
     *
     * @param probability The probability of two vertices having an edge
     * @paran n The number of vertices in the graph
     * @return A Erdos-Reyne random graph based on the given
     *         parameters.
     */
    public Graph<V,E> getGraph(double probability, int n) {

        Graph graph = new SparseGraph<>();

        return graph;
    }
}
