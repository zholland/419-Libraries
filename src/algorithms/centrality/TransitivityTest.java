package algorithms.centrality;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Test class for Transitivity.
 * @author Tony Culos
 */
public class TransitivityTest {

    // Since the method under test samples the given graph at random, it makes sense
    // to have a non deterministic test in this case.
    @Test
    public void transitivity_randomGraph_transitivityGreaterThan0() {
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

        double trans = Transitivity.approximate(graph, 10);
        assertTrue(0.0 < trans);
    }


    @Test
    public void transitivity_K6_transitivityEquals1() {

        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 0; i < 6; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (!(graph.containsEdge(i + "-" + j) || graph.containsEdge(j + "-" + i) || i == j)) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        double trans = Transitivity.approximate(graph, 10);
        assertTrue(1.0 == trans);
    }

    @Test
    public void transitivity_triangle_transitivityEquals1() {

        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 0; i < 3; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!(graph.containsEdge(i + "-" + j) || graph.containsEdge(j + "-" + i) || i == j)) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        double trans = Transitivity.approximate(graph, 3);
        assertTrue((1.0 == trans));
    }

    @Test
    public void transitivity_pair_transitivityEquals0() {

        Graph<Integer, String> graph = new SparseGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);

        graph.addEdge("1-2", 1, 2);

        double trans = Transitivity.approximate(graph, 1);
        assertTrue(0.0 == trans);
    }
}