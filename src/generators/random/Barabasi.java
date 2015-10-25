package generators.random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

/**
 * @author Mike Nowicki
 */
public class Barabasi<V,E> {

    /**
     * Generates a Barabasi-Albert random graph based on the given
     * parameter m and the maximum number of nodes n.
     *
     * @param m The number of nodes/edges to start
     * @paran n The number of vertices in the graph
     * @return A Barabasi-Albert random graph based on the given
     *         parameters.
     */
    public Graph<V,E> getGraph(int m, int n) {

        Graph<V,E> graph = new SparseGraph<>();


        return graph;
    }

}
